package marmara;

import marmara.ResponseService.Status;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * This class operates HTTP requests.
 */
public class RequestHandler {
    private final Database db;
    private final ResponseService responseService;

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

        if ((matcher = Pattern.compile("^/add\\?name=([\\w-]+)$").matcher(requestLine[1])).matches()) {
            String name = matcher.group(1);
            addPage(name);
            return;
        }

        if ((matcher = Pattern.compile("^/remove\\?name=([\\w-]+)$").matcher(requestLine[1])).matches()) {
            String name = matcher.group(1);
            removePage(name);
            return;
        }

        if ((matcher = Pattern.compile("^/reserve\\?name=([\\w-]+)&day=(\\d+)&hour=(\\d+)&duration=(\\d+)$").matcher(requestLine[1])).matches()) {
            String name = matcher.group(1);
            int day = Integer.parseInt(matcher.group(2));
            int hour = Integer.parseInt(matcher.group(3));
            int duration = Integer.parseInt(matcher.group(4));
            reservePage(name, day, hour, duration);
            return;
        }

        if ((matcher = Pattern.compile("^/checkavailability\\?name=([\\w-]+)&day=(\\d+)$").matcher(requestLine[1])).matches()) {
            String name = matcher.group(1);
            int day = Integer.parseInt(matcher.group(2));
            checkAvailabilityPage(name, day);
            return;
        }

        errorPage();
    }

    private void errorPage() throws IOException {
        responseService.send(Status.NOT_FOUND, "Error", "Page not found!");
    }

    private void addPage(String name) throws IOException {
        boolean result = db.add(name);

        if (result) responseService.send(Status.OK, "Room Added", "Room with name " + name + " is successfully added.");
        else
            responseService.send(Status.FORBIDDEN, "Room Already Exists", "Room with name " + name + " already exists.");
    }

    private void removePage(String name) throws IOException {
        boolean result = db.remove(name);

        if (result)
            responseService.send(Status.OK, "Room Removed", "Room with name " + name + " is successfully removed.");
        else responseService.send(Status.FORBIDDEN, "Error", "Room with name " + name + " is not found.");
    }

    private void reservePage(String name, int day, int hour, int duration) throws IOException {
        Room room = db.get(name);

        if (room == null) {
            responseService.send(Status.NOT_FOUND, "Error", "Room with name " + name + " is not found.");
            return;
        }

        try {
            boolean result = room.reserve(day, hour, duration);

            if (result)
                responseService.send(Status.OK, "Reservation Successful", "Room with name " + name + " is successfully booked " + Helper.getDayName(day) + ", " + hour + ":00 " + "and for " + duration + " hours.");
            else
                responseService.send(Status.FORBIDDEN, "Reservation Refused", "Room with name " + name + " is not available " + Helper.getDayName(day) + ", " + hour + ":00.");

        } catch (IllegalArgumentException e) {
            responseService.send(Status.BAD_REQUEST, "Bad Request", "Some of the parameters were sent improperly");
        }
    }

    private void checkAvailabilityPage(String name, int day) throws IOException {
        Room room = db.get(name);

        if (room == null) {
            responseService.send(Status.NOT_FOUND, "Error", "Room with name " + name + " is not found.");
            return;
        }

        try {
            List<Integer> hours = room.getAvailableHours(day);

            responseService.send(Status.OK, "Available Hours", "On " + Helper.getDayName(day) + ", Room " + name + " is available for the following hours: " + hours.stream().map(String::valueOf).collect(Collectors.joining(" ")));

        } catch (IllegalArgumentException e) {
            responseService.send(Status.BAD_REQUEST, "Bad Request", "Some of the parameters were sent improperly");
        }
    }
}
