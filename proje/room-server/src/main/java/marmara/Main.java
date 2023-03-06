package marmara;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        if (args.length == 0) new Server(8081, 80).run();
        else new Server(Integer.parseInt(args[0]), 80).run();
    }
}