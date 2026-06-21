package mars.dev.impl;

import jakarta.jws.WebService;
import mars.dev.entity.Usuario;
import mars.dev.repository.IUsuarioRepository;
import mars.dev.service.IUsuarioService;
import org.springframework.stereotype.Component;
import java.util.List;

@WebService(endpointInterface = "mars.dev.service.IUsuarioService")
@Component
public class UsuarioImpl implements IUsuarioService {

    private final IUsuarioRepository usuarioRepo;

    public UsuarioImpl(IUsuarioRepository usuarioRepo) {
        this.usuarioRepo = usuarioRepo;
    }

    @Override
    public Usuario login(String username, String password) {
        return usuarioRepo.findByUsername(username)
                .filter(u -> u.getPassword().equals(password))
                .orElse(null);
    }

    @Override
    public Usuario crearUsuario(String username, String password, String nombre, String rol) {
        if (usuarioRepo.findByUsername(username).isPresent()) {
            return null; // Username already exists
        }
        Usuario u = new Usuario(username, password, nombre, rol);
        return usuarioRepo.save(u);
    }

    @Override
    public List<Usuario> listarUsuarios() {
        return usuarioRepo.findAll();
    }

    @Override
    public String eliminarUsuario(int id) {
        if (usuarioRepo.existsById(id)) {
            usuarioRepo.deleteById(id);
            return "Usuario eliminado con éxito";
        }
        return "Usuario no encontrado";
    }
}
