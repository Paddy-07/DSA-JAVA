class City:
    """Represents a city in the transportation network."""
    
    def __init__(self, name):
        self.name = name
        self.connections = []  # List of (destination, comfort, cost, duration)
        self.next_city = None
    
    def add_connection(self, destination, comfort, cost, duration):
        # Check if connection already exists
        for dest, _, _, _ in self.connections:
            if dest.lower() == destination.lower():
                return False
        self.connections.append((destination, comfort, cost, duration))
        return True