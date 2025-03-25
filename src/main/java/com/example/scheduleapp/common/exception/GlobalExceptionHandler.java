package com.example.scheduleapp.common.exception;


import com.example.scheduleapp.common.response.ApiResponseDto;
import com.example.scheduleapp.common.response.enums.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    /**
     * DTO 유효성 검증 실패 시 발생하는 예외 처리
     * - @Valid 어노테이션이 붙은 DTO 필드 검증 실패 시 발생
     * - 예: @NotBlank, @Size 등 제약 조건을 위반한 경우
     * - 사용자가 설정한 message 값을 추출하여 클라이언트에게 응답
     */
    @ExceptionHandler(value = {MethodArgumentNotValidException.class})
    public ResponseEntity<ApiResponseDto<?>> handlerMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.error("Validation failed: {}", e.getMessage());

        // 첫 번째 필드 에러 메시지 추출 (여러 개일 경우 하나만)
        String errorMessage = e.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(fieldError -> fieldError.getDefaultMessage())
                .orElse("잘못된 요청입니다.");

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponseDto.fail(400, errorMessage)); // 커스텀 메시지 적용
    }

    /**
     * 요청 파라미터 타입이 일치하지 않을 때 발생하는 예외 처리
     * - 예: @RequestParam Long id 에 문자열이 들어올 경우
     */
    @ExceptionHandler(value = {MethodArgumentTypeMismatchException.class})
    public ResponseEntity<ApiResponseDto<?>> handlerMethodArgumentTypeMismatchException(Exception e) {
        log.error(
                "handlerMethodArgumentTypeMismatchException() in GlobalExceptionHandler throw MethodArgumentTypeMismatchException : {}",
                e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponseDto.fail(ErrorCode.BAD_REQUEST));
    }

    /**
     * @Validated 또는 @RequestParam 등에서 유효성 검증이 실패했을 때 발생
     * - Spring 6+에서 유효성 관련 검증 실패 시 발생하는 예외
     */
    @ExceptionHandler(value = {HandlerMethodValidationException.class})
    public ResponseEntity<ApiResponseDto<?>> handlerHandlerMethodValidationException(Exception e) {
        log.error(
                "handlerHandlerMethodValidationException() in GlobalExceptionHandler throw HandlerMethodValidationException : {}",
                e.getMessage());


        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponseDto.fail(ErrorCode.BAD_REQUEST));
    }

    /**
     * 요청 헤더가 누락된 경우 발생하는 예외 처리
     * - 예: @RequestHeader("Authorization") 이 누락됐을 때
     */
    @ExceptionHandler(value = {MissingRequestHeaderException.class})
    public ResponseEntity<ApiResponseDto<?>> handlerMissingRequestHeaderException(Exception e) {
        log.error(
                "handlerMissingRequestHeaderException() in GlobalExceptionHandler throw MissingRequestHeaderException : {}",
                e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponseDto.fail(ErrorCode.MISSING_REQUIRED_HEADER));
    }

    /**
     * 필수 요청 파라미터가 빠졌을 때 발생하는 예외 처리
     * - 예: @RequestParam(required = true) 가 누락된 경우
     */
    @ExceptionHandler(value = {MissingServletRequestParameterException.class})
    public ResponseEntity<ApiResponseDto<?>> handlerMissingServletRequestParameterException(Exception e) {
        log.error(
                "handlerMissingServletRequestParameterException() in GlobalExceptionHandler throw MissingServletRequestParameterException : {}",
                e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponseDto.fail(ErrorCode.MISSING_REQUIRED_PARAMETER));
    }

    /**
     * 요청 본문(JSON 등)이 올바르지 않아 역직렬화가 실패한 경우
     * - 예: 잘못된 JSON 형식, 타입 불일치 등
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponseDto<?>> handleMessageNotReadableException(HttpMessageNotReadableException e) {
        log.error(
                "handleMessageNotReadableException() in GlobalExceptionHandler throw HttpMessageNotReadableException : {}",
                e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponseDto.fail(ErrorCode.BAD_REQUEST));
    }

    /**
     * 존재하지 않는 URI로 접근했을 때 발생하는 예외 처리
     * - 예: 잘못된 URL 요청
     */
    @ExceptionHandler(value = {NoHandlerFoundException.class})
    public ResponseEntity<ApiResponseDto<?>> handleNoPageFoundException(Exception e) {
        log.error("handleNoPageFoundException() in GlobalExceptionHandler throw NoHandlerFoundException : {}",
                e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponseDto.fail(ErrorCode.NOT_FOUND));
    }

    /**
     * 지원하지 않는 HTTP 메서드로 요청했을 때 발생
     * - 예: POST만 가능한 엔드포인트에 GET 요청 등
     */
    @ExceptionHandler(value = {HttpRequestMethodNotSupportedException.class})
    public ResponseEntity<ApiResponseDto<?>> handleMethodNotSupportedException(Exception e) {
        log.error(
                "handleMethodNotSupportedException() in GlobalExceptionHandler throw HttpRequestMethodNotSupportedException : {}",
                e.getMessage());
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(ApiResponseDto.fail(ErrorCode.METHOD_NOT_ALLOWED));
    }

    /**
     * 커스텀 예외(CustomException)를 처리하는 핸들러
     * - 개발자가 정의한 도메인/비즈니스 예외
     * - 예: 로그인 실패, 권한 없음 등
     */
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ApiResponseDto<?>> handleCustomException(CustomException e) {
        log.error("handleException() in GlobalExceptionHandler throw BusinessException : {}", e.getMessage());
        return ResponseEntity.status(e.getHttpStatus())
                .body(ApiResponseDto.fail(e.getErrorCode()));
    }


    /**
     * 그 외 모든 예외 처리
     * - 명시적으로 처리하지 않은 예외들을 잡아 처리
     * - 예: NullPointerException, DB 예외 등
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponseDto<?>> handlerException(Exception e) {
        log.error("handlerException() in GlobalExceptionHandler throw Exception : {} {}", e.getClass(), e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponseDto.fail(ErrorCode.INTERNAL_SERVER_ERROR));
    }
}

