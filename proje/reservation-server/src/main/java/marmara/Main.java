package marmara;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        if (args.length == 3) {
            RequestService.RoomServerPort = Integer.parseInt(args[1]);
            RequestService.ActivityServerPort = Integer.parseInt(args[2]);
            new Server(Integer.parseInt(args[0]), 80).run();
        } else if (args.length == 2) {
            RequestService.RoomServerPort = Integer.parseInt(args[1]);
            new Server(Integer.parseInt(args[0]), 80).run();
        } else if (args.length == 1) {
            new Server(Integer.parseInt(args[0]), 80).run();
        } else {
            new Server(8080, 80).run();
        }
    }
}