package marmara;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * This class operates the server actions. The server manages demanding request by sending them the pool threads
 */
class Server implements Runnable {
    private final ExecutorService pool;
    private final ServerSocket serverSocket;

    private final Database db = new Database();

    /**
     * Creates the server and its affiliates
     *
     * @param port     server's port
     * @param poolSize server's pool size
     * @throws IOException if an I/O error occurs when opening the socket
     */
    public Server(int port, int poolSize) throws IOException {
        serverSocket = new ServerSocket(port);

        // create the pool
        pool = Executors.newFixedThreadPool(poolSize);

        System.out.println("Room server started on port: " + port);

        //we should add hook for graceful shutdown
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                System.out.println("Room server shutting down...");
                // Disable new tasks from being submitted
                pool.shutdown();
                try {
                    // Wait a while for existing tasks to terminate
                    if (!pool.awaitTermination(5, TimeUnit.SECONDS)) {
                        // Cancel currently executing tasks
                        pool.shutdownNow();

                        // Wait a while for tasks to respond to being cancelled
                        if (!pool.awaitTermination(5, TimeUnit.SECONDS)) {
                            System.err.println("Pool couldn't not terminate");
                        }
                    }
                } catch (InterruptedException ie) {
                    // (Re-)Cancel if current thread also interrupted
                    pool.shutdownNow();
                    // Preserve interrupt status
                    Thread.currentThread().interrupt();
                }
            }
        });
    }

    public void run() {
        try {
            for (; ; ) {
                //Invoke new handler for the request.
                pool.execute(new SocketHandler(serverSocket.accept(), db));
            }
        } catch (IOException ex) {
            pool.shutdown();
        }
    }
}
