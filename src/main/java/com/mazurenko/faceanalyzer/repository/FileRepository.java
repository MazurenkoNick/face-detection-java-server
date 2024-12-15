package com.mazurenko.faceanalyzer.repository;

import com.mazurenko.faceanalyzer.data.FileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.io.InputStream;

public interface FileRepository extends JpaRepository<FileEntity,Long> {

    @Modifying
    @Query(value = "INSERT INTO file_entity (file_name, content_type, file_blob) VALUES (:name, :contentType, :blob)",
            nativeQuery = true)
    void saveFile(@Param("name") String name,
                  @Param("contentType") String contentType,
                  @Param("blob") InputStream blob);

}
