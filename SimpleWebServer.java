import java.io.*;
import java.net.*;

public class SimpleWebServer extends Thread {
    private static String directoryPage = null;
    private static String directoryPath = null;
    private Socket clientSocket;

    SimpleWebServer(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    public static void main(String[] args) throws IOException {
        int port = Integer.parseInt(args[0]);
        System.out.println("YoU SeLeCt PoRt NuMbEr:" + args[0]);

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        // if user want to set path by own then -p tag
        if (args.length == 2 && args[1].equals("-p")) {
            System.out.print("Enter the directory path to serve files: ");
            directoryPath = reader.readLine();
        } else {
            try {
                // Get the absolute path of the current file
                File currentFile = new File(
                        SimpleWebServer.class.getProtectionDomain().getCodeSource().getLocation().toURI());
                directoryPath = currentFile.getParent() + "/test/";
                directoryPage = currentFile.getParent() + "/page/";
            } catch (Exception e) {
                System.err.println("Error retrieving file location: " + e.getMessage());
            }
        }

        // Checking is directory exists
        File directory = new File(directoryPath);
        if (!directory.exists() || !directory.isDirectory()) {
            System.out.println(directoryPath);
            System.out.println("Invalid directory path! Exiting...");
            return;
        }
        // reserving a port
        ServerSocket serverSocket = new ServerSocket(port);
        System.out.println("Server is listening on port " + port);

        while (true) {
            Socket clientSocket = serverSocket.accept(); // listning on that port
            System.out.println("Client connected");
            new SimpleWebServer(clientSocket).start(); // start a single thread

        }

    }
    public void run() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream());
    
            // Read HTTP request
            String line;
            String requestedFile = "/index.html";
            String requestedParam = null;
            int contentLength = 0;
    
            // Parse headers
            while ((line = in.readLine()) != null && !line.isEmpty()) {
               System.out.println(line); // Print headers
                if (line.startsWith("GET")) {
                    String[] parts = line.split(" ");
                    if (parts.length > 1) {
                        if (parts[1].contains("?")) {
                            String[] temp = parts[1].split("\\?", 2); // Split into path and parameters
                            requestedFile = temp[0];
                            requestedParam = temp[1];
                            System.out.println("File: " + requestedFile);
                            System.out.println("Parameters: " + requestedParam);
                        } else {
                            requestedFile = parts[1];
                        }
                    }
                } else if (line.startsWith("POST")) {
                    String[] parts = line.split(" ");
                    if (parts.length > 1) {
                        requestedFile = parts[1];
                        System.out.println("File: " + requestedFile);
                    }
                } else if (line.startsWith("Content-Length:")) {
                    contentLength = Integer.parseInt(line.split(":")[1].trim()); // store length of body
                }
            }
    
            // Read POST body if present
            String requestBody = null;
            if (contentLength > 0) {
                char[] body = new char[contentLength];
                in.read(body, 0, contentLength); // Read the exact number of bytes
                requestBody = new String(body);
                System.out.println("Request Body:\n" + requestBody); // prnit json logs
            }
    
            // Serve the requested file
            File fileToServe = new File(directoryPath + requestedFile);
            if (fileToServe.exists() && fileToServe.isFile()) {
                StringBuilder fileContent = new StringBuilder();
                try (BufferedReader fileReader = new BufferedReader(new FileReader(fileToServe))) {
                    String fileLine;
                    while ((fileLine = fileReader.readLine()) != null) {
                        fileContent.append(fileLine).append("\n");
                    }
                }
    
                // Checking which content type isit
                String contentType = "text/html";
                if (requestedFile.endsWith(".json")) {
                    contentType = "application/json";
                }
    
                // Send HTTP response
                String httpResponse = "HTTP/1.1 200 OK\r\n" +
                        "Content-Type: " + contentType + "\r\n\r\n" +
                        fileContent.toString();
                out.write(httpResponse);
            } else {
                // Send 404 response
                File errorPage = new File(directoryPage + "/error404.html");
    
                StringBuilder fileContent = new StringBuilder();
                try (BufferedReader fileReader = new BufferedReader(new FileReader(errorPage))) {
                    String fileLine;
                    while ((fileLine = fileReader.readLine()) != null) {
                        fileContent.append(fileLine).append("\n");
                    }
                }
    
                String httpResponse = "HTTP/1.1 404 Not Found\r\n" +
                        "Content-Type: text/html\r\n\r\n" +
                        fileContent.toString();
                out.write(httpResponse);
            }
    
            out.flush();
            clientSocket.close();
        } catch (Exception e) {
            System.out.println("Client Thread error: " + e.getMessage());
        }
    }
    
}
