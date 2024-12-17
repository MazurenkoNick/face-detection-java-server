package com.face.faceanalyzer.service;

import com.face.faceanalyzer.data.FileInfo;
import com.face.faceanalyzer.proto.FaceValidationResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import static java.nio.file.Files.deleteIfExists;

@Slf4j
@Service
@ConditionalOnProperty(prefix = "file-storage", value = "method", havingValue = "directory", matchIfMissing = true)
public class FileStorageImageFileService implements ImageFileService {

    private final Path fileStoragePath;

    private final FaceAnalyzerClient faceAnalyzerClient;

    public FileStorageImageFileService(@Value("${file-storage.directory.path}") String fileStoragePath,
                                       FaceAnalyzerClient faceAnalyzerClient) {
        this.fileStoragePath = Path.of(fileStoragePath);
        this.faceAnalyzerClient = faceAnalyzerClient;
    }

    @Override
    public void saveImageFile(MultipartFile multipartFile) throws IOException {
        InputStream is = multipartFile.getInputStream();
        FaceValidationResponse validationResponse = faceAnalyzerClient.isValidFacePicture(is);
        if (!validationResponse.getIsValid()) {
            boolean isDeleted = deleteIfExists(getFilePath(multipartFile.getOriginalFilename()));
            log.info("Delete status after invalid image response: {}", isDeleted);
            throw new IllegalArgumentException(validationResponse.getMessage());
        }
        saveToFileToStorage(multipartFile);
    }

    @Override
    public FileInfo writeImageFileToOutputStreamAndReturn(String fileName, OutputStream os) throws IOException {
        File file = getFile(fileName);
        if (!file.exists()) {
            throw new IllegalArgumentException("Couldn't find file with name: " + fileName);
        }
        try (FileInputStream fis = new FileInputStream(file)) {
            IOUtils.copy(fis, os);
        }
        return new FileInfo(fileName, Files.probeContentType(file.toPath()));
    }

    private void saveToFileToStorage(MultipartFile file) throws IOException {
        log.info("Uploading file to local file system: {}", file.getOriginalFilename());

        if (!Files.exists(fileStoragePath)) {
            Files.createDirectories(fileStoragePath);
        }

        try (InputStream inputStream = file.getInputStream()) {
            Path path = getFilePath(file.getOriginalFilename());
            Files.copy(inputStream, path, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    private File getFile(String name) {
        return getFilePath(name).toFile();
    }

    private Path getFilePath(String fileName) {
        String filenameWithExtension = Paths.get(fileName).getFileName().toString();
        return fileStoragePath.resolve(filenameWithExtension);
    }
}
