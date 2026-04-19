import { ref } from 'vue'

const toasts = ref([])
let nextId = 0

export function useToast() {
  const showToast = (message, type = 'success') => {
    const id = nextId++
    toasts.value.push({ id, message, type })
    
    // Remove the toast after 3 seconds
    setTimeout(() => {
      toasts.value = toasts.value.filter(t => t.id !== id)
    }, 3000)
  }

  return {
    toasts,
    showToast
  }
}
