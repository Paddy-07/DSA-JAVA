from models.transport_system import TransportSystem
from utils.network_generator import create_fully_connected_network

def main():
    """Main function to run the transport system."""
    transport = TransportSystem()
    
    # Add cities
    cities = ['Coimbatore', 'Palakkad', 'Chennai', 'Bangalore', 'Mumbai', 'Delhi']
    for city in cities:
        transport.add_city(city)
    
    # Create a fully connected network
    transport = create_fully_connected_network(transport)

    transport.show_cities()

    start_city = input("\nEnter departure city: ").strip()
    end_city = input("Enter destination: ").strip()
    
    print("\nOptimization priority:")
    print("1. Time (fastest route)")
    print("2. Cost (cheapest route)")
    print("3. Comfort (most comfortable route)")
    
    priority_choice = input("Select priority (1-3, default is Time): ").strip()
    
    priority = "time"
    if priority_choice == '2':
        priority = "cost"
    elif priority_choice == '3':
        priority = "comfort"
        
    print(f"\nSelected Route: {start_city} to {end_city}")
    print(f"Optimization: {priority.capitalize()}")
    
    transport.book_trip(start_city, end_city, priority)


if __name__ == "__main__":
    main()