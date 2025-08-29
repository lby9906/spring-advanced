package org.example.expert.domain.auth.controller;


import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.example.expert.domain.auth.dto.request.SigninRequest;
import org.example.expert.domain.auth.dto.request.SignupRequest;
import org.example.expert.domain.auth.dto.response.SigninResponse;
import org.example.expert.domain.auth.dto.response.SignupResponse;
import org.example.expert.domain.auth.service.AuthService;
import org.example.expert.domain.user.enums.UserRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private AuthService authService;

	@Autowired
	ObjectMapper objectMapper;

	@Test
	@DisplayName("회원가입 성공 시 토큰이 반환된다.")
	public void successJoinTokenResponse() throws Exception {
	    //given
		SignupRequest signupRequest = new SignupRequest("test@test.com", "1234", UserRole.USER.name());
		SignupResponse signupResponse = new SignupResponse("fake-token");

		given(authService.signup(any(SignupRequest.class))).willReturn(signupResponse);

		//when && then
		mockMvc.perform(post("/auth/signup")
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(signupRequest)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.bearerToken").value("fake-token"));
	}

	@Test
	@DisplayName("user가 정상적인 로그인 시 토큰이 반환된다.")
	public void successSignupTokenResponse() throws Exception {
	    //given
		SigninRequest signinRequest = new SigninRequest("test@test.com", "1234");
		SigninResponse signinResponse = new SigninResponse("fake-token");

		given(authService.signin(any(SigninRequest.class))).willReturn(signinResponse);

		//when && then
		mockMvc.perform(post("/auth/signin")
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(signinRequest)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.bearerToken").exists());
	}
}