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

        if ((matcher = Pattern.compile("^/check\\?name=([\\w-]+)$").matcher(requestLine[1])).matches()) {
            String name = matcher.group(1);
            checkPage(name);
            return;
        }

        errorPage();
    }

    private void errorPage() throws IOException {
        responseService.send(Status.NOT_FOUND, "Error", "Page not found!");
    }

    private void addPage(String name) throws IOException {
        boolean result = db.add(name);

        if (result)
            responseService.send(Status.OK, "Activity Added", "Activity with name " + name + " is successfully added.");
        else
            responseService.send(Status.FORBIDDEN, "Activity Already Exists", "Activity with name " + name + " already exists.");
    }

    private void removePage(String name) throws IOException {
        boolean result = db.remove(name);

        if (result)
            responseService.send(Status.OK, "Activity Removed", "Activity with name " + name + " is successfully removed.");
        else responseService.send(Status.FORBIDDEN, "Error", "Activity with name " + name + " is not found.");
    }

    private void checkPage(String name) throws IOException {
        Activity activity = db.get(name);

        if (activity != null)
            responseService.send(Status.OK, "Activity Exists", "Activity with name " + name + " exists.");
        else
            responseService.send(Status.NOT_FOUND, "Activity Doesn't Exist", "Activity with name " + name + " is not found.");
    }
}
