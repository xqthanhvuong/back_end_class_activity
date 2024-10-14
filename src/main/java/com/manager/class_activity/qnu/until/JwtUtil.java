package com.manager.class_activity.qnu.until;

import com.manager.class_activity.qnu.constant.JwtConstant;
import com.manager.class_activity.qnu.exception.InvalidTokenException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.RequiredArgsConstructor;
import lombok.experimental.NonFinal;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.Date;
import java.util.Set;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class JwtUtil {

    @Value("${jwt.signerKey}")
    @NonFinal
    private String SECRET_KEY;
    @NonFinal
    private static final long EXPIRE_DURATION = 24 * 60 * 60 * 1000; // 24 hours

    public String generateToken(String username, Set<String> permissions, String typeName) {
        try {
            JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);
            JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                    .subject(username)
                    .issuer("qnu.com")
                    .claim("scope", permissions)
                    .claim("type",typeName)
                    .issueTime(new Date())
                    .expirationTime(new Date(new Date().getTime() + EXPIRE_DURATION))
                    .jwtID(UUID.randomUUID().toString())
                    .build();
            Payload payload = new Payload(claimsSet.toJSONObject());
            JWSObject jwsObject = new JWSObject(header, payload);
            jwsObject.sign(new MACSigner(SECRET_KEY.getBytes()));
            return jwsObject.serialize();

        } catch (JOSEException e) {
            throw new InvalidTokenException(JwtConstant.JWT_GENER_ERROR);
        }
    }

    public SignedJWT parseToken(String token) {
        if (ObjectUtils.isEmpty(token)) {
            throw new InvalidTokenException(JwtConstant.TOKEN_EMPTY);
        }
        try {
            return SignedJWT.parse(token);
        } catch (ParseException e) {
            throw new InvalidTokenException(JwtConstant.TOKEN_PARSE_ERROR);
        }
    }

    public boolean validateTokenExpiration(SignedJWT signedJWT) {
        Date expirationDate;
        try {
            expirationDate = signedJWT.getJWTClaimsSet().getExpirationTime();
        } catch (ParseException e) {
            throw new InvalidTokenException(JwtConstant.TOKEN_PARSE_ERROR);
        }
        return ObjectUtils.isNotEmpty(expirationDate) && expirationDate.after(new Date());
    }

}

