package ma.isga.inventoryservice.sec;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class JwtAuthConverter implements Converter<Jwt, AbstractAuthenticationToken> {
    private final JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter=new JwtGrantedAuthoritiesConverter();


    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        Collection<GrantedAuthority> authorities = Stream.concat(
                jwtGrantedAuthoritiesConverter.convert(jwt).stream(),
                extractResourceRoles(jwt).stream()
        ).collect(Collectors.toSet());
        return new JwtAuthenticationToken(jwt, authorities,jwt.getClaim("preferred_username"));
    }

    private Collection<GrantedAuthority> extractResourceRoles(Jwt jwt) {
        Map<String , Object> realmAccess;
        Collection<String> roles;
        if(jwt.getClaim("realm_access")==null){
            return Set.of();
        }
        realmAccess = jwt.getClaim("realm_access");
        roles = (Collection<String>) realmAccess.get("roles");
        return roles.stream().map(role->new SimpleGrantedAuthority(role)).collect(Collectors.toSet());
    }

}
/*
JWT :
{
  "exp": 1705926580,
  "iat": 1705926280,
  "jti": "609044ad-81f9-47ee-88c3-bc3a0bf11c58",
  "iss": "http://localhost:8080/realms/sdia-realm",
  "aud": "account",
  "sub": "e01bd902-58a1-4519-85f2-381c53f68321",
  "typ": "Bearer",
  "azp": "sdia-customer-client",
  "session_state": "9cd4205e-ceb8-4a12-bb8b-24551bd4e1ef",
  "acr": "1",
  "allowed-origins": [
    "/*"
   ],
  "realm_access": {
    "roles": [
      "default-roles-sdia-realm",
      "offline_access",
      "uma_authorization",
      "USER"
    ]
  },
  "resource_access": {
    "account": {
      "roles": [
        "manage-account",
        "manage-account-links",
        "view-profile"
      ]
    }
  },
  "scope": "profile email",
  "sid": "9cd4205e-ceb8-4a12-bb8b-24551bd4e1ef",
  "email_verified": false,
  "name": "lionel mbetid",
  "preferred_username": "user1",
  "given_name": "lionel",
  "family_name": "mbetid",
  "email": "user1@gmail.com"
}
 */
