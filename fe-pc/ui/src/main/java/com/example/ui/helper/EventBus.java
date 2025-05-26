package com.example.ui.helper;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

// Một class EventBus cơ bản
public class EventBus {
    private static final List<Consumer<String>> listeners = new ArrayList<>();

    public static void register(Consumer<String> listener) {
        listeners.add(listener);
    }

    public static void post(String event) {
        for (Consumer<String> listener : listeners) {
            listener.accept(event);
        }
    }
}
