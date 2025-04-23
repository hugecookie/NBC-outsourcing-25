package org.example.outsourcing.common.advice;

import org.example.outsourcing.common.annotation.ResponseMessage;
import org.example.outsourcing.common.dto.CommonResponse;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import lombok.NonNull;

@RestControllerAdvice
public class ResponseWrapping implements ResponseBodyAdvice<Object> {

	@Override
	public boolean supports(MethodParameter returnType,
		@NonNull Class<? extends HttpMessageConverter<?>> converterType) {
		Class<?> declaringClass = returnType.getDeclaringClass();
		return declaringClass.isAnnotationPresent(RestController.class);
	}

	/* or 조건으로 @ResponseBody 까지 검사하면 exceptionHandler 도 여기서 랩핑 되서
	   일단 현재 계획은 ExceptionHandler 는 따로 또 ResponseBodyAdvice 를 만들던지
	   혹은 exceptionHandler 내부에서 랩핑하던지 하게끔 처리할 예정입니다      */

	@Override
	public Object beforeBodyWrite(
		Object body,
		@NonNull MethodParameter returnType,
		@NonNull MediaType selectedContentType,
		@NonNull Class<? extends HttpMessageConverter<?>> selectedConverterType,
		@NonNull ServerHttpRequest request,
		@NonNull ServerHttpResponse response
	) {

		String path = request.getURI().getPath();

		if (path.startsWith("/v3/api-docs") || path.startsWith("/swagger-ui")) {
			return body;
		}

		//swagger 내용물도 랩핑해버리므로 해당 경로는 예외처리

		if (body instanceof CommonResponse<?>) {
			return body;
		}

		ResponseMessage rm = returnType.getMethodAnnotation(ResponseMessage.class);
		String message = (rm != null)
			? rm.value()
			: "정상적으로 수행되었습니다.";
		HttpStatusCode status = response instanceof ServletServerHttpResponse servlet
			? HttpStatusCode.valueOf(servlet.getServletResponse().getStatus())
			: HttpStatusCode.valueOf(HttpStatus.OK.value());

		return CommonResponse.of(true, message, status.value(), body);
	}
}
