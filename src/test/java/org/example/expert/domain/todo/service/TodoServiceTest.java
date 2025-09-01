package org.example.expert.domain.todo.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.List;
import java.util.Optional;

import org.example.expert.client.WeatherClient;
import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.todo.dto.request.TodoSaveRequest;
import org.example.expert.domain.todo.dto.response.TodoResponse;
import org.example.expert.domain.todo.dto.response.TodoSaveResponse;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.todo.repository.TodoRepository;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.enums.UserRole;
import org.example.expert.domain.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class TodoServiceTest {

	@Mock
	private UserRepository userRepository;

	@Mock
	private TodoRepository todoRepository;

	@Mock
	private WeatherClient weatherClient;

	@InjectMocks
	private TodoService todoService;

	@Test
	@DisplayName("존재하는 회원이 제목과 내용 기반의 일정을 등록하면 정상적으로 등록된 일정 정보가 반환된다.")
	public void successSaveTodoResponse() {
	    //given
		AuthUser authUser = new AuthUser(1L, "test@test.com", UserRole.USER);
		User user = new User("test@test.com", "1234", UserRole.USER);
		TodoSaveRequest todoSaveRequest = new TodoSaveRequest("제목", "내용");

		Todo savedTodo = new Todo("제목", "내용", "Sunny", user);
		given(todoRepository.save(any(Todo.class))).willReturn(savedTodo);
		given(weatherClient.getTodayWeather()).willReturn("Sunny");

		//when
		TodoSaveResponse todoSaveResponse = todoService.saveTodo(authUser, todoSaveRequest);

		//then
		assertNotNull(todoSaveResponse);
		assertEquals( "제목", todoSaveResponse.getTitle());
		assertEquals("내용", todoSaveResponse.getContents());
		assertEquals("Sunny", todoSaveResponse.getWeather());
		assertEquals(todoSaveResponse.getId(), savedTodo.getId());
	}

	@Test
	@DisplayName("해당하는 user가 등록된 Todo의 목록을 전체 조회할 수 있다.")
	public void successUserTodoFindAll() {
	    //given
		User user = new User("test@test.com", "1234", UserRole.USER);

		Todo savedTodo1 = new Todo("제목1", "내용1", "Sunny", user);
		Todo savedTodo2 = new Todo("제목2", "내용2", "Sunny", user);

		given(todoRepository.findAllByOrderByModifiedAtDesc(any(Pageable.class)))
			.willReturn(new PageImpl<>(List.of(savedTodo1, savedTodo2)));

		PageRequest pageRequest = PageRequest.of(1, 10);

		//when
		Page<TodoResponse> result = todoService.getTodos(pageRequest.getPageNumber(), pageRequest.getPageSize());

		//then
		assertThat(result).hasSize(2);
		assertEquals(result.getContent().get(0).getTitle(), "제목1");
		assertEquals(result.getContent().get(0).getContents(), "내용1");
		assertEquals(result.getContent().get(0).getTitle(), "제목1");
	}

	@Test
	@DisplayName("해당하는 user가 등록한 Todo를 단건 조회할 수 있다.")
	public void successFindTodoUser() {
	    //given
		Long userId = 1L;
		User user = new User("test@test.com", "1234", UserRole.USER);
		ReflectionTestUtils.setField(user, "id", userId);

		Long todoId = 1L;
		Todo savedTodo = new Todo("제목1", "내용1", "Sunny", user);
		ReflectionTestUtils.setField(savedTodo, "id", todoId);

		given(todoRepository.findByIdWithUser(savedTodo.getId()))
			.willReturn(Optional.of(savedTodo));

	    //when
		TodoResponse todo = todoService.getTodo(savedTodo.getId());

		//then
		assertNotNull(todo);
		assertNotNull(todo.getUser().getId());
		assertEquals(todo.getId(), savedTodo.getId());
		assertEquals(todo.getTitle(), savedTodo.getTitle());
		assertEquals(todo.getContents(), savedTodo.getContents());
		assertEquals(todo.getWeather(), savedTodo.getWeather());
	}
}