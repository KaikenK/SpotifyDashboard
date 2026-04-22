import { useState, useEffect, useCallback } from 'react';
import api from '../api/axios';

/** Mirrors Java SongController.getPublishedSongs() response (Song entities with nested artist) */
export default function useFanData() {
  const [songs, setSongs] = useState([]);
  const [playing, setPlaying] = useState(null);
  const [likedSongs, setLikedSongs] = useState(new Set());
  const [followedArtists, setFollowedArtists] = useState(new Set());
  const [subscribedArtists, setSubscribedArtists] = useState(new Set());
  const [commentsBySong, setCommentsBySong] = useState({});
  const [notifications, setNotifications] = useState([]);
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

  const loadFollows = useCallback(async () => {
    try {
      const { data } = await api.get('/api/follows/my');
      const followed = new Set();
      const subscribed = new Set();
      data.forEach(item => {
        followed.add(item.artistId);
        if (item.isSubscribed) subscribed.add(item.artistId);
      });
      setFollowedArtists(followed);
      setSubscribedArtists(subscribed);
    } catch {
      // Fan may be unauthenticated in some environments; keep UI usable.
    }
  }, []);

  const loadNotifications = useCallback(async () => {
    try {
      const { data } = await api.get('/api/notifications/my');
      setNotifications(data || []);
    } catch {
      setNotifications([]);
    }
  }, []);

  useEffect(() => {
    loadFollows();
    loadNotifications();
  }, [loadFollows, loadNotifications]);

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

  const loadComments = useCallback(async (songId) => {
    try {
      const { data } = await api.get(`/api/comments/song/${songId}`);
      setCommentsBySong(prev => ({ ...prev, [songId]: data || [] }));
    } catch (err) {
      console.error('Failed to load comments:', err.message);
    }
  }, []);

  useEffect(() => {
    songs.slice(0, 12).forEach(song => {
      if (!commentsBySong[song.id]) {
        loadComments(song.id);
      }
    });
  }, [songs, commentsBySong, loadComments]);

  const addComment = useCallback(async (songId, content) => {
    await api.post(`/api/comments/song/${songId}`, { content });
    await loadComments(songId);
  }, [loadComments]);

  const followArtist = useCallback(async (artistId) => {
    await api.post(`/api/follows/${artistId}`);
    setFollowedArtists(prev => new Set([...prev, artistId]));
  }, []);

  const toggleSubscription = useCallback(async (artistId, subscribe) => {
    if (!followedArtists.has(artistId)) {
      await api.post(`/api/follows/${artistId}?subscribe=${subscribe}`);
      setFollowedArtists(prev => new Set([...prev, artistId]));
    } else {
      await api.put(`/api/follows/${artistId}/subscription?subscribe=${subscribe}`);
    }

    setSubscribedArtists(prev => {
      const next = new Set(prev);
      if (subscribe) next.add(artistId);
      else next.delete(artistId);
      return next;
    });
  }, [followedArtists]);

  // Java Song entity: song.id (not song._id)
  const nowPlaying = songs.find(s => s.id === playing);

  return {
    songs,
    playing,
    setPlaying,
    likedSongs,
    followedArtists,
    subscribedArtists,
    commentsBySong,
    notifications,
    loading,
    error,
    handlePlay,
    handleLike,
    loadComments,
    addComment,
    followArtist,
    toggleSubscription,
    nowPlaying,
  };
}
