<template>
  <div class="min-h-screen bg-slate-50 flex flex-col font-sans">
    <Navbar />
    <main class="flex-1 max-w-7xl w-full mx-auto p-6 md:p-8 animate-fade-in">
      <RouterView />
    </main>
  </div>
</template>

<script setup>
import Navbar from './components/Navbar.vue'
</script>

<style>
@keyframes fadeIn {
  from { opacity: 0; transform: translateY(10px); }
  to { opacity: 1; transform: translateY(0); }
}
.animate-fade-in {
  animation: fadeIn 0.4s ease-out forwards;
}
</style>
