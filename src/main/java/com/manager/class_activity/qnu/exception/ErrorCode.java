package com.manager.class_activity.qnu.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized exception", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_KEY(1000, "Uncategorized exception", HttpStatus.BAD_REQUEST),
    USER_EXISTED(1001, "User already existed", HttpStatus.CONFLICT),
    USERNAME_INVALID(1002, "Invalid email format.", HttpStatus.BAD_REQUEST),
    PASSWORD_INVALID(1003,
            "Password must be 8-20 characters long and contain at least one lowercase letter, one uppercase letter, one digit, and one special character.",
            HttpStatus.BAD_REQUEST),
    USER_NOT_EXISTED(1004, "User not existed", HttpStatus.NOT_FOUND),
    UNAUTHENTICATED(1005, "Unauthenticated", HttpStatus.UNAUTHORIZED),
    NAME_INVALID(1006, "Name must be 2-50 characters long and not be blank", HttpStatus.BAD_REQUEST),
    ACCESS_DENIED(1007, "You do not have permission.", HttpStatus.FORBIDDEN),
    METHOD_NOT_ALLOWED(1008, "Method not allowed.", HttpStatus.METHOD_NOT_ALLOWED),
    POST_NOT_EXISTED(1009, "Post not existed", HttpStatus.NOT_FOUND),
    COMMENT_INVALID(1010, "Comment invalid.", HttpStatus.BAD_REQUEST),
    MISSING_USER_ID(1011, "Missing user id", HttpStatus.BAD_REQUEST),
    MISSING_POST_ID(1012, "Missing post id", HttpStatus.BAD_REQUEST),
    POST_CONTENT_INVALID(1013, "Post content invalid", HttpStatus.BAD_REQUEST),
    POST_TITLE_INVALID(1014, "Post title invalid", HttpStatus.BAD_REQUEST),
    USER_IS_BLOCK(1015, "your account has been blocked.", HttpStatus.FORBIDDEN),
    INVALID_LOCATION(1016, "Invalid location", HttpStatus.BAD_REQUEST),
    COMMENT_NOT_EXISTED(1017, "Comment parent not existed", HttpStatus.NOT_FOUND),
    COMMENT_PARENT_INVALID(1018, "Comment parent invalid", HttpStatus.BAD_REQUEST),
    POST_IS_NOT_PUBLISH(1019, "This post is not publish", HttpStatus.BAD_REQUEST),
    TOKEN_INVALID(1020, "Token invalid", HttpStatus.UNAUTHORIZED),
    REJECTION_REASON_INVALID(1021, "The reason is not be blank, and max is 255 characters", HttpStatus.BAD_REQUEST),
    DEPARTMENT_NOT_FOND(1022,"Department not fond" ,HttpStatus.NOT_FOUND),
    DEPARTMENT_IS_EXISTED(1023,"Department name is existed" , HttpStatus.CONFLICT),
    COURSE_NOT_FOUND(1024,"Course not fond" ,HttpStatus.NOT_FOUND),
    INVALID_FORMAT_CSV(1025, "Invalid CSV format.", HttpStatus.BAD_REQUEST),
    STAFF_NOT_FOND(1026,"Staff not fond" ,HttpStatus.NOT_FOUND),
    COURSE_NAME_EXISTED(1027,"Course name is existed" , HttpStatus.CONFLICT),
    STAFF_IS_EXISTED(1028,"Staff email is existed" , HttpStatus.CONFLICT),
    LECTURER_NOT_FOUND(1029,"Lecturer not fond" ,HttpStatus.NOT_FOUND),
    LECTURER_IS_EXISTED(1030,"Lecturer email is existed" , HttpStatus.CONFLICT),
    CLASS_NOT_FOUND(1031,"Class not fond" ,HttpStatus.NOT_FOUND),
    STUDENT_NOT_FOUND(1032,"Student not fond" ,HttpStatus.NOT_FOUND),
    ADVISOR_NOT_FOUND(1033,"Advisor not fond" ,HttpStatus.NOT_FOUND),
    STUDENT_POSITION_NOT_FOND(1034,"Position not fond" ,HttpStatus.NOT_FOUND),
    DUPLICATE_POSITION(1035, "Student is already holding this position",HttpStatus.CONFLICT),
    UPLOAD_ERROR(1036,"Upload file ERROR" ,HttpStatus.BAD_REQUEST ),
    INVALID_FILE(1037,"File invalid" ,HttpStatus.BAD_REQUEST ),
    CLASS_ACTIVITY_NOT_FOUND(1038, "Class activity not found" ,HttpStatus.NOT_FOUND ),
    PERMISSION_NOT_FOUND(1039,"Permission not found" ,HttpStatus.NOT_FOUND ),
    ROLE_NOT_FOND(1040,"Role not found" ,HttpStatus.NOT_FOUND ),
    CANT_DELETE(1041,"Data can't delete" ,HttpStatus.FORBIDDEN ),
    TYPE_ERROR(1042,"Type error" ,HttpStatus.NOT_FOUND ),
    TYPE_NOT_MATCH(1043,"Type not match" ,HttpStatus.CONFLICT ),
    ACTIVITY_EXIST(1044,"Activity existed" ,HttpStatus.CONFLICT ),
    DOCUMENT_NOT_FOUND(1045,"document not found" ,HttpStatus.NOT_FOUND );

    private final int code;
    private final String message;
    private final HttpStatusCode statusCode;

    ErrorCode(int code, String message, HttpStatusCode statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }

    // if err key wrong return code 1000
    public static ErrorCode getError(String errKey) {
        ErrorCode errorCode = ErrorCode.INVALID_KEY;
        try {
            errorCode = ErrorCode.valueOf(errKey);
        } catch (IllegalArgumentException ignored) {

        }
        return errorCode;
    }

}

