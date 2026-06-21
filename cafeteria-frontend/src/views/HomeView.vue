<template>
  <div class="space-y-8">
    <div class="bg-white rounded-2xl shadow-sm border border-slate-200 p-8">
      <h1 class="text-3xl font-bold text-slate-800 mb-2">Bienvenido a Cafetería Mars</h1>
      <p class="text-slate-500">Gestión rápida y eficiente de pedidos y productos.</p>
    </div>

    <div class="grid grid-cols-1 md:grid-cols-3 gap-6">
      <RouterLink to="/nuevo-pedido" class="group bg-gradient-to-br from-amber-500 to-amber-600 rounded-2xl p-6 text-white shadow-md hover:shadow-xl transition-all duration-300 transform hover:-translate-y-1">
        <div class="flex items-center justify-between mb-4">
          <div class="p-3 bg-white/20 rounded-xl backdrop-blur-sm">
            <svg class="w-8 h-8" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4"></path></svg>
          </div>
        </div>
        <h2 class="text-2xl font-bold mb-1">Nuevo Pedido</h2>
        <p class="text-amber-100">Crear una nueva orden</p>
      </RouterLink>

      <RouterLink to="/cocina" class="group bg-gradient-to-br from-slate-800 to-slate-900 rounded-2xl p-6 text-white shadow-md hover:shadow-xl transition-all duration-300 transform hover:-translate-y-1">
        <div class="flex items-center justify-between mb-4">
          <div class="p-3 bg-white/10 rounded-xl backdrop-blur-sm">
            <svg class="w-8 h-8" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 11H5m14 0a2 2 0 012 2v6a2 2 0 01-2 2H5a2 2 0 01-2-2v-6a2 2 0 012-2m14 0V9a2 2 0 00-2-2M5 11V9a2 2 0 002-2m0 0V5a2 2 0 012-2h6a2 2 0 012 2v2M7 7h10"></path></svg>
          </div>
        </div>
        <h2 class="text-2xl font-bold mb-1">Cocina</h2>
        <p class="text-slate-300">Gestionar pedidos en curso</p>
      </RouterLink>

      <RouterLink to="/pedidos" class="group bg-gradient-to-br from-blue-500 to-blue-600 rounded-2xl p-6 text-white shadow-md hover:shadow-xl transition-all duration-300 transform hover:-translate-y-1">
        <div class="flex items-center justify-between mb-4">
          <div class="p-3 bg-white/20 rounded-xl backdrop-blur-sm">
            <svg class="w-8 h-8" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2m-3 7h3m-3 4h3m-6-4h.01M9 16h.01"></path></svg>
          </div>
        </div>
        <h2 class="text-2xl font-bold mb-1">Historial</h2>
        <p class="text-blue-100">Ver todos los pedidos</p>
      </RouterLink>
    </div>
  </div>
</template>
