<template>
  <div>
    <Header />
    <main class="container animate-fade-in">
      <div class="glass-panel max-w-md mx-auto">
        <div class="header-action">
          <h2>Calculadora de Salário</h2>
          <button class="btn btn-secondary btn-sm" @click="$router.push('/')">Voltar</button>
        </div>

        <form @submit.prevent="calcularSalario">
          <div class="form-group">
            <label>Qual o valor da sua hora? (R$)</label>
            <input type="number" step="0.01" class="form-control" v-model="valorHora" required placeholder="Ex: 25.50" />
          </div>

          <div class="form-group">
            <label>Quantas horas você trabalha por dia?</label>
            <input type="number" class="form-control" v-model="horasPorDia" required placeholder="Ex: 8" />
          </div>

          <div class="form-group">
            <label>Quantos dias você trabalha no mês?</label>
            <input type="number" class="form-control" v-model="diasPorMes" required placeholder="Ex: 22" />
          </div>

          <button type="submit" class="btn btn-primary" style="width: 100%; margin-top: 1rem;">
            Calcular
          </button>
        </form>

        <div v-if="resultado > 0" class="resultado-box animate-fade-in mt-4">
          <h4 class="text-muted">Seu salário neste mês será de:</h4>
          <h2 class="text-success pt-2">R$ {{ resultadoFormatado }}</h2>
        </div>
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import Header from '../components/Header.vue'

/**
 * Calculadora de Salario Mensal baseada em horas.
 */

const valorHora = ref('')
const horasPorDia = ref('')
const diasPorMes = ref('')
const resultado = ref(0)

const calcularSalario = () => {
  const hr = parseFloat(valorHora.value)
  const qtdHr = parseFloat(horasPorDia.value)
  const qtdDias = parseFloat(diasPorMes.value)

  if (hr && qtdHr && qtdDias) {
    resultado.value = hr * qtdHr * qtdDias
  } else {
    resultado.value = 0
  }
}

const resultadoFormatado = computed(() => {
  return parseFloat(resultado.value).toLocaleString('pt-BR', { minimumFractionDigits: 2, maximumFractionDigits: 2 })
})
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
.resultado-box {
  background: rgba(16, 185, 129, 0.1);
  border: 1px solid rgba(16, 185, 129, 0.3);
  border-radius: var(--radius-md);
  padding: 1.5rem;
  margin-top: 2rem;
  text-align: center;
}
.pt-2 { padding-top: 0.5rem; }
</style>
