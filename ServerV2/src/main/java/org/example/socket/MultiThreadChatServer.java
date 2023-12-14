package org.example.socket;

import com.github.alexdlaird.ngrok.NgrokClient;
import com.github.alexdlaird.ngrok.protocol.CreateTunnel;
import com.github.alexdlaird.ngrok.protocol.Tunnel;
import org.example.WriteToFirebase;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.github.alexdlaird.ngrok.protocol.Proto.TCP;


public class MultiThreadChatServer {
    private static final Pattern remoteAddrPattern = Pattern.compile("--remote-addr=(.+):(\\d+)");

    private static List<ClientHandler> clients = new ArrayList<>();
    private static List<Room> rooms = new ArrayList<>();

    public static void main(String[] args) {
        int port = 8878;
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            Thread.sleep(2000);
            System.out.println("Chat server started on port " + port);
            String ngrokUrl = exposeServer(port);
            System.out.println("Ngrok URL: " + ngrokUrl);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected");
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                String inputLine;
                boolean f = false;
                while ((inputLine = in.readLine()) != null) {
                    f = parseString(inputLine);
                    break;
                }
                if (!f) {

                    boolean added = false;

                    for (Room room : rooms) {
                        if (room.isOpened()) {
                            added = true;
                            ClientHandler clientHandler = new ClientHandler(clientSocket, room.getClientHandlers());
                            room.addJoiner(clientHandler);
                            clients.add(clientHandler);
                            room.getCreator().setClients(List.of(room.getCreator(), room.getJoiner()));
                            room.getJoiner().setClients(List.of(room.getCreator(), room.getJoiner()));
                            clientHandler.start();
                            room.startGame();
                        }
                    }
                    if (!added) {
                        createRoom(clientSocket);
                    }


                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public static Room createRoom(Socket clientSocket) {
        Room room = new Room();
        ClientHandler clientHandler = new ClientHandler(clientSocket, null);
        room.setCreator(clientHandler);
        clientHandler.setClients(List.of(clientHandler));
        clients.add(clientHandler);
        clientHandler.start();
        rooms.add(room);
        return room;
    }

    private static boolean parseString(String input) {
        if (input.contains("true")){
            return true;
        }
        return false;
//        String[] parts = input.split(":");
//        if (parts.length == 4) {
//            return parts;
//        } else if (parts.length == 3) {
//            return false; // Неправильный формат строки
//        }
    }

    public static String exposeServer(int localPort) throws IOException, InterruptedException, URISyntaxException {
        // Команда для запуска ngrok и трансляции локального сервера
        String killngrok = "taskkill /F /IM ngrok.exe";
        ProcessBuilder killngrokProcessBuilder = new ProcessBuilder("cmd.exe", "/c", killngrok);
        Process killngrokProcess = killngrokProcessBuilder.start();
        killngrokProcess.waitFor(); // Ждем завершения процесса

//        JavaNgrokConfig javaNgrokConfig = new JavaNgrokConfig.Builder().withAuthToken("1ptIccyXSH6S1W8LmLkRJpnyb5x_83o4kwwUjTkXxDeX7njwr").withRegion(Region.EU).build();
        final NgrokClient ngrokClient = new NgrokClient.Builder().build();
        CreateTunnel createTunnel = new CreateTunnel.Builder()
                .withProto(TCP)
                .withAddr(localPort)
                .build();
        Tunnel tunnel = ngrokClient.connect(createTunnel);
        System.out.println(tunnel.getPublicUrl());
        URI uri = new URI(tunnel.getPublicUrl());
        String host = uri.getHost();
        System.out.println("Host: " + host);

        // Получение порта
        int port = uri.getPort();
        System.out.println("Port: " + port);
        WriteToFirebase.write(host, port);

        // Create a tunnel
        String ngrokUrl = null;
//        try {
//            Tunnel tunnel = ngrokClient.connect(createTunnel);
//        } catch (Exception e) {
//            String ngrokCommand = "ngrok tcp " + localPort;
//            ProcessBuilder ngrokProcessBuilder = new ProcessBuilder("cmd.exe", "/c", ngrokCommand);
//            Process ngrokProcess = ngrokProcessBuilder.start();
//
//            // Читаем вывод команды ngrok
//            try (BufferedReader reader = new BufferedReader(new InputStreamReader(ngrokProcess.getInputStream()))) {
//                List<String> lines = reader.lines().collect(Collectors.toList());
//
//                for (String line : lines) {
//                    if (line.contains("ngrok tcp --remote-addr=")) {
//                        Matcher matcher = remoteAddrPattern.matcher(line);
//                        if (matcher.find()) {
//                            System.out.println("Remote address: " + matcher.group(1));
//                            System.out.println("Remote port: " + matcher.group(2));
//                            break;
//                        } else {
//                            System.out.println("Remote address not found in command: " + line);
//                        }
//                    }
//                }
//            } catch (IOException er) {
//                er.printStackTrace();
//            } finally {
//                try {
//                    // Explicitly close the stream after reading
//                    ngrokProcess.getInputStream().close();
//                } catch (IOException er) {
//                    er.printStackTrace();
//                }
//            }
            return null;

    }


    private static String parsePublicUrl(String responseBody) {
        // Предполагаем, что public_url находится внутри JSON-ответа
        // Вам, возможно, потребуется использовать библиотеку для более сложного парсинга JSON
        Pattern pattern = Pattern.compile("\"public_url\":\"(.*?)\"");
        Matcher matcher = pattern.matcher(responseBody);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }
}