import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

/**
 * @author FrankJin
 */

public class HillClimbing {
	/** The TSP Question **/
	public static Problem currentProblem;

	/** Space for 2-opt **/
	private static int[] optimalState;
	private static int optimalDistance;


	/**
	 * Init Function Below
	 * **/

    /** Initialize a solution : a cycle passing by all cities **/
    public static int[] generateRandom(Problem inputProblem) {
    	// Container to Store the Order of the Cities
		int[] state = new int[inputProblem.getNbCities()];
    	// A Boolean Array to Prevent Duplicate
		boolean[] visited = new boolean[inputProblem.getNbCities()];
		// Int to Store Random Index
		int randomIndex;
		// Trip Starts from Index 0
		state[0] = 0;
		visited[0] = true;
		// Start Random Selection
		for (int i = 1; i < inputProblem.getNbCities(); i++) {
			randomIndex = getRandom(inputProblem.getNbCities());
				if (visited[randomIndex]){
					// Repeat the process
					i--;
				}else{
					state[i] = randomIndex;
					visited[randomIndex] = true;
				}
			}
		// Return the cities
		return state;
		}



	/**
	 * Hill Climbing with 1-Opt Below
	 * **/

	/** Check the Neighbor State in 1-Opt: Simple Approach **/
	private static boolean getNewState_1opt_simple(){
		/*
		Difference Between 1Opt and 2Opt
		1Opt: Element Switch
		2Opt: Elements Reverse
	 	*/
		int currentDistance;
		// Swap two nodes to get new state
		for (int i = 0; i < currentProblem.getNbCities() - 1; i++) {
			for (int j = i + 1; j < currentProblem.getNbCities(); j++) {
				elementSwitch(optimalState, i, j);
				// Calculate and compare the distance
				currentDistance = calDistance(optimalState);
				// Judge whether get the better solution
				if (currentDistance < optimalDistance) {
					optimalDistance = currentDistance;
					return true;
				}else{
					// Switch Back to the Original State
					elementSwitch(optimalState, i, j);
				}
			}
		}
		// If Current State is the Local Max/Min
		return false;
	}

	/** Check the Neighbor State in 1-Opt: Steepest Approach **/
	private static boolean getNewState_1opt_steepest(){
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
		if(optimalExist){
			optimalState = currentOptimalState;
			optimalDistance = currentOptimalDistance;
			return true;
		}else{
			// If Current State is the Local Max/Min
			return false;
		}
	}


	/**
	 * Hill Climbing with 2-Opt Below
	 * Types: Simple, Steepest-Ascent, Stochastic
	 * **/

	/** Simple Hall Climbing: Check the Neighbor State in 2-Opt **/
	private static boolean getNewState_2opt_simple() {
    	int currentDistance;
		// Swap two nodes to get new state
		for (int i = 0; i < currentProblem.getNbCities() - 1; i++) {
			for (int j = i + 1; j < currentProblem.getNbCities(); j++) {
				elementReverse(optimalState, i, j);
				// Calculate and compare the distance
				currentDistance = calDistance(optimalState);
				// Judge whether get the better solution
				if (currentDistance < optimalDistance) {
					optimalDistance = currentDistance;
					return true;
				}else{
					// Switch Back to the Original State
					elementReverse(optimalState, i, j);
				}
			}
		}
		// If Current State is the Local Max/Min
		return false;
	}

	/** Steepest-Ascent Hall Climbing: Check the Neighbor State in 2-Opt **/
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
		if(optimalExist){
			optimalState = currentOptimalState;
			optimalDistance = currentOptimalDistance;
			return true;
		}else{
			// If Current State is the Local Max/Min
			return false;
		}
	}

	/** Stochastic Hall Climbing: Check the Neighbor State in 2-Opt **/
	private static boolean getNewState_2opt_random(int limit) {
		// Limit is Necessary: Counter will Increase 1 When Get a Bad Neighbour Solution
		int currentDistance;
		// Randomly Reverse Elements Between two nodes to get new state
		int counter = 0;
		// Container of Random Position Switching Variable
		int i,j;
		while (counter < limit){
			i = getRandom(currentProblem.getNbCities() - 1);
			// Prevent j == i Situation
			do{
				j = getRandom(currentProblem.getNbCities() - 1);
			}while(i == j);

			elementReverse(optimalState, i, j);
			// Calculate and compare the distance
			currentDistance = calDistance(optimalState);
			// Judge whether get the better solution
			if (currentDistance < optimalDistance) {
				optimalDistance = currentDistance;
				return true;
			}else{
				// Switch Back to the Original State
				elementReverse(optimalState, i, j);
				// Increase the Fail Counter
				counter++;
			}
		}
		// If Current State is the Local Max/Min
		return false;
	}
	/** Stochastic Hall Climbing With Default Iteration Limit */
	private static boolean getNewState_2opt_random() {
		// Here, I Set the limit as Half Amount of the Possible Solutions
		int limit = (int)Math.pow(currentProblem.getNbCities(), 2)/4;
		return getNewState_2opt_random(limit);
	}


	/**
	 * Hill Climbing with 3Opt Below
	 * 3Opt is Equal to 1Opt with Limitation
	 * **/

	private static boolean getNewState_3opt_simple(){
		int currentDistance;
		// Swap two nodes
		// Set First Node as i, Two Successors is i+1 and i+2
		for (int i = 0; i < currentProblem.getNbCities() - 5; i++) {
			// Set Second Node as j, Two successors is j+1 and j+2
			for (int j = i + 3; j < currentProblem.getNbCities() - 2; j++) {
				elementSwitch(optimalState, i + 1, j + 1);
				// Calculate and compare the distance
				currentDistance = calDistance(optimalState);
				// Judge whether get the better solution
				if (currentDistance < optimalDistance) {
					optimalDistance = currentDistance;
					return true;
				}else{
					// Switch Back to the Original State
					elementSwitch(optimalState, i, j);
				}
			}
		}
		// If Current State is the Local Max/Min
		return false;
	}


	/**
	 * Hill Climbing with 3-Opt Plus
	 */
	public static boolean getNewState_3opt_plus_simple(){
		for (int i = 0; i < currentProblem.getNbCities() - 5; i++) {
			for (int j = i + 2; j < currentProblem.getNbCities() - 3; j++) {
				for (int k = j + 2; k < currentProblem.getNbCities(); k++) {
					if (findOptimalCase(i, j, k)) {
						optimalDistance = calDistance(optimalState);
						return true;
					}
				}
			}
		}
		return false;
	}
	/** Function to Consider All the Situation in 3-Opt Plus
	 * FOR 3-OPT PLUS ONLY
	 * **/
	private static boolean findOptimalCase(int i,int j,int k){
		int[] currentstate = new int[optimalState.length];
		int kplus = k + 1;
		if (k == optimalState.length - 1){
			kplus = 0;
		}

		System.arraycopy(optimalState, 0, currentstate, 0, currentstate.length);

		Boolean found = false;
		int[] cases = new int[8];
		cases[0] = currentProblem.getEdgeDistance(optimalState[i], optimalState[i + 1]) +
				currentProblem.getEdgeDistance(optimalState[j], optimalState[j + 1]) +
				currentProblem.getEdgeDistance(optimalState[k], optimalState[kplus]);
		int currentmin = cases[0];

		cases[1] = currentProblem.getEdgeDistance(optimalState[i], optimalState[i + 1]) +
				currentProblem.getEdgeDistance(optimalState[j], optimalState[k]) +
				currentProblem.getEdgeDistance(optimalState[j + 1], optimalState[kplus]);

		if (currentmin > cases[1]) {
			elementReverse(currentstate, j + 1, k);
			currentmin = cases[1];
			found = true;
		}

		cases[2] = currentProblem.getEdgeDistance(optimalState[i], optimalState[j]) +
				currentProblem.getEdgeDistance(optimalState[i + 1], optimalState[j + 1]) +
				currentProblem.getEdgeDistance(optimalState[k], optimalState[kplus]);

		if (currentmin > cases[2]) {
			System.arraycopy(optimalState, 0, currentstate, 0, currentstate.length);
			elementReverse(currentstate, i + 1, j);
			currentmin = cases[2];
			found = true;
		}

		cases[3] = currentProblem.getEdgeDistance(optimalState[i], optimalState[j]) +
				currentProblem.getEdgeDistance(optimalState[i + 1], optimalState[k]) +
				currentProblem.getEdgeDistance(optimalState[j + 1], optimalState[kplus]);

		if (currentmin > cases[3]) {
			System.arraycopy(optimalState, 0, currentstate, 0, currentstate.length);
			elementReverse(currentstate, i + 1, j);
			elementReverse(currentstate, j + 1, k);
			currentmin = cases[3];
			found = true;
		}

		cases[4] = currentProblem.getEdgeDistance(optimalState[i], optimalState[j + 1]) +
				currentProblem.getEdgeDistance(optimalState[k], optimalState[i + 1]) +
				currentProblem.getEdgeDistance(optimalState[j], optimalState[kplus]);

		if (currentmin > cases[4]) {
			System.arraycopy(optimalState, 0, currentstate, 0, currentstate.length);

			elementReverse(currentstate, i + 1, j);
			elementReverse(currentstate, j + 1, k);
			elementReverse(currentstate, i + 1, k);

			currentmin = cases[4];
			found = true;
		}

		cases[5] = currentProblem.getEdgeDistance(optimalState[i], optimalState[j + 1]) +
				currentProblem.getEdgeDistance(optimalState[k], optimalState[j]) +
				currentProblem.getEdgeDistance(optimalState[i + 1], optimalState[kplus]);

		if (currentmin > cases[5]) {
			System.arraycopy(optimalState, 0, currentstate, 0, currentstate.length);
			elementReverse(currentstate, j + 1, k);
			elementReverse(currentstate, i + 1, k);
			currentmin = cases[5];
			found = true;
		}

		cases[6] = currentProblem.getEdgeDistance(optimalState[i], optimalState[k]) +
				currentProblem.getEdgeDistance(optimalState[j + 1], optimalState[i + 1]) +
				currentProblem.getEdgeDistance(optimalState[j], optimalState[kplus]);

		if (currentmin > cases[6]) {
			System.arraycopy(optimalState, 0, currentstate, 0, currentstate.length);
			elementReverse(currentstate, i + 1, j);
			elementReverse(currentstate, i + 1, k);
			currentmin = cases[6];
			found = true;
		}

		cases[7] = currentProblem.getEdgeDistance(optimalState[i], optimalState[k]) +
				currentProblem.getEdgeDistance(optimalState[j + 1], optimalState[j]) +
				currentProblem.getEdgeDistance(optimalState[i + 1], optimalState[kplus]);

		if (currentmin > cases[7]) {
			System.arraycopy(optimalState, 0, currentstate, 0, currentstate.length);
			elementReverse(currentstate, i + 1, k);
			found = true;
		}
		if (found) {
			System.arraycopy(currentstate, 0, optimalState, 0, currentstate.length);
		}
		return found;
	}



	/**
	 * Hill Climbing with City Insertion Below
	 * **/

	 private static boolean getNewState_insertion_simple(){
	 	int currentDistance;
		// Integer to Store the Edge(index - 1, index)
		 for(int insertPosition= 0; insertPosition < currentProblem.getNbCities(); insertPosition++){
			 // Integer to Store the Index of Insertion Value
			 for(int insertValueIndex = 0; insertValueIndex < currentProblem.getNbCities(); insertValueIndex++){
			 	// Two Special Cases:
			 	// #1: Skip the Adjacent Node
				if(insertPosition == insertValueIndex || insertPosition == insertValueIndex + 1){
					continue;
				}
				// #2: Solve the End -> Head Edge Problem
				 if(insertPosition == 0 && insertValueIndex == currentProblem.getNbCities() - 1){
				 	continue;
				 }

				 // Get the Array after the Insertion
				int[] currentState = insertArray(insertPosition, insertValueIndex);
				 // Calculate the Distance
				 currentDistance = calDistance(currentState);
				 // Judge whether get the better solution
				 if (currentDistance < optimalDistance) {
				 	// Copy to the optimalState
					 optimalState = currentState;
					 // Store the NewState's Distance
					 optimalDistance = currentDistance;
					 return true;
				 }
			 }
		 }
		 return false;
	 }

	 /** Solve the Problem in Tabu Search with 2Opt **/
	 public static void solve_tabu(boolean execAspiration, int tabuLength, int iterationLimit) {
		 // Define Last Best Neighbour
		 int[] lastBestNeighbour = new int[optimalState.length];
		 System.arraycopy(optimalState, 0, lastBestNeighbour, 0, optimalState.length);

		 // Record the Best Switch Index and Distance in the Current Iteration
		 int currentBestDistance;
		 Integer[] currentBestSwitch;

		 // Create the Tabu List to Store the Reversing Nodes
		 ArrayList<Integer[]> tabuList = new ArrayList<Integer[]>(tabuLength + 1);
		 tabuList.add(new Integer[]{lastBestNeighbour[0], lastBestNeighbour[1]});

		 // Loop until the End Situation
		 int currentDistance;
		 Integer[] currentSwitch;
		 int tabuIndex;
		 while (iterationLimit > 0) {
		 	// Init the First Neighbour of lastBestNeighbour as the Best Neighbour
			 elementReverse(lastBestNeighbour, 0, 1);
			 currentBestDistance = calDistance(lastBestNeighbour);
			 currentBestSwitch = new Integer[]{0, 1};
			 elementReverse(lastBestNeighbour, 0, 1);

			 // Use 2opt_steepest to Go through All the Neighbour
			 for (int i = 0; i < currentProblem.getNbCities() - 1; i++) {
				 for (int j = i + 1; j < currentProblem.getNbCities(); j++) {
					 elementReverse(lastBestNeighbour, i, j);
					 // Calculate and compare the distance
					 currentDistance = calDistance(lastBestNeighbour);
					 // Judge whether in the TabuList
					 currentSwitch = new Integer[]{i, j};
					 tabuIndex = inTabuList(tabuList, new Integer[]{lastBestNeighbour[currentSwitch[0]], lastBestNeighbour[currentSwitch[1]]});
					 if (tabuIndex == -1) {
						// Check whether Better than currentBestNeighbour
						if (currentDistance < currentBestDistance) {
							currentBestDistance = currentDistance;
							System.arraycopy(currentSwitch, 0, currentBestSwitch, 0, 2);
						}
						 // Judge whether obey the aspiration criteria
					 } else if (execAspiration && (currentDistance<optimalDistance)){
						 currentBestDistance = currentDistance;
						 System.arraycopy(currentSwitch, 0, currentBestSwitch, 0, 2);
						// Refresh the {i, j} in tabuList
						 tabuList.remove(tabuIndex);
						 tabuList.add(currentBestSwitch);
					 }
					 // Switch Back to the Original State
					 elementReverse(lastBestNeighbour, i, j);
				 }
			 }
			 // Perform Current Best Solution
			 elementReverse(lastBestNeighbour, currentBestSwitch[0], currentBestSwitch[1]);

			 // Judge Whether Greater than the OptimalState
			 if (currentBestDistance < optimalDistance){
			 	System.arraycopy(lastBestNeighbour, 0, optimalState, 0, optimalState.length);
			 	optimalDistance = currentBestDistance;
			 }
			 tabuList.add(new Integer[]{lastBestNeighbour[currentBestSwitch[0]], lastBestNeighbour[currentBestSwitch[1]]});
			 if(tabuList.size() > tabuLength) {
			 	tabuList.remove(0);
			 }
				 // Decrease Iteration Count
				 iterationLimit--;
		 }
	 }
	 /** Check Whether the Integer{i, j} is in the TabuList **/
	 private static int inTabuList(ArrayList<Integer[]> tabuList, Integer[] switchPoint){
		int tabuIndex = 0;
		for (Integer[] item: tabuList) {
			if (switchPoint[0].equals(item[0]) && switchPoint[1].equals(item[1])) {
				return tabuIndex;
			}
			tabuIndex++;
		}
		return -1;
	 }




	/**
	 * Function Methods Below
	 * **/

	/** Get random between 0 and borne -1 **/
	public static int getRandom(int borne){
		Random randomGenerator = new Random();
		return randomGenerator.nextInt(borne);
	}

	/** Calculate the Distance **/
	public static int calDistance(Problem inputProblem, int[] state) {
		int distance = 0;
		for (int i = 0; i < inputProblem.getNbCities() - 1; i++){
			// Add edges one by one
			distance += inputProblem.getDistances()[state[i]][state[i + 1]];
		}
		// Add the Last Edge
		distance += inputProblem.getDistances()[state[0]][state[inputProblem.getNbCities() - 1]];
		return distance;
	}
	public static int calDistance(int[] state) {
		return calDistance(currentProblem, state);
	}

	/** Switch the Position of Two Nodes, Used in 1-Opt and 3-Opt **/
	private static void elementSwitch(int[] list, int index1, int index2){
		int tmp = list[index1];
		list[index1] = list[index2];
		list[index2] = tmp;
	}

	/** Reverse the Route Between Two Nodes **/
	private static void elementReverse(int[] list, int index1, int index2){
		// Make Sure the index1 < index2, Especially for Random Approach
		if(index1 > index2){
			int tmp = index1;
			index1 = index2;
			index2 = tmp;
		}
		// Switch Elements One by One
		while(index1 < index2){
			elementSwitch(list, index1++, index2--);
		}
	}

	/** Insert Element into the Special Position **/
	private static int[] insertArray(int insertPosition, int insertValueIndex){
		// Generate the NewState
		int[] currentState = new int[currentProblem.getNbCities()];
		// Check the Order of Two Breakpoints
		if(insertPosition < insertValueIndex){
			// Start from the 0
			System.arraycopy(optimalState, 0, currentState, 0, insertPosition);
			// Insert into the insertPosition
			System.arraycopy(optimalState, insertValueIndex, currentState, insertPosition, 1);
			// Skip the insertValueIndex
			System.arraycopy(optimalState, insertPosition, currentState, insertPosition + 1, insertValueIndex - insertPosition);
			System.arraycopy(optimalState, insertValueIndex + 1, currentState, insertValueIndex + 1, currentProblem.getNbCities() - insertValueIndex - 1);
		}else{
			// Skip the insertValueIndex
			System.arraycopy(optimalState, 0, currentState, 0, insertValueIndex);
			System.arraycopy(optimalState, insertValueIndex + 1, currentState, insertValueIndex, insertPosition - insertValueIndex - 1);
			// Insert into the insertPosition
			System.arraycopy(optimalState, insertValueIndex, currentState, insertPosition - 1, 1);
			System.arraycopy(optimalState, insertPosition, currentState, insertPosition, currentProblem.getNbCities() - insertPosition - 1);
		}
		return currentState;
	}

	/** Print out the State With Calculated Distance **/
    public static void printSolution(int[] state, int distance) {
		// Print out the total distance
		System.out.print("Distance: " + distance + "Km | With Route: ");
		// Print out the route
		for (int i = 0; i < currentProblem.getNbCities(); i++){
			System.out.print(state[i] + " -> ");
		}
		// Print out the starting point
		System.out.println(state[0]);
    }
	/** Print out the State Without Distance: Calculate Distance First! **/
	public static void printSolution(int[] state){
		printSolution(state, calDistance(state));
	}



	/**
	 * Implementation Method Below
	 * **/

	/** Implement Different Neighbour Structures, Optimal Distance is Returned **/
	public static void solve(){
		// Start from a random solution(Order & State)
		optimalState = generateRandom(currentProblem);
		optimalDistance = calDistance(optimalState);

		// Print out the Initial State
		printSolution(optimalState, optimalDistance);

		// Find the Neighbour Solution
		while(true){
			if(getNewState_2opt_simple()){
				printSolution(optimalState, optimalDistance);
			}else{
				break;
			}
		}
	}

	/** FOR TEST: Output the Time of Each Iteration **/
	/** Mode 1: 1opt; Mode 2: 2opt; Mode 3: 3opt;
	 * Mode 4: insertion; Mode 5: 2opt_steepest; Mode 6: 2opt_random,
	 * Mode7: 3optPlus; Mode 8: tabu search **/
	public static void solve_loopTest(Problem inputProblem, int[] modeCollection, int loopTime){
		// Loop Test
		while (loopTime > 0) {
			// Create the random route
			int[] randomRoute = generateRandom(inputProblem);
			// Test Each Mode
			for (int mode: modeCollection){
				solve_singleTest(inputProblem, Arrays.copyOf(randomRoute, randomRoute.length), mode);
			}
			// Decrease the loopTime
			loopTime--;
		}


	}

	/** Test A Single Algorithm, Return the Cost Time **/
	public static void solve_singleTest(Problem inputProblem, int[] route, int mode){
		// Set the Problem
		currentProblem = inputProblem;
		optimalState = route;
		optimalDistance = calDistance(route);

		// Init the Iteration Counter & Timer
		int iterCount = 0;
		long startTime;

		// Print the Original State
//		System.out.println(mode + "\t" + 0 + "\t" + 0 + "\t" + optimalDistance);

		switch (mode) {
			// 1OPT_SINGLE
			case 1:
				// Start Timer
				startTime = System.nanoTime();
				while(true) {
					if (getNewState_1opt_simple()) {
						System.out.println(mode + "\t" + (++iterCount) + "\t" + (System.nanoTime() - startTime) + "\t" + optimalDistance);
					} else {
						break;
					}
				}
				break;

			// 2OPT_SINGLE
			case 2:
				// Start Timer
				startTime = System.nanoTime();
				while(true) {
					if (getNewState_2opt_simple()) {
						System.out.println(mode + "\t" + (++iterCount) + "\t" + (System.nanoTime() - startTime) + "\t" + optimalDistance);
					} else {
						break;
					}
				}
				break;

			// 3OPT_SINGLE
			case 3:
				// Start Timer
				startTime = System.nanoTime();
				while(true) {
					if (getNewState_3opt_simple()) {
						System.out.println(mode + "\t" + (++iterCount) + "\t" + (System.nanoTime() - startTime) + "\t" + optimalDistance);
					} else {
						break;
					}
				}
				break;

			// Insertion
			case 4:
				// Start Timer
				startTime = System.nanoTime();
				while(true) {
					if (getNewState_insertion_simple()) {
						System.out.println(mode + "\t" + (++iterCount) + "\t" + (System.nanoTime() - startTime) + "\t" + optimalDistance);
					} else {
						break;
					}
				}
				break;

			// 2OPT_STEEPEST
			case 5:
				// Start Timer
				startTime = System.nanoTime();
				while(true) {
					if (getNewState_2opt_steepest()) {
						System.out.println(mode + "\t" + (++iterCount) + "\t" + (System.nanoTime() - startTime) + "\t" + optimalDistance);
					} else {
						break;
					}
				}
				break;

			// 2OPT_RANDOM
			case 6:
				// Start Timer
				startTime = System.nanoTime();
				while(true) {
					if (getNewState_2opt_random()) {
						System.out.println(mode + "\t" + (++iterCount) + "\t" + (System.nanoTime() - startTime) + "\t" + optimalDistance);
					} else {
						break;
					}
				}
				break;

			// 3OPT_PLUS
			case 7:
				// Start Timer
				startTime = System.nanoTime();
				while(true) {
					if (getNewState_3opt_plus_simple()) {
						System.out.println(mode + "\t" + (++iterCount) + "\t" + (System.nanoTime() - startTime) + "\t" + optimalDistance);
					} else {
						break;
					}
				}
				break;

			// TABU_SEARCH With Aspiration Criteria
			case 8:
				// Start Timer
				startTime = System.nanoTime();
				solve_tabu(true, 30, 300);
				System.out.println(optimalDistance);
				break;

			// TABU_SEARCH Without Aspiration Criteria
			case 9:
				// Start Timer
				startTime = System.nanoTime();
				solve_tabu(false, 30, 300);
				System.out.println("Total Cost Time: " + (System.nanoTime() - startTime));
				break;
		}
	}


	/** Used in Generic Algorithm **/
	public static Solution solve_invoke(Problem inputProblem, Solution startPoint){
		// Start from a random solution(Order & State)
		currentProblem = inputProblem;
		optimalState = startPoint.route;
		optimalDistance = startPoint.distance;

		// Find the Neighbour Solution
		while(getNewState_2opt_simple()){
			continue;
		}
		return new Solution(optimalState, optimalDistance);
	}


	/**
	 * Main Method Below
	 * **/

	public static void main(String[] args) {
		int nbCities = 127;
		String fileName = "./distances_between_cities_127.txt";
		// Define the TSP Problem
		currentProblem = new Problem(nbCities, fileName);

		// Solve the Problem
		solve_loopTest(currentProblem, new int[]{8}, 30);
	}
}
