package com.cos.jwt.config.jwt;

public interface JwtProperties {
	String SECRET = "cos"; // 우리 서버만 알고 있는 비밀값
	int  EXPIRATION_TIME = 60000*10; // 10일 (1/1000초) 
	String TOKEN_PREFIX = "Bearer ";  // 공백 한 칸
	String HEADER_STRING = "Authorization";
}
