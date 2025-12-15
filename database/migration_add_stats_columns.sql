-- Migration: Add cumulative stats columns to listener table
-- Run this SQL script to add the new columns for tracking listening statistics
-- This migration handles both 'listener' (singular) and 'listeners' (plural) table names

-- Check if table exists and add columns (handles both table name variations)
DO $$
BEGIN
    -- Try to add columns to 'listener' table (singular - matches JPA entity)
    IF EXISTS (SELECT FROM information_schema.tables WHERE table_name = 'listener') THEN
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
        
        RAISE NOTICE 'Added stats columns to listener table';
    -- Handle legacy 'listeners' table (plural - from old init.sql)
    ELSIF EXISTS (SELECT FROM information_schema.tables WHERE table_name = 'listeners') THEN
        ALTER TABLE listeners 
        ADD COLUMN IF NOT EXISTS total_listening_time_ms BIGINT DEFAULT 0,
        ADD COLUMN IF NOT EXISTS total_songs_played INTEGER DEFAULT 0;
        
        -- Update existing records to have default values
        UPDATE listeners 
        SET total_listening_time_ms = 0 
        WHERE total_listening_time_ms IS NULL;
        
        UPDATE listeners 
        SET total_songs_played = 0 
        WHERE total_songs_played IS NULL;
        
        -- Set NOT NULL constraints after updating existing data
        ALTER TABLE listeners 
        ALTER COLUMN total_listening_time_ms SET NOT NULL,
        ALTER COLUMN total_songs_played SET NOT NULL;
        
        RAISE NOTICE 'Added stats columns to listeners table (legacy)';
    ELSE
        RAISE NOTICE 'Neither listener nor listeners table found. Please ensure the table exists.';
    END IF;
END $$;
