package org.example.outsourcing.common.s3;

import org.example.outsourcing.common.s3.exception.S3Exception;
import org.example.outsourcing.common.s3.exception.S3ExceptionCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockMultipartFile;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.net.MalformedURLException;
import java.net.URL;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class S3ServiceTest {

    private final S3Client s3Client = Mockito.mock(S3Client.class);
    private final S3Presigner s3Presigner = Mockito.mock(S3Presigner.class);
    private final S3Service s3Service = new S3Service(s3Client, s3Presigner);

    @Nested
    @DisplayName("uploadFile 메서드 테스트")
    class UploadFileTest {

        @Test
        @DisplayName("성공적으로 업로드할 수 있다.")
        void uploadFileSuccess() {
            // given
            MockMultipartFile file = new MockMultipartFile(
                    "file",
                    "test.jpg",
                    "image/jpeg",
                    "test data".getBytes()
            );

            // when & then
            assertDoesNotThrow(() -> s3Service.uploadFile(file));
        }

        @Test
        @DisplayName("파일 이름이 null이면 예외가 발생한다.")
        void uploadFileFilenameNull() {
            // given
            MockMultipartFile file = new MockMultipartFile(
                    "file",
                    null, // <--- 파일 이름이 null
                    "image/jpeg",
                    "test data".getBytes()
            );

            // when & then
            assertThatThrownBy(() -> s3Service.uploadFile(file))
                    .isInstanceOf(S3Exception.class)
                    .hasMessage(S3ExceptionCode.NOT_SUPPORTED_FORMAT.getMessage());
        }

        @Test
        @DisplayName("지원하지 않는 확장자 업로드시 예외가 발생한다.")
        void uploadFileUnsupportedFormat() {
            // given
            MockMultipartFile file = new MockMultipartFile(
                    "file",
                    "test.txt",
                    "text/plain",
                    "test data".getBytes()
            );

            // when & then
            assertThatThrownBy(() -> s3Service.uploadFile(file))
                    .isInstanceOf(S3Exception.class)
                    .hasMessage(S3ExceptionCode.NOT_SUPPORTED_FORMAT.getMessage());
        }

        @Test
        @DisplayName("10MB 초과 파일 업로드시 예외가 발생한다.")
        void uploadFileTooLarge() {
            // given
            byte[] largeData = new byte[11 * 1024 * 1024];
            MockMultipartFile file = new MockMultipartFile(
                    "file",
                    "large.jpg",
                    "image/jpeg",
                    largeData
            );

            // when & then
            assertThatThrownBy(() -> s3Service.uploadFile(file))
                    .isInstanceOf(S3Exception.class)
                    .hasMessage(S3ExceptionCode.FILE_TOO_LARGE.getMessage());
        }
    }

    @Nested
    @DisplayName("generateSignedUrl 메서드 테스트")
    class GenerateSignedUrlTest {

        @Test
        @DisplayName("완성된 URL이면 그대로 반환한다.")
        void generateSignedUrlAlreadyUrl() {
            // given
            String url = "https://my-bucket.s3.amazonaws.com/default/profile.png";

            // when
            String result = s3Service.generateSignedUrl(url);

            // then
            assertThat(result).isEqualTo(url);
        }

        @Test
        @DisplayName("null이면 null 그대로 반환한다.")
        void generateSignedUrlNull() {
            // when
            String result = s3Service.generateSignedUrl(null);

            // then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("정상적으로 presigned URL을 생성한다.")
        void generateSignedUrlSuccess() throws MalformedURLException {
            // given
            String key = "uploads/test.png";
            PresignedGetObjectRequest mockPresigned = Mockito.mock(PresignedGetObjectRequest.class);
            URL dummyUrl = new URL("https://dummy-presigned-url.com/file");
            when(mockPresigned.url()).thenReturn(dummyUrl);

            when(s3Presigner.presignGetObject(any(GetObjectPresignRequest.class)))
                    .thenReturn(mockPresigned);

            // when
            String signedUrl = s3Service.generateSignedUrl(key);

            // then
            assertThat(signedUrl).isEqualTo(dummyUrl.toString());
        }

        @Test
        @DisplayName("Presign 실패시 예외가 발생한다.")
        void generateSignedUrlFail() {
            // given
            String key = "uploads/error.png";
            when(s3Presigner.presignGetObject(any(GetObjectPresignRequest.class)))
                    .thenThrow(RuntimeException.class);

            // when & then
            assertThatThrownBy(() -> s3Service.generateSignedUrl(key))
                    .isInstanceOf(S3Exception.class)
                    .hasMessage(S3ExceptionCode.FAILED_TO_GENERATE_SIGNED_URL.getMessage());
        }
    }
}
