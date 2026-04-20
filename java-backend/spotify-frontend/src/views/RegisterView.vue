<template>
  <div class="min-h-screen flex items-center justify-center"
       style="background-color: #121212;">
    <div class="p-5 rounded-4" style="width: 420px; background-color: #282828;">

      <h1 class="text-white fw-bold fs-3 text-center mb-1">Create Account</h1>
      <p class="text-secondary text-center mb-4">Join Spotify Dashboard</p>

      <div v-if="error" class="alert alert-danger py-2">{{ error }}</div>
      <div v-if="success" class="alert alert-success py-2">{{ success }}</div>

      <div class="mb-3">
        <label class="text-white mb-1">Username</label>
        <input v-model="form.username" type="text"
               class="form-control bg-dark text-white border-secondary"
               placeholder="your_username" />
      </div>
      <div class="mb-3">
        <label class="text-white mb-1">Email</label>
        <input v-model="form.email" type="email"
               class="form-control bg-dark text-white border-secondary"
               placeholder="you@example.com" />
      </div>
      <div class="mb-3">
        <label class="text-white mb-1">Password</label>
        <input v-model="form.password" type="password"
               class="form-control bg-dark text-white border-secondary"
               placeholder="••••••••" />
      </div>
      <div class="mb-4">
        <label class="text-white mb-1">I am a...</label>
        <select v-model="form.role"
                class="form-select bg-dark text-white border-secondary">
          <option value="FAN">Fan</option>
          <option value="ARTIST">Artist</option>
        </select>
      </div>

      <button @click="register" :disabled="loading"
              class="btn w-100 fw-bold text-dark"
              style="background-color: #1DB954;">
        {{ loading ? 'Creating...' : 'Create Account' }}
      </button>

      <p class="text-center text-secondary mt-3 mb-0">
        Already have an account?
        <router-link to="/login" style="color: #1DB954;"
                     class="text-decoration-none">Sign in</router-link>
      </p>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import api from '../api/axios.js'

const form = ref({ username: '', email: '', password: '', role: 'FAN' })
const error = ref('')
const success = ref('')
const loading = ref(false)

async function register() {
  loading.value = true
  error.value = ''
  success.value = ''
  try {
    await api.post('/auth/register', form.value)
    success.value = 'Account created! Artists need admin approval before logging in.'
    form.value = { username: '', email: '', password: '', role: 'FAN' }
  } catch (e) {
    error.value = e.response?.data?.message || 'Registration failed'
  } finally {
    loading.value = false
  }
}
</script>
