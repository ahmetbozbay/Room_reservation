package marmara;

/**
 * This class charges the activity data structure
 */
public class Activity {
    private final String name;

    /**
     * Creates the activity by passing name
     *
     * @param name of the activity
     */
    public Activity(String name) {
        this.name = name;
    }

    /**
     * Name getter
     *
     * @return name of the activity
     */
    public String getName() {
        return name;
    }
}
