import { createRouter, createWebHistory } from 'vue-router'
import LoginView from '../views/LoginView.vue'
import CadastroView from '../views/CadastroView.vue'
import HomeView from '../views/HomeView.vue'
import LancamentoFormView from '../views/LancamentoFormView.vue'
import RelatorioView from '../views/RelatorioView.vue'
import UsuarioView from '../views/UsuarioView.vue'
import CalculadoraSalarioView from '../views/CalculadoraSalarioView.vue'
import DespesasFuturasView from '../views/DespesasFuturasView.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/login',
      name: 'login',
      component: LoginView
    },
    {
      path: '/cadastro',
      name: 'cadastro',
      component: CadastroView
    },
    {
      path: '/',
      name: 'home',
      component: HomeView,
      meta: { requiresAuth: true }
    },
    {
      path: '/lancamentos/novo',
      name: 'novo-lancamento',
      component: LancamentoFormView,
      meta: { requiresAuth: true }
    },
    {
      path: '/relatorio',
      name: 'relatorio',
      component: RelatorioView,
      meta: { requiresAuth: true }
    },
    {
      path: '/usuario',
      name: 'usuario',
      component: UsuarioView,
      meta: { requiresAuth: true }
    },
    {
      path: '/calculadora',
      name: 'calculadora',
      component: CalculadoraSalarioView,
      meta: { requiresAuth: true }
    },
    {
      path: '/despesas-futuras',
      name: 'despesas-futuras',
      component: DespesasFuturasView,
      meta: { requiresAuth: true }
    }
  ]
})

// Navigation Guard simple for local persistency
router.beforeEach((to, from, next) => {
  const loggedInUser = sessionStorage.getItem('usuario_id')
  
  if (to.meta.requiresAuth && !loggedInUser) {
    next('/login')
  } else if ((to.name === 'login' || to.name === 'cadastro') && loggedInUser) {
    next('/')
  } else {
    next()
  }
})

export default router
