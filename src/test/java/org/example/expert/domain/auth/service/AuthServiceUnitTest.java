package org.example.expert.domain.auth.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class AuthServiceUnitTest {

	@Mock
	private UserRepository userRepository;

	@InjectMocks
	private AuthService authService;

	@Mock
	private PasswordEncoder passwordEncoder;

	@Mock
	private JwtUtil jwtUtil;

	@Test
	@DisplayName("user는 올바른 이메일, 비밀번호를 입력할 시 회원가입이 성공하여 DB에 저장되고 정상적으로 토큰이 반환된다.")
	public void successEmailPasswordJoin() {
		//given
		String email = "test@test.com";
		String password = "Test1234";
		String userRole = "USER";

		SignupRequest signupRequest = new SignupRequest(email, password, userRole);
		when(userRepository.existsByEmail(signupRequest.getEmail())).thenReturn(false);
		when(passwordEncoder.encode(signupRequest.getPassword())).thenReturn("encodedPassword");

		User saveUser = new User(signupRequest.getEmail(), "encodedPassword", UserRole.USER);
		when(userRepository.save(any(User.class))).thenReturn(saveUser);
		when(jwtUtil.createToken(saveUser.getId(), saveUser.getEmail(), saveUser.getUserRole())).thenReturn("fakeToken");

		//when
		SignupResponse signup = authService.signup(signupRequest);

		//then
		assertNotNull(signup.getBearerToken());
		assertEquals("fakeToken", signup.getBearerToken());
		verify(userRepository).existsByEmail(signupRequest.getEmail());
	}

	@Test
	@DisplayName("user가 이미 존재하는 이메일로 회원가입을 시도할 시 예외가 발생한다.")
	public void overlapEmailException() {
		//given
		String email = "test@test.com";
		String password = "Test1234";
		String userRole = "USER";

		SignupRequest signupRequest = new SignupRequest(email, password, userRole);
		when(userRepository.existsByEmail(signupRequest.getEmail())).thenReturn(true);

		//when && then
		assertThrows(InvalidRequestException.class,
			() -> authService.signup(signupRequest));
		verify(userRepository, never()).save(any(User.class));
	}

	@Test
	@DisplayName("존재하는 회원이 로그인 성공 시 토큰이 반환된다.")
	public void successSignin() {
		String password = "Test1234";
		String encodedPassword = "encodedPassword";

		//given
		User saveUser = new User("test@test.com", encodedPassword, UserRole.USER);
		when(userRepository.findByEmail(saveUser.getEmail())).thenReturn(Optional.of(saveUser));
		when(passwordEncoder.matches(password, encodedPassword)).thenReturn(true);
		when(jwtUtil.createToken(any(), eq(saveUser.getEmail()), eq(saveUser.getUserRole()))).thenReturn("fakeToken");

		SigninRequest signinRequest = new SigninRequest(saveUser.getEmail(), password);

		//when
		SigninResponse signin = authService.signin(signinRequest);

		//then
		assertThat(signin.getBearerToken()).isEqualTo("fakeToken");
	}

	@Test
	@DisplayName("존재하지 않는 회원이 로그인 시 예외가 발생한다.")
	public void notExistUserSigninException() {
		String email = "xxx@xxx.com";
		String password = "Test1234";

		//given
		SigninRequest signinRequest = new SigninRequest(email, password);
		when(userRepository.findByEmail(signinRequest.getEmail())).thenReturn(Optional.empty());

		//when && then
		assertThrows(InvalidRequestException.class,
			() -> authService.signin(signinRequest));
	}

	@Test
	@DisplayName("로그인 시 이메일과 비밀번호가 일치하지 않을 경우 예외가 발생한다.")
	public void notMatchEmailPasswordException() {
		String password = "Test1234";
		String encodedPassword = "encodedPassword";

		//given
		User saveUser = new User("test@test.com", encodedPassword, UserRole.USER);
		when(userRepository.findByEmail(saveUser.getEmail())).thenReturn(Optional.of(saveUser));
		when(passwordEncoder.matches(password, encodedPassword)).thenReturn(false);

		SigninRequest signinRequest = new SigninRequest(saveUser.getEmail(), password);

		//when && then
		assertThrows(AuthException.class, () -> authService.signin(signinRequest));
	}
}
