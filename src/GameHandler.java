
import Entities.Card;
import Entities.Player;

import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Class for handling the game loop
 */
public class GameHandler{

    private final List<ClientHandler>  clientHandlerList = new ArrayList<>();
    private final GameState gameState = new GameState();
    private final Pot pot = new Pot();

    public List<ClientHandler> getClientHandlerList() {
        return clientHandlerList;
    }

    /**
     * Broadcasts the current gameState to all clients in this game
     */
    public void broadcastGameState(){
        for (ClientHandler clientHandler : clientHandlerList)
            try{
                clientHandler.getObjectOutputStream().reset();
                clientHandler.getObjectOutputStream().writeObject(gameState);
                clientHandler.getObjectOutputStream().flush();

            }catch (IOException e){
                e.getStackTrace();
                closeEverything(clientHandler);
            }
    }

    /**
     * Updates all the players states
     */
    public void updatePlayers(){
        for (ClientHandler clientHandler : clientHandlerList) {
            try {
                clientHandler.updatePlayer();
            } catch (IOException e) {
                e.printStackTrace();
                closeEverything(clientHandler);
            }
        }
    }

    /**
     * Initializes a client, adds him to this game and starts it if the requirements are met (min players = 3)
     */
    public void initializeClient(ClientHandler clientHandler){
        try{
            clientHandler.getObjectOutputStream().writeObject(clientHandlerList.size()-1); // player ID
            clientHandler.getObjectOutputStream().flush();
            broadcastMessage(clientHandler.getUsername() + " has joined the table!",clientHandler);

            if (clientHandlerList.size()==3 && !gameState.isOnGoing()) { // to bechanged to 2
                System.out.println("The game has started!");
                broadcastMessage("The game has started!");
                startGame();
            }
        } catch (IOException e) {
            e.printStackTrace();
            closeEverything(clientHandler);
        }
    }

    /**
     * Broadcasts a string to all clients except the sender
     */
    public void broadcastMessage(String message, ClientHandler sender){
        for (ClientHandler clientHandler : clientHandlerList)
            if(!clientHandler.equals(sender))
            try{
                clientHandler.getObjectOutputStream().reset();
                clientHandler.getObjectOutputStream().writeObject(message);
                clientHandler.getObjectOutputStream().flush();
            }catch (IOException e){
                e.getStackTrace();
                closeEverything(clientHandler);
            }
    }

    /**
     * Broadcasts a string to all clients
     */
    public void broadcastMessage(String message){
        for (ClientHandler clientHandler : clientHandlerList)
                try{
                    clientHandler.getObjectOutputStream().reset();
                    clientHandler.getObjectOutputStream().writeObject(message);
                    clientHandler.getObjectOutputStream().flush();
                }catch (IOException e){
                    e.getStackTrace();
                    closeEverything(clientHandler);
                }
    }

    /**
     * Sends a string to a specific Client
     */
    public void sendMessage(ClientHandler receiver , String message){
        try{
            receiver.getObjectOutputStream().reset();
            receiver.getObjectOutputStream().writeObject(message);
            receiver.getObjectOutputStream().flush();
        }catch (IOException e){
            e.getStackTrace();
            closeEverything(receiver);
        }
    }

    /**
     * Handles the exceptions by closing the client's socket and removing him from this game
     */
    public void closeEverything(ClientHandler clientHandler) {
        clientHandlerList.remove(clientHandler);
        broadcastMessage(clientHandler.getUsername() + " has left the table!");
        try {
            if (clientHandler.getSocket() != null)
                clientHandler.getSocket() .close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Initializes the gameState and the players states
     */
    public void initHand(){
        gameState.getDeck().construct();
        gameState.setPot(0);
        gameState.setFoldedPlayers(0);
        gameState.setAllinPlayers(0);
        gameState.incrementBlindsIndexes(clientHandlerList.size());

        System.out.println("Blinds Are : "+gameState.getTable().getBlinds()+ " / "+gameState.getTable().getBlinds()/2);
        //BIG BLIND
        clientHandlerList.get(gameState.getBigBlindIndex()).getPlayer().setBlind(gameState.getTable().getBlinds());
        System.out.println("Player "+clientHandlerList.get(gameState.getBigBlindIndex()).getPlayer().getId() + " is big blind");
        //SMALL BLIND
        clientHandlerList.get(gameState.getSmallBlindIndex()).getPlayer().setBlind(gameState.getTable().getBlinds()/2);
        System.out.println("Player "+clientHandlerList.get(gameState.getSmallBlindIndex()).getPlayer().getId() + " is small blind");

        gameState.setPrevBet(gameState.getTable().getBlinds());
        gameState.setOnGoing(true);
    }

    public void dealRound(int round) {
        String str="\n";
        switch (round) {
            case 0 -> {
                str+="----------PreFlop------------ Pot :" + gameState.getPot();
                dealPreFlop();
            }
            case 1 -> {
                str+="----------Flop------------ Pot :" + gameState.getPot();
                dealFlop();
            }
            case 2 -> {
                str+="----------Turn------------ Pot :" + gameState.getPot();
                dealTurnOrRiver();
            }
            case 3 -> {
                str+="----------River ------------ Pot :" + gameState.getPot();
                dealTurnOrRiver();
            }
        }
        System.out.println(str);
        broadcastMessage(str);
        updatePlayers();
        broadcastGameState();

    }

    private void dealPreFlop() {
        Iterator<Card> it = gameState.getDeck().getDeck().iterator();
        int i=0;
        int j=0;
        Card card;
        while (it.hasNext() && i<(clientHandlerList.size()*2) ){
            card=it.next();
            clientHandlerList.get(j).getPlayer().getHand().getCards().add(card);
            it.remove();
            i++;
            j++;
            if ( j == clientHandlerList.size())
                j=0;
        }
    }

    private void dealFlop()  {
        burnCard();
        Iterator<Card> it = gameState.getDeck().getDeck().iterator();
        int i=0;
        Card card;

        while (it.hasNext() && i<3) {
            card= it.next();
            for (ClientHandler clientHandler : clientHandlerList) {
                clientHandler.getPlayer().getHand().getCards().add(card);
            }
            it.remove();
            i++;
        }

    }

    private void dealTurnOrRiver() {
        burnCard();
        for (ClientHandler cl : clientHandlerList) {
            cl.getPlayer().getHand().getCards().add(gameState.getDeck().getDeck().get(0));
        }
        gameState.getDeck().getDeck().remove(0);
    }

    public void burnCard(){
        gameState.getDeck().getDeck().remove(0);
    }

    /**
     * This is the betting round loop, notifies the player to take turn if he has to with : {@link ClientHandler#takeTurn()}
     * @param i is the first player who has to take his turn in a given round
     */
    public void startBettingRound(int i){
        String action;
        while ( i < clientHandlerList.size() && gameState.getFoldedPlayers() < clientHandlerList.size()-1 && ( clientHandlerList.get(i).getPlayer().getCurrentBet() != gameState.getPrevBet() || !clientHandlerList.get(i).getPlayer().hasPlayed() ) ){
            if ( !clientHandlerList.get(i).getPlayer().hasFolded() && !clientHandlerList.get(i).getPlayer().isAllIn() ) {
                try {
                    clientHandlerList.get(i).takeTurn();
                    action=clientHandlerList.get(i).readTurn(gameState);
                    broadcastMessage(action, clientHandlerList.get(i));
                    broadcastGameState();

                } catch (ClassNotFoundException | IOException e) {
                    e.printStackTrace();
                    closeEverything(clientHandlerList.get(i));
                }
            }
            i++;
            if (i == clientHandlerList.size()) // if there's a BET/RAISE
                i = 0;
        }
    }

    public void initNextRound(){
        for(ClientHandler clientHandler: clientHandlerList){
            gameState.setPot( gameState.getPot() + clientHandler.getPlayer().getCurrentBet() );
            clientHandler.getPlayer().initForNextRound();
        }
        gameState.setPrevBet(0);
    }

    public void startGame()  {
        initHand();
        int i; // Player counter
        int j=0; // Round counter

        if (gameState.getBigBlindIndex() == clientHandlerList.size() - 1)
            i = 0;
        else i = gameState.getBigBlindIndex() + 1;

        while(j<4 && (clientHandlerList.size() - gameState.getFoldedPlayers() > 1) ) { // At least 2 players are committed
            dealRound(j); // updates the playersStates and the game state
            if (clientHandlerList.size() - gameState.getFoldedPlayers() - gameState.getAllinPlayers() > 1)
                startBettingRound(i);
            initNextRound();
            i = gameState.getSmallBlindIndex();
            j++;
        }

        pot.setPotSize(gameState.getPot());
        pot.distributeChips();

        broadcastMessage("Hand is Over!");
        System.out.println("Players after chips distribution :");
        clientHandlerList.forEach(clientHandler -> System.out.println(clientHandler.getPlayer()));
    }

    /**
     * Subclass to handle the pot distribution
     */
    public class Pot  {
        private float potSize;

        public Pot() {
            potSize=0;
        }

        public float getPotSize() {
            return potSize;
        }

        public void setPotSize(float potSize) {
            this.potSize = potSize;
        }

        /**
         * Used in {@link #distributeChips()} in order to get the winners list  of each subPot
         * @param uncheckedPlayerIndex player from which the subList should be extracted
         */
        public List<Player> getWinners(int uncheckedPlayerIndex) {
            // Players Hands must be evaluated

            double minHandScore =-1;
            List<Player> winningPlayersList = new ArrayList<>();
            Player qualifiedPlayer = new Player();

            for (ClientHandler clientHandler : clientHandlerList.subList(uncheckedPlayerIndex,clientHandlerList.size())){ // previous players are already checked
                if(!clientHandler.getPlayer().hasFolded()){
                    if (clientHandler.getPlayer().getHand().getScore() > minHandScore){ // if has a better handScore
                        minHandScore = clientHandler.getPlayer().getHand().getScore();
                        qualifiedPlayer=clientHandler.getPlayer();
                        winningPlayersList.clear();
                        winningPlayersList.add(clientHandler.getPlayer());
                    }
                    else if (clientHandler.getPlayer().getHand().getScore() == minHandScore){ //  then compare Kickers
                        if (clientHandler.getPlayer().getHand().compareTo(qualifiedPlayer.getHand())>0){ // if Kicker is bigger
                            qualifiedPlayer=clientHandler.getPlayer();
                            winningPlayersList.clear();
                            winningPlayersList.add(clientHandler.getPlayer());
                        }
                        else if (clientHandler.getPlayer().getHand().compareTo(qualifiedPlayer.getHand())==0)  // else if same Kickers
                            winningPlayersList.add(clientHandler.getPlayer());
                    }
                }

            }
            //System.out.println("Winners are ---->>"+winningPlayersList);
            return winningPlayersList;

        }

        public void sortPlayersByPotContributionASC(){
            clientHandlerList.sort((p1, p2) -> {
                if (p1.getPlayer().getPotContribution() > p2.getPlayer().getPotContribution())
                    return 1;
                else if (p1.getPlayer().getPotContribution() < p2.getPlayer().getPotContribution())
                    return -1;
                return 0;
            });
        }

        public void evaluatePlayersHands(){
            for(ClientHandler clientHandler : clientHandlerList){ // Evaluating Players Hands
                if (!clientHandler.getPlayer().hasFolded()){
                    System.out.println(clientHandler.getPlayer()+"\nHand Strength :" + clientHandler.getPlayer().getHand().evaluateHand()+ "\tScore :"+clientHandler.getPlayer().getHand().getScore()+"\n");
                }
            }
        }

        /**
         * Distributes the chips to the winners depending on their Pot Contribution
         */
        public void distributeChips(){
            evaluatePlayersHands();
            sortPlayersByPotContributionASC();

            List<Player> potWinners;
            float sidePot;
            float lastWinnerContrib = 0;
            float lostContribs = 0; // contributions from folds and loses
            int i=0;

            while ( i<clientHandlerList.size() )
            {
                if (potSize>0){
                    potWinners = getWinners(i);
                    if (potWinners.contains(clientHandlerList.get(i).getPlayer())) { // if is a winner
                        if (potWinners.size() == 1) {
                            sidePot = (lostContribs + (clientHandlerList.get(i).getPlayer().getPotContribution() - lastWinnerContrib) * (clientHandlerList.size()-i)); // uncheckedPlayers = players.size()-i
                            lostContribs = 0;
                            lastWinnerContrib = clientHandlerList.get(i).getPlayer().getPotContribution();
                        } else {
                            sidePot = ((lostContribs + (clientHandlerList.get(i).getPlayer().getPotContribution() - lastWinnerContrib) * (clientHandlerList.size()-i)) / potWinners.size());
                            lostContribs /= potWinners.size();
                        }
                        clientHandlerList.get(i).getPlayer().addChips(sidePot);
                        sendMessage(clientHandlerList.get(i),"You win $"+sidePot + " !");
                        broadcastMessage(clientHandlerList.get(i).getUsername() + ", (player "+clientHandlerList.get(i).getPlayer().getId() +") wins  $"+sidePot,clientHandlerList.get(i));
                        potSize -= sidePot;
                    }
                    else lostContribs += clientHandlerList.get(i).getPlayer().getPotContribution() - lastWinnerContrib;
                }

                clientHandlerList.get(i).getPlayer().initForNextHand();
                i++;
            }
        }


        @Override
        public String toString() {
            return "Pot Size ="+potSize;
        }

    }

}

