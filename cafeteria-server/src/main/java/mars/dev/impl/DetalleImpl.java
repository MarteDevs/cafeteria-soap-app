package mars.dev.impl;

import jakarta.jws.WebService;
import mars.dev.entity.DetallePedido;
import mars.dev.entity.Pedido;
import mars.dev.entity.Producto;
import mars.dev.repository.IDetalleRepository;
import mars.dev.repository.IPedidoRepository;
import mars.dev.repository.IProductoRepository;
import mars.dev.service.IDetalleService;
import org.springframework.stereotype.Component;

import java.util.List;

@WebService(endpointInterface = "mars.dev.service.IDetalleService")
@Component
public class DetalleImpl implements IDetalleService {

    private final IDetalleRepository detalleRepo;
    private final IPedidoRepository pedidoRepo;
    private final IProductoRepository productoRepo;

    public DetalleImpl(IDetalleRepository detalleRepo, IPedidoRepository pedidoRepo, IProductoRepository productoRepo) {
        this.detalleRepo = detalleRepo;
        this.pedidoRepo = pedidoRepo;
        this.productoRepo = productoRepo;
    }

    @Override
    public List<DetallePedido> listarDetallesPorPedido(int pedidoId) {
        return detalleRepo.findByPedidoId(pedidoId);
    }

    @Override
    public DetallePedido agregarDetalle(int pedidoId, int productoId, int cantidad) {
        Pedido pedido = pedidoRepo.findById(pedidoId).orElse(null);
        Producto producto = productoRepo.findById(productoId).orElse(null);

        if (pedido == null || producto == null) return null;

        DetallePedido detalle = new DetallePedido();
        detalle.setPedido(pedido);
        detalle.setProducto(producto);
        detalle.setCantidad(cantidad);
        detalle.setPrecioUnitario(producto.getPrecio());
        
        double subtotal = producto.getPrecio() * cantidad;
        detalle.setSubtotal(subtotal);

        // Actualizar stock de producto
        producto.setStock(producto.getStock() - cantidad);
        productoRepo.save(producto);

        DetallePedido guardado = detalleRepo.save(detalle);

        // Recalcular total del pedido
        actualizarTotalPedido(pedido);

        return guardado;
    }

    @Override
    public String eliminarDetalle(int detalleId) {
        return detalleRepo.findById(detalleId).map(detalle -> {
            Pedido pedido = detalle.getPedido();
            Producto producto = detalle.getProducto();
            
            // Devolver stock
            producto.setStock(producto.getStock() + detalle.getCantidad());
            productoRepo.save(producto);

            detalleRepo.delete(detalle);
            
            // Recalcular total del pedido
            actualizarTotalPedido(pedido);
            
            return "Detalle eliminado correctamente";
        }).orElse("Detalle no encontrado");
    }

    @Override
    public String actualizarCantidad(int detalleId, int nuevaCantidad) {
        return detalleRepo.findById(detalleId).map(detalle -> {
            Pedido pedido = detalle.getPedido();
            Producto producto = detalle.getProducto();
            
            int diferencia = nuevaCantidad - detalle.getCantidad();
            
            // Ajustar stock
            producto.setStock(producto.getStock() - diferencia);
            productoRepo.save(producto);

            detalle.setCantidad(nuevaCantidad);
            double subtotal = detalle.getPrecioUnitario() * nuevaCantidad;
            detalle.setSubtotal(subtotal);
            detalleRepo.save(detalle);

            // Recalcular total del pedido
            actualizarTotalPedido(pedido);

            return "Cantidad actualizada a " + nuevaCantidad;
        }).orElse("Detalle no encontrado");
    }

    private void actualizarTotalPedido(Pedido pedido) {
        List<DetallePedido> detalles = detalleRepo.findByPedidoId(pedido.getId());
        double total = detalles.stream().mapToDouble(DetallePedido::getSubtotal).sum();
        pedido.setTotal(total);
        pedidoRepo.save(pedido);
    }
}
