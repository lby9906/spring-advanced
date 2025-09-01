package org.example.expert.domain.user.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.example.expert.config.AuthUserArgumentResolver;
import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.user.dto.request.UserRoleChangeRequest;
import org.example.expert.domain.user.enums.UserRole;
import org.example.expert.domain.user.service.UserAdminService;
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

@WebMvcTest(UserAdminController.class)
@AutoConfigureMockMvc
class UserAdminControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private UserAdminService userAdminService;

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
	@DisplayName("admin은 user의 role를 성공적으로 변경한다.")
	public void successAdminChangeUserRole() throws Exception{
	    //given
		long userId = 1L;

		UserRoleChangeRequest request = new UserRoleChangeRequest("ADMIN");

		doNothing().when(userAdminService).changeUserRole(eq(userId), any(UserRoleChangeRequest.class));

		//when && then
		mockMvc.perform(patch("/admin/users/{userId}", userId)
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isOk());
	}
}