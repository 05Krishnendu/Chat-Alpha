# Chat Alpha

Chat Alpha is a real-time multi-client chat application developed using **Java Socket Programming**. It follows a **client-server architecture**, where multiple clients can connect to a central server and communicate with each other in real time.

The project demonstrates core concepts of **Java networking, socket communication, multithreading, I/O streams, and concurrent client handling**.

## Key Features

* Real-time messaging between multiple clients
* Multi-client support
* Client-server architecture
* Concurrent client handling using Java threads
* Message broadcasting to connected clients
* Socket-based communication using TCP
* Stream-based input and output handling

## Technologies Used

* **Java**
* **Java Socket Programming**
* **TCP/IP**
* **Multithreading**
* **I/O Streams**

## Architecture

Each client establishes a TCP connection with the central server. The server creates a separate thread for each connected client, allowing multiple users to communicate simultaneously without blocking other connections.

```text
Client 1 ──┐
Client 2 ──┼──> Chat Server ──> Broadcast Messages
Client 3 ──┘
```

## Learning Outcomes

Through this project, I explored practical implementation of:

* Client-server networking
* TCP socket communication
* Java `Socket` and `ServerSocket`
* Multithreading with `Runnable`
* Input/output streams
* Handling multiple connected clients
* Real-time message broadcasting
