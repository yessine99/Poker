import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;


public class Server {

    private final ServerSocket serverSocket;
    private final List<ClientHandler> clientHandlerList = new ArrayList<>();
    private final List<GameHandler> gameHandlerList = new ArrayList<>();
    public Server(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    public void startServer() throws IOException {

        int gameHandlerIndex =0;

        if (serverSocket!=null) {
            System.out.println("Server has started running!");
            System.out.println("Waiting for at least 3 players to start the game ...");
            while (!serverSocket.isClosed()) {

                Socket socket = serverSocket.accept();
                System.out.println("A new client has connected!");
                addClientToTheGameHandler(gameHandlerIndex, socket);

            }
        }
    }

    public void addClientToTheGameHandler(int i , Socket socket) throws IOException {
        try {
            ClientHandler clientHandler = new ClientHandler(socket,clientHandlerList.size());
            clientHandlerList.add(clientHandler);

            if( gameHandlerList.isEmpty()){
                gameHandlerList.add(new GameHandler());
                gameHandlerList.get(i).getClientHandlerList().add(clientHandler);
            }
            else if (gameHandlerList.get(i).getClientHandlerList().size()<=5){
                gameHandlerList.get(i).getClientHandlerList().add(clientHandler);
            }
            else{
                i++;
                gameHandlerList.add(new GameHandler());
                gameHandlerList.get(i).getClientHandlerList().add(clientHandler);

            }
            gameHandlerList.get(i).initializeClient(clientHandler);

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
       ServerSocket serverSocket = new ServerSocket(4999);
       Server server = new Server(serverSocket);
       server.startServer();

    }



}
