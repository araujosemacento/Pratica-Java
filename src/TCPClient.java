import java.io.*;
import java.net.*;

class TCPClient {
    private static int clientCount = 0;

    public static void main(String argv[]) throws Exception {
        String sentence;
        String modifiedSentence;
        try {

            BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
            Socket clientSocket = new Socket("localhost", 9090);

            DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
            BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            synchronized (TCPClient.class) {
                clientCount++;
            }
            int clientId = clientCount;

            System.out.println("Cliente" + clientId + " conectado");

            Thread listener = new Thread(() -> {
                try {
                    String response;
                    while ((response = inFromServer.readLine()) != null) {
                        System.out.println(response);
                    }
                } catch (IOException e) {
                    System.out.println("Cliente" + clientId + " desconectado");
                    System.out.print("~~~~~~~~~~~~~~~~~~~~~~~~~~Servidor fora do ar!~~~~~~~~~~~~~~~~~~~~~~~~~~");
                }
            });
            listener.start();

            while (true) {
                sentence = inFromUser.readLine();
                outToServer.writeBytes(clientId + ": " + sentence + '\n');
            }
        } catch (ConnectException e) {
            e.printStackTrace();
            System.out.print("\n~~~~~~~~~~~~~~~~~~~~~~~~~~Servidor fora do ar!~~~~~~~~~~~~~~~~~~~~~~~~~~\n");
        }
    }

}