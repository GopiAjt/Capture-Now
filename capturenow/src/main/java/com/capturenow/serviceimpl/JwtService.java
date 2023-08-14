package com.capturenow.serviceimpl;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;


@Component
public class JwtService {

	private static final String SECRET = "B6F38ED4EA45561006BA6E21F0187CC868C82226F42ADD9C4D077119A12850B7";

	public String ganarateToken(String username)
	{
		Map<String, Object> claims = new HashMap<>();
		return createToken(claims,username);
	}

	private String createToken(Map<String,Object> claims, String username)
	{
		return Jwts.builder()
				.setClaims(claims)
				.setSubject(username)
				.setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis()+1000*60*24))
				.signWith(getSignKey(), SignatureAlgorithm.HS256).compact();

	}

	private Key getSignKey() {
		byte[] key = Decoders.BASE64.decode(SECRET);
		return Keys.hmacShaKeyFor(key);
	}

	public String extractUsername(String token)
	{
		return extractClaim(token, Claims::getSubject);
	}

	public Date extractExpiration(String token)
	{
		return extractClaim(token, Claims::getExpiration);
	}

	public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
		final Claims claims = extractallClaims(token);
		return claimsResolver.apply(claims);
	}

	private Claims extractallClaims(String token) {
		return Jwts
				.parserBuilder()
				.setSigningKey(getSignKey())
				.build()
				.parseClaimsJws(token)
				.getBody();
	}

	private Boolean isTokenExpired(String token)
	{
		return extractExpiration(token).before(new Date());
	}

	public Boolean validatetoken(String token, UserDetails userDetails)
	{
		final String username = extractUsername(token);
		return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
	}
}



