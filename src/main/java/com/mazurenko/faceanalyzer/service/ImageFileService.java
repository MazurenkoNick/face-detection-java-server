package com.mazurenko.faceanalyzer.service;

import com.mazurenko.faceanalyzer.data.FileInfo;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.OutputStream;

public interface ImageFileService {

    void saveImageFile(MultipartFile multipartFile) throws IOException;
    FileInfo writeImageFileToOutputStreamAndReturn(String fileName, OutputStream os) throws IOException;
}
