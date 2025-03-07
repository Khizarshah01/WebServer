import java.io.*;
import java.net.*;

public class ClientHandler implements Runnable {
    private Socket clientSocket;
    private String directoryPath;
    private String directoryPage;

    public ClientHandler(Socket clientSocket, String directoryPath, String directoryPage) {
        this.clientSocket = clientSocket;
        this.directoryPath = directoryPath;
        this.directoryPage = directoryPage;
    }

    public void run() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream());

            String line;
            String requestedFile = "/index.html";
            String requestMethod = "";
            String requestBody = null;
            int contentLength = 0;

            while ((line = in.readLine()) != null && !line.isEmpty()) {
                System.out.println(line);
                if (line.startsWith("GET") || line.startsWith("POST") || line.startsWith("PUT")
                        || line.startsWith("DELETE")) {
                    String[] parts = line.split(" ");
                    if (parts.length > 1) {
                        requestMethod = parts[0];
                        requestedFile = parts[1];
                        System.out.println("File: " + requestedFile);
                    }
                } else if (line.startsWith("Content-Length:")) {
                    contentLength = Integer.parseInt(line.split(":")[1].trim());
                }
            }

            // Ensure root path ("/") serves "index.html"
            if (requestedFile.equals("/") || requestedFile.isEmpty()) {
                requestedFile = "/index.html";
            }

            // Read request body if present
            if (contentLength > 0) {
                char[] body = new char[contentLength];
                in.read(body, 0, contentLength);
                requestBody = new String(body);
                System.out.println("Request Body:\n" + requestBody);
            }

            // Handle HTTP methods
            String httpResponse = "";

            // GET request (Serve file)
            if (requestMethod.equals("GET")) {
                File fileToServe = new File(directoryPath + requestedFile);
                if (fileToServe.exists() && fileToServe.isFile()) {
                    httpResponse = FileService.readFile(fileToServe);
                } else {
                    File errorPage = new File(directoryPage + "/error404.html");
                    httpResponse = FileService.readFile(errorPage);
                }
            }
            // POST request (Similar to PUT but usually for form submissions)
            else if (requestMethod.equals("POST")) {
                File fileToWrite = new File(directoryPath + requestedFile);
                boolean success = FileService.writeFile(fileToWrite, requestBody);
                if (success) {
                    httpResponse = "HTTP/1.1 201 Created\r\nContent-Length: 0\r\n\r\n";
                } else {
                    httpResponse = "HTTP/1.1 500 Internal Server Error\r\nContent-Length: 0\r\n\r\n";
                }
            }
            // PUT request (Create or update a file)
            else if (requestMethod.equals("PUT")) {
                File fileToWrite = new File(directoryPath + requestedFile);
                boolean isNewFile = !fileToWrite.exists();
                boolean success = FileService.writeFile(fileToWrite, requestBody);
                if (success) {
                    if (isNewFile) {
                        httpResponse = "HTTP/1.1 201 Created\r\nContent-Length: 0\r\n\r\n";
                    } else {
                        httpResponse = "HTTP/1.1 200 OK\r\nContent-Length: 0\r\n\r\n";
                    }
                } else {
                    httpResponse = "HTTP/1.1 500 Internal Server Error\r\nContent-Length: 0\r\n\r\n";
                }
            }
            // DELETE request (Delete a file)
            else if (requestMethod.equals("DELETE")) {
                File fileToDelete = new File(directoryPath + requestedFile);
                if (FileService.deleteFile(fileToDelete)) {
                    httpResponse = "HTTP/1.1 200 OK\r\nContent-Length: 0\r\n\r\n";
                } else {
                    httpResponse = "HTTP/1.1 404 Not Found\r\nContent-Length: 0\r\n\r\n";
                }
            } else {
                httpResponse = "HTTP/1.1 405 Method Not Allowed\r\nContent-Length: 0\r\n\r\n";
            }

            out.write(httpResponse);
            out.flush();
            clientSocket.close();
        } catch (Exception e) {
            System.out.println("Client Thread error: " + e.getMessage());
        }
    }
}
