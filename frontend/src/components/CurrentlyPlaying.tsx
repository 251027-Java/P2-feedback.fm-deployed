import { useEffect, useState } from 'react';
import { songsAPI } from '../services/api';

function CurrentlyPlaying() {
  const [currentTrack, setCurrentTrack] = useState<any>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const fetchCurrentlyPlaying = async () => {
      try {
        setLoading(true);
        setError(null);
        const response = await songsAPI.getCurrentlyPlaying();
        setCurrentTrack(response.data);
      } catch (err: any) {
        console.error('Error fetching currently playing:', err);
        setError(err.response?.data?.message || 'Failed to load currently playing track');
      } finally {
        setLoading(false);
      }
    };

    fetchCurrentlyPlaying();

    // Poll every 5 seconds for updates
    const intervalId = setInterval(fetchCurrentlyPlaying, 5000);
    return () => clearInterval(intervalId);
  }, []);

  if (loading) return <div style={{ padding: '20px', color: '#1DB954' }}>Loading...</div>;

  if (error) return <div style={{ padding: '20px', color: '#1DB954' }}>Error: {error}</div>;

  const pageStyle = { 
    padding: '0 20px 20px 20px', 
    color: 'white',
    minHeight: '100%',
    backgroundColor: '#000'
  };

  if (!currentTrack) {
    return (
      <div style={pageStyle}>
        <h1 style={{ marginBottom: '20px', color: '#1DB954' }}>Currently Playing</h1>
        <p style={{ color: 'rgba(255, 255, 255, 0.7)' }}>No track is currently playing</p>
      </div>
    );
  }

  return (
    <div style={pageStyle}>
      <h1 style={{ marginBottom: '20px', color: '#1DB954' }}>Currently Playing</h1>
      <div style={{ 
        backgroundColor: 'rgba(29, 185, 84, 0.1)', 
        padding: '30px', 
        borderRadius: '15px',
        border: '1px solid rgba(29, 185, 84, 0.3)'
      }}>
        <h2 style={{ color: '#1DB954', marginBottom: '15px' }}>{currentTrack.name || 'Unknown Track'}</h2>
        <p style={{ fontSize: '1.1em', marginBottom: '10px' }}><strong style={{ color: '#1DB954' }}>Artist:</strong> {currentTrack.artist || 'Unknown Artist'}</p>
        <p style={{ fontSize: '1.1em', marginBottom: '10px' }}><strong style={{ color: '#1DB954' }}>Album:</strong> {currentTrack.album || 'Unknown Album'}</p>
        {currentTrack.isPlaying !== undefined && (
          <p style={{ fontSize: '1.1em' }}><strong style={{ color: '#1DB954' }}>Status:</strong> {currentTrack.isPlaying ? '▶️ Playing' : '⏸️ Paused'}</p>
        )}
      </div>
    </div>
  );
}

export default CurrentlyPlaying;
