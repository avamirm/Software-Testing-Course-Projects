package model;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class CommentTest {
    private Comment comment;

    @BeforeEach
    void setUp() {
        comment = new Comment(0, "user@email", "user", 1, "text");

    }

    @Test
    @DisplayName("Test Comment constructor with id")
    void testConstructorWithId() {
        int id = 1;
        Comment comment = new Comment(id, " ", " ", 1, " ");
        assertEquals(id, comment.getId());
    }

    @Test
    @DisplayName("Test Comment constructor with userEmail")
    void testConstructorWithEmail() {
        String email = "user@email.com";
        Comment comment = new Comment(1, email, " ", 1, " ");
        assertEquals(email, comment.getUserEmail());
    }

    @Test
    @DisplayName("Test Comment constructor with username")
    void testConstructorWithUsername() {
        String username = "user";
        Comment comment = new Comment(1, " ", username, 1, " ");
        assertEquals(username, comment.getUsername());
    }

    @Test
    @DisplayName("Test Comment constructor with commodityId")
    void testConstructorWithCommodityId() {
        int id = 1;
        Comment comment = new Comment(1, " ", " ", id, " ");
        assertEquals(id, comment.getCommodityId());
    }

    @Test
    @DisplayName("Test Comment constructor with text")
    void testConstructorWithText() {
        String text = "text";
        Comment comment = new Comment(1, " ", " ", 1, text);
        assertEquals(text, comment.getText());
    }
    @Test
    @DisplayName("Test Comment current date format")
    public void testCurrentDateFormat() {
        String currentDate = comment.getCurrentDate();
        assertTrue(currentDate.matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}"));
    }

    @Test
    @DisplayName("Test Comment current date")
    public void testCurrentDate() {
        String currentDate = comment.getCurrentDate();
        Date date = new Date();
        SimpleDateFormat formatOfDate = new SimpleDateFormat("yyyy-MM-dd");
        assertEquals(formatOfDate.format(date), currentDate.substring(0, 10));
    }
    @ParameterizedTest
    @CsvSource({"user1, like", "user2, dislike"})
    @DisplayName("Test Comment with user vote containing the key of userVote")
    void testAddVoteWithContainingKeyOfVote(String username, String vote) {
        comment.addUserVote(username, vote);
        Map<String, String> userVote = comment.getUserVote();
        assertTrue(userVote.containsKey(username));
    }

    @ParameterizedTest
    @CsvSource({"user1, like", "user2, dislike"})
    @DisplayName("Test Comment with adding user vote with correct vote")
    void testAddVoteWithHavingCorrectVote(String username, String vote) {
        comment.addUserVote(username, vote);
        Map<String, String> userVote = comment.getUserVote();
        assertEquals(vote, userVote.get(username));
    }
    @ParameterizedTest
    @CsvSource({"like, dislike", "dislike, like"})
    @DisplayName("Test Comment with add vote to userVote with same username")
    void testAddVoteToUserVoteWithSameUsername(String firstVote, String secondVote) {
        comment.addUserVote("user1", firstVote);
        comment.addUserVote("user1", secondVote);
        Map<String, String> userVote = comment.getUserVote();
        assertEquals(secondVote, userVote.get("user1"));
    }

    @Test
    @DisplayName("Test Comment likes with adding multiple votes")
    void testLikesWithAddingMultipleVotes() {
        int likesBeforeCount = comment.getLike();
        comment.addUserVote("user1", "like");
        comment.addUserVote("user2", "dislike");
        comment.addUserVote("user3", "like");
        assertEquals(likesBeforeCount + 2, comment.getLike());
    }

    @Test
    @DisplayName("Test Comment dislikes with adding multiple votes")
    void testDislikesWithAddingMultipleVotes() {
        int dislikesBeforeCount = comment.getDislike();
        comment.addUserVote("user1", "like");
        comment.addUserVote("user2", "dislike");
        assertEquals(dislikesBeforeCount + 1, comment.getDislike());
    }
}
