package mars.dev.service;

import jakarta.jws.WebService;
import mars.dev.entity.Producto;

import java.util.List;

@WebService
public interface IProductoService {
    List<Producto> listarProductos();
    Producto crearProducto(Producto producto);
    Producto buscarProducto(int id);
    String actualizarProducto(int id, Producto producto);
    String eliminarProducto(int id);
    List<Producto> listarPorCategoria(int categoriaId);
}
