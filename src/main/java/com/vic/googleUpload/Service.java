package com.vic.googleUpload;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.FileContent;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@org.springframework.stereotype.Service
public class Service {

    private final imageRepo imageRepo;

    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final String SERVICE_ACOUNT_KEY_PATH = getPathToGoodleCredentials();

    public Service(com.vic.googleUpload.imageRepo imageRepo) {
        this.imageRepo = imageRepo;
    }

    private static String getPathToGoodleCredentials() {
        String currentDirectory = System.getProperty("user.dir");
        Path filePath = Paths.get(currentDirectory, "cred.json");
        return filePath.toString();
    }

    public List<Res> uploadImagesToDrive(List<File> files) throws GeneralSecurityException, IOException {
        List<Res> responses = new ArrayList<>();

        for (File file : files) {
            Res res = new Res();
            try {
                String folderId = "1xcHN63FMgQQAk2E2TaUoeLU48aN9ceOu";
                Drive drive = createDriveService();
                com.google.api.services.drive.model.File fileMetaData = new com.google.api.services.drive.model.File();
                fileMetaData.setName(file.getName());
                fileMetaData.setParents(Collections.singletonList(folderId));

                // Determine the content type based on the file extension
                String mimeType = java.nio.file.Files.probeContentType(file.toPath());
                FileContent mediaContent = new FileContent(mimeType, file);

                com.google.api.services.drive.model.File uploadedFile = drive.files().create(fileMetaData, mediaContent)
                        .setFields("id").execute();
                String imageUrl = "https://drive.google.com/uc?export=view&id=" + uploadedFile.getId();
                images images = new images();
                images.setUploadId(uploadedFile.getId());
                images.setUrl(imageUrl);
                imageRepo.save(images);
                System.out.println("IMAGE URL: " + imageUrl);
                file.delete();
                res.setStatus(200);
                res.setMessage("Image Successfully Uploaded To Drive");
                res.setUrl(imageUrl);
            } catch (Exception e) {
                System.out.println(e.getMessage());
                res.setStatus(500);
                res.setMessage(e.getMessage());
            }
            responses.add(res);
        }
        return responses;
    }

    @Transactional
    public Res deleteImageFromDrive(String fileId) throws GeneralSecurityException, IOException {
        Res res = new Res();
        try {
            Drive drive = createDriveService();
            drive.files().delete(fileId).execute();
            imageRepo.deleteByUploadId(fileId);
            res.setStatus(200);
            res.setMessage("Image Successfully Deleted From Drive");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            res.setStatus(500);
            res.setMessage(e.getMessage());
        }
        return res;
    }

    private Drive createDriveService() throws GeneralSecurityException, IOException {
        GoogleCredential credential = GoogleCredential.fromStream(new FileInputStream(SERVICE_ACOUNT_KEY_PATH))
                .createScoped(Collections.singleton(DriveScopes.DRIVE));

        return new Drive.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                JSON_FACTORY,
                credential)
                .setApplicationName("GoogleUpload")
                .build();
    }
}
