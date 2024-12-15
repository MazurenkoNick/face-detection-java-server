package com.mazurenko.faceanalyzer.service;

import com.mazurenko.faceanalyzer.data.FileEntity;
import com.mazurenko.faceanalyzer.repository.FileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.sql.rowset.serial.SerialBlob;
import java.io.IOException;
import java.sql.SQLException;

@Service
@RequiredArgsConstructor
public class FileService {

    private final FileRepository fileRepository;

    public void saveFile(MultipartFile file) throws IOException, SQLException {
        FileEntity fileEntity = new FileEntity();
        fileEntity.setFileName(file.getOriginalFilename());
        fileEntity.setContentType(file.getContentType());
        fileEntity.setFileBlob(new SerialBlob(file.getBytes()));

        fileRepository.save(fileEntity);
    }
}