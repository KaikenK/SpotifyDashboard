import { useState, useEffect, useCallback } from 'react';
import api from '../api/axios';

/** Mirrors Java SongController.getPublishedSongs() response (Song entities with nested artist) */
export default function useFanData() {
  const [songs, setSongs] = useState([]);
  const [playing, setPlaying] = useState(null);
  const [likedSongs, setLikedSongs] = useState(new Set());
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const loadSongs = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const { data } = await api.get('/api/songs/published');
      setSongs(data);
    } catch (err) {
      console.error('Failed to load songs:', err.message);
      setError('Failed to load songs.');
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => { loadSongs(); }, [loadSongs]);

  const handlePlay = useCallback(async (songId) => {
    if (playing === songId) { setPlaying(null); return; }
    setPlaying(songId);
    try { await api.post(`/api/analytics/play/${songId}`); }
    catch (err) { console.error('Failed to record play:', err.message); }
  }, [playing]);

  const handleLike = useCallback(async (songId) => {
    setLikedSongs(prev => {
      const next = new Set(prev);
      if (next.has(songId)) next.delete(songId); else next.add(songId);
      return next;
    });
    try { await api.post(`/api/analytics/like/${songId}`); }
    catch (err) { console.error('Failed to record like:', err.message); }
  }, []);

  // Java Song entity: song.id (not song._id)
  const nowPlaying = songs.find(s => s.id === playing);

  return { songs, playing, setPlaying, likedSongs, loading, error, handlePlay, handleLike, nowPlaying };
}
