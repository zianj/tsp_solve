import java.util.*;

/**
 * @author FrankJin
 * @date May 25
 */
public class GeneticAlgorithm {
    // Declare the Optimal Solution
    /** The TSP Question **/
    public static Problem currentProblem;

    /** Space for Optimal Solution **/
    private static Solution optimalSolution;

    /** Generate initial population **/
    public static Solution[] initPopulation(Problem inputProblem, int popLimit) {
        // Create the Container
        Solution[] result = new Solution[popLimit];
        Solution newElement;
        // Generate the Random Solution: Method in HillClimbing
        for(int index = 0; index < popLimit; index++){
            newElement = new Solution(HillClimbing.generateRandom(inputProblem), inputProblem);
            // Prevent the Duplicated Solution: Create Again if have Duplicated
            if (haveDuplicated(result, newElement)) {
                index--;
            }else{
                result[index] = newElement;
            }
        }
        Arrays.sort(result);
        return result;
    }

    /** Get the Top-n From the Current and Next Population **/
    private static Solution[] getTopSolutions(Solution[] current, Solution[] generated, int popAmount){
        // Both Solution Array is Sorted
        // Set Pointer to Each Array
        int currPointer = 0;
        int genPointer = 0;
        // Create the output
        Solution[] result = new Solution[popAmount];
        // Fill into the result
        for (int index = 0; index < popAmount; index++) {
            if(current[currPointer].distance <= generated[genPointer].distance){
                // Prevent the Duplicated Solution: Create Again if have Duplicated
                if (haveDuplicated(result, current[currPointer])) {
                    // Skip Current Solution & Continue
                    currPointer++;
                    index--;
                }else{
                    result[index] = current[currPointer++];
                }

            }else{
                // Prevent the Duplicated Solution: Create Again if have Duplicated
                if (haveDuplicated(result, generated[genPointer])) {
                    // Skip Current Solution & Continue
                    genPointer++;
                    index--;
                }else{
                    result[index] = generated[genPointer++];
                }
            }
            // Prevent the IndexOutOfBound
            if (genPointer >= generated.length) {
                System.arraycopy(current, currPointer, result, index + 1, popAmount - 1 - index);
                return result;
            }
            if (currPointer >= current.length) {
                System.arraycopy(generated, genPointer, result, index + 1, popAmount - 1 - index);
                return result;
            }
        }
        return result;
    }


    /** Selection: Rank-Based Selection **/
    /** Return Two Parents Based On  **/
    public static Solution[] rankSelection(Solution[] currPop){
        // Get the City Amount
        int popAmount = currPop.length;
        int totalWeight = (1 + popAmount) * popAmount / 2;
        // Get 2 Parents
        int parentIndex_1 = rankParentIndex(popAmount, totalWeight);
        int parentIndex_2;
        // Make Sure Two Parents are Different
        do {
            parentIndex_2 = rankParentIndex(popAmount, totalWeight);
        } while(parentIndex_1 == parentIndex_2);
        return new Solution[]{currPop[parentIndex_1], currPop[parentIndex_2]};
    }

    /** Auxiliary Function For rankSelection: Generate the Random City Index **/
    private static int rankParentIndex(int popAmount, int totalWeight) {
        // Randomly Generate the Weight
        double randomWeight = Math.random() * (totalWeight + 1);
        // Find out the Living Range
        for (int rankWeight = popAmount; rankWeight > 0; rankWeight--) {
            // Reduce the rankWeight
            randomWeight = randomWeight - rankWeight;
            // Meet the Rank
            if (randomWeight <= 0) {
                return popAmount - rankWeight;
            }
        }
        return popAmount - 1;
    }
    private static int rankParentIndex(int cityAmount) {
        int totalWeight = (1 + cityAmount) * cityAmount / 2;
        return rankParentIndex(cityAmount, totalWeight);
    }


    /** CrossOver: Partial-Mapped Crossover **/
    public static Solution[] CrossOver_mapping(Problem inputProblem, Solution o1, Solution o2){
        // Get the City Amount
        int cityAmount = o1.route.length;
        // Generate Random Duration
        int startPoint = 1 + (int)(Math.random() * (cityAmount - 2));
        int duration = 1 + (int)(Math.random() * (cityAmount - startPoint - 1));
        // Create a ArrayList to Store the Mapping Relation
        ArrayList<Integer[]> mapTable = new ArrayList<Integer[]>(duration);
        // Add All the Element into the mapTable
        for (int index = startPoint; index < startPoint + duration; index++) {
            mapTable.add(new Integer[]{o2.route[index], o1.route[index]});
        }
        // Find Conflict from the End of the mapTable
        Integer[] conflictList, combineList;
        for (int conflictIndex = duration - 1; conflictIndex > 0; conflictIndex--) {
            conflictList = mapTable.get(conflictIndex);
            for (int combineIndex = 0; combineIndex < conflictIndex; combineIndex++) {
                combineList = mapTable.get(combineIndex);
                // Check Whether can be Combined
                if (conflictList[0].equals(combineList[1])){
                    // Assign the Value & Remove the conflictList
                    combineList[1] = conflictList[1];
                    mapTable.remove(conflictIndex);
                    break;
                }
                if (conflictList[1].equals(combineList[0])){
                    // Assign the Value & Remove the conflictList
                    combineList[0] = conflictList[0];
                    mapTable.remove(conflictIndex);
                    break;
                }
            }
        }
        // Build the OffSpring
        int[] new_o1_route = new int[cityAmount];
        int[] new_o2_route = new int[cityAmount];
        // Fill Route into the OffSprings
        System.arraycopy(o2.route, startPoint, new_o1_route, startPoint, duration);
        System.arraycopy(o1.route, startPoint, new_o2_route, startPoint, duration);
        for (int index = 0; index < startPoint; index++) {
            new_o1_route[index] = getMappingValue(mapTable, 1, o1.route[index]);
            new_o2_route[index] = getMappingValue(mapTable, 2, o2.route[index]);
        }
        for (int index = startPoint + duration; index < cityAmount; index++) {
            new_o1_route[index] = getMappingValue(mapTable, 1, o1.route[index]);
            new_o2_route[index] = getMappingValue(mapTable, 2, o2.route[index]);
        }
        // Create two Offspring
        Solution new_o1 = new Solution(new_o1_route, inputProblem);
        Solution new_o2 = new Solution(new_o2_route, inputProblem);
        return new Solution[]{new_o1, new_o2};
    }
    /** Check MapTable and Return the Replaced Value **/
    private static int getMappingValue(ArrayList<Integer[]> mapTable, int listOrder, int value) {
        // Define Mapping Order
        int mapIndex;
        if (listOrder == 1){
            mapIndex = 1;
        }else{
            mapIndex = 0;
        }
        // Go through the Whole List
        for(Integer[] item: mapTable) {
            if (item[listOrder - 1] == value){
                return item[mapIndex];
            }
        }
        return value;
    }

    /** Check Whether Have Duplicated Solution **/
    public static boolean haveDuplicated(Solution[] Collection, Solution element) {
        // Go through All the Collection
        for (Solution item: Collection) {
            // Prevent NullElementException
            if (item == null) {
                return false;
            }
            if(item.equals(element)) {
                return true;
            }
        }
        return false;
    }


    /** Method to Execute the GA **/
    public static boolean solve(Problem inputProblem, int popLimit, double mutChance, int iterLimit) {

        // Check All the Input Parameter
        // popLimit: Make as Even
        int evenBase = 2;
        if (popLimit % evenBase != 0){
            popLimit = popLimit + 1;
        }
        // mutChance: in the Range of 0% and 100%
        if (mutChance < 0 || mutChance > 1){
            System.out.println("Invalid Mutation Chance: " + mutChance);
            return false;
        }

        // Initialization
        Solution[] currentPop = initPopulation(inputProblem, popLimit);
        Solution[] generatedPop, nextPop;

        System.out.println(0 + "\t" +0+ "\t" +currentPop[0].distance);
        long time = System.nanoTime();

        // Loop For Counting Iteration
        for (int turn = 0; turn < iterLimit; turn++) {
            //System.out.println(currentPop[0].distance);
            // Initialization Next Generation
            generatedPop = new Solution[popLimit];

            // Loop for Generating Next Population
            Solution[] parent, offspring;
            for (int leftChild = popLimit; leftChild > 0; leftChild = leftChild - 2) {
                // Selection
                parent = rankSelection(currentPop);
                // CrossOver
                offspring = CrossOver_mapping(currentProblem, parent[0], parent[1]);
                // Prevent Duplicated Offspring
                if (haveDuplicated(generatedPop, offspring[0]) || haveDuplicated(generatedPop, offspring[1])) {
                    leftChild = leftChild + 2;
                    continue;
                }
                // Add Child into generatedPop
                generatedPop[leftChild - 1] = offspring[0];
                generatedPop[leftChild - 2] = offspring[1];
            }

            // Mutation For Each Solution
            Solution newElement;
            for (int index = 0; index < popLimit; index++){
                if(Math.random() < mutChance){
                    // Use Local Search to Finish Mutation
                    newElement = HillClimbing.solve_invoke(inputProblem, generatedPop[index]);
                    // Prevent Duplicated Offspring
                    if (!haveDuplicated(generatedPop, newElement)) {
                        generatedPop[index] = newElement;
                    }
                }
            }

            // Sort the Generated Array
            Arrays.sort(generatedPop);
            // Produce Next Generation
            nextPop = getTopSolutions(currentPop, generatedPop, popLimit);

            // Switch nextPop to currentPop
            currentPop = nextPop;
            System.out.println((System.nanoTime() - time) + "\t" + (turn+1) + "\t" + currentPop[0].distance);
        }
        // Store into the Static Variable
        optimalSolution = currentPop[0];
        return true;
    }


    /** Main Method **/
    public static void main(String[] args) {
        int nbCities = 127;
        String fileName = "./distances_between_cities_127.txt";
        // Define the TSP Problem
        currentProblem = new Problem(nbCities, fileName);

        // Execute the GA
        int populationLimit = 30;
        double mutationChance = 0.9;
        int iterationLimit = 60;
        for (int i = 0; i < 50; i++) {
            solve(currentProblem, populationLimit, mutationChance, iterationLimit);
        }

            
    }
}


class Solution implements Comparable<Solution>{
    /** Record Current Route **/
    int[] route;
    int distance;

    /** Constructors **/
    public Solution(int[] inputRoute, Problem inputProblem) {
        // Record the Distance by Calculation
        this.route = inputRoute;
        this.distance = HillClimbing.calDistance(inputProblem, inputRoute);
    }

    public Solution(int[] inputRoute, int inputDistance) {
        // Record the Distance by Input
        this.route = inputRoute;
        this.distance = inputDistance;
    }

    public boolean equals(Solution sol) {
        // Check the Route Length First
        if (this.route.length != sol.route.length){
            return false;
        }
        // Check the Distance
        else if (this.distance != sol.distance) {
            return false;
        }else{
            // Check the Route
            for (int index = 0; index < this.route.length; index++) {
                if (this.route[index] != sol.route[index]){
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public int compareTo(Solution o) {
        return this.distance - o.distance;
    }

    @Override
    public String toString() {
        return "Solution{" +
                "route=" + Arrays.toString(route) +
                ", distance=" + distance +
                '}';
    }
}