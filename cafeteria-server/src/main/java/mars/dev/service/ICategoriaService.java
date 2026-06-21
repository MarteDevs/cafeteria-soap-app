package mars.dev.service;

import jakarta.jws.WebService;
import mars.dev.entity.Categoria;

import java.util.List;

@WebService
public interface ICategoriaService {
    List<Categoria> listarCategorias();
    Categoria crearCategoria(Categoria categoria);
    Categoria buscarCategoria(int id);
    String actualizarCategoria(int id, Categoria categoria);
    String eliminarCategoria(int id);
}
