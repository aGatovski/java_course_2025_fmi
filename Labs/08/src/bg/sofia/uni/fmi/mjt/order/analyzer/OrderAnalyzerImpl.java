package bg.sofia.uni.fmi.mjt.order.analyzer;

import bg.sofia.uni.fmi.mjt.order.domain.Category;
import bg.sofia.uni.fmi.mjt.order.domain.Order;
import bg.sofia.uni.fmi.mjt.order.domain.PaymentMethod;
import bg.sofia.uni.fmi.mjt.order.domain.Status;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class OrderAnalyzerImpl implements OrderAnalyzer {
    private static final double SUSPICOUS_SALES_VALUES = 100.0;
    private static final int SUSPICIOUS_NUMBER_INTERACTIONS = 3;
    private final List<Order> orders;

    public OrderAnalyzerImpl(List<Order> orders) {
        this.orders = List.copyOf(orders);
    }

    @Override
    public List<Order> allOrders() {
        return List.copyOf(orders);
    }

    @Override
    public List<Order> ordersByCustomer(String customer) {
        if (customer == null || customer.isBlank()) {
            throw new IllegalArgumentException("Customer cannot be null or blank!");
        }

        return orders.stream().filter(order -> order.customerName().equals(customer)).toList();
    }

    @Override
    public Map.Entry<LocalDate, Long> dateWithMostOrders() {
        Map<LocalDate, Long> result =
            orders.stream().collect(Collectors.groupingBy(Order::date, Collectors.counting()));

        return result.entrySet().stream().max((entry1, entry2) -> {
            int resultCompare = entry1.getValue().compareTo(entry2.getValue());

            if (resultCompare == 0) {
                return entry2.getKey().compareTo(entry1.getKey());
            }

            return resultCompare;
        }).orElse(null);
    }

    @Override
    public List<String> topNMostOrderedProducts(int n) {
        if (n < 0) {
            throw new IllegalArgumentException("N cannot be less than 0!");
        }

        Map<String, Long> result =
            orders.stream().collect(Collectors.groupingBy(Order::product, Collectors.counting()));

        return result.entrySet().stream().sorted((entry1, entry2) -> {
            int resultCompare = entry2.getValue().compareTo(entry1.getValue());

            if (resultCompare == 0) {
                return entry1.getKey().compareTo(entry2.getKey());
            }

            return resultCompare;
        }).limit(n).map(Map.Entry::getKey).toList();
    }

    @Override
    public Map<Category, Double> revenueByCategory() {
        return orders.stream()
            .collect(Collectors.groupingBy(Order::category, Collectors.summingDouble(Order::totalSales)));
    }

    @Override
    public Set<String> suspiciousCustomers() {
        Map<String, Long> result = orders.stream().filter(order -> order.status().equals(Status.CANCELLED))
            .filter(order -> order.totalSales() < SUSPICOUS_SALES_VALUES)
            .collect(Collectors.groupingBy(Order::customerName, Collectors.counting()));

        return result.entrySet().stream()
            .filter(entry -> entry.getValue() > SUSPICIOUS_NUMBER_INTERACTIONS)
            .map(Map.Entry::getKey).collect(Collectors.toSet());
    }

    @Override
    public Map<Category, PaymentMethod> mostUsedPaymentMethodForCategory() {
        Map<Category, Map<PaymentMethod, Long>> groupedCategoryPayments = orders.stream().collect(
            Collectors.groupingBy(Order::category, Collectors.groupingBy(Order::paymentMethod, Collectors.counting())));

        return groupedCategoryPayments.entrySet().stream()
            .collect(Collectors.toMap(Map.Entry::getKey, entry -> {
                Map<PaymentMethod, Long> entryInnerValue = entry.getValue();

                return entryInnerValue.entrySet().stream().max((entry1, entry2) -> {
                    int resultCompare = entry1.getValue().compareTo(entry2.getValue());

                    if (resultCompare == 0) {
                        return entry2.getKey().name().compareTo(entry1.getKey().name());
                    }

                    return resultCompare;
                }).map(Map.Entry::getKey).get(); //Payment method
            })); // Category,Payment method
    }

    @Override
    public String locationWithMostOrders() {
        Map<String, Long> result =
            orders.stream().collect(Collectors.groupingBy(Order::customerLocation, Collectors.counting()));

        return result.entrySet().stream().max((e1, e2) -> {
            int resultCompare = e1.getValue().compareTo(e2.getValue());

            if (resultCompare == 0) {
                return e2.getKey().compareTo(e1.getKey());
            }
            return resultCompare;
        }).map(Map.Entry::getKey).orElse(null);
    }

    @Override
    public Map<Category, Map<Status, Long>> groupByCategoryAndStatus() {
        return orders.stream().collect(
            Collectors.groupingBy(Order::category, Collectors.groupingBy(Order::status, Collectors.counting())));
    }
}
