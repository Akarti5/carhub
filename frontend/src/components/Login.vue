<template>
  <div class="min-h-screen flex items-center justify-center gradient-orange">
    <div class="login-animation card w-full max-w-md mx-4">
      <div class="p-8">
        <div class="text-center mb-8">
          <div class="w-16 h-16 bg-white rounded-full flex items-center justify-center mx-auto mb-4">
            <i class="fas fa-car text-3xl text-primary-500"></i>
          </div>
          <h1 class="text-3xl font-bold text-gray-900 dark:text-white">Vroum Shop</h1>
          <p class="text-gray-600 dark:text-gray-300 mt-2">Car Dealership Management</p>
        </div>
        
        <form @submit.prevent="handleLogin" class="space-y-6">
          <div class="form-group">
            <label class="form-label">Username</label>
            <input 
              v-model="credentials.username"
              type="text" 
              required
              class="input-field"
              placeholder="Enter your username"
              :disabled="loading"
            >
          </div>
          
          <div class="form-group">
            <label class="form-label">Password</label>
            <input 
              v-model="credentials.password"
              type="password" 
              required
              class="input-field"
              placeholder="Enter your password"
              :disabled="loading"
            >
          </div>
          
          <button 
            type="submit" 
            :disabled="loading"
            class="btn-primary w-full flex items-center justify-center"
          >
            <div v-if="loading" class="loading-spinner mr-2"></div>
            {{ loading ? 'Signing in...' : 'Sign In' }}
          </button>
        </form>
        
        <div class="mt-6 text-center">
          <button 
            @click="toggleDarkMode"
            class="text-sm text-gray-600 dark:text-gray-300 hover:text-primary-500 transition-colors duration-200"
          >
            <i :class="isDarkMode ? 'fas fa-sun' : 'fas fa-moon'" class="mr-2"></i>
            {{ isDarkMode ? 'Light Mode' : 'Dark Mode' }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, inject } from 'vue'
import { useRouter } from 'vue-router'
import { useStore } from '../composables/useStore'
import { authService } from '../services/authService'

export default {
  name: 'Login',
  setup() {
    const router = useRouter()
    const toast = inject('toast')
    const { isDarkMode, toggleDarkMode, setUser } = useStore()
    
    const credentials = ref({
      username: '',
      password: ''
    })
    const loading = ref(false)

    const handleLogin = async () => {
      loading.value = true
      try {
        const response = await authService.login(credentials.value)
        localStorage.setItem('token', response.data.token)
        setUser(response.data.admin)
        router.push('/dashboard')
        toast.value.show('Login successful!', 'success')
      } catch (error) {
        toast.value.show('Invalid credentials', 'error')
      } finally {
        loading.value = false
      }
    }

    return {
      credentials,
      loading,
      isDarkMode,
      handleLogin,
      toggleDarkMode
    }
  }
}
</script>
