import { useEffect, useState, type ChangeEvent } from 'react';
import { songsAPI } from '../services/api';
import AnimatedContent from './AnimatedContent';

function TopSongs() {
  const [songs, setSongs] = useState<any[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [timeRange, setTimeRange] = useState('medium_term');
  const [filterText, setFilterText] = useState('');

  const handleTimeRangeChange = (e: ChangeEvent<HTMLSelectElement>) => {
    setTimeRange(e.target.value);
  };

  const handleFilterChange = (e: ChangeEvent<HTMLInputElement>) => {
    setFilterText(e.target.value);
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

  const filteredSongs = songs.filter((song: any) =>
    song.name?.toLowerCase().includes(filterText.toLowerCase()) ||
    song.artist?.toLowerCase().includes(filterText.toLowerCase())
  );

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
      <h1 style={{ marginBottom: '20px', color: '#1DB954' }}>Top Songs</h1>
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
            <option value="short_term" style={{ backgroundColor: '#1DB954', color: '#000' }}>Last 4 weeks</option>
            <option value="medium_term" style={{ backgroundColor: '#1DB954', color: '#000' }}>Last 6 months</option>
            <option value="long_term" style={{ backgroundColor: '#1a1a1a', color: 'white' }}>All time</option>
          </select>
        </label>
        <input
          type="text"
          value={filterText}
          onChange={handleFilterChange}
          onFocus={(e) => e.stopPropagation()}
          onClick={(e) => e.stopPropagation()}
          onMouseDown={(e) => e.stopPropagation()}
          placeholder="Filter songs..."
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
                  textAlign: 'center'
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
                  boxShadow: '0 4px 8px rgba(0, 0, 0, 0.3)'
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
                fontSize: '0.9rem',
                fontWeight: '500',
                margin: '0 0 4px 0',
                wordBreak: 'break-word'
              }}>
                {song.name || 'Unknown Song'}
              </p>
              <p style={{
                color: 'rgba(255, 255, 255, 0.7)',
                fontSize: '0.75rem',
                margin: 0,
                wordBreak: 'break-word'
              }}>
                {song.artistName || song.artist || 'Unknown Artist'}
              </p>
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
