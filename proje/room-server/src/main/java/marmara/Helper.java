package marmara;

public class Helper {
    public static String getDayName(int day) throws IllegalArgumentException {
        switch (day) {
            case 1:
                return "Monday";
            case 2:
                return "Tuesday";
            case 3:
                return "Wednesday";
            case 4:
                return "Thursday";
            case 5:
                return "Friday";
            case 6:
                return "Saturday";
            case 7:
                return "Sunday";
            default:
                throw new IllegalArgumentException("The day must one of the [1, 7] range.");
        }
    }
}
