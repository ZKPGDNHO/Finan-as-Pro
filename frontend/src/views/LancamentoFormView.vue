<template>
  <div>
    <!-- Renderiza o cabeçalho superior contendo navegação e saudações -->
    <Header />
    
    <main class="container animate-fade-in">
      <div v-if="saldo !== null" class="glass-panel text-center max-w-md mx-auto" style="margin-bottom: 2rem; padding: 1.5rem;">
        <h3 class="text-muted" style="margin-bottom: 0.5rem; font-weight: 500; font-size: 1.1rem; text-transform: uppercase; letter-spacing: 0.05em;">Saldo Atual</h3>
        <h1 :class="saldo >= 0 ? 'text-success' : 'text-danger'" style="font-size: 2.5rem; margin: 0; font-weight: 700;">
          R$ {{ formatarMoeda(saldo) }}
        </h1>
      </div>

      <div class="glass-panel max-w-md mx-auto">
        <div class="header-action">
          <h2>Novo Lançamento</h2>
          <button class="btn btn-secondary btn-sm" @click="$router.push('/')">Voltar</button>
        </div>

        <!-- Formulário para envio de dados do Lancamento -->
        <form @submit.prevent="salvarLancamento">
          <div class="form-group">
            <label>Tipo</label>
            <select class="form-control" v-model="tipo" required>
              <option value="RECEITA">Receita</option>
              <option value="DESPESA">Despesa</option>
            </select>
          </div>

          <div class="form-group">
            <label>Valor (R$)</label>
            <input type="number" step="0.01" class="form-control" v-model="valor" required placeholder="0.00" />
          </div>

          <div class="form-group">
            <label>Descrição</label>
            <input type="text" class="form-control" v-model="descricao" required placeholder="Ex: Conta de Luz" />
          </div>

          <div class="form-group">
            <label>Data</label>
            <input type="date" class="form-control" v-model="data" required />
          </div>

          <div class="form-group" v-if="tipo === 'DESPESA'" style="margin-top: 1rem; display: flex; align-items: center; gap: 0.75rem; user-select: none;">
             <label class="switch">
               <input type="checkbox" v-model="parcelado" />
               <span class="slider round"></span>
             </label>
             <label style="margin: 0; cursor: pointer; color: var(--text-color);" @click="parcelado = !parcelado">Parcelar lançamento?</label>
          </div>

          <div class="form-group animate-fade-in" v-if="tipo === 'DESPESA' && parcelado" style="margin-top: 1rem;">
             <label>Quantidade de Parcelas (1-12)</label>
             <input type="number" min="1" max="12" class="form-control" v-model="numeroParcelas" required />
          </div>

          <!-- Mensagem de erro caso a API tenha falhado -->
          <div class="error-msg" v-if="error">{{ error }}</div>

          <div class="button-group">
            <!-- Botão principal para CADASTRAR O LANÇAMENTO -->
            <button type="submit" class="btn btn-primary btn-block">
              {{ loading ? 'Salvando...' : 'Cadastrar Lançamento' }}
            </button>
            
            <!-- Botão secundário para exbir o extrato -->
            <button type="button" class="btn btn-secondary btn-block mt-3" @click="carregarExtrato" style="margin-top: 1rem;">
              {{ loadingExtrato ? 'Carregando...' : 'Ver Extrato' }}
            </button>
          </div>
        </form>
      </div>

      <!-- Área de Extrato: Renderizada apenas se mostrarExtrato for verdadeiro -->
      <div v-if="mostrarExtrato" class="glass-panel mx-auto extrato-container animate-fade-in" style="margin-top: 2rem;">
        <h3>Extrato de Lançamentos</h3>
        
        <div v-if="lancamentos.length === 0" class="text-center text-muted" style="padding: 1rem;">
          Nenhum lançamento no extrato.
        </div>
        
        <table class="relatorio-table" v-else>
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
            <tr v-for="item in extratoOrdenado" :key="item.id">
              <td>{{ formatarData(item.data) }}</td>
              <td>{{ item.descricao }}</td>
              <td>
                <span :class="item.tipo === 'RECEITA' ? 'text-success font-weight-bold' : 'text-danger font-weight-bold'">
                  {{ item.tipo === 'RECEITA' ? 'Receita' : 'Despesa' }}
                </span>
              </td>
              <td class="text-right" :class="item.tipo === 'RECEITA' ? 'text-success' : 'text-danger'">
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

    </main>

    <!-- Modal de Edição -->
    <div v-if="idEdicao !== null" class="modal-overlay">
      <div class="glass-panel modal-content animate-fade-in">
        <div class="header-action" style="margin-bottom: 1rem; border-bottom: none;">
          <h3 style="margin: 0;">Editar Lançamento</h3>
          <button type="button" class="btn btn-secondary btn-sm" @click="cancelarEdicao" style="padding: 0.1rem 0.5rem;">✕</button>
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
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import Header from '../components/Header.vue'
import api from '../api/axios'
import { useToast } from '../composables/useToast'

/**
 * Módulo de formulário para cadastro e visualização de lançamentos.
 * Utiliza o Composition API do Vue para gerenciar os estados de dados,
 * carregar a listagem e enviar o post HTTP ao backend.
 */

const router = useRouter()

// Variáveis reativas de preenchimento do formulário
const tipo = ref('DESPESA')
const valor = ref('')
const descricao = ref('')

// Gera o dia de hoje formatado para definir como valor inicial no form
const today = new Date().toISOString().split('T')[0]
const data = ref(today)
const idEdicao = ref(null)
const loadingEdicao = ref(false)
const formEdicao = ref({ tipo: 'DESPESA', valor: '', descricao: '', data: '' })
const { showToast } = useToast()

const saldo = ref(null)
const parcelado = ref(false)
const numeroParcelas = ref(1)

// Variáveis de controle de API
const error = ref('')
const loading = ref(false)
const loadingExtrato = ref(false)

// Variáveis para funcionalidade Extrato
const mostrarExtrato = ref(false)
const lancamentos = ref([])

const carregarSaldo = async () => {
  try {
    const usuarioId = sessionStorage.getItem('usuario_id')
    const response = await api.get(`/lancamentos/dashboard/${usuarioId}`)
    saldo.value = response.data.saldo
  } catch (err) {
    console.error("Erro ao carregar saldo", err)
  }
}

onMounted(() => {
  carregarSaldo()
})

/**
 * Função que faz o envio do formulário, passando via POST ou PUT
 * o JSON do tipo, valor, descrição e data.
 */
const salvarLancamento = async () => {
  loading.value = true
  error.value = ''
  
  try {
    const usuarioId = sessionStorage.getItem('usuario_id')
    
    if (parcelado.value && numeroParcelas.value > 1) {
      let qty = Math.min(Math.max(parseInt(numeroParcelas.value), 1), 12)
      let valorParcela = parseFloat(valor.value) / qty
      
      let requests = []
      let parts = data.value.split('-')
      let yearBase = parseInt(parts[0], 10)
      let monthBase = parseInt(parts[1], 10) - 1
      let dayBase = parseInt(parts[2], 10)
      
      for (let i = 0; i < qty; i++) {
        let pDate = new Date(yearBase, monthBase + i, dayBase)
        let y = pDate.getFullYear()
        let m = String(pDate.getMonth() + 1).padStart(2, '0')
        let d = String(pDate.getDate()).padStart(2, '0')
        let dtStr = `${y}-${m}-${d}`
        
        let desc = `${descricao.value} (${i+1}/${qty})`
        
        let payload = {
          tipo: tipo.value,
          valor: parseFloat(valorParcela.toFixed(2)),
          descricao: desc,
          data: dtStr
        }
        requests.push(api.post(`/lancamentos/${usuarioId}`, payload))
      }
      await Promise.all(requests)
      showToast('Lançamentos cadastrados com sucesso!', 'success')
    } else {
      const payload = {
        tipo: tipo.value,
        valor: parseFloat(valor.value),
        descricao: descricao.value,
        data: data.value
      }
      await api.post(`/lancamentos/${usuarioId}`, payload)
      showToast('Lançamento cadastrado com sucesso!', 'success')
    }
    
    // Limpar form
    descricao.value = ''
    valor.value = ''
    tipo.value = 'DESPESA'
    data.value = today
    parcelado.value = false
    numeroParcelas.value = 1
    
    await carregarSaldo()
    
    // Se o extrato estiver aberto no momento que ele cria um item, recarrega o extrato
    if (mostrarExtrato.value) {
      await carregarExtrato()
    } else {
      router.push('/')
    }
  } catch (err) {
    error.value = 'Erro ao cadastrar lançamento. Verifique os dados.'
    console.error("Erro interno:", err)
  } finally {
    loading.value = false
  }
}

/**
 * Função responsável por buscar os lançamentos do usuário e exibí-los
 * na mesma tela. Altera a flag mostrarExtrato.
 */
const carregarExtrato = async () => {
  loadingExtrato.value = true
  mostrarExtrato.value = true
  try {
    const usuarioId = sessionStorage.getItem('usuario_id')
    const response = await api.get(`/lancamentos/relatorio/${usuarioId}`)
    lancamentos.value = response.data
  } catch (err) {
    console.error("Erro ao carregar o extrato: ", err)
    error.value = "Falha ao exibir o extrato."
  } finally {
    loadingExtrato.value = false
  }
}

/**
 * Ordena o array de laçamentos da data mais recente (b.data) para a mais antiga (a.data).
 */
const extratoOrdenado = computed(() => {
  return [...lancamentos.value].sort((a, b) => new Date(b.data) - new Date(a.data))
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
    await carregarExtrato()
    await carregarSaldo()
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
    showToast('Lançamento excluído.', 'success')
    await carregarExtrato() // Recarrega os dados
    await carregarSaldo()
  } catch(err) {
    console.error("Erro ao excluir lançamento: ", err)
    showToast("Falha ao excluir o lançamento.", 'danger')
  }
}

/**
 * Utilitário de formatação de valores em reais
 */
const formatarMoeda = (val) => {
  return parseFloat(val).toLocaleString('pt-BR', { minimumFractionDigits: 2 })
}

/**
 * Utilitário de formatação da IsoDate (ex: 2023-05-12) para pt-BR
 */
const formatarData = (dataIso) => {
  if (!dataIso) return ''
  const partes = dataIso.split('-')
  return `${partes[2]}/${partes[1]}/${partes[0]}`
}
</script>

<style scoped>
.max-w-md {
  max-width: 500px;
}
.mx-auto {
  margin-left: auto;
  margin-right: auto;
}
.header-action {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 2rem;
  padding-bottom: 1rem;
  border-bottom: 1px solid var(--border-color);
}
.error-msg {
  color: var(--danger);
  font-size: 0.875rem;
  margin-bottom: 1rem;
  text-align: center;
}
.button-group {
  display: flex;
  flex-direction: column;
}
.btn-block {
  width: 100%;
}
.mt-3 {
  margin-top: 1rem;
}

.extrato-container {
  max-width: 800px;
  overflow-x: auto;
}

.relatorio-table {
  width: 100%;
  border-collapse: collapse;
  margin-top: 1rem;
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
.text-center { text-align: center !important; }
.font-weight-bold { font-weight: 600; }
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

/* Switch CSS */
.switch { position: relative; display: inline-block; width: 44px; height: 24px; }
.switch input { opacity: 0; width: 0; height: 0; }
.slider { position: absolute; cursor: pointer; top: 0; left: 0; right: 0; bottom: 0; background-color: rgba(255, 255, 255, 0.2); transition: .4s; border-radius: 24px; }
.slider:before { position: absolute; content: ""; height: 18px; width: 18px; left: 3px; bottom: 3px; background-color: white; transition: .4s; border-radius: 50%; box-shadow: 0 2px 4px rgba(0,0,0,0.2); }
input:checked + .slider { background-color: var(--primary); }
input:checked + .slider:before { transform: translateX(20px); }
</style>
