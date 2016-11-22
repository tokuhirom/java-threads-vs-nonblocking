package com.example;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.embedded.jetty.JettyEmbeddedServletContainerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@SpringBootApplication
public class DemoApplication {
    private static final int PORT = 8082;
    private static final int MIN_THREADS = 10000;
    private static final int MAX_THREADS = 10000;
    private static final int IDLE_TIMEOUT = 60 * 1000;

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

    @RestController
    public static class RootController {
        @GetMapping("/")
        public String proxy() {
            return "OK";
        }
    }

    @Bean
    public JettyEmbeddedServletContainerFactory jettyEmbeddedServletContainerFactory() {
        final JettyEmbeddedServletContainerFactory factory = new JettyEmbeddedServletContainerFactory(PORT);
        factory.addServerCustomizers((Server server) -> {
            final QueuedThreadPool threadPool = server.getBean(QueuedThreadPool.class);
            threadPool.setMinThreads(MIN_THREADS);
            threadPool.setMaxThreads(MAX_THREADS);
            threadPool.setIdleTimeout(IDLE_TIMEOUT);
            threadPool.setName("benchmark");
        });
        return factory;
    }

}
