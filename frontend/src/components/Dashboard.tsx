import { useEffect, useState } from 'react';
import { userAPI, songsAPI } from '../services/api';

function Dashboard() {
  const [dashboardData, setDashboardData] = useState<any>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [imageError, setImageError] = useState(false);
  const [currentTrack, setCurrentTrack] = useState<any>(null);
  const refreshInterval = 60;
  const getArtistKey = (artist: any, index: number) =>
    artist?.id || artist?.artistId || artist?.spotifyId || index;

  const getArtistName = (artist: any) =>
    artist?.name || artist?.artistName || artist?.displayName || 'Unknown Artist';

  const getArtistImage = (artist: any) =>
    artist?.image ||
    artist?.images?.[0]?.url ||
    artist?.profileImage ||
    artist?.photoUrl ||
    null;

  const getSongKey = (song: any, index: number) =>
    song?.id || song?.songId || song?.spotifyId || index;

  const getSongName = (song: any) =>
    song?.name || song?.songName || song?.title || 'Unknown Song';

  const getSongArtistName = (song: any) =>
    song?.artistName || song?.artist?.name || song?.artists?.[0]?.name || 'Unknown Artist';

  const getSongImage = (song: any) =>
    song?.image ||
    song?.albumImage ||
    song?.album?.images?.[0]?.url ||
    song?.images?.[0]?.url ||
    null;

  useEffect(() => {
    const fetchDashboard = async () => {
      try {
        setLoading(true);
        setError(null);

        const userId = localStorage.getItem('userId');
        if (!userId) {
          setError('User ID not found. Please login.');
          setLoading(false);
          return;
        }

        console.log('Fetching dashboard data at:', new Date().toISOString());
        const response = await userAPI.getDashboard(userId);

        console.log('Dashboard data received:', response.data);
        console.log('Profile image URL:', response.data?.profileImage);

        setDashboardData(response.data);
        setImageError(false);
      } catch (err: any) {
        console.error('Error fetching dashboard:', err);
        setError(err.response?.data?.message || 'Failed to load dashboard');
      } finally {
        setLoading(false);
      }
    };

    fetchDashboard();

    if (refreshInterval > 0) {
      const intervalId = setInterval(() => {
        console.log('Auto-refreshing dashboard at:', new Date().toISOString());
        fetchDashboard();
      }, refreshInterval * 1000);

      return () => clearInterval(intervalId);
    }
  }, [refreshInterval]);

  // Fetch currently playing track
  useEffect(() => {
    const fetchCurrentlyPlaying = async () => {
      try {
        const response = await songsAPI.getCurrentlyPlaying();
        setCurrentTrack(response.data);
      } catch (err: any) {
        console.error('Error fetching currently playing:', err);
        setCurrentTrack(null);
      }
    };

    fetchCurrentlyPlaying();

    // Poll every 10 seconds for updates
    const intervalId = setInterval(fetchCurrentlyPlaying, 10000);
    return () => clearInterval(intervalId);
  }, []);

  if (loading) return <div style={{ padding: '20px', color: '#1DB954' }}>Loading...</div>;
  if (error) return <div style={{ padding: '20px', color: '#1DB954' }}>Error: {error}</div>;

  return (
    <div
      style={{
        padding: '0 20px 20px 20px',
        color: 'white',
        minHeight: '100%',
        width: '100%',
        boxSizing: 'border-box',
        backgroundColor: '#000'
      }}
    >
      {/* Header */}
      <h1 style={{ 
        marginTop: '40px',
        marginBottom: '40px', 
        color: '#1DB954',
        fontSize: '3rem',
        fontWeight: '700',
        letterSpacing: '-0.5px'
      }}>
        Dashboard
      </h1>

      {dashboardData && (
        <div>
          {/* Profile and Now Playing Row */}
          <div style={{
            display: 'grid',
            gridTemplateColumns: 'repeat(auto-fit, minmax(300px, 1fr))',
            gap: '2rem',
            marginBottom: '40px'
          }}>
            {/* Profile Section */}
            <div style={{
              backgroundColor: 'rgba(29, 185, 84, 0.05)',
              border: '1px solid rgba(29, 185, 84, 0.3)',
              borderRadius: '16px',
              padding: '24px',
              boxShadow: '0 4px 12px rgba(0, 0, 0, 0.3)'
            }}>
              <h2 style={{ 
                marginTop: 0,
                marginBottom: '20px', 
                color: '#1DB954',
                fontSize: '0.7rem',
                fontWeight: '700',
                letterSpacing: '1px',
                textTransform: 'uppercase'
              }}>Profile</h2>
              <div
                style={{
                  display: 'flex',
                  flexDirection: 'row',
                  alignItems: 'flex-start',
                  gap: '1.5rem'
                }}
              >
                <div
                  style={{
                    width: '100px',
                    height: '100px',
                    minWidth: '100px',
                    borderRadius: '50%',
                    overflow: 'hidden',
                    backgroundColor: 'rgba(255, 255, 255, 0.1)',
                    display: 'flex',
                    alignItems: 'center',
                    justifyContent: 'center',
                    boxShadow: '0 4px 8px rgba(0, 0, 0, 0.3)',
                    border: '2px solid rgba(29, 185, 84, 0.4)'
                  }}
                >
                  {dashboardData.profileImage && !imageError ? (
                    <img
                      src={dashboardData.profileImage}
                      alt={dashboardData.username || 'User'}
                      style={{ width: '100%', height: '100%', objectFit: 'cover' }}
                      onError={() => {
                        console.error('Failed to load profile image:', dashboardData.profileImage);
                        setImageError(true);
                      }}
                    />
                  ) : (
                    <svg width="50" height="50" viewBox="0 0 24 24" fill="none" stroke="#1DB954" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                      <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"></path>
                      <circle cx="12" cy="7" r="4"></circle>
                    </svg>
                  )}
                </div>

                <div style={{ display: 'flex', flexDirection: 'column', flex: 1, justifyContent: 'center', gap: '8px' }}>
                  <div>
                    <h3 style={{ 
                      margin: '0 0 4px 0', 
                      color: 'white', 
                      fontSize: '1.5rem',
                      fontWeight: '700',
                      letterSpacing: '-0.5px'
                    }}>
                      {dashboardData.username || 'N/A'}
                    </h3>
                    <p style={{ 
                      margin: 0, 
                      color: 'rgba(255, 255, 255, 0.6)', 
                      fontSize: '0.9rem',
                      fontWeight: '400'
                    }}>
                      {dashboardData.email || 'N/A'}
                    </p>
                  </div>
                </div>
              </div>
            </div>

            {/* Now Playing Section */}
            <div style={{
              backgroundColor: 'rgba(29, 185, 84, 0.05)',
              border: '1px solid rgba(29, 185, 84, 0.3)',
              borderRadius: '16px',
              padding: '24px',
              boxShadow: '0 4px 12px rgba(0, 0, 0, 0.3)'
            }}>
              <h2 style={{
                marginTop: 0,
                marginBottom: '20px',
                color: '#1DB954',
                fontSize: '0.7rem',
                fontWeight: '700',
                letterSpacing: '1px',
                textTransform: 'uppercase'
              }}>Now Playing</h2>
              {currentTrack ? (
                <div style={{
                  display: 'flex',
                  gap: '16px',
                  alignItems: 'flex-start'
                }}>
                  {/* Album Artwork */}
                  <div style={{
                    flexShrink: 0,
                    width: '80px',
                    height: '80px',
                    borderRadius: '8px',
                    overflow: 'hidden',
                    backgroundColor: 'rgba(29, 185, 84, 0.2)',
                    border: '1px solid rgba(29, 185, 84, 0.4)'
                  }}>
                    {currentTrack.albumImage || currentTrack.image ? (
                      <img
                        src={currentTrack.albumImage || currentTrack.image}
                        alt={currentTrack.album || 'Album'}
                        style={{
                          width: '100%',
                          height: '100%',
                          objectFit: 'cover'
                        }}
                        onError={(e) => {
                          e.currentTarget.style.display = 'none';
                          e.currentTarget.parentElement!.innerHTML = '<div style="width:100%;height:100%;display:flex;align-items:center;justify-content:center;color:#1DB954;font-size:2rem;">🎵</div>';
                        }}
                      />
                    ) : (
                      <div style={{
                        width: '100%',
                        height: '100%',
                        display: 'flex',
                        alignItems: 'center',
                        justifyContent: 'center',
                        color: '#1DB954',
                        fontSize: '2rem'
                      }}>
                        🎵
                      </div>
                    )}
                  </div>

                  {/* Track Info */}
                  <div style={{
                    flex: 1,
                    minWidth: 0,
                    display: 'flex',
                    flexDirection: 'column',
                    gap: '6px'
                  }}>
                    <div style={{
                      fontSize: '1.05rem',
                      fontWeight: '700',
                      color: 'white',
                      lineHeight: '1.3',
                      overflow: 'hidden',
                      textOverflow: 'ellipsis',
                      display: '-webkit-box',
                      WebkitLineClamp: 2,
                      WebkitBoxOrient: 'vertical'
                    }}>
                      {currentTrack.name || 'Unknown Track'}
                    </div>
                    <div style={{
                      fontSize: '0.9rem',
                      color: 'rgba(255, 255, 255, 0.7)',
                      fontWeight: '500',
                      overflow: 'hidden',
                      textOverflow: 'ellipsis',
                      whiteSpace: 'nowrap'
                    }}>
                      {currentTrack.artist || 'Unknown Artist'}
                    </div>
                    {currentTrack.album && (
                      <div style={{
                        fontSize: '0.8rem',
                        color: 'rgba(255, 255, 255, 0.5)',
                        overflow: 'hidden',
                        textOverflow: 'ellipsis',
                        whiteSpace: 'nowrap'
                      }}>
                        {currentTrack.album}
                      </div>
                    )}
                    {currentTrack.isPlaying !== undefined && (
                      <div style={{
                        marginTop: '4px',
                        fontSize: '0.75rem',
                        color: currentTrack.isPlaying ? '#1DB954' : 'rgba(255, 255, 255, 0.5)',
                        fontWeight: '600',
                        display: 'flex',
                        alignItems: 'center',
                        gap: '4px'
                      }}>
                        <span>{currentTrack.isPlaying ? '▶' : '⏸'}</span>
                        <span>{currentTrack.isPlaying ? 'Playing' : 'Paused'}</span>
                      </div>
                    )}
                  </div>
                </div>
              ) : (
                <div style={{
                  display: 'flex',
                  alignItems: 'center',
                  justifyContent: 'center',
                  padding: '20px',
                  fontSize: '0.9rem',
                  color: 'rgba(255, 255, 255, 0.5)',
                  fontStyle: 'italic'
                }}>
                  No track playing
                </div>
              )}
            </div>
          </div>

          {/* Stats Section */}
          {dashboardData.stats && (
            <div style={{ marginBottom: '40px' }}>
              <h2 style={{ 
                marginBottom: '24px', 
                color: 'white',
                fontSize: '1.5rem',
                fontWeight: '700',
                letterSpacing: '-0.3px'
              }}>Your Stats</h2>
              <div style={{ 
                display: 'grid',
                gridTemplateColumns: 'repeat(3, 1fr)',
                gap: '1.5rem',
                padding: '20px',
                backgroundColor: 'rgba(29, 185, 84, 0.1)',
                borderRadius: '12px',
                border: '1px solid rgba(29, 185, 84, 0.2)'
              }}>
                  <div style={{ textAlign: 'center' }}>
                    <div style={{ 
                      fontSize: '2rem', 
                      fontWeight: '700',
                      color: '#1DB954',
                      marginBottom: '4px',
                      letterSpacing: '-0.5px'
                    }}>
                      {dashboardData.stats.totalListeningTime || '0 min'}
                    </div>
                    <div style={{ 
                      fontSize: '0.8rem',
                      color: 'rgba(255, 255, 255, 0.6)',
                      textTransform: 'uppercase',
                      letterSpacing: '0.5px',
                      fontWeight: '600'
                    }}>
                      Listening Time
                    </div>
                  </div>
                  <div style={{ textAlign: 'center' }}>
                    <div style={{ 
                      fontSize: '2rem', 
                      fontWeight: '700',
                      color: '#1DB954',
                      marginBottom: '4px',
                      letterSpacing: '-0.5px'
                    }}>
                      {dashboardData.stats.songsPlayed || 0}
                    </div>
                    <div style={{ 
                      fontSize: '0.8rem',
                      color: 'rgba(255, 255, 255, 0.6)',
                      textTransform: 'uppercase',
                      letterSpacing: '0.5px',
                      fontWeight: '600'
                    }}>
                      Songs Played
                    </div>
                  </div>
                  <div style={{ textAlign: 'center' }}>
                    <div style={{ 
                      fontSize: '2rem', 
                      fontWeight: '700',
                      color: '#1DB954',
                      marginBottom: '4px',
                      letterSpacing: '-0.5px'
                    }}>
                      {dashboardData.stats.currentStreak || 0}
                    </div>
                    <div style={{ 
                      fontSize: '0.8rem',
                      color: 'rgba(255, 255, 255, 0.6)',
                      textTransform: 'uppercase',
                      letterSpacing: '0.5px',
                      fontWeight: '600'
                    }}>
                      Day Streak
                    </div>
                  </div>
                </div>
            </div>
          )}

          <h2 style={{ 
            marginBottom: '24px',
            marginTop: '48px',
            color: 'white',
            fontSize: '1.5rem',
            fontWeight: '700',
            letterSpacing: '-0.3px'
          }}>Recent Top Artists</h2>
          {dashboardData.topArtists && dashboardData.topArtists.length > 0 ? (
            <div
              className="artists-grid"
              style={{
                display: 'grid',
                gridTemplateColumns: 'repeat(5, 1fr)',
                gap: '2rem',
                padding: '0 0 40px 0'
              }}
            >
              {dashboardData.topArtists.slice(0, 10).map((artist: any, index: number) => {
                const artistImage = getArtistImage(artist);
                return (
                  <div
                    key={getArtistKey(artist, index)}
                    style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', textAlign: 'center' }}
                  >
                    <div
                      style={{
                        width: '100%',
                        aspectRatio: '1',
                        borderRadius: '50%',
                        overflow: 'hidden',
                        marginBottom: '12px',
                        backgroundColor: 'rgba(255, 255, 255, 0.1)',
                        display: 'flex',
                        alignItems: 'center',
                        justifyContent: 'center',
                        boxShadow: '0 4px 8px rgba(0, 0, 0, 0.3)'
                      }}
                    >
                      {artistImage ? (
                        <img
                          src={artistImage}
                          alt={getArtistName(artist)}
                          style={{ width: '100%', height: '100%', objectFit: 'cover' }}
                        />
                      ) : (
                        <div style={{ color: 'rgba(255, 255, 255, 0.5)', fontSize: '3rem' }}>🎵</div>
                      )}
                    </div>
                    <p
                      style={{
                        color: 'white',
                        fontSize: '0.95rem',
                        fontWeight: '600',
                        margin: 0,
                        wordBreak: 'break-word',
                        letterSpacing: '-0.2px'
                      }}
                    >
                      {getArtistName(artist)}
                    </p>
                  </div>
                );
              })}
            </div>
          ) : (
            <p style={{ color: 'rgba(255, 255, 255, 0.6)', fontSize: '0.95rem' }}>No top artists data available</p>
          )}
          <h2 style={{ 
            marginBottom: '24px',
            marginTop: '48px',
            color: 'white',
            fontSize: '1.5rem',
            fontWeight: '700',
            letterSpacing: '-0.3px'
          }}>Recent Top Songs</h2>
          {dashboardData.topSongs && dashboardData.topSongs.length > 0 ? (
            <div
              className="songs-grid"
              style={{
                display: 'grid',
                gridTemplateColumns: 'repeat(5, 1fr)',
                gap: '2rem',
                padding: '0 0 40px 0'
              }}
            >
              {dashboardData.topSongs.slice(0, 10).map((song: any, index: number) => {
                const songImage = getSongImage(song);
                return (
                  <div
                    key={getSongKey(song, index)}
                    style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', textAlign: 'center' }}
                  >
                    <div
                      style={{
                        width: '100%',
                        aspectRatio: '1',
                        borderRadius: '8px',
                        overflow: 'hidden',
                        marginBottom: '12px',
                        backgroundColor: 'rgba(255, 255, 255, 0.1)',
                        display: 'flex',
                        alignItems: 'center',
                        justifyContent: 'center',
                        boxShadow: '0 4px 8px rgba(0, 0, 0, 0.3)'
                      }}
                    >
                      {songImage ? (
                        <img
                          src={songImage}
                          alt={getSongName(song)}
                          style={{ width: '100%', height: '100%', objectFit: 'cover' }}
                        />
                      ) : (
                        <div style={{ color: 'rgba(255, 255, 255, 0.5)', fontSize: '3rem' }}>🎵</div>
                      )}
                    </div>
                    <p
                      style={{
                        color: 'white',
                        fontSize: '0.95rem',
                        fontWeight: '600',
                        margin: '0 0 6px 0',
                        wordBreak: 'break-word',
                        letterSpacing: '-0.2px'
                      }}
                    >
                      {getSongName(song)}
                    </p>
                    <p
                      style={{
                        color: 'rgba(255, 255, 255, 0.6)',
                        fontSize: '0.85rem',
                        margin: 0,
                        wordBreak: 'break-word',
                        fontWeight: '400'
                      }}
                    >
                      {getSongArtistName(song)}
                    </p>
                  </div>
                );
              })}
            </div>
          ) : (
            <p style={{ color: 'rgba(255, 255, 255, 0.6)', fontSize: '0.95rem' }}>No top songs data available</p>
          )}
        </div>
      )}
    </div>
  );
}

export default Dashboard;