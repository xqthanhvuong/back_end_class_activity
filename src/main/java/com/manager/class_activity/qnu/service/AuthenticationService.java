    package com.manager.class_activity.qnu.service;

    import com.manager.class_activity.qnu.dto.request.AuthenticationRequest;
    import com.manager.class_activity.qnu.dto.request.IntrospectRequest;
    import com.manager.class_activity.qnu.dto.response.AuthenticationResponse;
    import com.manager.class_activity.qnu.dto.response.IntrospectResponse;
    import com.manager.class_activity.qnu.entity.Account;
    import com.manager.class_activity.qnu.entity.InvalidatedToken;
    import com.manager.class_activity.qnu.exception.BadException;
    import com.manager.class_activity.qnu.exception.ErrorCode;
    import com.manager.class_activity.qnu.repository.AccountRepository;
    import com.manager.class_activity.qnu.repository.InvalidatedTokenRepository;
    import com.manager.class_activity.qnu.until.JwtUtil;
    import com.manager.class_activity.qnu.until.SecurityUtils;
    import com.nimbusds.jose.JOSEException;
    import com.nimbusds.jwt.SignedJWT;
    import lombok.AccessLevel;
    import lombok.RequiredArgsConstructor;
    import lombok.experimental.FieldDefaults;
    import lombok.extern.slf4j.Slf4j;
    import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
    import org.springframework.security.crypto.password.PasswordEncoder;
    import org.springframework.stereotype.Service;

    import java.text.ParseException;
    import java.time.LocalDateTime;
    import java.time.ZoneId;
    import java.util.Objects;

    @RequiredArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    @Service
    @Slf4j
    public class AuthenticationService {
        AccountRepository accountRepository;
        PermissionService permissionService;
        InvalidatedTokenRepository invalidatedTokenRepository;
        JwtUtil jwtUtil;

        public AuthenticationResponse authenticate(AuthenticationRequest request) {
            Account user = accountRepository.findByUsernameAndIsDeleted(request.getUsername(), false).orElseThrow(
                    () -> new BadException(ErrorCode.USER_NOT_EXISTED));
            PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
            if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                throw new BadException(ErrorCode.UNAUTHENTICATED);
            }
            var token = jwtUtil.generateToken(user.getUsername()
                    , permissionService.getPermissionNamesOfAccount(user.getUsername())
                    , user.getType().getName());
            return AuthenticationResponse.builder()
                    .token(token)
                    .build();
        }

        public SignedJWT verifyToken(String token) throws ParseException, JOSEException {
            SignedJWT signedJWT = jwtUtil.parseToken(token);
            jwtUtil.validateTokenExpiration(signedJWT);
            checkTokenInvalidation(signedJWT.getJWTClaimsSet().getJWTID());
            return signedJWT;
        }

        private void checkTokenInvalidation(String jwtId) {
            if (invalidatedTokenRepository.existsById(jwtId)) {
                throw new BadException(ErrorCode.UNAUTHENTICATED);
            }
        }


        public IntrospectResponse introspect(IntrospectRequest request) {
            var token =request.getToken();
            boolean isValid = true;
            try {
                verifyToken(token);
            }catch (BadException | ParseException | JOSEException e) {
                isValid = false;
            }
            return IntrospectResponse.builder()
                    .valid(isValid)
                    .build();
        }


        public void logout() {
            try {
                invalidateToken(Objects.requireNonNull(SecurityUtils.getCurrentJWTToken()));
            } catch (ParseException e) {
                log.error(e.getMessage());
                throw new BadException(ErrorCode.UNCATEGORIZED_EXCEPTION);
            }
        }

        public void invalidateToken(SignedJWT signToken) throws ParseException{
            String jwt = signToken.getJWTClaimsSet().getJWTID();
            LocalDateTime expiryTime = signToken.getJWTClaimsSet().getExpirationTime().toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime();

            InvalidatedToken invalidatedToken = new InvalidatedToken(jwt, expiryTime);
            invalidatedTokenRepository.save(invalidatedToken);
        }
    }
