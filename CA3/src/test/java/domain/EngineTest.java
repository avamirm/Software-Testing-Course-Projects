package domain;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;



public class EngineTest {
    Engine engine;

    @BeforeEach
    public void setUp() {
        engine = new Engine();
    }

    @Test
    public void testgetAverageOrderQuantityByCustomerWithNoOrder() {
        engine.orderHistory = new ArrayList<>();
        int result = engine.getAverageOrderQuantityByCustomer(5);
        assert result == 0;
    }

}