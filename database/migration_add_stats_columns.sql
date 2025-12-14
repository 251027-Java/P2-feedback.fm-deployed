-- Migration: Add cumulative stats columns to listener table
-- Run this SQL script to add the new columns for tracking listening statistics

ALTER TABLE listener 
ADD COLUMN IF NOT EXISTS total_listening_time_ms BIGINT DEFAULT 0,
ADD COLUMN IF NOT EXISTS total_songs_played INTEGER DEFAULT 0;

-- Update existing records to have default values
UPDATE listener 
SET total_listening_time_ms = 0 
WHERE total_listening_time_ms IS NULL;

UPDATE listener 
SET total_songs_played = 0 
WHERE total_songs_played IS NULL;

-- Set NOT NULL constraints after updating existing data
ALTER TABLE listener 
ALTER COLUMN total_listening_time_ms SET NOT NULL,
ALTER COLUMN total_songs_played SET NOT NULL;
