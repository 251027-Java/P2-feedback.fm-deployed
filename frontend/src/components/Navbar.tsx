import { Link } from 'react-router-dom';

function Navbar() {
  return (
    <nav>
      <Link to="/">Login</Link>
      <Link to="/dashboard">Dashboard</Link>
      <Link to="/top-artists">Top Artists</Link>
      <Link to="/top-songs">Top Songs</Link>
    </nav>
  );
}

export default Navbar;
