package controllers;

import exceptions.NotExistentCommodity;
import exceptions.NotExistentUser;
import exceptions.RateOutOfRange;
import model.Comment;
import model.Commodity;
import model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import service.Baloot;

import java.util.ArrayList;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;
import static defines.Errors.NOT_EXISTENT_COMMODITY;
import static defines.Errors.RATE_OUT_OF_RANGE;

public class CommoditiesControllerTest {
    private CommoditiesController commoditiesController;
    private Baloot balootMock;
    @BeforeEach
    void setUp() {
        commoditiesController = new CommoditiesController();
        balootMock = mock(Baloot.class);
        commoditiesController.setBaloot(balootMock);
    }

    @Test
    @DisplayName("Test CommoditiesController getCommodities with empty commodity")
    void testGetCommoditiesWithEmptyCommodity() {
        ResponseEntity<ArrayList<Commodity>> response = commoditiesController.getCommodities();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(new ArrayList<>(), response.getBody());
    }

    @Test
    @DisplayName("Test CommoditiesController getCommodities with non-empty commodities")
    void testGetCommoditiesWithNonEmptyCommodities() {
        ArrayList<Commodity> commoditiesMock = new ArrayList<>() {{
            add(mock(Commodity.class));
            add(mock(Commodity.class));
        }};
        when(balootMock.getCommodities()).thenReturn(commoditiesMock);
        ResponseEntity<ArrayList<Commodity>> response = commoditiesController.getCommodities();
        verify(balootMock).getCommodities();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(commoditiesMock, response.getBody());
    }

    @Test
    @DisplayName("Test CommoditiesController getCommodity with existed id")
    void testGetCommodityWithExistedId() throws NotExistentCommodity {
        Commodity commodityMock = mock(Commodity.class);
        when(balootMock.getCommodityById("1")).thenReturn(commodityMock);
        ResponseEntity<Commodity> response = commoditiesController.getCommodity("1");
        verify(balootMock).getCommodityById("1");
        assertEquals(commodityMock, response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @DisplayName("Test CommoditiesController getCommodity with not existed id")
    void testGetCommodityStatusCodeWithNotExistedId() throws NotExistentCommodity {
        when(balootMock.getCommodityById("1")).thenThrow(new NotExistentCommodity());
        ResponseEntity<Commodity> response = commoditiesController.getCommodity("1");
        verify(balootMock).getCommodityById("1");
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    @DisplayName("Test CommoditiesController rateCommodity with existed id and correct rate")
    void testRateCommodityWithExistedIdAndCorrectRate() throws NotExistentCommodity, RateOutOfRange {
        Commodity commoditySpy = spy(Commodity.class);
        when(balootMock.getCommodityById("1")).thenReturn(commoditySpy);
        Map<String, String> input = Map.of("rate", "10", "username", "username");
        ResponseEntity<String> response = commoditiesController.rateCommodity("1", input);
        verify(balootMock).getCommodityById("1");
        verify(commoditySpy).addRate("username", 10);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("rate added successfully!", response.getBody());
    }

    @Test
    @DisplayName("Test CommoditiesController rateCommodity incorrect rate")
    void testRateCommodityWithIncorrectRate() throws NotExistentCommodity, RateOutOfRange {
        Commodity commoditySpy = spy(Commodity.class);
        when(balootMock.getCommodityById("1")).thenReturn(commoditySpy);
        Map<String, String> input = Map.of("rate", "11", "username", "username");
        ResponseEntity<String> response = commoditiesController.rateCommodity("1", input);
        verify(balootMock).getCommodityById("1");
        verify(commoditySpy).addRate("username", 11);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(RATE_OUT_OF_RANGE, response.getBody());
    }

    @Test
    @DisplayName("Test CommoditiesController rateCommodity with incorrect numberFormat")
    void testRateCommodityWithIncorrectNumberFormat() throws NotExistentCommodity {
        Map<String, String> input = Map.of("rate", "5.5", "username", "username");
        ResponseEntity<String> response = commoditiesController.rateCommodity("1", input);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("For input string: \"5.5\"", response.getBody());
    }

    @Test
    @DisplayName("Test CommoditiesController rateCommodity with not existed id")
    void testRateCommodityWithNotExistedId() throws NotExistentCommodity {
        when(balootMock.getCommodityById("1")).thenThrow(new NotExistentCommodity());
        Map<String, String> input = Map.of("rate", "10", "username", "username");
        ResponseEntity<String> response = commoditiesController.rateCommodity("1", input);
        verify(balootMock).getCommodityById("1");
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(NOT_EXISTENT_COMMODITY, response.getBody());
    }

    @Test
    @DisplayName("Test CommoditiesController addCommodityComment with existed id and correct input")
    void testAddCommodityCommentWithExistedIdAndCorrectInput() throws NotExistentUser {
        Map<String, String> input = Map.of("username", "usernameValue", "comment", "commentValue");
        when(balootMock.generateCommentId()).thenReturn(1);
        User user = new User();
        when(balootMock.getUserById("usernameValue")).thenReturn(new User() {{
            setEmail("email");
            setUsername("usernameValue");
        }});
        ArgumentCaptor<Comment> commentCaptor = ArgumentCaptor.forClass(Comment.class);
        doNothing().when(balootMock).addComment(commentCaptor.capture());
        ResponseEntity<String> response = commoditiesController.addCommodityComment("2", input);
        verify(balootMock).generateCommentId();
        verify(balootMock).getUserById("usernameValue");
        verify(balootMock).addComment(commentCaptor.capture());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("comment added successfully!", response.getBody());
        assertEquals(1, commentCaptor.getValue().getId());
        assertEquals("usernameValue", commentCaptor.getValue().getUsername());
        assertEquals("commentValue", commentCaptor.getValue().getText());
        assertEquals("email", commentCaptor.getValue().getUserEmail());
        assertEquals(2, commentCaptor.getValue().getCommodityId());
    }

    @Test
    @DisplayName("Test CommoditiesController addCommodityComment with not existed user")
    void testAddCommodityCommentWithNotExistedUser() throws NotExistentUser {
        Map<String, String> input = Map.of("username", "usernameValue", "comment", "commentValue");
        when(balootMock.generateCommentId()).thenReturn(1);
        when(balootMock.getUserById("usernameValue")).thenThrow(new NotExistentUser());
        ResponseEntity<String> response = commoditiesController.addCommodityComment("2", input);
        verify(balootMock).generateCommentId();
        verify(balootMock).getUserById("usernameValue");
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("user not found!", response.getBody());
    }

    @Test
    @DisplayName("Test CommoditiesController getCommodityComment with correct commodity id")
    void testGetCommodityCommentWithCorrectCommodityId() throws NotExistentCommodity {
        //add real comments to arraylist
        ArrayList<Comment> comments = new ArrayList<>() {{
            add(new Comment(1, "email", "username1", 1, "comment"));
            add(new Comment(2, "email2", "username2", 1, "comment2"));
        }};
        when(balootMock.getCommentsForCommodity(1)).thenReturn(comments);
        ResponseEntity<ArrayList<Comment>> response = commoditiesController.getCommodityComment("1");
        verify(balootMock).getCommentsForCommodity(1);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(comments, response.getBody());
    }

    @Test
    @DisplayName("Test CommoditiesController searchCommodities with name as the search option")
    void testSearchCommoditiesWithNameAsTheSearchOption() {
        Map<String, String> input = Map.of("searchOption", "name", "searchValue", "value");
        ArrayList<Commodity> commodities = new ArrayList<>() {{
            add(new Commodity());
            add(new Commodity());
        }};
        when(balootMock.filterCommoditiesByName("value")).thenReturn(commodities);
        ResponseEntity<ArrayList<Commodity>> response = commoditiesController.searchCommodities(input);
        verify(balootMock).filterCommoditiesByName("value");
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(commodities, response.getBody());
    }

    @Test
    @DisplayName("Test CommoditiesController searchCommodities with category as the search option")
    void testSearchCommoditiesWithCategoryAsTheSearchOption() {
        Map<String, String> input = Map.of("searchOption", "category", "searchValue", "value");
        ArrayList<Commodity> commodities = new ArrayList<>() {{
            add(new Commodity());
            add(new Commodity());
        }};
        when(balootMock.filterCommoditiesByCategory("value")).thenReturn(commodities);
        ResponseEntity<ArrayList<Commodity>> response = commoditiesController.searchCommodities(input);
        verify(balootMock).filterCommoditiesByCategory("value");
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(commodities, response.getBody());
    }

    @Test
    @DisplayName("Test CommoditiesController searchCommodities with provider as the search option")
    void testSearchCommoditiesWithProviderAsTheSearchOption() {
        Map<String, String> input = Map.of("searchOption", "provider", "searchValue", "value");
        ArrayList<Commodity> commodities = new ArrayList<>() {{
            add(new Commodity());
            add(new Commodity());
        }};
        when(balootMock.filterCommoditiesByProviderName("value")).thenReturn(commodities);
        ResponseEntity<ArrayList<Commodity>> response = commoditiesController.searchCommodities(input);
        verify(balootMock).filterCommoditiesByProviderName("value");
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(commodities, response.getBody());
    }

    @Test
    @DisplayName("Test CommoditiesController searchCommodities with default search option")
    void testSearchCommoditiesWithDefaultSearchOption() {
        Map<String, String> input = Map.of("searchOption", "default", "searchValue", "value");
        ResponseEntity<ArrayList<Commodity>> response = commoditiesController.searchCommodities(input);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(new ArrayList<>(), response.getBody());
    }

    @Test
    @DisplayName("Test CommoditiesController getSuggestedCommodities with not existed id")
    void testGetSuggestedCommoditiesWithNotExistedId() throws NotExistentCommodity {
        when(balootMock.getCommodityById("1")).thenThrow(new NotExistentCommodity());
        ResponseEntity<ArrayList<Commodity>> response = commoditiesController.getSuggestedCommodities("1");
        verify(balootMock).getCommodityById("1");
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(new ArrayList<>(), response.getBody());
    }

    @Test
    @DisplayName("Test CommoditiesController getSuggestedCommodities with existed id")
    void testGetSuggestedCommoditiesWithExistedId() throws NotExistentCommodity {
        Commodity commodity = new Commodity();
        when(balootMock.getCommodityById("1")).thenReturn(commodity);
        ArrayList<Commodity> suggestedCommodities = new ArrayList<>() {{
            add(new Commodity());
            add(new Commodity());
        }};
        when(balootMock.suggestSimilarCommodities(commodity)).thenReturn(suggestedCommodities);
        ResponseEntity<ArrayList<Commodity>> response = commoditiesController.getSuggestedCommodities("1");
        verify(balootMock).getCommodityById("1");
        verify(balootMock).suggestSimilarCommodities(commodity);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(suggestedCommodities, response.getBody());
    }
}
