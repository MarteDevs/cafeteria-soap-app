package mars.dev.repository;

import mars.dev.entity.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface IProductoRepository extends JpaRepository<Producto, Integer> {
    List<Producto> findByCategoriaId(int categoriaId);
}
