package com.face.faceanalyzer.service;

import com.google.protobuf.ByteString;
import com.face.faceanalyzer.proto.FaceDetectionServiceGrpc;
import com.face.faceanalyzer.proto.FaceValidationRequest;
import com.face.faceanalyzer.proto.FaceValidationResponse;
import io.grpc.Channel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;

@Service
public class FaceAnalyzerClient {

    private final FaceDetectionServiceGrpc.FaceDetectionServiceBlockingStub blockingStub;

    public FaceAnalyzerClient(@Value("${face.analyzer.server.host}") String host,
                              @Value("${face.analyzer.server.port}") int port) {
        Channel channel = ManagedChannelBuilder
                .forAddress(host, port)
                .usePlaintext()
                .build();
        this.blockingStub = FaceDetectionServiceGrpc.newBlockingStub(channel);
    }

    public FaceValidationResponse isValidFacePicture(InputStream is) throws IOException {
        try (is) {
            ByteString byteString = ByteString.readFrom(is);
            FaceValidationRequest request = FaceValidationRequest.newBuilder().setImage(byteString).build();
            return blockingStub.validateFace(request);
        } catch (Exception e) {
            return buildErrorValidationResponse(e);
        }
    }

    private static FaceValidationResponse buildErrorValidationResponse(Exception e) {
        return FaceValidationResponse.newBuilder()
                .setIsValid(false)
                .setMessage(e.getMessage())
                .build();
    }
}
