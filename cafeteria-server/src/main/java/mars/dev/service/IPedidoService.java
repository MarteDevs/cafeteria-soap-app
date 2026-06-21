package mars.dev.service;

import jakarta.jws.WebService;
import mars.dev.entity.Pedido;

import java.util.List;

@WebService
public interface IPedidoService {
    List<Pedido> listarPedidos();
    Pedido crearPedido(String mesa, String mesero);
    Pedido buscarPedido(int id);
    String cambiarEstado(int id, String nuevoEstado);
    String cancelarPedido(int id);
    List<Pedido> listarPorEstado(String estado);
}
