import java.io.*;

public class FileService {
    public static String readFile(File file) throws IOException {
        StringBuilder fileContent = new StringBuilder();
        try (BufferedReader fileReader = new BufferedReader(new FileReader(file))) {
            String fileLine;
            while ((fileLine = fileReader.readLine()) != null) {
                fileContent.append(fileLine).append("\n");
            }
        }

        String contentType = determineContentType(file.getName());

        return "HTTP/1.1 200 OK\r\n" +
                "Content-Type: " + contentType + "\r\n\r\n" +
                fileContent.toString();
    }

    public static String determineContentType(String fileName) {
        if (fileName.endsWith(".json")) {
            return "application/json";
        } else if (fileName.endsWith(".css")) {
            return "text/css";
        } else if (fileName.endsWith(".js")) {
            return "application/javascript";
        } else if (fileName.endsWith(".png")) {
            return "image/png";
        } else if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) {
            return "image/jpeg";
        } else {
            return "text/html";
        }
    }
}
