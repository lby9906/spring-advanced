package org.example.expert.domain.todo.controller;

import static java.time.LocalDateTime.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.List;

import org.example.expert.config.AuthUserArgumentResolver;
import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.todo.dto.request.TodoSaveRequest;
import org.example.expert.domain.todo.dto.response.TodoResponse;
import org.example.expert.domain.todo.dto.response.TodoSaveResponse;
import org.example.expert.domain.todo.service.TodoService;
import org.example.expert.domain.user.dto.response.UserResponse;
import org.example.expert.domain.user.enums.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(TodoController.class)
@AutoConfigureMockMvc
class TodoControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private TodoService todoService;

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
	@DisplayName("존재하는 회원이 일정 등록 시 성공적으로 일정 정보가 반환된다.")
	public void successSavedTodoInfoResponse() throws Exception {
	    //given
		TodoSaveRequest todoSaveRequest = new TodoSaveRequest("제목", "내용");

		UserResponse userResponse = new UserResponse(1L, "test@test.com");

		TodoSaveResponse todoSaveResponse = new TodoSaveResponse(1L, todoSaveRequest.getTitle(),
			todoSaveRequest.getContents(),
			"Sunny", userResponse);

		given(todoService.saveTodo(any(AuthUser.class), any(TodoSaveRequest.class))).willReturn(todoSaveResponse);

		//when && then
		mockMvc.perform(post("/todos")
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(todoSaveRequest))
			.header("Authorization", "Bearer fake-token"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.id").value(todoSaveResponse.getId()))
			.andExpect(jsonPath("$.title").value(todoSaveResponse.getTitle()))
			.andExpect(jsonPath("$.contents").value(todoSaveResponse.getContents()))
			.andExpect(jsonPath("$.weather").value(todoSaveResponse.getWeather()))
			.andExpect(jsonPath("$.user.id").value(todoSaveResponse.getUser().getId()))
			.andExpect(jsonPath("$.user.email").value(todoSaveResponse.getUser().getEmail()));
	}

	@Test
	@DisplayName("존재하는 회원이 등록한 Todo 목록을 정상적으로 반환한다.")
	public void successFindAllUserTodo() throws Exception {
	    //given
		int page = 1;
		int size = 2;

		UserResponse userResponse = new UserResponse(1L, "test@test.com");

		TodoResponse todoResponse1 = new TodoResponse(1L, "제목1", "내용1", "Sunny", userResponse, now(), now());
		TodoResponse todoResponse2 = new TodoResponse(2L, "제목2", "내용2", "Sunny", userResponse, now(), now());

		List<TodoResponse> content = List.of(todoResponse1, todoResponse2);
		PageImpl<TodoResponse> todoPage = new PageImpl<>(content, PageRequest.of(page, size), content.size());

		given(todoService.getTodos(page, size)).willReturn(todoPage);

		//when && then
		mockMvc.perform(get("/todos")
			.param("page", String.valueOf(page))
			.param("size", String.valueOf(size))
			.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.content[0].id").value(todoResponse1.getId()))
			.andExpect(jsonPath("$.content[0].title").value(todoResponse1.getTitle()))
			.andExpect(jsonPath("$.content[0].contents").value(todoResponse1.getContents()))
			.andExpect(jsonPath("$.content[0].weather").value(todoResponse1.getWeather()))
			.andExpect(jsonPath("$.content[0].user.id").value(todoResponse1.getUser().getId()))
			.andExpect(jsonPath("$.content[0].user.email").value(todoResponse1.getUser().getEmail()));
	}

	@Test
	@DisplayName("존재하는 회원이 등록한 Todo를 단건 조회할 수 있다.")
	public void successFindUserTodo() throws Exception {
	    //given

		long todoId = 1L;

		UserResponse userResponse = new UserResponse(1L, "test@test.com");
		TodoResponse todoResponse = new TodoResponse(1L, "제목1", "내용1", "Sunny", userResponse, now(), now());

		given(todoService.getTodo(todoResponse.getId())).willReturn(todoResponse);

	    //when && then
		mockMvc.perform(get("/todos/{todoId}", todoId)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.id").value(todoResponse.getId()))
			.andExpect(jsonPath("$.title").value(todoResponse.getTitle()))
			.andExpect(jsonPath("$.contents").value(todoResponse.getContents()))
			.andExpect(jsonPath("$.weather").value(todoResponse.getWeather()))
			.andExpect(jsonPath("$.user.id").value(todoResponse.getUser().getId()))
			.andExpect(jsonPath("$.user.email").value(todoResponse.getUser().getEmail()));
	}
}