<template>
  <div>
    <Header />
    <main class="container animate-fade-in">
      <div class="glass-panel">
        <div class="header-action">
          <h2>Relatório de Gastos</h2>
          <div class="action-buttons">
            <button class="btn btn-primary btn-sm mr-2" @click="exportarPdf">Exportar PDF</button>
            <button class="btn btn-secondary btn-sm" @click="$router.push('/')">Voltar</button>
          </div>
        </div>

        <div v-if="loading" class="text-center text-muted" style="padding: 2rem;">
          Carregando relatório...
        </div>

        <div v-else-if="lancamentos.length === 0" class="text-center text-muted" style="padding: 2rem;">
          Nenhum lançamento encontrado.
        </div>

        <div v-else class="relatorio-table-container">
          <table class="relatorio-table">
            <thead>
              <tr>
                <th>Data</th>
                <th>Descrição</th>
                <th>Tipo</th>
                <th class="text-right">Valor</th>
                <th class="text-center">Ações</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="item in lancamentosOrdenados" :key="item.id">
                <td>{{ formatarData(item.data) }}</td>
                <td>{{ item.descricao }}</td>
                <td>
                  <span :class="item.tipo === 'RECEITA' ? 'badge-success' : 'badge-danger'">
                    {{ item.tipo === 'RECEITA' ? 'Receita' : 'Despesa' }}
                  </span>
                </td>
                <td class="text-right font-weight-bold" :class="item.tipo === 'RECEITA' ? 'text-success' : 'text-danger'">
                  R$ {{ formatarMoeda(item.valor) }}
                </td>
                <td class="text-center">
                  <button class="action-btn text-muted" @click="prepararEdicao(item)" title="Editar" style="margin-right: 0.5rem; background: transparent; border: none; cursor: pointer;">
                    <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" viewBox="0 0 16 16">
                      <path d="M12.146.146a.5.5 0 0 1 .708 0l3 3a.5.5 0 0 1 0 .708l-10 10a.5.5 0 0 1-.168.11l-5 2a.5.5 0 0 1-.65-.65l2-5a.5.5 0 0 1 .11-.168l10-10zM11.207 2.5 13.5 4.793 14.793 3.5 12.5 1.207 11.207 2.5zm1.586 3L10.5 3.207 4 9.707V10h.5a.5.5 0 0 1 .5.5v.5h.5a.5.5 0 0 1 .5.5v.5h.293l6.5-6.5zm-9.761 5.175-.106.106-1.528 3.821 3.821-1.528.106-.106A.5.5 0 0 1 5 12.5V12h-.5a.5.5 0 0 1-.5-.5V11h-.5a.5.5 0 0 1-.468-.325z"/>
                    </svg>
                  </button>
                  <button class="action-btn text-danger" @click="excluirLancamento(item.id)" title="Excluir" style="background: transparent; border: none; cursor: pointer;">
                    <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" viewBox="0 0 16 16">
                      <path d="M5.5 5.5A.5.5 0 0 1 6 6v6a.5.5 0 0 1-1 0V6a.5.5 0 0 1 .5-.5zm2.5 0a.5.5 0 0 1 .5.5v6a.5.5 0 0 1-1 0V6a.5.5 0 0 1 .5-.5zm3 .5a.5.5 0 0 0-1 0v6a.5.5 0 0 0 1 0V6z"/>
                      <path fill-rule="evenodd" d="M14.5 3a1 1 0 0 1-1 1H13v9a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V4h-.5a1 1 0 0 1-1-1V2a1 1 0 0 1 1-1H6a1 1 0 0 1 1-1h2a1 1 0 0 1 1 1h3.5a1 1 0 0 1 1 1v1zM4.118 4 4 4.059V13a1 1 0 0 0 1 1h6a1 1 0 0 0 1-1V4.059L11.882 4H4.118zM2.5 3V2h11v1h-11z"/>
                    </svg>
                  </button>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>

    </main>

    <!-- Modal de Edição -->
    <div v-if="idEdicao !== null" class="modal-overlay">
      <div class="glass-panel modal-content animate-fade-in">
        <div class="header-action" style="margin-bottom: 1rem; border-bottom: none;">
          <h3 style="margin: 0;">Editar Lançamento</h3>
          <button class="btn btn-secondary btn-sm" @click="cancelarEdicao" style="padding: 0.1rem 0.5rem;">✕</button>
        </div>
        <form @submit.prevent="salvarEdicao">
          <div class="form-group" style="margin-bottom: 1rem;">
            <label style="display:block; margin-bottom: 0.5rem;">Tipo</label>
            <select class="form-control" v-model="formEdicao.tipo" required>
              <option value="RECEITA">Receita</option>
              <option value="DESPESA">Despesa</option>
            </select>
          </div>
          <div class="form-group" style="margin-bottom: 1rem;">
            <label style="display:block; margin-bottom: 0.5rem;">Valor (R$)</label>
            <input type="number" step="0.01" class="form-control" v-model="formEdicao.valor" required />
          </div>
          <div class="form-group" style="margin-bottom: 1rem;">
            <label style="display:block; margin-bottom: 0.5rem;">Descrição</label>
            <input type="text" class="form-control" v-model="formEdicao.descricao" required />
          </div>
          <div class="form-group" style="margin-bottom: 1rem;">
            <label style="display:block; margin-bottom: 0.5rem;">Data</label>
            <input type="date" class="form-control" v-model="formEdicao.data" required />
          </div>
          <button type="submit" class="btn btn-primary" style="width: 100%; margin-top: 1rem;">
            {{ loadingEdicao ? 'Salvando...' : 'Atualizar Lançamento' }}
          </button>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import Header from '../components/Header.vue'
import api from '../api/axios'
import jsPDF from 'jspdf'
import autoTable from 'jspdf-autotable'
import { useToast } from '../composables/useToast'

/**
 * View para o Relatório Completo.
 * Componente responsável por carregar todos os lançamentos inseridos pelo usuário
 * e exibi-los em uma estrutura baseada em tabelas HTML fluídas.
 * Possui função de exportação para PDF através da biblioteca jsPDF.
 */
const lancamentos = ref([])
const loading = ref(true)
const { showToast } = useToast()

const idEdicao = ref(null)
const loadingEdicao = ref(false)
const formEdicao = ref({
  tipo: 'DESPESA',
  valor: '',
  descricao: '',
  data: ''
})

const formatarMoeda = (valor) => {
  return parseFloat(valor).toLocaleString('pt-BR', { minimumFractionDigits: 2 })
}

const formatarData = (dataIso) => {
  if (!dataIso) return ''
  const partes = dataIso.split('-')
  return `${partes[2]}/${partes[1]}/${partes[0]}`
}

const lancamentosOrdenados = computed(() => {
  return [...lancamentos.value].sort((a, b) => new Date(b.data) - new Date(a.data))
})

const carregarLancamentos = async () => {
  try {
    loading.value = true
    const usuarioId = sessionStorage.getItem('usuario_id')
    const response = await api.get(`/lancamentos/relatorio/${usuarioId}`)
    lancamentos.value = response.data
  } catch (error) {
    console.error("Erro ao buscar relatório", error)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  carregarLancamentos()
})

const prepararEdicao = (item) => {
  idEdicao.value = item.id
  formEdicao.value = { ...item }
}

const cancelarEdicao = () => {
  idEdicao.value = null
}

const salvarEdicao = async () => {
  loadingEdicao.value = true
  try {
    await api.put(`/lancamentos/${idEdicao.value}`, formEdicao.value)
    showToast('Lançamento atualizado com sucesso!', 'success')
    idEdicao.value = null
    await carregarLancamentos()
  } catch (err) {
    console.error("Erro ao atualizar:", err)
    showToast('Erro ao atualizar lançamento.', 'danger')
  } finally {
    loadingEdicao.value = false
  }
}

const excluirLancamento = async (id) => {
  if (!confirm('Deseja realmente excluir este lançamento?')) return
  try {
    await api.delete(`/lancamentos/${id}`)
    await carregarLancamentos()
  } catch (err) {
    console.error("Erro ao excluir:", err)
    showToast("Falha ao excluir o lançamento.", 'danger')
  }
}

/**
 * Realiza o parse da tabela HTML invisível ou da estrutura de dados
 * e cria um arquivo PDF fazendo download na máquina do usuário localmente.
 */
const exportarPdf = () => {
  const doc = new jsPDF()

  doc.setFontSize(18)
  doc.text('Relatorio Mensal de Financas', 14, 22)

  const tableData = lancamentosOrdenados.value.map(l => [
    formatarData(l.data),
    l.descricao,
    l.tipo === 'RECEITA' ? 'Receita' : 'Despesa',
    `R$ ${formatarMoeda(l.valor)}`
  ])

  autoTable(doc, {
    startY: 30,
    head: [['Data', 'Descrição', 'Tipo', 'Valor']],
    body: tableData,
    theme: 'striped'
  })

  doc.save('relatorio-financas.pdf')
}
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

.action-buttons {
  display: flex;
  gap: 0.5rem;
}

.mr-2 {
  margin-right: 0.5rem;
}

.relatorio-table-container {
  overflow-x: auto;
}

.relatorio-table {
  width: 100%;
  border-collapse: collapse;
}

.relatorio-table th, .relatorio-table td {
  padding: 1rem;
  text-align: left;
  border-bottom: 1px solid rgba(255, 255, 255, 0.05);
}

.relatorio-table th {
  color: var(--text-muted);
  font-weight: 500;
  text-transform: uppercase;
  font-size: 0.8rem;
  letter-spacing: 0.05em;
}

.relatorio-table tbody tr {
  transition: background-color 0.2s;
}

.relatorio-table tbody tr:hover {
  background-color: rgba(255, 255, 255, 0.02);
}

.text-right { text-align: right !important; }
.font-weight-bold { font-weight: 600; }

.badge-success {
  background: rgba(16, 185, 129, 0.2);
  color: var(--success);
  padding: 0.25rem 0.75rem;
  border-radius: 999px;
  font-size: 0.8rem;
  font-weight: 500;
}

.badge-danger {
  background: rgba(239, 68, 68, 0.2);
  color: var(--danger);
  padding: 0.25rem 0.75rem;
  border-radius: 999px;
  font-size: 0.8rem;
  font-weight: 500;
}

.text-center { text-align: center !important; }

.action-btn {
  color: var(--text-muted);
  transition: color 0.2s;
}
.action-btn:hover {
  color: var(--text-color);
}
.action-btn.text-danger:hover {
  color: var(--danger);
}

.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  background: rgba(0, 0, 0, 0.6);
  backdrop-filter: blur(4px);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal-content {
  width: 90%;
  max-width: 400px;
  background: var(--bg-dark);
  padding: 2rem;
  border-radius: 12px;
  border: 1px solid var(--border-color);
  box-shadow: 0 10px 25px rgba(0,0,0,0.5);
}
</style>
