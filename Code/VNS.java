import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

/**
 * @author TingLuo
 */

public class VNS {
    /*** The TSP Question**/
    public static Problem currentProblem;
    public static ArrayList<int[]> local_min_VNS = new ArrayList<>(100);
    /*** Space for 2-opt**/
    private static int[] optimalState;
    private static int optimalDistance;

    /*** Initialize a solution : a cycle passing by all cities**/
    public static int[] generateRandom(Problem inputProblem) {
        // Container to Store the Order of the Cities
        int[] cities = new int[inputProblem.getNbCities()];
        // A Boolean Array to Prevent Duplicate
        boolean[] visited = new boolean[inputProblem.getNbCities()];
        // Int to Store Random Index
        int randomIndex = -1;
        // Trip Starts from Index 0
        cities[0] = 0;
        visited[0] = true;
        for (int i = 1; i < inputProblem.getNbCities(); i++) {
            randomIndex = getRandom(inputProblem.getNbCities());
            if (visited[randomIndex]) {
                // Repeat the process
                i--;
            } else {
                cities[i] = randomIndex;
                visited[randomIndex] = true;
            }
        }
        // Return the cities
        return cities;
    }

    /*** Get random between 0 and born -1**/
    public static int getRandom(int borne) {
        Random randomGenerator = new Random();
        int randomInt = randomGenerator.nextInt(borne);
        return randomInt;
    }

    /*** Calculate the distance**/
    public static int calDistance(int[] state) {
        int distance = 0;
        for (int i = 0; i < currentProblem.getNbCities() - 1; i++) {
            // Add edges one by one
            distance += currentProblem.getDistances()[state[i]][state[i + 1]];
        }
        // Add the last edge
        distance += currentProblem.getDistances()[state[0]][state[currentProblem.getNbCities() - 1]];
        return distance;
    }

    /*** Switch the Position of Two Nodes, Used in 1-Opt and 3-Opt**/
    private static void elementSwitch(int[] list, int index1, int index2) {
        int tmp = list[index1];
        list[index1] = list[index2];
        list[index2] = tmp;
    }

    public static void elementReverse(int[] cs, int a, int b) {
        while (b > a) {
            int temp = cs[b];
            cs[b] = cs[a];
            cs[a] = temp;
            b--;
            a++;
        }
    }

    /*** Print out the State**/
    public static void printSolution(int[] state, int distance) {
        // Print out the total distance
        System.out.print("Distance: " + distance + "Km | With Route: ");
        // Print out the route
        for (int i = 0; i < currentProblem.getNbCities(); i++) {
            System.out.print(state[i] + " -> ");
        }
        // Print out the starting point
        System.out.println(state[0]);
    }


    private static boolean findoptimal(int[] arr, int i, int j, int k) {
        int[] currentstate = new int[arr.length];
        int kplus = k + 1;
        if (k == arr.length - 1)
            kplus = 0;

        System.arraycopy(arr, 0, currentstate, 0, currentstate.length);

        Boolean found = false;
        int[] cases = new int[8];
        cases[0] = currentProblem.getEdgeDistance(arr[i], arr[i + 1]) +
                currentProblem.getEdgeDistance(arr[j], arr[j + 1]) +
                currentProblem.getEdgeDistance(arr[k], arr[kplus]);
        int currentmin = cases[0];

        cases[1] = currentProblem.getEdgeDistance(arr[i], arr[i + 1]) +
                currentProblem.getEdgeDistance(arr[j], arr[k]) +
                currentProblem.getEdgeDistance(arr[j + 1], arr[kplus]);

        if (currentmin > cases[1]) {
            elementReverse(currentstate, j + 1, k);
            currentmin = cases[1];
            found = true;
        }

        cases[2] = currentProblem.getEdgeDistance(arr[i], arr[j]) +
                currentProblem.getEdgeDistance(arr[i + 1], arr[j + 1]) +
                currentProblem.getEdgeDistance(arr[k], arr[kplus]);

        if (currentmin > cases[2]) {
            System.arraycopy(arr, 0, currentstate, 0, currentstate.length);
            elementReverse(currentstate, i + 1, j);
            currentmin = cases[2];
            found = true;
        }

        cases[3] = currentProblem.getEdgeDistance(arr[i], arr[j]) +
                currentProblem.getEdgeDistance(arr[i + 1], arr[k]) +
                currentProblem.getEdgeDistance(arr[j + 1], arr[kplus]);

        if (currentmin > cases[3]) {
            System.arraycopy(arr, 0, currentstate, 0, currentstate.length);
            elementReverse(currentstate, i + 1, j);
            elementReverse(currentstate, j + 1, k);
            currentmin = cases[3];
            found = true;
        }

        cases[4] = currentProblem.getEdgeDistance(arr[i], arr[j + 1]) +
                currentProblem.getEdgeDistance(arr[k], arr[i + 1]) +
                currentProblem.getEdgeDistance(arr[j], arr[kplus]);

        if (currentmin > cases[4]) {
            System.arraycopy(arr, 0, currentstate, 0, currentstate.length);

            elementReverse(currentstate, i + 1, j);
            elementReverse(currentstate, j + 1, k);
            elementReverse(currentstate, i + 1, k);

            currentmin = cases[4];
            found = true;
        }

        cases[5] = currentProblem.getEdgeDistance(arr[i], arr[j + 1]) +
                currentProblem.getEdgeDistance(arr[k], arr[j]) +
                currentProblem.getEdgeDistance(arr[i + 1], arr[kplus]);

        if (currentmin > cases[5]) {
            System.arraycopy(arr, 0, currentstate, 0, currentstate.length);
            elementReverse(currentstate, j + 1, k);
            elementReverse(currentstate, i + 1, k);
            currentmin = cases[5];
            found = true;
        }

        cases[6] = currentProblem.getEdgeDistance(arr[i], arr[k]) +
                currentProblem.getEdgeDistance(arr[j + 1], arr[i + 1]) +
                currentProblem.getEdgeDistance(arr[j], arr[kplus]);

        if (currentmin > cases[6]) {
            System.arraycopy(arr, 0, currentstate, 0, currentstate.length);
            elementReverse(currentstate, i + 1, j);
            elementReverse(currentstate, i + 1, k);
            currentmin = cases[6];
            found = true;
        }

        cases[7] = currentProblem.getEdgeDistance(arr[i], arr[k]) +
                currentProblem.getEdgeDistance(arr[j + 1], arr[j]) +
                currentProblem.getEdgeDistance(arr[i + 1], arr[kplus]);

        if (currentmin > cases[7]) {
            System.arraycopy(arr, 0, currentstate, 0, currentstate.length);
            elementReverse(currentstate, i + 1, k);
            found = true;
        }
        if (found) {
            System.arraycopy(currentstate, 0, arr, 0, currentstate.length);
        }
        return found;
    }

    private static boolean getNewState_1opt_steepest() {
        int[] currentOptimalState = optimalState;
        int currentOptimalDistance = optimalDistance;
        boolean optimalExist = false;
        int currentDistance;
        // Swap two nodes to get new state
        for (int i = 0; i < currentProblem.getNbCities() - 1; i++) {
            for (int j = i + 1; j < currentProblem.getNbCities(); j++) {
                elementSwitch(optimalState, i, j);
                // Calculate and compare the distance
                currentDistance = calDistance(optimalState);
                // Judge whether get the better solution
                if (currentDistance < currentOptimalDistance) {
                    currentOptimalDistance = currentDistance;
                    currentOptimalState = optimalState.clone();
                    optimalExist = true;
                }
                // Switch Back to the Original State
                elementSwitch(optimalState, i, j);
            }
        }
        // Do the Optimization
        if (optimalExist) {
            optimalState = currentOptimalState;
            optimalDistance = currentOptimalDistance;
            return true;
        } else {
            // If Current State is the Local Max/Min
            return false;
        }
    }

    private static boolean getNewState_2opt_steepest() {
        // Container to Store the Tmp Optimal Solution
        int[] currentOptimalState = optimalState;
        int currentOptimalDistance = optimalDistance;
        boolean optimalExist = false;
        int currentDistance;
        // Swap two nodes to get new state
        for (int i = 0; i < currentProblem.getNbCities() - 1; i++) {
            for (int j = i + 1; j < currentProblem.getNbCities(); j++) {
                elementReverse(optimalState, i, j);
                // Calculate and compare the distance
                currentDistance = calDistance(optimalState);
                // Judge whether get the better solution
                if (currentDistance < currentOptimalDistance) {
                    currentOptimalDistance = currentDistance;
                    currentOptimalState = optimalState.clone();
                    optimalExist = true;
                }
                // Switch Back to the Original State
                elementReverse(optimalState, i, j);
            }
        }
        // Do the Optimization
        if (optimalExist) {
            optimalState = currentOptimalState;
            optimalDistance = currentOptimalDistance;
            return true;
        } else {
            // If Current State is the Local Max/Min
            return false;
        }
    }

    public static boolean getNewState_3optplus_steepest() {
        int[] initial = new int[optimalState.length];
        int[] bestcandidate = new int[optimalState.length];
        System.arraycopy(optimalState, 0, initial, 0, initial.length);
        System.arraycopy(optimalState, 0, bestcandidate, 0, initial.length);
        int bestDistance = calDistance(bestcandidate);
        for (int i = 0; i < currentProblem.getNbCities() - 5; i++) {
            for (int j = i + 2; j < currentProblem.getNbCities() - 3; j++) {
                for (int k = j + 2; k < currentProblem.getNbCities(); k++) {
                    if (findoptimal(optimalState, i, j, k)) {
                        if (calDistance(optimalState) < bestDistance) {
                            System.arraycopy(optimalState, 0, bestcandidate, 0, initial.length);
                            bestDistance = calDistance(bestcandidate);
                        }
                        System.arraycopy(initial, 0, optimalState, 0, initial.length);
                    }
                }
            }
        }
        if (bestDistance < calDistance(optimalState)) {
            System.arraycopy(bestcandidate, 0, optimalState, 0, initial.length);
            optimalDistance = calDistance(optimalState);
            return true;
        }
        return false;
    }


    public static void VNS_solve() {
        // Start from a random solution(Order & State)
        optimalState = generateRandom(currentProblem);
        optimalDistance = calDistance(optimalState);

        int original_distance = optimalDistance;
        int[] initialstate = new int[optimalState.length];
        System.arraycopy(optimalState, 0, initialstate, 0, initialstate.length);

        while (true) {
//            printSolution(optimalState, optimalDistance);
            int k = 1;
            int[] storeOptimal = new int[optimalState.length];
            System.arraycopy(optimalState, 0, storeOptimal, 0, initialstate.length);//store x
            while (k < 4) {
                if (k == 1) {
                    int index1 = getRandom(optimalState.length - 5);
                    int index2 = getRandom(optimalState.length - 5 - index1) + index1 + 2;
                    int index3 = getRandom(optimalState.length - 3 - index2) + index2 + 2;
                    findoptimal(optimalState, index1, index2, index3);//x'
                    while (getNewState_3optplus_steepest()) ;//x''3993
                    if (calDistance(optimalState) < original_distance) {//x.distance
                        System.arraycopy(optimalState, 0, storeOptimal, 0, initialstate.length);
                        k = 1;
//                    printSolution(optimalState, optimalDistance);
                        original_distance = optimalDistance;
                    } else {
                        System.arraycopy(storeOptimal, 0, optimalState, 0, initialstate.length);
                        k++;
                        boolean existed_VNS = false;
                        for (int[] i : local_min_VNS) {
                            if (Arrays.equals(i, optimalState)) {
                                existed_VNS = true;
                                break;
                            }
                        }
                        if (!existed_VNS) {
                            int[] newBest = new int[optimalState.length];
                            System.arraycopy(optimalState, 0, newBest, 0, optimalState.length);
                            local_min_VNS.add(newBest);
                        }
                    }
                }

                if (k == 2) {
                    System.arraycopy(optimalState, 0, storeOptimal, 0, initialstate.length);
                    int index1 = getRandom(optimalState.length);
                    int index2 = getRandom(optimalState.length);
                    elementReverse(optimalState, index1, index2);
                    while (getNewState_2opt_steepest()) ;
                    if (calDistance(optimalState) < original_distance) {
                        System.arraycopy(optimalState, 0, storeOptimal, 0, initialstate.length);
                        k = 1;
//                    printSolution(optimalState, optimalDistance);
                        original_distance = optimalDistance;
                    } else {
                        System.arraycopy(storeOptimal, 0, optimalState, 0, initialstate.length);
                        k++;
                        boolean existed_VNS = false;
                        for (int[] i : local_min_VNS) {
                            if (Arrays.equals(i, optimalState)) {
                                existed_VNS = true;
                                break;
                            }
                        }
                        if (!existed_VNS) {
                            int[] newBest = new int[optimalState.length];
                            System.arraycopy(optimalState, 0, newBest, 0, optimalState.length);
                            local_min_VNS.add(newBest);
                        }
                    }
                }

                if (k == 3) {
                    System.arraycopy(optimalState, 0, storeOptimal, 0, initialstate.length);
                    int index1 = getRandom(optimalState.length);
                    int index2 = getRandom(optimalState.length);
                    elementSwitch(optimalState, index1, index2);
                    while (getNewState_1opt_steepest()) ;
                    if (calDistance(optimalState) < original_distance) {
                        System.arraycopy(optimalState, 0, storeOptimal, 0, initialstate.length);
                        k = 1;
                        original_distance = optimalDistance;
//                printSolution(optimalState, optimalDistance);
                    } else {
                        System.arraycopy(storeOptimal, 0, optimalState, 0, initialstate.length);
                        k++;
                        boolean existed_VNS = false;
                        for (int[] i : local_min_VNS) {
                            if (Arrays.equals(i, optimalState)) {
                                existed_VNS = true;
                                break;
                            }
                        }
                        if (!existed_VNS) {
                            int[] newBest = new int[optimalState.length];
                            System.arraycopy(optimalState, 0, newBest, 0, optimalState.length);
                            local_min_VNS.add(newBest);
                        }
                    }
                }
            }
            optimalDistance = calDistance(optimalState);
            break;
        }
    }

    public static void VNS_solve(Problem inputProblem, int[] route) {
        currentProblem = inputProblem;
        optimalState = route;
        VNS_solve();

    }

    public static void main(String[] args) {
        int nbCities = 127;
        String fileName = "./distances_between_cities_127.txt";
        // Define the TSP Problem
        currentProblem = new Problem(nbCities, fileName);
        currentProblem.constructDistances();
        int times = 0;
        long rtime;
        while (times < 50) {
            rtime = System.nanoTime();
            VNS_solve();
            System.out.println(System.nanoTime() - rtime);
            times++;
        }
//
    }
}
