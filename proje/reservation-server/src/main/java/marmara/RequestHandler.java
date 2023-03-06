package marmara;

import marmara.ResponseService.Status;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class operates HTTP requests.
 */
public class RequestHandler {
    private final Database db;
    private final ResponseService responseService;

    private final RequestService requestService = new RequestService();

    /**
     * Creates request handler
     *
     * @param firstLine of the incoming socket data
     * @param out       is the response socket stream
     * @param db        is the Database
     * @throws IOException if any I/O related error occurs
     */
    public RequestHandler(String firstLine, BufferedWriter out, Database db) throws IOException {
        this.db = db;
        this.responseService = new ResponseService(out);
        Matcher matcher;

        // A request-line begins with a method token, followed by a single space
        // (SP), the request-target, another single space (SP), the protocol
        // version, and ends with CRLF.
        // [https://www.rfc-editor.org/rfc/rfc7230#section-3.1.1]
        String[] requestLine = firstLine.split(" ");

        System.out.println("@" + requestLine[0] + ":" + requestLine[1]);

        if (requestLine[0].compareTo("GET") != 0) {
            errorPage();
            return;
        }

        if ((matcher = Pattern.compile("^/reserve\\?room=([\\w-]+)&activity=([\\w-]+)&day=(\\d+)&hour=(\\d+)&duration=(\\d+)$").matcher(requestLine[1])).matches()) {
            String room = matcher.group(1);
            String activity = matcher.group(2);
            int day = Integer.parseInt(matcher.group(3));
            int hour = Integer.parseInt(matcher.group(4));
            int duration = Integer.parseInt(matcher.group(5));
            reservePage(room, activity, day, hour, duration);
            return;
        }

        if ((matcher = Pattern.compile("^/listavailability\\?room=([\\w-]+)&day=(\\d+)$").matcher(requestLine[1])).matches()) {
            String room = matcher.group(1);
            int day = Integer.parseInt(matcher.group(2));
            listAvailabilityPageWithDay(room, day);
            return;
        }

        if ((matcher = Pattern.compile("^/listavailability\\?room=([\\w-]+)$").matcher(requestLine[1])).matches()) {
            String room = matcher.group(1);
            listAvailabilityPage(room);
            return;
        }

        if ((matcher = Pattern.compile("^/display\\?id=(\\d+)$").matcher(requestLine[1])).matches()) {
            long id = Long.parseLong(matcher.group(1));
            displayPage(id);
            return;
        }

        errorPage();
    }

    private void errorPage() throws IOException {
        responseService.send(Status.NOT_FOUND, "Error", "Page not found!");
    }

    private void reservePage(String room, String activity, int day, int hour, int duration) throws IOException {
        Status activityStatusCode = requestService.checkActivityExistence(activity);

        if (activityStatusCode != Status.OK) {
            responseService.send(Status.NOT_FOUND,
                    "Activity Doesn't Exist", "Activit with name " + activity + " is not found");
        } else {
            Status reservationStatusCode = requestService.tryRoomReservation(room, day, hour, duration);

            switch (reservationStatusCode) {
                case NOT_FOUND:
                    responseService.send(Status.NOT_FOUND, "Error", "Room with name " + room + " is not found.");
                    break;
                case FORBIDDEN:
                    responseService.send(Status.FORBIDDEN, "Reservation Refused", "Room with name " + room + " is not available " + Helper.getDayName(day) + ", " + hour + ":00.");
                    break;
                case BAD_REQUEST:
                    responseService.send(Status.BAD_REQUEST, "Bad Request", "Some of the parameters were sent improperly");
                    break;
                default:
                    long id = db.add(room, activity, day, hour, duration);
                    responseService.send(
                            Status.OK,
                            "Reservation Successful",
                            String.format(
                                    "Room %s is reserved for activity %s on %s %d:00-%d:00. Your Reservation ID is %d.",
                                    room,
                                    activity,
                                    Helper.getDayName(day),
                                    hour,
                                    hour + duration,
                                    id));
            }
        }
    }

    private void listAvailabilityPageWithDay(String room, int day) throws IOException {
        String result = requestService.getRoomAvailableHoursForADay(room, day);

        if (result == null) {
            responseService.send(Status.NOT_FOUND, "Error", "Room with name " + room + " is not found.");
        } else if (result.compareTo("") == 0) {
            responseService.send(Status.BAD_REQUEST, "Bad Request", "Some of the parameters were sent improperly");
        } else {
            responseService.send(Status.OK, "Available Hours", result);
        }
    }

    private void listAvailabilityPage(String room) throws IOException {
        String result = requestService.getRoomAvailableHours(room);

        if (result == null) {
            responseService.send(Status.NOT_FOUND, "Error", "Room with name " + room + " is not found.");
        } else if (result.compareTo("") == 0) {
            responseService.send(Status.BAD_REQUEST, "Bad Request", "Some of the parameters were sent improperly");
        } else {
            responseService.send(Status.OK, "Available Hours", result);
        }
    }

    private void displayPage(long id) throws IOException {
        Reservation reservation = db.get(id);

        if (reservation == null) {
            responseService.send(Status.NOT_FOUND, "Reservation Not Found", "There is no reservation with ID " + id);
        } else {
            responseService.send(
                    Status.OK,
                    "Reservation Info",
                    String.format(
                            "Reservation ID:%d <BR>Room: %s <BR>Activity: %s <BR>When: %s %d:00-%d:00.",
                            id,
                            reservation.getRoom(),
                            reservation.getActivity(),
                            Helper.getDayName(reservation.getDay()),
                            reservation.getHour(),
                            reservation.getHour() + reservation.getDuration()));
        }
    }
}
