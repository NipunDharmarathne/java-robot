package Robots.samples;

import swarm.robot.VirtualRobot;

import java.util.ArrayList;
import java.util.Random;

public class MappingRobotRandomMovingNew extends VirtualRobot {

    // Size of a grid cell
    private final double GRID_SPACE = 18.000;

    // The default movement speed
    private final int defaultMoveSpeed = 200;

    // The default rotate speed
    private final int defaultRotateSpeed = 200;

    // Proximity Sensor options
    // Angles for left,front and right side rotating
    private int[] proximityAngles = {-90, 0, 90};

    // Index to get left proximity angle
    public static int PROXIMITY_LEFT = 0;

    // Index to get front proximity angle
    public static int PROXIMITY_FRONT = 1;

    // Index to get right proximity angle
    public static int PROXIMITY_RIGHT = 2;

    private final int GRID_ROWS = 10;
    private final int GRID_COLS = 10;

    // Robot's initial position
    int robotRow = GRID_ROWS;
    int robotCol = GRID_COLS;
    int robotId = 0;

    public MappingRobotRandomMovingNew(int id, double x, double y, double heading) {
        super(id, x, y, heading);
        robotId = id;
    }

    int numRows = GRID_ROWS * 2 + 1;
    int numCols = GRID_COLS * 2 + 1;
    int[][] occupancyGrid = new int[numRows][numCols];

    int rightTurns = 0;
    int leftTurns = 0;

    public static void printArray(int[][] array) {
        for (int[] row : array) {
            for (int element : row) {
                System.out.print(element + " ");
            }
            System.out.println();
        }
    }

    public static String arrayToString(int[][] arr) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < arr.length; i++) {
            for (int j = 0; j < arr[i].length; j++) {
                sb.append(arr[i][j]);
                if (j < arr[i].length - 1) {
                    sb.append(" "); // Add space between elements in the same row
                }
            }
            if (i < arr.length - 1) {
                sb.append("\n"); // Add newline between rows
            }
        }
        return sb.toString();
    }

    public static int[][] stringToArray(String arrayAsString) {
        String[] rows = arrayAsString.split("\n");
        int numRows = rows.length;
        int[][] array = new int[numRows][];
        for (int i = 0; i < numRows; i++) {
            String[] elements = rows[i].split(" ");
            int numCols = elements.length;
            array[i] = new int[numCols];
            for (int j = 0; j < numCols; j++) {
                array[i][j] = Integer.parseInt(elements[j]);
            }
        }
        return array;
    }

    public static int[][] getMergedMap(int[][] arr1, int[][] arr2) {
        int rows = arr1.length;
        int cols = arr1[0].length;

        int[][] mergedMap = new int[rows][cols];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                mergedMap[i][j] = Math.max(arr1[i][j], arr2[i][j]);
            }
        }
        return mergedMap;
    }

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
        int startingRow = numRows - 1 - robotRow;
        int startingCol = robotCol;

        occupancyGrid[startingRow][startingCol] = 3;

        // Print the array
        // System.out.println("Robot ID: " + robotId);
        // printArray(occupancyGrid);
        // System.out.println();
    }

    public void loop() throws Exception {
        super.loop();

        if (state == robotState.RUN) {
            // Get present distances from robot's left,front and right
            int[] d = proximitySensor.getProximity().distances();

            // Determine the movement based on the sum of right and left turns modulo 4
            int direction = (rightTurns - leftTurns) % 4;

            // Adjust direction to ensure it's within the range of 0 to 3
            if (direction < 0) {
                direction += 4;
            }

            ArrayList<Integer> intList = new ArrayList<>();

            // Robot rotating way :- if distance from (any side + 6) > GRID_SPACE then robot will rotate that side.

            //////////////////////////////////
            int rowShift = numRows - 1;
            int xVal = rowShift - robotRow;

            int count;
            //////////////////////////////////

            if (d[PROXIMITY_RIGHT] + 6 > GRID_SPACE) {
                intList.add(1);
                // System.out.println(d[PROXIMITY_RIGHT]);

                // Mark free spaces
                double proximityRangeRight = (d[PROXIMITY_RIGHT] + 6) / GRID_SPACE;

                switch (direction) {
                    case 0: // Facing north
                        count = 0;
                        for (int i = 0; i < proximityRangeRight; i++) {
                            // System.out.println(i);
                            occupancyGrid[xVal][robotCol + i] = 1;
                            count++;
                        }
                        if (robotCol + count + 1 < numCols && occupancyGrid[xVal][robotCol + count] == 0) {
                            occupancyGrid[xVal][robotCol + count] = 2;
                        }
                        break;
                    case 1: // Facing east
                        count = 0;
                        for (int i = 0; i < proximityRangeRight; i++) {
                            occupancyGrid[xVal - i][robotCol] = 1;
                            count++;
                        }
                        if (xVal - count < numRows && occupancyGrid[xVal - count][robotCol] == 0) {
                            occupancyGrid[xVal - count][robotCol] = 2;
                        }
                        break;
                    case 2: // Facing south
                        count = 0;
                        for (int i = 0; i < proximityRangeRight; i++) {
                            occupancyGrid[xVal][robotCol - i] = 1;
                            count++;
                        }
                        if (robotCol - count > 0 && occupancyGrid[xVal][robotCol - count] == 0) {
                            occupancyGrid[xVal][robotCol - count] = 2;
                        }
                        break;
                    case 3: // Facing west
                        count = 0;
                        for (int i = 0; i < proximityRangeRight; i++) {
                            occupancyGrid[xVal + i][robotCol] = 1;
                            count++;
                        }
                        if (xVal + count > 0 && occupancyGrid[xVal + count][robotCol] == 0) {
                            occupancyGrid[xVal + count][robotCol] = 2;
                        }
                        break;
                }
            } else {
                // Mark obstacles
                switch (direction) {
                    case 0: // Facing north
                        if (robotCol != numCols - 1 && occupancyGrid[xVal][robotCol + 1] == 0) {
                            occupancyGrid[xVal][robotCol + 1] = 2;
                        }
                        break;
                    case 1: // Facing east
                        if (robotRow != 0 && occupancyGrid[xVal - 1][robotCol] == 0) {
                            occupancyGrid[xVal - 1][robotCol] = 2;
                        }
                        break;
                    case 2: // Facing south
                        if (robotCol != 0 && occupancyGrid[xVal][robotCol - 1] == 0) {
                            occupancyGrid[xVal][robotCol - 1] = 2;
                        }
                        break;
                    case 3: // Facing west
                        if (robotRow != rowShift && occupancyGrid[xVal + 1][robotCol] == 0) {
                            occupancyGrid[xVal + 1][robotCol] = 2;
                        }
                        break;
                }
            }

            if (d[PROXIMITY_FRONT] + 6 > GRID_SPACE) {
                intList.add(2);

                // Mark free spaces
                double proximityRangeFront = (d[PROXIMITY_FRONT] + 6) / GRID_SPACE;

                switch (direction) {
                    case 0: // Facing north
                        count = 0;
                        for (int i = 0; i < proximityRangeFront; i++) {
                            occupancyGrid[xVal + i][robotCol] = 1;
                            count++;
                        }
                        if (xVal + count > 0 && occupancyGrid[xVal + count][robotCol] == 0) {
                            occupancyGrid[xVal + count][robotCol] = 2;
                        }
                        break;
                    case 1: // Facing east
                        count = 0;
                        for (int i = 0; i < proximityRangeFront; i++) {
                            occupancyGrid[xVal][robotCol + i] = 1;
                            count++;
                        }
                        if (robotCol + count < numCols && occupancyGrid[xVal][robotCol + count] == 0) {
                            occupancyGrid[xVal][robotCol + count] = 2;
                        }
                        break;
                    case 2: // Facing south
                        count = 0;
                        for (int i = 0; i < proximityRangeFront; i++) {
                            occupancyGrid[xVal - i][robotCol] = 1;
                            count++;
                        }
                        if (xVal - count < numRows && occupancyGrid[xVal - count][robotCol] == 0) {
                            occupancyGrid[xVal - count][robotCol] = 2;
                        }
                        break;
                    case 3: // Facing west
                        count = 0;
                        for (int i = 0; i < proximityRangeFront; i++) {
                            occupancyGrid[xVal][robotCol - i] = 1;
                            count++;
                        }
                        if (robotCol - count > 0 && occupancyGrid[xVal][robotCol - count] == 0) {
                            occupancyGrid[xVal][robotCol - count] = 2;
                        }
                        break;
                }
            } else {
                // Mark obstacles
                switch (direction) {
                    case 0: // Facing north
                        if (robotRow != rowShift && occupancyGrid[xVal + 1][robotCol] == 0) {
                            occupancyGrid[xVal + 1][robotCol] = 2;
                        }
                        break;
                    case 1: // Facing east
                        if (robotCol != numCols - 1 && occupancyGrid[xVal][robotCol + 1] == 0) {
                            occupancyGrid[xVal][robotCol + 1] = 2;
                        }
                        break;
                    case 2: // Facing south
                        if (robotRow != 0 && occupancyGrid[xVal - 1][robotCol] == 0) {
                            occupancyGrid[xVal - 1][robotCol] = 2;
                        }
                        break;
                    case 3: // Facing west
                        if (robotCol != 0 && occupancyGrid[xVal][robotCol - 1] == 0) {
                            occupancyGrid[xVal][robotCol - 1] = 2;
                        }
                        break;
                }
            }

            if (d[PROXIMITY_LEFT] + 6 > GRID_SPACE) {
                intList.add(3);

                // Mark free spaces
                double proximityRangeLeft = (d[PROXIMITY_LEFT] + 6) / GRID_SPACE;

                switch (direction) {
                    case 0: // Facing north
                        count = 0;
                        for (int i = 0; i < proximityRangeLeft; i++) {
                            occupancyGrid[xVal][robotCol - i] = 1;
                            count++;
                        }
                        if (robotCol - count > 0 && occupancyGrid[xVal][robotCol - count] == 0) {
                            occupancyGrid[xVal][robotCol - count] = 2;
                        }
                        break;
                    case 1: // Facing east
                        count = 0;
                        for (int i = 0; i < proximityRangeLeft; i++) {
                            occupancyGrid[xVal + i][robotCol] = 1;
                            count++;
                        }
                        if (xVal + count > 0 && occupancyGrid[xVal + count][robotCol] == 0) {
                            occupancyGrid[xVal + count][robotCol] = 2;
                        }
                        break;
                    case 2: // Facing south
                        count = 0;
                        for (int i = 0; i < proximityRangeLeft; i++) {
                            occupancyGrid[xVal][robotCol + i] = 1;
                            count++;
                        }
                        if (robotCol + count < numCols && occupancyGrid[xVal][robotCol + count] == 0) {
                            occupancyGrid[xVal][robotCol + count] = 2;
                        }
                        break;
                    case 3: // Facing west
                        count = 0;
                        for (int i = 0; i < proximityRangeLeft; i++) {
                            occupancyGrid[xVal - i][robotCol] = 1;
                            count++;
                        }
                        if (xVal - count < numRows && occupancyGrid[xVal - count][robotCol] == 0) {
                            occupancyGrid[xVal - count][robotCol] = 2;
                        }
                        break;
                }
            } else {
                // Mark obstacles
                switch (direction) {
                    case 0: // Facing north
                        if (robotCol != 0 && occupancyGrid[xVal][robotCol - 1] == 0) {
                            occupancyGrid[xVal][robotCol - 1] = 2;
                        }
                        break;
                    case 1: // Facing east
                        if (robotRow != rowShift && occupancyGrid[xVal + 1][robotCol] == 0) {
                            occupancyGrid[xVal + 1][robotCol] = 2;
                        }
                        break;
                    case 2: // Facing south
                        if (robotCol != numCols - 1 && occupancyGrid[xVal][robotCol + 1] == 0) {
                            occupancyGrid[xVal][robotCol + 1] = 2;
                        }
                        break;
                    case 3: // Facing west
                        if (robotRow != 0 && occupancyGrid[xVal - 1][robotCol] == 0) {
                            occupancyGrid[xVal - 1][robotCol] = 2;
                        }
                        break;
                }
            }

            Random rand = new Random();
            int randomIndex = rand.nextInt(intList.size()); // Generate a random index
            int randomElement = intList.get(randomIndex); // Get the element at the random index    

            if (randomElement == 1) {
                // Right
                motion.rotateDegree(defaultRotateSpeed, 90);
                rightTurns++;

            } else if (randomElement == 2) {
                // Front

            } else if (randomElement == 3) {
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
            direction = (rightTurns - leftTurns) % 4;

            // Adjust direction to ensure it's within the range of 0 to 3
            if (direction < 0) {
                direction += 4;
            }

            // Move based on the calculated direction
            switch (direction) {
                case 0: // Facing north
                    robotRow++;
                    break;
                case 1: // Facing east
                    robotCol++;
                    break;
                case 2: // Facing south
                    robotRow--;
                    break;
                case 3: // Facing west
                    robotCol--;
                    break;
            }

            // Change entries with value 3 to 1
            for (int i = 0; i < occupancyGrid.length; i++) {
                for (int j = 0; j < occupancyGrid[i].length; j++) {
                    if (occupancyGrid[i][j] == 3) {
                        occupancyGrid[i][j] = 1;
                    }
                }
            }

            occupancyGrid[xVal][robotCol] = 3;

            // Print the array
            System.out.println("Robot ID: " + robotId);
            printArray(occupancyGrid);
            System.out.println();

            // simpleComm.sendMessage(arrayToString(occupancyGrid), 200);
        }
    }

}