package mars.dev.impl;

import jakarta.jws.WebService;
import mars.dev.entity.Pedido;
import mars.dev.repository.IPedidoRepository;
import mars.dev.service.IPedidoService;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@WebService(endpointInterface = "mars.dev.service.IPedidoService")
@Component
public class PedidoImpl implements IPedidoService {

    private final IPedidoRepository pedidoRepo;

    public PedidoImpl(IPedidoRepository pedidoRepo) {
        this.pedidoRepo = pedidoRepo;
    }

    @Override
    public List<Pedido> listarPedidos() {
        return pedidoRepo.findAll();
    }

    @Override
    public Pedido crearPedido(String mesa, String mesero) {
        Pedido p = new Pedido();
        p.setMesa(mesa);
        p.setEstado("PENDIENTE");
        p.setTotal(0.0);
        p.setFecha(LocalDateTime.now());
        p.setMesero(mesero);
        return pedidoRepo.save(p);
    }

    @Override
    public Pedido buscarPedido(int id) {
        return pedidoRepo.findById(id).orElse(null);
    }

    @Override
    public String cambiarEstado(int id, String nuevoEstado) {
        return pedidoRepo.findById(id).map(p -> {
            p.setEstado(nuevoEstado);
            pedidoRepo.save(p);
            return "Estado actualizado a: " + nuevoEstado;
        }).orElse("Pedido no encontrado");
    }

    @Override
    public String cancelarPedido(int id) {
        return cambiarEstado(id, "CANCELADO");
    }

    @Override
    public List<Pedido> listarPorEstado(String estado) {
        return pedidoRepo.findByEstado(estado);
    }
}
