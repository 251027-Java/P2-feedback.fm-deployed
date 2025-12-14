import { useEffect, useState } from 'react';
import { historyAPI } from '../services/api';
import AnimatedContent from './AnimatedContent';

function ListeningHistory() {
  const [history, setHistory] = useState<any[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const fetchHistory = async () => {
      try {
        setLoading(true);
        setError(null);
        const response = await historyAPI.getHistory(50);
        const historyData = response.data || [];
        setHistory(historyData);
      } catch (err: any) {
        console.error('Error fetching listening history:', err);
        setError(err.response?.data?.message || 'Failed to load listening history');
      } finally {
        setLoading(false);
      }
    };

    fetchHistory();
  }, []);

  if (loading) return <div style={{ padding: '20px', color: 'white' }}>Loading...</div>;

  if (error) return <div style={{ padding: '20px', color: '#ff6b6b' }}>Error: {error}</div>;

  return (
    <div style={{ 
      padding: '0 20px 20px 20px', 
      color: 'white',
      minHeight: '100%',
      width: '100%',
      boxSizing: 'border-box',
      position: 'relative',
      zIndex: 50
    }}>
      <h1 style={{ marginBottom: '20px', color: 'white' }}>Listening History</h1>
      {history.length > 0 ? (
        <div
          style={{
            display: 'flex',
            flexDirection: 'column',
            gap: '1rem',
            padding: '20px 0'
          }}
        >
          {history.map((item: any, index: number) => {
            // Format date: month, date, hours, minutes (no seconds)
            let formattedDate = '';
            if (item.playedAt) {
              try {
                const date = new Date(item.playedAt);
                const month = date.toLocaleString('default', { month: 'short' });
                const day = date.getDate();
                const hours = date.getHours().toString().padStart(2, '0');
                const minutes = date.getMinutes().toString().padStart(2, '0');
                formattedDate = `${month} ${day}, ${hours}:${minutes}`;
              } catch {
                formattedDate = item.playedAt;
              }
            }
            
            return (
              <AnimatedContent
                key={item.id || index}
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
                    flexDirection: 'row',
                    alignItems: 'center',
                    gap: '1rem',
                    padding: '12px',
                    backgroundColor: 'rgba(255, 255, 255, 0.05)',
                    borderRadius: '8px',
                    transition: 'background-color 0.2s'
                  }}
                  onMouseEnter={(e) => {
                    e.currentTarget.style.backgroundColor = 'rgba(255, 255, 255, 0.1)';
                  }}
                  onMouseLeave={(e) => {
                    e.currentTarget.style.backgroundColor = 'rgba(255, 255, 255, 0.05)';
                  }}
                >
                <div
                  style={{
                    width: '64px',
                    height: '64px',
                    minWidth: '64px',
                    borderRadius: '8px',
                    overflow: 'hidden',
                    backgroundColor: 'rgba(255, 255, 255, 0.1)',
                    display: 'flex',
                    alignItems: 'center',
                    justifyContent: 'center',
                    boxShadow: '0 2px 4px rgba(0, 0, 0, 0.3)'
                  }}
                >
                  {item.image ? (
                    <img
                      src={item.image}
                      alt={item.songName || 'Song'}
                      style={{
                        width: '100%',
                        height: '100%',
                        objectFit: 'cover'
                      }}
                    />
                  ) : (
                    <div style={{ color: 'rgba(255, 255, 255, 0.5)', fontSize: '1.5rem' }}>
                      🎵
                    </div>
                  )}
                </div>
                <div
                  style={{
                    display: 'flex',
                    flexDirection: 'column',
                    flex: 1,
                    minWidth: 0
                  }}
                >
                  <p
                    style={{
                      color: 'white',
                      fontSize: '1rem',
                      fontWeight: '500',
                      margin: '0 0 4px 0',
                      wordBreak: 'break-word'
                    }}
                  >
                    {item.songName || 'Unknown Song'}
                  </p>
                  {item.artistName && (
                    <p
                      style={{
                        color: 'rgba(255, 255, 255, 0.6)',
                        fontSize: '0.85rem',
                        margin: '0 0 4px 0',
                        wordBreak: 'break-word'
                      }}
                    >
                      {item.artistName}
                    </p>
                  )}
                  <p
                    style={{
                      color: 'rgba(255, 255, 255, 0.5)',
                      fontSize: '0.75rem',
                      margin: 0
                    }}
                  >
                    {formattedDate}
                  </p>
                </div>
              </div>
              </AnimatedContent>
            );
          })}
        </div>
      ) : (
        <p>No listening history found</p>
      )}
    </div>
  );
}

export default ListeningHistory;
