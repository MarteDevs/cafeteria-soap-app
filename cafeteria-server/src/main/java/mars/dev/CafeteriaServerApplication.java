package mars.dev;

import jakarta.xml.ws.Endpoint;
import mars.dev.impl.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class CafeteriaServerApplication {

	public static void main(String[] args) {
		ConfigurableApplicationContext ctx = SpringApplication.run(CafeteriaServerApplication.class, args);

		Endpoint.publish("http://localhost:8085/ws/categoria", ctx.getBean(CategoriaImpl.class));
		Endpoint.publish("http://localhost:8085/ws/producto", ctx.getBean(ProductoImpl.class));
		Endpoint.publish("http://localhost:8085/ws/pedido", ctx.getBean(PedidoImpl.class));
		Endpoint.publish("http://localhost:8085/ws/detalle", ctx.getBean(DetalleImpl.class));

		System.out.println("✅ Servicios SOAP publicados:");
		System.out.println("   http://localhost:8085/ws/categoria?wsdl");
		System.out.println("   http://localhost:8085/ws/producto?wsdl");
		System.out.println("   http://localhost:8085/ws/pedido?wsdl");
		System.out.println("   http://localhost:8085/ws/detalle?wsdl");
	}

}
