import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

class TCPServer {
    private static List<PrintWriter> clientWriters = new ArrayList<>();

    public static void main(String argv[]) throws Exception {
        ServerSocket welcomeSocket = new ServerSocket(9090);

        System.out.println("Servidor no ar");
        while (true) {
            // Aceita uma nova conexão de cliente
            Socket connectionSocket = welcomeSocket.accept();
            System.out.println("Nova conexão de um cliente");

            // Cria uma nova thread para tratar o cliente
            ClientHandler clientHandler = new ClientHandler(connectionSocket);
            new Thread(clientHandler).start();
        }
    }

    private static class ClientHandler implements Runnable {
        private Socket clientSocket;
        private BufferedReader socketEntradaCliente;
        private PrintWriter socketSaidaCliente;

        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        @Override
        public void run() {
            try {
                socketEntradaCliente = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                socketSaidaCliente = new PrintWriter(clientSocket.getOutputStream(), true);
                synchronized (clientWriters) {
                    clientWriters.add(socketSaidaCliente);
                }

                String msgCliente;
                while ((msgCliente = socketEntradaCliente.readLine()) != null) {
                    System.out.println("Cliente disse: " + msgCliente);
                    // Retransmite a mensagem para todos os clientes conectados
                    synchronized (clientWriters) {
                        for (PrintWriter writer : clientWriters) {
                            writer.println("Cliente: " + msgCliente);
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                synchronized (clientWriters) {
                    clientWriters.remove(socketSaidaCliente);
                }
                System.out.println("Cliente desconectado");
            }
        }
    }
}