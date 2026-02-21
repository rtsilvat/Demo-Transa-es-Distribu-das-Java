<template>
  <div class="min-h-screen bg-slate-950 text-slate-100">
    <header class="border-b border-slate-700 bg-slate-900/80">
      <div class="max-w-6xl mx-auto px-6 py-4">
        <h1 class="text-2xl font-bold text-emerald-400">Banking Demo</h1>
        <p class="text-slate-400 text-sm mt-1">Transações Distribuídas - Kafka + Postgres</p>
      </div>
    </header>
    <main class="max-w-6xl mx-auto px-6 py-8">
      <RouterView />
    </main>
  </div>
</template>
