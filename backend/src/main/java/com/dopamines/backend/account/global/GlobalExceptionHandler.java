package com.dopamines.backend.account.global;


import com.dopamines.backend.account.exception.ErrorResponse;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice
public class GlobalExceptionHandler {

    // Refresh Token 만료
    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<ErrorResponse> refreshTokenExpiredException() {
        ErrorResponse errorResponse = new ErrorResponse(401, "Refresh Token이 만료되었습니다. 다시 로그인을 진행하여 Token을 갱신해주세요.");
        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }

    // 잘못된 Refresh Token
    @ExceptionHandler(JwtException.class)
    public ResponseEntity<ErrorResponse> refreshTokenVerificationException() {
        ErrorResponse errorResponse = new ErrorResponse(400, "유효하지 않은 Refresh Token 입니다.");
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
}


//@RestControllerAdvice
//public class GlobalExceptionHandler {
//
//    // Refresh Token 만료
//    @ExceptionHandler(TokenExpiredException.class)
//    public ResponseEntity<ErrorResponse> refreshTokenExpiredException() {
//        ErrorResponse errorResponse = new ErrorResponse(401, "Refresh Token이 만료되었습니다. 다시 로그인을 진행하여 Token을 갱신해주세요.");
//        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
//    }
//
//    // 잘못된 Refresh Token
//    @ExceptionHandler(JWTVerificationException.class)
//    public ResponseEntity<ErrorResponse> refreshTokenVerificationException() {
//        ErrorResponse errorResponse = new ErrorResponse(400, "유효하지 않은 Refresh Token 입니다.");
//        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
//    }
//}
