package com.vic.googleUpload;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api")
public class Controller {

    @Autowired
    private Service imageUploadService;

    @PostMapping("/upload-images")
    public ResponseEntity<List<Res>> uploadImages(@RequestParam("files") MultipartFile[] files) {
        List<File> fileList = new ArrayList<>();
        List<Res> responses = new ArrayList<>();

        try {
            // Convert MultipartFile to File
            for (MultipartFile multipartFile : files) {
                File file = convertMultipartFileToFile(multipartFile);
                fileList.add(file);
            }

            // Upload files to Google Drive
            responses = imageUploadService.uploadImagesToDrive(fileList);

            // Delete temporary files
            for (File file : fileList) {
                file.delete();
            }

            return new ResponseEntity<>(responses, HttpStatus.OK);
        } catch (IOException | GeneralSecurityException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private File convertMultipartFileToFile(MultipartFile multipartFile) throws IOException {
        File file = new File(System.getProperty("java.io.tmpdir") + "/" + multipartFile.getOriginalFilename());
        multipartFile.transferTo(file);
        return file;
    }





        @DeleteMapping("/delete-image/{fileId}")
        public ResponseEntity<Res> deleteImage(@PathVariable String fileId) {
            try {
                Res response = imageUploadService.deleteImageFromDrive(fileId);
                return new ResponseEntity<>(response, HttpStatus.OK);
            } catch (IOException | GeneralSecurityException e) {
                e.printStackTrace();
                Res errorResponse = new Res();
                errorResponse.setStatus(500);
                errorResponse.setMessage("Error deleting image");
                return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
    }

