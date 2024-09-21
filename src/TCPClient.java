import java.io.*;
import java.net.*;

class TCPClient {
    private static int clientCount = 0;

    public static void main(String argv[]) throws Exception {
        String sentence; // Armazena mensagens enviadas pelo cliente
        String sentence;
        String modifiedSentence; // Armazena respostas recebidas do servidor
        try {
            BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));

            // Estabelece conexão com o servidor local na porta 9090
            Socket clientSocket = new Socket("localhost", 9090);

            // Cria fluxo de saída para enviar dados ao servidor

            DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());

            // Cria fluxo de entrada para receber dados do servidor
            BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            // Incrementa o contador de clientes e atribui um ID único
            synchronized (TCPClient.class) {
                clientCount++;
            }
            int clientId = clientCount;

            // Imprime mensagem de conexão bem-sucedida
            System.out.println("Cliente" + clientId + " conectado");

            // Cria uma thread para ouvir respostas do servidor
            Thread listener = new Thread(() -> {
                try {
                    String response;
                    while ((response = inFromServer.readLine()) != null) {
                        System.out.println(response); // Exibe respostas recebidas
                    }
                } catch (IOException e) {
                    System.out.println("Cliente" + clientId + " desconectado");
                    System.out.print("~~~~~~~~~~~~~~~~~~~~~~~~~~Servidor fora do ar!~~~~~~~~~~~~~~~~~~~~~~~~~~");
                }
            });

            // Inicia a thread de escuta
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
