./gradlew \
  :core:assembleRelease \
  :grpc-interceptor:assembleRelease \
  :okhttp-interceptor:assembleRelease

./gradlew \
  :core:publishToMavenLocal \
  :grpc-interceptor:publishToMavenLocal \
  :okhttp-interceptor:publishToMavenLocal \
  -Psigning.required=false