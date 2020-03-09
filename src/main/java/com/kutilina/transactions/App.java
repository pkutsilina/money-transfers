package com.kutilina.transactions;

import com.kutilina.transactions.db.Migration;
import com.kutilina.transactions.resources.AccountResourceConfig;
import com.kutilina.transactions.resources.TransferExceptionMapper;
import io.netty.channel.Channel;
import org.glassfish.jersey.netty.httpserver.NettyHttpContainerProvider;
import org.glassfish.jersey.server.ResourceConfig;

import java.net.URI;
import java.util.logging.Level;
import java.util.logging.Logger;

public class App {

    private static final URI BASE_URI = URI.create("http://localhost:8080/");

    public static void main(String[] args) {
        try {
            Migration.migrate();

            ResourceConfig resourceConfig = new ResourceConfig(AccountResourceConfig.class,
                    TransferExceptionMapper.class);

            final Channel server = NettyHttpContainerProvider.createHttp2Server(BASE_URI, resourceConfig, null);

            Runtime.getRuntime().addShutdownHook(new Thread(server::close));

            System.out.println("Application started. Stop application using: CTRL+C.");

            Thread.currentThread().join();
        } catch (InterruptedException ex) {
            Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
