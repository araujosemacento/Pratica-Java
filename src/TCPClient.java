import java.io.*;
import java.net.*;

class TCPClient {
    public static void main(String argv[]) throws Exception {
        String serverAddress = "localhost";
        int port = 9090;

        Socket clientSocket = new Socket(serverAddress, port);
        System.out.println("Conectado ao servidor");

        PrintWriter socketSaidaServer = new PrintWriter(clientSocket.getOutputStream(), true);
        BufferedReader socketEntradaServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        BufferedReader teclado = new BufferedReader(new InputStreamReader(System.in));

        // Thread para ler mensagens do servidor
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String respostaServidor;
                    while ((respostaServidor = socketEntradaServer.readLine()) != null) {
                        System.out.println(respostaServidor);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        // Thread para enviar mensagens ao servidor
        String mensagem;
        while ((mensagem = teclado.readLine()) != null) {
            socketSaidaServer.println(mensagem);
        }

        clientSocket.close();
    }
}