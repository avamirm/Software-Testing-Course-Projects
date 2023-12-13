package domain;


import com.sun.jdi.IntegerValue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;


public class EngineTest {
    Engine engine;

    @BeforeEach
    public void setUp() {
        engine = new Engine();
    }

    @Test
    @DisplayName("Test getAverageOrderQuantityByCustomer with no order")
    public void testGetAverageOrderQuantityByCustomerWithNoOrder() {
        engine.orderHistory = new ArrayList<>();
        int result = engine.getAverageOrderQuantityByCustomer(5);
        assertEquals(0, result);
    }

    @Test
    @DisplayName("Test getAverageOrderQuantityByCustomer with not existed customer")
    public void testGetAverageOrderQuantityByNotExistedCustomer() {
        engine.orderHistory = new ArrayList<>();
        Order order = new Order();
        order.customer = 6;
        engine.orderHistory.add(order);
        assertThrows(RuntimeException.class, () -> engine.getAverageOrderQuantityByCustomer(5));
    }

    @Test
    @DisplayName("Test getAverageOrderQuantityByCustomer with one order")
    public void testGetAverageOrderQuantityByCustomerWithOneOrder() {
        engine.orderHistory = new ArrayList<>();
        Order order = new Order();
        order.customer = 5;
        order.quantity = 10;
        engine.orderHistory.add(order);
        int result = engine.getAverageOrderQuantityByCustomer(5);
        assertEquals(10, result);
    }

    @Test
    @DisplayName("Test getAverageOrderQuantityByCustomer with multiple orders")
    public void testGetAverageOrderQuantityByCustomerWithMultipleOrders() {
        int customerId = 5;
        int quantity1 = 10, quantity2 = 20, quantity3 = 30;
        engine.orderHistory = new ArrayList<Order>() {{
            add(new Order() {{
                customer = customerId;
                quantity = quantity1;
            }});
            add(new Order() {{
                customer = customerId;
                quantity = quantity2;
            }});
            add(new Order() {{
                customer = customerId;
                quantity = quantity3;
            }});

            add(new Order() {{
                customer = 1;
                quantity = quantity3;
            }});
        }};
        assertEquals(Integer.valueOf((quantity1 + quantity2 + quantity3) / 3), engine.getAverageOrderQuantityByCustomer(customerId));
    }

    @Test
    @DisplayName("Test getQuantityPatternByPrice with no order")
    public void testGetQuantityPatternByPriceWithNoOrder() {
        engine.orderHistory = new ArrayList<>();
        assertEquals(0, engine.getQuantityPatternByPrice(5));
    }

    @Test
    @DisplayName("Test getQuantityPatternByPrice with one order")
    public void testGetQuantityPatternByPriceWithOneOrder() {
        engine.orderHistory = new ArrayList<>() {{
            add(new Order() {{
                price = 20;
                quantity = 10;
            }});
        }};
        assertEquals(0, engine.getQuantityPatternByPrice(5));
    }

    @Test
    @DisplayName("Test getQuantityPatternByPrice with multiple orders")
    void testGetQuantityPatternByPriceWithMultipleOrders() {
        int price_ = 20, quantity1 = 10, quantity2 = 20;
        engine.orderHistory = new ArrayList<>() {{
            add(new Order() {{
                id = 2;
                price = 10;
                quantity = quantity1;
            }});
            add(new Order() {{
                id = 3;
                price = 5;
                quantity = quantity2;
            }});
            add(new Order() {{
                id = 1;
                price = price_;
                quantity = quantity2;
            }});
        }};
        assertEquals(quantity2 - quantity1, engine.getQuantityPatternByPrice(price_));
    }

    @Test
    @DisplayName("Test getQuantityPatternByPrice with multiple orders and different diff")
    void testGetQuantityPatternByPriceWithDiffNotEqualToDifferenceOfPreviousAndCurrentQuantity() {
        int price_ = 20;
        engine.orderHistory = new ArrayList<>() {{
            add(new Order() {{
                id = 1;
                price = 10;
                quantity = 1;
            }});
            add(new Order() {{
                id = 2;
                price = price_;
                quantity = 3;
            }});
            add(new Order() {{
                id = 2;
                price = price_;
                quantity = 3;
            }});
            add(new Order() {{
                id = 6;
                price = price_;
                quantity = 5;
            }});
            add(new Order() {{
                id = 5;
                price = price_;
                quantity = 7;
            }});
        }};
        assertEquals(0, engine.getQuantityPatternByPrice(price_));
    }

    @Test
    @DisplayName("Test geetCustomerFraudulentQuantity with order with greater quantity")
    public void testGetCustomerFraudulentQuantityWithOrderWithGreaterQuantity() {
        int quantity1 = 10, quantity2 = 20;
        int customerId = 5;
        engine.orderHistory = new ArrayList<>() {{
            add(new Order() {{
                price = 10;
                quantity = quantity1;
                customer = customerId;
            }});
            add(new Order() {{
                price = 20;
                quantity = quantity2;
                customer = customerId;
            }});
        }};
        int avgQuantity = (quantity1 + quantity2) / 2;
        int orderQuantity = 40;
        assertEquals(orderQuantity - avgQuantity, engine.getCustomerFraudulentQuantity(new Order() {{
            price = 20;
            quantity = orderQuantity;
            customer = 5;
        }}));
    }

    @ParameterizedTest
    @DisplayName("Test getCustomerFraudulentQuantity with order with less or equal quantity")
    @ValueSource(ints = {15, 5})
    public void testGetCustomerFraudulentQuantityWithOrderWithLessOrEqualQuantity(int orderQuantity) {
        int quantity1 = 10, quantity2 = 20;
        int customerId = 5;
        engine.orderHistory = new ArrayList<>() {{
            add(new Order() {{
                price = quantity1;
                quantity = 10;
                customer = customerId;
            }});
            add(new Order() {{
                price = 20;
                quantity = quantity2;
                customer = customerId;
            }});
        }};
        assertEquals(0, engine.getCustomerFraudulentQuantity(new Order() {{
            price = 20;
            quantity = orderQuantity;
            customer = 5;
        }}));
    }

    @Test
    @DisplayName("Test getCustomerFraudulentQuantity with repetitive order")
    public void testAddOrderAndGetFraudulentQuantityWithRepetitiveOrder() {
        int quantity1 = 10, quantity2 = 20;
        int customerId = 5;
        Order order = new Order() {{
            price = 20;
            quantity = quantity2;
            customer = customerId;
        }};
        engine.orderHistory.add(order);
        assertEquals(0, engine.addOrderAndGetFraudulentQuantity(order));
    }

    @Test
    @DisplayName("Test addOrderAndGetFraudulentQuantity with different order added to order history")
    public void testAddOrderAndGetFraudulentQuantityWithDifferentOrderAddedToOrderHistory() {
        int quantity1 = 10, orderQuantity = 5;
        int customerId = 5;
        Order order = new Order() {{
            id = 2;
            price = 20;
            quantity = quantity1;
            customer = customerId;
        }};
        engine.orderHistory.add(order);
        Order orderAdded = new Order() {{
            price = 30;
            quantity = orderQuantity;
            customer = customerId;
        }};
        int res = engine.addOrderAndGetFraudulentQuantity(orderAdded);
        assertTrue(engine.orderHistory.contains(orderAdded));
    }

    @Test
    @DisplayName("Test addOrderAndGetFraudulentQuantity with different order when customer fraudulent quantity is zero")
    public void testAddOrderAndGetFraudulentQuantityWithDifferentOrderWhenCustomerFraudulentQuantityIsZero() {
        int quantity1 = 10, orderQuantity = 5;
        int customerId = 5;
        Order order = new Order() {{
            id = 2;
            price = 20;
            quantity = quantity1;
            customer = customerId;
        }};
        engine.orderHistory.add(order);
        Order orderAdded = new Order() {{
            price = 30;
            quantity = orderQuantity;
            customer = customerId;
        }};
        int res = engine.addOrderAndGetFraudulentQuantity(orderAdded);
        assertEquals(0, res);
    }

    @Test
    @DisplayName("Test addOrderAndGetFraudulentQuantity with different order when customer fraudulent quantity is non zero")
    public void testAddOrderAndGetFraudulentQuantityWithDifferentOrderWhenCustomerFraudulentQuantityIsNonZero() {
        int quantity1 = 10, orderQuantity = 20;
        int customerId = 5;
        Order order = new Order() {{
            id = 2;
            price = 20;
            quantity = quantity1;
            customer = customerId;
        }};
        engine.orderHistory.add(order);
        Order orderAdded = new Order() {{
            price = 30;
            quantity = orderQuantity;
            customer = customerId;
        }};
        int res = engine.addOrderAndGetFraudulentQuantity(orderAdded);
        assertEquals(engine.getQuantityPatternByPrice(orderAdded.price), res);
    }
}
