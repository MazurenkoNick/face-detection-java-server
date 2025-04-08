package com.face.faceanalyzer.service;

import com.face.faceanalyzer.proto.FaceDetectionServiceGrpc;
import com.face.faceanalyzer.proto.FaceValidationResponse;
import com.face.faceanalyzer.proto.ImageChunk;
import com.google.protobuf.ByteString;
import io.grpc.Channel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
public class FaceAnalyzerClient {

    private final FaceDetectionServiceGrpc.FaceDetectionServiceStub stub;

    public FaceAnalyzerClient(@Value("${face.analyzer.server.host}") String host,
                              @Value("${face.analyzer.server.port}") int port) {
        Channel channel = ManagedChannelBuilder
                .forAddress(host, port)
                .usePlaintext()
                .build();

        this.stub = FaceDetectionServiceGrpc.newStub(channel);
    }

    public CompletableFuture<FaceValidationResponse> isValidFacePicture(InputStream is) throws IOException {
        CompletableFuture<FaceValidationResponse> respFuture = new CompletableFuture<>();
        StreamObserver<FaceValidationResponse> responseObserver = getResponseObserver(respFuture);
        StreamObserver<ImageChunk> imageChunkStreamObserver = stub.validateFace(responseObserver);

        try (is) {
            byte[] buffer = new byte[1024 * 1024];
            int bytesRead;

            while ((bytesRead = is.read(buffer)) != -1) {
                ImageChunk chunk = ImageChunk.newBuilder()
                        .setImage(ByteString.copyFrom(buffer, 0, bytesRead))
                        .build();
                imageChunkStreamObserver.onNext(chunk);
            }
            imageChunkStreamObserver.onCompleted();
        } catch (Exception e) {
            imageChunkStreamObserver.onError(e);
            return CompletableFuture.completedFuture(buildErrorValidationResponse(e));
        }
        return respFuture;
    }

    private StreamObserver<FaceValidationResponse> getResponseObserver(
            CompletableFuture<FaceValidationResponse> completableFuture) {

        return new StreamObserver<>() {
            @Override
            public void onNext(FaceValidationResponse faceValidationResponse) {
                log.info("Received face validation response: {}", faceValidationResponse);
                completableFuture.complete(faceValidationResponse);
            }

            @Override
            public void onError(Throwable throwable) {
                log.error("Error on face validation request!", throwable);
                completableFuture.complete(buildErrorValidationResponse(throwable));
            }

            @Override
            public void onCompleted() {
                log.info("Completed face validation!");
            }
        };
    }

    private FaceValidationResponse buildErrorValidationResponse(Throwable e) {
        FaceValidationResponse reponse = FaceValidationResponse.newBuilder()
                .setIsValid(false)
                .setMessage(e.getMessage())
                .build();
        return reponse;
    }
}
