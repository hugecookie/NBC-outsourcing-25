package org.example.outsourcing.common.s3;

import lombok.RequiredArgsConstructor;
import org.example.outsourcing.common.s3.exception.S3Exception;
import org.example.outsourcing.common.s3.exception.S3ExceptionCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3Service {

    private final S3Client s3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB

    public String uploadFile(MultipartFile file) {
        validateFile(file);
        String key = UUID.randomUUID() + "_" + file.getOriginalFilename();

        try {
            PutObjectRequest putRequest = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .contentType(file.getContentType())
                    .build();

            s3Client.putObject(putRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

            return key;
        } catch (IOException e) {
            throw new S3Exception(S3ExceptionCode.UPLOAD_FAILED);
        }
    }

    public String getFileUrl(String key) {
        return "https://" + bucket + ".s3.amazonaws.com/" + key;
    }

    private void validateFile(MultipartFile file) {
        String filename = file.getOriginalFilename();

        if (filename == null || !filename.matches("(?i).*\\.(jpg|jpeg|png)$")) {
            throw new S3Exception(S3ExceptionCode.NOT_SUPPORTED_FORMAT);
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new S3Exception(S3ExceptionCode.FILE_TOO_LARGE);
        }
    }
}
