package controllers;

import exceptions.NotExistentComment;
import model.Comment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import service.Baloot;

import java.util.HashMap;
import java.util.Map;

import static defines.Errors.NOT_EXISTENT_COMMENT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class CommentControllerTest {
    private Baloot balootMock;
    private CommentController commentController = new CommentController();
    private Comment commentMock;
    private Map<String, String> input;


    @BeforeEach
    void setUp() {
        balootMock = mock(Baloot.class);
        commentMock = spy(new Comment(1, "user@email", "user", 1, "text"));
        commentController.setBaloot(balootMock);
    }

    @Test
    @DisplayName("Test CommentController likeComment an existing comment")
    public void testLikeCommentHttpStatusOk() throws NotExistentComment {
        input = new HashMap<>() {{
            put("username", "user");
        }};
        when(balootMock.getCommentById(1)).thenReturn(commentMock);
        ResponseEntity<String> response = commentController.likeComment("1", input);
        verify(commentMock, times(1)).addUserVote("user", "like");
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("The comment was successfully liked!", response.getBody());
    }

    @Test
    @DisplayName("Test CommentController likeComment a non-existing comment")
    public void testLikeCommentHttpStatusNotFound() throws NotExistentComment {
        input = new HashMap<>() {{
            put("username", "user");
        }};
        when(balootMock.getCommentById(1)).thenThrow(new NotExistentComment());
        ResponseEntity<String> response = commentController.likeComment("1", input);
        verify(commentMock, times(0)).addUserVote("user", "like");
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(NOT_EXISTENT_COMMENT, response.getBody());
    }

    @Test
    @DisplayName("Test CommentController likeComment null username")
    public void testLikeCommentHttpStatusBadRequest() throws NotExistentComment {
        input = new HashMap<>() {{
            put("username", null);
        }};
        when(balootMock.getCommentById(1)).thenReturn(commentMock);
        verify(commentMock, times(0)).addUserVote("user", "dislike");
        ResponseEntity<String> response = commentController.likeComment("1", input);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Please enter your username!", response.getBody());
    }

    @Test
    @DisplayName("Test CommentController dislikeComment an existing comment")
    public void testDislikeCommentHttpStatusOk() throws NotExistentComment {
        input = new HashMap<>() {{
            put("username", "user");
        }};
        when(balootMock.getCommentById(1)).thenReturn(commentMock);
        ResponseEntity<String> response = commentController.dislikeComment("1", input);
        verify(commentMock, times(1)).addUserVote("user", "dislike");
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("The comment was successfully disliked!", response.getBody());
    }

    @Test
    @DisplayName("Test CommentController dislikeComment a non-existing comment")
    public void testDislikeCommentHttpStatusNotFound() throws NotExistentComment {
        input = new HashMap<>() {{
            put("username", "user");
        }};
        when(balootMock.getCommentById(1)).thenThrow(new NotExistentComment());
        ResponseEntity<String> response = commentController.dislikeComment("1", input);
        verify(commentMock, times(0)).addUserVote("user", "dislike");
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(NOT_EXISTENT_COMMENT, response.getBody());
    }

    @Test
    @DisplayName("Test CommentController dislikeComment null username")
    public void testDislikeCommentHttpStatusBadRequest() throws NotExistentComment {
        input = new HashMap<>() {{
            put("username", null);
        }};
        when(balootMock.getCommentById(1)).thenReturn(commentMock);
        ResponseEntity<String> response = commentController.dislikeComment("1", input);
        verify(commentMock, times(0)).addUserVote("user", "dislike");
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Please enter your username!", response.getBody());
    }
}