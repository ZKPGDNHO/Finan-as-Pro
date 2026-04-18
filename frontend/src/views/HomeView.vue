<template>
  <div>
    <Header />
    <main class="container animate-fade-in">
      <div class="dashboard-grid">
        
        <!-- Saldo Card -->
        <div class="glass-panel balance-card">
          <h4>Saldo Atual</h4>
          <h1 :class="saldoClass">
            R$ {{ saldoFormatado }}
          </h1>
          
          <div class="balance-details">
            <div class="detail-item text-success">
              <small>Receitas</small>
              <span>+ R$ {{ receitasFormatado }}</span>
            </div>
            <div class="detail-item text-danger">
              <small>Despesas</small>
              <span>- R$ {{ despesasFormatado }}</span>
            </div>
          </div>
        </div>

        <!-- Action Cards -->
        <div class="actions-container">
          <div class="glass-panel action-card" @click="$router.push('/lancamentos/novo')">
            <div class="icon-circle primary-bg">
              <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="12" y1="5" x2="12" y2="19"></line><line x1="5" y1="12" x2="19" y2="12"></line></svg>
            </div>
            <h3>Cadastrar Lançamento</h3>
            <p class="text-muted">Adicione novas receitas ou despesas</p>
          </div>

          <div class="glass-panel action-card" @click="$router.push('/relatorio')">
            <div class="icon-circle secondary-bg">
              <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"></path><polyline points="14 2 14 8 20 8"></polyline><line x1="16" y1="13" x2="8" y2="13"></line><line x1="16" y1="17" x2="8" y2="17"></line><polyline points="10 9 9 9 8 9"></polyline></svg>
            </div>
            <h3>Gerar Relatório</h3>
            <p class="text-muted">Acompanhe seus gastos no mês</p>
          </div>

          <div class="glass-panel action-card" @click="$router.push('/calculadora')">
            <div class="icon-circle success-bg">
              <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect x="4" y="4" width="16" height="16" rx="2" ry="2"></rect><rect x="9" y="9" width="6" height="6"></rect><line x1="9" y1="1" x2="9" y2="4"></line><line x1="15" y1="1" x2="15" y2="4"></line><line x1="9" y1="20" x2="9" y2="23"></line><line x1="15" y1="20" x2="15" y2="23"></line><line x1="20" y1="9" x2="23" y2="9"></line><line x1="20" y1="14" x2="23" y2="14"></line><line x1="1" y1="9" x2="4" y2="9"></line><line x1="1" y1="14" x2="4" y2="14"></line></svg>
            </div>
            <h3>Calcular Salário</h3>
            <p class="text-muted">Descubra quanto você ganha trabalhando</p>
          </div>

          <div class="glass-panel action-card" @click="$router.push('/despesas-futuras')">
            <div class="icon-circle warning-bg">
              <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" viewBox="0 0 24 24"><rect x="3" y="4" width="18" height="18" rx="2" ry="2"></rect><line x1="16" y1="2" x2="16" y2="6"></line><line x1="8" y1="2" x2="8" y2="6"></line><line x1="3" y1="10" x2="21" y2="10"></line></svg>
            </div>
            <h3>Despesas Futuras</h3>
            <p class="text-muted">Consulte vencimentos e parcelas</p>
          </div>
        </div>

      </div>
    </main>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import Header from '../components/Header.vue'
import api from '../api/axios'

/**
 * Módulo de Dashboard e tela inicial focada em consolidação dos dados.
 * Recebe o payload de dashboard da API e extrai saldos.
 */

const saldo = ref(0)
const receitas = ref(0)
const despesas = ref(0)

const formatarMoeda = (valor) => {
  if (!valor) return '0,00'
  return parseFloat(valor).toLocaleString('pt-BR', { minimumFractionDigits: 2, maximumFractionDigits: 2 })
}

const saldoFormatado = computed(() => formatarMoeda(saldo.value))
const receitasFormatado = computed(() => formatarMoeda(receitas.value))
const despesasFormatado = computed(() => formatarMoeda(despesas.value))

const saldoClass = computed(() => {
  if (saldo.value > 0) return 'text-success'
  if (saldo.value < 0) return 'text-danger'
  return ''
})

onMounted(async () => {
  try {
    const usuarioId = sessionStorage.getItem('usuario_id')
    const response = await api.get(`/lancamentos/dashboard/${usuarioId}`)
    const data = response.data
    
    saldo.value = data.saldo || 0
    receitas.value = data.totalReceitas || 0
    despesas.value = data.totalDespesas || 0
  } catch (error) {
    console.error("Erro ao buscar dashboard", error)
  }
})
</script>

<style scoped>
.dashboard-grid {
  display: flex;
  flex-direction: column;
  gap: 2rem;
}

.balance-card {
  text-align: center;
  padding: 3rem 2rem;
}

.balance-card h4 {
  color: var(--text-muted);
  font-weight: 500;
  margin-bottom: 0.5rem;
}

.balance-card h1 {
  font-size: 3.5rem;
  margin-bottom: 2rem;
}

.balance-details {
  display: flex;
  justify-content: center;
  gap: 3rem;
  border-top: 1px solid var(--border-color);
  padding-top: 1.5rem;
}

.detail-item {
  display: flex;
  flex-direction: column;
  gap: 0.25rem;
}

.detail-item span {
  font-size: 1.25rem;
  font-weight: 600;
}

.actions-container {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 1.5rem;
}

.action-card {
  cursor: pointer;
  transition: transform 0.3s, box-shadow 0.3s;
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
  padding: 2.5rem 1.5rem;
}

.action-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 12px 40px rgba(0, 0, 0, 0.4);
  background: var(--bg-card-hover);
}

.icon-circle {
  width: 64px;
  height: 64px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 1.5rem;
  color: white;
}

.primary-bg { background: linear-gradient(135deg, var(--primary), #818cf8); }
.secondary-bg { background: linear-gradient(135deg, var(--secondary), #f472b6); }
.success-bg { background: linear-gradient(135deg, var(--success), #34d399); }
.warning-bg { background: linear-gradient(135deg, var(--danger), #f87171); }

.action-card h3 {
  margin-bottom: 0.5rem;
}

@media (max-width: 768px) {
  .actions-container {
    grid-template-columns: 1fr;
  }
}
</style>
