package org.example.expert.domain.auth.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import org.example.expert.config.JwtUtil;
import org.example.expert.config.PasswordEncoder;
import org.example.expert.domain.auth.dto.request.SigninRequest;
import org.example.expert.domain.auth.dto.request.SignupRequest;
import org.example.expert.domain.auth.dto.response.SigninResponse;
import org.example.expert.domain.auth.dto.response.SignupResponse;
import org.example.expert.domain.auth.exception.AuthException;
import org.example.expert.domain.common.exception.InvalidRequestException;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.enums.UserRole;
import org.example.expert.domain.user.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class AuthServiceTest {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private AuthService authService;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private JwtUtil jwtUtil;

	@BeforeEach
	public void clear() {
		userRepository.deleteAllInBatch();
	}

	private User createUser(String rawPassword) {
		String password = passwordEncoder.encode(rawPassword);
		User user = new User("test@test.com", password, UserRole.USER);
		return userRepository.save(user);
	}

	@Test
	@DisplayName("user는 올바른 이메일, 비밀번호를 입력할 시 회원가입이 성공하여 DB에 저장되고 정상적으로 토큰이 반환된다.")
	public void successEmailPasswordJoin() {
	    //given
		String email = "test@test.com";
		String password = "Test1234";
		String userRole = "USER";

		SignupRequest signupRequest = new SignupRequest(email, password, userRole);

	    //when
		SignupResponse signup = authService.signup(signupRequest);

		//then
		User user = userRepository.findByEmail(email).get();
		assertEquals(user.getEmail(), email);
		assertEquals(user.getUserRole().toString(), userRole);
		assertThat(passwordEncoder.matches(password, user.getPassword())).isTrue();
		assertNotNull(signup.getBearerToken());
	}

	@Test
	@DisplayName("user가 이미 존재하는 이메일로 회원가입을 시도할 시 예외가 발생한다.")
	public void overlapEmailException() {
	    //given
		String email = "test@test.com";
		String password = "Test1234";
		String userRole = "USER";

		userRepository.save(new User(email, password, UserRole.USER));
		SignupRequest signupRequest = new SignupRequest(email, password, userRole);

		//when && then
		InvalidRequestException ex = Assertions.assertThrows(InvalidRequestException.class,
			() -> authService.signup(signupRequest));
		assertThat(ex.getMessage()).isEqualTo("이미 존재하는 이메일입니다.");
	}

	@Test
	@DisplayName("존재하는 회원이 로그인 성공 시 토큰이 반환된다.")
	public void successSignin() {
		String password = "Test1234";

		//given
		User saveUser = createUser(password);

		SigninRequest signinRequest = new SigninRequest(saveUser.getEmail(), password);

		//when
		SigninResponse signin = authService.signin(signinRequest);

		//then
		User user = userRepository.findByEmail(saveUser.getEmail()).get();
		assertEquals(user.getId(), saveUser.getId());
		assertEquals(user.getEmail(), saveUser.getEmail());

		assertNotNull(signin.getBearerToken());
	}

	@Test
	@DisplayName("존재하지 않는 회원이 로그인 시 예외가 발생한다.")
	public void notExistUserSigninException() {
		String email = "xxx@xxx.com";
		String password = "Test1234";

		//given
		User saveUser = createUser(password);
		SigninRequest signinRequest = new SigninRequest(email, password);

		assertFalse(userRepository.findByEmail(email).isPresent());

		//when && then
		InvalidRequestException ex = assertThrows(InvalidRequestException.class,
			() -> authService.signin(signinRequest));
		assertEquals("가입되지 않은 유저입니다.", ex.getMessage());
	}

	@Test
	@DisplayName("로그인 시 이메일과 비밀번호가 일치하지 않을 경우 예외가 발생한다.")
	public void notMatchEmailPasswordException() {
		String email = "xxx@xxx.com";
		String password = "Test1234";

	    //given
		User saveUser = userRepository.save(new User(email, password, UserRole.USER));
		SigninRequest signinRequest = new SigninRequest(email, "1111");

		//when
		AuthException ex = assertThrows(AuthException.class,
			() -> authService.signin(signinRequest));

	    //then
		assertThat(ex).isInstanceOf(AuthException.class);
		assertEquals("잘못된 비밀번호입니다." ,ex.getMessage());
	}
}