import React from 'react';
import { useAuth } from '../context/AuthContext';
import { Home, Music, BarChart3, Settings, LogOut, Shield, Users, Radio, Disc3, LineChart, MessageSquare, FileBarChart2 } from 'lucide-react';
import { motion } from 'framer-motion';

export default function Sidebar({ activeTab, setActiveTab, tabs }) {
  const { user, logout } = useAuth();

  const iconMap = {
    overview: Home,
    songs: Music,
    analytics: BarChart3,
    browse: Radio,
    pending: Shield,
    artists: Users,
    albums: Disc3,
    insights: LineChart,
    comments: MessageSquare,
    reports: FileBarChart2,
    settings: Settings,
  };

  return (
    <motion.aside
      initial={{ x: -30, opacity: 0 }}
      animate={{ x: 0, opacity: 1 }}
      transition={{ duration: 0.4 }}
      className="fixed left-0 top-0 bottom-0 w-64 bg-spotify-black border-r border-white/[0.08] flex flex-col z-40"
      data-testid="sidebar"
    >
      <div className="p-6 pb-4">
        <div className="flex items-center gap-3">
          <div className="w-9 h-9 rounded-full bg-spotify-green flex items-center justify-center">
            <Music className="w-5 h-5 text-black" />
          </div>
          <span className="font-outfit font-bold text-lg text-white tracking-tight">Soundwave</span>
        </div>
      </div>

      <nav className="flex-1 px-3 space-y-1">
        {tabs.map((tab) => {
          const Icon = iconMap[tab.id] || Home;
          const isActive = activeTab === tab.id;
          return (
            <button
              key={tab.id}
              onClick={() => setActiveTab(tab.id)}
              data-testid={`nav-${tab.id}`}
              className={`w-full flex items-center gap-3 px-3 py-2.5 rounded-lg text-sm font-medium transition-all duration-200 group ${
                isActive
                  ? 'bg-white/[0.1] text-white'
                  : 'text-spotify-text hover:text-white hover:bg-white/[0.05]'
              }`}
            >
              <Icon className={`w-5 h-5 transition-colors ${isActive ? 'text-spotify-green' : 'text-spotify-text group-hover:text-white'}`} />
              {tab.label}
              {isActive && (
                <motion.div
                  layoutId="activeTab"
                  className="ml-auto w-1 h-4 rounded-full bg-spotify-green"
                />
              )}
            </button>
          );
        })}
      </nav>

      <div className="p-4 border-t border-white/[0.08]">
        <div className="flex items-center gap-3 mb-4 px-2">
          <div className="w-8 h-8 rounded-full bg-spotify-elevated flex items-center justify-center text-xs font-bold text-spotify-green">
            {user?.username?.[0]?.toUpperCase() || user?.email?.[0]?.toUpperCase() || 'U'}
          </div>
          <div className="flex-1 min-w-0">
            <p className="text-sm font-semibold text-white truncate">{user?.username || user?.email?.split('@')[0]}</p>
            <p className="text-xs text-spotify-muted truncate">{user?.role}</p>
          </div>
        </div>
        <button
          onClick={logout}
          data-testid="logout-button"
          className="w-full flex items-center gap-3 px-3 py-2 rounded-lg text-sm font-medium text-spotify-text hover:text-spotify-destructive hover:bg-white/[0.05] transition-all"
        >
          <LogOut className="w-4 h-4" />
          Sign Out
        </button>
      </div>
    </motion.aside>
  );
}
