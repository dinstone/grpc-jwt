package com.dinstone.grpc.server;

import java.io.IOException;

import com.dinstone.grpc.api.AuthConstant;
import com.dinstone.grpc.hello.api.HelloServiceGrpc;
import com.google.protobuf.StringValue;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.ServerInterceptors;
import io.grpc.stub.StreamObserver;

public class GrpcServiceServer {

    public static final class HelloServiceImpl extends HelloServiceGrpc.HelloServiceImplBase {
        @Override
        public void sayHello(StringValue request, StreamObserver<StringValue> responseObserver) {
            String clientId = AuthConstant.AUTH_CLIENT_ID.get();
            responseObserver
                    .onNext(StringValue.newBuilder().setValue(clientId + " say hello:" + request.getValue()).build());
            responseObserver.onCompleted();
        }
    }

    public static void main(String[] args) {
        Server server = ServerBuilder.forPort(9898)
                .addService(ServerInterceptors.intercept(new HelloServiceImpl(), new AuthInterceptor())).build();

        try {
            System.out.println("server on 9898");
            server.start().awaitTermination();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (server != null) {
                server.shutdownNow();
            }
        }
    }

}
