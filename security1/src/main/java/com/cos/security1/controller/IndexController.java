package com.cos.security1.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cos.security1.config.auth.PrincipalDetails;
import com.cos.security1.model.User;
import com.cos.security1.repository.UserRepository;

@Controller // view 를 리턴하겠다!
public class IndexController {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	@GetMapping("/test/login") // 일반로그인
	public @ResponseBody String testLogin(Authentication authentication, @AuthenticationPrincipal PrincipalDetails userDetails) { // DI(의존성 주입)
		// Authentication, DI(의존성주입), PrincipalDetails 타입(=UserDetails)
        // @AuthenticationPrincipal : security 세션정보 접근 가능한 annotation.
		System.out.println("/test/login ======================");
		// User 정보 찾는 방법 1 : authentication을 casting 하는 과정을 거쳐서 찾기
		PrincipalDetails principalDetails = (PrincipalDetails)authentication.getPrincipal();
		System.out.println("authentication: " + principalDetails.getUser());
		// User 정보 찾는 방법 2 : @AuthenticationPrincipal이라는 어노테이션을 통해서 찾기
		System.out.println("userDetails: " + userDetails.getUser());
		return "세션 정보 확인하기";
	}
	@GetMapping("/test/oauth/login") // Oauth2 로그인
	public @ResponseBody String testOAuthLogin(Authentication authentication, @AuthenticationPrincipal OAuth2User oauth) {
		System.out.println("/test/oauth/login ======================");
		OAuth2User oauth2User= (OAuth2User)authentication.getPrincipal();
		System.out.println("authentication: " + oauth2User.getAttributes());
		System.out.println("oauth2User: " + oauth.getAttributes());
		return "OAuth 세션 정보 확인하기";
	}

	// localhost:8080/
	// localhost:8080
	@GetMapping({ "", "/" })
	public String index() {
		// 머스테치 기본폴더 src/main/resources/
		// ViewResolver 설정: templates(prefix), .mustache (suffix) 생략가능!!
		return "index"; // src/main/resources/templates/index.mustache
	}

	// OAuth 로그인을 해도 PrincipalDetails
	// 일반 로그인을 해도 PrincipalDetails
	@GetMapping("/user")
	public @ResponseBody String user(@AuthenticationPrincipal PrincipalDetails principalDetails) {
		System.out.println("principalDetails: " + principalDetails.getUser());
		return "user";
	}

	@GetMapping("/admin")
	public @ResponseBody String admin() {
		return "admin";
	}

	@GetMapping("/manager")
	public @ResponseBody String manager() {
		return "manager";
	}

	// 스프링 시큐리티가 해당주소를 낚아채버림! -> SecurityConfig 파일 생성후 작동안함.
	@GetMapping("/loginForm")
	public String loginForm() {
		return "loginForm";
	}

	@GetMapping("/joinForm")
	public String joinForm() {
		return "joinForm";
	}

	@PostMapping("/join")
	public String join(User user) {
		user.setRole("ROLE_USER");
		String rawPassword = user.getPassword();
		String encPassword = bCryptPasswordEncoder.encode(rawPassword);
		user.setPassword(encPassword);
		userRepository.save(user); // 회원가입 잘됨. 비밀번호: 1234 => 시큐리티로 로그인을 할 수 없음. 이유는 패스워드가 암호화가 안되었기 때문
		return "redirect:/loginForm";
	}

	@Secured("ROLE_ADMIN") // 특정 메소드에 간단하게 secure 걸 수 있음 // secure 하나 걸고 싶을때 사용
	@GetMapping("/info")
	public @ResponseBody String info() {
		return "개인정보";
	}

	@PreAuthorize("hasRole('ROLE_MANAGER') or hasRole('ROLE_ADMIN')") // 메소드가 실행되기 직전에 실행 // 권한 여러개 걸고 싶을때 사용
	@GetMapping("/data")
	public @ResponseBody String data() {
		return "데이터정보";
	}

}
