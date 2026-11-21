import os
import tkinter as tk
import numpy as np
import tensorflow as tf
from PIL import Image, ImageDraw

BASE = os.path.dirname(os.path.abspath(__file__))

# Usa el modelo CNN si existe; si no, el MLP que ya tenias.
RUTA = None
for nombre_modelo in ("cnn_model.keras", "mlp_model.keras"):
    candidato = os.path.join(BASE, nombre_modelo)
    if os.path.exists(candidato):
        RUTA = candidato
        break
if RUTA is None:
    raise SystemExit("No hay modelo. Ejecuta primero entrenar_digitos.py")

model = tf.keras.models.load_model(RUTA)
print("Modelo cargado:", os.path.basename(RUTA))

TAM = 280   # tamano del lienzo
R = 12      # radio del pincel

# ---------- Interfaz ----------
ventana = tk.Tk()
ventana.title("Reconocedor de Digitos")
ventana.geometry("360x520")
ventana.configure(bg="#004d40")

tk.Label(ventana, text="Dibuja un numero (0-9)", font=("Arial", 14, "bold"),
         bg="#004d40", fg="#e0f2f1").pack(pady=12)

canvas = tk.Canvas(ventana, width=TAM, height=TAM, bg="black",
                   highlightthickness=2, highlightbackground="#00897b")
canvas.pack()

# Imagen en memoria donde se replica lo dibujado (para procesarla)
imagen_aux = Image.new("L", (TAM, TAM), 0)
draw = ImageDraw.Draw(imagen_aux)
ultimo = [None]


def presionar(event):
    ultimo[0] = None
    dibujar(event)


def dibujar(event):
    x, y = event.x, event.y
    x0, y0 = ultimo[0] if ultimo[0] else (x, y)
    # linea continua entre el punto anterior y el actual
    canvas.create_line(x0, y0, x, y, fill="white", width=2 * R,
                       capstyle=tk.ROUND)
    canvas.create_oval(x - R, y - R, x + R, y + R, fill="white", outline="white")
    draw.line([x0, y0, x, y], fill=255, width=2 * R)
    draw.ellipse([x - R, y - R, x + R, y + R], fill=255)
    ultimo[0] = (x, y)


def soltar(event):
    ultimo[0] = None
    predecir()  # predice automaticamente al terminar el trazo


canvas.bind("<Button-1>", presionar)
canvas.bind("<B1-Motion>", dibujar)
canvas.bind("<ButtonRelease-1>", soltar)


# ---------- Procesamiento y prediccion ----------
def preprocesar(img):
    """Deja el dibujo como lo espera MNIST: digito en caja de 20x20, centrado
    por centro de masa dentro de una imagen de 28x28."""
    img = img.crop(img.getbbox())
    img.thumbnail((20, 20), Image.Resampling.LANCZOS)  # mantiene proporcion

    fondo = Image.new("L", (28, 28), 0)
    fondo.paste(img, ((28 - img.width) // 2, (28 - img.height) // 2))
    datos = np.array(fondo, dtype="float32") / 255.0

    # Centrar por centro de masa (asi estan centrados los digitos de MNIST)
    total = datos.sum()
    if total > 0:
        ys, xs = np.indices(datos.shape)
        cy = (ys * datos).sum() / total
        cx = (xs * datos).sum() / total
        datos = np.roll(datos, (int(round(14 - cy)), int(round(14 - cx))),
                        axis=(0, 1))
    return datos


def predecir():
    if imagen_aux.getbbox() is None:
        lbl_resultado.config(text="¡Dibuja algo primero!")
        lbl_detalle.config(text="")
        return

    datos = preprocesar(imagen_aux)

    # El CNN espera (1, 28, 28, 1); el MLP espera (1, 28, 28)
    if len(model.input_shape) == 4:
        datos = datos.reshape(1, 28, 28, 1)
    else:
        datos = datos.reshape(1, 28, 28)

    probs = model.predict(datos, verbose=0)[0]
    top = np.argsort(probs)[::-1][:3]

    lbl_resultado.config(text=f"Resultado: {top[0]}  ({probs[top[0]] * 100:.0f}%)")
    lbl_detalle.config(
        text="   ".join(f"{d}: {probs[d] * 100:.0f}%" for d in top[1:]))


def limpiar():
    canvas.delete("all")
    draw.rectangle([0, 0, TAM, TAM], fill=0)
    lbl_resultado.config(text="Resultado: ?")
    lbl_detalle.config(text="")


# ---------- Botones y resultado ----------
frame_botones = tk.Frame(ventana, bg="#004d40")
frame_botones.pack(pady=10)

tk.Button(frame_botones, text="Predecir", command=predecir,
          font=("Arial", 11, "bold"), bg="#00897b", fg="white",
          width=10, relief="flat").grid(row=0, column=0, padx=5)

tk.Button(frame_botones, text="Limpiar", command=limpiar,
          font=("Arial", 11), bg="#b2dfdb", fg="#004d40",
          width=10, relief="flat").grid(row=0, column=1, padx=5)

lbl_resultado = tk.Label(ventana, text="Resultado: ?",
                         font=("Arial", 16, "bold"),
                         bg="#004d40", fg="#e0f2f1")
lbl_resultado.pack(pady=5)

lbl_detalle = tk.Label(ventana, text="", font=("Arial", 11),
                       bg="#004d40", fg="#b2dfdb")
lbl_detalle.pack()

ventana.mainloop()
