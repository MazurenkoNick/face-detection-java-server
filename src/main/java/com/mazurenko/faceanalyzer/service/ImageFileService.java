package com.mazurenko.faceanalyzer.service;

import com.mazurenko.faceanalyzer.data.FileEntity;
import com.mazurenko.faceanalyzer.proto.FaceValidationResponse;
import com.mazurenko.faceanalyzer.repository.FileRepository;
import com.mazurenko.faceanalyzer.util.LobHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.jdbc.core.JdbcTemplate;
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
public class ImageFileService {

    private static final String FILE_QUERY = "";

    private final LobHelper lobHelper;
    private final FaceAnalyzerClient faceAnalyzerClient;
    private final FileRepository fileRepository;
    private final JdbcTemplate jdbcTemplate;

    public void saveImageFile(MultipartFile multipartFile) throws IOException {
        // todo: optimize
        validateImage(multipartFile.getInputStream());
        FileEntity fileEntity = new FileEntity();
        Blob blob = lobHelper.createBlob(multipartFile.getInputStream(), multipartFile.getSize());
        fileEntity.setFileBlob(blob);
        fileEntity.setFileName(multipartFile.getOriginalFilename());
        fileEntity.setContentType(multipartFile.getContentType());

        fileRepository.save(fileEntity);
    }

    @Transactional(readOnly = true)
    public FileEntity writeImageFileToOutputStreamAndReturn(String fileName, OutputStream os)
            throws IOException, SQLException {

        FileEntity file = fileRepository.getFileEntityByFileName(fileName)
                .orElseThrow(() -> new IllegalArgumentException("Couldn't find file with name " + fileName));

        Blob fileBlob = file.getFileBlob();
        IOUtils.copy(fileBlob.getBinaryStream(), os);
        return file;
    }

    private void validateImage(InputStream is) throws IOException {
        FaceValidationResponse validationResponse = faceAnalyzerClient.isValidFacePicture(is);
        if (!validationResponse.getIsValid()) {
            throw new IllegalArgumentException(validationResponse.getMessage());
        }
    }
}