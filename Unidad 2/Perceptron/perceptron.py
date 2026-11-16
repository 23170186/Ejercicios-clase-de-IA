#HERRERA ZEPEDA JORGE ARMANDO
from sklearn.linear_model import Perceptron

#Perceptron para poder predecir si un alumno aprueba o reprueba basandonos en sus horas de estudios y asistencias a la semana

# Datos de entrenamiento
# [Horas de estudio a la semana, Porcentaje de asistencia total
A = [
    [1.0, 4.0],
    [2.0, 5.0],
    [1.5, 8.0],
    [5.0, 4.0],
    [5.0, 8.5],
    [6.5, 9.0],
    [4.5, 9.5],
    [7.0, 7.5],
]
# 0  Reprobado y 1 Aprobado
# Solo da 1 cuando el alumno cumple con suficientes horas de estudio y asistencia.
B = [0, 0, 0, 0, 1, 1, 1, 1]

# Crear el perceptrón utilizando sklearn.
perceptron = Perceptron(max_iter=1000, tol= None, random_state=42)

# Entrenar el perceptrón con los datos de entrenamiento.
perceptron.fit(A, B)

# Probar la neurona con nuevas entradas (alumnos no vistos) para ver si aprendió correctamente.
casos_prueba = [
    [2.0, 6.0],
    [6.0, 8.5],
    [3.5, 5.0],
    [5.5, 9.0],
]
predicciones = perceptron.predict(casos_prueba)

print("RESULTADOS DEL PERCEPTRÓN")
for entrada, resultado in zip(casos_prueba, predicciones):
  estado = "Aprobado" if resultado == 1 else "Reprobado"
  print(
      f"Entrada: {entrada} -> El perceptrón predice: {resultado} ({estado})"
  )

# resultados internos del modelo.
# Los pesos indican qué tanta importancia le dio la neurona a cada variable.
print("\nPesos sinápticos aprendidos (w):", perceptron.coef_)
# El sesgo actúa como el umbral mínimo que deben superar las entradas ponderadas para aprobar.
print("Sesgo aprendido (bias / b):", perceptron.intercept_)
#(9.5 * x1 + 4.5 * x2 - 68 >= 0) para que apruebe (el 0.5 y 4.5 son los pesos sinapticos)
