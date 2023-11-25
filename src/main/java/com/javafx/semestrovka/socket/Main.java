//package com.javafx.semestrovka.socket;
//
//import org.junit.Test;
//
//import java.io.IOException;
//
//import static org.junit.Assert.assertEquals;
//
//public class Main {
//    public static void main(String[] args) throws IOException {
//        ChatClient chatClient = new ChatClient();
//        chatClient.start("A");
//    }
//
//    @Test
//    public void firstTask() throws IOException {
//        EchoClient echoClient = new EchoClient();
//        String resp1 = echoClient.sendMessage("Client 1");
//        echoClient.sendMessage(".");
//        EchoClient echoClient1 = new EchoClient();
//        assertEquals(resp1, "Client 1");
//        assertEquals(echoClient1.sendMessage("123"), "123");
//    }
//}