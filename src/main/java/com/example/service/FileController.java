package com.example.service;
import io.minio.RemoveObjectArgs;
import io.minio.errors.MinioException;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import org.apache.commons.io.IOUtils;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.OutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@RestController
public class FileController {

    @Value("${minio.bucketName}")
    private String bucketName;

    private final MinioClient minioClient;

    public FileController(MinioClient minioClient) {
        this.minioClient = minioClient;
    }

    @PostMapping("/upload")
    public String uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            InputStream inputStream = file.getInputStream();
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(file.getOriginalFilename())
                    .stream(inputStream, inputStream.available(), -1)
                    .build());
            return "File uploaded successfully!";
        } catch (Exception e) {
            e.printStackTrace();
            return "File upload failed!";
        }
    }

    @DeleteMapping("/delete/{fileName}")
    public String deleteFile(@PathVariable("fileName") String fileName) {
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucketName)
                            .object(fileName)
                            .build()
            );
            return "File deleted successfully!";
        } catch (MinioException | IOException e) {
            e.printStackTrace();
            return "File deletion failed!";
        } catch (InvalidKeyException | NoSuchAlgorithmException e) {
            e.printStackTrace();
            return "File deletion failed due to security issues.";
        }
    }

    @GetMapping("/download/{fileName}")
    public StreamingResponseBody downloadFile(@PathVariable("fileName") String fileName) {
        return outputStream -> {
            try {
                InputStream inputStream = minioClient.getObject(
                        GetObjectArgs.builder()
                                .bucket(bucketName)
                                .object(fileName)
                                .build()
                );

                IOUtils.copy(inputStream, outputStream);

                inputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
                outputStream.write("File download failed".getBytes());
            }
        };
    }

}
