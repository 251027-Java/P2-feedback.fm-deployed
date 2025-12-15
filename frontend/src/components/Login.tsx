import { useState, useEffect, type FormEvent } from 'react';
import { useNavigate } from 'react-router-dom';
import { authAPI, setAccessToken } from '../services/api';
import LiquidEther from './LiquidEther';
import logo from '../../assets/logo.png';

function Login() {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const navigate = useNavigate();

  const handleCallback = async (code: string) => {
    try {
      setLoading(true);
      const response = await authAPI.handleCallback(code);
      if (response.data.token) {
        setAccessToken(response.data.token);
        if (response.data.listenerId) {
          localStorage.setItem('userId', response.data.listenerId);
        }
        navigate('/dashboard');
      }
    } catch (err: any) {
      console.error('Error handling callback:', err);
      setError(err.response?.data?.message || 'Failed to complete login');
      setLoading(false);
    }
  };

  // Handle OAuth callback if token is in URL (from backend redirect)
  useEffect(() => {
    const urlParams = new URLSearchParams(window.location.search);
    const token = urlParams.get('token');
    const listenerId = urlParams.get('listenerId');
    const spotifyToken = urlParams.get('spotifyToken');
    const error = urlParams.get('error');
    
    if (error) {
      setError(error);
      setLoading(false);
      // Clean up URL
      window.history.replaceState({}, document.title, '/');
      return;
    }
    
    if (token) {
      // Token is already provided by backend redirect
      setLoading(true);
      setAccessToken(token);
      if (listenerId) {
        localStorage.setItem('userId', listenerId);
      }
      // Store Spotify access token for API calls
      if (spotifyToken) {
        localStorage.setItem('spotifyAccessToken', spotifyToken);
      }
      // Clean up URL and navigate
      window.history.replaceState({}, document.title, '/');
      navigate('/dashboard');
      return;
    }
    
    // Fallback: if code is present but no token, call the API (legacy support)
    const code = urlParams.get('code');
    if (code) {
      handleCallback(code);
    }
  }, [navigate]);

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault();
    setLoading(true);
    setError(null);

    try {
      const response = await authAPI.getAuthUrl();
      const authUrl = response.data.authUrl;
      if (authUrl) {
        window.location.href = authUrl;
      }
    } catch (err: any) {
      console.error('Error getting auth URL:', err);
      setError(err.response?.data?.message || 'Failed to initiate login');
      setLoading(false);
    }
  };

  return (
    <div style={{
      position: 'fixed',
      top: 0,
      left: 0,
      width: '100%',
      height: '100%',
      overflow: 'hidden',
      display: 'flex',
      alignItems: 'center',
      justifyContent: 'center',
      background: '#0a0a0f'
    }}>
      {/* Liquid Ether Background */}
      <LiquidEther 
        colors={['#1DB954', '#1ed760', '#18a344']}
        style={{
          position: 'absolute',
          top: 0,
          left: 0,
          width: '100%',
          height: '100%',
          zIndex: 0
        }}
      />

      {/* Login Card */}
      <div style={{
        position: 'relative',
        zIndex: 10,
        backgroundColor: 'rgba(0, 0, 0, 0.6)',
        backdropFilter: 'blur(20px)',
        border: '2px solid rgba(29, 185, 84, 0.3)',
        borderRadius: '24px',
        padding: '3rem 4rem',
        boxShadow: '0 20px 60px rgba(0, 0, 0, 0.5), 0 0 40px rgba(29, 185, 84, 0.15)',
        minWidth: '450px',
        maxWidth: '500px',
        textAlign: 'center',
        display: 'flex',
        flexDirection: 'column',
        alignItems: 'center',
        animation: 'fadeInUp 0.6s ease-out'
      }}>
        <style>
          {`
            @keyframes fadeInUp {
              from {
                opacity: 0;
                transform: translateY(30px);
              }
              to {
                opacity: 1;
                transform: translateY(0);
              }
            }
          `}
        </style>
        <img 
          src={logo} 
          alt="Feedback.fm Logo" 
          style={{ 
            width: '300px', 
            height: 'auto',
            marginBottom: '1.5rem',
            filter: 'drop-shadow(0 4px 20px rgba(29, 185, 84, 0.5))',
            display: 'block',
            borderRadius: '20px'
          }} 
        />
        <h1 style={{
          fontSize: '2.8rem',
          fontWeight: '700',
          background: 'linear-gradient(135deg, #1DB954 0%, #1ed760 100%)',
          WebkitBackgroundClip: 'text',
          WebkitTextFillColor: 'transparent',
          backgroundClip: 'text',
          marginBottom: '0.5rem',
          marginTop: '0',
          letterSpacing: '-0.5px'
        }}>
          
        </h1>
        <p style={{
          color: 'rgba(255, 255, 255, 0.8)',
          marginBottom: '2rem',
          fontSize: '1.15rem',
          fontWeight: '400'
        }}>
          Your Spotify Wrapped, Anytime
        </p>
        
        {/* Feature highlights */}
        <div style={{
          display: 'flex',
          gap: '2.5rem',
          marginBottom: '2.5rem',
          justifyContent: 'center',
          flexWrap: 'wrap'
        }}>
          <div style={{ textAlign: 'center' }}>
            <div style={{ 
              marginBottom: '0.5rem',
              display: 'flex',
              justifyContent: 'center'
            }}>
              <svg width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="#1DB954" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                <path d="M9 18V5l12-2v13" />
                <circle cx="6" cy="18" r="3" />
                <circle cx="18" cy="16" r="3" />
              </svg>
            </div>
            <div style={{ color: 'rgba(255, 255, 255, 0.9)', fontSize: '0.9rem', fontWeight: '500' }}>Top Tracks</div>
          </div>
          <div style={{ textAlign: 'center' }}>
            <div style={{ 
              marginBottom: '0.5rem',
              display: 'flex',
              justifyContent: 'center'
            }}>
              <svg width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="#1DB954" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                <path d="M12 2a3 3 0 0 0-3 3v7a3 3 0 0 0 6 0V5a3 3 0 0 0-3-3Z" />
                <path d="M19 10v2a7 7 0 0 1-14 0v-2" />
                <line x1="12" y1="19" x2="12" y2="22" />
              </svg>
            </div>
            <div style={{ color: 'rgba(255, 255, 255, 0.9)', fontSize: '0.9rem', fontWeight: '500' }}>Top Artists</div>
          </div>
          <div style={{ textAlign: 'center' }}>
            <div style={{ 
              marginBottom: '0.5rem',
              display: 'flex',
              justifyContent: 'center'
            }}>
              <svg width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="#1DB954" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                <line x1="18" y1="20" x2="18" y2="10" />
                <line x1="12" y1="20" x2="12" y2="4" />
                <line x1="6" y1="20" x2="6" y2="14" />
              </svg>
            </div>
            <div style={{ color: 'rgba(255, 255, 255, 0.9)', fontSize: '0.9rem', fontWeight: '500' }}>Analytics</div>
          </div>
        </div>

        {error && (
          <div style={{
            color: '#ff6b6b',
            backgroundColor: 'rgba(255, 107, 107, 0.15)',
            border: '1px solid rgba(255, 107, 107, 0.4)',
            borderRadius: '12px',
            padding: '1rem',
            marginBottom: '1.5rem',
            fontSize: '0.9rem',
            width: '100%',
            boxSizing: 'border-box'
          }}>
            {error}
          </div>
        )}

        <form onSubmit={handleSubmit} style={{ display: 'flex', flexDirection: 'column', gap: '1rem', width: '100%' }}>
          <button
            type="submit"
            disabled={loading}
            style={{
              padding: '1.2rem 2.5rem',
              borderRadius: '50px',
              border: 'none',
              background: loading
                ? 'linear-gradient(135deg, #159943 0%, #18a344 100%)'
                : 'linear-gradient(135deg, #1DB954 0%, #1ed760 100%)',
              color: '#000',
              fontSize: '1.1rem',
              fontWeight: '700',
              cursor: loading ? 'not-allowed' : 'pointer',
              opacity: loading ? 0.7 : 1,
              transition: 'all 0.3s cubic-bezier(0.4, 0, 0.2, 1)',
              boxShadow: '0 8px 24px rgba(29, 185, 84, 0.4)',
              letterSpacing: '0.5px',
              textTransform: 'uppercase'
            }}
            onMouseEnter={(e) => {
              if (!loading) {
                e.currentTarget.style.transform = 'translateY(-3px) scale(1.02)';
                e.currentTarget.style.boxShadow = '0 12px 32px rgba(29, 185, 84, 0.6)';
              }
            }}
            onMouseLeave={(e) => {
              if (!loading) {
                e.currentTarget.style.transform = 'translateY(0) scale(1)';
                e.currentTarget.style.boxShadow = '0 8px 24px rgba(29, 185, 84, 0.4)';
              }
            }}
          >
            {loading ? (
              <span style={{ display: 'flex', alignItems: 'center', justifyContent: 'center', gap: '0.5rem' }}>
                <span style={{ 
                  display: 'inline-block',
                  width: '16px',
                  height: '16px',
                  border: '2px solid rgba(0, 0, 0, 0.3)',
                  borderTopColor: '#000',
                  borderRadius: '50%',
                  animation: 'spin 0.8s linear infinite'
                }}></span>
                Connecting...
              </span>
            ) : (
              <span style={{ display: 'flex', alignItems: 'center', justifyContent: 'center', gap: '0.5rem' }}>
                <svg width="20" height="20" viewBox="0 0 24 24" fill="currentColor">
                  <path d="M12 0C5.4 0 0 5.4 0 12s5.4 12 12 12 12-5.4 12-12S18.66 0 12 0zm5.521 17.34c-.24.359-.66.48-1.021.24-2.82-1.74-6.36-2.101-10.561-1.141-.418.122-.779-.179-.899-.539-.12-.421.18-.78.54-.9 4.56-1.021 8.52-.6 11.64 1.32.42.18.479.659.301 1.02zm1.44-3.3c-.301.42-.841.6-1.262.3-3.239-1.98-8.159-2.58-11.939-1.38-.479.12-1.02-.12-1.14-.6-.12-.48.12-1.021.6-1.141C9.6 9.9 15 10.561 18.72 12.84c.361.181.54.78.241 1.2zm.12-3.36C15.24 8.4 8.82 8.16 5.16 9.301c-.6.179-1.2-.181-1.38-.721-.18-.601.18-1.2.72-1.381 4.26-1.26 11.28-1.02 15.721 1.621.539.3.719 1.02.419 1.56-.299.421-1.02.599-1.559.3z"/>
                </svg>
                Continue with Spotify
              </span>
            )}
          </button>
        </form>
        
        <style>
          {`
            @keyframes spin {
              to { transform: rotate(360deg); }
            }
          `}
        </style>
      </div>
    </div>
  );
}

export default Login;
