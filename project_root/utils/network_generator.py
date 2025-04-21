import random
from utils.distance_calculator import get_distance

def create_fully_connected_network(transport):
    """Create a fully connected network where each city connects to every other city."""
    # Get all cities
    cities = []
    temp = transport.head
    while temp:
        cities.append(temp.name)
        temp = temp.next_city
    
    # Create comfort levels
    comfort_levels = ['Economy', 'Standard', 'Premium', 'Express']
    
    # Connect every city with every other city
    for i in range(len(cities)):
        for j in range(i + 1, len(cities)):
            # Get real distance between cities (in kilometers)
            distance = get_distance(cities[i], cities[j])
            
            # Calculate cost and duration based on distance
            # Cost: ₹15 per kilometer
            base_cost = distance * 15
            # Duration: roughly 1 minute per kilometer
            base_duration = distance
            
            # Select a random comfort level
            comfort = random.choice(comfort_levels)
            
            # Add the route
            transport.add_route(cities[i], cities[j], comfort, base_cost, base_duration)
    
    return transport