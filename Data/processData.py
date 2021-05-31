import os
import re
import math
import sys


def processFile(fileName, cityLimit):
    # Get the file Name
    sourceFile = open(sourcePath + "/" + fileName)
    # Get the Dimension
    line = sourceFile.readline().strip()
    while "DIMENSION" not in line:
        # Skip if Meet Error File
        if "EOF" in line:
            print("\033[1;35mError: DIMENSION Missing...Skip!\033[0m")
            return False
        # Get Next Line
        line = sourceFile.readline().strip()
    dimension = int(re.findall(r"\d+", line)[0])
    # Skip if the dimension is too large
    if dimension > cityLimit:
        print("\033[1;35mError: Beyond City Limit...Skip!\033[0m")
        return False

    # Get the startPoint
    line = sourceFile.readline().strip()
    while "NODE_COORD_SECTION" not in line:
        # Skip if Meet Error File
        if "EOF" in line:
            print("\033[1;35mError: NODE_COORD_SECTION Missing...Skip!\033[0m")
            return False
        # Get Next Line
        line = sourceFile.readline().strip()

    # Process the Coordination Data
    # Create the Distance Table
    print("- Fetching Coordination Data...", end="")
    table = []
    line = sourceFile.readline().strip()
    while len(line) != 0 and "EOF" not in line:
        readData = re.findall(r"\d+\.?\d*", line)
        # Switch String to Integer
        readData = [float(i) for i in readData]
        # Add into the array
        table.append(readData)
        # Get the Next Line
        line = sourceFile.readline().strip()
    print("Done!")

    # Create the Output File
    output = open(resultPath + "/distance_" + fileName[:-3] + "txt", "w")
    print("- Calculating Distance...", end="")
    # Write into file line by line
    for targetIndex in range(0, dimension - 1):
        # Prepared for writing data
        writeData = ""
        for destIndex in range(targetIndex + 1, dimension):
            # Calculate the Distance Data
            distance = round(math.sqrt(math.pow(table[destIndex][2] - table[targetIndex][2], 2)
                                       + math.pow(table[destIndex][1] - table[targetIndex][1], 2)))
            writeData = writeData + str(distance) + " "
            # Write into the file
        output.write(writeData + "\r")
    # Close the file
    print("Done!")
    output.close()
    return True


# Get the whole list of the files
sourcePath = "./source"
resultPath = "./output"
dirs = os.listdir(sourcePath)


# Record the process
processCount = 0
# Record the completed file
completeCount = 0

for file in dirs:
    # Add the fileCount
    processCount = processCount + 1
    print("Processing " + file + "(" + str(processCount) + "/" + str(len(dirs)) + ")...")
    # Skip the Hidden File
    if file.startswith("."):
        print("\033[1;35mFile " + file + " Error: Hidden File...\033[0m")
        continue
    else:
        # Process each file
        if processFile(fileName=file, cityLimit=5000):
            completeCount = completeCount + 1
    print("-" * 40)
# Draw Conclusion
print("Task Finished with " + str(completeCount) + " Finished, "
      + str(len(dirs) - completeCount) + " Skipped")
