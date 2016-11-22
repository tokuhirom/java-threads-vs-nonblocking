package com.example;

import org.apache.catalina.Context;
import org.apache.catalina.Executor;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.core.StandardThreadExecutor;
import org.apache.catalina.startup.Tomcat;
import org.apache.coyote.AbstractProtocol;
import org.apache.coyote.ProtocolHandler;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;

public class TomcatServer {
    public static void main(String[] args) throws LifecycleException, InterruptedException, ServletException {
        int port = 8086;
        int minSpareThreads = 10000;

        Tomcat tomcat = new Tomcat();
        tomcat.setPort(port);
        for (Executor executor : tomcat.getService().findExecutors()) {
            tomcat.getConnector().getService().removeExecutor(executor);
        }
        ProtocolHandler handler = tomcat.getConnector().getProtocolHandler();
        if (handler instanceof AbstractProtocol) {
            AbstractProtocol protocol = (AbstractProtocol) handler;
            protocol.setMinSpareThreads(minSpareThreads);
            protocol.setMaxThreads(10000);
        }

        Context ctx = tomcat.addContext("/", new File(".").getAbsolutePath());
        Tomcat.addServlet(ctx, "benchmark", new MyServlet());
        ctx.addServletMapping("/*", "benchmark");
        tomcat.start();
        tomcat.getServer().await();
    }

    private static class MyServlet extends HttpServlet {
        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
            try (ServletOutputStream outputStream = resp.getOutputStream()) {
                outputStream.print("OK");
            }
        }
    }
}
