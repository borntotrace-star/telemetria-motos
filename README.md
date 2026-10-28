# Real-Time Motorcycle Telemetry

Example application that simulates electric motorcycles, stores their current state and location history in Apache Cassandra, and exposes the data through an HTTP API built with Kotlin and Ktor.

## Architecture

```text
Simulator (Kotlin) ──► Cassandra ◄── HTTP API (Ktor)
                           │
                           ├── motos: latest state per motorcycle
                           ├── motos_por_flota: fleet queries
                           └── telemetria_motos: history per motorcycle
```

The simulator and API run as separate processes. The Kubernetes manifest creates a Deployment for each process and an internal Service for the API. Cassandra must be available separately on the cluster network.

## Requirements

- JDK 26 (Gradle uses Java Toolchain 26).
- Docker.
- Apache Cassandra reachable on port 9042.
- `kubectl` and a Kubernetes cluster for optional deployment.

## Configure Cassandra

The schema is in [`k8s/schema.cql`](k8s/schema.cql). Apply it to your Cassandra instance before starting the application:

```bash
cqlsh "${CASSANDRA_HOST:-127.0.0.1}" 9042 -f k8s/schema.cql
```

If Cassandra is running on your machine, set `CASSANDRA_HOST=127.0.0.1`. The Kubernetes manifest assumes a Service named `cassandra` exists in the same namespace.

## Build and run locally

Build the project and create the JAR:

```bash
./gradlew :app:build
```

Set these variables if Cassandra is not available at `127.0.0.1:9042`:

```bash
export CASSANDRA_HOST=127.0.0.1
export CASSANDRA_PORT=9042
export CASSANDRA_DATACENTER=datacenter1
export CASSANDRA_KEYSPACE=telemetria
```

Start the API and simulator in separate terminals:

```bash
java -jar app/build/libs/app.jar
```

```bash
java -cp app/build/libs/app.jar org.example.SimuladorMainKt
```

The API listens on port 8080. Available endpoints:

| Method | Path | Description |
| --- | --- | --- |
| `GET` | `/health/live` | Checks that the API process is running. |
| `GET` | `/health/ready` | Checks that the API can query Cassandra. |
| `GET` | `/motos` | Returns the motorcycles in the `barcelona` fleet. |
| `GET` | `/motos/{id}` | Returns the current state of a motorcycle. |
| `GET` | `/motos/{id}/historial` | Returns a motorcycle's history, newest first. |

Example:

```bash
curl http://localhost:8080/motos/moto-101
```

## Docker

Build the JAR and Docker image:

```bash
./gradlew :app:build
docker build -t telemetria-motos:latest .
```

The container runs the API as an unprivileged user. Kubernetes overrides the container command to run the simulator.

## Kubernetes

The image must be available to the cluster. The manifest uses `localhost:5001/telemetria-motos:latest`; change that image name or push the image to that registry before applying the resources:

```bash
docker build -t localhost:5001/telemetria-motos:latest .
docker push localhost:5001/telemetria-motos:latest
kubectl apply -f k8s/telemetria-motos.yaml
```

View the logs and forward the API port:

```bash
kubectl logs deployment/telemetria-api -f
kubectl logs deployment/telemetria-simulador -f
kubectl port-forward service/telemetria-api 8080:8080
```

## Project structure

```text
app/src/main/kotlin/org/example/
├── ApiMain.kt               # HTTP API
├── CassandraConnection.kt   # Shared Cassandra configuration
├── HistorialMain.kt         # Command-line history query
├── MotoRepository.kt        # Queries and persistence
├── SimuladorMain.kt         # Simulator entry point
└── SimuladorMotos.kt        # Simulated fleet updates
k8s/
├── schema.cql               # Cassandra schema
└── telemetria-motos.yaml    # Kubernetes Deployments and Service
Dockerfile
README.md
```

## Author

Igor Rotaru — [@borntotrace-star](https://github.com/borntotrace-star)
