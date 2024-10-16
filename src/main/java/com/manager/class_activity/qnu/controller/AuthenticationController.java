package com.manager.class_activity.qnu.controller;

import com.manager.class_activity.qnu.dto.request.AuthenticationRequest;
import com.manager.class_activity.qnu.dto.request.IntrospectRequest;
import com.manager.class_activity.qnu.dto.response.AuthenticationResponse;
import com.manager.class_activity.qnu.dto.response.IntrospectResponse;
import com.manager.class_activity.qnu.dto.response.JsonResponse;
import com.manager.class_activity.qnu.service.AuthenticationService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationController {
    AuthenticationService authenticationService;

    @PostMapping("/log-in")
    public JsonResponse<AuthenticationResponse> logIn(@RequestBody AuthenticationRequest request){
        return JsonResponse.success(authenticationService.authenticate(request));
    }

    @PostMapping("/introspect")
    public JsonResponse<IntrospectResponse> introspect(@RequestBody IntrospectRequest request) {
        return JsonResponse.success(authenticationService.introspect(request));
    }

    @PostMapping("/log-out")
    public JsonResponse<?> logout() throws ParseException {
        authenticationService.logout();
        return JsonResponse.success(null);
    }

}
