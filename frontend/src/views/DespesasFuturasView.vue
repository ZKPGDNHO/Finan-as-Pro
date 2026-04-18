<template>
  <div>
    <Header />
    <main class="container animate-fade-in">
      <div class="glass-panel mx-auto" style="max-width: 900px;">
        <div class="header-action">
          <div style="display: flex; flex-direction: column;">
            <h2>Despesas Futuras</h2>
            <div style="display: flex; align-items: center; gap: 1rem; margin-top: 0.5rem;">
              <button class="btn btn-secondary btn-sm" @click="prevMonth" style="padding: 0.2rem 0.6rem;">&lt;</button>
              <p class="text-muted" style="text-transform: capitalize; margin: 0; min-width: 140px; text-align: center; font-weight: 600;">{{ monthYearText }}</p>
              <button class="btn btn-secondary btn-sm" @click="nextMonth" style="padding: 0.2rem 0.6rem;">&gt;</button>
            </div>
          </div>
          <button class="btn btn-secondary btn-sm" @click="$router.push('/')">Voltar</button>
        </div>

        <div v-if="loading" class="text-center text-muted" style="padding: 2rem;">
          Carregando calendário...
        </div>
        <div v-else>
          <!-- Calendário -->
          <div class="calendar-header">
            <div>Dom</div><div>Seg</div><div>Ter</div><div>Qua</div><div>Qui</div><div>Sex</div><div>Sáb</div>
          </div>
          <div class="calendar">
            <div 
              v-for="(cell, index) in calendarCells" 
              :key="index" 
              :class="['calendar-day', { empty: cell.isEmpty }]"
            >
              <template v-if="!cell.isEmpty">
                <span class="day-number">{{ cell.dayNumber }}</span>
                <div class="day-content" style="gap: 0.25rem; width: 100%;">
                  <span 
                    v-for="parcela in parcelasDoMes(cell.dateStr)" 
                    :key="parcela.id"
                    class="badge animate-pulse" 
                    :title="'Valor: R$ ' + formatarMoeda(parcela.valor)"
                    style="white-space: nowrap; overflow: hidden; text-overflow: ellipsis; max-width: 100%; display: block; margin-bottom: 0.2rem; text-align: left;"
                  >
                    {{ parcela.descricao }}
                  </span>
                </div>
              </template>
            </div>
          </div>

          <hr style="border-color: var(--border-color); margin: 2rem 0;" />

          <button class="btn btn-primary btn-block" @click="mostrarTabela = !mostrarTabela">
            {{ mostrarTabela ? 'Ocultar Consultar Parcelas' : 'Consultar Parcelas' }}
          </button>

          <!-- Tabela de Parcelas -->
          <div v-show="mostrarTabela" class="animate-fade-in" style="margin-top: 1rem; overflow-x: auto;">
             <div v-if="lancamentosParcelados.length === 0" class="text-center text-muted" style="padding: 1rem;">
               Nenhuma despesa parcelada programada.
             </div>
             <table class="relatorio-table" v-else>
               <thead>
                 <tr>
                   <th>Data</th>
                   <th>Descrição</th>
                   <th class="text-right">Valor da Parcela</th>
                 </tr>
               </thead>
               <tbody>
                 <tr v-for="item in parcelasOrdenadas" :key="item.id">
                   <td>{{ formatarData(item.data) }}</td>
                   <td class="font-weight-bold" style="color: var(--warning);">{{ item.descricao }}</td>
                   <td class="text-right text-danger">
                     R$ {{ formatarMoeda(item.valor) }}
                   </td>
                 </tr>
               </tbody>
             </table>
          </div>
        </div>
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import Header from '../components/Header.vue'
import api from '../api/axios'

const loading = ref(true)
const lancamentos = ref([])
const mostrarTabela = ref(false)

const currentYear = ref(new Date().getFullYear())
const currentMonth = ref(new Date().getMonth())

const prevMonth = () => {
  if (currentMonth.value === 0) {
    currentMonth.value = 11
    currentYear.value--
  } else {
    currentMonth.value--
  }
}

const nextMonth = () => {
  if (currentMonth.value === 11) {
    currentMonth.value = 0
    currentYear.value++
  } else {
    currentMonth.value++
  }
}

// Nome do mês Capitalizado (ex: Abril de 2026)
const monthYearText = computed(() => {
   const d = new Date(currentYear.value, currentMonth.value, 1)
   return d.toLocaleString('pt-BR', { month: 'long', year: 'numeric' })
})

const daysInMonth = computed(() => new Date(currentYear.value, currentMonth.value + 1, 0).getDate())
// 0(Dom) ao 6(Sáb)
const firstDayOfMonth = computed(() => new Date(currentYear.value, currentMonth.value, 1).getDay())

const calendarCells = computed(() => {
   let cells = []
   for(let i=0; i<firstDayOfMonth.value; i++) cells.push({ isEmpty: true })
   for(let i=1; i<=daysInMonth.value; i++) {
      let y = currentYear.value
      let m = String(currentMonth.value + 1).padStart(2, '0')
      let d = String(i).padStart(2, '0')
      cells.push({ isEmpty: false, dayNumber: i, dateStr: `${y}-${m}-${d}` })
   }
   return cells
})

const lancamentosParcelados = computed(() => {
  return lancamentos.value.filter(l => l.tipo === 'DESPESA' && /\(\d+\/\d+\)/.test(l.descricao))
})

const parcelasOrdenadas = computed(() => {
  return [...lancamentosParcelados.value].sort((a, b) => new Date(a.data) - new Date(b.data))
})

const parcelasDoMes = (dateStr) => {
  return lancamentosParcelados.value.filter(l => l.data === dateStr)
}

const formatarMoeda = (valor) => {
  return parseFloat(valor).toLocaleString('pt-BR', { minimumFractionDigits: 2 })
}

const formatarData = (dataIso) => {
  if (!dataIso) return ''
  const partes = dataIso.split('-')
  return `${partes[2]}/${partes[1]}/${partes[0]}`
}

onMounted(async () => {
  try {
    const usuarioId = sessionStorage.getItem('usuario_id')
    const response = await api.get(`/lancamentos/relatorio/${usuarioId}`)
    lancamentos.value = response.data
  } catch (error) {
    console.error("Erro ao buscar relatório", error)
  } finally {
    loading.value = false
  }
})

</script>

<style scoped>
.header-action {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 2rem;
  padding-bottom: 1rem;
  border-bottom: 1px solid var(--border-color);
}
.header-action h2 { margin: 0; }
.header-action p { margin: 0; }

.calendar-header {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  text-align: center;
  font-weight: 600;
  color: var(--text-muted);
  margin-bottom: 0.5rem;
  font-size: 0.9rem;
  text-transform: uppercase;
}

.calendar {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  gap: 0.5rem;
}

.calendar-day {
  background: rgba(var(--text-main-rgb, 255, 255, 255), 0.02);
  padding: 0.75rem;
  border-radius: 8px;
  border: 1px solid var(--border-color);
  min-height: 90px;
  position: relative;
  transition: background 0.3s;
}

body.light-theme .calendar-day {
  background: rgba(0, 0, 0, 0.03);
}


.calendar-day:not(.empty):hover {
  background: rgba(255, 255, 255, 0.05);
}

.calendar-day.empty {
  background: transparent;
  border: none;
}

.day-number {
  font-size: 1.1rem;
  font-weight: bold;
  color: var(--text-color);
}

.day-content {
  margin-top: 0.5rem;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.badge {
  display: inline-block;
  padding: 0.2rem 0.5rem;
  background: linear-gradient(135deg, var(--danger), #f87171);
  color: white;
  border-radius: 6px;
  font-size: 0.7rem;
  font-weight: bold;
  box-shadow: 0 2px 4px rgba(0,0,0,0.3);
}

.btn-block { width: 100%; }

.relatorio-table {
  width: 100%;
  border-collapse: collapse;
}

.relatorio-table th, .relatorio-table td {
  padding: 0.75rem 1rem;
  text-align: left;
  border-bottom: 1px solid rgba(255, 255, 255, 0.05);
}

.relatorio-table th {
  color: var(--text-muted);
  font-weight: 500;
  text-transform: uppercase;
  font-size: 0.8rem;
}
.relatorio-table tbody tr:hover {
  background-color: rgba(255, 255, 255, 0.02);
}

.text-right { text-align: right !important; }
.font-weight-bold { font-weight: 600; }
.text-center { text-align: center; }

/* Animaçãozinha pulso */
@keyframes pulse-once {
  0% { transform: scale(1); }
  50% { transform: scale(1.05); }
  100% { transform: scale(1); }
}
.animate-pulse {
  animation: pulse-once 1s ease-in-out infinite;
}
</style>
