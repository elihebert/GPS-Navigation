import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RoadNetwork {
    private Map<Long, Location> locations;
    private Map<Long, List<Road>> roads;

    public RoadNetwork() {
        locations = new HashMap<>();
        roads = new HashMap<>();
    }

    public Road getRoadBetween(Location loc1, Location loc2) {
        List<Road> adjacentRoads = ACTIONS(loc1);
        for (Road road : adjacentRoads) {
            if (road.endId() == loc2.id()) {
                return road;
            }
        }
        return null; //if no direct road found between loc1 and loc2
    }

    public void updateSpeedBoostForLocation(long id, boolean usedSpeedBoost) {
        Location loc = locations.get(id);
        if (loc != null) {
            loc.setUsedSpeedBoost(usedSpeedBoost);
        }
    }


    public void addLocation(Location loc) {
        locations.put(loc.id(), loc);
        roads.put(loc.id(), new ArrayList<>());
    }

    public void addRoad(Road road) {
        roads.get(road.startId()).add(road);
    }

    public Location getLocationForId(long id) {
        Location loc = locations.get(id);
        if (loc == null) throw new IllegalArgumentException("Location " + id + " doesn't exist in graph.");
        return loc;
    }

    //getAdjacentRoads
    public List<Road> ACTIONS(Location loc) {
        List<Road> r = roads.get(loc.id());
        if (r == null) throw new IllegalArgumentException("Location " + loc + " doesn't exist in graph.");
        return r;
    }

    public List<Road> ACTIONS(long id) {
        return ACTIONS(getLocationForId(id));
    }


}

