package org.example.google.drive.api.configuration;

import jakarta.annotation.Nonnull;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CustomJwtAuthenticationConverter implements Converter<Jwt, Collection<GrantedAuthority>> {
	@SuppressWarnings("unchecked")
	@Override
	public Collection<GrantedAuthority> convert(@Nonnull Jwt jwt) {
		Map<String, Object> realmAccess = jwt.getClaimAsMap("realm_access");
		if (realmAccess != null && realmAccess.containsKey("roles")) {
			List<String> roles = (List<String>) realmAccess.get("roles");
			// Convert roles to GrantedAuthority
			return roles.stream()
						.map(SimpleGrantedAuthority::new)
						.collect(Collectors.toList());
		}
		return List.of();
	}
}
