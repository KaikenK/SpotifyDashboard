# Spotify Artist Dashboard - PRD

## Original Problem Statement
User has a Java Spring Boot MVC for a Spotify Artist Dashboard. Needs corresponding React frontend with high dynamics matching Spotify's aesthetic.

## Architecture
- **Backend**: FastAPI (Python) + MongoDB
- **Frontend**: React + TailwindCSS + Framer Motion + Recharts + Lucide React
- **Auth**: JWT via sessionStorage Bearer tokens, bcrypt password hashing, role-based access (ARTIST, FAN, ADMIN)
- **Database**: MongoDB (users, songs, song_analytics, login_attempts)

## What's Been Implemented (Jan 2026)
- Full-stack app replicating Spring Boot MVC in FastAPI + React
- JWT auth with sessionStorage tokens, brute force protection
- Artist Dashboard with overview, songs table, analytics charts
- Fan Browse with card grid, play/like, player bar
- Admin Panel with pending approvals, artist verification
- Demo data seeded (3 users, 5 songs with analytics)

## Code Review Fixes Applied (Jan 2026)
- **Security**: Switched from localStorage to sessionStorage for tokens (XSS mitigation)
- **Hook Dependencies**: All useCallback hooks use proper dependency arrays
- **Empty Error Handlers**: Added console.error logging to all catch blocks
- **Component Complexity**: Extracted custom hooks (useArtistData, useFanData, useAdminData) and sub-components (SongCard, PlayerBar, StatCard, etc.)
- **Backend Decomposition**: seed_admin split into _seed_user, _seed_demo_songs, _write_test_credentials
- **Secure Random**: Replaced random.randint with secrets.randbelow

## Backlog
- P0: Spotify API integration for real music data
- P1: Real music playback / audio streaming
- P1: Song comments system
- P2: Follower/subscription system, notifications
- P3: Real-time WebSocket updates
