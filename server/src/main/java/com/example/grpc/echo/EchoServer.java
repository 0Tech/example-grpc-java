/*
 * Copyright 2015 The gRPC Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.grpc.echo;

import com.example.grpc.echo.EchoRequest;
import com.example.grpc.echo.EchoReply;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * Server that manages startup/shutdown of a {@code Echoer} server.
 */
public class EchoServer {
    private static final Logger logger = Logger.getLogger(EchoServer.class.getName());

    private Server server;

    private void start() throws IOException {
        /* The port on which the server should run */
        final int port = 50051;
        server = ServerBuilder.forPort(port)
            .addService(new EchoerImpl())
            .build()
            .start();
        logger.info("Server started, listening on " + port);
        Runtime.getRuntime().addShutdownHook(new Thread() {
                @Override
                public void run() {
                    // Use stderr here since the logger may have been reset by its JVM shutdown hook.
                    System.err.println("*** shutting down gRPC server since JVM is shutting down");
                    try {
                        EchoServer.this.stop();
                    } catch (InterruptedException e) {
                        e.printStackTrace(System.err);
                    }
                    System.err.println("*** server shut down");
                }
            });
    }

    private void stop() throws InterruptedException {
        if (server != null) {
            server.shutdown().awaitTermination(30, TimeUnit.SECONDS);
        }
    }

    /**
     * Await termination on the main thread since the grpc library uses daemon threads.
     */
    private void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }

    /**
     * Main launches the server from the command line.
     */
    public static void main(String[] args) throws IOException, InterruptedException {
        final EchoServer server = new EchoServer();
        server.start();
        server.blockUntilShutdown();
    }

    static class EchoerImpl extends EchoerGrpc.EchoerImplBase {
        @Override
        public void echoOnce(EchoRequest req, StreamObserver<EchoReply> responseObserver) {
            final String message = req.getMessage();
            logger.info("ping: " + message);
            final EchoReply reply = EchoReply.newBuilder().setMessage("[Echo] " + message).build();
            responseObserver.onNext(reply);
            responseObserver.onCompleted();
            logger.info("pong: " + reply.getMessage());
        }
    }
}
