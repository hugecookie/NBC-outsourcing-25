package org.example.outsourcing.common.s3;

import lombok.RequiredArgsConstructor;
import org.example.outsourcing.common.s3.exception.S3Exception;
import org.example.outsourcing.common.s3.exception.S3ExceptionCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;


import java.io.IOException;
import java.time.Duration;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3Service {

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;

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

    private void validateFile(MultipartFile file) {
        String filename = file.getOriginalFilename();

        if (filename == null || !filename.matches("(?i).*\\.(jpg|jpeg|png)$")) {
            throw new S3Exception(S3ExceptionCode.NOT_SUPPORTED_FORMAT);
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new S3Exception(S3ExceptionCode.FILE_TOO_LARGE);
        }
    }

    public String generateSignedUrl(String key) {
        if (key == null || key.startsWith("http")) {
            // 이미 완성된 URL이거나 null이면 그대로 반환
            return key;
        }

        try {

            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .build();

            GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                    .getObjectRequest(getObjectRequest)
                    .signatureDuration(Duration.ofMinutes(10))
                    .build();

            return s3Presigner.presignGetObject(presignRequest)
                    .url()
                    .toString();
        } catch (Exception e) {
            throw new S3Exception(S3ExceptionCode.FAILED_TO_GENERATE_SIGNED_URL);
        }
    }

}
