package dk.dd.udps;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.*;
import java.util.Arrays;

/**
 * @author Dora
 */
public class UDPServer {
    private static final int serverPort = 7777;

    // buffers for the messages
    private static byte[] dataIn = new byte[64000];
    private static byte[] dataOut = new byte[128];

    // In UDP messages are encapsulated in packages and sent over sockets
    private static DatagramPacket requestPacket;
    private static DatagramPacket responsePacket;
    private static DatagramSocket serverSocket;

    // Directory that images will be saved to
    private static String imageDirectory = "C:\\Users\\adams\\Pictures\\test.jpg";


    public static void main(String[] args) throws Exception {
        String messageIn, messageOut;
        try {
            String serverIP = InetAddress.getLocalHost().getHostAddress();
            // Opens socket for accepting requests
            serverSocket = new DatagramSocket(serverPort);
            while (true) {
                System.out.println("Server " + serverIP + " running ...");
                receiveRequest();
                sendResponse("Image received");
            }
        } catch (Exception e) {
            System.out.println(" Connection fails: " + e);
        } finally {
            serverSocket.close();
            System.out.println("Server port closed");
        }
    }

    public static void receiveRequest() throws IOException {
        // Receive packet
        requestPacket = new DatagramPacket(dataIn, dataIn.length);
        serverSocket.receive(requestPacket);

        // Convert packet to image then write it to a file
        ByteArrayInputStream bis = new ByteArrayInputStream(requestPacket.getData());
        BufferedImage bImage = ImageIO.read(bis);
        ImageIO.write(bImage, "jpg", new File(imageDirectory));
    }

    public static String processRequest(String message) {
        return message.toUpperCase();
    }

    public static void sendResponse(String message) throws IOException {
        InetAddress clientIP;
        int clientPort;

        clientIP = requestPacket.getAddress();
        clientPort = requestPacket.getPort();
        System.out.println("Client port: " + clientPort);
        System.out.println("Response: " + message);
        dataOut = message.getBytes();
        responsePacket = new DatagramPacket(dataOut, dataOut.length, clientIP, clientPort);
        serverSocket.send(responsePacket);
        System.out.println("Message sent back " + message);
    }
}
