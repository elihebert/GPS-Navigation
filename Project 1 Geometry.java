public class Geometry {
    public static double getDistanceInMiles(double lat1, double long1, double lat2, double long2) {

        if (lat1 == lat2 && long1 == long2) return 0;

        /* Convert latitude and longitude to
        # spherical coordinates in radians. */
        double degrees_to_radians = Math.PI / 180.0;

        // phi = 90 - latitude
        double phi1 = (90.0 - lat1) * degrees_to_radians;
        double phi2 = (90.0 - lat2) * degrees_to_radians;

        // theta = longitude
        double theta1 = long1 * degrees_to_radians;
        double theta2 = long2 * degrees_to_radians;

        // Compute spherical distance from spherical coordinates.

        /* For two locations in spherical coordinates
        # (1, theta, phi) and (1, theta, phi)
        # cosine( arc length ) =
        #    sin phi sin phi' cos(theta-theta') + cos phi cos phi'
        # distance = rho * arc length */

        double cos = (Math.sin(phi1) * Math.sin(phi2) * Math.cos(theta1 - theta2) +
                Math.cos(phi1) * Math.cos(phi2));

        double arc = Math.acos(cos);

        /* Remember to multiply arc by the radius of the earth
        # in your favorite set of units to get length. */
        return arc * 3960;
    }

    public static double getDriveTimeInSeconds(double lat1, double long1, double lat2, double long2, int speedLimit)
    {
        return getDistanceInMiles(lat1, long1, lat2, long2) / speedLimit * 60 * 60;
    }

    public static double getDistanceInMiles(Road road, RoadNetwork graph)
    {
        Location start = graph.getLocationForId(road.startId());
        Location end = graph.getLocationForId(road.endId());
        return getDistanceInMiles(start.latitude(), start.longitude(), end.latitude(), end.longitude());
    }

    public static double getDistanceInMiles(Location start, Location end)
    {
        return getDistanceInMiles(start.latitude(), start.longitude(), end.latitude(), end.longitude());
    }

    public static double getDriveTimeInSeconds(Road road, RoadNetwork graph)
    {
        Location start = graph.getLocationForId(road.startId());
        Location end = graph.getLocationForId(road.endId());

        return getDriveTimeInSeconds(start.latitude(), start.longitude(), end.latitude(), end.longitude(), road.speedLimit());
    }


}

