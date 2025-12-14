import { useEffect, useState, type ChangeEvent } from 'react';
import { artistsAPI } from '../services/api';
import AnimatedContent from './AnimatedContent';

function TopArtists() {
  const [artists, setArtists] = useState<any[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [timeRange, setTimeRange] = useState('medium_term');
  const [searchTerm, setSearchTerm] = useState('');

  const handleSearchChange = (e: ChangeEvent<HTMLInputElement>) => {
    setSearchTerm(e.target.value);
  };

  const handleTimeRangeChange = (e: ChangeEvent<HTMLSelectElement>) => {
    setTimeRange(e.target.value);
  };

  useEffect(() => {
    const fetchTopArtists = async () => {
      try {
        setLoading(true);
        setError(null);
        const response = await artistsAPI.getTopArtists(timeRange);
        setArtists(response.data || []);
      } catch (err: any) {
        console.error('Error fetching top artists:', err);
        setError(err.response?.data?.message || 'Failed to load top artists');
      } finally {
        setLoading(false);
      }
    };

    fetchTopArtists();
  }, [timeRange]);

  const filteredArtists = artists.filter((artist: any) =>
    artist.name?.toLowerCase().includes(searchTerm.toLowerCase())
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
      <h1 style={{ marginBottom: '20px', color: '#1DB954' }}>Top Artists</h1>
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
            <option value="short_term" style={{ backgroundColor: '#1DB954', color: '#000' }}>Last 4 weeks</option>
            <option value="medium_term" style={{ backgroundColor: '#1DB954', color: '#000' }}>Last 6 months</option>
            <option value="long_term" style={{ backgroundColor: '#1DB954', color: '#000' }}>All time</option>
          </select>
        </label>
        <input
          type="text"
          value={searchTerm}
          onChange={handleSearchChange}
          onFocus={(e) => e.stopPropagation()}
          onClick={(e) => e.stopPropagation()}
          onMouseDown={(e) => e.stopPropagation()}
          placeholder="Search artists..."
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
      {filteredArtists.length > 0 ? (
        <div 
          className="artists-grid"
          style={{
            display: 'grid',
            gridTemplateColumns: 'repeat(5, 1fr)',
            gap: '2rem',
            padding: '20px 0'
          }}
        >
          {filteredArtists.map((artist: any, index: number) => (
            <AnimatedContent
              key={artist.id || index}
              distance={100}
              direction="vertical"
              reverse={false}
              duration={0.6}
              ease="back.out(1.7)"
              initialOpacity={0}
              animateOpacity={true}
              scale={0.8}
              threshold={0.4}
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
                    borderRadius: '50%',
                    overflow: 'hidden',
                    marginBottom: '12px',
                    backgroundColor: 'rgba(255, 255, 255, 0.1)',
                    display: 'flex',
                    alignItems: 'center',
                    justifyContent: 'center'
                  }}
                >
                  {artist.image ? (
                    <img
                      src={artist.image}
                      alt={artist.name || 'Artist'}
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
                  margin: 0,
                  wordBreak: 'break-word'
                }}>
                  {artist.name || 'Unknown Artist'}
                </p>
              </div>
            </AnimatedContent>
          ))}
        </div>
      ) : (
        <p>No artists found</p>
      )}
    </div>
  );
}

export default TopArtists;
