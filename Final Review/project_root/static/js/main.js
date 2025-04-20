// Display current date
const displayDate = () => {
    const now = new Date();
    const options = { weekday: 'long', year: 'numeric', month: 'long', day: 'numeric' };
    document.getElementById('current-date').textContent = now.toLocaleDateString('en-US', options);
};

// Fetch cities from the API
const fetchCities = async () => {
    try {
        const response = await fetch('/api/cities');
        if (!response.ok) {
            throw new Error('Failed to fetch cities');
        }
        const cities = await response.json();
        populateCities(cities);
    } catch (error) {
        console.error('Error fetching cities:', error);
        showError('Unable to load cities. Please refresh the page and try again.');
    }
};

// Populate city lists
const populateCities = (cities) => {
    const cityList = document.getElementById('city-list');
    const departureSelect = document.getElementById('departure');
    const destinationSelect = document.getElementById('destination');
    
    // Clear existing options (keeping the first one)
    while (departureSelect.options.length > 1) {
        departureSelect.remove(1);
    }
    
    while (destinationSelect.options.length > 1) {
        destinationSelect.remove(1);
    }
    
    // Clear city pills
    cityList.innerHTML = '';
    
    // Add city pills and options to selects
    cities.forEach(city => {
        // Add city pill
        const cityPill = document.createElement('span');
        cityPill.className = 'city-pill';
        cityPill.textContent = city;
        cityList.appendChild(cityPill);
        
        // Add to departure select
        const departureOption = document.createElement('option');
        departureOption.value = city;
        departureOption.textContent = city;
        departureSelect.appendChild(departureOption);
        
        // Add to destination select
        const destinationOption = document.createElement('option');
        destinationOption.value = city;
        destinationOption.textContent = city;
        destinationSelect.appendChild(destinationOption);
    });
};

// Handle priority selection
document.querySelectorAll('.priority-option').forEach(option => {
    option.addEventListener('click', function() {
        // Remove selected class from all options
        document.querySelectorAll('.priority-option').forEach(opt => {
            opt.classList.remove('selected');
        });
        
        // Add selected class to clicked option
        this.classList.add('selected');
        
        // Update hidden input value
        document.getElementById('priority').value = this.getAttribute('data-value');
    });
});

// Show error message
const showError = (message) => {
    const errorDiv = document.createElement('div');
    errorDiv.className = 'error-message';
    errorDiv.textContent = message;
    errorDiv.style.display = 'block';
    
    // Add before the form submit button
    const formButton = document.querySelector('#booking-form .btn');
    formButton.parentNode.insertBefore(errorDiv, formButton);
    
    // Auto-remove after 5 seconds
    setTimeout(() => {
        errorDiv.remove();
    }, 5000);
};

// Form submission
document.getElementById('booking-form').addEventListener('submit', async function(e) {
    e.preventDefault();
    
    // Remove any existing error messages
    document.querySelectorAll('.error-message').forEach(el => el.remove());
    
    const departure = document.getElementById('departure').value;
    const destination = document.getElementById('destination').value;
    const priority = document.getElementById('priority').value;
    
    if (!departure || !destination) {
        showError('Please select both departure and destination cities');
        return;
    }
    
    if (departure === destination) {
        showError('Departure and destination cities cannot be the same');
        return;
    }
    
    // Add loading indicator
    const submitBtn = this.querySelector('button[type="submit"]');
    const originalText = submitBtn.textContent;
    submitBtn.innerHTML = 'Processing... <div class="loading"></div>';
    submitBtn.disabled = true;
    
    try {
        const response = await fetch('/api/book', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                departure,
                destination,
                priority
            })
        });
        
        if (!response.ok) {
            const errorData = await response.json();
            throw new Error(errorData.error || 'Failed to book trip');
        }
        
        const bookingData = await response.json();
        displayBookingResults(bookingData);
    } catch (error) {
        console.error('Error booking trip:', error);
        showError(error.message || 'Failed to book trip. Please try again.');
    } finally {
        // Restore button
        submitBtn.innerHTML = originalText;
        submitBtn.disabled = false;
    }
});

// Function to reset form and hide results
function resetForm() {
    document.getElementById('booking-form').reset();
    document.getElementById('result-container').style.display = 'none';
    document.querySelector('.priority-option[data-value="time"]').click();
}

// Function to display booking results
function displayBookingResults(data) {
    // Set booking reference
    document.getElementById('booking-reference').textContent = data.booking_ref;
    
    // Set journey summary
    document.getElementById('journey-summary').textContent = `${data.journey.departure} to ${data.journey.destination}`;
    
    // Set selected priority
    document.getElementById('selected-priority').textContent = data.journey.priority.charAt(0).toUpperCase() + data.journey.priority.slice(1);
    
    // Build route path visualization
    const routePath = document.getElementById('route-path');
    routePath.innerHTML = '';
    
    data.route.forEach((city, index) => {
        // Add city node
        const cityNode = document.createElement('div');
        cityNode.className = 'city-node';
        cityNode.textContent = city;
        routePath.appendChild(cityNode);
        
        // Add arrow if not the last city
        if (index < data.route.length - 1) {
            const arrow = document.createElement('div');
            arrow.className = 'route-arrow';
            arrow.innerHTML = '<i class="fas fa-arrow-right"></i>';
            routePath.appendChild(arrow);
        }
    });
    
    // Build segment details
    const segmentDetails = document.getElementById('segment-details');
    segmentDetails.innerHTML = '';
    
    data.segments.forEach((segment, index) => {
        const segmentDiv = document.createElement('div');
        segmentDiv.className = 'segment';
        
        segmentDiv.innerHTML = `
            <div class="segment-header">${index + 1}. ${segment.start} → ${segment.end}</div>
            <div class="segment-info">
                <div class="segment-info-item">
                    <strong>Class:</strong> ${segment.comfort}
                </div>
                <div class="segment-info-item">
                    <strong>Cost:</strong> $${segment.cost.toFixed(2)}
                </div>
                <div class="segment-info-item">
                    <strong>Duration:</strong> ${segment.duration} mins
                </div>
                <div class="segment-info-item">
                    <strong>Traffic:</strong> ${segment.traffic}
                </div>
            </div>
        `;
        
        segmentDetails.appendChild(segmentDiv);
    });
    
    // Set weather warning if present
    const weatherWarning = document.getElementById('weather-warning');
    if (data.weather_warning) {
        document.getElementById('weather-info').textContent = data.weather_warning;
        weatherWarning.style.display = 'block';
    } else {
        weatherWarning.style.display = 'none';
    }
    
    // Set summary values
    document.getElementById('total-cost').textContent = `$${data.total_cost.toFixed(2)}`;
    
    const hours = Math.floor(data.total_time / 60);
    const minutes = data.total_time % 60;
    document.getElementById('total-time').textContent = `${hours}h ${minutes}m`;
    
    document.getElementById('comfort-rating').textContent = data.comfort_rating.toFixed(1);
    
    // Generate star rating
    const starRating = document.getElementById('star-rating');
    starRating.innerHTML = '';
    
    for (let i = 1; i <= 4; i++) {
        const star = document.createElement('i');
        
        if (i <= Math.floor(data.comfort_rating)) {
            star.className = 'fas fa-star star filled';
        } else if (i - 0.5 <= data.comfort_rating) {
            star.className = 'fas fa-star-half-alt star filled';
        } else {
            star.className = 'far fa-star star';
        }
        
        starRating.appendChild(star);
    }
    
    // Show result container
    document.getElementById('result-container').style.display = 'block';
    
    // Scroll to results
    document.getElementById('result-container').scrollIntoView({ behavior: 'smooth' });
}

// Initialize the page
window.onload = function() {
    displayDate();
    fetchCities();
};