import { useState, useEffect, useCallback } from 'react';
import api from '../api/axios';

/** Mirrors Java SongController + AdminController responses */
export default function useAdminData() {
  const [pending, setPending] = useState([]);
  const [artists, setArtists] = useState([]);
  const [comments, setComments] = useState([]);
  const [report, setReport] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const loadData = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const [pendingRes, artistsRes, commentsRes, reportRes] = await Promise.all([
        api.get('/api/songs/pending'),
        api.get('/api/admin/unverified-artists'),
        api.get('/api/comments/moderation'),
        api.get('/api/admin/reports/system'),
      ]);
      setPending(pendingRes.data);
      setArtists(artistsRes.data);
      setComments(commentsRes.data || []);
      setReport(reportRes.data || null);
    } catch (err) {
      console.error('Failed to load admin data:', err.message);
      setError('Failed to load admin data.');
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => { loadData(); }, [loadData]);

  const approveSong = useCallback(async (songId) => {
    try {
      await api.put(`/api/songs/${songId}/approve`);
      setPending(prev => prev.filter(s => s.id !== songId));
    } catch (err) { console.error('Failed to approve:', err.message); }
  }, []);

  const rejectSong = useCallback(async (songId) => {
    try {
      await api.put(`/api/songs/${songId}/reject`);
      setPending(prev => prev.filter(s => s.id !== songId));
    } catch (err) { console.error('Failed to reject:', err.message); }
  }, []);

  const verifyArtist = useCallback(async (userId) => {
    try {
      await api.put(`/api/admin/verify-artist/${userId}`);
      setArtists(prev => prev.filter(a => a.id !== userId));
    } catch (err) { console.error('Failed to verify:', err.message); }
  }, []);

  const removeComment = useCallback(async (commentId) => {
    try {
      await api.put(`/api/comments/${commentId}/remove`);
      setComments(prev => prev.filter(c => c.id !== commentId));
    } catch (err) {
      console.error('Failed to remove comment:', err.message);
    }
  }, []);

  return {
    pending,
    artists,
    comments,
    report,
    loading,
    error,
    approveSong,
    rejectSong,
    verifyArtist,
    removeComment,
  };
}
