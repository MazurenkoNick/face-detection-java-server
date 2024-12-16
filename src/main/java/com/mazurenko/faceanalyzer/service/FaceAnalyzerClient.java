package com.mazurenko.faceanalyzer.service;

import com.google.protobuf.ByteString;
import com.mazurenko.faceanalyzer.proto.FaceDetectionServiceGrpc;
import com.mazurenko.faceanalyzer.proto.FaceValidationRequest;
import com.mazurenko.faceanalyzer.proto.FaceValidationResponse;
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
        ByteString byteString = ByteString.readFrom(is);
        FaceValidationRequest request = FaceValidationRequest.newBuilder().setImage(byteString).build();
        return blockingStub.validateFace(request);
    }
}
