package com.vic.googleUpload;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class images {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    Long id;

    String uploadId;

    String url;



}
