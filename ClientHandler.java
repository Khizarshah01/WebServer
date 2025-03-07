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
            String requestedParam = null;
            int contentLength = 0;

            while ((line = in.readLine()) != null && !line.isEmpty()) {
                System.out.println(line);
                if (line.startsWith("GET")) {
                    String[] parts = line.split(" ");
                    if (parts.length > 1) {
                        if (parts[1].contains("?")) {
                            String[] temp = parts[1].split("\\?", 2);
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
                    contentLength = Integer.parseInt(line.split(":")[1].trim());
                }
            }

            String requestBody = null;
            if (contentLength > 0) {
                char[] body = new char[contentLength];
                in.read(body, 0, contentLength);
                requestBody = new String(body);
                System.out.println("Request Body:\n" + requestBody);
            }

            // Serve the requested file
            String httpResponse;
            if (requestedFile.equals("/")) {
                requestedFile = "/index.html";
            }
            File fileToServe = new File(directoryPath + requestedFile);
            System.out.println("File to Serve: " + fileToServe);
            if (fileToServe.exists() && fileToServe.isFile()) {
                httpResponse = FileService.readFile(fileToServe);
            } else {
                File errorPage = new File(directoryPage + "/error404.html");
                httpResponse = FileService.readFile(errorPage);
            }

            out.write(httpResponse);
            out.flush();
            clientSocket.close();
        } catch (Exception e) {
            System.out.println("Client Thread error: " + e.getMessage());
        }
    }
}
