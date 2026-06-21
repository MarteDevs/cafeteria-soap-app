import { createRouter, createWebHistory } from 'vue-router'
import HomeView        from '../views/HomeView.vue'
import CategoriasView  from '../views/CategoriasView.vue'
import ProductosView   from '../views/ProductosView.vue'
import PedidosView     from '../views/PedidosView.vue'
import NuevoPedidoView from '../views/NuevoPedidoView.vue'
import CocinaView      from '../views/CocinaView.vue'
import UsuariosView    from '../views/UsuariosView.vue'

const routes = [
  { path: '/',            component: HomeView },
  { path: '/categorias',  component: CategoriasView },
  { path: '/productos',   component: ProductosView },
  { path: '/pedidos',     component: PedidosView },
  { path: '/nuevo-pedido',component: NuevoPedidoView },
  { path: '/cocina',      component: CocinaView },
  { path: '/usuarios',    component: UsuariosView },
]

export default createRouter({
  history: createWebHistory(),
  routes
})
