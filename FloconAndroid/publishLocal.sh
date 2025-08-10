./gradlew \
  :flocon-base:assembleRelease \
  :flocon:assembleRelease \
  :flocon-no-op:assembleRelease \
  :grpc-interceptor:assembleRelease \
  :okhttp-interceptor:assembleRelease

./gradlew \
  :flocon-base:publishToMavenLocal \
  :flocon:publishToMavenLocal \
  :flocon-no-op:publishToMavenLocal \
  :grpc-interceptor:publishToMavenLocal \
  :okhttp-interceptor:publishToMavenLocal \
  -Psigning.required=false