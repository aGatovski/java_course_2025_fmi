package bg.sofia.uni.fmi.mjt.order.domain;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public record Order(String id, LocalDate date, String product, Category category, double price, int quantity,
                    double totalSales, String customerName, String customerLocation, PaymentMethod paymentMethod,
                    Status status) {

    private static final int EXPECTED_COLLUM_COUNT = 11;
    private static final String DELIMITER = ",";

    public static Order of(String line) {
        if (line == null || line.isBlank()) {
            throw new IllegalArgumentException("CSV line cannot be null or empty!");
        }

        String[] tokens = line.split(DELIMITER);

        if (tokens.length != EXPECTED_COLLUM_COUNT) {
            throw new IllegalArgumentException(
                String.format("Invalid CSV line. Expected 11 fields but found %d", tokens.length));
        }

        int i = 0;
        String idNewOrder = tokens[i++];
        LocalDate dateNewOrder = LocalDate.parse(tokens[i++], DateTimeFormatter.ofPattern("dd-MM-yy"));
        String productNewOrder = tokens[i++];
        Category categoryNewOrder = Category.valueOf(tokens[i++].toUpperCase().replace(" ", "_"));
        double priceNewOrder = Double.parseDouble(tokens[i++]);
        int quantityNewOrder = Integer.parseInt(tokens[i++]);
        double totalSalesNewOrder = Double.parseDouble(tokens[i++]);
        String customerNameNewOrder = tokens[i++];
        String customerLocationNewOrder = tokens[i++];
        PaymentMethod paymentMethodNewOrder =
            PaymentMethod.valueOf(tokens[i++].toUpperCase().toUpperCase().replace(" ", "_"));
        Status statusNewOrder = Status.valueOf(tokens[i++].toUpperCase());

        return new Order(idNewOrder, dateNewOrder, productNewOrder, categoryNewOrder, priceNewOrder, quantityNewOrder,
            totalSalesNewOrder, customerNameNewOrder, customerLocationNewOrder, paymentMethodNewOrder, statusNewOrder);
    }
}
