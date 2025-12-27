import { useEffect, useState } from 'react';
import { historyAPI } from '../services/api';
import AnimatedContent from './AnimatedContent';

function ListeningHistory() {
  const [history, setHistory] = useState<any[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [limit, setLimit] = useState<number>(50);

  useEffect(() => {
    const fetchHistory = async () => {
      try {
        setLoading(true);
        setError(null);
        const response = await historyAPI.getHistory(limit);
        const historyData = response.data || [];
        setHistory(historyData);
      } catch (err: any) {
        console.error('Error fetching listening history:', err);
        setError(
          err.response?.data?.message || 'Failed to load listening history',
        );
      } finally {
        setLoading(false);
      }
    };

    fetchHistory();
  }, [limit]);

  if (loading)
    return <div style={{ padding: '20px', color: '#1DB954' }}>Loading...</div>;
  if (error)
    return (
      <div style={{ padding: '20px', color: '#1DB954' }}>Error: {error}</div>
    );

  return (
    <div
      style={{
        padding: '0 20px 20px 20px',
        color: 'white',
        minHeight: '100%',
        width: '100%',
        boxSizing: 'border-box',
        backgroundColor: '#000',
        position: 'relative',
        zIndex: 50,
      }}
    >
      <h1
        style={{
          marginTop: '40px',
          marginBottom: '40px',
          color: '#1DB954',
          fontSize: '3rem',
          fontWeight: '700',
          letterSpacing: '-0.5px',
        }}
      >
        Listening History
      </h1>
      <div
        style={{
          marginBottom: '20px',
          display: 'flex',
          alignItems: 'center',
          gap: '10px',
        }}
      >
        <label
          htmlFor="track-count-select"
          style={{ color: '#1DB954', fontWeight: 500 }}
        >
          Show last:
        </label>
        <select
          id="track-count-select"
          value={limit}
          onChange={(e) => setLimit(Number.parseInt(e.target.value, 10))}
          style={{
            padding: '8px 16px',
            borderRadius: '20px',
            backgroundColor: '#1DB954',
            color: '#000',
            fontWeight: 'bold',
            border: 'none',
            cursor: 'pointer',
            fontSize: '14px',
            transition: 'all 0.2s',
            outline: 'none',
          }}
          onMouseEnter={(e) => {
            e.currentTarget.style.backgroundColor = '#1ed760';
            e.currentTarget.style.transform = 'scale(1.05)';
          }}
          onMouseLeave={(e) => {
            e.currentTarget.style.backgroundColor = '#1DB954';
            e.currentTarget.style.transform = 'scale(1)';
          }}
        >
          <option
            value={10}
            style={{ backgroundColor: '#181818', color: '#fff' }}
          >
            10 tracks
          </option>
          <option
            value={20}
            style={{ backgroundColor: '#181818', color: '#fff' }}
          >
            20 tracks
          </option>
          <option
            value={30}
            style={{ backgroundColor: '#181818', color: '#fff' }}
          >
            30 tracks
          </option>
          <option
            value={50}
            style={{ backgroundColor: '#181818', color: '#fff' }}
          >
            50 tracks
          </option>
          <option
            value={75}
            style={{ backgroundColor: '#181818', color: '#fff' }}
          >
            75 tracks
          </option>
          <option
            value={100}
            style={{ backgroundColor: '#181818', color: '#fff' }}
          >
            100 tracks
          </option>
        </select>
      </div>

      {history.length > 0 ? (
        <div
          style={{
            display: 'flex',
            flexDirection: 'column',
            gap: '1rem',
            padding: '20px 0',
          }}
        >
          {history.map((item: any, index: number) => {
            // Format date similar to main (short + no seconds), with fallback like Sidney2
            let formattedDate = '';
            if (item.playedAt) {
              try {
                const date = new Date(item.playedAt);
                const month = date.toLocaleString('default', {
                  month: 'short',
                });
                const day = date.getDate();
                const hours = date.getHours().toString().padStart(2, '0');
                const minutes = date.getMinutes().toString().padStart(2, '0');
                formattedDate = `${month} ${day}, ${hours}:${minutes}`;
              } catch {
                formattedDate = item.playedAt;
              }
            }

            const songName =
              item.songName || item.song?.name || item.name || 'Unknown Song';
            const artistName =
              item.artistName ||
              item.artist?.name ||
              item.song?.artistName ||
              item.song?.artist?.name ||
              'Unknown Artist';
            const image =
              item.image ||
              item.song?.image ||
              item.albumImage ||
              item.song?.albumImage ||
              item.song?.album?.images?.[0]?.url ||
              null;
            const href =
              item.href ||
              item.song?.href ||
              item.external_urls?.spotify ||
              null;

            return (
              <AnimatedContent
                key={item.id || item.historyId || index}
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
                <button
                  type="button"
                  className="w-full focus:outline-2 focus:outline-brand"
                  style={{
                    display: 'flex',
                    flexDirection: 'row',
                    alignItems: 'center',
                    gap: '1rem',
                    padding: '12px',
                    backgroundColor: 'rgba(255, 255, 255, 0.05)',
                    borderRadius: '8px',
                    transition: 'all 0.2s',
                    cursor: href ? 'pointer' : 'default',
                  }}
                  onClick={() => {
                    if (href) {
                      window.open(href, '_blank');
                    }
                  }}
                  onMouseEnter={(e) => {
                    e.currentTarget.style.backgroundColor =
                      'rgba(255, 255, 255, 0.1)';
                    if (href) {
                      e.currentTarget.style.transform = 'translateX(4px)';
                    }
                  }}
                  onMouseLeave={(e) => {
                    e.currentTarget.style.backgroundColor =
                      'rgba(255, 255, 255, 0.05)';
                    e.currentTarget.style.transform = 'translateX(0)';
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
                      boxShadow: '0 2px 4px rgba(0, 0, 0, 0.3)',
                    }}
                  >
                    {image ? (
                      <img
                        src={image}
                        alt={songName}
                        style={{
                          width: '100%',
                          height: '100%',
                          objectFit: 'cover',
                        }}
                      />
                    ) : (
                      <div
                        style={{
                          color: 'rgba(255, 255, 255, 0.5)',
                          fontSize: '1.5rem',
                        }}
                      >
                        🎵
                      </div>
                    )}
                  </div>

                  <div
                    className="items-start"
                    style={{
                      display: 'flex',
                      flexDirection: 'column',
                      flex: 1,
                      minWidth: 0,
                    }}
                  >
                    <p
                      style={{
                        color: 'white',
                        fontSize: '1rem',
                        fontWeight: 500,
                        margin: '0 0 4px 0',
                        wordBreak: 'break-word',
                      }}
                    >
                      {songName}
                    </p>

                    <p
                      style={{
                        color: 'rgba(255, 255, 255, 0.6)',
                        fontSize: '0.85rem',
                        margin: '0 0 4px 0',
                        wordBreak: 'break-word',
                      }}
                    >
                      {artistName}
                    </p>

                    <p
                      style={{
                        color: 'rgba(255, 255, 255, 0.5)',
                        fontSize: '0.75rem',
                        margin: 0,
                      }}
                    >
                      {formattedDate}
                    </p>
                  </div>

                  {href && (
                    <div
                      className="opacity-70 hover:opacity-100"
                      style={{
                        color: '#1DB954',
                        fontSize: '1.2rem',
                        display: 'flex',
                        alignItems: 'center',
                        transition: 'opacity 0.2s',
                      }}
                    >
                      ▶
                    </div>
                  )}
                </button>
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
