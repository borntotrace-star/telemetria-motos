
# 🛵 Telemetría de Motos en Tiempo Real

Sistema distribuido de ingesta, simulación y persistencia de telemetría para flotas de motos eléctricas en tiempo real, desplegado sobre Kubernetes con persistencia en Apache Cassandra.

---

## 📌 Arquitectura del Sistema

El sistema simula y procesa eventos de telemetría (velocidad, nivel de batería, coordenadas GPS en Barcelona y estado operativo) emitidos periódicamente por cada vehículo.

    [ Simulador de Motos (Kotlin) ]
                  │
                  ▼
        [ MotoRepository ]
                  │
                  ▼  (CQL Driver)
       [ Apache Cassandra (k8s) ]
         ├── Keyspace: telemetria
         ├── Tabla: motos (Estado actual)
         └── Tabla: telemetria_motos (Series temporales)

---

## 🛠️ Stack Tecnológico

- Lenguaje: Kotlin (JVM)
- Gestor de compilación: Gradle (Kotlin DSL)
- Base de Datos NoSQL: Apache Cassandra (series temporales)
- Contenedores: Docker
- Orquestación: Kubernetes (KinD)
- Control de versiones: Git & GitHub

---

## 📂 Estructura del Proyecto

    telemetria-motos/
    ├── app/
    │   ├── src/main/kotlin/org/example/
    │   │   ├── SimuladorMotos.kt      # Simulación de rutas y consumo de batería
    │   │   ├── SimuladorMain.kt       # Entrypoint del bucle de simulación
    │   │   ├── CassandraConnection.kt # Inicialización del clúster y sesión CQL
    │   │   ├── MotoRepository.kt      # Inserción y consulta en Cassandra
    │   │   ├── ApiMain.kt             # Servicio de consulta de estado
    │   │   └── HistorialMain.kt       # Consulta histórica de trayectorias
    │   └── build.gradle.kts           # Dependencias Gradle
    ├── k8s/
    │   └── telemetria-motos.yaml      # Manifiestos de Kubernetes
    ├── Dockerfile                     # Imagen Docker
    └── .gitignore                     # Exclusión de binarios

---

## 🗄️ Modelo de Datos (Cassandra)

- Tabla motos: Almacena el último estado de cada unidad (id, matrícula, batería, velocidad, latitud, longitud, estado, timestamp).
- Tabla telemetria_motos: Almacena la serie temporal para trazabilidad de trayectorias con partición por id_moto y orden descendente por timestamp.

---

## 🚀 Despliegue y Ejecución

Compilación:
./gradlew build

Construcción Docker:
docker build -t telemetria-motos:latest .

Despliegue en Kubernetes:
kubectl apply -f k8s/telemetria-motos.yaml

Verificación de logs:
kubectl logs -l app=telemetria-motos -f

---

## 👤 Autor

- Igor Rotaru — @borntotrace-star (https://github.com/borntotrace-star)
