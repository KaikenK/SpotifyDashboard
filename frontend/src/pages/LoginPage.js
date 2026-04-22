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

export default function LoginPage() {
  const { login } = useAuth();
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const [showPw, setShowPw] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError('');
    try {
      await login(email, password);
    } catch (err) {
      setError(formatApiError(err.response?.data) || err.message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-spotify-black flex items-center justify-center p-4 relative overflow-hidden">
      <div className="absolute top-0 left-0 w-[600px] h-[600px] bg-spotify-green/[0.06] rounded-full blur-[120px] -translate-x-1/2 -translate-y-1/2" />
      <div className="absolute bottom-0 right-0 w-[400px] h-[400px] bg-spotify-green/[0.04] rounded-full blur-[100px] translate-x-1/3 translate-y-1/3" />

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
          <h1 className="font-outfit text-3xl font-bold text-white tracking-tight" data-testid="login-title">Soundwave</h1>
          <p className="text-spotify-text mt-2 text-sm">Sign in to your artist dashboard</p>
        </div>

        <div className="bg-spotify-surface border border-white/[0.08] rounded-2xl p-8">
          {error && (
            <motion.div
              initial={{ opacity: 0, height: 0 }}
              animate={{ opacity: 1, height: 'auto' }}
              className="bg-spotify-destructive/10 border border-spotify-destructive/20 text-spotify-destructive text-sm rounded-lg px-4 py-3 mb-6"
              data-testid="login-error"
            >
              {error}
            </motion.div>
          )}

          <form onSubmit={handleSubmit} className="space-y-5">
            <div>
              <label className="block text-xs font-bold uppercase tracking-[0.15em] text-spotify-text mb-2">Email</label>
              <input
                type="email"
                value={email}
                onChange={e => setEmail(e.target.value)}
                placeholder="you@example.com"
                data-testid="login-email-input"
                required
                className="w-full bg-spotify-elevated border border-white/[0.1] rounded-lg px-4 py-3 text-white text-sm placeholder:text-spotify-muted focus:outline-none focus:border-spotify-green/50 focus:ring-1 focus:ring-spotify-green/30 transition-all"
              />
            </div>
            <div>
              <label className="block text-xs font-bold uppercase tracking-[0.15em] text-spotify-text mb-2">Password</label>
              <div className="relative">
                <input
                  type={showPw ? 'text' : 'password'}
                  value={password}
                  onChange={e => setPassword(e.target.value)}
                  placeholder="Enter your password"
                  data-testid="login-password-input"
                  required
                  className="w-full bg-spotify-elevated border border-white/[0.1] rounded-lg px-4 py-3 pr-11 text-white text-sm placeholder:text-spotify-muted focus:outline-none focus:border-spotify-green/50 focus:ring-1 focus:ring-spotify-green/30 transition-all"
                />
                <button
                  type="button"
                  onClick={() => setShowPw(!showPw)}
                  className="absolute right-3 top-1/2 -translate-y-1/2 text-spotify-muted hover:text-white transition-colors"
                >
                  {showPw ? <EyeOff className="w-4 h-4" /> : <Eye className="w-4 h-4" />}
                </button>
              </div>
            </div>
            <button
              type="submit"
              disabled={loading}
              data-testid="login-submit-button"
              className="w-full bg-spotify-green hover:bg-spotify-green-hover text-black font-bold py-3 rounded-full transition-all duration-200 hover:scale-[1.02] active:scale-[0.98] disabled:opacity-60 disabled:cursor-not-allowed text-sm"
            >
              {loading ? 'Signing in...' : 'Sign In'}
            </button>
          </form>

          <div className="mt-6 text-center">
            <span className="text-spotify-text text-sm">No account? </span>
            <Link to="/register" className="text-spotify-green hover:underline text-sm font-semibold" data-testid="register-link">
              Create one
            </Link>
          </div>
        </div>
      </motion.div>
    </div>
  );
}
