package KPP.Service;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import java.util.ArrayList;
import java.util.Hashtable;

public class Broker {
    private Hashtable<EventType, ArrayList<EventHandler>> listeners = new Hashtable<>();
    private static Broker instance = new Broker();

    public static <T extends Event> void listen(final EventType<T>[] types, final EventHandler<? super T> handler)
    {
        for (EventType<T> type : types) {
            listen(type, handler);
        }
    }

    public static <T extends Event> void listen(final EventType<T> type, final EventHandler<? super T> handler)
    {
        if (!instance.listeners.containsKey(type)) instance.listeners.put(type, new ArrayList<>());
        instance.listeners.get(type).add(handler);
    }

    public static void dispatch(Event event)
    {
        EventType type = event.getEventType();

        if (instance.listeners.containsKey(type)) {
            for (EventHandler handler : instance.listeners.get(type)) {
                try {
                    handler.handle(event);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (event.isConsumed()) return;
            }
        }
    }
}
