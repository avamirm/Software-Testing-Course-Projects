package model;

import exceptions.CommodityIsNotInBuyList;
import exceptions.InsufficientCredit;
import exceptions.InvalidCreditRange;
import exceptions.NotInStock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserTest {
    User user;

    @BeforeEach
    void setUp() {
        user = new User("username", "password", "user@gmail.com", "2020-10-10", "address");
    }

    @ParameterizedTest
    @ValueSource(floats = {0.1f, 0f, 20f, 30.3f})
    @DisplayName("test User with adding non negative credit with not throwing exception")
    void testAddNonNegativeCredit(float amount) {
        user.setCredit(0);
        assertDoesNotThrow(() -> {
            user.addCredit(amount);
        });
    }

    @ParameterizedTest
    @CsvSource({"0.1, 0.1", "0, 0", "20, 20", "30.3, 30.3"})
    @DisplayName("test User with adding non negative credit correctly")
    void testAddNonNegativeCreditCorrectly(float initialCredit, float amount) throws InvalidCreditRange {
        user.setCredit(initialCredit);
        user.addCredit(amount);
        assertEquals(initialCredit + amount, user.getCredit());
    }

    @ParameterizedTest
    @ValueSource(floats = {-0.1f, -0.0001f, -20f, -30.3f})
    @DisplayName("test User assertion with adding negative credit")
    void testAddNegativeCredit(float amount) {
        user.setCredit(0);
        assertThrows(InvalidCreditRange.class, () -> {
            user.addCredit(amount);
        });
    }

    @ParameterizedTest
    @CsvSource({"0.1f, 0.1f", "0f, 0f", "20f, 10f", "30.3f, 1f"})
    @DisplayName("test User with not throwing exception with correct withdrawing")
    void testWithdrawWithNonNegativeCreditInTheEnd(float initialCredit, float amount) {
        user.setCredit(initialCredit);
        assertDoesNotThrow(() -> {
            user.withdrawCredit(amount);
        });
    }

    @ParameterizedTest
    @CsvSource({"0.1f, 0.1f", "0f, 0f", "30f, 20f"})
    @DisplayName("test User with withdrawing non negative credit correctly")
    void testWithdrawCorrectly(float initialCredit, float amount) throws InsufficientCredit {
        user.setCredit(initialCredit);
        user.withdrawCredit(amount);
        assertEquals(initialCredit - amount, user.getCredit());
    }

    @ParameterizedTest
    @CsvSource({"0.1, 0.2", "0, 0.1", "30, 40"})
    @DisplayName("test User with withdrawing with insufficient credit")
    void testWithdrawWithInsufficientCredit(float initialCredit, float amount) {
        user.setCredit(initialCredit);
        assertThrows(InsufficientCredit.class, () -> {
            user.withdrawCredit(amount);
        });
    }

    @ParameterizedTest
    @CsvSource({"1, 1", "0, 1", "20, 10"})
    @DisplayName("test User with adding purchased with repetitive id")
    void testAddPurchasedWithRepetitiveId(Integer initialQuantity, Integer quantity) {
        Map<String, Integer> purchasedList = new HashMap<>() {{
            put("1", initialQuantity);
            put("2", 2);
        }};
        user.setPurchasedList(purchasedList);
        user.addPurchasedItem("1", quantity);
        assertEquals(initialQuantity + quantity, user.getPurchasedList().get("1"));
    }

    @ParameterizedTest
    @CsvSource({"1, 1", "0, 1", "20, 10"})
    @DisplayName("test User with adding purchased with new id")
    void testAddPurchasedWithNewId(Integer initialQuantity, Integer quantity) {
        Map<String, Integer> purchasedList = new HashMap<>() {{
            put("1", initialQuantity);
            put("2", 2);
        }};
        user.setPurchasedList(purchasedList);
        user.addPurchasedItem("3", quantity);
        assertEquals(quantity, user.getPurchasedList().get("3"));
    }

    @Test
    @DisplayName("test User by adding to buy item when commodity in stock is not positive")
    void testAddBuyItemWhenCommodityInStockIsNotPositive() {
        Commodity commodity = new Commodity();
        commodity.setId("3");
        Map<String, Integer> buyList = new HashMap<>() {{
            put("1", 1);
            put("2", 2);
        }};
        user.setBuyList(buyList);
        commodity.setInStock(0);
        assertThrows(NotInStock.class, () -> {
            user.addBuyItem(commodity);
        });
    }

    @Test
    @DisplayName("test User by adding to buy item when commodity in stock is positive")
    void testAddBuyItemWhenCommodityInStockIsPositive() throws NotInStock {
        Commodity commodity = new Commodity();
        commodity.setId("3");
        Map<String, Integer> buyList = new HashMap<>() {{
            put("1", 1);
            put("2", 2);
        }};
        user.setBuyList(buyList);
        commodity.setInStock(1);
        assertDoesNotThrow(() -> {
            user.addBuyItem(commodity);
        });
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3})
    @DisplayName("test User with adding new buy item and check if the id is added")
    void testAddBuyItemWithNewItemAndCheckIfIdIsAdded(int initialQuantity) throws NotInStock {
//        Commodity commodity = mock(Commodity.class);
//        when(commodity.getId()).thenReturn("3");
        Commodity commodity = new Commodity();
        commodity.setId("3");
        Map<String, Integer> buyList = new HashMap<>() {{
            put("1", initialQuantity);
            put("2", 2);
        }};
        user.setBuyList(buyList);
        commodity.setInStock(1);
        user.addBuyItem(commodity);
        assertTrue(user.getBuyList().containsKey("3"));
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3})
    @DisplayName("test User: adding new buy item with correct quantity")
    void testAddBuyItemWithNewItem(int initialQuantity) throws NotInStock {
//        Commodity commodity = mock(Commodity.class);
        Commodity commodity = new Commodity();
        commodity.setId("3");
//        when(commodity.getId()).thenReturn("3");
        Map<String, Integer> buyList = new HashMap<>() {{
            put("1", initialQuantity);
            put("2", 2);
        }};
        user.setBuyList(buyList);
        commodity.setInStock(1);
        user.addBuyItem(commodity);
        assertEquals(1, user.getBuyList().get("3"));
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3})
    @DisplayName("test User with adding existed buy item")
    void testAddBuyItemWithExistedItem(int initialQuantity) throws NotInStock {
//        Commodity commodity = mock(Commodity.class);
//        when(commodity.getId()).thenReturn("1");
        Commodity commodity = new Commodity();
        commodity.setId("1");
        Map<String, Integer> buyList = new HashMap<>() {{
            put("1", initialQuantity);
            put("2", 2);
        }};
        user.setBuyList(buyList);
        commodity.setInStock(1);
        user.addBuyItem(commodity);
        assertEquals(initialQuantity + 1, user.getBuyList().get("1"));
    }

    @Test
    @DisplayName("test remove non-existing item from buy list")
    void testRemoveNonExistingItemFromBuyList() {
        Commodity commodity = new Commodity();
        commodity.setId("1");
        assertThrows(CommodityIsNotInBuyList.class, () -> user.removeItemFromBuyList(commodity));
    }

    @Test
    @DisplayName("test remove 1 item from buy list")
    void testRemoveOneItemFromBuyList() throws CommodityIsNotInBuyList {
        Commodity commodity = new Commodity();
        commodity.setId("1");
        Map<String, Integer> buyList = new HashMap<>() {{
            put("1", 1);
        }};
        user.setBuyList(buyList);
        user.removeItemFromBuyList(commodity);
        assertFalse(user.getBuyList().containsKey("1"));
    }

    @Test
    @DisplayName("test decrease item quantity from buy list")
    void testDecreaseItemQuantityFromBuyList() throws CommodityIsNotInBuyList {
        Commodity commodity = new Commodity();
        commodity.setId("1");
        Map<String, Integer> buyList = new HashMap<>() {{
            put("1", 3);
        }};
        user.setBuyList(buyList);
        int initialQuantity = user.getBuyList().get("1");
        user.removeItemFromBuyList(commodity);
        assertEquals(initialQuantity - 1, user.getBuyList().get("1"));
    }
}