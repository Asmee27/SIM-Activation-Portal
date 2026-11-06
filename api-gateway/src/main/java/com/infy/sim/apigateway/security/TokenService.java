package com.infy.sim.apigateway.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.stream.Collectors;

@Service
public class TokenService {

    private final JwtEncoder encoder;
    private final String issuer;
    private final Duration expiry;

    public TokenService(JwtEncoder encoder,
                        @Value("${app.jwt.issuer}") String issuer,
                        @Value("${app.jwt.expiry-minutes:30}") long expiryMinutes) {
        this.encoder = encoder;
        this.issuer = issuer;
        this.expiry = Duration.ofMinutes(expiryMinutes);
    }

    public TokenResponse issue(Authentication authentication) {
        Instant now = Instant.now();
        String scope = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(authority -> authority.startsWith("ROLE_"))   // skip internal ones like FACTOR_PASSWORD
                .map(role -> role.substring("ROLE_".length()))
                .collect(Collectors.joining(" "));

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(issuer)
                .subject(authentication.getName())
                .issuedAt(now)
                .expiresAt(now.plus(expiry))
                .claim("scope", scope)              // becomes SCOPE_USER authority in resource servers
                .build();
        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();

        String token = encoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
        return new TokenResponse(token, "Bearer", expiry.toSeconds());
    }
}
