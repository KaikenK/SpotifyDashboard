import React, { useState } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { Play, Heart, MessageCircle, Bookmark, TrendingUp, Upload, Clock, X, Music2 } from 'lucide-react';
import { AreaChart, Area, XAxis, YAxis, Tooltip, ResponsiveContainer } from 'recharts';
import useArtistData from '../hooks/useArtistData';
import DashboardLayout from '../components/DashboardLayout';

const TABS = [
  { id: 'overview', label: 'Overview' },
  { id: 'songs', label: 'My Songs' },
  { id: 'analytics', label: 'Analytics' },
];
const stagger = { hidden: {}, visible: { transition: { staggerChildren: 0.06 } } };
const fadeUp = { hidden: { opacity: 0, y: 12 }, visible: { opacity: 1, y: 0, transition: { duration: 0.35 } } };

function fmtDur(sec) { if (!sec) return '--:--'; return `${Math.floor(sec / 60)}:${(sec % 60).toString().padStart(2, '0')}`; }
function fmtNum(n) { if (!n && n !== 0) return '0'; if (n >= 1e6) return (n / 1e6).toFixed(1) + 'M'; if (n >= 1e3) return (n / 1e3).toFixed(1) + 'K'; return n.toString(); }

function StatusBadge({ status }) {
  const styles = { UPLOADED: 'bg-white/10 text-spotify-text', PENDING_APPROVAL: 'bg-spotify-warning/15 text-spotify-warning', PUBLISHED: 'bg-spotify-green/15 text-spotify-green', ACTIVE: 'bg-spotify-green/15 text-spotify-green', REJECTED: 'bg-spotify-destructive/15 text-spotify-destructive', UNPUBLISHED: 'bg-white/10 text-spotify-muted' };
  return <span className={`inline-flex items-center px-2.5 py-1 rounded-full text-[11px] font-bold uppercase tracking-wider ${styles[status] || styles.UPLOADED}`}>{status?.replace('_', ' ')}</span>;
}

function StatCard({ icon: Icon, label, value, color }) {
  return (
    <motion.div variants={fadeUp} className="bg-spotify-surface border border-white/[0.08] rounded-xl p-5 hover:-translate-y-0.5 hover:border-white/[0.15] transition-all duration-300 group cursor-default" data-testid={`stat-${label.toLowerCase().replace(/\s/g, '-')}`}>
      <div className="flex items-center justify-between mb-3">
        <span className="text-[11px] font-bold uppercase tracking-[0.15em] text-spotify-text">{label}</span>
        <div className={`w-8 h-8 rounded-lg flex items-center justify-center ${color} transition-transform group-hover:scale-110`}><Icon className="w-4 h-4" /></div>
      </div>
      <p className="font-outfit text-3xl font-black tracking-tighter text-white">{fmtNum(value)}</p>
    </motion.div>
  );
}

function UploadModal({ open, onClose, onUpload }) {
  const [title, setTitle] = useState('');
  const [duration, setDuration] = useState('');
  const [busy, setBusy] = useState(false);
  const submit = async () => {
    if (!title.trim()) return;
    setBusy(true);
    try { await onUpload(title, parseInt(duration) || 0); setTitle(''); setDuration(''); onClose(); }
    catch (e) { console.error('Upload failed:', e.message); }
    finally { setBusy(false); }
  };
  if (!open) return null;
  return (
    <AnimatePresence>
      <motion.div initial={{ opacity: 0 }} animate={{ opacity: 1 }} exit={{ opacity: 0 }} className="fixed inset-0 bg-black/80 backdrop-blur-sm z-50 flex items-center justify-center p-4" onClick={onClose}>
        <motion.div initial={{ opacity: 0, scale: 0.95 }} animate={{ opacity: 1, scale: 1 }} onClick={e => e.stopPropagation()} className="bg-spotify-surface border border-white/[0.1] rounded-2xl p-6 w-full max-w-md">
          <div className="flex items-center justify-between mb-6">
            <h3 className="font-outfit text-xl font-bold text-white">Upload New Track</h3>
            <button onClick={onClose} data-testid="upload-modal-close" className="text-spotify-muted hover:text-white transition-colors"><X className="w-5 h-5" /></button>
          </div>
          <div className="space-y-4">
            <div><label className="block text-xs font-bold uppercase tracking-[0.15em] text-spotify-text mb-2">Song Title</label><input value={title} onChange={e => setTitle(e.target.value)} placeholder="Enter song title" data-testid="upload-title-input" className="w-full bg-spotify-elevated border border-white/[0.1] rounded-lg px-4 py-3 text-white text-sm placeholder:text-spotify-muted focus:outline-none focus:border-spotify-green/50 transition-all" /></div>
            <div><label className="block text-xs font-bold uppercase tracking-[0.15em] text-spotify-text mb-2">Duration (seconds)</label><input value={duration} onChange={e => setDuration(e.target.value)} type="number" placeholder="e.g. 210" data-testid="upload-duration-input" className="w-full bg-spotify-elevated border border-white/[0.1] rounded-lg px-4 py-3 text-white text-sm placeholder:text-spotify-muted focus:outline-none focus:border-spotify-green/50 transition-all" /></div>
          </div>
          <div className="flex gap-3 mt-6">
            <button onClick={onClose} className="flex-1 py-3 rounded-full border border-white/[0.15] text-white text-sm font-semibold hover:border-white/[0.3] transition-all">Cancel</button>
            <button onClick={submit} disabled={busy || !title.trim()} data-testid="upload-submit-button" className="flex-1 py-3 rounded-full bg-spotify-green text-black text-sm font-bold hover:bg-spotify-green-hover hover:scale-[1.02] active:scale-[0.98] transition-all disabled:opacity-50">{busy ? 'Uploading...' : 'Upload'}</button>
          </div>
        </motion.div>
      </motion.div>
    </AnimatePresence>
  );
}

function PerfChart({ data, dataKey, gid }) {
  return (
    <ResponsiveContainer width="100%" height={280}>
      <AreaChart data={data}><defs><linearGradient id={gid} x1="0" y1="0" x2="0" y2="1"><stop offset="5%" stopColor="#1ED760" stopOpacity={0.3} /><stop offset="95%" stopColor="#1ED760" stopOpacity={0} /></linearGradient></defs>
        <XAxis dataKey="name" stroke="#757575" fontSize={11} tickLine={false} axisLine={false} /><YAxis stroke="#757575" fontSize={11} tickLine={false} axisLine={false} />
        <Tooltip contentStyle={{ backgroundColor: '#1A1A1A', border: '1px solid rgba(255,255,255,0.1)', borderRadius: '12px', color: '#fff', fontSize: '13px' }} />
        <Area type="monotone" dataKey={dataKey} stroke="#1ED760" strokeWidth={2} fill={`url(#${gid})`} /></AreaChart>
    </ResponsiveContainer>
  );
}

/* Java Song entity: song.id, song.title, song.durationSec, song.coverArt, song.status, song.artist?.username */
/* Java AnalyticsResponse: a.songId, a.songTitle, a.totalPlays, a.totalLikes, a.totalComments, a.totalSaves, a.engagementScore */

function SongRow({ song, index, analytics, onSubmit }) {
  const a = analytics.find(x => x.songId === song.id);
  return (
    <motion.div initial={{ opacity: 0, x: -10 }} animate={{ opacity: 1, x: 0 }} transition={{ delay: index * 0.05 }}
      className="flex items-center gap-4 px-5 py-3 hover:bg-white/[0.03] transition-colors group" data-testid={`song-row-${song.id}`}>
      <span className="text-spotify-muted text-sm w-6 text-right">{index + 1}</span>
      <img src={song.coverArt || 'https://images.unsplash.com/photo-1587659927818-f89edd4db555?w=80'} alt="" className="w-10 h-10 rounded object-cover" />
      <div className="flex-1 min-w-0">
        <p className="text-white text-sm font-medium truncate">{song.title}</p>
        <p className="text-spotify-muted text-xs">{fmtDur(song.durationSec)}</p>
      </div>
      <StatusBadge status={song.status} />
      {a && <span className="text-spotify-text text-xs">{fmtNum(a.totalPlays)} plays</span>}
      {song.status === 'UPLOADED' && (
        <button onClick={() => onSubmit(song.id)} data-testid={`submit-song-${song.id}`} className="text-xs font-semibold text-spotify-green hover:underline opacity-0 group-hover:opacity-100 transition-opacity">Submit</button>
      )}
    </motion.div>
  );
}

function OverviewTab({ songs, analytics, chartData, totalPlays, totalLikes, totalComments, onSubmit }) {
  return (
    <motion.div initial="hidden" animate="visible" variants={stagger}>
      <motion.div variants={stagger} className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4 mb-8">
        <StatCard icon={Music2} label="Total Songs" value={songs.length} color="bg-spotify-green/15 text-spotify-green" />
        <StatCard icon={Play} label="Total Plays" value={totalPlays} color="bg-blue-500/15 text-blue-400" />
        <StatCard icon={Heart} label="Total Likes" value={totalLikes} color="bg-pink-500/15 text-pink-400" />
        <StatCard icon={MessageCircle} label="Comments" value={totalComments} color="bg-purple-500/15 text-purple-400" />
      </motion.div>
      {chartData.length > 0 && <motion.div variants={fadeUp} className="bg-spotify-surface border border-white/[0.08] rounded-xl p-6 mb-8"><h3 className="font-outfit text-lg font-semibold text-white mb-4">Performance Overview</h3><PerfChart data={chartData} dataKey="plays" gid="gGreen" /></motion.div>}
      <motion.div variants={fadeUp} className="bg-spotify-surface border border-white/[0.08] rounded-xl overflow-hidden">
        <div className="p-5 border-b border-white/[0.05]"><h3 className="font-outfit text-lg font-semibold text-white">Recent Tracks</h3></div>
        {songs.length === 0 ? <div className="p-12 text-center"><Music2 className="w-12 h-12 text-spotify-muted mx-auto mb-3" /><p className="text-spotify-text">No songs yet. Upload your first track!</p></div>
        : <div className="divide-y divide-white/[0.05]">{songs.slice(0, 5).map((s, i) => <SongRow key={s.id} song={s} index={i} analytics={analytics} onSubmit={onSubmit} />)}</div>}
      </motion.div>
    </motion.div>
  );
}

function SongsTab({ songs, analytics, onSubmit }) {
  return (
    <motion.div initial="hidden" animate="visible" variants={stagger}>
      <motion.div variants={fadeUp} className="bg-spotify-surface border border-white/[0.08] rounded-xl overflow-hidden">
        <div className="p-5 border-b border-white/[0.05]"><h3 className="font-outfit text-lg font-semibold text-white">All Songs</h3></div>
        <table className="w-full" data-testid="songs-table"><thead><tr className="text-left text-[11px] font-bold uppercase tracking-[0.15em] text-spotify-muted border-b border-white/[0.05]"><th className="px-5 py-3">#</th><th className="px-5 py-3">Title</th><th className="px-5 py-3"><Clock className="w-3.5 h-3.5 inline" /></th><th className="px-5 py-3">Status</th><th className="px-5 py-3">Plays</th><th className="px-5 py-3">Likes</th><th className="px-5 py-3">Action</th></tr></thead>
          <tbody>{songs.map((song, i) => {
            const a = analytics.find(x => x.songId === song.id);
            return (
              <motion.tr key={song.id} variants={fadeUp} className="hover:bg-white/[0.03] transition-colors" data-testid={`song-table-row-${song.id}`}>
                <td className="px-5 py-3 text-spotify-muted text-sm">{i + 1}</td>
                <td className="px-5 py-3"><div className="flex items-center gap-3"><img src={song.coverArt || 'https://images.unsplash.com/photo-1587659927818-f89edd4db555?w=80'} alt="" className="w-9 h-9 rounded object-cover" /><span className="text-white text-sm font-medium">{song.title}</span></div></td>
                <td className="px-5 py-3 text-spotify-text text-sm">{fmtDur(song.durationSec)}</td>
                <td className="px-5 py-3"><StatusBadge status={song.status} /></td>
                <td className="px-5 py-3 text-white text-sm">{a ? fmtNum(a.totalPlays) : '-'}</td>
                <td className="px-5 py-3 text-white text-sm">{a ? fmtNum(a.totalLikes) : '-'}</td>
                <td className="px-5 py-3">{song.status === 'UPLOADED' && <button onClick={() => onSubmit(song.id)} data-testid={`submit-table-${song.id}`} className="text-xs font-bold text-spotify-green hover:underline">Submit</button>}</td>
              </motion.tr>);
          })}</tbody></table>
      </motion.div>
    </motion.div>
  );
}

function AnalyticsTab({ analytics, chartData, totalPlays, totalLikes, totalComments, totalSaves }) {
  return (
    <motion.div initial="hidden" animate="visible" variants={stagger}>
      <motion.div variants={stagger} className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4 mb-8">
        <StatCard icon={Play} label="Total Plays" value={totalPlays} color="bg-blue-500/15 text-blue-400" />
        <StatCard icon={Heart} label="Total Likes" value={totalLikes} color="bg-pink-500/15 text-pink-400" />
        <StatCard icon={MessageCircle} label="Comments" value={totalComments} color="bg-purple-500/15 text-purple-400" />
        <StatCard icon={Bookmark} label="Saves" value={totalSaves} color="bg-amber-500/15 text-amber-400" />
      </motion.div>
      {chartData.length > 0 && <motion.div variants={fadeUp} className="bg-spotify-surface border border-white/[0.08] rounded-xl p-6 mb-8"><h3 className="font-outfit text-lg font-semibold text-white mb-4">Engagement Score by Song</h3><PerfChart data={chartData} dataKey="engagement" gid="gEng" /></motion.div>}
      <motion.div variants={fadeUp} className="bg-spotify-surface border border-white/[0.08] rounded-xl overflow-hidden">
        <div className="p-5 border-b border-white/[0.05]"><h3 className="font-outfit text-lg font-semibold text-white">Song Analytics</h3></div>
        <div className="divide-y divide-white/[0.05]">{analytics.map((a, i) => (
          <motion.div key={a.songId} variants={fadeUp} className="flex items-center gap-4 px-5 py-4 hover:bg-white/[0.03] transition-colors" data-testid={`analytics-row-${a.songId}`}>
            <span className="text-spotify-muted text-sm w-6 text-right">{i + 1}</span>
            <div className="flex-1 min-w-0"><p className="text-white text-sm font-medium truncate">{a.songTitle}</p></div>
            <div className="flex items-center gap-6 text-xs">
              <span className="flex items-center gap-1 text-spotify-text"><Play className="w-3.5 h-3.5" />{fmtNum(a.totalPlays)}</span>
              <span className="flex items-center gap-1 text-pink-400"><Heart className="w-3.5 h-3.5" />{fmtNum(a.totalLikes)}</span>
              <span className="flex items-center gap-1 text-purple-400"><MessageCircle className="w-3.5 h-3.5" />{fmtNum(a.totalComments)}</span>
              <span className="flex items-center gap-1 text-amber-400"><Bookmark className="w-3.5 h-3.5" />{fmtNum(a.totalSaves)}</span>
              <div className="flex items-center gap-1.5"><TrendingUp className="w-3.5 h-3.5 text-spotify-green" /><span className="text-spotify-green font-bold">{a.engagementScore?.toFixed(1)}</span></div>
            </div>
          </motion.div>
        ))}</div>
      </motion.div>
    </motion.div>
  );
}

export default function ArtistDashboard() {
  const [activeTab, setActiveTab] = useState('overview');
  const [showUpload, setShowUpload] = useState(false);
  const { songs, analytics, uploadSong, submitSong, totalPlays, totalLikes, totalComments, totalSaves, chartData } = useArtistData();
  return (
    <DashboardLayout activeTab={activeTab} setActiveTab={setActiveTab} tabs={TABS}>
      <div className="p-6 md:p-10 lg:p-12" data-testid="artist-dashboard">
        <motion.div initial={{ opacity: 0, y: -10 }} animate={{ opacity: 1, y: 0 }} className="flex items-center justify-between mb-8">
          <div><h1 className="font-outfit text-3xl font-bold text-white tracking-tight">Dashboard</h1><p className="text-spotify-text text-sm mt-1">Your music performance at a glance</p></div>
          <button onClick={() => setShowUpload(true)} data-testid="upload-song-button" className="flex items-center gap-2 bg-spotify-green hover:bg-spotify-green-hover text-black font-bold px-6 py-2.5 rounded-full transition-all duration-200 hover:scale-[1.03] active:scale-[0.97] text-sm"><Upload className="w-4 h-4" /> Upload Track</button>
        </motion.div>
        {activeTab === 'overview' && <OverviewTab songs={songs} analytics={analytics} chartData={chartData} totalPlays={totalPlays} totalLikes={totalLikes} totalComments={totalComments} onSubmit={submitSong} />}
        {activeTab === 'songs' && <SongsTab songs={songs} analytics={analytics} onSubmit={submitSong} />}
        {activeTab === 'analytics' && <AnalyticsTab analytics={analytics} chartData={chartData} totalPlays={totalPlays} totalLikes={totalLikes} totalComments={totalComments} totalSaves={totalSaves} />}
      </div>
      <UploadModal open={showUpload} onClose={() => setShowUpload(false)} onUpload={uploadSong} />
    </DashboardLayout>
  );
}
