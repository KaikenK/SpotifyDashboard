import React, { useState } from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { motion } from 'framer-motion';
import { Music, Eye, EyeOff } from 'lucide-react';

function formatApiError(payload) {
  if (!payload) return 'Something went wrong. Please try again.';
  if (typeof payload === 'string') return payload;
  if (Array.isArray(payload.detail)) return payload.detail.map(e => e?.msg || JSON.stringify(e)).join(' ');
  if (typeof payload.detail === 'string') return payload.detail;
  if (typeof payload.message === 'string') return payload.message;
  return String(payload);
}

export default function RegisterPage() {
  const { register } = useAuth();
  const [form, setForm] = useState({ username: '', email: '', password: '', role: 'FAN' });
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [loading, setLoading] = useState(false);
  const [showPw, setShowPw] = useState(false);

  const update = (k, v) => setForm(prev => ({ ...prev, [k]: v }));

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError('');
    setSuccess('');
    try {
      await register(form.username, form.email, form.password, form.role);
      setSuccess(form.role === 'ARTIST'
        ? 'Account created! Artists need admin approval before logging in.'
        : 'Account created! You can now sign in.');
      setForm({ username: '', email: '', password: '', role: 'FAN' });
    } catch (err) {
      setError(formatApiError(err.response?.data) || err.message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-spotify-black flex items-center justify-center p-4 relative overflow-hidden">
      <div className="absolute top-0 right-0 w-[500px] h-[500px] bg-spotify-green/[0.05] rounded-full blur-[100px] translate-x-1/3 -translate-y-1/3" />

      <motion.div
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.5 }}
        className="w-full max-w-[420px] relative z-10"
      >
        <div className="text-center mb-8">
          <motion.div
            initial={{ scale: 0.8 }}
            animate={{ scale: 1 }}
            transition={{ delay: 0.2, type: 'spring', stiffness: 200 }}
            className="w-14 h-14 rounded-full bg-spotify-green flex items-center justify-center mx-auto mb-4"
          >
            <Music className="w-7 h-7 text-black" />
          </motion.div>
          <h1 className="font-outfit text-3xl font-bold text-white tracking-tight" data-testid="register-title">Create Account</h1>
          <p className="text-spotify-text mt-2 text-sm">Join the Soundwave community</p>
        </div>

        <div className="bg-spotify-surface border border-white/[0.08] rounded-2xl p-8">
          {error && (
            <motion.div initial={{ opacity: 0 }} animate={{ opacity: 1 }}
              className="bg-spotify-destructive/10 border border-spotify-destructive/20 text-spotify-destructive text-sm rounded-lg px-4 py-3 mb-5"
              data-testid="register-error">{error}</motion.div>
          )}
          {success && (
            <motion.div initial={{ opacity: 0 }} animate={{ opacity: 1 }}
              className="bg-spotify-green/10 border border-spotify-green/20 text-spotify-green text-sm rounded-lg px-4 py-3 mb-5"
              data-testid="register-success">{success}</motion.div>
          )}

          <form onSubmit={handleSubmit} className="space-y-4">
            <div>
              <label className="block text-xs font-bold uppercase tracking-[0.15em] text-spotify-text mb-2">Username</label>
              <input type="text" value={form.username} onChange={e => update('username', e.target.value)}
                placeholder="your_username" data-testid="register-username-input" required
                className="w-full bg-spotify-elevated border border-white/[0.1] rounded-lg px-4 py-3 text-white text-sm placeholder:text-spotify-muted focus:outline-none focus:border-spotify-green/50 focus:ring-1 focus:ring-spotify-green/30 transition-all" />
            </div>
            <div>
              <label className="block text-xs font-bold uppercase tracking-[0.15em] text-spotify-text mb-2">Email</label>
              <input type="email" value={form.email} onChange={e => update('email', e.target.value)}
                placeholder="you@example.com" data-testid="register-email-input" required
                className="w-full bg-spotify-elevated border border-white/[0.1] rounded-lg px-4 py-3 text-white text-sm placeholder:text-spotify-muted focus:outline-none focus:border-spotify-green/50 focus:ring-1 focus:ring-spotify-green/30 transition-all" />
            </div>
            <div>
              <label className="block text-xs font-bold uppercase tracking-[0.15em] text-spotify-text mb-2">Password</label>
              <div className="relative">
                <input type={showPw ? 'text' : 'password'} value={form.password} onChange={e => update('password', e.target.value)}
                  placeholder="Create a password" data-testid="register-password-input" required
                  className="w-full bg-spotify-elevated border border-white/[0.1] rounded-lg px-4 py-3 pr-11 text-white text-sm placeholder:text-spotify-muted focus:outline-none focus:border-spotify-green/50 focus:ring-1 focus:ring-spotify-green/30 transition-all" />
                <button type="button" onClick={() => setShowPw(!showPw)}
                  className="absolute right-3 top-1/2 -translate-y-1/2 text-spotify-muted hover:text-white transition-colors">
                  {showPw ? <EyeOff className="w-4 h-4" /> : <Eye className="w-4 h-4" />}
                </button>
              </div>
            </div>
            <div>
              <label className="block text-xs font-bold uppercase tracking-[0.15em] text-spotify-text mb-2">I am a...</label>
              <div className="flex gap-3">
                {['FAN', 'ARTIST'].map(r => (
                  <button key={r} type="button" onClick={() => update('role', r)}
                    data-testid={`register-role-${r.toLowerCase()}`}
                    className={`flex-1 py-3 rounded-lg text-sm font-semibold transition-all duration-200 border ${
                      form.role === r
                        ? 'bg-spotify-green/10 border-spotify-green text-spotify-green'
                        : 'bg-spotify-elevated border-white/[0.1] text-spotify-text hover:border-white/[0.2]'
                    }`}>
                    {r === 'FAN' ? 'Listener' : 'Artist'}
                  </button>
                ))}
              </div>
            </div>
            <button type="submit" disabled={loading} data-testid="register-submit-button"
              className="w-full bg-spotify-green hover:bg-spotify-green-hover text-black font-bold py-3 rounded-full transition-all duration-200 hover:scale-[1.02] active:scale-[0.98] disabled:opacity-60 text-sm mt-2">
              {loading ? 'Creating...' : 'Create Account'}
            </button>
          </form>

          <div className="mt-6 text-center">
            <span className="text-spotify-text text-sm">Already have an account? </span>
            <Link to="/login" className="text-spotify-green hover:underline text-sm font-semibold" data-testid="login-link">Sign in</Link>
          </div>
        </div>
      </motion.div>
    </div>
  );
}
