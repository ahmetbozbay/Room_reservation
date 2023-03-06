package marmara;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * This class states as a database
 */
public class Database {
    private final AtomicLong lastId = new AtomicLong(1);
    private final ConcurrentHashMap<Long, Reservation> data = new ConcurrentHashMap<>();

    /**
     * Adds new reservation to the database
     *
     * @param room     name of the room
     * @param activity name of the activity
     * @param day      is the day
     * @param hour     is the initial hour
     * @param duration is the duration
     * @return true if reservation add, false otherwise
     * @throws IllegalArgumentException if some arguments improperly passed
     */
    public long add(String room, String activity, int day, int hour, int duration) throws IllegalArgumentException {
        if (room == null) throw new IllegalArgumentException("The room name must not be null");
        if (activity == null) throw new IllegalArgumentException("The activity name must not be null");
        if (day < 1 || day > 7) throw new IllegalArgumentException("The day must one of the [1, 7] range.");
        if (hour < 9 || hour > 17) throw new IllegalArgumentException("The hour must one of the [9, 17] range.");

        Reservation reservation = new Reservation(
                lastId.get(),
                room, activity, day, hour, duration);

        data.putIfAbsent(lastId.get(), reservation);
        return lastId.getAndIncrement();
    }

    /**
     * Finds a reservation
     *
     * @param id is the id of the reservation
     * @return the reservation if it is found, null otherwise
     */
    public Reservation get(long id) {
        return data.get(id);
    }
}
