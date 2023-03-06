package marmara;

/**
 * This class states as Reservation data structure
 */
public class Reservation {
    private final Long id;
    private final String room;
    private final String activity;
    private final int hour, day, duration;

    public Reservation(Long id, String room, String activity, int day, int hour, int duration) throws IllegalArgumentException {
        this.id = id;
        this.room = room;
        this.activity = activity;
        this.hour = hour;
        this.day = day;
        this.duration = duration;
    }

    public Long getId() {
        return id;
    }

    public String getRoom() {
        return room;
    }

    public String getActivity() {
        return activity;
    }

    public int getHour() {
        return hour;
    }

    public int getDay() {
        return day;
    }

    public int getDuration() {
        return duration;
    }
}
