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

import io.grpc.Channel;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EchoClient {
    private static final Logger logger = Logger.getLogger(EchoClient.class.getName());

    private final EchoerGrpc.EchoerBlockingStub blockingStub;

    /** Construct client for accessing Echo server using the existing channel. */
    public EchoClient(Channel channel) {
        // 'channel' here is a Channel, not a ManagedChannel, so it is not this code's responsibility to
        // shut it down.

        // Passing Channels to code makes code easier to test and makes it easier to reuse Channels.
        blockingStub = EchoerGrpc.newBlockingStub(channel);
    }

    private void log(String message) {
        System.out.println(message);
    }

    /** Send ping to server. */
    public void ping(String message) {
        log("ping: " + message);
        final EchoRequest request = EchoRequest.newBuilder().setMessage(message).build();
        EchoReply response;
        try {
            response = blockingStub.echoOnce(request);
        } catch (StatusRuntimeException e) {
            logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus());
            return;
        }
        log("pong: " + response.getMessage());
    }

    /**
     * Ping server. If provided, the first element of {@code args} is the message to use in
     * the ping. The second argument is the target server.
     */
    public static void main(String[] args) throws Exception {
        final String message = (args.length > 0) ? args[0] : "hello";
        // Access a service running on the local machine on port 50051
        final String target = (args.length > 1) ? args[1] : "localhost:50051";

        // Create a communication channel to the server, known as a Channel. Channels are thread-safe
        // and reusable. It is common to create channels at the beginning of your application and reuse
        // them until the application shuts down.
        ManagedChannel channel = ManagedChannelBuilder.forTarget(target)
            // Channels are secure by default (via SSL/TLS). For the example we disable TLS to avoid
            // needing certificates.
            .usePlaintext()
            .build();
        try {
            EchoClient client = new EchoClient(channel);
            client.ping(message);
        } finally {
            // ManagedChannels use resources like threads and TCP connections. To prevent leaking these
            // resources the channel should be shut down when it will no longer be used. If it may be used
            // again leave it running.
            channel.shutdownNow().awaitTermination(5, TimeUnit.SECONDS);
        }
    }
}
