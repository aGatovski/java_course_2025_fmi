package bg.sofia.uni.fmi.mjt.eventbus;

import bg.sofia.uni.fmi.mjt.eventbus.events.Event;
import bg.sofia.uni.fmi.mjt.eventbus.exception.MissingSubscriptionException;
import bg.sofia.uni.fmi.mjt.eventbus.subscribers.Subscriber;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventBusImpl implements EventBus {
    //List<Subscriber<?> is basically saying i want a lot of subscribers to some event but the events can differ like
    //LoginEvent LogoutEvent so thats why ?
    //We need a way to keep the eventTypes so we keep the reference to them in Class<?>
    //We do Class<?> not of Class<Event> because the eventType might be again eventTypeA and eventTypeB both Events but
    //we cannot put them in the same place
    private Map<Class<?>, List<Subscriber<?>>> eventBus;
    private Map<Class<?>, List<Event<?>>> eventLogs;

    public EventBusImpl() {
        eventBus = new HashMap<>();
        eventLogs = new HashMap<>();
    }

    @Override
    public <T extends Event<?>> void subscribe(Class<T> eventType, Subscriber<? super T> subscriber) {
        if (eventType == null) {
            throw new IllegalArgumentException("Event type cannot be null!");
        }

        if (subscriber == null) {
            throw new IllegalArgumentException("Subscriber cannot be null!");
        }

        if (eventBus.containsKey(eventType)) { //we have this eventType
            if (!eventBus.get(eventType).contains(subscriber)) { //we do not have this subscriber to it
                eventBus.get(eventType).add(subscriber);
            }
        } else { //we do not have this eventType
            List<Subscriber<?>> subscriberList = new ArrayList<>();
            subscriberList.add(subscriber);
            eventBus.put(eventType, subscriberList);
        }
    }

    @Override
    public <T extends Event<?>> void unsubscribe(Class<T> eventType, Subscriber<? super T> subscriber) {
        if (eventType == null) {
            throw new IllegalArgumentException("Event type cannot be null!");
        }

        if (subscriber == null) {
            throw new IllegalArgumentException("Subscriber cannot be null!");
        }

        //if there is no eventtype in the map there is no subs list
        if (!eventBus.containsKey(eventType) || !eventBus.get(eventType).contains(subscriber)) {
            throw new MissingSubscriptionException("No such subscriber is subscribed to the event type!");
        }

        eventBus.get(eventType).remove(subscriber);
    }

    @Override
    public <T extends Event<?>> void publish(T event) {
        if (event == null) {
            throw new IllegalArgumentException("Event to publish cannot be null!");
        }

        List<Subscriber<?>> subscriberList = eventBus.get(event.getClass());

        if (subscriberList != null) {
            onEventNotifySubscribers(subscriberList, event);
        }

        //track all events that have happened
        if (!eventLogs.containsKey(event.getClass())) {
            List<Event<?>> eventList = new ArrayList<>();
            eventList.add(event);
            eventLogs.put(event.getClass(), eventList);
        } else {
            eventLogs.get(event.getClass()).add(event);
        }
    }

    @Override
    public void clear() {
        eventBus.clear();
        eventLogs.clear();
    }

    @Override
    public Collection<? extends Event<?>> getEventLogs(Class<? extends Event<?>> eventType, Instant from, Instant to) {
        if (eventType == null) {
            throw new IllegalArgumentException("Event type cannot be null!");
        }

        if (from == null) {
            throw new IllegalArgumentException("Start timestamp cannot be null!");
        }

        if (to == null) {
            throw new IllegalArgumentException("End timestamp cannot be null!");
        }

        List<Event<?>> eventsInTimeStamp = new ArrayList<>();

        if (from.compareTo(to) == 0) {
            return Collections.unmodifiableCollection(eventsInTimeStamp);
        }

        findEventsInTimeStamp(eventsInTimeStamp, eventType, from, to);

        return Collections.unmodifiableCollection(eventsInTimeStamp);
    }

    @Override
    public <T extends Event<?>> Collection<Subscriber<?>> getSubscribersForEvent(Class<T> eventType) {
        if (eventType == null) {
            throw new IllegalArgumentException("Event type cannot be null!");
        }

        List<Subscriber<?>> subscribersList = eventBus.get(eventType);

        if (subscribersList == null) {
            List<Subscriber<?>> emptyList = new ArrayList<>();
            return Collections.unmodifiableCollection(emptyList);
        }

        return Collections.unmodifiableCollection(subscribersList);
    }

    private <T extends Event<?>> void onEventNotifySubscribers(List<Subscriber<?>> subscribers, T event) {
        for (Subscriber<?> subscriber : subscribers) {
            //I can do this type casting here because from method publish() I have ensured that the subscribers are only
            //subscribed to Event Type T events however I had to pass them in Subscriber<?> list because I cannot cast
            //the whole List from <Unknown> to <Known>
            Subscriber<T> subscriberOnEventTypeT = (Subscriber<T>) subscriber;
            subscriberOnEventTypeT.onEvent(event);
        }
    }

    private void findEventsInTimeStamp(List<Event<?>> eventsInTimeStamp, Class<? extends Event<?>> eventType,
                                       Instant from, Instant to) {
        List<Event<?>> eventListOfEventType = eventLogs.get(eventType);

        if (eventListOfEventType != null) {
            for (Event<?> event : eventListOfEventType) {
                Instant currentEventTimeStamp = event.getTimestamp();

                if ((currentEventTimeStamp.compareTo(from) >= 0) && (currentEventTimeStamp.compareTo(to) < 0)) {
                    eventsInTimeStamp.add(event);
                }
            }
        }
    }
}
