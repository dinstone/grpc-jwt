package com.dinstone.grpc.server;

import com.dinstone.grpc.api.AuthConstant;

import io.grpc.Context;
import io.grpc.Contexts;
import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;
import io.grpc.Status;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;

public class AuthInterceptor implements ServerInterceptor {
    private JwtParser parser = Jwts.parserBuilder().setSigningKey(AuthConstant.JWT_KEY).build();

    @Override
    public <reqt, respt> ServerCall.Listener<reqt> interceptCall(ServerCall<reqt, respt> serverCall, Metadata metadata,
            ServerCallHandler<reqt, respt> serverCallHandler) {
        String authorization = metadata
                .get(Metadata.Key.of(AuthConstant.AUTH_HEADER, Metadata.ASCII_STRING_MARSHALLER));
        Status status = Status.OK;
        if (authorization == null) {
            status = Status.UNAUTHENTICATED.withDescription("miss authentication token");
        } else if (!authorization.startsWith(AuthConstant.AUTH_TOKEN_TYPE)) {
            status = Status.UNAUTHENTICATED.withDescription("unknown token type");
        } else {
            Jws<Claims> claims = null;
            String token = authorization.substring(AuthConstant.AUTH_TOKEN_TYPE.length()).trim();
            try {
                claims = parser.parseClaimsJws(token);
            } catch (JwtException e) {
                status = Status.UNAUTHENTICATED.withDescription(e.getMessage()).withCause(e);
            }
            if (claims != null) {
                Context ctx = Context.current().withValue(AuthConstant.AUTH_CLIENT_ID, claims.getBody().getSubject());
                return Contexts.interceptCall(ctx, serverCall, metadata, serverCallHandler);
            }
        }
        serverCall.close(status, new Metadata());
        return new ServerCall.Listener<reqt>() {
        };
    }
}