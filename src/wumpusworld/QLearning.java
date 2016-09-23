/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wumpusworld;

import java.util.Random;


public class QLearning {

    public double[][][][] Qsa; // Will store Q values for state. State is [x][y][direction][action]
    public int[][][][] Nsa; // Will store number of times an action is used in a state State is [x][y][direction][action]
    private final int matrixSize = 4; // Matrix size
    private final int directions = 4; // we have 4 directions up,down,left,right
    private final String[] actions = {"g", "c", "s", "m", "l", "r"}; // possible actions we have in our wumpus world
    private final double gama = 0.6; // Discount factor used to calculate Q values
    private final double minQValue = 0; // Minimum Q value to initialize Q matrix
    private final int maxEpisodes = 10000; // Maximium numbe rof iterations to explore world. More iterations more learning

    public QLearning(World initialWord) {
        // initialize qvalues
        // Here we define our Q matrix and number of times action used matrix
        this.Qsa = new double[matrixSize][matrixSize][directions][actions.length];
        this.Nsa = new int[matrixSize][matrixSize][directions][actions.length];

        // Here we initialize q matrix and actions usage matrix
        for (int x = 0; x < this.matrixSize; x++) {
            for (int y = 0; y < this.matrixSize; y++) {
                for (int direction = 0; direction < this.directions; direction++) {
                    for (int action = 0; action < this.actions.length; action++) {
                        this.Qsa[x][y][direction][action] = minQValue;
                        this.Nsa[x][y][direction][action] = 0;
                    }
                }
            }
        }

        // this is main iteration loop that runs for a specific numbe of times and explore the world
        for (int episode = 0; episode < this.maxEpisodes; episode++) {
            this.exploreTheWorld(initialWord.cloneWorld());
        }
    }

    private void exploreTheWorld(World tempWorld) {
        /*
        This is main function where Qvalues are calculated in an iterative way
        Every time this function starts it starts from very first state of wumpus that is (1,1)
        Then it select a random action and perform that action to go to next state. and then calculate q value 
        by comparing current state and next achieved state
         */
        while (!tempWorld.gameOver()) {
            /*
            Here at first we record current state paramaters like x,y and direction and current score at this state
            as our arrays start from 0 so subtracting 1 from wumpus world position to move it to our arrays
             */
            int newX = tempWorld.getPlayerX() - 1; // holds x value
            int newY = tempWorld.getPlayerY() - 1; // holds y value
            int newDirection = tempWorld.getDirection(); // holds direction in tis state
            int scoreBeforeApplyingAction = tempWorld.getScore(); // Holds state score
            Random rand = new Random();
            int randomAction = rand.nextInt(6); // Choose random action to go to next state
            tempWorld.doAction(this.actions[randomAction]); // perform action
            int scoreAfterApplyingAction = tempWorld.getScore();// get score after performing action
            // in follwoign statement we calcualte immediate reward of state by subtracting score before applying action from
            // score after applying action.
            // In QLearning Rewards array is given for states but in wumpus we dont know reward before we jump into state
            // so Thats why we calculate reward by comparing previous state and next state scores.
            int immediateReward = scoreAfterApplyingAction - scoreBeforeApplyingAction;
            if(tempWorld.hasWumpus(tempWorld.getPlayerX(),tempWorld.getPlayerY())){
                immediateReward -= 100;
            }
            /*
                In following code snippet we actually look for maximum q value for all possbile actions in next state
            So we loop through each action and get its next state q value for that action. And then find maximum q value.
            As this is to be used in calculating q value for current state.
             */
            double maxQvalue = minQValue; // just an initializer to start with maxQvalue as 0
            for (int action = 0; action < this.actions.length; action++) {
                double qValue = this.Qsa[tempWorld.getPlayerX() - 1][tempWorld.getPlayerY() - 1][tempWorld.getDirection()][action];
                if (qValue > maxQvalue) {
                    maxQvalue = qValue;
                }
            }
            /*
            This is formula we are using to calculate q value of current state
            Q(s, a) ← Q(s, a) + alpha * (reward + gama * maxQ(s0, a0..a5) − Q(s, a))
            Where 
            Q(s,a) => current state value for action a which is actually [x][y][direction][action] in our q matrix
             */

            double oldQValue = this.Qsa[newX][newY][newDirection][randomAction];
            double alpha = 0.01;
            double qValue = oldQValue + alpha * (immediateReward + gama * maxQvalue - oldQValue);
            this.Qsa[newX][newY][newDirection][randomAction] = qValue;
            //System.out.println("X = " + newX + " Y = " + newY + " Direction = " + newDirection + " Action = " + this.actions[randomAction]);
        }
    }

    /*
    This is action which actually return action with highest q value for state.
     */
    public String getBestAction(World world) {
        int bestAction = -1;
        int px = world.getPlayerX() - 1;
        int py = world.getPlayerY() - 1;
        int direction = world.getDirection();
        double highestValue = -1 * Integer.MAX_VALUE;
        
        int actionApplicationThreshhold = 0;// to control number of times action is used in same state
        
        // Following is code where we actually solve the problem for conditions explained in submitted report.
        // We loop through untill we find a best possible action
        while (bestAction == -1) {
            ++actionApplicationThreshhold;
            for (int action = 0; action < this.actions.length; action++) {
                double tempValue = this.Qsa[px][py][direction][action];
                switch (this.actions[action]) {
                    case "g": {
                        // ignore grab gold action when there is no glitter in current square
                        if (!world.hasGlitter(world.getPlayerX(), world.getPlayerY())) {
                            continue;
                        }
                        break;
                    }
                    case "c": {
                        // ignore climb action when player is not in pit
                        if (!world.isInPit()) {
                            continue;
                        }
                        break;
                    }
                    case "l": {
                        // ignore left action which will lead to bump in wall
                        if ((px == 0 && direction == World.DIR_UP) || (px == 4 && direction == World.DIR_DOWN) || (py == 0 && direction == World.DIR_LEFT) || (py == 4 && direction == World.DIR_RIGHT)) {
                            continue;
                        }
                        break;
                    }
                    case "r": {
                        // ignore right action which will lead to bump in wall
                        if ((px == 0 && direction == World.DIR_DOWN) || (px == 4 && direction == World.DIR_UP) || (py == 0 && direction == World.DIR_RIGHT) || (py == 4 && direction == World.DIR_LEFT)) {
                            continue;
                        }
                        break;
                    }
                    case "m": {
                        // ignore move action which will lead to bump in wall
                        if ((px == 0 && direction == World.DIR_LEFT) || (px == 4 && direction == World.DIR_RIGHT) || (py == 0 && direction == World.DIR_DOWN) || (py == 4 && direction == World.DIR_UP)) {
                            continue;
                        }
                        break;
                    }case "s":{
                        // ignore shoot action when no stentch or wumpus is dead
                        if(!world.wumpusAlive() || !world.hasStench(world.getPlayerX(), world.getPlayerY())){
                            continue;
                        }
                        break;
                    }
                }
                
                // this is qvalue part. Here we compare actions to select action with highest qvalue and also 
                // take care of number of times action is used in this state.
                if (tempValue > highestValue && this.Nsa[px][py][direction][action] < actionApplicationThreshhold) {
                    bestAction = action;
                    highestValue = tempValue;
                }
            }
        }
        
        //Increment the action usage counter in this state
        this.Nsa[px][py][direction][bestAction] = this.Nsa[px][py][direction][bestAction] + 1;
        // return string form of selected action
        return this.actions[bestAction];
    }
}
