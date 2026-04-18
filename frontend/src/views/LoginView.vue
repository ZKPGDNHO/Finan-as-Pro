<template>
  <div class="full-screen-center">
    <div class="glass-panel animate-fade-in login-container">
      <h1 class="text-center">Finanças Pro</h1>
      <p class="text-center text-muted" style="margin-bottom: 2rem;">Controle seu dinheiro de forma simples.</p>
      
      <form @submit.prevent="handleLogin">
        <div class="form-group">
          <label>E-mail</label>
          <input type="email" class="form-control" v-model="email" required placeholder="seu@email.com" />
        </div>
        
        <div class="form-group">
          <label>Senha</label>
          <input type="password" class="form-control" v-model="senha" required placeholder="Sua senha" />
        </div>
        
        <div class="error-msg" v-if="error">{{ error }}</div>

        <button type="submit" class="btn btn-primary" style="width: 100%; margin-top: 1rem;">
          {{ loading ? 'Entrando...' : 'Login' }}
        </button>
        
        <div class="register-prompt">
          <button type="button" class="btn btn-secondary btn-sm" @click="$router.push('/cadastro')">
            Ainda não tem conta? Cadastre-se
          </button>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import api from '../api/axios'

/**
 * Tela de Autenticação/Login.
 * Gerencia o payload de login, interage com AuthController no Backend,
 * e salva o identificador e nome localmente para a persistência do Vue Router.
 */

const email = ref('')
const senha = ref('')
const error = ref('')
const loading = ref(false)
const router = useRouter()

const handleLogin = async () => {
  loading.value = true
  error.value = ''
  
  try {
    const response = await api.post('/auth/login', {
      email: email.value,
      senha: senha.value
    })
    
    // Save user ID to simple local sessionStorage
    sessionStorage.setItem('usuario_id', response.data.id)
    sessionStorage.setItem('usuario_nome', response.data.nome)
    
    router.push('/')
  } catch (err) {
    error.value = err.response?.data || 'Erro de conexão. Tente novamente.'
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-container {
  width: 100%;
  max-width: 400px;
}
.register-prompt {
  text-align: center;
  margin-top: 1.5rem;
  padding-top: 1.5rem;
  border-top: 1px solid var(--border-color);
}
.btn-sm {
  padding: 0.5rem 1rem;
  font-size: 0.875rem;
  width: 100%;
}
.error-msg {
  color: var(--danger);
  font-size: 0.875rem;
  margin-bottom: 1rem;
  text-align: center;
}
</style>
