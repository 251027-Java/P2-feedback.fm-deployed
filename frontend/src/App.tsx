import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import Navbar from './components/Navbar';
import Login from './components/Login';
import Dashboard from './components/Dashboard';
import TopArtists from './components/TopArtists';
import TopSongs from './components/TopSongs';

function App() {
  return (
    <Router>
      <div className="App">
        <Navbar />
        <Routes>
          <Route path="/" element={<Login />} />
          <Route path="/dashboard" element={<Dashboard />} />
          <Route path="/top-artists" element={<TopArtists />} />
          <Route path="/top-songs" element={<TopSongs />} />
        </Routes>
      </div>
    </Router>
  );
}

export default App;
