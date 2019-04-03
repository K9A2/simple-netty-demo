import client.Client;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import server.Server;

public class Main {

    private static final Logger logger = LogManager.getLogger();

    public static void main(String[] args) {

        if (args == null || args.length == 0) {
            logger.error("Please specify whether run as server or client");
            System.exit(1);
        }
        if (args[0].equals("-s")) {
            runServer();
        } else if (args[0].equals("-c")) {
            runClient();
        }

    }

    private static void runServer() {
        logger.info("Run as server");
        try {
            new Server(9090).run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void runClient() {
        logger.info("Run as client");
        new Client().run();
    }

}
