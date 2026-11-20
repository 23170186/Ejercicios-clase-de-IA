import numpy as np  
import matplotlib.pyplot as plt 
from sklearn.datasets import fetch_california_housing

plt.style.use('seaborn-v0_8-whitegrid'
              if 'seaborn-v0_8-whitegrid' in plt.style.available else 'default')
#  CARGA DE DATOS
N_MUESTRAS = 50  # cuantas zonas usar (prueba con 20640 para usar todo el dataset)

california = fetch_california_housing(as_frame=True)
X = california.data['MedInc'].values[:N_MUESTRAS]
Y = california.target.values[:N_MUESTRAS]
n = len(X)

print(f"Datos cargados con exito: {n} registros analizados.")

# PARAMETROS
w = 0.0  # pendiente: cuanto sube el precio por cada unidad de ingreso
b = 0.0  # ordenada: precio estimado cuando el ingreso es 0

learning_rate = 0.005  # tamano de cada paso del gradiente descendente
epochs = 20000         # veces que se repite prediccion + ajuste (con menos, b no alcanza a converger)

historial_costos = []

# ENTRENAMIENTO (gradiente descendente)
for epoch in range(epochs):
    # Prediccion con la recta actual
    Y_pred = w * X + b

    # Costo: error cuadratico medio (con 1/2 para simplificar la derivada)
    error = Y_pred - Y
    cost = (1 / (2 * n)) * np.sum(error ** 2)
    historial_costos.append(cost)

    # Gradientes: derivadas parciales del costo respecto a w y b.
    # Como el costo lleva 1/(2n), la derivada queda con 1/n (el 2 se cancela).
    dw = (1 / n) * np.sum(error * X)
    db = (1 / n) * np.sum(error)

    # Actualizacion: se avanza en direccion contraria al gradiente
    w = w - learning_rate * dw
    b = b - learning_rate * db

print("¡Entrenamiento completado!")
print(f"Ecuacion final aprendida: Valor Casa = {w:.4f} * (Ingreso Medio) + {b:.4f}")

# VERIFICACION
w_exacto, b_exacto = np.polyfit(X, Y, 1)
print(f"Solucion exacta (minimos cuadrados): w = {w_exacto:.4f}, b = {b_exacto:.4f}")

# R2: que parte de la variacion del precio explica el ingreso (1 = perfecto, 0 = nada)
ss_res = np.sum((Y - (w * X + b)) ** 2)
ss_tot = np.sum((Y - Y.mean()) ** 2)
print(f"R2 del modelo: {1 - ss_res / ss_tot:.3f}")
print(f"Costo final (MSE/2): {historial_costos[-1]:.4f}")

# Ejemplo de uso: predecir el precio para un ingreso dado
ingreso = 5.0  # = $50,000 de ingreso medio
precio = (w * ingreso + b) * 100_000
print(f"Con ingreso medio de {ingreso} (${ingreso * 10_000:,.0f}), "
      f"el modelo predice una casa de ${precio:,.0f}")

# VISUALIZACION
plt.figure(figsize=(10, 6))
plt.scatter(X, Y, color='royalblue', s=70,
            label='Datos Reales (California Housing)', zorder=5)

X_linea = np.linspace(min(X) - 0.5, max(X) + 0.5, 100)
plt.plot(X_linea, w * X_linea + b, color='crimson', linewidth=3,
         label=f'Modelo Lineal: Y = {w:.2f}X + {b:.2f}')

plt.title('Regresión Lineal From Scratch: Ingreso Medio vs Valor de la Vivienda',
          fontsize=12, fontweight='bold', pad=15)
plt.xlabel('Ingreso Medio (MedInc)', fontsize=11)
plt.ylabel('Valor de la Casa ($100,000s)', fontsize=11)
plt.legend(fontsize=11)
plt.grid(True, linestyle='--', alpha=0.6)
plt.show()

# Evolucion del costo: debe bajar con las epocas
plt.figure(figsize=(8, 4))
plt.plot(range(epochs), historial_costos, color='darkorange', linewidth=2)
plt.title('Evolución de la Función de Costo (Error Cuadrático Medio)',
          fontsize=11, fontweight='bold')
plt.xlabel('Épocas / Iteraciones', fontsize=10)
plt.ylabel('Costo (MSE)', fontsize=10)
plt.grid(True, linestyle='--', alpha=0.6)
plt.show()