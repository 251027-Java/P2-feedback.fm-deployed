import { BrowserRouter as Router, Routes, Route, useLocation } from 'react-router-dom';
import Navbar from './components/Navbar';
import Login from './components/Login';
import Dashboard from './components/Dashboard';
import TopArtists from './components/TopArtists';
import TopSongs from './components/TopSongs';
import ListeningHistory from './components/ListeningHistory';
import CurrentlyPlaying from './components/CurrentlyPlaying';
import Playlists from './components/Playlists';

function AppContent() {
  const location = useLocation();
  const isLoginPage = location.pathname === '/';

  return (
    <div 
      className="App" 
      style={{ 
        position: 'relative', 
        minHeight: '100vh',
        backgroundColor: '#000',
        color: 'white' // Default text color for readability
      }}
    >
      {!isLoginPage && <Navbar />}
      <div 
        className="main-content-wrapper"
        style={{ 
          position: 'relative',
          zIndex: 1,
          width: '100%',
          minHeight: '100vh',
          paddingTop: '60px',
          backgroundColor: '#000',
          backgroundImage: 'radial-gradient(circle, rgba(255, 255, 255, 0.1) 1px, transparent 1px)',
          backgroundSize: '24px 24px',
          backgroundPosition: '0 0',
          pointerEvents: 'auto'
        }}
      >
        <Routes>
          <Route path="/" element={<Login />} />
          <Route path="/dashboard" element={<Dashboard />} />
          <Route path="/top-artists" element={<TopArtists />} />
          <Route path="/top-songs" element={<TopSongs />} />
          <Route path="/playlists" element={<Playlists />} />
          <Route path="/history" element={<ListeningHistory />} />
          <Route path="/currently-playing" element={<CurrentlyPlaying />} />
        </Routes>
      </div>
    </div>
  );
}

function App() {
  return (
    <Router>
      <AppContent />
    </Router>
  );
}

export default App;
