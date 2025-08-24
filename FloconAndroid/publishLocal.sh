./gradlew \
  :flocon-base:assembleRelease \
  :flocon:assembleRelease \
  :flocon-no-op:assembleRelease \
  :grpc:grpc-interceptor-base:assembleRelease \
  :grpc:grpc-interceptor:assembleRelease \
  :grpc:grpc-interceptor-lite:assembleRelease \
  :okhttp-interceptor:assembleRelease \
  :ktor-interceptor:assembleRelease

./gradlew \
  :flocon-base:publishToMavenLocal \
  :flocon:publishToMavenLocal \
  :flocon-no-op:publishToMavenLocal \
  :grpc:grpc-interceptor-base:publishToMavenLocal \
  :grpc:grpc-interceptor:publishToMavenLocal \
  :grpc:grpc-interceptor-lite:publishToMavenLocal \
  :okhttp-interceptor:publishToMavenLocal \
  :ktor-interceptor:publishToMavenLocal \
  -Psigning.required=false