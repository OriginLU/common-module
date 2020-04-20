package com.zeroone.console.security.utils;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import org.springframework.util.Base64Utils;

import javax.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.List;

/**
 * @author Charles
 * @since 2020-02-01
 */
@Slf4j
@Component
public class JwtTokenHelper {


    private static final String AUTHORITIES_KEY = "auth";


    private static final String SECRET_KEY = "SecretKey012345678901234567899630214785012365478901478523699876543210";

    /**
     * Token validity time(ms)
     */
    private long tokenValidityInMilliseconds;

    /**
     * secret key
     */
    private Key key;


    @PostConstruct
    private void init(){

        this.tokenValidityInMilliseconds = 1000 * 60 * 30L;
        this.key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));
    }


    /**
     * generate token
     */
    public String generateToken(Authentication authentication){

        Date validTime = new Date(System.currentTimeMillis() + tokenValidityInMilliseconds);

        return Jwts.builder()
                .setSubject(authentication.getName())
                .claim(AUTHORITIES_KEY,"")
                .setExpiration(validTime)
                .signWith(key)
                .compact();

    }

    public static void main(String[] args) {

        JwtTokenHelper jwtTokenHelper = new JwtTokenHelper();

        jwtTokenHelper.init();

        String test = jwtTokenHelper.generateToken(new UsernamePasswordAuthenticationToken("test", "1231456"));
        System.out.println(test);

        System.out.println(new String(Base64Utils.decodeFromString("eyJhbGciOiJIUzUxMiJ9")));
        System.out.println(new String(Base64Utils.decodeFromString("eyJzdWIiOiJ0ZXN0IiwiYXV0aCI6IiIsImV4cCI6MTU4MDY5NjczMX0")));
    }


    /**
     *
     * get auth info
     */
    public Authentication getAuthentication(String token){

        Claims claims = Jwts.parser().setSigningKey(key).parseClaimsJws(token).getBody();
        List<GrantedAuthority> authorities = AuthorityUtils.commaSeparatedStringToAuthorityList((String) claims.get(AUTHORITIES_KEY));

        User user = new User(claims.getSubject(), "", authorities);

        return new UsernamePasswordAuthenticationToken(user,"",authorities);

    }


    /**
     * valid token
     */
    public boolean validToken(String token){

        try {
            Jwts.parser().setSigningKey(key).parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            log.debug("Expired Jwt",e);
        } catch (UnsupportedJwtException e) {
            log.debug("Unsupported Jwt token",e);
        } catch (MalformedJwtException e) {
            log.debug("Malformed Jwt",e);
        } catch (SignatureException e) {
            log.debug("invalid token",e);
        } catch (IllegalArgumentException e) {
            log.debug("illegal argument Jwt",e);
        }
        return false;
    }

}
