package mars.dev.impl;

import jakarta.jws.WebService;
import mars.dev.entity.Categoria;
import mars.dev.repository.ICategoriaRepository;
import mars.dev.service.ICategoriaService;
import org.springframework.stereotype.Component;

import java.util.List;

@WebService(endpointInterface = "mars.dev.service.ICategoriaService")
@Component
public class CategoriaImpl implements ICategoriaService {

    private final ICategoriaRepository categoriaRepo;

    public CategoriaImpl(ICategoriaRepository categoriaRepo) {
        this.categoriaRepo = categoriaRepo;
    }

    @Override
    public List<Categoria> listarCategorias() {
        return categoriaRepo.findAll();
    }

    @Override
    public Categoria crearCategoria(Categoria categoria) {
        return categoriaRepo.save(categoria);
    }

    @Override
    public Categoria buscarCategoria(int id) {
        return categoriaRepo.findById(id).orElse(null);
    }

    @Override
    public String actualizarCategoria(int id, Categoria categoria) {
        return categoriaRepo.findById(id).map(c -> {
            c.setNombre(categoria.getNombre());
            c.setDescripcion(categoria.getDescripcion());
            categoriaRepo.save(c);
            return "Categoría actualizada correctamente";
        }).orElse("Categoría no encontrada");
    }

    @Override
    public String eliminarCategoria(int id) {
        if (categoriaRepo.existsById(id)) {
            categoriaRepo.deleteById(id);
            return "Categoría eliminada correctamente";
        }
        return "Categoría no encontrada";
    }
}
