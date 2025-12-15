import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { userAPI, songsAPI, historyAPI } from '../services/api';
import cycleList from '../../resources/cycleList?raw';
import ASCIIText from './ASCIIText';

function Dashboard() {
  const [dashboardData, setDashboardData] = useState<any>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [imageError, setImageError] = useState(false);
  const [currentTrack, setCurrentTrack] = useState<any>(null);
  const [recentlyPlayed, setRecentlyPlayed] = useState<any[]>([]);
  const [cycleMessage, setCycleMessage] = useState<string>('');
  const [cycleColor, setCycleColor] = useState<string>('#1DB954');
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

  const colorWithAlpha = (color: string, alpha: number) => {
    const match = color.match(/\d+/g);
    if (!match || match.length < 3) return `rgba(29, 185, 84, ${alpha})`;
    const [r, g, b] = match.map(Number);
    return `rgba(${r}, ${g}, ${b}, ${alpha})`;
  };

  const computeDominantColor = (img: HTMLImageElement) => {
    const canvas = document.createElement('canvas');
    const context = canvas.getContext('2d');
    if (!context) return null;
    const width = Math.max(1, Math.min(50, img.width || 50));
    const height = Math.max(1, Math.min(50, img.height || 50));
    canvas.width = width;
    canvas.height = height;
    context.drawImage(img, 0, 0, width, height);
    const imageData = context.getImageData(0, 0, width, height);
    const pixels = imageData.data;
    const buckets: Record<string, number> = {};
    const quant = 24;
    for (let i = 0; i < pixels.length; i += 4) {
      const r = Math.round(pixels[i] / quant) * quant;
      const g = Math.round(pixels[i + 1] / quant) * quant;
      const b = Math.round(pixels[i + 2] / quant) * quant;
      const key = `${r},${g},${b}`;
      buckets[key] = (buckets[key] || 0) + 1;
    }
    let dominant = null;
    let max = 0;
    Object.entries(buckets).forEach(([key, count]) => {
      if (count > max) {
        max = count;
        dominant = key;
      }
    });
    return dominant ? `rgb(${dominant})` : null;
  };

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
        const spotifyToken = localStorage.getItem('spotifyAccessToken');
        console.log('Spotify token exists:', !!spotifyToken);
        console.log('Spotify token length:', spotifyToken?.length || 0);
        const response = await userAPI.getDashboard(userId);

        console.log('Dashboard data received:', response.data);
        console.log('Profile image URL:', response.data?.profileImage);
        console.log('Stats data:', response.data?.stats);
        console.log('Total listening time:', response.data?.stats?.totalListeningTime);
        console.log('Songs played:', response.data?.stats?.songsPlayed);

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

  // Pick a random cycle message and color whenever the track changes
  useEffect(() => {
    if (!currentTrack || !currentTrack.name) return;
    const messages = cycleList.split(/\r?\n/).filter(Boolean);
    const palette = ['#1DB954', '#FF6B6B', '#FFD166', '#06D6A0', '#118AB2', '#C77DFF', '#F06595'];
    const message = messages[Math.floor(Math.random() * messages.length)];
    const fallbackColor = palette[Math.floor(Math.random() * palette.length)];
    setCycleMessage(message);

    const imgUrl = currentTrack.albumImage || currentTrack.image;
    if (!imgUrl) {
      setCycleColor(fallbackColor);
      return;
    }

    const img = new Image();
    img.crossOrigin = 'Anonymous';
    img.src = imgUrl;
    img.onload = () => {
      const dominant = computeDominantColor(img);
      setCycleColor(dominant || fallbackColor);
    };
    img.onerror = () => setCycleColor(fallbackColor);

    return () => {
      img.onload = null;
      img.onerror = null;
    };
  }, [currentTrack?.id, currentTrack?.name]);

  // Push accent color to CSS variable for shared components (e.g., nav/logo)
  useEffect(() => {
    if (!cycleColor) return;
    document.documentElement.style.setProperty('--accent-color', cycleColor);
  }, [cycleColor]);

  // Fetch recently played tracks
  useEffect(() => {
    const fetchRecentlyPlayed = async () => {
      try {
        const response = await historyAPI.getHistory(10);
        setRecentlyPlayed(response.data || []);
      } catch (err: any) {
        console.error('Error fetching recently played:', err);
        setRecentlyPlayed([]);
      }
    };

    fetchRecentlyPlayed();
  }, []);

  if (loading) {
    return (
      <div style={{ padding: '0 20px 20px 20px', color: 'white', backgroundColor: '#000', minHeight: '100%' }}>
        <div style={{ marginTop: '40px', marginBottom: '40px', height: '60px', width: '300px', backgroundColor: 'rgba(255, 255, 255, 0.1)', borderRadius: '8px', animation: 'pulse 1.5s ease-in-out infinite' }} />
        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(2, 1fr)', gap: '2rem', marginBottom: '40px' }}>
          {[1, 2].map(i => (
            <div key={i} style={{ height: '200px', backgroundColor: 'rgba(255, 255, 255, 0.05)', borderRadius: '16px', animation: 'pulse 1.5s ease-in-out infinite' }} />
          ))}
        </div>
        <style>{`@keyframes pulse { 0%, 100% { opacity: 1; } 50% { opacity: 0.5; } }`}</style>
      </div>
    );
  }
  
  if (error) return <div style={{ padding: '20px', color: cycleColor }}>Error: {error}</div>;

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
      {/* Header with Time Range Selector */}
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginTop: '40px', marginBottom: '40px', flexWrap: 'wrap', gap: '20px' }}>
        <h1 style={{ 
          margin: 0,
          color: cycleColor,
          fontSize: '3rem',
          fontWeight: '700',
          letterSpacing: '-0.5px'
        }}>
          Dashboard
        </h1>
        <div style={{ display: 'flex', gap: '10px', alignItems: 'center' }}>
        </div>
      </div>

      {dashboardData && (
        <div>
          {/* Quip above Profile/Now Playing */}
          <div style={{ marginBottom: '20px', width: '100%', display: 'flex' }}>
            <div style={{
              width: '100%',
              maxWidth: '100%',
              minHeight: '220px',
              borderRadius: '0px',
              border: 'none',
              backgroundColor: 'transparent',
              overflow: 'hidden',
              position: 'relative'
            }}>
              <ASCIIText
                text={cycleMessage || '...'}
                asciiFontSize={4}
                textFontSize={90}
                textColor={cycleColor}
                enableWaves={false}
              />
            </div>
          </div>

          {/* Profile and Now Playing Row */}
          <div style={{
            display: 'grid',
            gridTemplateColumns: 'repeat(auto-fit, minmax(300px, 1fr))',
            gap: '2rem',
            marginBottom: '40px'
          }}>
            {/* Profile Section */}
            <a
              href={`https://open.spotify.com/user/${dashboardData?.userId || ''}`}
              target="_blank"
              rel="noreferrer"
              style={{ textDecoration: 'none' }}
            >
              <div style={{
                backgroundColor: colorWithAlpha(cycleColor, 0.05),
                border: `1px solid ${colorWithAlpha(cycleColor, 0.3)}`,
                borderRadius: '16px',
                padding: '24px',
                boxShadow: `0 4px 12px ${colorWithAlpha(cycleColor, 0.25)}`,
                cursor: 'pointer',
                transition: 'all 0.2s ease',
              }}
              onMouseEnter={(e) => {
                e.currentTarget.style.transform = 'translateY(-2px)';
                e.currentTarget.style.boxShadow = `0 6px 16px ${colorWithAlpha(cycleColor, 0.3)}`;
                e.currentTarget.style.border = `1px solid ${colorWithAlpha(cycleColor, 0.5)}`;
              }}
              onMouseLeave={(e) => {
                e.currentTarget.style.transform = 'translateY(0)';
                e.currentTarget.style.boxShadow = `0 4px 12px ${colorWithAlpha(cycleColor, 0.25)}`;
                e.currentTarget.style.border = `1px solid ${colorWithAlpha(cycleColor, 0.3)}`;
              }}>
              <h2 style={{ 
                marginTop: 0,
                marginBottom: '20px', 
                color: cycleColor,
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
                    border: `2px solid ${colorWithAlpha(cycleColor, 0.4)}`
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
                    <svg width="50" height="50" viewBox="0 0 24 24" fill="none" stroke={cycleColor} strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
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
            </a>

            {/* Now Playing Section */}
            <Link to="/currently-playing" style={{ textDecoration: 'none' }}>
              <div style={{
                backgroundColor: 'rgba(29, 185, 84, 0.05)',
              border: `1px solid ${colorWithAlpha(cycleColor, 0.3)}`,
                borderRadius: '16px',
                padding: '24px',
              boxShadow: `0 4px 12px ${colorWithAlpha(cycleColor, 0.25)}`,
                cursor: 'pointer',
                transition: 'all 0.2s ease',
              }}
              onMouseEnter={(e) => {
                e.currentTarget.style.transform = 'translateY(-2px)';
                e.currentTarget.style.boxShadow = `0 6px 16px ${colorWithAlpha(cycleColor, 0.3)}`;
                e.currentTarget.style.border = `1px solid ${colorWithAlpha(cycleColor, 0.5)}`;
              }}
              onMouseLeave={(e) => {
                e.currentTarget.style.transform = 'translateY(0)';
                e.currentTarget.style.boxShadow = `0 4px 12px ${colorWithAlpha(cycleColor, 0.25)}`;
                e.currentTarget.style.border = `1px solid ${colorWithAlpha(cycleColor, 0.3)}`;
              }}>
                <h2 style={{
                  marginTop: 0,
                  marginBottom: '20px',
                  color: cycleColor,
                  fontSize: '0.7rem',
                  fontWeight: '700',
                  letterSpacing: '1px',
                  textTransform: 'uppercase'
                }}>Now Playing</h2>
                {currentTrack && currentTrack.name && currentTrack.name.trim() !== '' && currentTrack.isPlaying !== false ? (
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
                    backgroundColor: colorWithAlpha(cycleColor, 0.2),
                    border: `1px solid ${colorWithAlpha(cycleColor, 0.4)}`
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
                      {currentTrack.name}
                    </div>
                    <div style={{
                      fontSize: '0.9rem',
                      color: colorWithAlpha(cycleColor, 0.7),
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
            </Link>
          </div>

          {/* Stats Section */}
          <div style={{ marginBottom: '40px' }}>
            <h2 style={{ 
              marginBottom: '24px', 
              color: cycleColor,
              fontSize: '1.5rem',
              fontWeight: '700',
              letterSpacing: '-0.3px'
            }}>Your Stats</h2>
            <div style={{ 
              display: 'grid',
              gridTemplateColumns: 'repeat(3, minmax(180px, 1fr))',
              gap: '1.5rem',
              padding: '20px',
              backgroundColor: colorWithAlpha(cycleColor, 0.1),
              borderRadius: '12px',
              border: `1px solid ${colorWithAlpha(cycleColor, 0.2)}`
            }}>
                <div style={{ textAlign: 'center' }}>
                  <div style={{ 
                    fontSize: '2rem', 
                    fontWeight: '700',
                    color: cycleColor,
                    marginBottom: '4px',
                    letterSpacing: '-0.5px'
                  }}>
                    {dashboardData?.stats?.totalListeningTime || '0 minutes'}
                  </div>
                  <div style={{ 
                    fontSize: '0.8rem',
                    color: colorWithAlpha(cycleColor, 0.6),
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
                    color: cycleColor,
                    marginBottom: '4px',
                    letterSpacing: '-0.5px'
                  }}>
                    {dashboardData?.stats?.songsPlayed ?? 0}
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
                    color: cycleColor,
                    marginBottom: '4px',
                    letterSpacing: '-0.5px'
                  }}>
                    {dashboardData?.stats?.currentStreak ?? 0}
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

          {/* Recently Played Section */}
          {recentlyPlayed.length > 0 && (
            <div style={{ marginBottom: '48px' }}>
          <h2 style={{ 
            marginBottom: '24px',
            marginTop: '48px',
            color: cycleColor,
            fontSize: '1.5rem',
            fontWeight: '700',
            letterSpacing: '-0.3px'
          }}>Recently Played</h2>
              <div style={{
                backgroundColor: colorWithAlpha(cycleColor, 0.05),
                border: `1px solid ${colorWithAlpha(cycleColor, 0.2)}`,
                borderRadius: '16px',
                padding: '20px',
                maxHeight: '400px',
                overflowY: 'auto'
              }}>
                {recentlyPlayed.slice(0, 8).map((item: any, index: number) => {
                  const songName = item.songName || item.name || 'Unknown Song';
                  const artistName = item.artistName || item.artist?.name || 'Unknown Artist';
                  const image = item.image || null;
                  const href = item.href || item.external_urls?.spotify;
                  const playedAt = item.playedAt ? (() => {
                    try {
                      const date = new Date(item.playedAt);
                      const now = new Date();
                      const diffMs = now.getTime() - date.getTime();
                      const diffMins = Math.floor(diffMs / 60000);
                      const diffHours = Math.floor(diffMins / 60);
                      const diffDays = Math.floor(diffHours / 24);
                      
                      if (diffMins < 1) return 'Just now';
                      if (diffMins < 60) return `${diffMins}m ago`;
                      if (diffHours < 24) return `${diffHours}h ago`;
                      if (diffDays === 1) return 'Yesterday';
                      return `${diffDays}d ago`;
                    } catch {
                      return '';
                    }
                  })() : '';

                  return (
                    <div
                      key={item.id || index}
                      style={{
                        display: 'flex',
                        alignItems: 'center',
                        gap: '16px',
                        padding: '12px',
                        marginBottom: index < recentlyPlayed.length - 1 ? '8px' : 0,
                        backgroundColor: colorWithAlpha(cycleColor, 0.03),
                        borderRadius: '8px',
                        cursor: href ? 'pointer' : 'default',
                        transition: 'all 0.2s'
                      }}
                      onClick={() => href && window.open(href, '_blank')}
                      onMouseEnter={(e) => {
                        e.currentTarget.style.backgroundColor = colorWithAlpha(cycleColor, 0.08);
                        if (href) e.currentTarget.style.transform = 'translateX(4px)';
                      }}
                      onMouseLeave={(e) => {
                        e.currentTarget.style.backgroundColor = colorWithAlpha(cycleColor, 0.03);
                        e.currentTarget.style.transform = 'translateX(0)';
                      }}
                    >
                      {/* Album Art */}
                      <div style={{
                        width: '56px',
                        height: '56px',
                        minWidth: '56px',
                        borderRadius: '6px',
                        overflow: 'hidden',
                        backgroundColor: 'rgba(255, 255, 255, 0.1)',
                        display: 'flex',
                        alignItems: 'center',
                        justifyContent: 'center',
                        boxShadow: '0 2px 8px rgba(0, 0, 0, 0.3)'
                      }}>
                        {image ? (
                          <img src={image} alt={songName} style={{ width: '100%', height: '100%', objectFit: 'cover' }} />
                        ) : (
                          <div style={{ color: 'rgba(255, 255, 255, 0.4)', fontSize: '1.5rem' }}>🎵</div>
                        )}
                      </div>
                      {/* Song Info */}
                      <div style={{ flex: 1, minWidth: 0 }}>
                        <div style={{
                          color: 'white',
                          fontSize: '0.95rem',
                          fontWeight: '600',
                          marginBottom: '4px',
                          overflow: 'hidden',
                          textOverflow: 'ellipsis',
                          whiteSpace: 'nowrap'
                        }}>
                          {songName}
                        </div>
                        <div style={{
                          color: 'rgba(255, 255, 255, 0.6)',
                          fontSize: '0.85rem',
                          overflow: 'hidden',
                          textOverflow: 'ellipsis',
                          whiteSpace: 'nowrap'
                        }}>
                          {artistName}
                        </div>
                      </div>
                      {/* Time Ago */}
                      {playedAt && (
                        <div style={{
                          color: 'rgba(255, 255, 255, 0.5)',
                          fontSize: '0.8rem',
                          fontWeight: '500',
                          whiteSpace: 'nowrap'
                        }}>
                          {playedAt}
                        </div>
                      )}
                      {/* Play Icon */}
                      {href && (
                      <div style={{
                        color: cycleColor,
                        fontSize: '1.1rem',
                        opacity: 0.7,
                        transition: 'opacity 0.2s'
                      }}
                      onMouseEnter={(e) => e.currentTarget.style.opacity = '1'}
                      onMouseLeave={(e) => e.currentTarget.style.opacity = '0.7'}>
                        ▶
                      </div>
                      )}
                    </div>
                  );
                })}
              </div>
            </div>
          )}

          <h2 style={{ 
            marginBottom: '24px',
            marginTop: '48px',
            color: cycleColor,
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
                const href = artist.href || artist.external_urls?.spotify;
                return (
                  <div
                    key={getArtistKey(artist, index)}
                    style={{ 
                      display: 'flex', 
                      flexDirection: 'column', 
                      alignItems: 'center', 
                      textAlign: 'center',
                      cursor: href ? 'pointer' : 'default',
                      transition: 'transform 0.2s ease'
                    }}
                    onClick={() => href && window.open(href, '_blank')}
                    onMouseEnter={(e) => {
                      if (href) e.currentTarget.style.transform = 'translateY(-8px)';
                    }}
                    onMouseLeave={(e) => {
                      e.currentTarget.style.transform = 'translateY(0)';
                    }}
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
                        boxShadow: '0 4px 8px rgba(0, 0, 0, 0.3)',
                        border: '2px solid transparent',
                        transition: 'border-color 0.2s'
                      }}
                      onMouseEnter={(e) => {
                        if (href) e.currentTarget.style.borderColor = cycleColor;
                      }}
                      onMouseLeave={(e) => {
                        e.currentTarget.style.borderColor = 'transparent';
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
            color: cycleColor,
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
                const href = song.href || song.external_urls?.spotify;
                return (
                  <div
                    key={getSongKey(song, index)}
                    style={{ 
                      display: 'flex', 
                      flexDirection: 'column', 
                      alignItems: 'center', 
                      textAlign: 'center',
                      cursor: href ? 'pointer' : 'default',
                      transition: 'transform 0.2s ease'
                    }}
                    onClick={() => href && window.open(href, '_blank')}
                    onMouseEnter={(e) => {
                      if (href) e.currentTarget.style.transform = 'translateY(-8px)';
                    }}
                    onMouseLeave={(e) => {
                      e.currentTarget.style.transform = 'translateY(0)';
                    }}
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
                        boxShadow: '0 4px 8px rgba(0, 0, 0, 0.3)',
                        border: '2px solid transparent',
                        transition: 'border-color 0.2s'
                      }}
                      onMouseEnter={(e) => {
                        if (href) e.currentTarget.style.borderColor = cycleColor;
                      }}
                      onMouseLeave={(e) => {
                        e.currentTarget.style.borderColor = 'transparent';
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