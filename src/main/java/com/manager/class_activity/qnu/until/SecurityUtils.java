package com.manager.class_activity.qnu.until;

import com.manager.class_activity.qnu.exception.BadException;
import com.manager.class_activity.qnu.exception.ErrorCode;
import com.nimbusds.jwt.SignedJWT;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

import java.text.ParseException;

public class SecurityUtils {

    /**
     * Retrieves the username of the currently authenticated user.
     *
     * @return the username of the current user or null if no authentication is
     * present.
     */
    public static String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (ObjectUtils.isEmpty(authentication) || !authentication.isAuthenticated()) {
            return null;
        }
        return authentication.getName();
    }

    public static String getCurrentUserType() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Kiểm tra xem authentication có tồn tại và đã xác thực hay chưa
        if (ObjectUtils.isEmpty(authentication) || !authentication.isAuthenticated()) {
            return null;
        }

        // Kiểm tra nếu authentication chứa thông tin JWT claims
        if (authentication.getPrincipal() instanceof org.springframework.security.oauth2.jwt.Jwt) {
            org.springframework.security.oauth2.jwt.Jwt jwt = (org.springframework.security.oauth2.jwt.Jwt) authentication.getPrincipal();
            return jwt.getClaimAsString("type");
        }

        // Nếu principal là dạng khác, có thể sử dụng cách custom để trích xuất claims
        // Ví dụ: nếu bạn dùng UserDetails
        return null;
    }

    public static boolean isRoleDepartment(){
        return "DEPARTMENT".equals(getCurrentUserType());
    }

    public static boolean isRoleStudent(){
        return "STUDENT".equals(getCurrentUserType());
    }

    public static SignedJWT getCurrentJWTToken(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (ObjectUtils.isEmpty(authentication) || !authentication.isAuthenticated()) {
            return null;
        }
        Jwt jwt = (Jwt) authentication.getCredentials();  // Cast để lấy đối tượng Jwt
        String jwtToken = jwt.getTokenValue();
        try {
            return SignedJWT.parse(jwtToken);
        }catch (ParseException e){
            System.out.println("Lỗi khi phân tích JWT: " + e.getMessage());
            throw new BadException(ErrorCode.UNCATEGORIZED_EXCEPTION);
        }
    }

}
