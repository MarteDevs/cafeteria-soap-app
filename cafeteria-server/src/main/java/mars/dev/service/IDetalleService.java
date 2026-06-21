package mars.dev.service;

import jakarta.jws.WebService;
import mars.dev.entity.DetallePedido;

import java.util.List;

@WebService
public interface IDetalleService {
    List<DetallePedido> listarDetallesPorPedido(int pedidoId);
    DetallePedido agregarDetalle(int pedidoId, int productoId, int cantidad);
    String eliminarDetalle(int detalleId);
    String actualizarCantidad(int detalleId, int nuevaCantidad);
}
