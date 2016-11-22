import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;

public class Hello extends AbstractVerticle {
	public void start() {
		vertx.createHttpServer()
				.requestHandler(request -> {
					request.response()
							.end("OK");
				})
				.listen(8080);
	}
}

