package com.mazurenko.faceanalyzer.repository;

import com.mazurenko.faceanalyzer.data.FileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Blob;
import java.util.Optional;

public interface FileRepository extends JpaRepository<FileEntity,Long> {

    @Transactional
    @Query(value = "INSERT INTO file_entity (file_name, content_type, file_blob) VALUES (:name, :contentType, :blobData)", nativeQuery = true)
    void saveFileWithNativeQuery(
            @Param("name") String name,
            @Param("contentType") String contentType,
            @Param("blobData") Blob blobData);

    Optional<FileEntity> getFileEntityByFileName(String name);
}
