package ch.uzh.ifi.hase.soprafs24.service;

import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import org.springframework.stereotype.Service;

@Service
public class GoogleCloudStorageService {

    private final Storage storage;
    private final String bucketName = "fs25-group40-bucket"; // Replace with your actual bucket name

    public GoogleCloudStorageService(Storage storage) {
        this.storage = storage;
    }

    // Method to get file from bucket by filename
    public Blob getFileFromBucket(String filename) {
        String bucketName = "fs25-group40-bucket";  // Use your bucket name
        BlobId blobId = BlobId.of(bucketName, filename);  // Specify the bucket and file name
        return storage.get(blobId);  // Return the Blob (which contains the file data)
    }

    public String uploadFile(byte[] fileBytes, String fileName) {
        BlobId blobId = BlobId.of(bucketName, fileName);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();

        storage.create(blobInfo, fileBytes);

        // Optional: Generate a public URL if the file is publicly accessible
        return String.format("https://storage.googleapis.com/%s/%s", bucketName, fileName);
    }

    public boolean deleteFile(String imageUrl) {
        try {
            String bucketName = this.bucketName;
            String fileName = extractFileNameFromUrl(imageUrl);

            Storage storage = StorageOptions.getDefaultInstance().getService();
            Blob blob = storage.get(bucketName, fileName);

            if (blob != null) {
                return blob.delete();
            } else {
                return false; // File not found
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private String extractFileNameFromUrl(String url) {
        return url.substring(url.lastIndexOf("/") + 1);
    }






}

