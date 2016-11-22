package com.example;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.util.thread.QueuedThreadPool;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JettyServer {
    private static final int PORT = 8085;
    private static final int MIN_THREADS = 10000;
    private static final int MAX_THREADS = 10000;
    private static final int IDLE_TIMEOUT = 60 * 1000;

    public static void main(String[] args) throws Exception {
        Server server = new Server(PORT);

        final QueuedThreadPool threadPool = server.getBean(QueuedThreadPool.class);
        threadPool.setMinThreads(MIN_THREADS);
        threadPool.setMaxThreads(MAX_THREADS);
        threadPool.setIdleTimeout(IDLE_TIMEOUT);
        threadPool.setName("benchmark");

        server.setHandler(new MyHandler());
        server.start();
        server.join();
    }

    public static class MyHandler implements Handler {
        public void handle(String s, Request request, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws IOException, ServletException {
            httpServletResponse.getOutputStream().print("OK");
            request.setHandled(true);
        }

        public void setServer(Server server) {

        }

        public Server getServer() {
            return null;
        }

        public void destroy() {

        }

        public void start() throws Exception {

        }

        public void stop() throws Exception {

        }

        public boolean isRunning() {
            return false;
        }

        public boolean isStarted() {
            return false;
        }

        public boolean isStarting() {
            return false;
        }

        public boolean isStopping() {
            return false;
        }

        public boolean isStopped() {
            return false;
        }

        public boolean isFailed() {
            return false;
        }

        public void addLifeCycleListener(Listener listener) {

        }

        public void removeLifeCycleListener(Listener listener) {

        }
    }
}
