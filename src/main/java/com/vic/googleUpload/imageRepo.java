package com.vic.googleUpload;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface imageRepo extends JpaRepository<images, Long> {
    void deleteByUploadId(String uploadId);
}
