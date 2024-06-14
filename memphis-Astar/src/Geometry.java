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
        return arc * 3960.0;
    }

    public static double getDriveTimeInSeconds(double lat1, double long1, double lat2, double long2, int speedLimit)
    {
        return getDistanceInMiles(lat1, long1, lat2, long2) / speedLimit * 60 * 60;
    }

    public static double getDistanceInMiles(Road road, RoadNetwork graph)
    {
        Location start = graph.getLocation(road.startId());
        Location end = graph.getLocation(road.endId());
        return getDistanceInMiles(start.latitude(), start.longitude(), end.latitude(), end.longitude());
    }

    public static double getDistanceInMiles(Location loc1, Location loc2) {
        double lat1 = loc1.latitude();
        double lon1 = loc1.longitude();
        double lat2 = loc2.latitude();
        double lon2 = loc2.longitude();
        return getDistanceInMiles(lat1, lon1, lat2, lon2);
    }

    public static double getDriveTimeInSeconds(Road road, RoadNetwork graph)
    {
        Location start = graph.getLocation(road.startId());
        Location end = graph.getLocation(road.endId());

        return getDriveTimeInSeconds(start.latitude(), start.longitude(), end.latitude(), end.longitude(), road.speedLimit());
    }


    /**
     * More custom version of getDriveTimeInSeconds, where the speed limit can be any number and not from the road
     * The starting and ending location is interchangable
     *
     * @param start The starting location.
     * @param end   The ending location.
     * @param graph The graph where these locations are hold
     * @param speed The custom speed limit
     * @return double
     */
    public static double predictTimeInSecFromLocations(Location start, Location end, RoadNetwork graph, int speed) {

        return getDriveTimeInSeconds(start.latitude(), start.longitude(), end.latitude(), end.longitude(), speed);
    }


    /**
     * The method that will give you the closest caredinal direction of two given point.
     * The order of the two locations will give you different answers. Use the first two
     * parameters as the perspective of the orientation.
     *
     * @param lat1 The latitude of point one
     * @param lon1 The longitude of point one
     * @param lat2 The latitude of point two
     * @param lon2 The longitude of point two
     * @return String
     */
    public static String compassDirection(double lat1, double lon1, double lat2, double lon2) {
        double latABS, lonABS, latDIFF, lonDIFF;
        boolean upDown = false;
        boolean leftRight = false;
        String direction = "";
        latDIFF = lat2 - lat1;
        lonDIFF = lon2 - lon1;
        latABS = Math.abs(latDIFF);
        lonABS = Math.abs(lonDIFF);

        if (latABS >= lonABS) { upDown = true;
        } else if (latABS < lonABS) { leftRight = true; }

        if (upDown) {
            if (latDIFF > 0) {
                direction += "north";
            } else if (latDIFF < 0) {
                direction += "south";
            }
        }

        if (leftRight) {
            if (lonDIFF > 0) {
                direction += "east";
            } else if (lonDIFF < 0) {
                direction += "west";
            }
        }
        return direction;
    }


    /**
     * Helper function for compassDirection. It will take two locations and search for
     * their latitude and longitude. Use the first parameter as the orientation for the
     * cardinal coordinates,
     * @param loc1 The first location.
     * @param loc2 The second location
     * @return String The respective cardinal coordinates as "north", "south", "east", or "west"
     */
    public static String compassDirection(Location loc1, Location loc2) {
        double lat1 = loc1.latitude();
        double lon1 = loc1.longitude();
        double lat2 = loc2.latitude();
        double lon2 = loc2.longitude();
        return compassDirection(lat1, lon1, lat2, lon2);
    }


    /**
     * This method will return the direction from location 1 to location 2 with respect of
     * location 1 orientated (facing towards) a given cardinal coordinate.
     *
     * @param orient_1to2 The respective orientation of loc1 ("north", "south", "east", or "west")
     * @param loc1 The location 1
     * @param loc2 The location 2
     * @return String
     */
    public static String compassDirLeftRight(String orient_1to2, Location loc1, Location loc2) {
        double lat1 = loc1.latitude();
        double lat2 = loc2.latitude();

        double lon1 = loc1.longitude();
        double lon2 = loc2.longitude();

        double latDIFF = lat2 - lat1;
        double lonDIFF = lon2 - lon1;

        if ((orient_1to2.equals("north") && lonDIFF > 0) ||
            (orient_1to2.equals("west")  && latDIFF > 0) ||
            (orient_1to2.equals("south") && lonDIFF < 0) ||
            ((orient_1to2.equals("east") && latDIFF < 0) )) {
            return "right";
        } else {
            return "left";
        }
    }
}
