package mars.dev.impl;

import jakarta.jws.WebService;
import mars.dev.entity.Categoria;
import mars.dev.entity.Producto;
import mars.dev.repository.ICategoriaRepository;
import mars.dev.repository.IProductoRepository;
import mars.dev.service.IProductoService;
import org.springframework.stereotype.Component;

import java.util.List;

@WebService(endpointInterface = "mars.dev.service.IProductoService")
@Component
public class ProductoImpl implements IProductoService {

    private final IProductoRepository productoRepo;
    private final ICategoriaRepository categoriaRepo;

    public ProductoImpl(IProductoRepository productoRepo, ICategoriaRepository categoriaRepo) {
        this.productoRepo = productoRepo;
        this.categoriaRepo = categoriaRepo;
    }

    @Override
    public List<Producto> listarProductos() {
        return productoRepo.findAll();
    }

    @Override
    public Producto crearProducto(Producto producto) {
        if (producto.getCategoria() != null && producto.getCategoria().getId() > 0) {
            Categoria cat = categoriaRepo.findById(producto.getCategoria().getId()).orElse(null);
            producto.setCategoria(cat);
        }
        return productoRepo.save(producto);
    }

    @Override
    public Producto buscarProducto(int id) {
        return productoRepo.findById(id).orElse(null);
    }

    @Override
    public String actualizarProducto(int id, Producto producto) {
        return productoRepo.findById(id).map(p -> {
            p.setNombre(producto.getNombre());
            p.setPrecio(producto.getPrecio());
            p.setStock(producto.getStock());
            if (producto.getCategoria() != null && producto.getCategoria().getId() > 0) {
                Categoria cat = categoriaRepo.findById(producto.getCategoria().getId()).orElse(null);
                p.setCategoria(cat);
            }
            productoRepo.save(p);
            return "Producto actualizado correctamente";
        }).orElse("Producto no encontrado");
    }

    @Override
    public String eliminarProducto(int id) {
        if (productoRepo.existsById(id)) {
            productoRepo.deleteById(id);
            return "Producto eliminado correctamente";
        }
        return "Producto no encontrado";
    }

    @Override
    public List<Producto> listarPorCategoria(int categoriaId) {
        return productoRepo.findByCategoriaId(categoriaId);
    }
}
