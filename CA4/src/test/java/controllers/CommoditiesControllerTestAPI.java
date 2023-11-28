package controllers;

import application.BalootApplication;
import com.fasterxml.jackson.databind.ObjectMapper;
import exceptions.*;
import model.Comment;
import model.Commodity;
import model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import service.Baloot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static defines.Errors.*;
import static org.mockito.AdditionalMatchers.not;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@SpringBootTest(classes = BalootApplication.class)
public class CommoditiesControllerTestAPI {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CommoditiesController commoditiesController;

    @MockBean
    private Baloot balootMock;

    @BeforeEach
    public void setUp() {
        commoditiesController.setBaloot(balootMock);
    }

    @Test
    @DisplayName("Test getCommodity return ok status when commodities is empty")
    public void testGetCommodityWhenCommoditiesIsEmpty() throws Exception {
        ArrayList<Commodity> commodities = new ArrayList<>();
        when(balootMock.getCommodities()).thenReturn(commodities);
        mockMvc.perform(get("/commodities"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    @DisplayName("Test getCommodities return ok status when commodities is not empty")
    public void testGetCommoditiesWhenCommoditiesIsNotEmpty() throws Exception {
        ArrayList<Commodity> commodities = new ArrayList<>();
        commodities.add(new Commodity());
        commodities.add(new Commodity());
        when(balootMock.getCommodities()).thenReturn(commodities);
        mockMvc.perform(get("/commodities"))
                .andExpect(status().isOk())
                .andExpect(content().json("[{},{}]"));
    }

    @Test
    @DisplayName("Test getComodity with existed id")
    public void testGetCommodityWithExistedId() throws Exception {
        String commodityId = "1";
        Commodity commodity = new Commodity();
        when(balootMock.getCommodityById(commodityId)).thenReturn(commodity);
        mockMvc.perform(get("/commodities/" + commodityId))
                .andExpect(status().isOk())
                .andExpect(content().json("{}"));
    }

    @Test
    @DisplayName("Test getCommodity with not existed id")
    public void testGetCommodityWithNotExistedId() throws Exception {
        String commodityId = "1";
        when(balootMock.getCommodityById(commodityId)).thenThrow(new NotExistentCommodity());
        mockMvc.perform(get("/commodities/" + commodityId))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Test rateCommodity with existed commodityId, correct rate format, and correct rate range return ok status")
    public void testRateCommodityWithExistedCommodityId() throws Exception {
        String commodityId = "1";
        Map<String, String> input = Map.of("rate", "3", "username", "username1");
        Commodity commodity = new Commodity();
        when(balootMock.getCommodityById(commodityId)).thenReturn(commodity);
        mockMvc.perform(post("/commodities/" + commodityId + "/rate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(input)))
                .andExpect(status().isOk())
                .andExpect(content().string("rate added successfully!"));
    }

    @Test
    @DisplayName("Test rateCommodity with not existed commodityId return not found status")
    public void testRateCommodityWithNotExistedCommodityId() throws Exception {
        String commodityId = "1";
        Map<String, String> input = Map.of("rate", "3", "username", "username1");
        when(balootMock.getCommodityById(commodityId)).thenThrow(new NotExistentCommodity());
        mockMvc.perform(post("/commodities/" + commodityId + "/rate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(input)))
                .andExpect(status().isNotFound())
                .andExpect(content().string(NOT_EXISTENT_COMMODITY));
    }

    @ParameterizedTest
    @DisplayName("Test rateCommodity with existed commodityId and invalid rate range return bad request status")
    @ValueSource(strings = {"-1", "0", "11"})
    public void testRateCommodityWithExistedCommodityIdAndInValidRateRange(String rate) throws Exception {
        String commodityId = "1";
        Map<String, String> input = Map.of("rate", rate, "username", "username1");
        Commodity commodity = new Commodity();
        when(balootMock.getCommodityById(commodityId)).thenReturn(commodity);
        mockMvc.perform(post("/commodities/" + commodityId + "/rate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(input)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(RATE_OUT_OF_RANGE));
    }

    // CHECKME: should check null format in this method or add a new method for null format?

    @Test
    @DisplayName("Test rateCommoidty with existed commodityId and null rate return bad request status")
    public void testRateCommodityWithExistedCommodityIdAndNullRate() throws Exception {
        String commodityId = "1";
        Map<String, String> input = new HashMap<>();
        input.put("rate", null);
        input.put("username", "username1");
        Commodity commodity = new Commodity();
        when(balootMock.getCommodityById(commodityId)).thenReturn(commodity);
        mockMvc.perform(post("/commodities/" + commodityId + "/rate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(input)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Cannot parse null string"));
    }

    @Test
    @DisplayName("Test addCommodityComment with existed username return ok status")
    public void testAddCommodityCommentWithExistedUsername() throws Exception {
        String commodityId = "1";
        User user = new User("username1", "password", "user@gmail.com", "2001-11-25", "address");
        when(balootMock.generateCommentId()).thenReturn(1);
        when(balootMock.getUserById("username1")).thenReturn(user);
        Map<String, String> input = Map.of("username", "username1", "comment", "comment2");
        mockMvc.perform(post("/commodities/" + commodityId + "/comment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(input)))
                .andExpect(status().isOk())
                .andExpect(content().string("comment added successfully!"));
    }

    @Test
    @DisplayName("Test addCommodityComment with not existed username return not found status")
    public void testAddCommodityCommentWithNotExistedUsername() throws Exception {
        String commodityId = "1";
        when(balootMock.generateCommentId()).thenReturn(1);
        when(balootMock.getUserById("username1")).thenThrow(new NotExistentUser());
        Map<String, String> input = Map.of("username", "username1", "comment", "comment2");
        mockMvc.perform(post("/commodities/" + commodityId + "/comment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(input)))
                .andExpect(status().isNotFound())
                .andExpect(content().string("user not found!"));
    }

    @Test
    @DisplayName("Test addCommodityComment with null user return not found status")
    public void testAddCommodityCommentWithNullUser() throws Exception {
        String commodityId = "1";
        when(balootMock.generateCommentId()).thenReturn(1);
        when(balootMock.getUserById(not(eq("username")))).thenThrow(new NotExistentUser());
        Map<String, String> input = new HashMap<>();
        input.put("username", null);
        input.put("comment", "comment2");
        mockMvc.perform(post("/commodities/" + commodityId + "/comment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(input)))
                .andExpect(status().isNotFound())
                .andExpect(content().string("user not found!"));
    }

    @Test
    @DisplayName("Test getCommodityComment with existed commodityId return ok status")
    public void testGetCommodityCommentWithExistedCommodityId() throws Exception {
        String commodityId = "1";
        ArrayList<Comment> comments = new ArrayList<>();
        comments.add(new Comment());
        comments.add(new Comment());
        when(balootMock.getCommentsForCommodity(Integer.parseInt(commodityId))).thenReturn(comments);
        mockMvc.perform(get("/commodities/" + commodityId + "/comment"))
                .andExpect(status().isOk())
                .andExpect(content().json("[{},{}]"));
    }

    @Test
    @DisplayName("Test searchCommodity with existed name return ok status")
    public void testSearchCommodityWithExistedName() throws Exception {
    Map<String, String> input = Map.of("searchOption", "name", "searchValue", "name1");
    ArrayList<Commodity> commodities = new ArrayList<>();
    commodities.add(new Commodity());
    when(balootMock.filterCommoditiesByName("name1")).thenReturn(commodities);
    mockMvc.perform(post("/commodities/search")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(new ObjectMapper().writeValueAsString(input)))
            .andExpect(status().isOk())
            .andExpect(content().json("[{}]"));
    }

    @Test
    @DisplayName("Test searchCommodity with existed category return ok status")
    public void testSearchCommodityWithExistedCategory() throws Exception {
        Map<String, String> input = Map.of("searchOption", "category", "searchValue", "category1");
        ArrayList<Commodity> commodities = new ArrayList<>();
        commodities.add(new Commodity());
        when(balootMock.filterCommoditiesByCategory("category1")).thenReturn(commodities);
        mockMvc.perform(post("/commodities/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(input)))
                .andExpect(status().isOk())
                .andExpect(content().json("[{}]"));
    }

    @Test
    @DisplayName("Test searchCommodity with existed provider return ok status")
    public void testSearchCommodityWithExistedProviderReturnOkStatus() throws Exception {
        Map<String, String> input = Map.of("searchOption", "provider", "searchValue", "provider1");
        ArrayList<Commodity> commodities = new ArrayList<>();
        commodities.add(new Commodity());
        when(balootMock.filterCommoditiesByProviderName("provider1")).thenReturn(commodities);
        mockMvc.perform(post("/commodities/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(input)))
                .andExpect(status().isOk())
                .andExpect(content().json("[{}]"));
    }

    @Test
    @DisplayName("Test searchCommodity with searchValue null return bad request status")
    public void testSearchCommodityWithSearchValueNull() throws Exception {
        Map<String, String> input = new HashMap<>();
        input.put("searchOption", "blah");
        input.put("searchValue", null);
        mockMvc.perform(post("/commodities/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(input)))
                .andExpect(status().isBadRequest())
                .andExpect(content().json("[]"));
    }

    @Test
    @DisplayName("Test getSuggestedCommodities with existed commodity")
    public void testGetSuggestedCommoditiesWithExistedCommodity() throws Exception {
        Commodity commodity = new Commodity();
        String commodityId = "1";
        commodity.setId(commodityId);
        when(balootMock.getCommodityById(commodityId)).thenReturn(commodity);
        ArrayList<Commodity> commodities = new ArrayList<Commodity>() {{
            add(commodity);
        }};
        when(balootMock.suggestSimilarCommodities(commodity)).thenReturn(commodities);

        mockMvc.perform(get("/commodities/" + commodityId + "/suggested"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(commodityId));
    }

    @Test
    @DisplayName("Test getSuggestedCommodities with not existed commodity")
    public void testGetSuggestedCommoditiesWithNotExistedCommodity() throws Exception {
        String commodityId = "1";
        when(balootMock.getCommodityById(commodityId)).thenThrow(new NotExistentCommodity());
        mockMvc.perform(get("/commodities/" + commodityId + "/suggested"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("[]"));
    }

}