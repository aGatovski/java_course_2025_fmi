package bg.sofia.uni.fmi.mjt.eventbus.subscribers;

import bg.sofia.uni.fmi.mjt.eventbus.events.Event;

import java.util.Comparator;

public class EventPriorityComparator implements Comparator<Event<?>> {
    @Override
    public int compare(Event<?> event1, Event<?> event2) {
        int event1Priority = event1.getPriority();
        int event2Priority = event2.getPriority();

        //Get an iterator for the unprocessed events. The iterator should provide the events sorted
        //by priority, with higher-priority events first (lower priority number = higher priority).
        if (event1Priority != event2Priority) {
            return Integer.compare(event1Priority, event2Priority);
        }

        //For events with equal priority, earlier events (by timestamp) come first.
        return event1.getTimestamp().compareTo(event2.getTimestamp());
    }
}
