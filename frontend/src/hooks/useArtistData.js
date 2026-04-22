import { useState, useEffect, useCallback } from 'react';
import api from '../api/axios';

/**
 * Mirrors Java SongController + AnalyticsController responses.
 *
 * GET /api/songs/my        → List<Song>  (Song entity with nested artist)
 * GET /api/analytics/my    → List<SongAnalytics>  (entity with nested song object)
 *   Keys: id, song{id,title,...}, totalPlays, totalLikes, totalComments, totalSaves, engagementScore
 */
export default function useArtistData() {
  const [songs, setSongs] = useState([]);
  const [rawAnalytics, setRawAnalytics] = useState([]);
  const [albums, setAlbums] = useState([]);
  const [fanInsights, setFanInsights] = useState([]);
  const [trendPoints, setTrendPoints] = useState([]);
  const [selectedAlbumAnalytics, setSelectedAlbumAnalytics] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const loadData = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const [songsRes, analyticsRes, albumsRes, fanInsightsRes, trendRes] = await Promise.all([
        api.get('/api/songs/my'),
        api.get('/api/analytics/my'),
        api.get('/api/albums/my'),
        api.get('/api/analytics/fan-insights?limit=10'),
        api.get('/api/analytics/trend?days=14'),
      ]);
      setSongs(songsRes.data);
      setRawAnalytics(analyticsRes.data);
      setAlbums(albumsRes.data || []);
      setFanInsights(fanInsightsRes.data || []);
      setTrendPoints(trendRes.data || []);
    } catch (err) {
      console.error('Failed to load artist data:', err.message);
      setError('Failed to load dashboard data.');
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => { loadData(); }, [loadData]);

  // Java upload: POST /api/songs/upload?title=X&durationSec=Y (@RequestParam)
  const uploadSong = useCallback(async (title, durationSec) => {
    await api.post(`/api/songs/upload?title=${encodeURIComponent(title)}&durationSec=${durationSec}`);
    await loadData();
  }, [loadData]);

  const submitSong = useCallback(async (songId) => {
    await api.put(`/api/songs/${songId}/submit`);
    await loadData();
  }, [loadData]);

  const createAlbum = useCallback(async (payload) => {
    await api.post('/api/albums', payload);
    await loadData();
  }, [loadData]);

  const updateAlbum = useCallback(async (albumId, payload) => {
    await api.put(`/api/albums/${albumId}`, payload);
    await loadData();
  }, [loadData]);

  const addSongToAlbum = useCallback(async (albumId, songId) => {
    await api.put(`/api/albums/${albumId}/songs/${songId}`);
    await loadData();
  }, [loadData]);

  const loadAlbumAnalytics = useCallback(async (albumId) => {
    const { data } = await api.get(`/api/albums/${albumId}/analytics`);
    setSelectedAlbumAnalytics(data);
  }, []);

  // Normalize: Java SongAnalytics entity → flat shape for UI
  // rawAnalytics[i] = { id, song: {id, title, ...}, totalPlays, totalLikes, ... }
  const analytics = rawAnalytics.map(a => ({
    songId: a.song?.id ?? a.songId,
    songTitle: a.song?.title ?? a.songTitle ?? 'Unknown',
    totalPlays: a.totalPlays ?? 0,
    totalLikes: a.totalLikes ?? 0,
    totalComments: a.totalComments ?? 0,
    totalSaves: a.totalSaves ?? 0,
    engagementScore: a.engagementScore ?? 0,
  }));

  const totalPlays = analytics.reduce((s, a) => s + a.totalPlays, 0);
  const totalLikes = analytics.reduce((s, a) => s + a.totalLikes, 0);
  const totalComments = analytics.reduce((s, a) => s + a.totalComments, 0);
  const totalSaves = analytics.reduce((s, a) => s + a.totalSaves, 0);

  const chartData = analytics.map(a => ({
    name: a.songTitle.length > 12 ? a.songTitle.substring(0, 12) + '...' : a.songTitle,
    plays: a.totalPlays,
    likes: a.totalLikes,
    engagement: a.engagementScore,
  }));

  return {
    songs,
    analytics,
    albums,
    fanInsights,
    trendPoints,
    selectedAlbumAnalytics,
    loading,
    error,
    uploadSong,
    submitSong,
    createAlbum,
    updateAlbum,
    addSongToAlbum,
    loadAlbumAnalytics,
    totalPlays,
    totalLikes,
    totalComments,
    totalSaves,
    chartData,
  };
}
