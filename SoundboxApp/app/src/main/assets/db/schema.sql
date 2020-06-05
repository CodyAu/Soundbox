-- Create database
CREATE SCHEMA PUBLIC AUTHORIZATION DBA

-- Create tables
CREATE MEMORY TABLE IF NOT EXISTS PUBLIC.Songs(
    song_id BIGINT IDENTITY,
    song_name VARCHAR(255) NOT NULL,
    song_artist VARCHAR(255) NOT NULL,
    song_album_artist VARCHAR(255) NOT NULL,
    song_album VARCHAR(255) NOT NULL,
    song_genre VARCHAR(255) NOT NULL,
    song_score INTEGER DEFAULT 0,
    song_filepath VARCHAR(255) NOT NULL,
    song_mime_type VARCHAR(255),
    song_length INTEGER,
    song_bitrate INTEGER,
    song_size BIGINT,
    song_has_thumbnail BOOLEAN DEFAULT FALSE,
    PRIMARY KEY(song_id),
    UNIQUE(song_filepath)
)

CREATE MEMORY TABLE IF NOT EXISTS PUBLIC.Playlists(
    playlist_id BIGINT IDENTITY,
    playlist_name VARCHAR(255) NOT NULL,
    playlist_thumbnail INTEGER,
    PRIMARY KEY(playlist_id)
)

CREATE MEMORY TABLE IF NOT EXISTS PUBLIC.PlaylistSongs(
    playlist_id BIGINT,
    song_id BIGINT,
    FOREIGN KEY (song_id)
        REFERENCES PUBLIC.Songs(song_id)
    	ON DELETE CASCADE,
    FOREIGN KEY (playlist_id)
            REFERENCES PUBLIC.Playlists(playlist_id)
        	ON DELETE CASCADE,
    PRIMARY KEY(song_id, playlist_id)
)

CREATE MEMORY TABLE IF NOT EXISTS PUBLIC.SongStatistics(
    song_id BIGINT,
    stats_id INTEGER,
    stats_value INTEGER DEFAULT 0,
    FOREIGN KEY (song_id)
        REFERENCES PUBLIC.Songs(song_id)
    	ON DELETE CASCADE,
    PRIMARY KEY(song_id, stats_id)
)

-- Database user
CREATE USER SA PASSWORD DIGEST 'd41d8cd98f00b204e9800998ecf8427e'
ALTER USER SA SET LOCAL TRUE

SET DATABASE DEFAULT INITIAL SCHEMA PUBLIC
GRANT DBA TO SA
SET WRITE_DELAY 20
SET FILES SCALE 32
SET SCHEMA PUBLIC

-- Any initial insertions
