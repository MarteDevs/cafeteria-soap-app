<script setup>
defineProps(['estado'])
const colores = {
  PENDIENTE:       'bg-yellow-100 text-yellow-800 border-yellow-200',
  EN_PREPARACION:  'bg-blue-100 text-blue-800 border-blue-200',
  ENTREGADO:       'bg-green-100 text-green-800 border-green-200',
  CANCELADO:       'bg-red-100 text-red-800 border-red-200',
}
</script>

<template>
  <span :class="['px-2.5 py-1 rounded-full text-xs font-bold border shadow-sm', colores[estado] ?? 'bg-gray-100 text-gray-800 border-gray-200']">
    {{ estado }}
  </span>
</template>
