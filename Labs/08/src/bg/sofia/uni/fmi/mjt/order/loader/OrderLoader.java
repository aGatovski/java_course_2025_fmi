package bg.sofia.uni.fmi.mjt.order.loader;

import bg.sofia.uni.fmi.mjt.order.domain.Order;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.List;

public class OrderLoader {

    /**
     * Returns a list of orders read from the source Reader.
     *
     * @param reader the Reader with orders
     * @throws IllegalArgumentException if the reader is null
     */
    public static List<Order> load(Reader reader) {
        if (reader == null) {
            throw new IllegalArgumentException("Reader cannot be null!");
        }

        try (BufferedReader bufferedReader = new BufferedReader(reader)) {
            return bufferedReader.lines()
                                 .skip(1)
                                 .map(Order::of)
                                 .toList();
        } catch (IOException e) {
            throw new java.io.UncheckedIOException("Error reading", e);
        }
    }
}