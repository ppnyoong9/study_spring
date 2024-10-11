package com.cos.jwt.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.context.SecurityContextHolderFilter;
import org.springframework.security.web.context.SecurityContextPersistenceFilter;
import org.springframework.web.filter.CorsFilter;

import com.cos.jwt.config.jwt.JwtAuthenticationFilter;
import com.cos.jwt.config.jwt.JwtAuthorizationFilter;
import com.cos.jwt.filter.MyFilter3;
import com.cos.jwt.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity // 시큐리티 활성화
@RequiredArgsConstructor
public class SecurityConfig {

	private final CorsFilter corsFilter;
	private final UserRepository userRepository;

	// (1번방식) AuthenticationManager를 빈으로 주입받음
	private final AuthenticationConfiguration authenticationConfiguration;

	// (1번방식) AuthenticationManager를 주입받는 메서드
	@Bean
	public AuthenticationManager authenticationManager() throws Exception {
		return authenticationConfiguration.getAuthenticationManager();
	}

	@Bean
	SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

		// (1번방식) AuthenticationManager를 주입받음
		AuthenticationManager authenticationManager = authenticationManager(); // (1번방식) 좀더 복잡하지만 유지보수와 테스트 측면에서 유리
		// AuthenticationManager authenticationManager = http.getSharedObject(AuthenticationManager.class); (2번방식) 간단하고 빠르게 설정하고 싶을때

		http.csrf(AbstractHttpConfigurer::disable);
		http.sessionManagement(sc -> sc.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // 세션을 사용하지 않음
				.addFilter(corsFilter) // @CrossOrigin= (인증 X), 시큐리티 필터에 등록 = 인증(O)
				// http.addFilterBefore(new MyFilter3(), SecurityContextHolderFilter.class);
				// //시큐리티 필터 이전에 필터 실행하기 테스트용
				.addFilter(new JwtAuthenticationFilter(authenticationManager)) // AuthenticationManager 파라미터 꼭 던져줘야 함 이걸 통해 로그인해야하기 때문에
				.addFilter(new JwtAuthorizationFilter(authenticationManager,userRepository))
				.formLogin(form -> form.disable()).httpBasic(basic -> basic.disable())
				.authorizeHttpRequests(authorize -> authorize.requestMatchers("/api/v1/user/**").authenticated()
						.requestMatchers("/api/v1/manager/**").hasAnyRole("MANAGER", "ADMIN")
						.requestMatchers("/api/v1/admin/**").hasRole("ADMIN").anyRequest().permitAll());

		return http.build();
	}
}
