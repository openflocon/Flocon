adb reverse tcp:50051 tcp:50051
cd grpc-go/examples/helloworld/
go run greeter_server/main.go