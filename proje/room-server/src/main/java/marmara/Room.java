package marmara;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This class charges the Room data structure and operates all the room related actions.
 */
public class Room {
    private final String name;

    /**
     * The first integer keys indicate to the days.
     * The first map holds the hour availabilities of the day.
     * The first map's keys indicate to the hours.
     * The first map's values hold the availability of the hour.
     */
    private final ConcurrentHashMap<Integer, ConcurrentHashMap<Integer, Boolean>> availability = new ConcurrentHashMap<>();

    /**
     * Creates the room by passing name
     *
     * @param name is the name of the room
     */
    public Room(String name) {
        this.name = name;
        for (int i = 1; i <= 7; ++i) {
            ConcurrentHashMap<Integer, Boolean> hours = new ConcurrentHashMap<>();

            for (int j = 9; j <= 17; ++j) {
                hours.put(j, true);
            }

            availability.put(i, hours);
        }
    }

    /**
     * Gives the name of the room
     *
     * @return the name of the room
     */
    public String getName() {
        return name;
    }

    /**
     * Book reservation
     *
     * @param day      is the day
     * @param hour     is the beginning hour
     * @param duration is the duration
     * @return true if reservation booked, false otherwise
     * @throws IllegalArgumentException in case of the day passed by out of the range [1-7],
     *                                  in case of the hour passed by out of the range [9-17]
     */
    public boolean reserve(int day, int hour, int duration) throws IllegalArgumentException {
        if (day < 1 || day > 7) throw new IllegalArgumentException("The day must one of the [1, 7] range.");
        if (hour < 9 || hour > 17) throw new IllegalArgumentException("The hour must one of the [9, 17] range.");

        ConcurrentHashMap<Integer, Boolean> hours = availability.get(day);

        // check all the demanded hours
        // in case of any unavailability occurred then the reservation couldn't be booked
        for (int i = 0; i < duration; ++i) {
            if (hours.get(hour + i) != null) {
                if (!hours.get(hour + i)) return false;
            }
        }

        //make unavailable all the included hours in the reservation
        for (int i = 0; i < duration; ++i) {
            if (hours.get(hour + i) != null) {
                availability.get(day).replace(hour + i, false);
            }
        }

        return true;
    }

    /**
     * Finds the available hours of the day
     *
     * @param day the day
     * @return the available hour list
     * @throws IllegalArgumentException in case of the day passed by out of the range [1-7]
     */
    public List<Integer> getAvailableHours(int day) throws IllegalArgumentException {
        if (day < 1 || day > 7) throw new IllegalArgumentException("The day must one of the [1, 7] range.");

        List<Integer> hours = new ArrayList<>();
        ConcurrentHashMap<Integer, Boolean> hoursOfTheDay = availability.get(day);

        for (int i = 9; i <= 17; ++i) {
            if (hoursOfTheDay.get(i)) {
                hours.add(i);
            }
        }

        return hours;
    }
}
