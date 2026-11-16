import cv2
import numpy as np
import os
import json

DATASET = "dataset"

imagenes = []
etiquetas = []
nombres = {}  # id -> nombre

# cada carpeta dentro de dataset/ es una persona
for id_persona, nombre in enumerate(sorted(os.listdir(DATASET))):
    carpeta = os.path.join(DATASET, nombre)
    if not os.path.isdir(carpeta):
        continue
    nombres[id_persona] = nombre
    for archivo in os.listdir(carpeta):
        img = cv2.imread(os.path.join(carpeta, archivo), cv2.IMREAD_GRAYSCALE)
        if img is not None:
            imagenes.append(img)
            etiquetas.append(id_persona)

if len(imagenes) == 0:
    print("No hay fotos en la carpeta dataset. Ejecuta primero 1_captura_rostros.py")
    raise SystemExit

print(f"Entrenando con {len(imagenes)} fotos de {len(nombres)} persona(s)...")
reconocedor = cv2.face.LBPHFaceRecognizer_create()
reconocedor.train(imagenes, np.array(etiquetas))

reconocedor.write("modelo.yml")
with open("nombres.json", "w", encoding="utf-8") as f:
    json.dump(nombres, f, ensure_ascii=False)

print("modelo.yml y nombres.json guardados")
