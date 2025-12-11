package com.restaurant.filter_module.jwt.enums;

import com.restaurant.data.enums.IBaseErrorCode;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;


/**
 * The enum Jwt expired error code.
 */
public enum JwtExpiredErrorCode implements IBaseErrorCode {

        JWT_EXPIRED_ERROR_CODE("429", "ID_IS_NULL", HttpStatus.UNAUTHORIZED.value())
        ;
        private final String errorCode;
        private final String messageCode;
        private final int httpStatusCode;

        JwtExpiredErrorCode(String errorCode, String messageCode, int httpStatusCode) {
            this.errorCode = errorCode;
            this.messageCode = messageCode;
            this.httpStatusCode = httpStatusCode;
        }

        @Override
        public String getErrorCode() {
            return errorCode;
        }

        @Override
        public String getMessageCode() {
            return messageCode;
        }

        @Override
        public int getHttpStatusCode() {
            return httpStatusCode;
        }

}
