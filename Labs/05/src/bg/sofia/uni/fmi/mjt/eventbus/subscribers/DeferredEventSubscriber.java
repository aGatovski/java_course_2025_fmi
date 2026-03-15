package bg.sofia.uni.fmi.mjt.eventbus.subscribers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import bg.sofia.uni.fmi.mjt.eventbus.events.Event;

public class DeferredEventSubscriber<T extends Event<?>> implements Subscriber<T>, Iterable<T> {
    private List<T> unprocessedEvents;

    public DeferredEventSubscriber() {
        unprocessedEvents = new ArrayList<>();
    }

    /**
     * Store an event for processing at a later time.
     *
     * @param event the event to be processed
     * @throws IllegalArgumentException if the event is null
     */
    @Override
    public void onEvent(T event) {
        if (event == null) {
            throw new IllegalArgumentException("Event cannot be null!");
        }

        unprocessedEvents.add(event);
    }

    /**
     * Get an iterator for the unprocessed events. The iterator should provide the events sorted
     * by priority, with higher-priority events first (lower priority number = higher priority).
     * For events with equal priority, earlier events (by timestamp) come first.
     *
     * @return an iterator for the unprocessed events
     */
    @Override
    public Iterator<T> iterator() {
        List<T> unprocessedEventsCopy = new ArrayList<>(unprocessedEvents);
        Collections.sort(unprocessedEventsCopy, new EventPriorityComparator());

        return unprocessedEventsCopy.iterator();
    }

    /**
     * Check if there are unprocessed events.
     *
     * @return true if there are no unprocessed events, false otherwise
     */
    public boolean isEmpty() {
        return unprocessedEvents.isEmpty();
    }

}