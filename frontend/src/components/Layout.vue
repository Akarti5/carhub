<template>
  <div class="min-h-screen bg-gray-100 dark:bg-gray-900 transition-colors duration-300">
    <!-- Header -->
    <header class="bg-white dark:bg-gray-800 shadow-sm border-b border-gray-200 dark:border-gray-700">
      <div class="flex items-center justify-between px-6 py-4">
        <div class="flex items-center">
          <button 
            @click="toggleSidebar"
            class="text-gray-500 hover:text-gray-700 dark:text-gray-300 dark:hover:text-white mr-4 lg:hidden"
          >
            <i class="fas fa-bars text-xl"></i>
          </button>
          <div class="flex items-center">
            <i class="fas fa-car text-2xl text-primary-500 mr-3"></i>
            <h1 class="text-2xl font-bold text-gray-900 dark:text-white">Carhub</h1>
          </div>
        </div>
        
        <div class="flex items-center space-x-4">
          <button 
            @click="toggleDarkMode"
            class="text-gray-500 hover:text-gray-700 dark:text-gray-300 dark:hover:text-white transition-colors duration-200"
          >
            <i :class="isDarkMode ? 'fas fa-sun' : 'fas fa-moon'" class="text-xl"></i>
          </button>
          
          <div class="relative" ref="profileDropdown">
            <button 
              @click="showProfileDropdown = !showProfileDropdown"
              class="flex items-center text-gray-700 dark:text-gray-300 hover:text-gray-900 dark:hover:text-white transition-colors duration-200"
            >
              <div class="w-8 h-8 bg-primary-500 rounded-full flex items-center justify-center text-white font-semibold mr-2">
                {{ userInitials }}
              </div>
              <span class="hidden md:block">{{ user?.firstName }} {{ user?.lastName }}</span>
              <i class="fas fa-chevron-down ml-2"></i>
            </button>
            
            <div v-if="showProfileDropdown" class="absolute right-0 mt-2 w-48 bg-white dark:bg-gray-800 rounded-md shadow-lg py-1 z-50">
              <a href="#" class="block px-4 py-2 text-sm text-gray-700 dark:text-gray-300 hover:bg-gray-100 dark:hover:bg-gray-700 transition-colors duration-200">
                <i class="fas fa-user mr-2"></i>Profile
              </a>
              <a href="#" class="block px-4 py-2 text-sm text-gray-700 dark:text-gray-300 hover:bg-gray-100 dark:hover:bg-gray-700 transition-colors duration-200">
                <i class="fas fa-cog mr-2"></i>Settings
              </a>
              <hr class="my-1 border-gray-200 dark:border-gray-700">
              <button 
                @click="handleLogout"
                class="block w-full text-left px-4 py-2 text-sm text-gray-700 dark:text-gray-300 hover:bg-gray-100 dark:hover:bg-gray-700 transition-colors duration-200"
              >
                <i class="fas fa-sign-out-alt mr-2"></i>Logout
              </button>
            </div>
          </div>
        </div>
      </div>
    </header>
    
    <div class="flex">
      <!-- Sidebar -->
      <nav :class="sidebarClasses">
        <div class="p-4">
          <ul class="space-y-2">
            <li>
              <router-link 
                to="/dashboard"
                class="sidebar-item"
                active-class="active"
              >
                <i class="fas fa-tachometer-alt text-xl"></i>
                <span v-if="!sidebarCollapsed" class="ml-3">Dashboard</span>
              </router-link>
            </li>
            <li>
              <router-link 
                to="/cars"
                class="sidebar-item"
                active-class="active"
              >
                <i class="fas fa-car text-xl"></i>
                <span v-if="!sidebarCollapsed" class="ml-3">Voitures</span>
              </router-link>
            </li>
            <li>
              <router-link 
                to="/sales"
                class="sidebar-item"
                active-class="active"
              >
                <i class="fas fa-shopping-cart text-xl"></i>
                <span v-if="!sidebarCollapsed" class="ml-3">Achats</span>
              </router-link>
            </li>
            <li>
              <router-link 
                to="/clients"
                class="sidebar-item"
                active-class="active"
              >
                <i class="fas fa-users text-xl"></i>
                <span v-if="!sidebarCollapsed" class="ml-3">Clients</span>
              </router-link>
            </li>
            <li>
              <router-link 
                to="/reports"
                class="sidebar-item"
                active-class="active"
              >
                <i class="fas fa-chart-bar text-xl"></i>
                <span v-if="!sidebarCollapsed" class="ml-3">Rapports</span>
              </router-link>
            </li>
            <li>
              <router-link 
                to="/settings"
                class="sidebar-item"
                active-class="active"
              >
                <i class="fas fa-cog text-xl"></i>
                <span v-if="!sidebarCollapsed" class="ml-3">Paramètres</span>
              </router-link>
            </li>
          </ul>
        </div>
      </nav>
      
      <!-- Main Content -->
      <main class="flex-1 overflow-x-hidden">
        <router-view />
      </main>
    </div>

    <!-- Mobile Sidebar Overlay -->
    <div 
      v-if="sidebarOpen && isMobile" 
      @click="closeSidebar"
      class="fixed inset-0 bg-gray-600 bg-opacity-50 z-40 lg:hidden"
    ></div>
  </div>
</template>

<script>
import { ref, computed, inject, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { useStore } from '../composables/useStore'
import { authService } from '../services/authService'

export default {
  name: 'Layout',
  setup() {
    const router = useRouter()
    const toast = inject('toast')
    const { user, isDarkMode, toggleDarkMode, setUser } = useStore()
    
    const showProfileDropdown = ref(false)
    const sidebarCollapsed = ref(false)
    const sidebarOpen = ref(false)
    const isMobile = ref(window.innerWidth < 1024)
    const profileDropdown = ref(null)

    const userInitials = computed(() => {
      if (!user.value) return 'U'
      return `${user.value.firstName?.charAt(0) || ''}${user.value.lastName?.charAt(0) || ''}`
    })

    const sidebarClasses = computed(() => ({
      'bg-white dark:bg-gray-800 shadow-sm border-r border-gray-200 dark:border-gray-700 sidebar-transition': true,
      'w-16': sidebarCollapsed.value && !isMobile.value,
      'w-64': !sidebarCollapsed.value || isMobile.value,
      'fixed inset-y-0 left-0 z-50 lg:relative lg:translate-x-0': isMobile.value,
      'transform -translate-x-full': isMobile.value && !sidebarOpen.value,
      'transform translate-x-0': isMobile.value && sidebarOpen.value
    }))

    const toggleSidebar = () => {
      if (isMobile.value) {
        sidebarOpen.value = !sidebarOpen.value
      } else {
        sidebarCollapsed.value = !sidebarCollapsed.value
      }
    }

    const closeSidebar = () => {
      sidebarOpen.value = false
    }

    const handleLogout = async () => {
      try {
        await authService.logout()
        localStorage.removeItem('token')
        setUser(null)
        router.push('/login')
        toast.value.show('Logged out successfully!', 'success')
      } catch (error) {
        console.error('Logout error:', error)
      }
    }

    const handleResize = () => {
      isMobile.value = window.innerWidth < 1024
      if (!isMobile.value) {
        sidebarOpen.value = false
      }
    }

    const handleClickOutside = (event) => {
      if (profileDropdown.value && !profileDropdown.value.contains(event.target)) {
        showProfileDropdown.value = false
      }
    }

    onMounted(() => {
      window.addEventListener('resize', handleResize)
      document.addEventListener('click', handleClickOutside)
    })

    onUnmounted(() => {
      window.removeEventListener('resize', handleResize)
      document.removeEventListener('click', handleClickOutside)
    })

    return {
      user,
      isDarkMode,
      showProfileDropdown,
      sidebarCollapsed,
      sidebarOpen,
      isMobile,
      profileDropdown,
      userInitials,
      sidebarClasses,
      toggleDarkMode,
      toggleSidebar,
      closeSidebar,
      handleLogout
    }
  }
}
</script>
