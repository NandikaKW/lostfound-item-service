package com.spms.item_service.service;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
public class GcsService {

    // ":" passe empty default ekak denawa — property eka nathnam bean creation
    // fail wenne na, app eka start wenawa. Photo upload try kළoth witharai
    // meka empty widihata thiyenawa nam clear error ekak throw wenawa.
    @Value("${gcs.bucket-name:}")
    private String bucketName;

    @Value("${gcs.project-id:}")
    private String projectId;

    public String uploadFile(MultipartFile file) throws IOException {
        if (bucketName.isBlank() || projectId.isBlank()) {
            throw new IllegalStateException(
                    "Image upload is disabled — set gcs.bucket-name and gcs.project-id " +
                            "in application.properties to enable Google Cloud Storage uploads."
            );
        }

        Storage storage = StorageOptions.newBuilder()
                .setProjectId(projectId)
                .setCredentials(GoogleCredentials.getApplicationDefault())
                .build()
                .getService();

        String objectName = "items/" + UUID.randomUUID() + "_" + file.getOriginalFilename();

        BlobId blobId = BlobId.of(bucketName, objectName);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
                .setContentType(file.getContentType())
                .build();

        storage.create(blobInfo, file.getBytes());

        return String.format("https://storage.googleapis.com/%s/%s", bucketName, objectName);
    }

    public void deleteFile(String fileUrl) throws IOException {
        if (fileUrl == null || fileUrl.isEmpty() || bucketName.isBlank()) return;

        String prefix = "https://storage.googleapis.com/" + bucketName + "/";
        if (!fileUrl.startsWith(prefix)) return;
        String objectName = fileUrl.substring(prefix.length());

        Storage storage = StorageOptions.newBuilder()
                .setProjectId(projectId)
                .setCredentials(GoogleCredentials.getApplicationDefault())
                .build()
                .getService();

        storage.delete(BlobId.of(bucketName, objectName));
    }
}