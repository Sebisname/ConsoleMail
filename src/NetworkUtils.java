import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;


public class NetworkUtils {
    public static NetworkStates checkStability(String host, int port) {
        long startTime = System.currentTimeMillis();
        try(Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(host, port), 3000);

        } catch (IOException e){
            return NetworkStates.OFFLINE;
        }

        long duration = System.currentTimeMillis() - startTime;

        if (duration < 300) {
            return NetworkStates.STABLE;
        } else if (duration < 1000) {
            return NetworkStates.UNSTABLE;
        } else {
            return NetworkStates.VERY_UNSTABLE;
        }
    }
}
