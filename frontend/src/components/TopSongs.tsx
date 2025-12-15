import { useEffect, useState, type ChangeEvent } from 'react';
import { songsAPI } from '../services/api';
import AnimatedContent from './AnimatedContent';

function TopSongs() {
  const [songs, setSongs] = useState<any[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [timeRange, setTimeRange] = useState('short_term');
  const [filterText, setFilterText] = useState('');
  const [sortBy, setSortBy] = useState('default');

  const handleTimeRangeChange = (e: ChangeEvent<HTMLSelectElement>) => {
    setTimeRange(e.target.value);
  };

  const handleFilterChange = (e: ChangeEvent<HTMLInputElement>) => {
    setFilterText(e.target.value);
  };

  const handleSortChange = (e: ChangeEvent<HTMLSelectElement>) => {
    setSortBy(e.target.value);
  };

  useEffect(() => {
    const fetchTopSongs = async () => {
      try {
        setLoading(true);
        setError(null);
        const response = await songsAPI.getTopSongs(timeRange);
        setSongs(response.data || []);
      } catch (err: any) {
        console.error('Error fetching top songs:', err);
        setError(err.response?.data?.message || 'Failed to load top songs');
      } finally {
        setLoading(false);
      }
    };

    fetchTopSongs();
  }, [timeRange]);

  const filteredSongs = songs
    .filter((song: any) =>
      song.name?.toLowerCase().includes(filterText.toLowerCase()) ||
      song.artist?.toLowerCase().includes(filterText.toLowerCase()) ||
      song.artistName?.toLowerCase().includes(filterText.toLowerCase())
    )
    .sort((a: any, b: any) => {
      if (sortBy === 'name') {
        return (a.name || '').localeCompare(b.name || '');
      } else if (sortBy === 'artist') {
        const artistA = a.artistName || a.artist || '';
        const artistB = b.artistName || b.artist || '';
        return artistA.localeCompare(artistB);
      } else if (sortBy === 'popularity') {
        return (b.popularity || 0) - (a.popularity || 0);
      }
      return 0; // default order from API
    });

  if (loading) return <div style={{ padding: '20px', color: '#1DB954' }}>Loading...</div>;

  if (error) return <div style={{ padding: '20px', color: '#1DB954' }}>Error: {error}</div>;

  return (
    <div style={{ 
      padding: '0 20px 20px 20px', 
      color: 'white',
      minHeight: '100%',
      width: '100%',
      boxSizing: 'border-box',
      position: 'relative',
      zIndex: 50,
      backgroundColor: '#000'
    }}>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginTop: '40px', marginBottom: '20px' }}>
        <h1 style={{ 
          margin: 0,
          color: '#1DB954',
          fontSize: '3rem',
          fontWeight: '700',
          letterSpacing: '-0.5px'
        }}>
          Top Songs
        </h1>
        <div style={{ 
          color: 'rgba(255, 255, 255, 0.7)', 
          fontSize: '1rem',
          fontWeight: '500'
        }}>
          {filteredSongs.length} {filteredSongs.length === 1 ? 'song' : 'songs'}
        </div>
      </div>
      <div style={{
        display: 'flex',
        gap: '1rem',
        marginBottom: '2rem',
        alignItems: 'center',
        flexWrap: 'wrap',
        position: 'relative',
        zIndex: 100
      }}>
        <label style={{ color: '#1DB954', fontWeight: '500', position: 'relative', zIndex: 100, pointerEvents: 'auto' }}>
          Time Range:
          <select 
            value={timeRange} 
            onChange={handleTimeRangeChange}
            onFocus={(e) => e.stopPropagation()}
            onClick={(e) => {
              e.stopPropagation();
              e.currentTarget.focus();
            }}
            onMouseDown={(e) => e.stopPropagation()}
            style={{
              marginLeft: '0.5rem',
              padding: '6px 12px',
              backgroundColor: '#1DB954',
              color: '#000',
              fontWeight: 'bold',
              borderRadius: '6px',
              border: '1px solid #1DB954',
              fontSize: '14px',
              cursor: 'pointer',
              position: 'relative',
              zIndex: 101,
              pointerEvents: 'auto',
              WebkitAppearance: 'menulist',
              MozAppearance: 'menulist',
              appearance: 'menulist'
            }}
          >
            <option value="short_term" style={{ backgroundColor: '#1DB954', color: '#000' }}>Last 4 Weeks</option>
            <option value="medium_term" style={{ backgroundColor: '#1DB954', color: '#000' }}>Last 6 Months</option>
            <option value="long_term" style={{ backgroundColor: '#1DB954', color: '#000' }}>All Time</option>
          </select>
        </label>
        <label style={{ color: '#1DB954', fontWeight: '500', position: 'relative', zIndex: 100, pointerEvents: 'auto' }}>
          Sort By:
          <select 
            value={sortBy} 
            onChange={handleSortChange}
            onFocus={(e) => e.stopPropagation()}
            onClick={(e) => {
              e.stopPropagation();
              e.currentTarget.focus();
            }}
            onMouseDown={(e) => e.stopPropagation()}
            style={{
              marginLeft: '0.5rem',
              padding: '6px 12px',
              borderRadius: '6px',
              backgroundColor: '#1DB954',
              color: '#000',
              fontWeight: 'bold',
              border: '1px solid #1DB954',
              fontSize: '14px',
              cursor: 'pointer',
              position: 'relative',
              zIndex: 101,
              pointerEvents: 'auto',
              WebkitAppearance: 'menulist',
              MozAppearance: 'menulist',
              appearance: 'menulist'
            }}
          >
            <option value="default" style={{ backgroundColor: '#1DB954', color: '#000' }}>Default</option>
            <option value="name" style={{ backgroundColor: '#1DB954', color: '#000' }}>Song Name (A-Z)</option>
            <option value="artist" style={{ backgroundColor: '#1DB954', color: '#000' }}>Artist Name (A-Z)</option>
            <option value="popularity" style={{ backgroundColor: '#1DB954', color: '#000' }}>Popularity</option>
          </select>
        </label>
        <input
          type="text"
          value={filterText}
          onChange={handleFilterChange}
          onFocus={(e) => e.stopPropagation()}
          onClick={(e) => e.stopPropagation()}
          onMouseDown={(e) => e.stopPropagation()}
          placeholder="Search songs..."
          style={{
            padding: '6px 12px',
            borderRadius: '6px',
            backgroundColor: '#1a1a1a',
            border: '1px solid rgba(255, 255, 255, 0.3)',
            color: 'white',
            fontSize: '14px',
            minWidth: '200px',
            cursor: 'text',
            position: 'relative',
            zIndex: 100,
            pointerEvents: 'auto'
          }}
        />
      </div>
      {filteredSongs.length > 0 ? (
        <div 
          className="songs-grid"
          style={{
            display: 'grid',
            gridTemplateColumns: 'repeat(5, 1fr)',
            gap: '2rem',
            padding: '20px 0'
          }}
        >
          {filteredSongs.map((song: any, index: number) => (
            <AnimatedContent
              key={song.id || index}
              distance={100}
              direction="vertical"
              reverse={false}
              duration={0.6}
              ease="back.out(1.7)"
              initialOpacity={0}
              animateOpacity={true}
              scale={0.8}
              delay={index * 0.1}
            >
              <div 
                style={{
                  display: 'flex',
                  flexDirection: 'column',
                  alignItems: 'center',
                  textAlign: 'center',
                  cursor: 'pointer',
                  transition: 'transform 0.2s ease',
                  position: 'relative'
                }}
                onClick={() => {
                  if (song.href || song.external_urls?.spotify) {
                    window.open(song.href || song.external_urls.spotify, '_blank');
                  }
                }}
                onMouseEnter={(e) => {
                  e.currentTarget.style.transform = 'translateY(-8px)';
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
                  boxShadow: '0 4px 12px rgba(0, 0, 0, 0.4)',
                  border: '2px solid transparent',
                  transition: 'border-color 0.2s ease'
                }}
                onMouseEnter={(e) => {
                  e.currentTarget.style.borderColor = '#1DB954';
                }}
                onMouseLeave={(e) => {
                  e.currentTarget.style.borderColor = 'transparent';
                }}
              >
                {song.image ? (
                  <img
                    src={song.image}
                    alt={song.name || 'Song'}
                    style={{
                      width: '100%',
                      height: '100%',
                      objectFit: 'cover'
                    }}
                  />
                ) : (
                  <div style={{
                    color: 'rgba(255, 255, 255, 0.5)',
                    fontSize: '3rem'
                  }}>
                    🎵
                  </div>
                )}
              </div>
              <p style={{
                color: 'white',
                fontSize: '0.95rem',
                fontWeight: '600',
                margin: '0 0 4px 0',
                wordBreak: 'break-word'
              }}>
                {song.name || 'Unknown Song'}
              </p>
              <p style={{
                color: 'rgba(255, 255, 255, 0.6)',
                fontSize: '0.8rem',
                margin: '0 0 6px 0',
                wordBreak: 'break-word'
              }}>
                {song.artistName || song.artist || 'Unknown Artist'}
              </p>
              {song.album?.name && (
                <p style={{
                  color: 'rgba(255, 255, 255, 0.5)',
                  fontSize: '0.7rem',
                  margin: '0 0 4px 0',
                  wordBreak: 'break-word',
                  fontStyle: 'italic'
                }}>
                  {song.album.name}
                </p>
              )}
              {song.duration_ms && (
                <p style={{
                  color: '#1DB954',
                  fontSize: '0.7rem',
                  fontWeight: '500',
                  margin: 0
                }}>
                  {Math.floor(song.duration_ms / 60000)}:{String(Math.floor((song.duration_ms % 60000) / 1000)).padStart(2, '0')}
                </p>
              )}
              </div>
            </AnimatedContent>
          ))}
        </div>
      ) : (
        <p>No songs found</p>
      )}
    </div>
  );
}

export default TopSongs;
