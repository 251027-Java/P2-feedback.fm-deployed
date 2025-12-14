import { useNavigate } from 'react-router-dom';
import { removeAccessToken } from '../services/api';
import StaggeredMenu from './StaggeredMenu';
import type { StaggeredMenuItem } from './StaggeredMenu';

function Navbar() {
  const navigate = useNavigate();

  const handleLogout = () => {
    // Clear all tokens and user data
    removeAccessToken();
    localStorage.removeItem('spotifyAccessToken');
    localStorage.removeItem('userId');
    
    // Redirect to login page
    navigate('/');
  };

  const menuItems: StaggeredMenuItem[] = [
    { label: 'Dashboard', ariaLabel: 'Go to dashboard', link: '/dashboard' },
    { label: 'Top Artists', ariaLabel: 'View top artists', link: '/top-artists' },
    { label: 'Top Songs', ariaLabel: 'View top songs', link: '/top-songs' },
    { label: 'History', ariaLabel: 'View listening history', link: '/history' },
    { label: 'Now Playing', ariaLabel: 'View currently playing', link: '/currently-playing' },
    { label: 'Logout', ariaLabel: 'Logout', link: '/logout' },
  ];

  const handleItemClick = (link: string) => {
    if (link === '/logout') {
      handleLogout();
    } else {
      navigate(link);
    }
  };

  return (
    <StaggeredMenu
      position="right"
      items={menuItems}
      displaySocials={false}
      displayItemNumbering={true}
      menuButtonColor="#1DB954"
      openMenuButtonColor="#1DB954"
      changeMenuColorOnOpen={true}
      colors={['#1DB954', '#1ed760']}
      accentColor="#1DB954"
      isFixed={true}
      closeOnClickAway={true}
      onItemClick={handleItemClick}
    />
  );
}

export default Navbar;
