package org.example.expert.domain.user.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.example.expert.config.AuthUserArgumentResolver;
import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.user.dto.request.UserChangePasswordRequest;
import org.example.expert.domain.user.dto.response.UserResponse;
import org.example.expert.domain.user.enums.UserRole;
import org.example.expert.domain.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc
class UserControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private UserService userService;

	@Autowired
	ObjectMapper objectMapper;

	@MockBean
	private AuthUserArgumentResolver authArgumentResolver;

	@BeforeEach
	void setUp() {
		given(authArgumentResolver.supportsParameter(any())).willReturn(true);
		given(authArgumentResolver.resolveArgument(any(), any(), any(), any()))
			.willReturn(new AuthUser(1L, "test@test.com", UserRole.USER));
	}

	@Test
	@DisplayName("회원 정보를 성공적으로 반환한다.")
	public void successGetUserInfo() throws Exception {
	    //given
		long userId = 1L;

		UserResponse userResponse = new UserResponse(userId, "test@test.com");

		given(userService.getUser(userId)).willReturn(userResponse);

		//when && then
		mockMvc.perform(get("/users/{userId}", userId)
			.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.id").value(userResponse.getId()))
			.andExpect(jsonPath("$.email").value(userResponse.getEmail()));
	}

	@Test
	@DisplayName("user는 비밀번호 변경을 성공적으로 변경한다.")
	public void successChangeUserPassword() throws Exception {
	    //given
		long userId = 1L;

		UserChangePasswordRequest request = new UserChangePasswordRequest("1234", "Test1234");

		doNothing().when(userService).changePassword(eq(userId), any(UserChangePasswordRequest.class));

	    //when && then
		mockMvc.perform(put("/users")
			.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isOk());
	}
}