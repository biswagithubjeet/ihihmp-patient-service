package com.ihimp.patientservice.security;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class JwtAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {

        List<String> permissions = jwt.getClaimAsStringList(SecurityConstants.PERMISSIONS_CLAIM);

        if(permissions==null){
            permissions = Collections.emptyList();
        }

        Collection<SimpleGrantedAuthority> authorities =
                permissions.stream().map(SimpleGrantedAuthority::new).toList();

        Objects.requireNonNull(jwt.getSubject(),SecurityConstants.JWT_SUBJECT_REQUIRED_MESSAGE);

        return new JwtAuthenticationToken(jwt, authorities,jwt.getSubject());
    }
}
