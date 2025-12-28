import { useEffect, useState } from 'react';
import { playlistsAPI } from '../services/api';
import AnimatedContent from './AnimatedContent';

function Playlists() {
  const [playlists, setPlaylists] = useState<any[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [selectedPlaylist, setSelectedPlaylist] = useState<any>(null);
  const [showSongs, setShowSongs] = useState(false);

  useEffect(() => {
    const fetchPlaylists = async () => {
      try {
        setLoading(true);
        setError(null);
        const response = await playlistsAPI.getAll();
        setPlaylists(response.data || []);
      } catch (err: any) {
        console.error('Error fetching playlists:', err);
        setError(err.response?.data?.message || 'Failed to load playlists');
      } finally {
        setLoading(false);
      }
    };

    fetchPlaylists();
  }, []);

  const handlePlaylistClick = async (playlist: any) => {
    try {
      const response = await playlistsAPI.getSongs(playlist.playlistId);
      const playlistWithSongs = {
        ...playlist,
        songs: response.data,
      };
      setSelectedPlaylist(playlistWithSongs);
      setShowSongs(true);
    } catch (err: any) {
      console.error('Error fetching playlist songs:', err);
      setError('Failed to load playlist songs');
    }
  };

  const closeModal = () => {
    setShowSongs(false);
    setSelectedPlaylist(null);
  };

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
        Your Playlists
      </h1>

      {playlists.length === 0 ? (
        <div
          style={{
            textAlign: 'center',
            padding: '60px 20px',
            color: 'rgba(255, 255, 255, 0.5)',
          }}
        >
          <div style={{ fontSize: '3rem', marginBottom: '20px', opacity: 0.3 }}>
            🎵
          </div>
          <p style={{ fontSize: '1.2rem' }}>No playlists found</p>
          <p style={{ fontSize: '0.9rem', marginTop: '10px' }}>
            Create playlists in Spotify to see them here
          </p>
        </div>
      ) : (
        <AnimatedContent>
          <div
            style={{
              display: 'grid',
              gridTemplateColumns: 'repeat(auto-fill, minmax(280px, 1fr))',
              gap: '24px',
              paddingBottom: '40px',
            }}
          >
            {playlists.map((playlist: any, index: number) => (
              <button
                type="button"
                className="focus:outline-2 focus:outline-brand"
                key={playlist.playlistId || index}
                onClick={() => handlePlaylistClick(playlist)}
                style={{
                  backgroundColor: 'rgba(255, 255, 255, 0.03)',
                  border: '1px solid rgba(255, 255, 255, 0.1)',
                  borderRadius: '12px',
                  padding: '20px',
                  cursor: 'pointer',
                  transition: 'all 0.3s ease',
                  position: 'relative',
                  overflow: 'hidden',
                }}
                onMouseEnter={(e) => {
                  e.currentTarget.style.transform = 'translateY(-8px)';
                  e.currentTarget.style.backgroundColor =
                    'rgba(29, 185, 84, 0.1)';
                  e.currentTarget.style.border =
                    '1px solid rgba(29, 185, 84, 0.5)';
                  e.currentTarget.style.boxShadow =
                    '0 8px 24px rgba(29, 185, 84, 0.2)';
                }}
                onMouseLeave={(e) => {
                  e.currentTarget.style.transform = 'translateY(0)';
                  e.currentTarget.style.backgroundColor =
                    'rgba(255, 255, 255, 0.03)';
                  e.currentTarget.style.border =
                    '1px solid rgba(255, 255, 255, 0.1)';
                  e.currentTarget.style.boxShadow = 'none';
                }}
              >
                <div
                  className="items-start"
                  style={{
                    display: 'flex',
                    flexDirection: 'column',
                    gap: '12px',
                  }}
                >
                  {/* Playlist Icon */}
                  <div
                    style={{
                      width: '60px',
                      height: '60px',
                      borderRadius: '8px',
                      backgroundColor: 'rgba(29, 185, 84, 0.2)',
                      border: '1px solid rgba(29, 185, 84, 0.4)',
                      display: 'flex',
                      alignItems: 'center',
                      justifyContent: 'center',
                      fontSize: '2rem',
                      marginBottom: '8px',
                    }}
                  >
                    🎵
                  </div>

                  {/* Playlist Name */}
                  <h3
                    style={{
                      margin: 0,
                      fontSize: '1.2rem',
                      fontWeight: '700',
                      color: 'white',
                      overflow: 'hidden',
                      textOverflow: 'ellipsis',
                      display: '-webkit-box',
                      WebkitLineClamp: 2,
                      WebkitBoxOrient: 'vertical',
                      lineHeight: '1.3',
                    }}
                  >
                    {playlist.name || 'Untitled Playlist'}
                  </h3>

                  {/* Playlist Description */}
                  {playlist.description && (
                    <p
                      style={{
                        margin: 0,
                        fontSize: '0.85rem',
                        color: 'rgba(255, 255, 255, 0.6)',
                        overflow: 'hidden',
                        textOverflow: 'ellipsis',
                        display: '-webkit-box',
                        WebkitLineClamp: 2,
                        WebkitBoxOrient: 'vertical',
                        lineHeight: '1.4',
                      }}
                    >
                      {playlist.description}
                    </p>
                  )}

                  {/* Playlist Stats */}
                  <div
                    style={{
                      display: 'flex',
                      alignItems: 'center',
                      gap: '12px',
                      fontSize: '0.8rem',
                      color: 'rgba(255, 255, 255, 0.5)',
                      marginTop: '8px',
                    }}
                  >
                    {(playlist.trackCount !== undefined ||
                      (playlist.songs && playlist.songs.length > 0)) && (
                      <span>
                        {playlist.trackCount || playlist.songs.length} songs
                      </span>
                    )}
                    {playlist.isPublic !== undefined && (
                      <span
                        style={{
                          padding: '2px 8px',
                          borderRadius: '12px',
                          backgroundColor: playlist.isPublic
                            ? 'rgba(29, 185, 84, 0.2)'
                            : 'rgba(255, 255, 255, 0.1)',
                          color: playlist.isPublic
                            ? '#1DB954'
                            : 'rgba(255, 255, 255, 0.7)',
                          fontSize: '0.75rem',
                          fontWeight: '600',
                        }}
                      >
                        {playlist.isPublic ? 'Public' : 'Private'}
                      </span>
                    )}
                  </div>
                </div>
              </button>
            ))}
          </div>
        </AnimatedContent>
      )}

      {/* Modal for showing playlist songs */}
      {showSongs && selectedPlaylist && (
        // biome-ignore lint/a11y/noStaticElementInteractions: Modal logic to allow clicks outside of primary modal to close the modal
        // biome-ignore lint/a11y/useKeyWithClickEvents: The modal content has a close button that users can use
        <div
          onClick={closeModal}
          style={{
            position: 'fixed',
            top: 0,
            left: 0,
            right: 0,
            bottom: 0,
            backgroundColor: 'rgba(0, 0, 0, 0.9)',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            zIndex: 1000,
            padding: '20px',
          }}
        >
          {/** biome-ignore lint/a11y/noStaticElementInteractions: Users should be able to click on the modal content to copy text, etc  */}
          {/** biome-ignore lint/a11y/useKeyWithClickEvents: Key events are not needed */}
          <div
            onClick={(e) => e.stopPropagation()}
            style={{
              backgroundColor: '#121212',
              borderRadius: '16px',
              padding: '32px',
              maxWidth: '800px',
              width: '100%',
              maxHeight: '80vh',
              overflow: 'auto',
              border: '1px solid rgba(29, 185, 84, 0.3)',
              boxShadow: '0 8px 32px rgba(0, 0, 0, 0.5)',
            }}
          >
            {/* Modal Header */}
            <div
              style={{
                display: 'flex',
                justifyContent: 'space-between',
                alignItems: 'flex-start',
                marginBottom: '24px',
              }}
            >
              <div>
                <h2
                  style={{
                    margin: 0,
                    marginBottom: '8px',
                    color: '#1DB954',
                    fontSize: '2rem',
                    fontWeight: '700',
                  }}
                >
                  {selectedPlaylist.name}
                </h2>
                {selectedPlaylist.description && (
                  <p
                    style={{
                      margin: 0,
                      color: 'rgba(255, 255, 255, 0.6)',
                      fontSize: '0.9rem',
                    }}
                  >
                    {selectedPlaylist.description}
                  </p>
                )}
              </div>
              <button
                type="button"
                className="focus:outline-2 focus:outline-brand rounded-sm"
                onClick={closeModal}
                style={{
                  background: 'none',
                  border: 'none',
                  color: 'rgba(255, 255, 255, 0.7)',
                  fontSize: '1.5rem',
                  cursor: 'pointer',
                  padding: '8px',
                  transition: 'color 0.2s',
                }}
                onMouseEnter={(e) => {
                  e.currentTarget.style.color = '#1DB954';
                }}
                onMouseLeave={(e) => {
                  e.currentTarget.style.color = 'rgba(255, 255, 255, 0.7)';
                }}
              >
                ✕
              </button>
            </div>

            {/* Songs List */}
            <div style={{ marginTop: '24px' }}>
              {!selectedPlaylist.songs ||
              selectedPlaylist.songs.length === 0 ? (
                <div
                  style={{
                    textAlign: 'center',
                    padding: '40px 20px',
                    color: 'rgba(255, 255, 255, 0.5)',
                  }}
                >
                  <p>No songs in this playlist</p>
                </div>
              ) : (
                <div
                  style={{
                    display: 'flex',
                    flexDirection: 'column',
                    gap: '12px',
                  }}
                >
                  {selectedPlaylist.songs.map((song: any, index: number) => (
                    <div
                      className="border border-white/10 bg-white/3 hover:border-[rgba(29,185,84,0.3)] hover:bg-[rgba(29,185,84,0.1)]"
                      key={song.songId || index}
                      style={{
                        display: 'flex',
                        alignItems: 'center',
                        gap: '16px',
                        padding: '12px',
                        borderRadius: '8px',
                        transition: 'all 0.2s',
                      }}
                    >
                      {/* Track Number */}
                      <div
                        style={{
                          width: '32px',
                          textAlign: 'center',
                          color: 'rgba(255, 255, 255, 0.5)',
                          fontSize: '0.9rem',
                          fontWeight: '600',
                        }}
                      >
                        {index + 1}
                      </div>

                      {/* Song Info */}
                      <div style={{ flex: 1, minWidth: 0 }}>
                        <div
                          style={{
                            fontWeight: '600',
                            color: 'white',
                            fontSize: '0.95rem',
                            overflow: 'hidden',
                            textOverflow: 'ellipsis',
                            whiteSpace: 'nowrap',
                            marginBottom: '4px',
                          }}
                        >
                          {song.name || song.songName || 'Unknown Song'}
                        </div>
                        <div
                          style={{
                            fontSize: '0.85rem',
                            color: 'rgba(255, 255, 255, 0.6)',
                            overflow: 'hidden',
                            textOverflow: 'ellipsis',
                            whiteSpace: 'nowrap',
                          }}
                        >
                          {song.artistName ||
                            song.artist?.name ||
                            'Unknown Artist'}
                        </div>
                      </div>

                      {/* Duration */}
                      {(song.duration_ms || song.duration) && (
                        <div
                          style={{
                            color: 'rgba(255, 255, 255, 0.5)',
                            fontSize: '0.85rem',
                            fontWeight: '500',
                          }}
                        >
                          {(() => {
                            const ms = song.duration_ms || song.duration * 1000;
                            const minutes = Math.floor(ms / 60000);
                            const seconds = Math.floor((ms % 60000) / 1000);
                            return `${minutes}:${String(seconds).padStart(2, '0')}`;
                          })()}
                        </div>
                      )}
                    </div>
                  ))}
                </div>
              )}
            </div>
          </div>
        </div>
      )}
    </div>
  );
}

export default Playlists;
