import os
import numpy as np
import tensorflow as tf
import matplotlib.pyplot as plt

BASE = os.path.dirname(os.path.abspath(__file__))
RUTA_MODELO = os.path.join(BASE, "cnn_model.keras")

layers = tf.keras.layers

# 1. Datos: MNIST (imagenes de 28x28 con digitos escritos a mano)
(x_train, y_train), (x_test, y_test) = tf.keras.datasets.mnist.load_data()

# Normalizar a 0-1 y agregar el canal (28, 28) -> (28, 28, 1)
x_train = (x_train.astype("float32") / 255.0)[..., np.newaxis]
x_test = (x_test.astype("float32") / 255.0)[..., np.newaxis]

print("x_train:", x_train.shape, "y_train:", y_train.shape)
print("x_test:", x_test.shape, "y_test:", y_test.shape)

# 2. Modelo: red convolucional (CNN)
# Las capas de aumento (rotar, mover, acercar) solo actuan al entrenar y hacen
# que el modelo funcione mejor con digitos dibujados con el mouse.
model = tf.keras.Sequential([
    tf.keras.Input(shape=(28, 28, 1)),
    layers.RandomRotation(0.06),
    layers.RandomTranslation(0.08, 0.08),
    layers.RandomZoom(0.1),

    layers.Conv2D(32, 3, activation="relu"),
    layers.MaxPooling2D(),
    layers.Conv2D(64, 3, activation="relu"),
    layers.MaxPooling2D(),

    layers.Flatten(),
    layers.Dropout(0.3),  # evita sobreajuste
    layers.Dense(128, activation="relu"),
    layers.Dense(10, activation="softmax"),  # probabilidad de cada digito 0-9
])

model.compile(optimizer="adam",
              loss="sparse_categorical_crossentropy",
              metrics=["accuracy"])
model.summary()

# 3. Entrenamiento
hist = model.fit(x_train, y_train,
                 epochs=12,
                 batch_size=128,
                 validation_split=0.1)

# 4. Evaluacion con datos que el modelo nunca vio
loss, acc = model.evaluate(x_test, y_test, verbose=0)
print(f"Test loss: {loss:.4f} | Test accuracy: {acc:.4f}")

# 5. Guardar modelo
model.save(RUTA_MODELO)
print(f"Modelo guardado en {RUTA_MODELO}")

# 6. Graficas de entrenamiento
plt.figure(figsize=(12, 5))

plt.subplot(1, 2, 1)
plt.plot(hist.history["accuracy"], label="Entrenamiento")
plt.plot(hist.history["val_accuracy"], label="Validacion")
plt.title("Precision")
plt.xlabel("Epoca")
plt.legend()
plt.grid(True)

plt.subplot(1, 2, 2)
plt.plot(hist.history["loss"], label="Entrenamiento")
plt.plot(hist.history["val_loss"], label="Validacion")
plt.title("Perdida")
plt.xlabel("Epoca")
plt.legend()
plt.grid(True)

plt.tight_layout()
plt.savefig(os.path.join(BASE, "entrenamiento.png"))
plt.show()
