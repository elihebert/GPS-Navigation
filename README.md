# GPS-Navigation
Program that uses the A* algorithm to find the fastest route between two locations in Memphis. Program will print out the route as a list of GPS-style navigation directions.

# Project: Fastest Route Finder
# Overview
This project develops a program that efficiently computes the fastest driving route between two points using graph algorithms. By leveraging the A* algorithm and considering real-world constraints like speed limits, the project aims to provide quick and accurate navigation solutions.

# Objectives
To read and process the memphis-medium.txt map, applying concepts from initial graph theory projects.
To interactively query the user for start and end locations within the map and optionally enable debugging output for detailed execution insights.
To implement the A* search algorithm to determine the fastest route between the specified locations, using road speed limits to estimate travel times.
To output the most efficient route, detailing the sequence of traversed locations and streets, the total number of nodes visited, and the total travel time in seconds.
# Functional Requirements
# Route Calculation
The program shall calculate the fastest route based on the A* algorithm, factoring in the speed limits of each road segment to estimate travel times.

# User Interaction
Users will input the starting and ending location IDs for their desired route.
Users can opt-in to receive debugging information during the route calculation process.

# Output Specifications
Display the fastest route as a sequence of location IDs and street names.
Include aggregate data such as the total number of nodes visited and the total time taken to traverse the route.

# Debugging Features
When enabled, the program will provide detailed information about each node's processing and child node generation, including state IDs, parent state IDs, and f(n), g(n), and h(n) values.

# Implementation Notes
The program can be developed in Java, Python, or C++, with openness to other languages upon consultation.
Utilize efficient data structures for graph representation and node management to ensure responsive computation, even with large datasets.
For the heuristic function in the A* algorithm, assume straight-line travel at 65 mph to avoid overestimating travel times.

# Advanced Features
Speed Override
Implement a feature allowing the user to specify a number of instances where the speed limit can be exceeded, representing the agent's ability to speed under certain conditions.
Adjust the route calculation to account for this capability, including modifications to state representation and action definitions.







