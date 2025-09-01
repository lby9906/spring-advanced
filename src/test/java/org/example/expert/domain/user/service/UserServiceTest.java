package org.example.expert.domain.user.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.Optional;

import org.example.expert.config.PasswordEncoder;
import org.example.expert.domain.common.exception.InvalidRequestException;
import org.example.expert.domain.user.dto.request.UserChangePasswordRequest;
import org.example.expert.domain.user.dto.response.UserResponse;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.enums.UserRole;
import org.example.expert.domain.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

	@Mock
	private UserRepository userRepository;

	@Mock
	private PasswordEncoder passwordEncoder;

	@InjectMocks
	private UserService userService;

	@Test
	@DisplayName("user를 id로 조회할 수 있다.")
	public void findUserId() {
	    //given
		Long userId = 1L;
		User user = new User("test@test.com", "1234", UserRole.USER);
		ReflectionTestUtils.setField(user, "id", userId);

		given(userRepository.findById(anyLong())).willReturn(Optional.of(user));

		//when
		UserResponse userResponse = userService.getUser(user.getId());

		//then
		assertNotNull(userResponse);
		assertEquals(userResponse.getId(), userId);
		assertEquals(userResponse.getEmail(), user.getEmail());
	}

	@Test
	@DisplayName("user id를 못찾을 시 예외가 발생한다.")
	public void notFoundUserIdException() {
	    //given
		Long userId = -1L;
		User user = new User("test@test.com", "1234", UserRole.USER);
		ReflectionTestUtils.setField(user, "id", userId);

		given(userRepository.findById(anyLong())).willReturn(Optional.empty());

	    //when && then
		assertThrows(InvalidRequestException.class,
			() -> userService.getUser(userId));
	}

	@Test
	@DisplayName("user는 올바른 조건으로 비밀번호를 입력할 시 비밀번호 변경이 성공적으로 된다.")
	public void successUserPasswordChange() {
	    //given
		Long userId = 1L;
		String oldPassword = "1234";
		String newPassword = "Test1234";
		User user = new User("test@test.com", passwordEncoder.encode(oldPassword), UserRole.USER);
		ReflectionTestUtils.setField(user, "id", userId);

		UserChangePasswordRequest userChangePasswordRequest = new UserChangePasswordRequest(oldPassword,
			newPassword);

		given(userRepository.findById(userId)).willReturn(Optional.of(user));
		given(passwordEncoder.matches(any(), any())).willReturn(true);
		given(passwordEncoder.matches(eq(newPassword), any())).willReturn(false);
		given(passwordEncoder.encode(any())).willAnswer(invocation -> invocation.getArgument(0));

		//when
		userService.changePassword(user.getId(), userChangePasswordRequest);

		// then
		assertTrue(passwordEncoder.matches(oldPassword, user.getPassword()));
	}

	@Test
	@DisplayName("user는 비밀번호 변경 시 새로운 비밀번호가 기존 비밀번호와 같으면 예외가 발생한다.")
	public void oldPasswordNewPasswordEqualsException() {
	    //given
		Long userId = 1L;
		String oldPassword = "Test1234";
		String newPassword = "Test1234";
		User user = new User("test@test.com", passwordEncoder.encode(oldPassword), UserRole.USER);
		ReflectionTestUtils.setField(user, "id", userId);

		UserChangePasswordRequest userChangePasswordRequest = new UserChangePasswordRequest(oldPassword,
			newPassword);

		given(userRepository.findById(userId)).willReturn(Optional.of(user));
		given(passwordEncoder.matches(eq(newPassword), any())).willReturn(true);

		//when && then
		InvalidRequestException exception = assertThrows(InvalidRequestException.class,
			() -> userService.changePassword(user.getId(), userChangePasswordRequest));

		assertEquals("새 비밀번호는 기존 비밀번호와 같을 수 없습니다.", exception.getMessage());
	}

	@Test
	@DisplayName("비밀번호 변경시 입력받은 user의 기존 비밀번호와 등록되어있는 비밀번호가 일치하지 않는 경우 예외가 발생한다.")
	public void notMatchPasswordException() {
	    //given
		Long userId = 1L;
		String oldPassword = "1234";
		String newPassword = "Test1234";
		User user = new User("test@test.com", oldPassword, UserRole.USER);
		ReflectionTestUtils.setField(user, "id", userId);

		UserChangePasswordRequest userChangePasswordRequest = new UserChangePasswordRequest(newPassword,
			newPassword);

	    //when
		given(userRepository.findById(userId)).willReturn(Optional.of(user));
		given(passwordEncoder.matches(userChangePasswordRequest.getOldPassword(), user.getPassword())).willReturn(false);

	    //then
		InvalidRequestException exception = assertThrows(InvalidRequestException.class,
			() -> userService.changePassword(user.getId(), userChangePasswordRequest));

		assertEquals("잘못된 비밀번호입니다.", exception.getMessage());
	}
}