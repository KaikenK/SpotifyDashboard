<template>
  <div style="background-color: #121212; min-height: 100vh;">

    <!-- Navbar -->
    <nav class="navbar px-4 py-3" style="background-color: #000000;">
      <span class="navbar-brand text-white fw-bold fs-5">Spotify Dashboard</span>
      <div class="d-flex align-items-center gap-3">
        <span class="text-secondary">Artist View</span>
        <button @click="logout" class="btn btn-sm btn-outline-light">Logout</button>
      </div>
    </nav>

    <div class="container-fluid px-4 py-4">

      <!-- Stats row -->
      <div class="row g-3 mb-4">
        <div class="col-6 col-md-3" v-for="stat in stats" :key="stat.label">
          <div class="rounded-3 p-3" style="background-color: #282828;">
            <p class="text-secondary mb-1" style="font-size: 13px;">{{ stat.label }}</p>
            <h3 class="text-white fw-bold mb-0">{{ stat.value }}</h3>
          </div>
        </div>
      </div>

      <!-- Songs table -->
      <div class="rounded-3 p-4 mb-4" style="background-color: #282828;">
        <div class="d-flex justify-content-between align-items-center mb-3">
          <h5 class="text-white mb-0">My Songs</h5>
          <button class="btn btn-sm fw-bold text-dark"
                  style="background-color: #1DB954;"
                  data-bs-toggle="modal" data-bs-target="#uploadModal">
            + Upload Song
          </button>
        </div>

        <div v-if="songs.length === 0" class="text-secondary text-center py-4">
          No songs yet. Upload your first track!
        </div>

        <table v-else class="table table-dark table-hover mb-0">
          <thead>
          <tr class="text-secondary">
            <th>Title</th>
            <th>Duration</th>
            <th>Status</th>
            <th>Plays</th>
            <th>Likes</th>
            <th>Action</th>
          </tr>
          </thead>
          <tbody>
          <tr v-for="song in songs" :key="song.id">
            <td class="text-white">{{ song.title }}</td>
            <td class="text-secondary">{{ formatDuration(song.durationSec) }}</td>
            <td>
                <span class="badge rounded-pill"
                      :style="statusStyle(song.status)">
                  {{ song.status }}
                </span>
            </td>
            <td class="text-white">{{ getAnalytics(song.id)?.totalPlays ?? '-' }}</td>
            <td class="text-white">{{ getAnalytics(song.id)?.totalLikes ?? '-' }}</td>
            <td>
              <button v-if="song.status === 'UPLOADED'"
                      @click="submitSong(song.id)"
                      class="btn btn-sm btn-outline-light">
                Submit
              </button>
            </td>
          </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- Upload Modal -->
    <div class="modal fade" id="uploadModal" tabindex="-1">
      <div class="modal-dialog">
        <div class="modal-content" style="background-color: #282828;">
          <div class="modal-header border-secondary">
            <h5 class="modal-title text-white">Upload New Song</h5>
            <button type="button" class="btn-close btn-close-white"
                    data-bs-dismiss="modal"></button>
          </div>
          <div class="modal-body">
            <div class="mb-3">
              <label class="text-white mb-1">Song Title</label>
              <input v-model="newSong.title" type="text"
                     class="form-control bg-dark text-white border-secondary"
                     placeholder="Enter song title" />
            </div>
            <div class="mb-3">
              <label class="text-white mb-1">Duration (seconds)</label>
              <input v-model="newSong.durationSec" type="number"
                     class="form-control bg-dark text-white border-secondary"
                     placeholder="e.g. 210" />
            </div>
          </div>
          <div class="modal-footer border-secondary">
            <button type="button" class="btn btn-secondary"
                    data-bs-dismiss="modal">Cancel</button>
            <button @click="uploadSong"
                    class="btn fw-bold text-dark"
                    style="background-color: #1DB954;">
              Upload
            </button>
          </div>
        </div>
      </div>
    </div>

  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import api from '../api/axios.js'

const router = useRouter()
const songs = ref([])
const analyticsMap = ref({})
const newSong = ref({ title: '', durationSec: '' })

const stats = computed(() => {
  const allAnalytics = Object.values(analyticsMap.value)
  return [
    { label: 'Total Songs', value: songs.value.length },
    { label: 'Total Plays', value: allAnalytics.reduce((s, a) => s + (a.totalPlays || 0), 0) },
    { label: 'Total Likes', value: allAnalytics.reduce((s, a) => s + (a.totalLikes || 0), 0) },
    { label: 'Total Comments', value: allAnalytics.reduce((s, a) => s + (a.totalComments || 0), 0) }
  ]
})

onMounted(async () => {
  await loadSongs()
})

async function loadSongs() {
  try {
    const res = await api.get('/songs/my')
    songs.value = res.data
    for (const song of songs.value) {
      try {
        const aRes = await api.get(`/analytics/song/${song.id}`)
        analyticsMap.value[song.id] = aRes.data
      } catch {}
    }
  } catch (e) {
    console.error(e)
  }
}

function getAnalytics(songId) {
  return analyticsMap.value[songId]
}

async function uploadSong() {
  try {
    await api.post(`/songs/upload?title=${newSong.value.title}&durationSec=${newSong.value.durationSec}`)
    newSong.value = { title: '', durationSec: '' }
    await loadSongs()
  } catch (e) {
    console.error(e)
  }
}

async function submitSong(id) {
  try {
    await api.put(`/songs/${id}/submit`)
    await loadSongs()
  } catch (e) {
    console.error(e)
  }
}

function formatDuration(sec) {
  if (!sec) return '-'
  const m = Math.floor(sec / 60)
  const s = sec % 60
  return `${m}:${s.toString().padStart(2, '0')}`
}

function statusStyle(status) {
  const map = {
    UPLOADED: 'background:#535353;color:#fff',
    PENDING_APPROVAL: 'background:#b87333;color:#fff',
    PUBLISHED: 'background:#1DB954;color:#000',
    ACTIVE: 'background:#1DB954;color:#000',
    REJECTED: 'background:#e22134;color:#fff',
    UNPUBLISHED: 'background:#535353;color:#fff'
  }
  return map[status] || 'background:#535353;color:#fff'
}

function logout() {
  localStorage.clear()
  router.push('/login')
}
</script>
