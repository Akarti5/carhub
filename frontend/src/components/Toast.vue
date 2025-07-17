<template>
  <Teleport to="body">
    <div v-if="visible" :class="toastClasses" class="toast">
      <div class="flex items-center">
        <i :class="iconClass" class="mr-3"></i>
        <span>{{ message }}</span>
        <button @click="hide" class="ml-auto text-white hover:text-gray-200">
          <i class="fas fa-times"></i>
        </button>
      </div>
    </div>
  </Teleport>
</template>

<script>
import { ref, computed } from 'vue'

export default {
  name: 'Toast',
  setup() {
    const visible = ref(false)
    const message = ref('')
    const type = ref('success')
    let timeoutId = null

    const toastClasses = computed(() => ({
      'toast': true,
      'success': type.value === 'success',
      'error': type.value === 'error',
      'warning': type.value === 'warning',
      'info': type.value === 'info'
    }))

    const iconClass = computed(() => {
      const icons = {
        success: 'fas fa-check-circle',
        error: 'fas fa-exclamation-circle',
        warning: 'fas fa-exclamation-triangle',
        info: 'fas fa-info-circle'
      }
      return icons[type.value] || icons.info
    })

    const show = (msg, toastType = 'success', duration = 3000) => {
      message.value = msg
      type.value = toastType
      visible.value = true

      if (timeoutId) {
        clearTimeout(timeoutId)
      }

      timeoutId = setTimeout(() => {
        hide()
      }, duration)
    }

    const hide = () => {
      visible.value = false
      if (timeoutId) {
        clearTimeout(timeoutId)
        timeoutId = null
      }
    }

    return {
      visible,
      message,
      type,
      toastClasses,
      iconClass,
      show,
      hide
    }
  }
}
</script>
