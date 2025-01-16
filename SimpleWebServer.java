import java.io.*;
import java.net.*;

public class SimpleWebServer {
    public static void main(String[] args) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        
        // Taking port and directory from user which want to serve
        System.out.print("Enter the port number: ");
        int port = Integer.parseInt(reader.readLine());
        System.out.print("Enter the directory path to serve files: ");
        String directoryPath = reader.readLine();

        // Checking is directory exists 
        File directory = new File(directoryPath);
        if (!directory.exists() || !directory.isDirectory()) {
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
                String httpResponse = "HTTP/1.1 404 Not Found\r\n" +
                                      "Content-Type: text/html\r\n\r\n" +
                                      "<html><body><h1>404 Not Found</h1></body></html>";
                out.write(httpResponse);
            }
            out.flush(); 
            clientSocket.close();
        }
    }
}
