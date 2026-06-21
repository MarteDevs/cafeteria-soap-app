<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import LoginView from './views/LoginView.vue'

const currentUsuario = ref(null)
const router = useRouter()
const route = useRoute()

function checkSession() {
  const session = localStorage.getItem('usuario')
  if (session) {
    currentUsuario.value = JSON.parse(session)
  } else {
    currentUsuario.value = null
  }
}

onMounted(() => {
  checkSession()
  verificarPermisosRuta()
})

function onLoginSuccess(user) {
  currentUsuario.value = user
  if (user.rol === 'COCINERO') {
    router.push('/cocina')
  } else {
    router.push('/')
  }
}

function logout() {
  localStorage.removeItem('usuario')
  currentUsuario.value = null
  router.push('/')
}

function verificarPermisosRuta() {
  if (!currentUsuario.value) return

  const rol = currentUsuario.value.rol
  const path = route.path

  if (rol === 'MESERO') {
    // Mesero no puede ir a Cocina o Configuración/Usuarios
    if (path === '/cocina' || path === '/categorias' || path === '/productos' || path === '/usuarios') {
      router.push('/')
    }
  } else if (rol === 'COCINERO') {
    // Cocinero solo puede estar en Cocina
    if (path !== '/cocina') {
      router.push('/cocina')
    }
  }
}

watch(() => route.path, () => {
  verificarPermisosRuta()
})
</script>

<template>
  <!-- Modo No Autenticado: Login a pantalla completa -->
  <div v-if="!currentUsuario" class="h-screen w-screen flex items-center justify-center font-inter text-[#e4e1e6] bg-[#131316]">
    <!-- Fondo decorativo galáctico -->
    <div class="absolute inset-0 overflow-hidden pointer-events-none">
      <div class="absolute top-1/4 left-1/4 w-96 h-96 rounded-full bg-[#6b46c1]/10 blur-[100px]"></div>
      <div class="absolute bottom-1/4 right-1/4 w-96 h-96 rounded-full bg-[#ff7a45]/5 blur-[120px]"></div>
    </div>
    <LoginView @login-success="onLoginSuccess" />
  </div>

  <!-- Modo Autenticado: Layout con Sidebar y Contenido Central -->
  <div v-else class="flex h-screen overflow-hidden font-inter text-[#e4e1e6] bg-[#131316]">
    <!-- Fondo decorativo galáctico persistente -->
    <div class="absolute inset-0 overflow-hidden pointer-events-none">
      <div class="absolute top-1/4 left-1/4 w-96 h-96 rounded-full bg-[#6b46c1]/5 blur-[100px]"></div>
      <div class="absolute bottom-1/4 right-1/4 w-96 h-96 rounded-full bg-[#ff7a45]/3 blur-[120px]"></div>
    </div>

    <!-- Sidebar -->
    <aside class="w-[280px] flex-shrink-0 glass-panel border-l-0 border-y-0 rounded-none rounded-r-3xl flex flex-col z-20">
      <div class="p-8 flex items-center gap-3">
        <div class="w-10 h-10 rounded-xl bg-gradient-to-br from-[#ff7a45] to-[#6b46c1] flex items-center justify-center shadow-[0_0_15px_rgba(255,122,69,0.5)]">
          <svg class="w-6 h-6 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 10h18M3 14h18m-9-4v8m-7 0h14a2 2 0 002-2V8a2 2 0 00-2-2H5a2 2 0 00-2 2v8a2 2 0 002 2z"></path>
          </svg>
        </div>
        <h1 class="font-outfit font-bold text-xl tracking-wide text-white">Ambrosia</h1>
      </div>
      
      <nav class="flex-1 px-4 space-y-2 mt-4">
        <!-- Links para MESERO o ADMINISTRADOR -->
        <template v-if="currentUsuario.rol === 'MESERO' || currentUsuario.rol === 'ADMINISTRADOR'">
          <RouterLink to="/" class="nav-item">
            <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 6a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2H6a2 2 0 01-2-2V6zM14 6a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2h-2a2 2 0 01-2-2V6zM4 16a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2H6a2 2 0 01-2-2v-2zM14 16a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2h-2a2 2 0 01-2-2v-2z"></path>
            </svg>
            Dashboard
          </RouterLink>
          <RouterLink to="/nuevo-pedido" class="nav-item text-[#ff7a45]">
            <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6v6m0 0v6m0-6h6m-6 0H6"></path>
            </svg>
            Nuevo Pedido
          </RouterLink>
          <RouterLink to="/pedidos" class="nav-item">
            <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2m-3 7h3m-3 4h3m-6-4h.01M9 16h.01"></path>
            </svg>
            Historial
          </RouterLink>
        </template>

        <!-- Links para COCINERO o ADMINISTRADOR -->
        <template v-if="currentUsuario.rol === 'COCINERO' || currentUsuario.rol === 'ADMINISTRADOR'">
          <RouterLink to="/cocina" class="nav-item">
            <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 14v6m-3-3h6M6 10h2a2 2 0 002-2V6a2 2 0 00-2-2H6a2 2 0 00-2 2v2a2 2 0 002 2zm10 0h2a2 2 0 002-2V6a2 2 0 00-2-2h-2a2 2 0 00-2 2v2a2 2 0 002 2zM6 20h2a2 2 0 002-2v-2a2 2 0 00-2-2H6a2 2 0 00-2 2v2a2 2 0 002 2z"></path>
            </svg>
            Cocina
          </RouterLink>
        </template>
      </nav>

      <!-- Panel de Sesión y Configuración en el footer del Sidebar -->
      <div class="p-4 mt-auto space-y-3">
        <!-- Tarjeta de Sesión de Usuario -->
        <div class="px-4 py-3 border border-white/10 rounded-2xl text-sm bg-white/5 flex flex-col gap-1.5 backdrop-blur-md">
          <div class="flex items-center gap-2">
            <span class="w-2.5 h-2.5 rounded-full bg-[#ff7a45] shadow-[0_0_8px_rgba(255,122,69,0.8)]"></span>
            <span class="font-outfit font-bold text-white tracking-wide text-sm">{{ currentUsuario.nombre }}</span>
          </div>
          <div class="text-xs uppercase font-medium tracking-wider text-[#a09fb9]/70">{{ currentUsuario.rol }}</div>
          <button @click="logout" class="mt-2 text-left text-xs font-semibold text-[#ffb4ab] hover:text-white transition-colors flex items-center gap-1.5">
            <svg class="w-3.5 h-3.5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 16l4-4m0 0l-4-4m4 4H7m6 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h4a3 3 0 013 3v1" />
            </svg>
            Cerrar Sesión
          </button>
        </div>

        <!-- Opciones de Configuración (Solo ADMINISTRADOR) -->
        <div class="px-4 py-3 border border-white/10 rounded-2xl text-sm text-[#a09fb9] bg-[#131316]/50" v-if="currentUsuario.rol === 'ADMINISTRADOR'">
          <div class="font-outfit font-bold text-white text-xs uppercase tracking-wider mb-2">Administración</div>
          <RouterLink to="/usuarios" class="block hover:text-white transition-colors py-1 flex items-center gap-2">
            <span class="w-1.5 h-1.5 rounded-full bg-white/30"></span> Usuarios
          </RouterLink>
          <RouterLink to="/categorias" class="block hover:text-white transition-colors py-1 flex items-center gap-2">
            <span class="w-1.5 h-1.5 rounded-full bg-white/30"></span> Categorías
          </RouterLink>
          <RouterLink to="/productos" class="block hover:text-white transition-colors py-1 flex items-center gap-2">
            <span class="w-1.5 h-1.5 rounded-full bg-white/30"></span> Productos
          </RouterLink>
        </div>
      </div>
    </aside>

    <!-- Main Content -->
    <main class="flex-1 h-full overflow-y-auto relative z-10 scroll-smooth">
      <div class="p-8 max-w-7xl mx-auto min-h-full">
        <RouterView />
      </div>
    </main>
  </div>
</template>

<style>
@reference "./assets/main.css";

.nav-item {
  @apply flex items-center gap-3 px-4 py-3 rounded-xl font-medium text-[#c6c4df] transition-all duration-200 hover:bg-white/5 hover:text-white;
}
.router-link-active.nav-item {
  @apply bg-[#ff7a45]/10 text-[#ff7a45] border border-[#ff7a45]/20 shadow-[inset_0_0_10px_rgba(255,122,69,0.1)];
}
</style>
