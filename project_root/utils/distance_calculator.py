# Create a dictionary with real distances (in kilometers) between major Indian cities
# These are approximate road distances

CITY_DISTANCES = {
    ('Coimbatore', 'Palakkad'): 40,
    ('Coimbatore', 'Chennai'): 500,
    ('Coimbatore', 'Bangalore'): 330,
    ('Coimbatore', 'Mumbai'): 1200,
    ('Coimbatore', 'Delhi'): 2200,
    
    ('Palakkad', 'Chennai'): 530,
    ('Palakkad', 'Bangalore'): 370,
    ('Palakkad', 'Mumbai'): 1240,
    ('Palakkad', 'Delhi'): 2240,
    
    ('Chennai', 'Bangalore'): 350,
    ('Chennai', 'Mumbai'): 1350,
    ('Chennai', 'Delhi'): 2180,
    
    ('Bangalore', 'Mumbai'): 980,
    ('Bangalore', 'Delhi'): 2150,
    
    ('Mumbai', 'Delhi'): 1400
}

def get_distance(city1, city2):
    """Get the distance between two cities."""
    # Normalize city names for case-insensitive comparison
    city1 = city1.strip().title()
    city2 = city2.strip().title()
    
    # Check if the cities are the same
    if city1 == city2:
        return 0
    
    # Check if we have the distance in our dictionary (in either order)
    if (city1, city2) in CITY_DISTANCES:
        return CITY_DISTANCES[(city1, city2)]
    elif (city2, city1) in CITY_DISTANCES:
        return CITY_DISTANCES[(city2, city1)]
    else:
        # Fallback to an approximation if the cities aren't in our database
        # This is just a backup and should be avoided in production
        return 500  # Default distance in km