import React, { useState } from 'react';
import { motion } from 'framer-motion';
import { Shield, CheckCircle2, XCircle, Users, AlertCircle } from 'lucide-react';
import useAdminData from '../hooks/useAdminData';
import DashboardLayout from '../components/DashboardLayout';

const TABS = [
  { id: 'pending', label: 'Pending Songs' },
  { id: 'artists', label: 'Unverified Artists' },
  { id: 'comments', label: 'Moderate Comments' },
  { id: 'reports', label: 'Reports' },
];
const stagger = { hidden: {}, visible: { transition: { staggerChildren: 0.06 } } };
const fadeUp = { hidden: { opacity: 0, y: 12 }, visible: { opacity: 1, y: 0, transition: { duration: 0.35 } } };

function fmtDur(sec) { if (!sec) return '--:--'; return `${Math.floor(sec / 60)}:${(sec % 60).toString().padStart(2, '0')}`; }

/* Java Song entity: song.id, song.title, song.durationSec, song.artist?.username */

function AdminStatCard({ icon: Icon, label, value, color }) {
  return (
    <motion.div variants={fadeUp} className="bg-spotify-surface border border-white/[0.08] rounded-xl p-5">
      <div className="flex items-center gap-3">
        <div className={`w-10 h-10 rounded-lg ${color} flex items-center justify-center`}><Icon className="w-5 h-5" /></div>
        <div><p className="text-spotify-text text-xs font-bold uppercase tracking-wider">{label}</p><p className="font-outfit text-2xl font-black text-white" data-testid={`${label.toLowerCase().replace(/\s/g, '-')}-count`}>{value}</p></div>
      </div>
    </motion.div>
  );
}

function PendingSongRow({ song, onApprove, onReject }) {
  return (
    <motion.div variants={fadeUp} className="flex items-center gap-4 px-5 py-4 hover:bg-white/[0.03] transition-colors" data-testid={`pending-song-${song.id}`}>
      <img src={song.coverArt || 'https://images.unsplash.com/photo-1587659927818-f89edd4db555?w=80'} alt="" className="w-12 h-12 rounded-lg object-cover" />
      <div className="flex-1 min-w-0">
        <p className="text-white text-sm font-semibold truncate">{song.title}</p>
        <p className="text-spotify-muted text-xs">by {song.artist?.username || 'Unknown'} &middot; {fmtDur(song.durationSec)}</p>
      </div>
      <div className="flex items-center gap-2">
        <button onClick={() => onApprove(song.id)} data-testid={`approve-song-${song.id}`} className="flex items-center gap-1.5 bg-spotify-green/10 text-spotify-green px-4 py-2 rounded-full text-xs font-bold hover:bg-spotify-green/20 transition-colors"><CheckCircle2 className="w-3.5 h-3.5" /> Approve</button>
        <button onClick={() => onReject(song.id)} data-testid={`reject-song-${song.id}`} className="flex items-center gap-1.5 bg-spotify-destructive/10 text-spotify-destructive px-4 py-2 rounded-full text-xs font-bold hover:bg-spotify-destructive/20 transition-colors"><XCircle className="w-3.5 h-3.5" /> Reject</button>
      </div>
    </motion.div>
  );
}

function ArtistRow({ artist, onVerify }) {
  return (
    <motion.div variants={fadeUp} className="flex items-center gap-4 px-5 py-4 hover:bg-white/[0.03] transition-colors" data-testid={`unverified-artist-${artist.id}`}>
      <div className="w-10 h-10 rounded-full bg-spotify-elevated flex items-center justify-center text-sm font-bold text-spotify-green">{artist.username?.[0]?.toUpperCase() || '?'}</div>
      <div className="flex-1 min-w-0"><p className="text-white text-sm font-semibold">{artist.username}</p><p className="text-spotify-muted text-xs">{artist.email}</p></div>
      <button onClick={() => onVerify(artist.id)} data-testid={`verify-artist-${artist.id}`} className="flex items-center gap-1.5 bg-spotify-green/10 text-spotify-green px-4 py-2 rounded-full text-xs font-bold hover:bg-spotify-green/20 transition-colors"><CheckCircle2 className="w-3.5 h-3.5" /> Verify</button>
    </motion.div>
  );
}

function Empty({ msg }) { return <div className="p-12 text-center"><CheckCircle2 className="w-12 h-12 text-spotify-green/40 mx-auto mb-3" /><p className="text-spotify-text">{msg}</p></div>; }

export default function AdminPanel() {
  const [activeTab, setActiveTab] = useState('pending');
  const { pending, artists, comments, report, approveSong, rejectSong, verifyArtist, removeComment } = useAdminData();
  return (
    <DashboardLayout activeTab={activeTab} setActiveTab={setActiveTab} tabs={TABS}>
      <div className="p-6 md:p-10 lg:p-12" data-testid="admin-panel">
        <motion.div initial={{ opacity: 0, y: -10 }} animate={{ opacity: 1, y: 0 }}>
          <div className="flex items-center gap-3 mb-1"><Shield className="w-6 h-6 text-spotify-green" /><h1 className="font-outfit text-3xl font-bold text-white tracking-tight">Admin Panel</h1></div>
          <p className="text-spotify-text text-sm mb-8">Manage song approvals and artist verifications</p>
        </motion.div>
        <motion.div initial="hidden" animate="visible" variants={stagger} className="grid grid-cols-1 sm:grid-cols-2 gap-4 mb-8">
          <AdminStatCard icon={AlertCircle} label="Pending Songs" value={pending.length} color="bg-spotify-warning/15 text-spotify-warning" />
          <AdminStatCard icon={Users} label="Unverified Artists" value={artists.length} color="bg-blue-500/15 text-blue-400" />
        </motion.div>
        {activeTab === 'pending' && (
          <motion.div initial="hidden" animate="visible" variants={stagger}>
            <motion.div variants={fadeUp} className="bg-spotify-surface border border-white/[0.08] rounded-xl overflow-hidden">
              <div className="p-5 border-b border-white/[0.05]"><h3 className="font-outfit text-lg font-semibold text-white">Songs Awaiting Approval</h3></div>
              {pending.length === 0 ? <Empty msg="All caught up! No pending songs." /> : <div className="divide-y divide-white/[0.05]">{pending.map(s => <PendingSongRow key={s.id} song={s} onApprove={approveSong} onReject={rejectSong} />)}</div>}
            </motion.div>
          </motion.div>
        )}
        {activeTab === 'artists' && (
          <motion.div initial="hidden" animate="visible" variants={stagger}>
            <motion.div variants={fadeUp} className="bg-spotify-surface border border-white/[0.08] rounded-xl overflow-hidden">
              <div className="p-5 border-b border-white/[0.05]"><h3 className="font-outfit text-lg font-semibold text-white">Artists Awaiting Verification</h3></div>
              {artists.length === 0 ? <Empty msg="All artists are verified!" /> : <div className="divide-y divide-white/[0.05]">{artists.map(a => <ArtistRow key={a.id} artist={a} onVerify={verifyArtist} />)}</div>}
            </motion.div>
          </motion.div>
        )}
        {activeTab === 'comments' && (
          <motion.div initial="hidden" animate="visible" variants={stagger}>
            <motion.div variants={fadeUp} className="bg-spotify-surface border border-white/[0.08] rounded-xl overflow-hidden">
              <div className="p-5 border-b border-white/[0.05]"><h3 className="font-outfit text-lg font-semibold text-white">Comment Moderation</h3></div>
              {comments.length === 0 ? <Empty msg="No active comments to moderate." /> : (
                <div className="divide-y divide-white/[0.05]">
                  {comments.map(c => (
                    <div key={c.id} className="px-5 py-4 flex items-center gap-4">
                      <div className="flex-1">
                        <p className="text-white text-sm">{c.content}</p>
                        <p className="text-spotify-muted text-xs mt-1">Fan: {c.fanUsername}</p>
                      </div>
                      <button onClick={() => removeComment(c.id)} className="px-3 py-1.5 rounded-full bg-spotify-destructive/10 text-spotify-destructive text-xs font-bold hover:bg-spotify-destructive/20">Remove</button>
                    </div>
                  ))}
                </div>
              )}
            </motion.div>
          </motion.div>
        )}
        {activeTab === 'reports' && (
          <motion.div initial="hidden" animate="visible" variants={stagger}>
            <motion.div variants={fadeUp} className="bg-spotify-surface border border-white/[0.08] rounded-xl p-6">
              <h3 className="font-outfit text-lg font-semibold text-white mb-4">System Report</h3>
              {!report ? <p className="text-spotify-muted text-sm">Report unavailable.</p> : (
                <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-3 text-sm">
                  <div className="bg-spotify-elevated rounded-lg p-3 text-white">Users: {report.totalUsers}</div>
                  <div className="bg-spotify-elevated rounded-lg p-3 text-white">Artists: {report.totalArtists}</div>
                  <div className="bg-spotify-elevated rounded-lg p-3 text-white">Fans: {report.totalFans}</div>
                  <div className="bg-spotify-elevated rounded-lg p-3 text-white">Songs: {report.totalSongs}</div>
                  <div className="bg-spotify-elevated rounded-lg p-3 text-white">Published: {report.publishedSongs}</div>
                  <div className="bg-spotify-elevated rounded-lg p-3 text-white">Pending: {report.pendingSongs}</div>
                  <div className="bg-spotify-elevated rounded-lg p-3 text-white">Comments: {report.totalComments}</div>
                  <div className="bg-spotify-elevated rounded-lg p-3 text-white">Subscriptions: {report.activeSubscriptions}</div>
                </div>
              )}
            </motion.div>
          </motion.div>
        )}
      </div>
    </DashboardLayout>
  );
}
