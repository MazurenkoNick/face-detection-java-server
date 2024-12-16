package com.mazurenko.faceanalyzer.util;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.hibernate.Session;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;

@Service
public class LobHelper {

    @PersistenceContext
    private EntityManager entityManager;

    public Blob createBlob(InputStream is, long size) throws IOException {
        return entityManager.unwrap(Session.class).getLobHelper().createBlob(is, size);
    }
}