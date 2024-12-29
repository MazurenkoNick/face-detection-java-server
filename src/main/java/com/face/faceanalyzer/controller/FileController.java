package com.face.faceanalyzer.controller;

import com.face.faceanalyzer.data.FileInfo;
import com.face.faceanalyzer.data.FileUploadedResponse;
import com.face.faceanalyzer.service.ImageFileService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class FileController {

    private final ImageFileService imageFileService;

    @PostMapping("/upload")
    public ResponseEntity<FileUploadedResponse> uploadFile(@RequestParam("file") MultipartFile file) throws IOException {
        imageFileService.saveImageFile(file);
        return ResponseEntity.ok(new FileUploadedResponse("File uploaded successfully"));
    }

    @GetMapping("/download/{name}")
    public ResponseEntity<Void> downloadFile(@PathVariable String name, HttpServletResponse response) throws IOException {

        FileInfo file = imageFileService.writeImageFileToOutputStreamAndReturn(name, response.getOutputStream());
        setResponseHeaders(response, file);

        return ResponseEntity.ok().build();
    }

    private void setResponseHeaders(HttpServletResponse response, FileInfo file) {
        response.setHeader(HttpHeaders.LAST_MODIFIED, String.valueOf(System.currentTimeMillis()));
        response.setHeader(HttpHeaders.CONTENT_TYPE, file.contentType());
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.name() + "\"");
    }
}
