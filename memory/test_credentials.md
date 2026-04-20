# Test Credentials

## Admin
- Email: admin@spotify.com
- Password: admin123
- Role: ADMIN

## Artist
- Email: artist@spotify.com
- Password: artist123
- Role: ARTIST

## Fan
- Email: fan@spotify.com
- Password: fan123
- Role: FAN

## API Endpoints (matches Java Spring Boot)
- POST /api/auth/register
- POST /api/auth/login
- GET /api/songs/published
- POST /api/songs/upload?title=X&durationSec=Y
- PUT /api/songs/{id}/submit
- GET /api/songs/my
- GET /api/songs/pending
- PUT /api/songs/{id}/approve
- GET /api/analytics/song/{songId}
- GET /api/analytics/my
- POST /api/analytics/play/{songId}
- POST /api/analytics/like/{songId}
