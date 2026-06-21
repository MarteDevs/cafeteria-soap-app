package mars.dev;

import jakarta.xml.ws.Endpoint;
import mars.dev.impl.*;
import mars.dev.repository.IUsuarioRepository;
import mars.dev.entity.Usuario;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class CafeteriaServerApplication {

	public static void main(String[] args) {
		ConfigurableApplicationContext ctx = SpringApplication.run(CafeteriaServerApplication.class, args);

		Endpoint.publish("http://0.0.0.0:8085/ws/categoria", ctx.getBean(CategoriaImpl.class));
		Endpoint.publish("http://0.0.0.0:8085/ws/producto", ctx.getBean(ProductoImpl.class));
		Endpoint.publish("http://0.0.0.0:8085/ws/pedido", ctx.getBean(PedidoImpl.class));
		Endpoint.publish("http://0.0.0.0:8085/ws/detalle", ctx.getBean(DetalleImpl.class));
		Endpoint.publish("http://0.0.0.0:8085/ws/usuario", ctx.getBean(UsuarioImpl.class));

		// Semilla de usuarios por defecto
		IUsuarioRepository userRepo = ctx.getBean(IUsuarioRepository.class);
		if (userRepo.count() == 0) {
			userRepo.save(new Usuario("mesero1", "123", "Juan Mesero", "MESERO"));
			userRepo.save(new Usuario("cocinero1", "123", "Chef Carlos", "COCINERO"));
			userRepo.save(new Usuario("admin", "123", "Admin Ambrosia", "ADMINISTRADOR"));
			System.out.println("🌱 Usuarios semilla creados con éxito.");
		}

		System.out.println("✅ Servicios SOAP publicados:");
		System.out.println("   http://localhost:8085/ws/categoria?wsdl");
		System.out.println("   http://localhost:8085/ws/producto?wsdl");
		System.out.println("   http://localhost:8085/ws/pedido?wsdl");
		System.out.println("   http://localhost:8085/ws/detalle?wsdl");
		System.out.println("   http://localhost:8085/ws/usuario?wsdl");
	}

}

