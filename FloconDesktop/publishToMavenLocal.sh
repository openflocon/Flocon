./gradlew \
  :client:core:assembleRelease \
  :client:grpc-interceptor:assembleRelease \
  :client:okhttp-interceptor:assembleRelease

./gradlew \
  :client:core:publishToMavenLocal \
  :client:grpc-interceptor:publishToMavenLocal \
  :client:okhttp-interceptor:publishToMavenLocal \
  -Psigning.required=false