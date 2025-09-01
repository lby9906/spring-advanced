package org.example.expert.domain.comment.service;

import static org.mockito.Mockito.*;

import org.example.expert.domain.comment.repository.CommentRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CommentAdminServiceTest {
	@Mock
	private CommentRepository commentRepository;

	@InjectMocks
	private CommentAdminService commentAdminService;

	@Test
	@DisplayName("admin는 성공적으로 댓글을 삭제한다.")
	public void successDeleteAdminComment() {
	    //given
		long commentId = 1L;

		//when
		commentAdminService.deleteComment(commentId);

		//then
		verify(commentRepository, times(1)).deleteById(commentId);
	}
}