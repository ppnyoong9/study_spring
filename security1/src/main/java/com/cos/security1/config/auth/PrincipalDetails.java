package com.cos.security1.config.auth;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import com.cos.security1.model.User;

import lombok.Data;

// 시큐리티가 /login 주소 요청이 오면 낚아채서 로그인을 진행시킨다.
// 로그인 진행이 완료가 되면 시큐리티 session을 만들어준다. (Security ContextHolder)
// 오브젝트 => Authentication 타입 객체
// Authentication 안에 User 정보가 있어야 됨.
// User 오브젝트타입 => UserDetails 타입 객체

// Security Session => Authentication => UserDetails(PrincipalDetails)
// 즉, 시큐리티 세션이라는 세션 영역이 있는데 여기에 세션 정보를 저장해주는데 여기 들어 갈 수 있는 객체가 Authentication 로 정해져 있음 이 Authentication 이라는 객체 안에 유저 정보를 저장할 때 유저 정보는 UserDetails 타입이어야함  
// Security Session 있는 이 session 정보를 get 해서 꺼내면 Authentication 가 나오고 이 Authentication 에서 UserDetails 를 꺼내면 User 객체에 접근할 수 있음

@Data
public class PrincipalDetails implements UserDetails, OAuth2User{

	private User user; // 컴포지션
	private Map<String,Object> attributes;

	// 일반 로그인
	public PrincipalDetails(User user) {
		this.user = user;
	}
	
	// OAuth 로그인
	public PrincipalDetails(User user, Map<String,Object> attributes) {
		this.user = user;
		this.attributes=attributes;
	}

	// 해당 User의 권한을 리턴하는 곳!!
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		Collection<GrantedAuthority> collect = new ArrayList<>();
		collect.add(new GrantedAuthority() {

			@Override
			public String getAuthority() {
				return user.getRole();
			}
		});
		return collect;
	}

	@Override
	public String getPassword() {
		return user.getPassword();
	}

	@Override
	public String getUsername() {
		return user.getUsername();
	}
	
	// 계정이 만료되지 않았는지 확인
	@Override
	public boolean isAccountNonExpired() {
		return true; // 만료 안됐어용!
	}
	
	// 자격 증명 만료되지는 않았는지 확인
	@Override
	public boolean isCredentialsNonExpired() {
		return true; // 만료 안됐어용!
	}
	
	// 계정 활성화되어있는지 확인
	@Override
	public boolean isEnabled() {
		// 우리 사이트!! 1년 동안 회원이 로그인을 안하면!! 휴면 계정으로 하기로 함.
		// 현재시간 - 로그인 시간 => 1년을 초과하면 return false;
		return true; 
	}

	@Override
	public Map<String, Object> getAttributes() {
		return attributes;
	}

	@Override
	public String getName() {
		return null;
	}
	

}
