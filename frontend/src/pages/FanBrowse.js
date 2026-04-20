import React, { useState } from 'react';
import { motion } from 'framer-motion';
import { Play, Heart, Clock, Music2, Search, Pause } from 'lucide-react';
import useFanData from '../hooks/useFanData';
import DashboardLayout from '../components/DashboardLayout';

const TABS = [{ id: 'browse', label: 'Browse' }];
const stagger = { hidden: {}, visible: { transition: { staggerChildren: 0.06 } } };
const fadeUp = { hidden: { opacity: 0, y: 12 }, visible: { opacity: 1, y: 0, transition: { duration: 0.35 } } };

function fmtDur(sec) { if (!sec) return '--:--'; return `${Math.floor(sec / 60)}:${(sec % 60).toString().padStart(2, '0')}`; }

/* Java Song entity fields: song.id, song.title, song.durationSec, song.coverArt, song.artist?.username */

function SongCard({ song, isPlaying, isLiked, onPlay, onLike }) {
  return (
    <motion.div variants={fadeUp} className="bg-spotify-surface border border-white/[0.08] rounded-xl p-4 hover:bg-spotify-elevated hover:border-white/[0.12] transition-all duration-300 group" data-testid={`browse-song-${song.id}`}>
      <div className="relative mb-4">
        <img src={song.coverArt || 'https://images.unsplash.com/photo-1587659927818-f89edd4db555?w=300'} alt={song.title} className="w-full aspect-square object-cover rounded-lg" />
        <button onClick={() => onPlay(song.id)} data-testid={`play-song-${song.id}`}
          className={`absolute bottom-2 right-2 w-12 h-12 rounded-full flex items-center justify-center shadow-xl transition-all duration-300 ${isPlaying ? 'bg-spotify-green scale-100 opacity-100' : 'bg-spotify-green scale-90 opacity-0 group-hover:scale-100 group-hover:opacity-100'}`}>
          {isPlaying ? <Pause className="w-5 h-5 text-black" fill="currentColor" /> : <Play className="w-5 h-5 text-black ml-0.5" fill="currentColor" />}
        </button>
        {isPlaying && <div className="absolute top-2 left-2 flex items-center gap-1">{[3,4,2,5].map((h,i) => <span key={i} className={`w-0.5 h-${h} bg-spotify-green rounded-full animate-pulse`} style={{ animationDelay: `${i*100}ms` }} />)}</div>}
      </div>
      <h4 className="text-white text-sm font-semibold truncate mb-0.5">{song.title}</h4>
      <p className="text-spotify-muted text-xs truncate mb-3">{song.artist?.username || 'Unknown Artist'}</p>
      <div className="flex items-center justify-between">
        <span className="text-spotify-muted text-xs flex items-center gap-1"><Clock className="w-3 h-3" />{fmtDur(song.durationSec)}</span>
        <button onClick={() => onLike(song.id)} data-testid={`like-song-${song.id}`} className={`p-1.5 rounded-full transition-all duration-200 ${isLiked ? 'text-spotify-green' : 'text-spotify-muted hover:text-white'}`}><Heart className="w-4 h-4" fill={isLiked ? 'currentColor' : 'none'} /></button>
      </div>
    </motion.div>
  );
}

function PlayerBar({ song, isLiked, onLike, onPause }) {
  return (
    <motion.div initial={{ y: 80, opacity: 0 }} animate={{ y: 0, opacity: 1 }} className="fixed bottom-0 left-64 right-0 bg-spotify-black/80 backdrop-blur-2xl border-t border-white/[0.08] z-50 px-6 py-3" data-testid="player-bar">
      <div className="flex items-center gap-4">
        <img src={song.coverArt || 'https://images.unsplash.com/photo-1587659927818-f89edd4db555?w=80'} alt="" className="w-12 h-12 rounded object-cover" />
        <div className="flex-1 min-w-0"><p className="text-white text-sm font-semibold truncate">{song.title}</p><p className="text-spotify-muted text-xs">{song.artist?.username}</p></div>
        <div className="flex items-center gap-4">
          <button onClick={() => onLike(song.id)} className={`transition-colors ${isLiked ? 'text-spotify-green' : 'text-spotify-text hover:text-white'}`}><Heart className="w-4 h-4" fill={isLiked ? 'currentColor' : 'none'} /></button>
          <button onClick={onPause} className="w-10 h-10 rounded-full bg-white flex items-center justify-center hover:scale-105 transition-transform"><Pause className="w-5 h-5 text-black" fill="currentColor" /></button>
        </div>
        <div className="flex-1 max-w-xs"><div className="h-1 bg-white/10 rounded-full overflow-hidden"><motion.div initial={{ width: '0%' }} animate={{ width: '65%' }} transition={{ duration: 2, ease: 'easeOut' }} className="h-full bg-spotify-green rounded-full" /></div></div>
      </div>
    </motion.div>
  );
}

export default function FanBrowse() {
  const [activeTab, setActiveTab] = useState('browse');
  const [search, setSearch] = useState('');
  const { songs, playing, setPlaying, likedSongs, loading, handlePlay, handleLike, nowPlaying } = useFanData();
  const filtered = songs.filter(s => s.title?.toLowerCase().includes(search.toLowerCase()) || s.artist?.username?.toLowerCase().includes(search.toLowerCase()));
  return (
    <DashboardLayout activeTab={activeTab} setActiveTab={setActiveTab} tabs={TABS}>
      <div className="p-6 md:p-10 lg:p-12 pb-32" data-testid="fan-browse">
        <motion.div initial={{ opacity: 0, y: -10 }} animate={{ opacity: 1, y: 0 }}><h1 className="font-outfit text-3xl font-bold text-white tracking-tight mb-1">Browse Music</h1><p className="text-spotify-text text-sm mb-8">Discover new tracks from your favorite artists</p></motion.div>
        <motion.div initial={{ opacity: 0 }} animate={{ opacity: 1 }} transition={{ delay: 0.1 }} className="relative mb-8 max-w-md"><Search className="absolute left-4 top-1/2 -translate-y-1/2 w-4 h-4 text-spotify-muted" /><input value={search} onChange={e => setSearch(e.target.value)} placeholder="Search songs or artists..." data-testid="search-input" className="w-full bg-spotify-surface border border-white/[0.08] rounded-full pl-11 pr-4 py-3 text-white text-sm placeholder:text-spotify-muted focus:outline-none focus:border-spotify-green/50 transition-all" /></motion.div>
        {loading ? <div className="flex items-center justify-center py-20"><div className="w-8 h-8 border-2 border-spotify-green border-t-transparent rounded-full animate-spin" /></div>
        : filtered.length === 0 ? <div className="text-center py-20"><Music2 className="w-16 h-16 text-spotify-muted mx-auto mb-4" /><p className="text-spotify-text text-lg">No published songs yet</p></div>
        : <motion.div initial="hidden" animate="visible" variants={stagger} className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-4">
            {filtered.map(song => <SongCard key={song.id} song={song} isPlaying={playing === song.id} isLiked={likedSongs.has(song.id)} onPlay={handlePlay} onLike={handleLike} />)}
          </motion.div>}
      </div>
      {nowPlaying && <PlayerBar song={nowPlaying} isLiked={likedSongs.has(nowPlaying.id)} onLike={handleLike} onPause={() => setPlaying(null)} />}
    </DashboardLayout>
  );
}
