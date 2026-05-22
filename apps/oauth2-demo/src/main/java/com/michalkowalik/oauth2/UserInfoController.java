package com.michalkowalik.oauth2;

import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class UserInfoController {

    @GetMapping("/me")
    public Map<String, String> me(JwtAuthenticationToken auth) {
        Jwt jwt = auth.getToken();
        return Map.of(
                "sub",    jwt.getSubject(),
                "iss",    jwt.getIssuer().toString(),
                "scopes", jwt.getClaimAsString("scope")
        );
    }
}
