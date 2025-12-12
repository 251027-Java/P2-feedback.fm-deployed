import axios from 'axios';

const API_BASE_URL = 'http://localhost:8080/api';

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
  }
);

export const authAPI = {
  getAuthUrl: () => api.get('/auth/login'),
  handleCallback: (code: string) => api.get(`/auth/callback?code=${code}`),
};

export const userAPI = {
  getProfile: (id: string) => api.get(`/users/${id}`),
  getDashboard: (id: string) => api.get(`/users/${id}/dashboard`),
  getStats: (id: string) => api.get(`/users/${id}/stats`),
};

export const artistsAPI = {
  getTopArtists: (timeRange: string = 'medium_term') => 
    api.get(`/artists/top?time_range=${timeRange}`),
};

export const songsAPI = {
  getTopSongs: (timeRange: string = 'medium_term') => 
    api.get(`/songs/top?time_range=${timeRange}`),
  getCurrentlyPlaying: () => api.get('/songs/currently-playing'),
};

export const historyAPI = {
  getHistory: (limit: number = 50) => api.get(`/history?limit=${limit}`),
};

export const playlistsAPI = {
  getAll: () => api.get('/playlists'),
  getById: (id: string) => api.get(`/playlists/${id}`),
  getSongs: (id: string) => api.get(`/playlists/${id}/songs`),
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