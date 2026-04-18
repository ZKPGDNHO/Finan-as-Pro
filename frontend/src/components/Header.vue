<template>
  <header class="app-header glass-panel">
    <div class="header-container container">
      <div class="logo">
        <h3>Finanças Pro</h3>
      </div>
      <nav class="nav-links">
        <router-link to="/" class="nav-link" active-class="active">Home</router-link>
        <router-link to="/lancamentos/novo" class="nav-link" active-class="active">Lançamentos</router-link>
        

        <router-link to="/usuario" class="user-display nav-link" active-class="active">
          <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" style="margin-right: 4px; vertical-align: middle;"><path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"></path><circle cx="12" cy="7" r="4"></circle></svg>
          {{ nomeUsuario }}
        </router-link>
        
        <button @click="logout" class="btn btn-secondary btn-sm ml-2">Sair</button>

        <label class="theme-switch" title="Alternar Modo Claro/Escuro" style="margin-left: 0.5rem;">
          <input type="checkbox" v-model="isLightMode" @change="toggleTheme" />
          <span class="slider round"></span>
        </label>
      </nav>
    </div>
  </header>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'

const nomeUsuario = ref('Usuário')
const router = useRouter()
const isLightMode = ref(false)

onMounted(() => {
  const nome = sessionStorage.getItem('usuario_nome')
  if (nome) {
    nomeUsuario.value = nome
  }
  
  const savedTheme = localStorage.getItem('theme')
  if (savedTheme === 'light') {
    isLightMode.value = true
    document.body.classList.add('light-theme')
  } else {
    document.body.classList.remove('light-theme')
  }
})

const toggleTheme = () => {
  if (isLightMode.value) {
    document.body.classList.add('light-theme')
    localStorage.setItem('theme', 'light')
  } else {
    document.body.classList.remove('light-theme')
    localStorage.setItem('theme', 'dark')
  }
}

const logout = () => {
  sessionStorage.removeItem('usuario_id')
  sessionStorage.removeItem('usuario_nome')
  router.push('/login')
}
</script>

<style scoped>
.app-header {
  padding: 1rem 0;
  border-radius: 0;
  border-top: none;
  border-left: none;
  border-right: none;
  margin-bottom: 2rem;
}

.header-container {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.logo h3 {
  margin: 0;
  background: linear-gradient(135deg, var(--primary), var(--secondary));
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  font-weight: 700;
}

.nav-links {
  display: flex;
  align-items: center;
  gap: 1.5rem;
}

.nav-link {
  color: var(--text-muted);
  text-decoration: none;
  font-weight: 500;
  transition: color 0.3s;
}

.nav-link:hover, .nav-link.active {
  color: var(--text-main);
}

.btn-sm {
  padding: 0.4rem 0.8rem;
  font-size: 0.875rem;
}
.ml-2 {
  margin-left: 0.5rem;
}

/* Theme Toggle Switch */
.theme-switch {
  display: inline-block;
  height: 24px;
  position: relative;
  width: 44px;
}
.theme-switch input { display:none; }
.slider {
  background-color: rgba(255, 255, 255, 0.2);
  bottom: 0; cursor: pointer; left: 0;
  position: absolute; right: 0; top: 0;
  transition: .4s;
  border-radius: 24px;
  border: 1px solid var(--border-color);
}
.slider:before {
  background-color: #fff;
  bottom: 3px; content: "";
  height: 16px; left: 3px;
  position: absolute; transition: .4s;
  width: 16px; border-radius: 50%;
  box-shadow: 0 1px 3px rgba(0,0,0,0.5);
}
input:checked + .slider { background-color: var(--primary); }
input:checked + .slider:before { transform: translateX(20px); }
</style>
