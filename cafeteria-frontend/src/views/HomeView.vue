<template>
  <div class="space-y-8 font-inter">
    <div class="glass-panel p-10 relative overflow-hidden border border-[#ff7a45]/20 shadow-[0_0_30px_rgba(255,122,69,0.1)]">
      <div class="absolute -top-24 -right-24 w-64 h-64 bg-[#ff7a45] rounded-full mix-blend-screen filter blur-[100px] opacity-20"></div>
      <div class="absolute -bottom-24 -left-24 w-64 h-64 bg-[#6b46c1] rounded-full mix-blend-screen filter blur-[100px] opacity-20"></div>
      
      <div class="relative z-10">
        <h1 class="text-5xl font-outfit font-bold text-white mb-4">Bienvenido a <span class="mars-gradient-text">Cafetería Ambrosia</span></h1>
        <p class="text-xl text-[#c6c4df]">Gestión rápida y eficiente de pedidos y productos en el espacio.</p>
      </div>
    </div>

    <div class="grid grid-cols-1 md:grid-cols-3 gap-6">
      <RouterLink to="/nuevo-pedido" class="group glass-card p-8 flex flex-col relative overflow-hidden">
        <div class="absolute inset-0 bg-gradient-to-br from-[#ff7a45]/10 to-transparent opacity-0 group-hover:opacity-100 transition-opacity duration-500"></div>
        <div class="relative z-10 flex flex-col h-full">
          <div class="flex items-center justify-between mb-6">
            <div class="p-4 bg-[#ff7a45]/20 rounded-2xl border border-[#ff7a45]/30 text-[#ff7a45] shadow-[0_0_15px_rgba(255,122,69,0.3)]">
              <svg class="w-8 h-8" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4"></path></svg>
            </div>
            <svg class="w-6 h-6 text-white/20 group-hover:text-white/80 group-hover:translate-x-2 transition-all duration-300" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7"></path></svg>
          </div>
          <h2 class="text-2xl font-outfit font-bold text-white mb-2">Nuevo Pedido</h2>
          <p class="text-[#a09fb9]">Crear una nueva orden de cliente</p>
        </div>
      </RouterLink>

      <RouterLink to="/cocina" class="group glass-card p-8 flex flex-col relative overflow-hidden">
        <div class="absolute inset-0 bg-gradient-to-br from-[#6b46c1]/10 to-transparent opacity-0 group-hover:opacity-100 transition-opacity duration-500"></div>
        <div class="relative z-10 flex flex-col h-full">
          <div class="flex items-center justify-between mb-6">
            <div class="p-4 bg-[#6b46c1]/20 rounded-2xl border border-[#6b46c1]/30 text-[#d9cbf6] shadow-[0_0_15px_rgba(107,70,193,0.3)]">
              <svg class="w-8 h-8" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 11H5m14 0a2 2 0 012 2v6a2 2 0 01-2 2H5a2 2 0 01-2-2v-6a2 2 0 012-2m14 0V9a2 2 0 00-2-2M5 11V9a2 2 0 002-2m0 0V5a2 2 0 012-2h6a2 2 0 012 2v2M7 7h10"></path></svg>
            </div>
            <svg class="w-6 h-6 text-white/20 group-hover:text-white/80 group-hover:translate-x-2 transition-all duration-300" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7"></path></svg>
          </div>
          <h2 class="text-2xl font-outfit font-bold text-white mb-2">Cocina</h2>
          <p class="text-[#a09fb9]">Gestionar y entregar pedidos</p>
        </div>
      </RouterLink>

      <RouterLink to="/pedidos" class="group glass-card p-8 flex flex-col relative overflow-hidden">
        <div class="absolute inset-0 bg-gradient-to-br from-white/5 to-transparent opacity-0 group-hover:opacity-100 transition-opacity duration-500"></div>
        <div class="relative z-10 flex flex-col h-full">
          <div class="flex items-center justify-between mb-6">
            <div class="p-4 bg-white/10 rounded-2xl border border-white/10 text-white shadow-[0_0_15px_rgba(255,255,255,0.1)]">
              <svg class="w-8 h-8" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2m-3 7h3m-3 4h3m-6-4h.01M9 16h.01"></path></svg>
            </div>
            <svg class="w-6 h-6 text-white/20 group-hover:text-white/80 group-hover:translate-x-2 transition-all duration-300" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7"></path></svg>
          </div>
          <h2 class="text-2xl font-outfit font-bold text-white mb-2">Historial</h2>
          <p class="text-[#a09fb9]">Revisar todos los pedidos</p>
        </div>
      </RouterLink>
    </div>
  </div>
</template>
