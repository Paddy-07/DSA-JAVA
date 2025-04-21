from flask import Flask, request, jsonify, render_template, send_from_directory
from flask_cors import CORS
import os
import json

# Import your existing transport system
from models.transport_system import TransportSystem
from utils.network_generator import create_fully_connected_network

app = Flask(__name__, static_folder='static', template_folder='templates')
CORS(app)  # Enable CORS for all routes

# Create a global instance of the transport system
transport = TransportSystem()

# Initialize the transport system with cities
def initialize_transport():
    cities = ['Coimbatore', 'Palakkad', 'Chennai', 'Bangalore', 'Mumbai', 'Delhi']
    for city in cities:
        transport.add_city(city)
    return create_fully_connected_network(transport)

# Initialize on startup
transport = initialize_transport()

@app.route('/')
def index():
    """Serve the main HTML page"""
    return render_template('index.html')

@app.route('/api/cities', methods=['GET'])
def get_cities():
    """Return all available cities"""
    cities = []
    temp = transport.head
    while temp:
        cities.append(temp.name)
        temp = temp.next_city
    return jsonify(cities)

@app.route('/api/book', methods=['POST'])
def book_trip():
    """Book a trip between two cities"""
    data = request.get_json()
    
    if not data:
        return jsonify({"error": "No data provided"}), 400
        
    start = data.get('departure')
    destination = data.get('destination')
    priority = data.get('priority', 'time')
    
    if not start or not destination:
        return jsonify({"error": "Both departure and destination cities must be provided"}), 400
        
    if start.lower() == destination.lower():
        return jsonify({"error": "Departure and destination cities cannot be the same"}), 400
    
    # Calculate the best route
    result = transport.calculate_best_route(start, destination, priority)
    
    if not result:
        return jsonify({"error": f"No route available from {start} to {destination}"}), 404
    
    # Format the response
    route = result['route']
    traffic_info = result['traffic_applied']
    costs = result['costs']
    durations = result['durations']
    comfort_levels = result['comfort_levels']
    total_cost = round(result['total_cost'], 2)
    total_time = int(result['total_duration'])
    
    # Calculate comfort score
    comfort_scores = [transport.comfort_levels[c]['comfort_score'] for c in comfort_levels]
    avg_comfort = sum(comfort_scores) / len(comfort_scores) if comfort_scores else 0
    
    # Generate booking reference
    import random
    booking_ref = f"BK{random.randint(10000, 99999)}"
    
    # Generate weather warning (simulated)
    weather_warning = None
    if random.random() < 0.3:
        weather_conditions = ["rain", "fog", "snow", "high winds"]
        weather = random.choice(weather_conditions)
        weather_warning = f"Expect {weather} along parts of this route."
    
    # Build segments
    segments = []
    for i in range(len(route) - 1):
        segments.append({
            "start": route[i],
            "end": route[i+1],
            "comfort": comfort_levels[i],
            "cost": round(costs[i], 2),
            "duration": int(durations[i]),
            "traffic": traffic_info[i]
        })
    
    response = {
        "booking_ref": booking_ref,
        "journey": {
            "departure": start,
            "destination": destination,
            "priority": priority
        },
        "route": route,
        "segments": segments,
        "total_cost": total_cost,
        "total_time": total_time,
        "comfort_rating": round(avg_comfort, 1),
        "weather_warning": weather_warning
    }
    
    return jsonify(response)

if __name__ == '__main__':
    # Create directories if they don't exist
    os.makedirs('static', exist_ok=True)
    os.makedirs('templates', exist_ok=True)
    
    app.run(debug=True, port=5000)