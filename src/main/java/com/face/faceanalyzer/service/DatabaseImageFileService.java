package com.face.faceanalyzer.service;

import com.face.faceanalyzer.data.FileEntity;
import com.face.faceanalyzer.data.FileInfo;
import com.face.faceanalyzer.proto.FaceValidationResponse;
import com.face.faceanalyzer.repository.FileRepository;
import com.face.faceanalyzer.util.LobHelper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Blob;
import java.sql.SQLException;

@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "file-storage", value = "method", havingValue = "db")
public class DatabaseImageFileService implements ImageFileService {

    private final LobHelper lobHelper;
    private final FaceAnalyzerClient faceAnalyzerClient;
    private final FileRepository fileRepository;

    @SneakyThrows
    @Override
    public void saveImageFile(MultipartFile multipartFile) {
        validateImage(multipartFile.getInputStream());
        FileEntity fileEntity = new FileEntity();
        Blob blob = lobHelper.createBlob(multipartFile.getInputStream(), multipartFile.getSize());
        fileEntity.setFileBlob(blob);
        fileEntity.setFileName(multipartFile.getOriginalFilename());
        fileEntity.setContentType(multipartFile.getContentType());

        fileRepository.save(fileEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public FileInfo writeImageFileToOutputStreamAndReturn(String fileName, OutputStream os) throws IOException {
        FileEntity file = fileRepository.getFileEntityByFileName(fileName)
                .orElseThrow(() -> new IllegalArgumentException("Couldn't find file with name " + fileName));

        Blob fileBlob = file.getFileBlob();
        try (InputStream stream = fileBlob.getBinaryStream()) {
            IOUtils.copy(stream, os);
            return new FileInfo(fileName, file.getContentType());
        } catch (SQLException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private void validateImage(InputStream is) {
        try {
            FaceValidationResponse validationResponse = faceAnalyzerClient.isValidFacePicture(is);

            if (!validationResponse.getIsValid()) {
                throw new IllegalArgumentException(validationResponse.getMessage());
            }
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }
}