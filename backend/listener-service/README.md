Listener Service

This service owns listener profiles. Listening history and aggregated stats are now owned by the `history-service` and accessed via HTTP calls.

Build:

```bash
mvn -f backend/listener-service clean package
```

Run (requires PostgreSQL on localhost:5432 with database `listener_db`):

```bash
java -jar backend/listener-service/target/listener-service-0.0.1.jar
```

Docker build:

```bash
docker build -t listener-service:local backend/listener-service
```

Endpoints:
- `GET /api/users/{id}` — get profile
- `GET /api/users/{id}/dashboard` — aggregated dashboard
- `GET /api/users/{id}/stats` — stats
- `PUT /api/users/{id}` — update
- `POST /api/users/register` — register
- `POST /api/users/login` — simple login (stub)
- `POST /api/users/{id}/history` — add history entry
	- Note: `POST /api/users/{id}/history` forwards the history record to the `history-service` (`history.service.url`), so the listener service no longer persists history locally.
