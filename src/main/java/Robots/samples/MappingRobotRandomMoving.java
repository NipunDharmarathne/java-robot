package Robots.samples;

import swarm.robot.VirtualRobot;
import java.util.ArrayList;
import java.util.Random;

public class MappingRobotRandomMoving extends VirtualRobot {
    
    // Size of a grid cell
    private final double GRID_SPACE = 18.000;

    // The default movement speed
    private final int defaultMoveSpeed = 200;

    // The default rotate speed
    private final int defaultRotateSpeed = 200;

    // Proximity Sensor options
    // Angles for left,front and right side rotating
    private int[] proximityAngles = { -90, 0, 90 };

    // Index to get left proximity angle
    public static int PROXIMITY_LEFT = 0;

    // Index to get front proximity angle
    public static int PROXIMITY_FRONT = 1;

    // Index to get right proximity angle
    public static int PROXIMITY_RIGHT = 2;

    // Robot's initial position
    double robotX = 0;
    double robotY = 0;

    public MappingRobotRandomMoving(int id, double x, double y, double heading) {
        super(id, x, y, heading);
        robotX=(x+81)/18;
        robotY=(y+81)/18;
    }

    int numRows=10;
    int numCols=10;
    int[][] occupancyGrid = new int[numRows][numCols];

    int rightTurns=0;
    int leftTurns=0;

    public void setup() {
        System.out.println("My Test Robot Started");

        super.setup();

        // Setup proximity sensor with 3 angles
        proximitySensor.setAngles(proximityAngles);

        // Start immediately after the setup
        state = robotState.RUN;


        for (int row = 0; row < numRows; row++) {
            for (int col = 0; col < numCols; col++) {
                occupancyGrid[row][col] = 0; // set to unoccupied
            }
        }

        // Mark the starting cell as visited
        occupancyGrid[numRows-1-(int)robotX][(int)robotY] = 3;

        // Print the array
        for (int row = 0; row < numRows; row++) {
            for (int col = 0; col < numCols; col++) {
                System.out.print(occupancyGrid[row][col] + " ");
            }
            System.out.println();
        }
        System.out.println();
    }

    @Override
    public void loop() throws Exception {
        super.loop();

        if (state == robotState.RUN) {
            // Get present distances from robot's left,front and right
            int[] d = proximitySensor.getProximity().distances();

            // Robot rotating way :- if distance from (any side +6) > GRID_SPACE then robot
            // will rotate that side.

            ArrayList<Integer> intList = new ArrayList<>();

            if (d[PROXIMITY_RIGHT] + 6 > GRID_SPACE) {
                intList.add(1);
            } 
            // else {
            //     occupancyGrid[numRows-1-(int)robotX][(int)robotY] = 3;
            // }
            
            if (d[PROXIMITY_FRONT] + 6 > GRID_SPACE) {intList.add(2);}
            
            if (d[PROXIMITY_LEFT] + 6 > GRID_SPACE) {intList.add(3);}

            Random rand = new Random();
            int randomIndex = rand.nextInt(intList.size()); // Generate a random index
            int randomElement = intList.get(randomIndex); // Get the element at the random index    

            if (randomElement==1) {
                // Right
                motion.rotateDegree(defaultRotateSpeed, 90);
                rightTurns++;

            } else if (randomElement==2) {
                // Front

            } else if (randomElement==3) {
                // Turn Left
                motion.rotateDegree(defaultRotateSpeed, -90);
                leftTurns++;
            } else {
                // If robot can't go left,right and front then robot will rotate to back.
                motion.rotateDegree(defaultRotateSpeed, 180);
            }

            // Robot move
            motion.moveDistance(defaultMoveSpeed, GRID_SPACE);
            delay(1000);

            // Determine the movement based on the sum of right and left turns modulo 4
            int direction = (rightTurns - leftTurns) % 4;

            // Adjust direction to ensure it's within the range of 0 to 3
            if (direction < 0) {
                direction += 4;
            }

            // Move based on the calculated direction
            switch (direction) {
                case 0: // Facing north
                    robotX++;
                    break;
                case 1: // Facing east
                    robotY++;
                    break;
                case 2: // Facing south
                    robotX--;
                    break;
                case 3: // Facing west
                    robotY--;
                    break;
            }     
                   
            // // Determine the movement based on the number of right and left turns
            // if (rightTurns % 4 == 0) { // If the number of right turns modulo 4 is 0 (facing north)
            //     if (leftTurns % 4 == 0) { // If the number of left turns modulo 4 is 0
            //         // Move north
            //         robotX++;
            //     } else if (leftTurns % 4 == 1) { // If the number of left turns modulo 4 is 1
            //         // Move west
            //         robotY--;
            //     } else if (leftTurns % 4 == 2) { // If the number of left turns modulo 4 is 2
            //         // Move south
            //         robotX--;
            //     } else if (leftTurns % 4 == 3) { // If the number of left turns modulo 4 is 3
            //         // Move east
            //         robotY++;
            //     }
            // } else if (rightTurns % 4 == 1) { // If the number of right turns modulo 4 is 1 (facing east)
            //     // Similar logic follows for other directions
            //     if (leftTurns % 4 == 0) {
            //         // Move east
            //         robotY++;
            //     } else if (leftTurns % 4 == 1) {
            //         // Move north
            //         robotX++;
            //     } else if (leftTurns % 4 == 2) {
            //         // Move west
            //         robotY--;
            //     } else if (leftTurns % 4 == 3) {
            //         // Move south
            //         robotX--;
            //     }
            // } else if (rightTurns % 4 == 2) { // If the number of right turns modulo 4 is 2 (facing south)
            //     if (leftTurns % 4 == 0) {
            //         // Move south
            //         robotX--;
            //     } else if (leftTurns % 4 == 1) {
            //         // Move east
            //         robotY++;
            //     } else if (leftTurns % 4 == 2) {
            //         // Move north
            //         robotX++;
            //     } else if (leftTurns % 4 == 3) {
            //         // Move west
            //         robotY--;
            //     }
            // } else if (rightTurns % 4 == 3) { // If the number of right turns modulo 4 is 3 (facing west)
            //     if (leftTurns % 4 == 0) {
            //         // Move west
            //         robotY--;
            //     } else if (leftTurns % 4 == 1) {
            //         // Move south
            //         robotX--;
            //     } else if (leftTurns % 4 == 2) {
            //         // Move east
            //         robotY++;
            //     } else if (leftTurns % 4 == 3) {
            //         // Move north
            //         robotX++;
            //     }
            // }

            // Change entries with value 3 to 1
            for (int i = 0; i < occupancyGrid.length; i++) {
                for (int j = 0; j < occupancyGrid[i].length; j++) {
                    if (occupancyGrid[i][j] == 3) {
                        occupancyGrid[i][j] = 1;
                    }
                }
            }

            occupancyGrid[numRows-1-(int)robotX][(int)robotY] = 3;
            
            // Print the array
            for (int row = 0; row < numRows; row++) {
                for (int col = 0; col < numCols; col++) {
                    System.out.print(occupancyGrid[row][col] + " ");
                }
                System.out.println();
            }
            System.out.println();
        }
    }
}