import java.io.*;
import java.net.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SimpleWebServer {
    private static String directoryPath = null;
    private static String directoryPage = null;
    private static final int coreCount = Runtime.getRuntime().availableProcessors();

    public static void main(String[] args) throws IOException {
        int port = Integer.parseInt(args[0]);
        System.out.println("YoU SeLeCt PoRt NuMbEr:" + args[0]);

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        if (args.length == 2 && args[1].equals("-p")) {
            System.out.print("Enter the directory path to serve files: ");
            directoryPath = reader.readLine();
        } else {
            try {
                // File currentFile = new File(SimpleWebServer.class.getProtectionDomain().getCodeSource().getLocation().toURI());
                directoryPath = new File("").getAbsolutePath() + "/test/";
                directoryPage = new File("").getAbsolutePath() + "/page/";
            } catch (Exception e) {
                System.err.println("Error retrieving file location: " + e.getMessage());
            }
        }

        File directory = new File(directoryPath);
        if (!directory.exists() || !directory.isDirectory()) {
            System.out.println(directoryPath);
            System.out.println("Invalid directory path! Exiting...");
            return;
        }

        ExecutorService threadPool = Executors.newFixedThreadPool(2 * coreCount);
        try(ServerSocket serverSocket = new ServerSocket(port)){

            System.out.println("Server is listening on port " + port);
            
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected");
                threadPool.execute(new ClientHandler(clientSocket, directoryPath, directoryPage));
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }finally{
            threadPool.shutdown();
        }
    }
}
