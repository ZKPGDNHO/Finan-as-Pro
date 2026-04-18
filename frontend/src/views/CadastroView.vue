<template>
  <div class="full-screen-center">
    <div class="glass-panel animate-fade-in login-container">
      <h2 class="text-center">Criar Conta</h2>
      <p class="text-center text-muted" style="margin-bottom: 1.5rem;">Crie sua conta para acessar o sistema.</p>
      
      <form @submit.prevent="handleCadastro">
        <div class="form-group">
          <label>Nome Completo</label>
          <input type="text" class="form-control" v-model="nome" required placeholder="João Silva" />
        </div>

        <div class="form-group">
          <label>E-mail</label>
          <input type="email" class="form-control" v-model="email" required placeholder="seu@email.com" />
        </div>
        
        <div class="form-group">
          <label>Senha</label>
          <input type="password" class="form-control" v-model="senha" required placeholder="Sua senha secreta" />
        </div>
        
        <div class="error-msg" v-if="error">{{ error }}</div>

        <button type="submit" class="btn btn-primary" style="width: 100%; margin-top: 1rem;">
          {{ loading ? 'Cadastrando...' : 'Cadastrar' }}
        </button>
        
        <div class="register-prompt">
          <button type="button" class="btn btn-secondary btn-sm" @click="$router.push('/login')">
            Voltar para o Login
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

const nome = ref('')
const email = ref('')
const senha = ref('')
const error = ref('')
const loading = ref(false)
const router = useRouter()

const handleCadastro = async () => {
  loading.value = true
  error.value = ''
  
  try {
    await api.post('/auth/cadastro', {
      nome: nome.value,
      email: email.value,
      senha: senha.value
    })
    
    alert('Cadastro realizado com sucesso! Faça seu login.')
    router.push('/login')
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
