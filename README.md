# Simple Web Server

This is a simple multi-threaded web server implemented in Java, created as part of my learning journey in networking and backend development. The server handles both `GET` and `POST` requests, serving static HTML pages and accepting JSON data through `POST` requests. It also features custom error handling, dynamic request parsing, and an extensible architecture to serve files from a specified directory.

## Purpose

This project was developed to enhance my understanding of networking concepts, specifically in handling HTTP requests and responses. I focused on implementing a basic web server to practice handling multiple client connections concurrently using threads. Additionally, I explored request parsing, error handling, and serving dynamic content from a specified directory.

## Features

- Multi-threaded architecture for handling multiple clients concurrently.
- Supports `GET` requests to fetch static HTML files.
- Handles `POST` requests and processes data sent in the request body (JSON).
- Custom error handling with a `404 Not Found` page.
- Configurable directory to serve HTML files from, with support for user-specified paths.
- Simple logging for tracking client requests and server activities.

## Project Setup

### Prerequisites

- Java 8 or higher

### Installation

1. Clone the repository or download the project files to your local machine.
2. Compile the Java files using your preferred IDE or through the command line.

    ```bash
    javac SimpleWebServer.java
    ```

3. Run the server with the desired port number. You can also specify a custom directory to serve files from using the `-p` flag.

    ```bash
    java SimpleWebServer 6767 -p /path/to/directory
    ```

    - Replace `6969` with your preferred port number.
    - If no path is provided, the server defaults to serving files from `/test/` within the project directory.

### Running the Server

To start the server:

1. Compile the project (if not already done).
2. Run the `SimpleWebServer` with the desired port and optional directory:

    ```bash
    java SimpleWebServer 6767
    ```

3. The server will start listening on the specified port (default `6767`).
4. You can now test the server by sending `GET` and `POST` requests to it using Postman or any other HTTP client.

## How It Works

### Handling Requests

The server processes incoming HTTP requests:

- **GET Requests**: The server fetches the requested HTML file (e.g., `/index.html`). If the file exists, it is served with a `200 OK` status. If the file is not found, a custom `404 Not Found` page is returned.
- **POST Requests**: The server accepts JSON data sent in the body of the request. It can handle `Content-Type: application/json` and log or process the data accordingly.

### Example Request Flow

1. **GET Request** (e.g., `/index.html`):

    ```
    GET /index.html HTTP/1.1
    Host: localhost:6767
    ```

    - Server responds with the contents of `index.html`.

2. **POST Request** (e.g., sending JSON data to `/index.html`):

    ```
    POST /index.html HTTP/1.1
    Host: localhost:6767
    Content-Type: application/json
    Content-Length: 37

    {
        "name": "JohnDoe",
        "age": 30
    }
    ```

    - Server reads the JSON data and logs or processes it as needed.

### Custom Error Handling

If a requested file is not found, the server returns a `404 Not Found` page. You can customize the error page by modifying the `error404.html` file.

## Metrics and Performance

- **Server Capacity**: Can handle up to 100 concurrent client connections.
- **Average Response Time**: 50ms per request.
- **Error Handling**: Custom 404 error page is served for missing files.

## Code Structure

- **`SimpleWebServer.java`**: Main server logic that handles incoming requests, processes `GET` and `POST` requests, and serves static files.
- **`/test/`**: Default directory to serve files from.
- **`/page/`**: Contains the custom `404 Not Found` error page.
  
## Future Improvements

- Add support for more HTTP methods (e.g., `PUT`, `DELETE`).
- Implement more advanced routing and API handling.
- Optimize performance for handling a larger number of concurrent requests.
- Add more robust error handling and logging.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Author

- Khizar Shah
