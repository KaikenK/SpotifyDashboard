<template>
  <div style="background-color: #121212; min-height: 100vh;">
    <nav class="navbar px-4 py-3" style="background-color: #000000;">
      <span class="navbar-brand text-white fw-bold fs-5">Spotify Dashboard</span>
      <div class="d-flex gap-3 align-items-center">
        <span class="text-secondary">Fan View</span>
        <button @click="logout" class="btn btn-sm btn-outline-light">Logout</button>
      </div>
    </nav>

    <div class="container-fluid px-4 py-4">
      <h4 class="text-white mb-4">Browse Songs</h4>

      <div class="row g-3">
        <div class="col-12 col-md-6 col-lg-4" v-for="song in songs" :key="song.id">
          <div class="rounded-3 p-3" style="background-color: #282828;">
            <div class="d-flex justify-content-between align-items-start mb-2">
              <div>
                <h6 class="text-white mb-0">{{ song.title }}</h6>
                <small class="text-secondary">{{ song.artist?.username }}</small>
              </div>
              <small class="text-secondary">{{ formatDuration(song.durationSec) }}</small>
            </div>
            <div class="d-flex gap-2 mt-3">
              <button @click="recordPlay(song.id)"
                      class="btn btn-sm fw-bold text-dark flex-grow-1"
                      style="background-color: #1DB954;">
                Play
              </button>
              <button @click="recordLike(song.id)"
                      class="btn btn-sm btn-outline-light">
                Like
              </button>
            </div>
          </div>
        </div>
      </div>

      <div v-if="songs.length === 0" class="text-secondary text-center mt-5">
        No published songs yet.
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import api from '../api/axios.js'

const router = useRouter()
const songs = ref([])

onMounted(async () => {
  const res = await api.get('/songs/published')
  songs.value = res.data
})

async function recordPlay(id) {
  await api.post(`/analytics/play/${id}`)
}
async function recordLike(id) {
  await api.post(`/analytics/like/${id}`)
}
function formatDuration(sec) {
  if (!sec) return '-'
  const m = Math.floor(sec / 60)
  const s = sec % 60
  return `${m}:${s.toString().padStart(2, '0')}`
}
function logout() {
  localStorage.clear()
  router.push('/login')
}
</script>
