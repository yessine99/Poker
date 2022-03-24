import Entities.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Objects;

/**
 *  This class handles  the client's moves ( check / call / raise / fold )
 */
public class ClientHandler{

    private final String username;
    private final Socket socket ;
    private final ObjectOutputStream objectOutputStream;
    private final ObjectInputStream objectInputStream;
    private final Player player;



    public ClientHandler(Socket socket, int playerID) throws IOException, ClassNotFoundException {

            this.socket = socket;
            player = new Player (playerID+1);
            objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            objectInputStream = new ObjectInputStream(socket.getInputStream());
            username = (String) objectInputStream.readObject();
    }

    public Socket getSocket() {
        return socket;
    }

    public ObjectOutputStream getObjectOutputStream() {
        return objectOutputStream;
    }

    public ObjectInputStream getObjectInputStream() {
        return objectInputStream;
    }

    public String getUsername() {
        return username;
    }

    public Player getPlayer() {
        return player;
    }

    /**
     * Updates the player's state ( chips , hand ... )
     * @throws IOException handled by {@link GameHandler#closeEverything(ClientHandler)}
     */
    public void updatePlayer() throws IOException {
            objectOutputStream.reset();
            objectOutputStream.writeObject(player);
            objectOutputStream.flush();
    }

    /**
     * Sends a Boolean object to the client to take his turn
     * @throws IOException handled by {@link GameHandler#closeEverything(ClientHandler)}
     */
    public void takeTurn() throws IOException {
            objectOutputStream.reset();
            objectOutputStream.writeObject(true);
            objectOutputStream.flush();
    }

    /**
     * Reads the client's inputs received from {@link Client#updatePlayerState(ClientInput)}
     * @param gameState updates the gameState depending on the player's action
     * @return returns fold by default
     * @throws IOException handled by {@link GameHandler#closeEverything(ClientHandler)}
     * @throws ClassNotFoundException handled by {@link GameHandler#closeEverything(ClientHandler)}
     */
    public String readTurn(GameState gameState) throws IOException, ClassNotFoundException {
        Object object = objectInputStream.readObject();
        if (object instanceof ClientInput){
            switch (((ClientInput) object).getAction()) {
                case "Check":
                    return check();
                case "Fold":
                    return fold(gameState);
                case "Call":
                    return call(gameState);
                case "Raise":
                    return raise(gameState, (ClientInput) object);
            }
        }
        return fold(gameState);
    }

    public String raise(GameState gameState, ClientInput clientInput){
        String str="";
        if (clientInput.getValue() >= player.getChips()){
            player.setAllIn(true);
            player.setCurrentBet(player.getCurrentBet()+ player.getChips());
            player.setChips(0);
            gameState.incrementAllinPlayers();
            str = ", all in";
        } else {
            player.setChips(player.getChips()-( clientInput.getValue() - player.getCurrentBet()));
            player.setCurrentBet(clientInput.getValue());
        }
        player.setHasPlayed(true);
        gameState.setPrevBet(player.getCurrentBet());
        return username + " Raise to $"+ getPlayer().getCurrentBet() + ""+ str;
    }

    public String call(GameState gameState){
        String str="";
        if (gameState.getPrevBet() >= player.getChips()){
            player.setCurrentBet(player.getCurrentBet()+ player.getChips());
            player.setChips(0);
            player.setAllIn(true);
            gameState.incrementAllinPlayers();
            str = ", $"+ player.getCurrentBet() + " all in";
        } else {
            player.setChips(player.getChips()-(gameState.getPrevBet() - player.getCurrentBet()));
            player.setCurrentBet(gameState.getPrevBet());
        }
        player.setHasPlayed(true);
        return username + " Call" +str;
    }

    public String fold(GameState gameState){

        player.setHasFolded(true);
        gameState.incrementFoldedPlayers();
        return username + " Fold";
    }

    public String check(){
        player.setHasPlayed(true);
        return username + " Check";
    }



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ClientHandler that)) return false;
        return Objects.equals(getUsername(), that.getUsername());
    }
}
