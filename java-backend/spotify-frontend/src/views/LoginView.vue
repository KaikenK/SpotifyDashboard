<template>
  <div class="min-h-screen flex items-center justify-center"
       style="background-color: #121212;">
    <div class="bg-dark p-5 rounded-4 shadow-lg" style="width: 400px; background-color: #282828;">

      <div class="text-center mb-4">
        <h1 class="text-white fw-bold fs-3">Spotify Dashboard</h1>
        <p class="text-secondary">Sign in to your account</p>
      </div>

      <div v-if="error" class="alert alert-danger py-2">{{ error }}</div>

      <div class="mb-3">
        <label class="text-white mb-1">Email</label>
        <input v-model="email" type="email"
               class="form-control bg-dark text-white border-secondary"
               placeholder="you@example.com" />
      </div>

      <div class="mb-4">
        <label class="text-white mb-1">Password</label>
        <input v-model="password" type="password"
               class="form-control bg-dark text-white border-secondary"
               placeholder="••••••••" />
      </div>

      <button @click="login" :disabled="loading"
              class="btn w-100 fw-bold text-dark"
              style="background-color: #1DB954;">
        {{ loading ? 'Signing in...' : 'Sign In' }}
      </button>

      <p class="text-center text-secondary mt-3 mb-0">
        No account?
        <router-link to="/register" class="text-decoration-none"
                     style="color: #1DB954;">Register</router-link>
      </p>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import api from '../api/axios.js'

const router = useRouter()
const email = ref('')
const password = ref('')
const error = ref('')
const loading = ref(false)

async function login() {
  loading.value = true
  error.value = ''
  try {
    const res = await api.post('/auth/login', {
      email: email.value,
      password: password.value
    })
    const token = res.data.token
    // Decode role from JWT payload
    const payload = JSON.parse(atob(token.split('.')[1]))
    localStorage.setItem('token', token)
    localStorage.setItem('role', payload.role)

    if (payload.role === 'ARTIST') router.push('/dashboard')
    else if (payload.role === 'ADMIN') router.push('/admin')
    else router.push('/browse')
  } catch (e) {
    error.value = e.response?.data?.message || 'Login failed'
  } finally {
    loading.value = false
  }
}
</script>
