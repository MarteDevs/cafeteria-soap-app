package mars.dev.repository;

import mars.dev.entity.DetallePedido;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface IDetalleRepository extends JpaRepository<DetallePedido, Integer> {
    List<DetallePedido> findByPedidoId(int pedidoId);
}
