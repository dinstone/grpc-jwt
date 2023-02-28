package com.dinstone.grpc.client;

import java.util.concurrent.Executor;

import com.dinstone.grpc.api.AuthConstant;
import com.dinstone.grpc.authen.api.AuthenServiceGrpc;
import com.dinstone.grpc.authen.api.AuthenServiceGrpc.AuthenServiceBlockingStub;
import com.dinstone.grpc.authen.api.LoginRequest;
import com.dinstone.grpc.authen.api.LoginResponse;
import com.dinstone.grpc.hello.api.HelloServiceGrpc;
import com.dinstone.grpc.hello.api.HelloServiceGrpc.HelloServiceBlockingStub;
import com.google.protobuf.StringValue;

import io.grpc.CallCredentials;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Metadata;
import io.grpc.Status;

public class GrpcServiceClient {

    public static void main(String[] args) {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 9999).usePlaintext().build();
        AuthenServiceBlockingStub stub = AuthenServiceGrpc.newBlockingStub(channel);
        LoginResponse r = stub.login(LoginRequest.newBuilder().setUsername("dinstone").setPassword("123456").build());
        String t = r.getToken();
        System.out.println(t);

        hello(t);

    }

    private static void hello(String token) {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 9898).usePlaintext().build();
        HelloServiceBlockingStub stub = HelloServiceGrpc.newBlockingStub(channel)
                .withCallCredentials(new CallCredentials() {

                    @Override
                    public void applyRequestMetadata(RequestInfo requestInfo, Executor executor,
                            MetadataApplier metadataApplier) {
                        executor.execute(() -> {
                            try {
                                Metadata headers = new Metadata();
                                headers.put(Metadata.Key.of(AuthConstant.AUTH_HEADER, Metadata.ASCII_STRING_MARSHALLER),
                                        String.format("%s %s", AuthConstant.AUTH_TOKEN_TYPE, token));
                                metadataApplier.apply(headers);
                            } catch (Throwable e) {
                                metadataApplier.fail(Status.UNAUTHENTICATED.withCause(e));
                            }
                        });
                    }

                    @Override
                    public void thisUsesUnstableApi() {
                        // TODO Auto-generated method stub

                    }
                });
        StringValue v = stub.sayHello(StringValue.newBuilder().setValue("wawo").build());
        System.out.println(" return is : " + v.getValue());
    }

}
