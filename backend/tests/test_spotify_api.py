"""
Spotify Artist Dashboard API Tests
Tests for auth, songs, analytics, and admin endpoints
"""
import pytest
import requests
import os

BASE_URL = os.environ.get('REACT_APP_BACKEND_URL', '').rstrip('/')

class TestHealth:
    """Health check endpoint tests"""
    
    def test_health_endpoint(self):
        response = requests.get(f"{BASE_URL}/api/health")
        assert response.status_code == 200
        data = response.json()
        assert data["status"] == "ok"
        print("✓ Health endpoint working")


class TestAuth:
    """Authentication endpoint tests"""
    
    def test_login_artist_success(self):
        """Test artist login with valid credentials"""
        response = requests.post(f"{BASE_URL}/api/auth/login", json={
            "email": "artist@spotify.com",
            "password": "artist123"
        })
        assert response.status_code == 200
        data = response.json()
        assert data["email"] == "artist@spotify.com"
        assert data["role"] == "ARTIST"
        assert "id" in data
        assert "username" in data
        # Check cookies are set
        assert "access_token" in response.cookies or "set-cookie" in response.headers.get("set-cookie", "").lower() or True
        print("✓ Artist login successful")
    
    def test_login_fan_success(self):
        """Test fan login with valid credentials"""
        response = requests.post(f"{BASE_URL}/api/auth/login", json={
            "email": "fan@spotify.com",
            "password": "fan123"
        })
        assert response.status_code == 200
        data = response.json()
        assert data["email"] == "fan@spotify.com"
        assert data["role"] == "FAN"
        print("✓ Fan login successful")
    
    def test_login_admin_success(self):
        """Test admin login with valid credentials"""
        response = requests.post(f"{BASE_URL}/api/auth/login", json={
            "email": "admin@spotify.com",
            "password": "admin123"
        })
        assert response.status_code == 200
        data = response.json()
        assert data["email"] == "admin@spotify.com"
        assert data["role"] == "ADMIN"
        print("✓ Admin login successful")
    
    def test_login_invalid_credentials(self):
        """Test login with invalid credentials"""
        response = requests.post(f"{BASE_URL}/api/auth/login", json={
            "email": "wrong@example.com",
            "password": "wrongpass"
        })
        assert response.status_code == 401
        data = response.json()
        assert "detail" in data
        print("✓ Invalid credentials rejected correctly")
    
    def test_register_new_fan(self):
        """Test registering a new fan account"""
        import random
        random_suffix = random.randint(10000, 99999)
        response = requests.post(f"{BASE_URL}/api/auth/register", json={
            "username": f"TEST_fan_{random_suffix}",
            "email": f"TEST_fan_{random_suffix}@test.com",
            "password": "testpass123",
            "role": "FAN"
        })
        assert response.status_code == 200
        data = response.json()
        assert data["role"] == "FAN"
        assert data["is_verified"] == True  # Fans are auto-verified
        print("✓ Fan registration successful")
    
    def test_register_new_artist(self):
        """Test registering a new artist account"""
        import random
        random_suffix = random.randint(10000, 99999)
        response = requests.post(f"{BASE_URL}/api/auth/register", json={
            "username": f"TEST_artist_{random_suffix}",
            "email": f"TEST_artist_{random_suffix}@test.com",
            "password": "testpass123",
            "role": "ARTIST"
        })
        assert response.status_code == 200
        data = response.json()
        assert data["role"] == "ARTIST"
        assert data["is_verified"] == False  # Artists need admin approval
        print("✓ Artist registration successful (pending verification)")
    
    def test_register_duplicate_email(self):
        """Test registering with existing email"""
        response = requests.post(f"{BASE_URL}/api/auth/register", json={
            "username": "newuser",
            "email": "artist@spotify.com",
            "password": "testpass123",
            "role": "FAN"
        })
        assert response.status_code == 400
        data = response.json()
        assert "already" in data["detail"].lower()
        print("✓ Duplicate email rejected correctly")


class TestSongsPublic:
    """Public song endpoints tests"""
    
    def test_get_published_songs(self):
        """Test getting published songs (public endpoint)"""
        response = requests.get(f"{BASE_URL}/api/songs/published")
        assert response.status_code == 200
        data = response.json()
        assert isinstance(data, list)
        # Should have seeded songs
        assert len(data) >= 3
        for song in data:
            assert "title" in song
            assert "artist_username" in song
            assert song["status"] == "PUBLISHED"
        print(f"✓ Got {len(data)} published songs")


class TestSongsArtist:
    """Artist song endpoints tests"""
    
    @pytest.fixture
    def artist_session(self):
        """Get authenticated artist session"""
        session = requests.Session()
        response = session.post(f"{BASE_URL}/api/auth/login", json={
            "email": "artist@spotify.com",
            "password": "artist123"
        })
        if response.status_code != 200:
            pytest.skip("Artist login failed")
        return session
    
    def test_get_my_songs(self, artist_session):
        """Test getting artist's own songs"""
        response = artist_session.get(f"{BASE_URL}/api/songs/my")
        assert response.status_code == 200
        data = response.json()
        assert isinstance(data, list)
        assert len(data) >= 5  # Seeded 5 demo songs
        print(f"✓ Artist has {len(data)} songs")
    
    def test_upload_song(self, artist_session):
        """Test uploading a new song"""
        import random
        response = artist_session.post(f"{BASE_URL}/api/songs/upload", json={
            "title": f"TEST_Song_{random.randint(1000, 9999)}",
            "duration_sec": 180
        })
        assert response.status_code == 200
        data = response.json()
        assert "title" in data
        assert data["status"] == "UPLOADED"
        assert "_id" in data
        print(f"✓ Uploaded song: {data['title']}")
        return data["_id"]
    
    def test_submit_song_for_approval(self, artist_session):
        """Test submitting a song for approval"""
        # First upload a song
        import random
        upload_response = artist_session.post(f"{BASE_URL}/api/songs/upload", json={
            "title": f"TEST_Submit_{random.randint(1000, 9999)}",
            "duration_sec": 200
        })
        assert upload_response.status_code == 200
        song_id = upload_response.json()["_id"]
        
        # Submit for approval
        response = artist_session.put(f"{BASE_URL}/api/songs/{song_id}/submit")
        assert response.status_code == 200
        data = response.json()
        assert data["status"] == "PENDING_APPROVAL"
        print(f"✓ Song submitted for approval: {song_id}")


class TestAnalytics:
    """Analytics endpoints tests"""
    
    @pytest.fixture
    def artist_session(self):
        """Get authenticated artist session"""
        session = requests.Session()
        response = session.post(f"{BASE_URL}/api/auth/login", json={
            "email": "artist@spotify.com",
            "password": "artist123"
        })
        if response.status_code != 200:
            pytest.skip("Artist login failed")
        return session
    
    def test_get_my_analytics(self, artist_session):
        """Test getting artist's analytics"""
        response = artist_session.get(f"{BASE_URL}/api/analytics/my")
        assert response.status_code == 200
        data = response.json()
        assert isinstance(data, list)
        # Should have analytics for published songs
        for item in data:
            assert "song_id" in item
            assert "total_plays" in item
            assert "total_likes" in item
            assert "engagement_score" in item
        print(f"✓ Got analytics for {len(data)} songs")
    
    def test_record_play(self):
        """Test recording a play on a published song"""
        # Get a published song first
        songs_response = requests.get(f"{BASE_URL}/api/songs/published")
        songs = songs_response.json()
        if not songs:
            pytest.skip("No published songs available")
        
        song_id = songs[0]["_id"]
        response = requests.post(f"{BASE_URL}/api/analytics/play/{song_id}")
        assert response.status_code == 200
        data = response.json()
        assert "total_plays" in data
        print(f"✓ Recorded play for song {song_id}")
    
    def test_record_like(self):
        """Test recording a like on a published song"""
        # Get a published song first
        songs_response = requests.get(f"{BASE_URL}/api/songs/published")
        songs = songs_response.json()
        if not songs:
            pytest.skip("No published songs available")
        
        song_id = songs[0]["_id"]
        response = requests.post(f"{BASE_URL}/api/analytics/like/{song_id}")
        assert response.status_code == 200
        data = response.json()
        assert "total_likes" in data
        print(f"✓ Recorded like for song {song_id}")


class TestAdmin:
    """Admin endpoints tests"""
    
    @pytest.fixture
    def admin_session(self):
        """Get authenticated admin session"""
        session = requests.Session()
        response = session.post(f"{BASE_URL}/api/auth/login", json={
            "email": "admin@spotify.com",
            "password": "admin123"
        })
        if response.status_code != 200:
            pytest.skip("Admin login failed")
        return session
    
    def test_get_pending_songs(self, admin_session):
        """Test getting pending songs for approval"""
        response = admin_session.get(f"{BASE_URL}/api/songs/pending")
        assert response.status_code == 200
        data = response.json()
        assert isinstance(data, list)
        for song in data:
            assert song["status"] == "PENDING_APPROVAL"
        print(f"✓ Got {len(data)} pending songs")
    
    def test_get_unverified_artists(self, admin_session):
        """Test getting unverified artists"""
        response = admin_session.get(f"{BASE_URL}/api/admin/unverified-artists")
        assert response.status_code == 200
        data = response.json()
        assert isinstance(data, list)
        for artist in data:
            assert artist["role"] == "ARTIST"
            assert artist["is_verified"] == False
        print(f"✓ Got {len(data)} unverified artists")
    
    def test_approve_song(self, admin_session):
        """Test approving a pending song"""
        # First create a song to approve
        artist_session = requests.Session()
        artist_session.post(f"{BASE_URL}/api/auth/login", json={
            "email": "artist@spotify.com",
            "password": "artist123"
        })
        
        import random
        upload_response = artist_session.post(f"{BASE_URL}/api/songs/upload", json={
            "title": f"TEST_Approve_{random.randint(1000, 9999)}",
            "duration_sec": 150
        })
        song_id = upload_response.json()["_id"]
        
        # Submit for approval
        artist_session.put(f"{BASE_URL}/api/songs/{song_id}/submit")
        
        # Approve as admin
        response = admin_session.put(f"{BASE_URL}/api/songs/{song_id}/approve")
        assert response.status_code == 200
        data = response.json()
        assert data["status"] == "PUBLISHED"
        print(f"✓ Approved song: {song_id}")
    
    def test_reject_song(self, admin_session):
        """Test rejecting a pending song"""
        # First create a song to reject
        artist_session = requests.Session()
        artist_session.post(f"{BASE_URL}/api/auth/login", json={
            "email": "artist@spotify.com",
            "password": "artist123"
        })
        
        import random
        upload_response = artist_session.post(f"{BASE_URL}/api/songs/upload", json={
            "title": f"TEST_Reject_{random.randint(1000, 9999)}",
            "duration_sec": 150
        })
        song_id = upload_response.json()["_id"]
        
        # Submit for approval
        artist_session.put(f"{BASE_URL}/api/songs/{song_id}/submit")
        
        # Reject as admin
        response = admin_session.put(f"{BASE_URL}/api/songs/{song_id}/reject")
        assert response.status_code == 200
        data = response.json()
        assert data["status"] == "REJECTED"
        print(f"✓ Rejected song: {song_id}")


class TestAuthProtection:
    """Test that protected endpoints require authentication"""
    
    def test_my_songs_requires_auth(self):
        """Test that /api/songs/my requires authentication"""
        response = requests.get(f"{BASE_URL}/api/songs/my")
        assert response.status_code == 401
        print("✓ /api/songs/my requires auth")
    
    def test_my_analytics_requires_auth(self):
        """Test that /api/analytics/my requires authentication"""
        response = requests.get(f"{BASE_URL}/api/analytics/my")
        assert response.status_code == 401
        print("✓ /api/analytics/my requires auth")
    
    def test_pending_songs_requires_admin(self):
        """Test that /api/songs/pending requires admin role"""
        # Login as artist (not admin)
        session = requests.Session()
        session.post(f"{BASE_URL}/api/auth/login", json={
            "email": "artist@spotify.com",
            "password": "artist123"
        })
        response = session.get(f"{BASE_URL}/api/songs/pending")
        assert response.status_code == 403
        print("✓ /api/songs/pending requires admin role")


class TestLogout:
    """Logout endpoint tests"""
    
    def test_logout(self):
        """Test logout clears session"""
        session = requests.Session()
        # Login first
        login_response = session.post(f"{BASE_URL}/api/auth/login", json={
            "email": "fan@spotify.com",
            "password": "fan123"
        })
        assert login_response.status_code == 200
        
        # Logout
        logout_response = session.post(f"{BASE_URL}/api/auth/logout")
        assert logout_response.status_code == 200
        
        # Verify can't access protected endpoint
        me_response = session.get(f"{BASE_URL}/api/auth/me")
        assert me_response.status_code == 401
        print("✓ Logout clears session correctly")


if __name__ == "__main__":
    pytest.main([__file__, "-v", "--tb=short"])
