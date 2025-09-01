package org.example.expert.domain.user.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.Optional;

import org.example.expert.config.PasswordEncoder;
import org.example.expert.domain.common.exception.InvalidRequestException;
import org.example.expert.domain.user.dto.request.UserRoleChangeRequest;
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
class UserAdminServiceTest {
	@Mock
	private UserRepository userRepository;

	@Mock
	private PasswordEncoder passwordEncoder;

	@InjectMocks
	private UserAdminService userAdminService;

	@Test
	@DisplayName("admin이 userRole를 올바르게 입력할 시 정상적으로 변경된다.")
	public void successAdminChangeUserRole() {
	    //given
		Long adminId = 1L;
		User admin = new User("admin@admin.com", "1234", UserRole.ADMIN);
		ReflectionTestUtils.setField(admin, "id", adminId);

		Long userId = 2L;
		User user = new User("user@user.com", "1111", UserRole.USER);
		ReflectionTestUtils.setField(user, "id", userId);

		UserRoleChangeRequest changeAdmin = new UserRoleChangeRequest("ADMIN");

		given(userRepository.findById(userId)).willReturn(Optional.of(user));

		//when
		userAdminService.changeUserRole(user.getId(), changeAdmin);

	    //then
		assertEquals(UserRole.ADMIN, user.getUserRole());
	}

	@Test
	@DisplayName("admin이 userRole을 수정할 시 userId를 찾을 수 없다면 예외가 발생한다.")
	public void notFoundAdminChangeUserRoleException() {
	    //given
		Long adminId = 1L;
		User admin = new User("admin@admin.com", "1234", UserRole.ADMIN);
		ReflectionTestUtils.setField(admin, "id", adminId);

		Long userId = -1L;
		User user = new User("user@user.com", "1111", UserRole.USER);
		ReflectionTestUtils.setField(user, "id", userId);

		UserRoleChangeRequest changeAdmin = new UserRoleChangeRequest("ADMIN");

		given(userRepository.findById(userId)).willReturn(Optional.empty());

		//when && then
		InvalidRequestException exception = assertThrows(InvalidRequestException.class,
			() -> userAdminService.changeUserRole(userId, changeAdmin));

		assertEquals("User not found", exception.getMessage());
	}
}