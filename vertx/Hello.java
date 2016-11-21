import io.vertx.rxjava.core.AbstractVerticle;
import io.vertx.rxjava.core.Vertx;
import io.vertx.rxjava.core.http.HttpServer;
import io.vertx.rxjava.ext.web.Router;
import io.vertx.rxjava.ext.web.RoutingContext;

public class Hello extends AbstractVerticle {
	public void start() {
		HttpServer httpServer = vertx.createHttpServer();
		Router router = Router.router(vertx);
		router.route("/").handler(c -> {
			c.response().end("OK");
		});
		httpServer.requestHandler(router::accept)
				.listen(8080);
	}
}

