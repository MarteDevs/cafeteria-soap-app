<template>
  <nav class="bg-amber-700 text-white px-6 py-4 flex gap-6 items-center shadow-lg sticky top-0 z-50">
    <span class="font-bold text-xl tracking-wide flex items-center gap-2">
      ☕ <span class="hidden sm:inline">Cafetería Mars</span>
    </span>
    <div class="flex-1 flex gap-4 ml-4">
      <RouterLink to="/" class="hover:bg-amber-600 px-3 py-2 rounded transition-colors duration-200">Inicio</RouterLink>
      <RouterLink to="/categorias" class="hover:bg-amber-600 px-3 py-2 rounded transition-colors duration-200">Categorías</RouterLink>
      <RouterLink to="/productos" class="hover:bg-amber-600 px-3 py-2 rounded transition-colors duration-200">Productos</RouterLink>
      <RouterLink to="/pedidos" class="hover:bg-amber-600 px-3 py-2 rounded transition-colors duration-200">Pedidos</RouterLink>
    </div>
    <div class="flex gap-4">
      <RouterLink to="/nuevo-pedido" class="bg-amber-500 hover:bg-amber-400 text-amber-950 font-semibold px-4 py-2 rounded-full transition-all duration-200 shadow hover:shadow-md transform hover:-translate-y-0.5">
        + Nuevo Pedido
      </RouterLink>
      <RouterLink to="/cocina" class="bg-amber-900 hover:bg-amber-800 text-amber-100 font-semibold px-4 py-2 rounded-full transition-all duration-200 shadow flex items-center gap-2">
        🍳 Cocina
      </RouterLink>
    </div>
  </nav>
</template>
