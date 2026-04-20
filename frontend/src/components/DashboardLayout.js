import React from 'react';
import Sidebar from './Sidebar';

export default function DashboardLayout({ activeTab, setActiveTab, tabs, children }) {
  return (
    <div className="min-h-screen bg-spotify-black">
      <Sidebar activeTab={activeTab} setActiveTab={setActiveTab} tabs={tabs} />
      <main className="ml-64 min-h-screen">
        {children}
      </main>
    </div>
  );
}
