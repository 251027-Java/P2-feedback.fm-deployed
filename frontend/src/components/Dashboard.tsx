import { useEffect, useState } from 'react';
import { userAPI } from '../services/api';

function Dashboard() {
  const [dashboardData, setDashboardData] = useState<any>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [imageError, setImageError] = useState(false);
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
      <h1 style={{ marginBottom: '20px', color: '#1DB954' }}>Dashboard</h1>

      {dashboardData && (
        <div>
          {/* USER INFO (new layout + old fields) */}
          <h2 style={{ marginBottom: '20px', color: 'white' }}>User Info</h2>
          <div
            style={{
              display: 'flex',
              flexDirection: 'row',
              alignItems: 'flex-start',
              gap: '2rem',
              marginBottom: '40px'
            }}
          >
            <div
              style={{
                width: '120px',
                height: '120px',
                minWidth: '120px',
                borderRadius: '50%',
                overflow: 'hidden',
                backgroundColor: 'rgba(255, 255, 255, 0.1)',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
                boxShadow: '0 4px 8px rgba(0, 0, 0, 0.3)'
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
                <div style={{ color: 'rgba(255, 255, 255, 0.5)', fontSize: '3rem' }}>👤</div>
              )}
            </div>

            <div style={{ display: 'flex', flexDirection: 'column', flex: 1, gap: '1rem' }}>
              <div>
                <h3 style={{ margin: '0 0 8px 0', color: 'white', fontSize: '1.5rem' }}>
                  {dashboardData.username || 'N/A'}
                </h3>
                <p style={{ margin: 0, color: 'rgba(255, 255, 255, 0.7)', fontSize: '0.9rem' }}>
                  {dashboardData.email || 'N/A'}
                </p>
              </div>

              {dashboardData.stats && (
                <div style={{ display: 'flex', flexDirection: 'column', gap: '0.75rem' }}>
                  <div>
                    <strong style={{ color: 'rgba(255, 255, 255, 0.8)' }}>Total Listening Time:</strong>{' '}
                    <span style={{ color: 'white' }}>
                      {dashboardData.stats.totalListeningTime || '0 hours'}
                    </span>
                  </div>
                  <div>
                    <strong style={{ color: 'rgba(255, 255, 255, 0.8)' }}>Songs Played:</strong>{' '}
                    <span style={{ color: 'white' }}>{dashboardData.stats.songsPlayed || 0}</span>
                  </div>
                  <div>
                    <strong style={{ color: 'rgba(255, 255, 255, 0.8)' }}>Current Streak:</strong>{' '}
                    <span style={{ color: 'white' }}>{dashboardData.stats.currentStreak || 0} days</span>
                  </div>
                </div>
              )}
            </div>
          </div>
          <h2 style={{ marginBottom: '20px', color: 'white' }}>Top Artists</h2>
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
                        fontSize: '0.9rem',
                        fontWeight: '500',
                        margin: 0,
                        wordBreak: 'break-word'
                      }}
                    >
                      {getArtistName(artist)}
                    </p>
                  </div>
                );
              })}
            </div>
          ) : (
            <p>No top artists data available</p>
          )}
          <h2 style={{ marginBottom: '20px', color: 'white' }}>Top Songs</h2>
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
                        fontSize: '0.9rem',
                        fontWeight: '500',
                        margin: '0 0 4px 0',
                        wordBreak: 'break-word'
                      }}
                    >
                      {getSongName(song)}
                    </p>
                    <p
                      style={{
                        color: 'rgba(255, 255, 255, 0.7)',
                        fontSize: '0.75rem',
                        margin: 0,
                        wordBreak: 'break-word'
                      }}
                    >
                      {getSongArtistName(song)}
                    </p>
                  </div>
                );
              })}
            </div>
          ) : (
            <p>No top songs data available</p>
          )}
        </div>
      )}
    </div>
  );
}

export default Dashboard;
