<template>
  <div style="background-color: #121212; min-height: 100vh;">
    <nav class="navbar px-4 py-3" style="background-color: #000000;">
      <span class="navbar-brand text-white fw-bold fs-5">Spotify Dashboard</span>
      <div class="d-flex gap-3 align-items-center">
        <span class="text-secondary">Admin View</span>
        <button @click="logout" class="btn btn-sm btn-outline-light">Logout</button>
      </div>
    </nav>

    <div class="container-fluid px-4 py-4">
      <h4 class="text-white mb-4">Pending Song Approvals</h4>

      <div v-if="pending.length === 0" class="text-secondary">
        No songs pending approval.
      </div>

      <div class="row g-3">
        <div class="col-12 col-md-6" v-for="song in pending" :key="song.id">
          <div class="rounded-3 p-3" style="background-color: #282828;">
            <h6 class="text-white mb-0">{{ song.title }}</h6>
            <small class="text-secondary">by {{ song.artist?.username }}</small>
            <div class="d-flex gap-2 mt-3">
              <button @click="approve(song.id)"
                      class="btn btn-sm fw-bold text-dark"
                      style="background-color: #1DB954;">
                Approve
              </button>
              <button @click="reject(song.id)"
                      class="btn btn-sm btn-danger">
                Reject
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import api from '../api/axios.js'

const router = useRouter()
const pending = ref([])

onMounted(async () => {
  const res = await api.get('/songs/pending')
  pending.value = res.data
})

async function approve(id) {
  await api.put(`/songs/${id}/approve`)
  pending.value = pending.value.filter(s => s.id !== id)
}
async function reject(id) {
  await api.put(`/songs/${id}/reject`)
  pending.value = pending.value.filter(s => s.id !== id)
}
function logout() {
  localStorage.clear()
  router.push('/login')
}
</script>
