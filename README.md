# example-grpc-java

Minimal example of gRPC in java

## Build

You can build the client and server with:

```shell
$ ./gradlew build
```

## Usage

You can run echo server with:

```shell
$ ./gradlew :server:run
```

After that, you may run echo client to ping to the server:

```shell
$ ./gradlew :client:run
```
