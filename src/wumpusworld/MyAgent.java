package wumpusworld;

/**
 * Contains starting code for creating your own Wumpus World agent.
 * Currently the agent only make a random decision each turn.
 * 
 * @author Johan Hagelbäck
 */
public class MyAgent implements Agent
{
    private World w;
    QLearning learn;
    /**
     * Creates a new instance of your solver agent.
     * 
     * @param world Current world state 
     */
    public MyAgent(World world)
    {
        w = world;
        // Here i create object of my class
        // So every time new game is started. Qlearning object is created. which then exlore the map and calcualte QValues
        learn = new QLearning(w);
    }
   
            
    /**
     * Asks your solver agent to execute an action.
     */

    public void doAction()
    {
        //Location of the player
        int cX = w.getPlayerX();
        int cY = w.getPlayerY();
        
        // Call method of Qlearnign class to get best possible action in state based on calculated q values
        String bestAction = this.learn.getBestAction(w);
        System.out.println("new function = "+bestAction);
        w.doAction(bestAction);
    }    
}

