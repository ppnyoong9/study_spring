package com.cos.security1.config;

import org.springframework.beans.factory.annotation.Autowired;

/*
 인증 단계

1. 코드 받기 (인증완료)
2. 이 코드를 통해 엑세스 토큰 받기  (사용자 정보에 접근할 수 있는 권한 생김)
3. 사용자 프로필 정보를 가져옴
4. 그 정보틀 토대로 회원가입을 자동으로 진행시키기도 함 
(+) 그 정보에서 추가적인 정보(구성)이 필요하면 추가적인 회원 가입 창 필요
 */

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.web.SecurityFilterChain;

import com.cos.security1.config.oauth.PrincipalOauth2UserService;

@Configuration
@EnableWebSecurity // 내가 등록할 스프링 시큐리티 필터가 스프링 필터체인에 등록이 된다.
@EnableMethodSecurity(securedEnabled = true,prePostEnabled = true) // secure 어노테이션 활성화, preAuthorize(메소드 실행 전), postAuthorize(실행 후) 어노테이션 활성화
public class SecurityConfig {

	@Autowired
	private PrincipalOauth2UserService principalOauth2UserService;
	
	
	
	/*
	// 해당 메서드의 리턴되는 오브젝트를 IoC로 등록해준다  // 순환참조 오류로 주석처리
	@Bean
	 BCryptPasswordEncoder encodePwd() {
		return new BCryptPasswordEncoder();
	}
	*/
	
	@Bean
	SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.csrf(CsrfConfigurer::disable);
		http.authorizeHttpRequests(authorize -> authorize.requestMatchers("/user/**").authenticated() // user라는 URL로 들어오면 인증 필요
				.requestMatchers("/manager/**").hasAnyRole("ADMIN", "MANAGER") // manager로 들어오는 URL은 ROLE이 ADMIN 또는 MANAGER 이여야함
				.requestMatchers("/admin/**").hasAnyRole("ADMIN") // admin으로 들어오는 URL은 ROLE이 ADMIN 이여야함
				.anyRequest().permitAll()); // 나머지 URL은 전부 권한 허용
		http.formLogin(form -> form.loginPage("/loginForm").loginProcessingUrl("/login").defaultSuccessUrl("/")); 
		// 사용자가 인증되지 않은 상태에서 보호된 리소스에 접근하려고 할 때 리다이렉트될 로그인 페이지 지정
		// login 주소가 호출되면 시큐리티가 낚아채서 대신 로그인을 진행해줌
		// .usernameParameter("username2") 여기서 userName 변수 이름 지정해줄 수 있음
		http.oauth2Login(authorize -> authorize.loginPage("/loginForm").userInfoEndpoint(c->c.userService(principalOauth2UserService)));
		// 로그인 성공 후 사용자 정보를 가져오는 엔드포인트를 설정 OAuth2UserService를 통해 사용자 정보를 처리할 수 있음
		return http.build();
	}
}
