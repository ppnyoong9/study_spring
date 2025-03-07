package com.cos.jwt.config.auth;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.cos.jwt.model.User;

import lombok.Data;

@Data
public class PrincipalDetails implements UserDetails {
	
	private User user;
	
	public PrincipalDetails(User user) {
		this.user = user;
	}

	
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		Collection<GrantedAuthority> authorities = new ArrayList<>();
		user.getRoleList().forEach(r->{
		});
		return authorities;
	}
	//r -> { }  입력 파라미터(순회하는 각 요소) -> 실행할 코드(로직) 각 권한을 순회하지만 아무 동작을 하지 않는 상태입니다.

	@Override
	public String getPassword() {
		return user.getPassword();
	}

	@Override
	public String getUsername() {
		return user.getUsername();
	}
	
	@Override
	public boolean isAccountNonExpired() {
		return true;
	}
	
	@Override
	public boolean isAccountNonLocked() {
		return true;
	}
	
	@Override
	public boolean isCredentialsNonExpired() {
		return true;
		
	}
	
	@Override
	public boolean isEnabled() {
		return true;
	}

	
}
