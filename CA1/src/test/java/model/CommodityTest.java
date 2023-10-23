package model;
import static defines.Errors.*;
import static org.junit.jupiter.api.Assertions.*;
import exceptions.NotInStock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Map;


public class CommodityTest {
    private Commodity commodity;

    @BeforeEach
    public void setUp() {
         commodity = new Commodity();
    }

    @ParameterizedTest
    @DisplayName("updateInStock does not throw exception")
    @ValueSource(ints = {-100, -50, 0, 50, 100})
    void updateInStockDoesNotThrowExp(int amount) {
        this.commodity.setInStock(100);
        assertDoesNotThrow(() -> {
            this.commodity.updateInStock(amount);
        });
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 10, -200, -20})
    @DisplayName("Test Commodity updateInStock with non-negative stock in the end")
    void testUpdateInStockeWithNonNegativeStockInTheEnd(int amount) {
        commodity.setInStock(200);
        assertDoesNotThrow(() -> {
            commodity.updateInStock(amount);
        });
        assertEquals(200 + amount, commodity.getInStock());
    }

    @ParameterizedTest
    @CsvSource({"-20, -30", "-20, 10", "-20, 0", "10, -30", "0, -5"})
    @DisplayName("Test Commodity updateInStock with negative stock in the end")
    void testUpdateStockWithNegativeStockInTheEnd(int setUpValue, int amount) {
        commodity.setInStock(setUpValue);
        assertThrows(NotInStock.class, () -> {
            commodity.updateInStock(amount);
        });
    }

    @ParameterizedTest
    @ValueSource(ints = {-20, 0, 10})
    @DisplayName("Test Commodity updateStock correctly in the end")
    void testUpdateStockCorrectlyInTheEnd(int amount) throws NotInStock{
        commodity.setInStock(20);
        commodity.updateInStock(amount);
        assertEquals(20 + amount, commodity.getInStock());
    }


    @Test
    @DisplayName("addRate to userRate")
    public void addRateCorrectly() {
        this.commodity.addRate("user1", 1);
        assertTrue(this.commodity.getUserRate().containsKey("user1"));
    }

    @Test
    @DisplayName("new rate added to userRate with correct score")
    public void addRateWithUserAndRateCorrectly() {
        this.commodity.addRate("user1", 1);
        assertEquals(1, this.commodity.getUserRate().get("user1"));
    }

    // FIXME: should it throw exception?
    @Test
    @DisplayName("addRate with Null username")
    public void addRateNullUsername() {
        this.commodity.addRate(null, 1);
        assertTrue(this.commodity.getUserRate().containsKey(null));
    }

    @Test
    @DisplayName("existing user updated score")
    public void updateRateOnExistingUser() {
        commodity.addRate("user1", 3);
        commodity.addRate("user1", 5);
        Map<String, Integer> userRate = this.commodity.getUserRate();
        assertEquals(5, userRate.get("user1"));
    }

    @ParameterizedTest
    @CsvSource({"10, 10, 10", "5, 10, 7.5"})
    @DisplayName("calculate rating with single user")
    public void calcRatingWithSingleUser(int initRate, int addRate, double expected) {
        commodity.setInitRate(initRate);
        commodity.addRate("user1", addRate);
        assertEquals(expected, commodity.getRating());
    }

    @ParameterizedTest
    @CsvSource({"10, 20, 30, 20"})
    @DisplayName("calculate rating with multiple user")
    public void calcRatingWithMultipleUser(int initRate, int addRateUser1, int addRateUser2, double expected) {
        commodity.setInitRate(initRate);
        commodity.addRate("user1", addRateUser1);
        commodity.addRate("user2", addRateUser2);
        assertEquals(expected, commodity.getRating());
    }


    @Test
    @DisplayName("calculate rating with duplicate user")
    public void calcRatingWithDuplicateUser() {
        commodity.setInitRate(10);
        commodity.addRate("user1", 5);
        commodity.addRate("user2", 3);
        commodity.addRate("user1", 2);
        commodity.addRate("user2", 9);
        assertEquals(7, commodity.getRating());
    }
}
