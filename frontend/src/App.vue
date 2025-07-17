<template>
  <div id="app" :class="{ 'dark': isDarkMode }">
    <router-view />
    <Toast ref="toast" />
  </div>
</template>

<script>
import { ref, provide, onMounted } from 'vue'
import Toast from './components/Toast.vue'
import { useStore } from './composables/useStore'

export default {
  name: 'App',
  components: {
    Toast
  },
  setup() {
    const toast = ref(null)
    const { isDarkMode, initializeStore } = useStore()

    // Provide toast globally
    provide('toast', toast)

    onMounted(() => {
      initializeStore()
      // Apply dark mode class to document
      document.documentElement.classList.toggle('dark', isDarkMode.value)
    })

    return {
      toast,
      isDarkMode
    }
  }
}
</script>
