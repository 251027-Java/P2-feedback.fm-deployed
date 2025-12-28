import { useEffect, useState } from 'react';
import { songsAPI } from '../services/api';

function CurrentlyPlaying() {
  const [currentTrack, setCurrentTrack] = useState<any>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [lastUpdated, setLastUpdated] = useState<Date>(new Date());

  useEffect(() => {
    const fetchCurrentlyPlaying = async () => {
      try {
        if (loading) setLoading(true);
        setError(null);
        const response = await songsAPI.getCurrentlyPlaying();
        setCurrentTrack(response.data);
        setLastUpdated(new Date());
      } catch (err: any) {
        console.error('Error fetching currently playing:', err);
        setError(
          err.response?.data?.message ||
            'Failed to load currently playing track',
        );
      } finally {
        setLoading(false);
      }
    };

    fetchCurrentlyPlaying();

    // Poll every 2 minutes for updates
    const intervalId = setInterval(fetchCurrentlyPlaying, 120000);
    return () => clearInterval(intervalId);
  }, []);

  if (loading)
    return <div style={{ padding: '20px', color: '#1DB954' }}>Loading...</div>;

  if (error)
    return (
      <div style={{ padding: '20px', color: '#1DB954' }}>Error: {error}</div>
    );

  const pageStyle = {
    padding: '0 20px 20px 20px',
    color: 'white',
    minHeight: '100%',
    backgroundColor: '#000',
  };

  if (
    !currentTrack ||
    !currentTrack.name ||
    currentTrack.name.trim() === '' ||
    currentTrack.isPlaying === false
  ) {
    return (
      <div style={pageStyle}>
        <div
          style={{
            display: 'flex',
            justifyContent: 'space-between',
            alignItems: 'center',
            marginTop: '40px',
            marginBottom: '40px',
          }}
        >
          <h1
            style={{
              margin: 0,
              color: '#1DB954',
              fontSize: '3rem',
              fontWeight: '700',
              letterSpacing: '-0.5px',
            }}
          >
            Now Playing
          </h1>
          <div
            style={{
              color: 'rgba(255, 255, 255, 0.5)',
              fontSize: '0.9rem',
              fontWeight: '500',
            }}
          >
            Updated {lastUpdated.toLocaleTimeString()}
          </div>
        </div>
        <div
          style={{
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            minHeight: '400px',
            backgroundColor: 'rgba(255, 255, 255, 0.03)',
            borderRadius: '20px',
            border: '1px solid rgba(255, 255, 255, 0.1)',
          }}
        >
          <div
            style={{
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
              padding: '20px',
              fontSize: '0.9rem',
              color: 'rgba(255, 255, 255, 0.5)',
              fontStyle: 'italic',
            }}
          >
            No track playing
          </div>
        </div>
      </div>
    );
  }

  const formatDuration = (ms: number) => {
    const minutes = Math.floor(ms / 60000);
    const seconds = Math.floor((ms % 60000) / 1000);
    return `${minutes}:${seconds.toString().padStart(2, '0')}`;
  };

  const progress =
    currentTrack.progress_ms && currentTrack.duration_ms
      ? (currentTrack.progress_ms / currentTrack.duration_ms) * 100
      : 0;

  return (
    <div style={pageStyle}>
      <div
        style={{
          display: 'flex',
          justifyContent: 'space-between',
          alignItems: 'center',
          marginTop: '40px',
          marginBottom: '40px',
        }}
      >
        <h1
          style={{
            margin: 0,
            color: '#1DB954',
            fontSize: '3rem',
            fontWeight: '700',
            letterSpacing: '-0.5px',
          }}
        >
          Now Playing
        </h1>
        <div
          style={{
            color: 'rgba(255, 255, 255, 0.5)',
            fontSize: '0.9rem',
            fontWeight: '500',
          }}
        >
          Updated {lastUpdated.toLocaleTimeString()}
        </div>
      </div>

      <div
        style={{
          background:
            'linear-gradient(135deg, rgba(29, 185, 84, 0.15) 0%, rgba(0, 0, 0, 0.3) 100%)',
          padding: '40px',
          borderRadius: '24px',
          border: '1px solid rgba(29, 185, 84, 0.2)',
          boxShadow: '0 8px 32px rgba(0, 0, 0, 0.4)',
          backdropFilter: 'blur(10px)',
          maxWidth: '900px',
          margin: '0 auto',
        }}
      >
        <div
          style={{
            display: 'flex',
            gap: '40px',
            alignItems: 'flex-start',
            flexWrap: 'wrap',
          }}
        >
          {/* Album Art */}
          <div
            style={{
              flex: '0 0 auto',
              display: 'flex',
              flexDirection: 'column',
              gap: '15px',
            }}
          >
            {currentTrack.image ? (
              <img
                src={currentTrack.image}
                alt={currentTrack.album || 'Album'}
                style={{
                  width: '280px',
                  height: '280px',
                  borderRadius: '16px',
                  boxShadow: '0 20px 60px rgba(0, 0, 0, 0.6)',
                  objectFit: 'cover',
                  border: '3px solid rgba(255, 255, 255, 0.1)',
                }}
              />
            ) : (
              <div
                style={{
                  width: '280px',
                  height: '280px',
                  borderRadius: '16px',
                  backgroundColor: 'rgba(255, 255, 255, 0.05)',
                  display: 'flex',
                  alignItems: 'center',
                  justifyContent: 'center',
                  fontSize: '5rem',
                  border: '3px solid rgba(255, 255, 255, 0.1)',
                }}
              >
                🎵
              </div>
            )}
            {/* Status Badge - Outside Image */}
            <div
              style={{
                backgroundColor: currentTrack.isPlaying
                  ? '#1DB954'
                  : 'rgba(255, 255, 255, 0.9)',
                color: currentTrack.isPlaying ? '#000' : '#666',
                padding: '12px 20px',
                borderRadius: '24px',
                fontSize: '0.9rem',
                fontWeight: '700',
                boxShadow: '0 4px 12px rgba(0, 0, 0, 0.3)',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
                gap: '8px',
              }}
            >
              <span style={{ fontSize: '1.1rem' }}>
                {currentTrack.isPlaying ? '▶' : '⏸'}
              </span>
              {currentTrack.isPlaying ? 'PLAYING' : 'PAUSED'}
            </div>
          </div>

          {/* Track Info */}
          <div
            style={{
              flex: '1 1 300px',
              display: 'flex',
              flexDirection: 'column',
              justifyContent: 'center',
              gap: '20px',
            }}
          >
            <div>
              <h2
                style={{
                  color: 'white',
                  fontSize: '2.5rem',
                  fontWeight: '800',
                  margin: '0 0 10px 0',
                  lineHeight: '1.2',
                  letterSpacing: '-0.5px',
                }}
              >
                {currentTrack.name || 'Unknown Track'}
              </h2>
              <p
                style={{
                  fontSize: '1.5rem',
                  margin: '0 0 8px 0',
                  color: 'rgba(255, 255, 255, 0.8)',
                  fontWeight: '600',
                }}
              >
                {currentTrack.artist || 'Unknown Artist'}
              </p>
              <p
                style={{
                  fontSize: '1.1rem',
                  margin: 0,
                  color: 'rgba(255, 255, 255, 0.6)',
                  fontWeight: '500',
                }}
              >
                {currentTrack.album || 'Unknown Album'}
              </p>
            </div>

            {/* Progress Bar */}
            {currentTrack.duration_ms && (
              <div style={{ marginTop: '10px' }}>
                <div
                  style={{
                    width: '100%',
                    height: '6px',
                    backgroundColor: 'rgba(255, 255, 255, 0.2)',
                    borderRadius: '3px',
                    overflow: 'hidden',
                    marginBottom: '8px',
                  }}
                >
                  <div
                    style={{
                      width: `${progress}%`,
                      height: '100%',
                      backgroundColor: '#1DB954',
                      transition: 'width 1s linear',
                      borderRadius: '3px',
                    }}
                  />
                </div>
                <div
                  style={{
                    display: 'flex',
                    justifyContent: 'space-between',
                    fontSize: '0.85rem',
                    color: 'rgba(255, 255, 255, 0.6)',
                    fontWeight: '500',
                  }}
                >
                  <span>
                    {currentTrack.progress_ms
                      ? formatDuration(currentTrack.progress_ms)
                      : '0:00'}
                  </span>
                  <span>{formatDuration(currentTrack.duration_ms)}</span>
                </div>
              </div>
            )}

            {/* Additional Info */}
            {currentTrack.href && (
              <div style={{ marginTop: '10px' }}>
                <button
                  type="button"
                  onClick={() => window.open(currentTrack.href, '_blank')}
                  style={{
                    backgroundColor: '#1DB954',
                    color: '#000',
                    border: 'none',
                    padding: '14px 32px',
                    borderRadius: '24px',
                    fontSize: '1rem',
                    fontWeight: '700',
                    cursor: 'pointer',
                    transition: 'all 0.2s ease',
                    boxShadow: '0 4px 12px rgba(29, 185, 84, 0.4)',
                    textTransform: 'uppercase',
                    letterSpacing: '0.5px',
                  }}
                  onMouseEnter={(e) => {
                    e.currentTarget.style.transform = 'scale(1.05)';
                    e.currentTarget.style.backgroundColor = '#1ed760';
                  }}
                  onMouseLeave={(e) => {
                    e.currentTarget.style.transform = 'scale(1)';
                    e.currentTarget.style.backgroundColor = '#1DB954';
                  }}
                >
                  ▶ Open in Spotify
                </button>
              </div>
            )}
          </div>
        </div>
      </div>
    </div>
  );
}

export default CurrentlyPlaying;
