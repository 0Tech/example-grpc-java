import com.google.protobuf.gradle.protobuf
import com.google.protobuf.gradle.protoc
import com.google.protobuf.gradle.plugins
import com.google.protobuf.gradle.id
import com.google.protobuf.gradle.generateProtoTasks

plugins {
    id("com.example.grpc.java-library-conventions")
    id("com.google.protobuf")
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:${libs.versions.proto.get()}"
    }

    plugins {
        id("grpc") {
            artifact = "io.grpc:protoc-gen-grpc-java:${libs.versions.grpc.get()}"
        }
    }

    generateProtoTasks {
        all().forEach {
            it.plugins {
                id("grpc") {}
            }
        }
    }
}

dependencies {
    compileOnly(libs.javax.annotation.api)
    api(libs.protobuf.java)
    api(libs.grpc.stub)
    api(libs.grpc.protobuf)
}
