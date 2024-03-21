
import java.io.InputStream;
import java.util.*;

public class Main {
    private static boolean DEBUG = false;
    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);

        RoadNetwork graph = readGraph("memphis-medium.txt");

        System.out.print("Enter the starting location ID: ");
        long startId = Long.parseLong(scan.nextLine());
        Location startLocation = graph.getLocationForId(startId);

        System.out.print("Enter the ending location ID: ");
        long goalId = Long.parseLong(scan.nextLine());
        Location goalLocation = graph.getLocationForId(goalId);

        System.out.print("How many times are you allowed to speed?: ");
        int speedChoice = Integer.parseInt(scan.nextLine());


        System.out.print("Do you want debugging information (y/n)? ");
        char debugChoice = scan.nextLine().charAt(0);
        if (debugChoice == 'y' || debugChoice == 'Y') {
            DEBUG = true;
        }

        List<Location> path = aStarSearch(startLocation, goalLocation, graph, speedChoice);

        if (!path.isEmpty()) {
            double totalDriveTime = 0.0;

            System.out.println("\nRoute found is: ");
            for (Location loc : path) {
                //System.out.println(loc.id() + " (" + (loc.id() == startId ? "starting location" : loc.id()) + ")");
                System.out.println(loc.id() + " (" + (loc.id() == startId ? "starting location" : (loc.id() == goalId ? "ending location" : loc.id())) + ")");
            }
            System.out.println("\nGPS directions:");
            for (int i = 1; i < path.size(); i++) {
                Location currentLoc = path.get(i - 1);
                Location nextLoc = path.get(i);

                Road road = graph.getRoadBetween(currentLoc, nextLoc);
                if (road != null) {
                    boolean usedBoost = path.get(i - 1).usedSpeedBoost();
                    String direction = ""; //need to determine a direction based on previous, current and next roads
                    System.out.println("Head " + direction + " on " + road.name());

                    //double distance = Geometry.getDistanceInMiles(road, graph);
                    double distance = Geometry.getDistanceInMiles(currentLoc, nextLoc);
                    double time = Geometry.getDriveTimeInSeconds(road, graph);
                    if (currentLoc.usedSpeedBoost()) {
                        time /= 2;
                    }
                    totalDriveTime += time;
                    System.out.println("   Drive for " + String.format("%.2f", distance) + " miles (" + String.format("%.2f", time) + " seconds)");
                }
            }
            System.out.println("\nTotal travel time in seconds: " + totalDriveTime);
            System.out.println("Number of nodes visited: " + getNodesVisited());
            System.out.println("You have arrived!");
        } else {
            System.out.println("No path found between the locations.");
        }
    }

    private static List<Location> reconstructPath(Node goalNode) {
        List<Location> path = new ArrayList<>();
        Node currentNode = goalNode;
        while (currentNode != null) {
            path.add(currentNode.getLocation());
            currentNode = currentNode.getParent();
        }
        Collections.reverse(path);
        return path;
    }

    private static int nodesVisited;

    public static int getNodesVisited() {
        return nodesVisited;
    }


    public static List<Location> aStarSearch(Location start, Location goal, RoadNetwork graph, int speedChoice) {
        nodesVisited = 0;
        PriQueue<Node, Double> frontier = new PriQueue<>();
        Set<Location> closedList = new HashSet<>();
        Map<Location, Node> reached = new HashMap<>();

        //check value of canSpeed
        Node startNode = new Node(start, true,null, 0, Geometry.getDistanceInMiles(start, goal));
        frontier.add(startNode, startNode.getF_COST());
        reached.put(start, startNode);

        while (!frontier.isEmpty()) {
            Node currNode = frontier.remove();
            nodesVisited++;

            if (DEBUG) {
                System.out.println("Visiting [State=" + currNode.getLocation().getID() + ", parent=" + (currNode.getParent() == null ? "null" : currNode.getParent().getLocation().getID()) + ", g=" + currNode.getG_COST() + ", h=" + currNode.getH_COST() + ", f=" + currNode.getF_COST() + "]");
                //System.out.println("Visiting [State=" + currNode.getLocation().getID() + ", parent=" + ", g=" + currNode.getG_COST() + ", h=" + currNode.getH_COST() + ", f=" + currNode.getF_COST() + "]");
            }

            if (currNode.getLocation().equals(goal)) {
                return reconstructPath(currNode);
            }

            closedList.add(currNode.getLocation());
            expand(currNode, frontier, closedList, reached, graph, goal, speedChoice);
        }
        return new ArrayList<>();
    }

    public static void expand(Node currNode, PriQueue<Node, Double> frontier, Set<Location> closedList,
                              Map<Location, Node> reached, RoadNetwork graph, Location goal, int speedChoice) {
        List<Road> adjacentRoads = graph.ACTIONS(currNode.getLocation());

        for (Road road : adjacentRoads) {
            Location heirLocation = graph.getLocationForId(road.endId());
            if (heirLocation.equals(currNode.getLocation())) {
                heirLocation = graph.getLocationForId(road.startId());
            }
            //speed expansion
            expandForAction(currNode, heirLocation, graph, goal, frontier, closedList, reached, true);
            //is there is no speed boost used
            if (currNode.getTimesSped() < speedChoice) {
                expandForAction(currNode, heirLocation, graph, goal, frontier, closedList, reached, false);
            }
        }
    }
    private static void expandForAction(Node currNode, Location heirLocation, RoadNetwork graph, Location goal,
                                        PriQueue<Node, Double> frontier, Set<Location> closedList,
                                        Map<Location, Node> reached, boolean useSpeedBoost) {

        boolean shouldSpeed = useSpeedBoost && (currNode.getTimesSped() < 1);
        //potential goal
        double potentialG = currNode.getG_COST() + actionCost(currNode.getLocation(), heirLocation, graph, shouldSpeed);

        if (DEBUG) {
            String actionDescriptor = useSpeedBoost ? " with speed boost" : "";
            if (closedList.contains(heirLocation)) {
                System.out.println("    Skipping [State=" + heirLocation.getID() + actionDescriptor + ","  + "," + currNode.getLocation().getSpeedAmount() +  ", parent=" + currNode.getLocation().getID() + ", g=" + potentialG + ", h=" + heuristic(heirLocation, goal) + ", f=" + (potentialG + heuristic(heirLocation, goal)) + "] (already on closed list).");
            } else if (reached.containsKey(heirLocation) && reached.get(heirLocation).getG_COST() <= potentialG) {
                System.out.println("    Skipping [State=" + heirLocation.getID() + actionDescriptor + ", parent=" + currNode.getLocation().getID() + ", g=" + potentialG + ", h=" + heuristic(heirLocation, goal) + ", f=" + (potentialG + heuristic(heirLocation, goal)) + "] (already on frontier with lower cost).");
            } else {
                System.out.println("    Adding [State=" + heirLocation.getID() + actionDescriptor + ", parent=" + currNode.getLocation().getID() + ", g=" + potentialG + ", h=" + heuristic(heirLocation, goal) + ", f=" + (potentialG + heuristic(heirLocation, goal)) + "] to frontier.");
            }
        }

        if (closedList.contains(heirLocation)) {
            return;
        }

        Node existingNode = reached.get(heirLocation);
        if (existingNode == null) {
            Node successor = new Node(heirLocation, shouldSpeed, currNode, potentialG, heuristic(heirLocation, goal));
            if (shouldSpeed) {
                successor.timesSped++;
            }
            frontier.add(successor, successor.getF_COST());
            reached.put(heirLocation, successor);
        } else if (potentialG < existingNode.getG_COST()) {
            existingNode.setParent(currNode);
            existingNode.setG(potentialG);
            existingNode.setF(potentialG + existingNode.getH_COST());
            existingNode.setUsedSpeedBoost(shouldSpeed);
            if (shouldSpeed) {
                existingNode.timesSped++;
            }
            frontier.changePriority(existingNode, existingNode.getF_COST());
        }
    }

    //FOR PART B
    private static class Node {
        private final Location location; //essentially STATE
        private int timesSped;
        private final boolean usedSpeedBoost;
        private Node parent;
        private double G_COST;
        private final double H_COST;
        private int speedAllowance;

        public Node(Location location, boolean useSpeedBoost, Node parent, double G_COST, double H_COST) {
            this.location = location;
            //this.timesSped = useSpeedBoost ? 1 : 0; //if useSpeedBoost is true/used, increment
            this.timesSped = (parent != null ? parent.timesSped : 0) + (useSpeedBoost ? 1 : 0);
            this.usedSpeedBoost = useSpeedBoost;
            this.parent = parent;
            this.G_COST = G_COST;
            this.H_COST = H_COST;
        }

        public int getTimesSped() {
            return timesSped;
        }

        public double getF_COST() {
            return G_COST + H_COST;
        }

        public double getG_COST() {
            return G_COST;
        }

        public double getH_COST() {
            return H_COST;
        }

        public Location getLocation() {
            return location;
        }

        public Node getParent() {
            return parent;
        }

        public void setParent(Node parent) {
            this.parent = parent;
        }

        public void setG(double G_COST) {
            this.G_COST = G_COST;
        }

        private void setF(double f) {
            this.G_COST = f - this.H_COST;//this.f = f;
        }

        public void setUsedSpeedBoost(boolean useSpeedBoost) {
            if (useSpeedBoost) this.timesSped++;
        }
    }

    private static double actionCost(Location from, Location to, RoadNetwork graph, boolean useSpeedBoost) {
        List<Road> roads = graph.ACTIONS(from.getID());
        for (Road road : roads) {
            if (road.endId() == to.getID()) {
                double time = Geometry.getDriveTimeInSeconds(road, graph);
                return useSpeedBoost ? time / 2 : time;
            }
        }
        throw new IllegalArgumentException("No road from " + from + " to " + to);
    }

    public static double heuristic(Location current, Location goal) {
        return Geometry.getDistanceInMiles(current, goal);
    }

    public static RoadNetwork readGraph(String filename)
    {
        InputStream is = Main.class.getResourceAsStream(filename);
        if (is == null) {
            System.err.println("Bad filename: " + filename);
            System.exit(1);
        }
        Scanner scan = new Scanner(is);

        RoadNetwork graph = new RoadNetwork();

        while (scan.hasNextLine()) {
            String line = scan.nextLine();
            String[] pieces = line.split("\\|");

            int speed = 0;
            if (pieces[0].equals("location")) {
                long id = Long.parseLong(pieces[1]);
                double lat = Double.parseDouble(pieces[2]);
                double longi = Double.parseDouble(pieces[3]);
                Location loc = new Location(id, speed, lat, longi, false);
                graph.addLocation(loc);
            } else if (pieces[0].equals("road")) {
                long startId = Long.parseLong(pieces[1]);
                long endId = Long.parseLong(pieces[2]);
                speed = Integer.parseInt(pieces[3]);
                String name = pieces[4];
                Road r1 = new Road(startId, endId, speed, name);
                Road r2 = new Road(endId, startId, speed, name);
                graph.addRoad(r1);
                graph.addRoad(r2);
            }
        }
        scan.close();
        return graph;
    }
}

