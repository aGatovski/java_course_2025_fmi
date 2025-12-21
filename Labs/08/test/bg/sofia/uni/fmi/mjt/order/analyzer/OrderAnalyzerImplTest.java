package bg.sofia.uni.fmi.mjt.order.analyzer;

import bg.sofia.uni.fmi.mjt.order.domain.Category;
import bg.sofia.uni.fmi.mjt.order.domain.Order;
import bg.sofia.uni.fmi.mjt.order.domain.PaymentMethod;
import bg.sofia.uni.fmi.mjt.order.domain.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class OrderAnalyzerImplTest {

    private OrderAnalyzer analyzer;
    private List<Order> orders;

    @BeforeEach
    void setUp() {
        Order order1 = new Order("1", LocalDate.now(), "Laptop", Category.ELECTRONICS, 1000, 1, 1000, "Ivan", "Sofia",
            PaymentMethod.CREDIT_CARD, Status.COMPLETED);
        Order order2 = new Order("2", LocalDate.now(), "Mouse", Category.ELECTRONICS, 50, 1, 50, "Ivan", "Sofia",
            PaymentMethod.CREDIT_CARD, Status.COMPLETED);
        Order order3 =
            new Order("3", LocalDate.now(), "Book", Category.BOOKS, 20, 1, 20, "Petar", "Plovdiv",
                PaymentMethod.CREDIT_CARD,
                Status.COMPLETED);

        List<Order> orders = List.of(order1, order2, order3);

        analyzer = new OrderAnalyzerImpl(orders);
    }

    @Test
    void testOrdersByCustomerThrowsIllegalArgumentExceptionWhenCustomerNull() {
        assertThrows(IllegalArgumentException.class, () -> analyzer.ordersByCustomer(null),
            "Expected ordersByCustomer method to throw IllegalArgumentException when customer is null");
    }

    @Test
    void testOrdersByCustomerReturnsCorrectOrdersForExistingCustomer() {
        List<Order> result = analyzer.ordersByCustomer("Ivan");

        assertEquals(2, result.size());

        assertTrue(result.stream().allMatch(o -> o.customerName().equals("Ivan")));
    }

    @Test
    void testOrdersByCustomerReturnsEmptyListForNonExistentCustomer() {
        List<Order> result = analyzer.ordersByCustomer("Antonio");

        assertTrue(result.isEmpty(), "Should return empty list for unknown customer");
    }

    @Test
    void testDateWithMostOrdersReturnsEntry() {
        List<Order> orders = List.of(
            new Order("1", LocalDate.of(2023, 1, 1), "P1", Category.ELECTRONICS, 10, 1, 10, "C1", "L1",
                PaymentMethod.CREDIT_CARD, Status.COMPLETED),
            new Order("2", LocalDate.of(2023, 1, 1), "P2", Category.ELECTRONICS, 10, 1, 10, "C1", "L1",
                PaymentMethod.CREDIT_CARD, Status.COMPLETED),
            new Order("3", LocalDate.of(2023, 1, 5), "P3", Category.ELECTRONICS, 10, 1, 10, "C1", "L1",
                PaymentMethod.CREDIT_CARD, Status.COMPLETED)
        );
        analyzer = new OrderAnalyzerImpl(orders);

        Map.Entry<LocalDate, Long> result = analyzer.dateWithMostOrders();

        assertNotNull(result);
        assertEquals(LocalDate.of(2023, 1, 1), result.getKey());
        assertEquals(2L, result.getValue());
    }

    @Test
    void testRevenueByCategoryReturnsMap() {
        List<Order> orders = List.of(
            new Order("1", LocalDate.now(), "Laptop", Category.ELECTRONICS, 100, 1, 100.0, "C1", "L1",
                PaymentMethod.CREDIT_CARD, Status.COMPLETED),
            new Order("2", LocalDate.now(), "Mouse", Category.ELECTRONICS, 200, 1, 200.0, "C1", "L1",
                PaymentMethod.CREDIT_CARD, Status.COMPLETED),
            new Order("3", LocalDate.now(), "Book", Category.BOOKS, 50, 1, 50.0, "C1", "L1", PaymentMethod.CREDIT_CARD,
                Status.COMPLETED)
        );
        analyzer = new OrderAnalyzerImpl(orders);

        Map<Category, Double> result = analyzer.revenueByCategory();

        assertEquals(2, result.size(), "Should contain exactly 2 categories");
        assertEquals(300.0, result.get(Category.ELECTRONICS), 0.001, "Electronics revenue should be sum of orders");
        assertEquals(50.0, result.get(Category.BOOKS), 0.001, "Books revenue should be sum of orders");

        assertFalse(result.containsKey(Category.HOME_APPLIANCES),
            "Categories with no orders should not be in the map");
    }

    @Test
    void testTopNMostOrderedProductsThrowsIllegalArgumentExceptionWhenCustomerNull() {
        assertThrows(IllegalArgumentException.class, () -> analyzer.topNMostOrderedProducts(-1),
            "Expected topNMostOrderedProducts method to throw IllegalArgumentException when n is negative");
    }

    @Test
    void testTopNMostOrderedProductsReturnsMap() {
        List<Order> orders = List.of(
            new Order("1", LocalDate.now(), "B", Category.ELECTRONICS, 10, 1, 10, "C1", "L1", PaymentMethod.CREDIT_CARD,
                Status.COMPLETED),
            new Order("2", LocalDate.now(), "B", Category.ELECTRONICS, 10, 1, 10, "C1", "L1", PaymentMethod.CREDIT_CARD,
                Status.COMPLETED),
            new Order("3", LocalDate.now(), "A", Category.ELECTRONICS, 10, 1, 10, "C1", "L1", PaymentMethod.CREDIT_CARD,
                Status.COMPLETED),
            new Order("4", LocalDate.now(), "A", Category.ELECTRONICS, 10, 1, 10, "C1", "L1", PaymentMethod.CREDIT_CARD,
                Status.COMPLETED)
        );
        analyzer = new OrderAnalyzerImpl(orders);

        List<String> result = analyzer.topNMostOrderedProducts(2);

        assertEquals(2, result.size());
        assertEquals("A", result.get(0),
            "Alphabetically 'A' should come before 'B' when counts are equal");
        assertEquals("B", result.get(1));
    }

    @Test
    void testMostUsedPaymentMethodTieBreakerAlphabetical() {
        List<Order> orders = List.of(
            new Order("1", LocalDate.now(), "Book1", Category.BOOKS, 10, 1, 10, "C1", "L1", PaymentMethod.CREDIT_CARD, Status.COMPLETED),
            new Order("2", LocalDate.now(), "Book2", Category.BOOKS, 10, 1, 10, "C1", "L1", PaymentMethod.AMAZON_PAY, Status.COMPLETED)
        );
        analyzer = new OrderAnalyzerImpl(orders);

        Map<Category, PaymentMethod> result = analyzer.mostUsedPaymentMethodForCategory();

        assertEquals(PaymentMethod.AMAZON_PAY, result.get(Category.BOOKS),
            "In a tie, AMAZON_PAY should win over CREDIT_CARD because it comes first alphabetically");
    }

}