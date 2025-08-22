./gradlew \
  :flocon-base:assembleRelease \
  :flocon:assembleRelease \
  :flocon-no-op:assembleRelease \
  :grpc-interceptor:assembleRelease \
  :grpc-interceptor-base:assembleRelease \
  :grpc-interceptor-lite:assembleRelease \
  :okhttp-interceptor:assembleRelease \
  :ktor-interceptor:assembleRelease

./gradlew \
  :flocon-base:publishToMavenLocal \
  :flocon:publishToMavenLocal \
  :flocon-no-op:publishToMavenLocal \
  :grpc-interceptor:publishToMavenLocal \
  :grpc-interceptor-base:publishToMavenLocal \
  :grpc-interceptor-lite:publishToMavenLocal \
  :okhttp-interceptor:publishToMavenLocal \
  :ktor-interceptor:publishToMavenLocal \
  -Psigning.required=false