package marmara;

import java.io.BufferedWriter;
import java.io.IOException;

public class ResponseService {
    private final BufferedWriter out;

    public ResponseService(BufferedWriter out) {
        this.out = out;
    }

    public void send(Status status, String title, String body) throws IOException {
        System.out.println("Responded: " + status.code + ", " + title + " " + body);
        out.write("HTTP/1.1 " + status.code + " \r\n");
        out.write("Content-Type: text/html\r\n");
        out.write("\r\n");
        out.write("<HTML><HEAD><TITLE>" + title + "</TITLE></HEAD><BODY>" + body + "</BODY></HTML>");
    }

    public enum Status {
        OK("200"), BAD_REQUEST("400"), FORBIDDEN("403"), NOT_FOUND("404");

        private final String code;

        Status(String code) {
            this.code = code;
        }

        public static Status generate(String code) {
            if (code == null) return null;

            switch (code) {
                case "200":
                    return Status.OK;
                case "400":
                    return Status.BAD_REQUEST;
                case "403":
                    return Status.FORBIDDEN;
                case "404":
                    return Status.NOT_FOUND;
            }
            return null;
        }
    }
}
