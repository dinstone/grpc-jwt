package com.dinstone.grpc.authen;

import java.io.IOException;

import com.dinstone.grpc.api.AuthConstant;
import com.dinstone.grpc.authen.api.AuthenServiceGrpc;
import com.dinstone.grpc.authen.api.LoginRequest;
import com.dinstone.grpc.authen.api.LoginResponse;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import io.jsonwebtoken.Jwts;

public class AuthenServer {

    public static void main(String[] args) {
        Server server = ServerBuilder.forPort(9999).addService(new AuthenServiceGrpc.AuthenServiceImplBase() {

            @Override
            public void login(LoginRequest request, StreamObserver<LoginResponse> responseObserver) {
                String username = request.getUsername();
                String password = request.getPassword();
                if ("dinstone".equals(username) && "123456".equals(password)) {
                    System.out.println("login success");
                    // 登录成功
                    String jwtToken = Jwts.builder().setSubject(username).signWith(AuthConstant.JWT_KEY).compact();
                    responseObserver.onNext(LoginResponse.newBuilder().setToken(jwtToken).build());
                    responseObserver.onCompleted();
                } else {
                    System.out.println("login error");
                    // 登录失败
                    responseObserver.onError(
                            new StatusRuntimeException(Status.FAILED_PRECONDITION.withDescription("login error")));
                    // responseObserver.onCompleted();
                }
            }
        }).build();

        try {
            System.out.println("server on 9999");
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
