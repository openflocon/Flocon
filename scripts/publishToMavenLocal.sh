./gradlew \
  :protocol:assembleRelease \
  :client:core:assembleRelease \
  :client:grpc-interceptor:assembleRelease \
  :client:okhttp-interceptor:assembleRelease

./gradlew \
  :protocol:publishToMavenLocal \
  :client:core:publishToMavenLocal \
  :client:grpc-interceptor:publishToMavenLocal \
  :client:okhttp-interceptor:publishToMavenLocal \
  -Psigning.required=false