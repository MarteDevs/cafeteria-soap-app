<template>
  <div class="flex h-screen overflow-hidden font-inter text-[#e4e1e6]">
    <!-- Sidebar -->
    <aside class="w-[280px] flex-shrink-0 glass-panel border-l-0 border-y-0 rounded-none rounded-r-3xl flex flex-col z-20">
      <div class="p-8 flex items-center gap-3">
        <div class="w-10 h-10 rounded-xl bg-gradient-to-br from-[#ff7a45] to-[#6b46c1] flex items-center justify-center shadow-[0_0_15px_rgba(255,122,69,0.5)]">
          <svg class="w-6 h-6 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 10h18M3 14h18m-9-4v8m-7 0h14a2 2 0 002-2V8a2 2 0 00-2-2H5a2 2 0 00-2 2v8a2 2 0 002 2z"></path></svg>
        </div>
        <h1 class="font-outfit font-bold text-xl tracking-wide text-white">Ambrosia</h1>
      </div>
      
      <nav class="flex-1 px-4 space-y-2 mt-4">
        <RouterLink to="/" class="nav-item">
          <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 6a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2H6a2 2 0 01-2-2V6zM14 6a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2h-2a2 2 0 01-2-2V6zM4 16a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2H6a2 2 0 01-2-2v-2zM14 16a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2h-2a2 2 0 01-2-2v-2z"></path></svg>
          Dashboard
        </RouterLink>
        <RouterLink to="/nuevo-pedido" class="nav-item text-[#ff7a45]">
          <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6v6m0 0v6m0-6h6m-6 0H6"></path></svg>
          Nuevo Pedido
        </RouterLink>
        <RouterLink to="/pedidos" class="nav-item">
          <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2m-3 7h3m-3 4h3m-6-4h.01M9 16h.01"></path></svg>
          Historial
        </RouterLink>
        <RouterLink to="/cocina" class="nav-item">
          <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 14v6m-3-3h6M6 10h2a2 2 0 002-2V6a2 2 0 00-2-2H6a2 2 0 00-2 2v2a2 2 0 002 2zm10 0h2a2 2 0 002-2V6a2 2 0 00-2-2h-2a2 2 0 00-2 2v2a2 2 0 002 2zM6 20h2a2 2 0 002-2v-2a2 2 0 00-2-2H6a2 2 0 00-2 2v2a2 2 0 002 2z"></path></svg>
          Cocina
        </RouterLink>
      </nav>

      <div class="p-4 mt-auto">
        <div class="px-4 py-2 border border-white/10 rounded-xl text-sm text-[#a09fb9]">
          <div class="font-semibold text-white">Configuración</div>
          <RouterLink to="/categorias" class="block mt-2 hover:text-white transition-colors">Categorías</RouterLink>
          <RouterLink to="/productos" class="block mt-1 hover:text-white transition-colors">Productos</RouterLink>
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
