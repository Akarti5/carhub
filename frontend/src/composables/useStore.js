import { ref } from "vue"

const user = ref(null)
const isDarkMode = ref(localStorage.getItem("darkMode") === "true")

export function useStore() {
  const setUser = (userData) => {
    user.value = userData
  }

  const toggleDarkMode = () => {
    isDarkMode.value = !isDarkMode.value
    localStorage.setItem("darkMode", isDarkMode.value)
    document.documentElement.classList.toggle("dark", isDarkMode.value)
  }

  const initializeStore = () => {
    // Initialize dark mode
    document.documentElement.classList.toggle("dark", isDarkMode.value)

    // Check for existing token and user
    const token = localStorage.getItem("token")
    if (token && !user.value) {
      // You might want to validate the token here
    }
  }

  return {
    user,
    isDarkMode,
    setUser,
    toggleDarkMode,
    initializeStore,
  }
}
