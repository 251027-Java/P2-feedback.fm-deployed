import axios from 'axios';

const API_BASE_URL = `${import.meta.env.VITE_API_URL || 'http://localhost:8080'}/api`;

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

api.interceptors.request.use((config) => {
  const token = localStorage.getItem('accessToken');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('accessToken');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  },
);

export const authAPI = {
  getAuthUrl: () => api.get('/auth/login'),
  handleCallback: (code: string) => api.get(`/auth/callback?code=${code}`),
};

export const userAPI = {
  getProfile: (id: string) => api.get(`/users/${id}`),
  getDashboard: (id: string) => {
    const spotifyToken = localStorage.getItem('spotifyAccessToken');
    return api.get(`/users/${id}/dashboard`, {
      headers: spotifyToken ? { 'X-Spotify-Token': spotifyToken } : {},
    });
  },
  getStats: (id: string) => api.get(`/users/${id}/stats`),
};

export const artistsAPI = {
  getTopArtists: (timeRange: string = 'medium_term') => {
    const spotifyToken = localStorage.getItem('spotifyAccessToken');
    return api.get(`/artists/top?time_range=${timeRange}`, {
      headers: spotifyToken ? { 'X-Spotify-Token': spotifyToken } : {},
    });
  },
};

export const songsAPI = {
  getTopSongs: (timeRange: string = 'medium_term') => {
    const spotifyToken = localStorage.getItem('spotifyAccessToken');
    return api.get(`/songs/top?time_range=${timeRange}`, {
      headers: spotifyToken ? { 'X-Spotify-Token': spotifyToken } : {},
    });
  },
  getCurrentlyPlaying: () => {
    const spotifyToken = localStorage.getItem('spotifyAccessToken');
    return api.get('/songs/currently-playing', {
      headers: spotifyToken ? { 'X-Spotify-Token': spotifyToken } : {},
    });
  },
};

export const historyAPI = {
  getHistory: (limit: number = 50) => {
    const spotifyToken = localStorage.getItem('spotifyAccessToken');
    return api.get(`/history?limit=${limit}`, {
      headers: spotifyToken ? { 'X-Spotify-Token': spotifyToken } : {},
    });
  },
};

export const playlistsAPI = {
  getAll: () => {
    const spotifyToken = localStorage.getItem('spotifyAccessToken');
    return api.get('/playlists/spotify', {
      headers: spotifyToken ? { 'X-Spotify-Token': spotifyToken } : {},
    });
  },
  getById: (id: string) => api.get(`/playlists/${id}`),
  getSongs: (id: string) => {
    const spotifyToken = localStorage.getItem('spotifyAccessToken');
    return api.get(`/playlists/spotify/${id}/tracks`, {
      headers: spotifyToken ? { 'X-Spotify-Token': spotifyToken } : {},
    });
  },
};

export const albumsAPI = {
  getAll: () => api.get('/albums'),
  getById: (id: string) => api.get(`/albums/${id}`),
  getSongs: (id: string) => api.get(`/albums/${id}/songs`),
  search: (query: string) => api.get(`/albums/search?query=${query}`),
};

export const setAccessToken = (token: string) => {
  localStorage.setItem('accessToken', token);
};

export const getAccessToken = () => {
  return localStorage.getItem('accessToken');
};

export const removeAccessToken = () => {
  localStorage.removeItem('accessToken');
};

export const isAuthenticated = () => {
  return !!getAccessToken();
};

export default api;
