package mars.dev.service;

import jakarta.jws.WebService;
import mars.dev.entity.Usuario;
import java.util.List;

@WebService
public interface IUsuarioService {
    Usuario login(String username, String password);
    Usuario crearUsuario(String username, String password, String nombre, String rol);
    List<Usuario> listarUsuarios();
    String eliminarUsuario(int id);
}
