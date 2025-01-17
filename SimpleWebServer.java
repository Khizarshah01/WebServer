import java.io.*;
import java.net.*;

public class SimpleWebServer {
    public static void main(String[] args) throws IOException {
        int port = Integer.parseInt(args[0]);
        System.out.println("YoU SeLeCt PoRt NuMbEr:" + args[0]);

        
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String directoryPath = null;
        String directoryPage = null;
 
        // if user want to se path by own then -p tag
        if (args.length == 2 && args[1].equals("-p")) {
            System.out.print("Enter the directory path to serve files: ");
            directoryPath = reader.readLine();
        }
        else{
            try {
                // Get the absolute path of the current file
                File currentFile = new File(SimpleWebServer.class.getProtectionDomain().getCodeSource().getLocation().toURI());
                directoryPath =  currentFile.getParent()+"/test/";
                directoryPage = currentFile.getParent()+"/page/";
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
        // reserving a port from Os
        ServerSocket serverSocket = new ServerSocket(port);
        System.out.println("Server is listening on port " + port);

        while (true) {
            Socket clientSocket = serverSocket.accept(); // listning on that port
            System.out.println("Client connected");

            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream());

            // Read HTTP request
            String line;
            String requestedFile = "/index.html";
            while (!(line = in.readLine()).isEmpty()) {
                System.out.println(line);
                if (line.startsWith("GET")) {
                    String[] parts = line.split(" ");
                    if (parts.length > 1) {
                        requestedFile = parts[1];
                    }
                }
            }

            // serving that particular .html file
            File fileToServe = new File(directoryPath + requestedFile);
            if (fileToServe.exists() && fileToServe.isFile()) {
                // Read file content
                StringBuilder fileContent = new StringBuilder();
                try (BufferedReader fileReader = new BufferedReader(new FileReader(fileToServe))) {
                    String fileLine;
                    while ((fileLine = fileReader.readLine()) != null) {
                        fileContent.append(fileLine).append("\n");
                    }
                }

                // making response with status code 

                // Send HTTP response
                String httpResponse = "HTTP/1.1 200 OK\r\n" +
                                      "Content-Type: text/html\r\n\r\n" +
                                      fileContent.toString();
                out.write(httpResponse);
            } else {
                // Send 404 response
                File fileToError = new File(directoryPage+"/error404.html");

                StringBuilder fileContent = new StringBuilder();
                try (BufferedReader fileReader = new BufferedReader(new FileReader(fileToError))) {
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
        }
    }
}
