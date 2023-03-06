package marmara;

import javax.xml.crypto.Data;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This class states as a database
 */
public class Database {
    private final ConcurrentHashMap<String, Room> data = new ConcurrentHashMap<>();



    /**
     * Adds new rom to the database
     *
     * @param name the name of the new room
     * @return true if room added, false if the room exists
     * @throws NullPointerException if name passed as NULL
     */
    public boolean add(String name) throws NullPointerException {
        if (name == null) throw new NullPointerException("The name must not be null");

        Room room = new Room(name);
        return data.putIfAbsent(name, room) == null;
    }

    /**
     * Removes a room
     *
     * @param name the name of the room
     * @return true if room removed, false if the room doesn't exist
     * @throws NullPointerException if name passed as NULL
     */
    public boolean remove(String name) throws NullPointerException {
        if (name == null) throw new NullPointerException("The name must not be null");

        return data.remove(name) != null;
    }

    /**
     * Finds a room
     *
     * @param name the name of the room
     * @return the room if the room found, null otherwise
     * @throws NullPointerException if name passed as NULL
     */
    public Room get(String name) throws NullPointerException {
        if (name == null) throw new NullPointerException("The name must not be null");

        return data.get(name);
    }
}
