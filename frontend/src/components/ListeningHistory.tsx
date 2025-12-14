import { useEffect, useState } from 'react';
import { historyAPI } from '../services/api';

function ListeningHistory() {
  const [history, setHistory] = useState<any[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [limit, setLimit] = useState(50);

  useEffect(() => {
    const fetchHistory = async () => {
      try {
        setLoading(true);
        setError(null);
        const response = await historyAPI.getHistory(limit);
        setHistory(response.data || []);
      } catch (err: any) {
        console.error('Error fetching listening history:', err);
        setError(err.response?.data?.message || 'Failed to load listening history');
      } finally {
        setLoading(false);
      }
    };

    fetchHistory();
  }, [limit]);

  if (loading) return <div style={{ padding: '20px', color: '#1DB954' }}>Loading...</div>;

  if (error) return <div style={{ padding: '20px', color: '#1DB954' }}>Error: {error}</div>;

  return (
    <div style={{ 
      padding: '0 20px 20px 20px', 
      color: 'white',
      minHeight: '100%',
      backgroundColor: '#000'
    }}>
      <h1 style={{ marginBottom: '20px', color: '#1DB954' }}>Listening History</h1>
      <div style={{ marginBottom: '20px' }}>
        <label style={{ color: '#1DB954', fontWeight: '500' }}>
          Limit:
          <input
            type="number"
            value={limit}
            onChange={(e) => setLimit(parseInt(e.target.value, 10) || 50)}
            min="1"
            max="100"
            style={{
              marginLeft: '10px',
              padding: '6px 12px',
              borderRadius: '6px',
              backgroundColor: '#1DB954',
              color: '#000',
              fontWeight: 'bold',
              border: 'none'
            }}
          />
        </label>
      </div>
      {history.length > 0 ? (
        <ul style={{ 
          backgroundColor: 'rgba(29, 185, 84, 0.1)', 
          padding: '20px', 
          borderRadius: '10px',
          border: '1px solid rgba(29, 185, 84, 0.3)',
          listStyle: 'none'
        }}>
          {history.map((item: any, index: number) => (
            <li key={item.id || index} style={{ 
              marginBottom: '12px',
              paddingBottom: '12px',
              borderBottom: index < history.length - 1 ? '1px solid rgba(29, 185, 84, 0.2)' : 'none'
            }}>
              <span style={{ color: '#1DB954', fontWeight: 'bold' }}>{item.songName || 'Unknown Song'}</span>
              <span style={{ color: 'rgba(255, 255, 255, 0.7)' }}> - {item.artistName || 'Unknown Artist'}</span>
              {item.playedAt && (
                <span style={{ color: 'rgba(255, 255, 255, 0.5)', fontSize: '0.9em' }}> ({(() => {
                  try {
                    return new Date(item.playedAt).toLocaleString();
                  } catch {
                    return item.playedAt;
                  }
                })()})</span>
              )}
            </li>
          ))}
        </ul>
      ) : (
        <p>No listening history found</p>
      )}
    </div>
  );
}

export default ListeningHistory;
