package com.scity.user.security.jwt;

//import com.scity.user.security.service.UserDetailsImpl;
import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.UUID;

@Component
public class JwtUtils {

  private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

  @Value("${stms.app.jwtSecret}")
  private String jwtSecret;

  @Value("${stms.app.jwtExpirationMs}")
  private long jwtExpirationMs;

  @Value("${stms.app.jwtRefreshExpirationMs}")
  private long jwtExpirationMsRefresh;

//  public String generateJwtToken(UserDetailsImpl userPrincipal) {
//    return generateTokenFromUsername(userPrincipal.getEmail(), userPrincipal.getId(), userPrincipal.getTenant(), userPrincipal.getRole());
//  }

  public String generateTokenFromUsername(String username, UUID userid, String tenant, String roles, String permissions, String locationId, String permissionGroupId) {
    Date date = new Date();
    return Jwts.builder()
        .setSubject(username)
        .setIssuedAt(date)
        .claim("user_id", userid)
        .claim("tenant", tenant)
        .claim("roles", roles)
        .claim("permissions", permissions)
        .claim("location", locationId)
        .claim("username", username)
        .claim("permission_group", permissionGroupId)
        .setExpiration(new Date(date.getTime() + jwtExpirationMs))
        .signWith(SignatureAlgorithm.HS512, jwtSecret)
        .compact();
  }

  public String generateRefreshTokenFromUsername(String username, UUID userid, Date date) {
    return Jwts.builder()
            .setSubject(username)
            .setIssuedAt(new Date())
            .claim("user_id", userid)
            .setExpiration(date)
            .signWith(SignatureAlgorithm.HS512, jwtSecret)
            .compact();
  }

  public String generateRefreshTokenFromUsername(String username, UUID userid) {
    Date date = new Date();
    return Jwts.builder()
            .setSubject(username)
            .setIssuedAt(date)
            .claim("user_id", userid)
            .setExpiration(new Date(date.getTime() + jwtExpirationMsRefresh))
            .signWith(SignatureAlgorithm.HS512, jwtSecret)
            .compact();
  }

  public String getUsernameFromJwtToken(String token) {
    return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody().getSubject();
  }

  public String generateTokenFromUsernameVerify(String username, UUID userId) {
    return Jwts.builder()
        .setSubject(username)
        .setIssuedAt(new Date())
            .claim("user_id", userId)
        .setExpiration(new Date((new Date()).getTime() + 100000))
        .signWith(SignatureAlgorithm.HS512, jwtSecret)
        .compact();
  }

  public String getUserNameFromJwtToken(String token) {
    return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody().getSubject();
  }

  public boolean validateJwtToken(String authToken) {
    try {
      Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
      return true;
    } catch (SignatureException e) {
      logger.error("Invalid JWT signature: {}", e.getMessage());
    } catch (MalformedJwtException e) {
      logger.error("Invalid JWT token: {}", e.getMessage());
    } catch (ExpiredJwtException e) {
      logger.error("JWT token is expired: {}", e.getMessage());
    } catch (UnsupportedJwtException e) {
      logger.error("JWT token is unsupported: {}", e.getMessage());
    } catch (IllegalArgumentException e) {
      logger.error("JWT claims string is empty: {}", e.getMessage());
    }

    return false;
  }

  public Date getTokenExpiryFromJWT(String token) {
    Claims claims = Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody();

    return claims.getExpiration();
  }
}
