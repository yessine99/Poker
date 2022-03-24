import Entities.Player;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private Socket socket;
    private ObjectOutputStream objectOutputStream;
    private ObjectInputStream objectInputStream;
    private GameState gameState = new GameState();
    private Player player = new Player();


    public Client(Socket socket, String username) {
        try{
            this.socket = socket;

            objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            objectInputStream = new ObjectInputStream(socket.getInputStream());

            objectOutputStream.writeObject(username);
            objectOutputStream.flush();

            int playerID = (Integer) objectInputStream.readObject() + 1;
            System.out.println("Connected to server as "+  username +", (player "+ playerID +")");

        } catch(IOException | ClassNotFoundException e){
            closeEverything();
        }
    }

    public void closeEverything(){
        try {
            if (socket != null)
                socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void takeTurn() {
        new Thread(() -> {
            ClientInput clientInput=null;
            Scanner input = new Scanner(System.in);
            int choice = 0;
            while (socket.isConnected() && (choice < 1 || choice > 5)) {

                if (player.getCurrentBet() == gameState.getPrevBet() )
                    System.out.print("(1)Check\t\t");
                else
                    System.out.print("(2)Call :" + ((gameState.getPrevBet() - player.getCurrentBet())<player.getChips() ? (gameState.getPrevBet() - player.getCurrentBet()) : player.getChips())+ "\t\t");
                System.out.print("(3)Bet/Raise\t\t");
                System.out.print("(4)Fold\t\t");
                System.out.print("Choice :\t\t:");
                choice = input.nextInt();

                switch (choice) {
                    case 1 -> {// CHECK
                        player.check();
                        clientInput = new ClientInput("Check");
                    }
                    case 2 -> { // CALL
                        player.call(gameState.getPrevBet());
                        clientInput = new ClientInput("Call");
                    }
                    case 3 -> { // BET / RAISE
                        player.raise(gameState.getPrevBet());
                        clientInput = new ClientInput("Raise",player.getCurrentBet());
                    }
                    case 4 -> { // FOLD
                        player.fold();
                        clientInput = new ClientInput("Fold");
                    }
                }
                updatePlayerState(clientInput);
            }

        }).start();
    }

    public void startListening(){
        new Thread(() -> {
            while(socket.isConnected()){
                try{
                    Object object = objectInputStream.readObject();
                    if (object instanceof String)
                        System.out.println((String)object);

                    else if (object instanceof Player){
                        player = (Player) object;
                        System.out.println(player);
                    }
                    else if (object instanceof GameState){
                        gameState = (GameState) object;
                    }
                    else if ( object instanceof Boolean)
                        takeTurn();

                }
                catch (IOException | ClassNotFoundException e ){
                    closeEverything();
                }
            }
        }).start();
    }


    public void updatePlayerState(ClientInput input){
        try{
            objectOutputStream.reset();
            objectOutputStream.writeObject(input);
            objectOutputStream.flush();


        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter your username :");
        String username = scanner.nextLine();

        Socket socket = new Socket("localhost",4999);
        Client client = new Client(socket , username);

        client.startListening();

    }


}
