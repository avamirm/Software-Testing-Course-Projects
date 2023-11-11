package domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class OrderTest {
    Order order;

    @BeforeEach
    public void setUp() {
        order = new Order(){{
            id = 1;
            customer = 0;
            price = 10;
            quantity = 1;}};
    }

    @Test
    @DisplayName("Test setId")
    public void testSetId() {
        order.setId(2);
        assertEquals(2, order.getId());
    }

    @Test
    @DisplayName("Test setCustomer")
    public void testSetCustomer() {
        order.setCustomer(2);
        assertEquals(2, order.getCustomer());
    }

    @Test
    @DisplayName("Test setPrice")
    public void testSetPrice() {
        order.setPrice(20);
        assertEquals(20, order.getPrice());
    }

    @Test
    @DisplayName("Test setQuantity")
    public void testSetQuantity() {
        order.setQuantity(2);
        assertEquals(2, order.getQuantity());
    }

    @Test
    @DisplayName("Test getId")
    public void testGetId() {
        assertEquals(1, order.getId());
    }

    @Test
    @DisplayName("Test getCustomer")
    public void testGetCustomer() {
        assertEquals(0, order.getCustomer());
    }

    @Test
    @DisplayName("Test getPrice")
    public void testGetPrice() {
        assertEquals(10, order.getPrice());
    }

    @Test
    @DisplayName("Test getQuantity")
    public void testGetQuantity() {
        assertEquals(1, order.getQuantity());
    }

    @Test
    @DisplayName("Test equals with object which is not instance of Order")
    public void testEqualsWithObjectWhichIsNotInstanceOfOrder() {
        Object object = new Object();
        assertFalse(order.equals(object));
    }

    @Test
    @DisplayName("Test equals with equal id")
    public void testEqualsWithEqualId() {
        Order order2 = new Order(){{id = 1;}};
        assertTrue(order.equals(order2));
    }

    @Test
    @DisplayName("Test equals with not equal id")
    public void testEqualsWithNotEqualId() {
        Order order2 = new Order(){{id = 2;}};
        assertFalse(order.equals(order2));
    }
}