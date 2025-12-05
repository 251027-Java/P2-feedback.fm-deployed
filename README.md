# feedback.fm

Group project 1 is a full-stack web application that allows users to view their Spotify stats (including top artists, songs, albums, currently playing tracks, and listening history) and is built using React, Spring, and PostgresSQL.

## Technologies Used

### Frontend
- React
- Reactbits.dev (subject to change)

### Backend
- Java
- Spring Boot
- Spring Data JPA
- Spring Web
- Maven
- JUnit
- Mockito
- Spotify Web API

### Database
- PostgreSQL
- Docker

### Ensure you have:
- Node.js v16 or higher
- npm
- Java JDK 17 or higher
- Maven v3.8 or higher
- DOcker
- Git
- Spotify Account (for API credentials)

## Installation

### 1. Clone the Repo

```bash
cd feedback.fm
git clone https://github.com/251027-Java/P1-feedback.fm
```

### 2. Setup the Database

Our program just requires you to start a PostgreSQL container either through the docker application itself or through these bash commands.

Starts up the PostgreSQL db using Docker:
```bash
cd db
docker-compose up -d
```

Verifies db is running:
```bash
docker ps
```

### 3. Backend Setup
```properties
add this later for spring and the spotify api
```

The backend API will be available at `http://localhost:8080`

### 4. Frontend Setup

in `cd frontend`

Install dependencies

```bash
npm install
```

The frontend will be available at `http://localhost:3000`

## Database Schema

The database consists of 7 tables (in 3NF):
- users
- artists
- songs
- albums
- playlists
- playlist_songs (junction)
- listening_history (junction)

See (./db/ERD.png) for the Entity Relationship Diagram

## License

This project is created for educational purposes as part of a training program.

