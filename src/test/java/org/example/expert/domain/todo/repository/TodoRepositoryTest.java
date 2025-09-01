package org.example.expert.domain.todo.repository;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;

import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.enums.UserRole;
import org.example.expert.domain.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

@DataJpaTest
class TodoRepositoryTest {

	@Autowired
	private TodoRepository todoRepository;

	@Autowired
	private UserRepository userRepository;

	@Test
	@DisplayName("수정일 기준 내림차순으로 Todo 목록을 조회할 수 있다.")
	public void successFindAllTodoAndModifiedAtDesc() {
	    //given
		User user = new User("test1@test.com", "1234", UserRole.USER);
		userRepository.save(user);

		Todo todo1 = new Todo("제목1", "내용1", "Sunny", user);
		Todo todo2 = new Todo("제목2", "내용2", "Sunny", user);

		todoRepository.save(todo1);
		todoRepository.save(todo2);

		PageRequest pageRequest = PageRequest.of(0, 10);

		//when
		Page<Todo> result = todoRepository.findAllByOrderByModifiedAtDesc(pageRequest);

		//then
		assertThat(result).hasSize(2);
		assertThat(result.getContent().get(0).getModifiedAt())
			.isAfter(result.getContent().get(1).getModifiedAt());

		Todo todoFirst = result.getContent().get(0);
		assertEquals(todoFirst.getUser().getEmail(), user.getEmail());
	}

	@Test
	@DisplayName("user가 작성한 Todo를 단건 조회할 수 있다.")
	public void successFindTodoUser() {
	    //given
		User user = new User("test1@test.com", "1234", UserRole.USER);
		userRepository.save(user);

		Todo todo = new Todo("제목1", "내용1", "Sunny", user);
		todoRepository.save(todo);

	    //when
		Todo findTodo = todoRepository.findByIdWithUser(todo.getId()).get();

		//then
		assertEquals(findTodo.getTitle(), "제목1");
		assertEquals(findTodo.getContents(), "내용1");
		assertEquals(findTodo.getWeather(), "Sunny");

		assertThat(findTodo.getUser()).isNotNull();
		assertEquals(findTodo.getUser().getId(), user.getId());
		assertEquals(findTodo.getUser().getEmail(), "test1@test.com");

		assertThat(findTodo.getModifiedAt()).isNotNull();
		assertThat(findTodo.getCreatedAt()).isNotNull();
	}

	@Test
	@DisplayName("해당 일정을 만든 user와 Todo를 조회할 수 있다.")
	public void successFindTodoAndUser() {
	    //given
		User user = new User("test1@test.com", "1234", UserRole.USER);
		userRepository.save(user);

		Todo todo = new Todo("제목1", "내용1", "Sunny", user);
		todoRepository.save(todo);

	    //when
		Todo findTodo = todoRepository.findByIdAndUserId(todo.getId(), user.getId()).get();

		//then
		assertNotNull(findTodo);
		assertEquals(findTodo.getId(), todo.getId());
		assertEquals(findTodo.getUser().getId(), user.getId());
	}
}