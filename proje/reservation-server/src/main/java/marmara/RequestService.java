package marmara;

import marmara.ResponseService.Status;

import java.io.*;
import java.net.Socket;

public class RequestService {
    public static int RoomServerPort = 8081;
    public static int ActivityServerPort = 8082;

    /**
     * Sends a request to Activity server to check activity existence
     *
     * @param activity is the activity name
     * @return Status code, fetched from activity server
     */
    public Status checkActivityExistence(String activity) {
        if (activity == null) throw new IllegalArgumentException("The activity name can not passed as null.");

        try (Socket socket = new Socket("localhost", ActivityServerPort)) {
            String s;
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            out.write(String.format("GET /check?name=%s HTTP/1.1\r\n", activity));
            out.flush();

            s = in.readLine();

            out.close();
            in.close();

            return Status.generate(s.split(" ")[1]);

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    /**
     * Sends a request to room server to book a reservation
     *
     * @param room     is the room name
     * @param day      is the day
     * @param hour     is the book hour
     * @param duration is the book duration
     * @return Status code, fetched from room server
     */
    public Status tryRoomReservation(String room, int day, int hour, int duration) {
        if (room == null) throw new IllegalArgumentException("The room name can not passed as null.");

        try (Socket socket = new Socket("localhost", RoomServerPort)) {
            String s;
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            out.write(String.format("GET /reserve?name=%s&day=%d&hour=%d&duration=%d HTTP/1.1\r\n", room, day, hour, duration));
            out.flush();

            s = in.readLine();

            in.close();
            out.close();

            return Status.generate(s.split(" ")[1]);

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    /**
     * Sends a request to room server to get available hours of the day
     *
     * @param room is the room name
     * @param day  is the day
     * @return null if server responded as 404,
     * empty string ("") if server responded 400,
     * Response body message otherwise
     */
    public String getRoomAvailableHoursForADay(String room, int day) {
        if (room == null) throw new IllegalArgumentException("The room name can not passed as null.");

        try (Socket socket = new Socket("localhost", RoomServerPort)) {
            String s;
            StringBuilder builder = new StringBuilder();
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            out.write(String.format("GET /checkavailability?name=%s&day=%d HTTP/1.1\r\n", room, day));
            out.flush();

            while ((s = in.readLine()) != null) {
                builder.append(s);
            }

            in.close();
            out.close();

            s = builder.toString();

            if (s.split(" ")[1].compareTo("404") == 0) return null;
            else if (s.split(" ")[1].compareTo("400") == 0) return "";

            s = s.split("<BODY>")[1];
            return s.split("</BODY>")[0].trim();

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        return null;
    }

    /**
     * Sends a request to room server to get available hours of each day
     *
     * @param room is the room name
     * @return null if server responded as 404,
     * empty string ("") if server responded 400,
     * Concatenation of response body messages otherwise
     */
    public String getRoomAvailableHours(String room) {
        if (room == null) throw new IllegalArgumentException("The room name can not passed as null.");

        StringBuilder result = new StringBuilder();

        for (int i = 1; i <= 7; ++i) {
            String current = getRoomAvailableHoursForADay(room, i);

            if (current == null) return null;
            if (current.compareTo("") == 0) return "";

            result.append(current).append("<BR>");
        }

        return result.toString();
    }
}
