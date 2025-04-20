import java.time.*;
import java.util.*;

class City {
    String name;
    List<Route> connections;
    City nextCity;

    public City(String name) {
        this.name = name;
        this.connections = new ArrayList<>();
        this.nextCity = null;
    }
}

class Route {
    String destination;
    int cost;
    int duration;

    public Route(String destination, String comfort, int cost, int duration) {
        this.destination = destination;
        this.cost = cost;
        this.duration = duration;
    }
}

class TransportSystem {
    private City head;
    private static final Map<String, Double> WEEKDAY_TRAFFIC = Map.of(
        "low", 0.8, "moderate", 1.0, "high", 1.3
    );
    private static final Map<String, Double> WEEKEND_TRAFFIC = Map.of(
        "low", 1.1, "moderate", 1.3, "high", 1.6
    );
    
    public void addCity(String name) {
        City city = new City(name);
        if (head == null) {
            head = city;
        } else {
            City temp = head;
            while (temp.nextCity != null) {
                temp = temp.nextCity;
            }
            temp.nextCity = city;
        }
    }
    
    public void addRoute(String start, String end, String comfort, int cost, int duration) {
        City origin = getCity(start);
        City destination = getCity(end);
        
        if (origin != null) {
            origin.connections.add(new Route(end, comfort, cost, duration));
        }
        if (destination != null) {
            destination.connections.add(new Route(start, comfort, cost, duration));
        }
    }
    
    public City getCity(String name) {
        City temp = head;
        while (temp != null) {
            if (temp.name.equals(name)) {
                return temp;
            }
            temp = temp.nextCity;
        }
        return null;
    }
    
    public void showCities() {
        City temp = head;
        System.out.println("\nCities Available for Booking:");
        while (temp != null) {
            System.out.println("- " + temp.name);
            temp = temp.nextCity;
        }
    }
    
    public TripResult calculateBestRoute(String start, String end) {
        LocalDateTime now = LocalDateTime.now();
        boolean weekend = now.getDayOfWeek() == DayOfWeek.SATURDAY || now.getDayOfWeek() == DayOfWeek.SUNDAY;
        Map<String, Double> trafficConditions = weekend ? WEEKEND_TRAFFIC : WEEKDAY_TRAFFIC;
        
        PriorityQueue<RouteNode> queue = new PriorityQueue<>(Comparator.comparingDouble(n -> n.totalDuration));
        queue.add(new RouteNode(0, start, new ArrayList<>(), new ArrayList<>()));
        Map<String, Double> visited = new HashMap<>();
        
        while (!queue.isEmpty()) {
            RouteNode node = queue.poll();
            
            if (visited.containsKey(node.current) && visited.get(node.current) <= node.totalDuration) {
                continue;
            }
            visited.put(node.current, node.totalDuration);
            node.path.add(node.current);
            
            if (node.current.equals(end)) {
                return new TripResult(node.totalDuration, node.path, node.trafficApplied);
            }
            
            City city = getCity(node.current);
            if (city != null) {
                for (Route route : city.connections) {
                    String traffic = getRandomTraffic();
                    double adjustedDuration = route.duration * trafficConditions.get(traffic);
                    List<String> newTrafficList = new ArrayList<>(node.trafficApplied);
                    newTrafficList.add(traffic);
                    queue.add(new RouteNode(node.totalDuration + adjustedDuration, route.destination, new ArrayList<>(node.path), newTrafficList));
                }
            }
        }
        return null;
    }
    
    public void bookTrip(String start, String destination) {
        TripResult bestRoute = calculateBestRoute(start, destination);
        if (bestRoute != null) {
            System.out.println("\nBooking Successful!");
            System.out.println("   Journey Route:");
            for (int i = 0; i < bestRoute.route.size() - 1; i++) {
                System.out.println("   " + bestRoute.route.get(i) + " → " + bestRoute.route.get(i + 1) + " (Traffic: " + bestRoute.trafficInfo.get(i) + ")");
            }
            System.out.println("   Total Adjusted Travel Time: " + (int) bestRoute.totalTime + " minutes");
        } else {
            System.out.println("\nNo route available for the selected cities.");
        }
    }
    
    private String getRandomTraffic() {
        List<String> keys = new ArrayList<>(WEEKDAY_TRAFFIC.keySet());
        return keys.get(new Random().nextInt(keys.size()));
    }
}

class RouteNode {
    double totalDuration;
    String current;
    List<String> path;
    List<String> trafficApplied;

    public RouteNode(double totalDuration, String current, List<String> path, List<String> trafficApplied) {
        this.totalDuration = totalDuration;
        this.current = current;
        this.path = path;
        this.trafficApplied = trafficApplied;
    }
}

class TripResult {
    double totalTime;
    List<String> route;
    List<String> trafficInfo;

    public TripResult(double totalTime, List<String> route, List<String> trafficInfo) {
        this.totalTime = totalTime;
        this.route = route;
        this.trafficInfo = trafficInfo;
    }
}

public class Main {
    public static void main(String[] args) {
        TransportSystem transport = new TransportSystem();
        
        for (String city : List.of("Coimbatore", "Palakkad", "Chennai", "Bangalore")) {
            transport.addCity(city);
        }
        
        List<Route> routes = List.of(
            new Route("Palakkad", "Premium", 350, 90),
            new Route("Chennai", "Economy", 800, 420),
            new Route("Bangalore", "Premium", 1000, 360),
            new Route("Coimbatore", "Economy", 900, 390)
        );
        
        transport.addRoute("Coimbatore", "Palakkad", "Premium", 350, 90);
        transport.addRoute("Palakkad", "Chennai", "Economy", 800, 420);
        transport.addRoute("Chennai", "Bangalore", "Premium", 1000, 360);
        transport.addRoute("Bangalore", "Coimbatore", "Economy", 900, 390);
        
        transport.showCities();
        
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter departure city: ");
        String startCity = scanner.nextLine().trim();
        System.out.print("Enter destination: ");
        String endCity = scanner.nextLine().trim();
        
        System.out.println("\nSelected Route: " + startCity + " to " + endCity);
        transport.bookTrip(startCity, endCity);
        
        scanner.close();
    }
}