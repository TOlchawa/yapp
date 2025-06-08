package integration;

import org.junit.jupiter.api.Assumptions;

import java.io.IOException;
import java.net.Socket;

/** Utility to skip integration tests if the server is not running. */
public final class IntegrationTestUtil {
    private static final String HOST = "127.0.0.1";
    private static final int PORT = 9090;

    private IntegrationTestUtil() {}

    public static void assumeServerRunning() {
        try (Socket socket = new Socket(HOST, PORT)) {
            // connection successful
        } catch (IOException e) {
            Assumptions.assumeTrue(false, "Server not running on " + HOST + ":" + PORT);
        }
    }
}
