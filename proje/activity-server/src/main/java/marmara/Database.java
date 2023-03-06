package marmara;

import java.io.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This class states as a database
 */
public class Database {
    private final ConcurrentHashMap<String, Activity> data = new ConcurrentHashMap<>();

    /**
     * Adds new activity to the database
     *
     * @param name the name of the new activity
     * @return true if activity added, false if the activity exists
     * @throws NullPointerException if name passed as NULL
     */
    public boolean add(String name) throws NullPointerException {
        if (name == null) throw new NullPointerException("The name must not be null");

        Activity activity = new Activity(name);
        return data.putIfAbsent(name, activity) == null;
    }

    /**
     * Removes an activity
     *
     * @param name the name of the activity
     * @return true if activity removed, false if the activity doesn't exist
     * @throws NullPointerException if name passed as NULL
     */
    public boolean remove(String name) throws NullPointerException {
        if (name == null) throw new NullPointerException("The name must not be null");

        return data.remove(name) != null;
    }

    /**
     * Finds an activity
     *
     * @param name the name of the activity
     * @return the activity if the room found, null otherwise
     * @throws NullPointerException if name passed as NULL
     */
    public Activity get(String name) throws NullPointerException {
        if (name == null) throw new NullPointerException("The name must not be null");

        return data.get(name);
    }
}
