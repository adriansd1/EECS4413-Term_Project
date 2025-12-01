# Auction404 Database Population Script
$baseUrl = "http://localhost:8080"

Write-Host "=== Auction404 Database Population Script ===" -ForegroundColor Cyan
Write-Host ""

# Step 1: Create Admin User
Write-Host "Step 1: Creating admin user..." -ForegroundColor Yellow

$adminCredentials = @{
    username = "admin"
    password = "Admin123!"
    firstName = "Admin"
    lastName = "User"
    email = "admin@auction404.com"
    shippingAddress = "123 Admin St, Toronto, ON M5H 2N2"
}

try {
    $signupResponse = Invoke-RestMethod -Uri "$baseUrl/api/auth/signup" -Method Post -ContentType "application/json" -Body ($adminCredentials | ConvertTo-Json)
    Write-Host "Admin user created successfully!" -ForegroundColor Green
    $adminUser = $signupResponse
}
catch {
    Write-Host "Admin user may already exist, continuing..." -ForegroundColor Yellow
}

# Step 2: Login to get JWT token
Write-Host ""
Write-Host "Step 2: Logging in to get authentication token..." -ForegroundColor Yellow

$loginCredentials = @{
    username = "admin"
    password = "Admin123!"
}

try {
    $loginResponse = Invoke-RestMethod -Uri "$baseUrl/api/auth/signin" -Method Post -ContentType "application/json" -Body ($loginCredentials | ConvertTo-Json)
    $token = $loginResponse.token
    $userId = $loginResponse.userId
    Write-Host "Login successful! User ID: $userId" -ForegroundColor Green
}
catch {
    Write-Host "Login failed: $($_.Exception.Message)" -ForegroundColor Red
    exit 1
}

# Step 3: Create Auction Items
Write-Host ""
Write-Host "Step 3: Creating auction items..." -ForegroundColor Yellow
Write-Host ""

$items = @(
    @{
        title = "Vintage 1967 Shelby Mustang"
        description = "Classic muscle car, fully restored. Forward auction running for 24 hours."
        startingPrice = 45000.00
        type = "FORWARD"
        durationMinutes = 1440
        imageUrl = "https://placehold.co/400x300?text=Mustang"
    },
    @{
        title = "Sony 65-inch 4K OLED TV"
        description = "Dutch Auction! Price drops every minute. Grab it before it is gone."
        startingPrice = 1200.00
        minPrice = 800.00
        decreaseAmount = 50.00
        type = "DUTCH"
        durationMinutes = 1440
        imageUrl = "https://placehold.co/400x300?text=OLED+TV"
    },
    @{
        title = "Ending Soon: Front Row Concert Tickets"
        description = "Sold out everywhere else! Auction ends in 10 minutes."
        startingPrice = 50.00
        type = "FORWARD"
        durationMinutes = 10
        imageUrl = "https://placehold.co/400x300?text=Tickets"
    },
    @{
        title = "Apple MacBook Pro 16-inch M3 Max"
        description = "Brand new, sealed in box. Latest 2024 model with 32GB RAM."
        startingPrice = 2500.00
        type = "FORWARD"
        durationMinutes = 2880
        imageUrl = "https://placehold.co/400x300?text=MacBook+Pro"
    },
    @{
        title = "Vintage Rolex Submariner Watch"
        description = "1980s classic timepiece, authenticated and serviced."
        startingPrice = 8500.00
        type = "FORWARD"
        durationMinutes = 4320
        imageUrl = "https://placehold.co/400x300?text=Rolex"
    }
)

$successCount = 0
$failCount = 0

foreach ($item in $items) {
    try {
        $headers = @{
            "Authorization" = "Bearer $token"
            "Content-Type" = "application/json"
        }
        
        $response = Invoke-RestMethod -Uri "$baseUrl/api/auctions/create" -Method Post -Headers $headers -Body ($item | ConvertTo-Json)
        Write-Host "Created: $($item.title)" -ForegroundColor Green
        $successCount++
    }
    catch {
        Write-Host "Failed to create: $($item.title)" -ForegroundColor Red
        Write-Host "  Error: $($_.Exception.Message)" -ForegroundColor Red
        $failCount++
    }
}

# Summary
Write-Host ""
Write-Host "=== Summary ===" -ForegroundColor Cyan
Write-Host "Successfully created: $successCount auctions" -ForegroundColor Green
Write-Host "Failed to create: $failCount auctions" -ForegroundColor $(if ($failCount -gt 0) { "Red" } else { "Gray" })
Write-Host ""
Write-Host "You can now view the auctions at: http://localhost:3000" -ForegroundColor Cyan
