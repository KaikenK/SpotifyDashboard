import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      redirect: '/login'
    },
    {
      path: '/login',
      name: 'login',
      component: () => import('../views/LoginView.vue')
    },
    {
      path: '/register',
      name: 'register',
      component: () => import('../views/RegisterView.vue')
    },
    {
      path: '/dashboard',
      name: 'dashboard',
      component: () => import('../views/ArtistDashboard.vue'),
      meta: { requiresAuth: true, role: 'ARTIST' }
    },
    {
      path: '/browse',
      name: 'browse',
      component: () => import('../views/BrowseView.vue'),
      meta: { requiresAuth: true, role: 'FAN' }
    },
    {
      path: '/admin',
      name: 'admin',
      component: () => import('../views/AdminView.vue'),
      meta: { requiresAuth: true, role: 'ADMIN' }
    }
  ]
})

// Route guard — protect pages that need login
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  const role = localStorage.getItem('role')

  if (to.meta.requiresAuth && !token) {
    next('/login')
  } else if (to.meta.role && to.meta.role !== role) {
    next('/login')
  } else {
    next()
  }
})

export default router
