<script setup>
import { ref, onMounted } from 'vue'
import { listarUsuarios, crearUsuario, eliminarUsuario } from '../services/usuarioService.js'

const users = ref([])
const loading = ref(false)
const error = ref('')
const success = ref('')

const form = ref({
  username: '',
  password: '',
  nombre: '',
  rol: 'MESERO'
})

const currentLoggedInUser = ref(null)

async function cargarUsuarios() {
  loading.value = true
  error.value = ''
  try {
    users.value = await listarUsuarios()
  } catch (err) {
    error.value = 'Error al cargar los usuarios del servidor.'
    console.error(err)
  } finally {
    loading.value = false
  }
}

async function guardar() {
  error.value = ''
  success.value = ''
  
  if (!form.value.username || !form.value.password || !form.value.nombre || !form.value.rol) {
    error.value = 'Todos los campos son obligatorios.'
    return
  }

  try {
    loading.value = true
    const result = await crearUsuario(form.value.username, form.value.password, form.value.nombre, form.value.rol)
    if (result) {
      success.value = 'Usuario registrado con éxito.'
      form.value = { username: '', password: '', nombre: '', rol: 'MESERO' }
      await cargarUsuarios()
    } else {
      error.value = 'El nombre de usuario ya está registrado.'
    }
  } catch (err) {
    error.value = 'Error al registrar el usuario.'
    console.error(err)
  } finally {
    loading.value = false
  }
}

async function borrar(id, username) {
  if (currentLoggedInUser.value && currentLoggedInUser.value.id === id) {
    alert('No puedes eliminar tu propio usuario administrador en sesión.')
    return
  }

  if (!confirm(`¿Estás seguro de eliminar al usuario "${username}"?`)) return

  error.value = ''
  success.value = ''
  
  try {
    loading.value = true
    const msg = await eliminarUsuario(id)
    success.value = msg
    await cargarUsuarios()
  } catch (err) {
    error.value = 'Error al eliminar el usuario.'
    console.error(err)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  const session = localStorage.getItem('usuario')
  if (session) {
    currentLoggedInUser.value = JSON.parse(session)
  }
  cargarUsuarios()
})
</script>

<template>
  <div class="max-w-6xl mx-auto font-inter">
    <div class="flex justify-between items-end mb-8">
      <div>
        <h1 class="text-4xl font-outfit font-bold text-white mb-2">Gestión de Usuarios</h1>
        <p class="text-[#a09fb9]">Administra los accesos de meseros, cocineros y administradores.</p>
      </div>
    </div>

    <!-- Alertas -->
    <div v-if="error" class="bg-[#93000a]/20 text-[#ffb4ab] border border-[#93000a] p-4 rounded-xl mb-6 backdrop-blur-md">
      {{ error }}
    </div>
    <div v-if="success" class="bg-[#154b2d]/30 text-[#82e8a6] border border-[#154b2d] p-4 rounded-xl mb-6 backdrop-blur-md">
      {{ success }}
    </div>

    <div class="grid grid-cols-1 lg:grid-cols-3 gap-8">
      <!-- Formulario de Registro (1/3) -->
      <div class="lg:col-span-1">
        <div class="glass-panel p-6 sticky top-8">
          <h3 class="text-2xl font-outfit font-bold text-white mb-6">Nuevo Usuario</h3>
          <form @submit.prevent="guardar" class="space-y-4">
            <div>
              <label class="block text-sm font-medium text-[#c6c4df] mb-2">Nombre Completo</label>
              <input v-model="form.nombre" required type="text" class="w-full bg-[#131316] border border-white/10 rounded-xl px-4 py-3 text-white focus:outline-none focus:border-[#ff7a45] transition-colors" placeholder="Ej. Juan Pérez">
            </div>

            <div>
              <label class="block text-sm font-medium text-[#c6c4df] mb-2">Nombre de Usuario (Login)</label>
              <input v-model="form.username" required type="text" class="w-full bg-[#131316] border border-white/10 rounded-xl px-4 py-3 text-white focus:outline-none focus:border-[#ff7a45] transition-colors" placeholder="Ej. jperez">
            </div>

            <div>
              <label class="block text-sm font-medium text-[#c6c4df] mb-2">Contraseña</label>
              <input v-model="form.password" required type="password" class="w-full bg-[#131316] border border-white/10 rounded-xl px-4 py-3 text-white focus:outline-none focus:border-[#ff7a45] transition-colors" placeholder="••••••••">
            </div>

            <div>
              <label class="block text-sm font-medium text-[#c6c4df] mb-2">Rol / Permisos</label>
              <select v-model="form.rol" required class="w-full bg-[#131316] border border-white/10 rounded-xl px-4 py-3 text-white focus:outline-none focus:border-[#ff7a45] transition-colors appearance-none">
                <option value="MESERO">Mesero (Toma pedidos)</option>
                <option value="COCINERO">Cocinero (Gestiona cocina)</option>
                <option value="ADMINISTRADOR">Administrador (Control total)</option>
              </select>
            </div>

            <button type="submit" :disabled="loading" class="w-full btn-primary mt-6">
              {{ loading ? 'Registrando...' : 'Registrar Usuario' }}
            </button>
          </form>
        </div>
      </div>

      <!-- Tabla de Listado (2/3) -->
      <div class="lg:col-span-2">
        <div class="glass-panel overflow-hidden">
          <div class="overflow-x-auto">
            <table class="w-full text-left border-collapse">
              <thead>
                <tr class="bg-[#1a1a2e]/80 border-b border-white/10 text-[#a09fb9] text-sm uppercase tracking-wider font-outfit">
                  <th class="p-5 font-semibold">Usuario</th>
                  <th class="p-5 font-semibold">Nombre Completo</th>
                  <th class="p-5 font-semibold">Rol</th>
                  <th class="p-5 font-semibold text-right">Acciones</th>
                </tr>
              </thead>
              <tbody class="divide-y divide-white/5">
                <tr v-for="user in users" :key="user.id" class="hover:bg-white/5 transition-colors group">
                  <td class="p-5">
                    <div class="font-outfit font-bold text-white text-lg">@{{ user.username }}</div>
                    <div class="text-xs text-[#a09fb9]">ID: {{ user.id }}</div>
                  </td>
                  <td class="p-5 font-medium text-[#c6c4df]">{{ user.nombre }}</td>
                  <td class="p-5">
                    <span :class="['inline-flex items-center px-3 py-1 rounded-full text-xs font-semibold border', 
                      user.rol === 'ADMINISTRADOR' ? 'bg-[#ff7a45]/20 text-[#ff7a45] border-[#ff7a45]/30' : 
                      user.rol === 'COCINERO' ? 'bg-[#6b46c1]/20 text-[#e9ddff] border-[#6b46c1]/30' : 
                      'bg-white/5 text-[#c6c4df] border-white/10']">
                      {{ user.rol }}
                    </span>
                  </td>
                  <td class="p-5 text-right">
                    <button 
                      @click="borrar(user.id, user.username)" 
                      class="p-2 rounded-lg bg-white/5 text-[#ffb4ab] hover:text-white hover:bg-[#93000a]/80 border border-white/5 transition-all"
                      :disabled="currentLoggedInUser && currentLoggedInUser.id === user.id"
                      :class="{ 'opacity-30 cursor-not-allowed': currentLoggedInUser && currentLoggedInUser.id === user.id }"
                      title="Eliminar usuario"
                    >
                      <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
                      </svg>
                    </button>
                  </td>
                </tr>
                <tr v-if="users.length === 0">
                  <td colspan="4" class="p-12 text-center text-[#a09fb9]">
                    <div class="flex flex-col items-center gap-3">
                      <div class="animate-spin rounded-full h-8 w-8 border-b-2 border-[#ff7a45]" v-if="loading"></div>
                      <p v-else>No hay usuarios registrados</p>
                    </div>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
