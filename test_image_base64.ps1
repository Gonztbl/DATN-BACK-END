# Step 0: Login to get JWT token
Write-Host "=== Logging in to get JWT token ==="
$loginBody = @{
    userName = "admin"
    password = "admin123"
} | ConvertTo-Json

try {
    $loginResponse = Invoke-RestMethod -Uri 'http://localhost:8080/api/auth/login' -Method POST -Body $loginBody -ContentType 'application/json'
    $token = $loginResponse.token
    Write-Host "Login SUCCESS! Token received (first 50 chars): $($token.Substring(0, [Math]::Min(50, $token.Length)))..."
}
catch {
    Write-Host "Login FAILED: $_"
    Write-Host "Trying without auth..."
    $token = $null
}

$headers = @{}
if ($token) {
    $headers["Authorization"] = "Bearer $token"
}

# Generate a 50,000+ character base64 string
$prefix = "data:image/png;base64,"
$base64Body = "A" * 50000
$longString = $prefix + $base64Body
$totalLength = $longString.Length
Write-Host "`n=== TEST: image_base64 with $totalLength characters ==="

# Build JSON body
$body = @{
    name          = "TestLargeImage_$(Get-Date -Format 'yyyyMMddHHmmss')"
    description   = "Test product for large image"
    price         = 10.00
    image_base64  = $longString
    category_id   = 1
    restaurant_id = "1"
} | ConvertTo-Json -Depth 10

# Step 1: POST
Write-Host "`n--- STEP 1: POST /api/admin/products ---"
try {
    $postResponse = Invoke-RestMethod -Uri 'http://localhost:8080/api/admin/products' -Method POST -Body $body -ContentType 'application/json; charset=utf-8' -Headers $headers
    $productId = $postResponse.id
    Write-Host "POST SUCCESS! Product ID = $productId"
    Write-Host "image_base64 length in POST response = $($postResponse.imageBase64.Length)"
}
catch {
    Write-Host "POST FAILED: $_"
    exit 1
}

# Step 3: PUT (Update restaurant)
Write-Host "`n--- STEP 3: PUT /api/admin/products/$productId ---"
$newRestaurantId = "RS-1234" # Assuming this exists
$updateBody = @{
    restaurantId = $newRestaurantId
} | ConvertTo-Json

try {
    $putResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/admin/products/$productId" -Method PUT -Body $updateBody -ContentType 'application/json' -Headers $headers
    Write-Host "PUT SUCCESS!"
    
    if ($putResponse.id -eq $productId) {
        Write-Host " PASS: Product ID remains $productId (No new record created)"
    }
    else {
        Write-Host " FAIL: Product ID changed to $($putResponse.id) (New record created!)"
    }
}
catch {
    Write-Host "PUT FAILED: $_"
}

# Step 4: Final GET to verify restaurant change
Write-Host "`n--- STEP 4: Final GET /api/admin/products/$productId ---"
try {
    $finalGetResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/admin/products/$productId" -Method GET -ContentType 'application/json' -Headers $headers
    Write-Host "Final restaurant ID = $($finalGetResponse.restaurant.id)"
    if ($finalGetResponse.restaurant.id -eq $newRestaurantId) {
        Write-Host " PASS: Restaurant updated successfully"
    }
    else {
        Write-Host " FAIL: Restaurant NOT updated"
    }
}
catch {
    Write-Host "Final GET FAILED: $_"
}

Write-Host "`n=== ALL TESTS COMPLETE ==="
