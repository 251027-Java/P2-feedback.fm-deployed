import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { removeAccessToken, userAPI } from '../services/api';
import type {
  StaggeredMenuItem,
  StaggeredMenuSocialItem,
} from './StaggeredMenu';
import StaggeredMenu from './StaggeredMenu';

function Navbar() {
  const navigate = useNavigate();
  const [socialItems, setSocialItems] = useState<StaggeredMenuSocialItem[]>([]);

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
    {
      label: 'Top Artists',
      ariaLabel: 'View top artists',
      link: '/top-artists',
    },
    { label: 'Top Songs', ariaLabel: 'View top songs', link: '/top-songs' },
    { label: 'Playlists', ariaLabel: 'View playlists', link: '/playlists' },
    { label: 'History', ariaLabel: 'View listening history', link: '/history' },
    {
      label: 'Now Playing',
      ariaLabel: 'View currently playing',
      link: '/currently-playing',
    },
    { label: 'Logout', ariaLabel: 'Logout', link: '/logout' },
  ];

  useEffect(() => {
    const fetchUserProfile = async () => {
      const userId = localStorage.getItem('userId');
      if (!userId) return;

      try {
        const response = await userAPI.getProfile(userId);
        const userData = response.data;

        // Get Spotify profile URL - could be in href or construct from userId
        let spotifyUrl = userData?.href;
        if (!spotifyUrl && userId) {
          // Construct Spotify profile URL: https://open.spotify.com/user/{userId}
          spotifyUrl = `https://open.spotify.com/user/${userId}`;
        }

        const socials: StaggeredMenuSocialItem[] = [
          {
            label: 'GitHub',
            link: 'https://github.com/251027-Java/P1-feedback.fm',
          },
        ];

        if (spotifyUrl) {
          socials.push({ label: 'Spotify', link: spotifyUrl });
        }

        setSocialItems(socials);
      } catch (error) {
        console.error('Error fetching user profile for socials:', error);
        // Set GitHub link even if profile fetch fails
        setSocialItems([
          {
            label: 'GitHub',
            link: 'https://github.com/251027-Java/P1-feedback.fm',
          },
        ]);
      }
    };

    fetchUserProfile();
  }, []);

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
      socialItems={socialItems}
      displaySocials={true}
      displayItemNumbering={false}
      menuButtonColor="#FFFFFF"
      openMenuButtonColor="#1DB954"
      changeMenuColorOnOpen={true}
      colors={['#121212', '#181818', '#282828']}
      accentColor="#1DB954"
      isFixed={true}
      closeOnClickAway={true}
      onItemClick={handleItemClick}
      logoUrl=""
    />
  );
}

export default Navbar;
