<template>
  <div class="toast-container">
    <TransitionGroup name="toast">
      <div 
        v-for="toast in toasts" 
        :key="toast.id" 
        class="toast-message" 
        :class="`toast-${toast.type}`"
      >
        <span class="icon" v-if="toast.type === 'success'">✓</span>
        <span class="icon" v-if="toast.type === 'danger'">✕</span>
        <span>{{ toast.message }}</span>
      </div>
    </TransitionGroup>
  </div>
</template>

<script setup>
import { useToast } from '../composables/useToast'

const { toasts } = useToast()
</script>

<style scoped>
.toast-container {
  position: fixed;
  top: 20px;
  right: 20px;
  z-index: 9999;
  display: flex;
  flex-direction: column;
  gap: 10px;
  pointer-events: none;
}

.toast-message {
  display: flex;
  align-items: center;
  gap: 10px;
  min-width: 250px;
  padding: 12px 20px;
  border-radius: 8px;
  color: #fff;
  font-weight: 500;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
  pointer-events: auto;
}

.icon {
  font-weight: bold;
  font-size: 1.1rem;
}

.toast-success {
  background-color: var(--success); /* Usually a green color */
  border-left: 4px solid #065F46; /* darker green accent */
}

.toast-danger {
  background-color: var(--danger); /* Usually a red color */
  border-left: 4px solid #7F1D1D; /* darker red accent */
}

/* Vue TransitionGroup generic styling */
.toast-enter-active,
.toast-leave-active {
  transition: all 0.3s ease;
}
.toast-enter-from {
  opacity: 0;
  transform: translateX(30px);
}
.toast-leave-to {
  opacity: 0;
  transform: translateX(30px) scale(0.9);
}
</style>
