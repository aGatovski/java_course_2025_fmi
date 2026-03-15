package bg.sofia.uni.fmi.mjt.order.loader;

import bg.sofia.uni.fmi.mjt.order.domain.Category;
import bg.sofia.uni.fmi.mjt.order.domain.Order;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.UncheckedIOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

class OrderLoaderTest {
    private static final String HEADER_LINE =
        "Order ID,Date,Product,Category,Price,Quantity,Total Sales,Customer Name,Customer Location,Payment Method,Status";

    private static final String VALID_ORDER_LINE =
        "ORD0001,14-03-25,Running Shoes,FOOTWEAR,60,3,180,Emma Clark,New York,DEBIT_CARD,CANCELLED";

    @Test
    void testLoadThrowsExceptionIfReaderIsNull() {
        assertThrows(IllegalArgumentException.class,
            () -> OrderLoader.load(null),
            "Expected load to throw IllegalArgumentException when reader is null");
    }

    @Test
    void testLoadValidData() {
        //setup
        Reader reader = new StringReader(HEADER_LINE + System.lineSeparator() + VALID_ORDER_LINE);
        //ex
        List<Order> orders = OrderLoader.load(reader);
        //test
        assertEquals(1, orders.size(), "Expected exactly one order to be loaded");

        Order actualOrder = orders.get(0);
        assertEquals("ORD0001", actualOrder.id());
        assertEquals("Emma Clark", actualOrder.customerName());
        assertEquals(Category.FOOTWEAR, actualOrder.category());
        assertEquals(180.0, actualOrder.totalSales());
    }

    @Test
    void testLoadHeaderOnlyReturnsEmptyList(){
        //setup
        Reader reader = new StringReader(HEADER_LINE);
        //ex
        List<Order> orders = OrderLoader.load(reader);
        //test
        assertTrue(orders.isEmpty(), "Expected empty list when input contains only header");
    }


}