<template>
  <div>
    <Header />
    <main class="container animate-fade-in">
      <div class="glass-panel max-w-md mx-auto">
        <div class="header-action">
          <h2>Perfil do Usuário</h2>
          <button class="btn btn-secondary btn-sm" @click="$router.push('/')">Voltar</button>
        </div>

        <div v-if="loading" class="text-center text-muted" style="padding: 2rem;">
          Carregando informações...
        </div>

        <div v-else>
          <!-- Modo de Visualização -->
          <div v-if="!isEditing">
            <div class="user-info-card">
              <div v-if="usuario.foto" class="avatar-circle">
                <img :src="usuario.foto" alt="Foto de perfil" class="avatar-img" />
              </div>
              <div v-else class="avatar-circle">
                {{ usuario.nome ? usuario.nome.charAt(0).toUpperCase() : 'U' }}
              </div>
              
              <h3>{{ usuario.nome }}</h3>
              <p class="text-muted">{{ usuario.email }}</p>
            </div>

            <div class="actions-group">
              <button class="btn btn-primary" style="width: 100%; margin-top: 1rem;" @click="iniciarEdicao">
                Editar Perfil
              </button>
              <button class="btn btn-secondary" style="width: 100%;" @click="gerarRelatorioSistema" :disabled="gerandoRelatorio">
                {{ gerandoRelatorio ? 'Gerando...' : 'Gerar Relatório do Sistema' }}
              </button>
            </div>
          </div>

          <!-- Modo de Edição -->
          <form v-else @submit.prevent="salvarEdicao" class="edit-form">
            <div class="form-group text-center">
              <div class="avatar-preview-container">
                <div v-if="editForm.foto" class="avatar-circle mx-auto">
                  <img :src="editForm.foto" alt="Preview foto" class="avatar-img" />
                </div>
                <div v-else class="avatar-circle mx-auto">
                  {{ editForm.nome ? editForm.nome.charAt(0).toUpperCase() : 'U' }}
                </div>
              </div>
              <label for="fotoInput" class="btn btn-sm btn-secondary mt-2" style="cursor: pointer;">
                Alterar Foto
              </label>
              <input type="file" id="fotoInput" accept="image/*" @change="onFileChange" style="display: none;" />
            </div>

            <div class="form-group">
              <label class="form-label">Nome Completo</label>
              <input type="text" v-model="editForm.nome" class="form-control" required placeholder="Seu nome" />
            </div>

            <div class="form-group">
              <label class="form-label">E-mail</label>
              <input type="email" v-model="editForm.email" class="form-control" required placeholder="exemplo@email.com" />
            </div>

            <div class="form-group">
              <label class="form-label">Nova Senha (Opcional)</label>
              <input type="password" v-model="editForm.senha" class="form-control" placeholder="Deixe em branco para manter a atual" />
            </div>
            
            <div v-if="errorMsg" class="alert-danger mt-2 mb-2" style="padding: 10px; border-radius: 4px;">
              {{ errorMsg }}
            </div>

            <div class="actions-group" style="margin-top: 1.5rem; flex-direction: row; gap: 0.5rem;">
              <button type="submit" class="btn btn-primary" style="flex: 1;" :disabled="saving">
                {{ saving ? 'Salvando...' : 'Salvar Alterações' }}
              </button>
              <button type="button" class="btn btn-secondary" style="flex: 1;" @click="cancelarEdicao" :disabled="saving">
                Cancelar
              </button>
            </div>
          </form>
        </div>
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref, onMounted, reactive } from 'vue'
import Header from '../components/Header.vue'
import api from '../api/axios'
import jsPDF from 'jspdf'
import autoTable from 'jspdf-autotable'
import { useToast } from '../composables/useToast'

const usuario = ref({})
const loading = ref(true)
const gerandoRelatorio = ref(false)
const { showToast } = useToast()

const isEditing = ref(false)
const saving = ref(false)
const errorMsg = ref('')

const editForm = reactive({
  nome: '',
  email: '',
  senha: '',
  foto: ''
})

const getUsuarioId = () => sessionStorage.getItem('usuario_id')

const loadUserData = async () => {
  try {
    const usuarioId = getUsuarioId()
    const response = await api.get(`/auth/${usuarioId}`)
    usuario.value = response.data
  } catch (error) {
    console.error("Erro ao buscar dados do usuário: ", error)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadUserData()
})

const iniciarEdicao = () => {
  editForm.nome = usuario.value.nome || ''
  editForm.email = usuario.value.email || ''
  editForm.senha = '' // Senha não deve ser carregada do back (JsonIgnore)
  editForm.foto = usuario.value.foto || ''
  errorMsg.value = ''
  isEditing.value = true
}

const cancelarEdicao = () => {
  isEditing.value = false
  errorMsg.value = ''
}

const gerarRelatorioSistema = async () => {
  gerandoRelatorio.value = true
  try {
    const usuarioId = getUsuarioId()
    const { data: dashboard } = await api.get(`/lancamentos/dashboard/${usuarioId}`)
    
    const doc = new jsPDF()
    doc.setFontSize(18)
    doc.text('Relatorio Geral do Sistema', 14, 22)
    
    doc.setFontSize(12)
    doc.text(`Usuário: ${usuario.value.nome}`, 14, 32)
    doc.text(`E-mail: ${usuario.value.email}`, 14, 38)
    
    const formatter = new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' })
    
    doc.text(`Resumo Financeiro Financeiro:`, 14, 50)
    
    autoTable(doc, {
      startY: 55,
      head: [['Dimensão Estratégica', 'Métrica']],
      body: [
        ['Saldo Atual', formatter.format(dashboard.saldo)],
        ['Total de Receitas', formatter.format(dashboard.totalReceitas)],
        ['Total de Despesas', formatter.format(dashboard.totalDespesas)],
        ['Total de Lançamentos Registrados no Sistema', dashboard.ultimosLancamentos.length.toString()]
      ],
      theme: 'grid',
      headStyles: { fillColor: [41, 128, 185] }
    })
    
    doc.save('relatorio-sistema.pdf')
  } catch (error) {
    console.error("Erro ao gerar relatório do sistema:", error)
    showToast("Ocorreu um erro ao gerar o relatório. Tente novamente.", 'danger')
  } finally {
    gerandoRelatorio.value = false
  }
}

const onFileChange = (e) => {
  const file = e.target.files[0]
  if (!file) return

  const reader = new FileReader()
  reader.onload = (event) => {
    editForm.foto = event.target.result // Base64 string
  }
  reader.readAsDataURL(file)
}

const salvarEdicao = async () => {
  saving.value = true
  errorMsg.value = ''
  try {
    const usuarioId = getUsuarioId()
    const response = await api.put(`/auth/${usuarioId}`, {
      nome: editForm.nome,
      email: editForm.email,
      senha: editForm.senha,
      foto: editForm.foto
    })
    // Atualiza a visualização local para refletir o backend
    usuario.value = response.data
    // Se o nome no storage for usado em algum lugar (ex: Header), atualiza.
    sessionStorage.setItem('usuario_nome', usuario.value.nome)
    
    isEditing.value = false
    showToast('Perfil atualizado com sucesso!', 'success')
  } catch (error) {
    console.error("Erro ao atualizar o perfil:", error)
    errorMsg.value = "Falha ao salvar. Verifique os dados e tente novamente."
  } finally {
    saving.value = false
  }
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
.user-info-card {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 2rem;
  background: rgba(255, 255, 255, 0.02);
  border-radius: var(--radius-md);
  margin-bottom: 1.5rem;
}
.avatar-circle {
  width: 100px;
  height: 100px;
  border-radius: 50%;
  background: linear-gradient(135deg, var(--primary), var(--secondary));
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 2.5rem;
  font-weight: bold;
  margin-bottom: 1rem;
  overflow: hidden;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.3);
}
.avatar-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
.actions-group {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}
.mt-2 {
  margin-top: 0.5rem;
}
.mb-2 {
  margin-bottom: 0.5rem;
}
.text-center {
  text-align: center;
}
.alert-danger {
  background-color: rgba(239, 68, 68, 0.2);
  color: var(--danger);
  border-left: 4px solid var(--danger);
}
.avatar-preview-container {
  display: flex;
  justify-content: center;
  margin-bottom: 0.5rem;
}
</style>
