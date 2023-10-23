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
    @DisplayName("Test Comment id setter and getter")
    void testSetGetId() {
        int id = 1;
        comment.setId(id);
        assertEquals(id, comment.getId());
    }

    @Test
    @DisplayName("Test Comment userEmail setter and getter")
    void testSetGetEmail() {
        String email = "user@gmail.com";
        comment.setUserEmail(email);
        assertEquals(email, comment.getUserEmail());
    }

    @Test
    @DisplayName("Test Comment username setter and getter")
    void testSetGetUsername() {
        String userName = "user";
        comment.setUsername(userName);
        assertEquals(userName, comment.getUsername());
    }

    @Test
    @DisplayName("Test Comment commodityId setter and getter")
    void testSetGetCommodityId() {
        int id = 1;
        comment.setCommodityId(id);
        assertEquals(id, comment.getCommodityId());
    }

    @Test
    @DisplayName("Test Comment text setter and getter")
    void testSetGetText() {
        String text = "text";
        comment.setText(text);
        assertEquals(text, comment.getText());
    }

    @Test
    @DisplayName("Test Comment date setter and getter")
    void testSetGetDate() {
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String expectedDate = dateFormat.format(date);
        comment.setDate(expectedDate);
        assertEquals(expectedDate, comment.getDate());
    }

    @Test
    @DisplayName("Test Comment like setter and getter")
    void testSetGetLike() {
        int likes = 2;
        comment.setLike(likes);
        assertEquals(likes, comment.getLike());
    }

    @Test
    @DisplayName("Test Comment dislike setter and getter")
    void testSetGetDislike() {
        int dislikes = 1;
        comment.setDislike(dislikes);
        assertEquals(dislikes, comment.getDislike());
    }

    @Test
    @DisplayName("Test Comment userVote setter and getter")
    void testSetGetUserVote() {
        Map<String, String> userVote = new HashMap<>() {{
            put("user1", "like");
            put("user2", "dislike");
        }};
        comment.setUserVote(userVote);
        assertEquals(userVote, comment.getUserVote());
    }

    @ParameterizedTest
    @CsvSource({"user1,like", "user2,dislike"})
    @DisplayName("Test Comment with user vote containing the key of userVote")
    void testAddVoteWithContainingKeyOfVote(String username, String vote) {
        comment.addUserVote(username, vote);
        Map<String, String> userVote = comment.getUserVote();
        assertTrue(userVote.containsKey(username));
    }

    @ParameterizedTest
    @CsvSource({"user1,like", "user2,dislike"})
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
