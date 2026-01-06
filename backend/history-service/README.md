History/Stats Service

This service stores raw listening history and aggregated stats for listeners.

Build:

```bash
mvn -f backend/history-service clean package
```

Run (requires PostgreSQL on localhost:5432 with database `history_db`):

```bash
java -jar backend/history-service/target/history-service-0.0.1.jar
```

Docker build:

```bash
docker build -t history-service:local backend/history-service
```

Endpoints:
- `GET /api/history/{listenerId}` — list history records
- `POST /api/history/{listenerId}` — add history record
- `GET /api/stats/{listenerId}` — get aggregated stats
- `POST /api/stats/{listenerId}` — create/update stats
