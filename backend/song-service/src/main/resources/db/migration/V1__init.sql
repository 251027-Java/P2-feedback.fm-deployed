-- Create songs table for Song Service
CREATE TABLE songs (
    id SERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    duration_ms INTEGER,
    album_id INTEGER REFERENCES albums(id)
);
