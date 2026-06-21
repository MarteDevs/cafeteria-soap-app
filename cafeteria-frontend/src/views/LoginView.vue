<script setup>
import { ref } from 'vue'
import { login } from '../services/usuarioService.js'

const emit = defineEmits(['login-success'])

const username = ref('')
const password = ref('')
const loading = ref(false)
const error = ref('')

async function handleLogin() {
  if (!username.value || !password.value) {
    error.value = 'Por favor, ingresa tu usuario y contraseña.'
    return
  }

  error.value = ''
  loading.value = true

  try {
    const user = await login(username.value, password.value)
    if (user) {
      localStorage.setItem('usuario', JSON.stringify(user))
      emit('login-success', user)
    } else {
      error.value = 'Usuario o contraseña incorrectos.'
    }
  } catch (err) {
    error.value = 'Error al conectar con el servidor. Inténtalo de nuevo.'
    console.error(err)
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="w-full max-w-md p-8 glass-panel border border-white/15 shadow-[0_0_50px_rgba(255,122,69,0.15)] relative overflow-hidden animate-fade-in">
    <!-- Ecos de fondo/Gradientes circulares -->
    <div class="absolute -top-24 -left-24 w-48 h-48 rounded-full bg-[#ff7a45]/10 blur-3xl"></div>
    <div class="absolute -bottom-24 -right-24 w-48 h-48 rounded-full bg-[#6b46c1]/10 blur-3xl"></div>

    <div class="text-center mb-8 relative z-10">
      <div class="w-16 h-16 bg-gradient-to-br from-[#ff7a45] to-[#6b46c1] flex items-center justify-center rounded-2xl mx-auto mb-4 shadow-[0_0_20px_rgba(255,122,69,0.4)]">
        <svg class="w-8 h-8 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 15v2m-6 4h12a2 2 0 002-2v-6a2 2 0 00-2-2H6a2 2 0 00-2 2v6a2 2 0 002 2zm10-10V7a4 4 0 00-8 0v4h8z" />
        </svg>
      </div>
      <h2 class="text-3xl font-outfit font-bold text-white tracking-wide">Ambrosia</h2>
      <p class="text-[#a09fb9] text-sm mt-1">Estación de Control e Ingreso</p>
    </div>

    <!-- Mensajes de Error -->
    <div v-if="error" class="mb-6 p-4 rounded-xl bg-[#93000a]/20 border border-[#93000a] text-[#ffb4ab] text-sm text-center backdrop-blur-md animate-shake">
      {{ error }}
    </div>

    <form @submit.prevent="handleLogin" class="space-y-6 relative z-10">
      <div>
        <label class="block text-sm font-medium text-[#c6c4df] mb-2">Usuario</label>
        <div class="relative">
          <span class="absolute inset-y-0 left-0 pl-4 flex items-center text-[#a09fb9]">
            <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" />
            </svg>
          </span>
          <input 
            v-model="username" 
            required 
            type="text" 
            placeholder="Ej. mesero1" 
            class="w-full bg-[#131316]/80 border border-white/10 rounded-xl pl-12 pr-4 py-3.5 text-white focus:outline-none focus:border-[#ff7a45] focus:shadow-[0_0_15px_rgba(255,122,69,0.25)] transition-all duration-300"
            :disabled="loading"
          >
        </div>
      </div>

      <div>
        <label class="block text-sm font-medium text-[#c6c4df] mb-2">Contraseña</label>
        <div class="relative">
          <span class="absolute inset-y-0 left-0 pl-4 flex items-center text-[#a09fb9]">
            <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 15v2m-6 4h12a2 2 0 002-2v-6a2 2 0 00-2-2H6a2 2 0 00-2 2v6a2 2 0 002 2zm10-10V7a4 4 0 00-8 0v4h8z" />
            </svg>
          </span>
          <input 
            v-model="password" 
            required 
            type="password" 
            placeholder="••••••••" 
            class="w-full bg-[#131316]/80 border border-white/10 rounded-xl pl-12 pr-4 py-3.5 text-white focus:outline-none focus:border-[#ff7a45] focus:shadow-[0_0_15px_rgba(255,122,69,0.25)] transition-all duration-300"
            :disabled="loading"
          >
        </div>
      </div>

      <button 
        type="submit" 
        :disabled="loading" 
        class="w-full py-3.5 rounded-xl font-outfit font-bold text-white bg-gradient-to-r from-[#ff7a45] to-[#6b46c1] hover:brightness-110 shadow-[0_0_20px_rgba(255,122,69,0.3)] hover:shadow-[0_0_25px_rgba(255,122,69,0.5)] transition-all duration-300 disabled:opacity-50 disabled:cursor-not-allowed flex items-center justify-center gap-2"
      >
        <span v-if="loading" class="w-5 h-5 border-2 border-white border-t-transparent rounded-full animate-spin"></span>
        <span>{{ loading ? 'Autenticando...' : 'Iniciar Sesión' }}</span>
      </button>
    </form>

    <div class="mt-8 pt-6 border-t border-white/5 text-center text-xs text-[#a09fb9]/70 relative z-10">
      <div class="font-medium text-white/50 mb-2">Credenciales de prueba:</div>
      <div class="grid grid-cols-3 gap-2">
        <div class="bg-white/5 p-2 rounded-lg">
          <div class="font-semibold text-white">Mesero</div>
          <div>mesero1 / 123</div>
        </div>
        <div class="bg-white/5 p-2 rounded-lg">
          <div class="font-semibold text-white">Cocinero</div>
          <div>cocinero1 / 123</div>
        </div>
        <div class="bg-white/5 p-2 rounded-lg">
          <div class="font-semibold text-white">Admin</div>
          <div>admin / 123</div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
@keyframes shake {
  0%, 100% { transform: translateX(0); }
  25% { transform: translateX(-4px); }
  75% { transform: translateX(4px); }
}
.animate-shake {
  animation: shake 0.3s ease-in-out;
}
</style>
