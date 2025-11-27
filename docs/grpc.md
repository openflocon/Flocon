# Grpc

Similar to network inteceptions, Flocon works with grpc 

it works with `io.grpc:grpc-android` : https://github.com/grpc/grpc-java

> [!WARNING]
> please ensure your version is at lease `1.70.0`

[![Maven Central](https://img.shields.io/maven-central/v/io.github.openflocon/flocon-grpc-interceptor.svg)](https://search.maven.org/artifact/io.github.openflocon/flocon-grpc-interceptor)

> [!IMPORTANT]
> While dealing with protobuf on Android projects, it's best to use its lighter artifact (protobuf-javalite or protobuf-kotlin-lite). 
> It might be that your project needs the larger protobuf version (protobuf-java or protobuf-kotlin).
> Flocon offers two interceptor artifacts that leverage a different JSON formatter. It declutters the JSON printing by removing unwanted fields with a dedicated formatter depending on the protobuf library.
> Make sure you choose the correct artifact.

```
 // If you're using protobuf-javalite or protobuf-kotlin-lite
implementation("com.google.protobuf:protobuf-kotlin-lite:$PROTOBUF_VERSION")

implementation("io.github.openflocon:grpc-interceptor-lite:LAST_VERSION")
```
or
```
// If you're using protobuf-java or protobuf-kotlin
implementation("com.google.protobuf:protobuf-java:$PROTOBUF_VERSION")

implementation("io.github.openflocon:grpc-interceptor:LAST_VERSION") 
```


```kotlin
ManagedChannelBuilder
            ...
            .intercept(
                FloconGrpcInterceptor()
            )
            .build()
```