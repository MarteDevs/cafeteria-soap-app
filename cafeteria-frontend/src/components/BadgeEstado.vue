<script setup>
defineProps(['estado'])
const colores = {
  PENDIENTE:       'bg-[#ff7a45]/20 text-[#ffb394] border-[#ff7a45]/30 shadow-[0_0_10px_rgba(255,122,69,0.2)]',
  EN_PREPARACION:  'bg-[#6b46c1]/20 text-[#d9cbf6] border-[#6b46c1]/30 shadow-[0_0_10px_rgba(107,70,193,0.2)]',
  ENTREGADO:       'bg-[#82e8a6]/20 text-[#82e8a6] border-[#82e8a6]/30 shadow-[0_0_10px_rgba(130,232,166,0.2)]',
  CANCELADO:       'bg-[#93000a]/20 text-[#ffb4ab] border-[#93000a]/30 shadow-[0_0_10px_rgba(147,0,10,0.2)]',
}
</script>

<template>
  <span :class="['px-3 py-1 rounded-full text-xs font-outfit font-bold border', colores[estado] ?? 'bg-white/10 text-[#a09fb9] border-white/20']">
    {{ estado }}
  </span>
</template>
