./gradlew \
  :core:assembleRelease \
  :grpc-interceptor:assembleRelease \
  :okhttp-interceptor:assembleRelease \
  :graphql-interceptor:assembleRelease

./gradlew \
  :core:publishToMavenLocal \
  :grpc-interceptor:publishToMavenLocal \
  :okhttp-interceptor:publishToMavenLocal \
  :graphql-interceptor:publishToMavenLocal \
  -Psigning.required=false