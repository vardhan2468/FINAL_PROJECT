# LMS Application - Comprehensive Test Suite
Write-Host "`n=============================================================" -ForegroundColor Cyan
Write-Host "  FULL APPLICATION TEST SUITE - LMS PLATFORM" -ForegroundColor Cyan
Write-Host "=============================================================`n" -ForegroundColor Cyan

$baseUrl = "http://localhost:8080"
$passedTests = 0
$failedTests = 0

# ========== ADMIN TESTS ==========
Write-Host "`n====== ADMINISTRATOR TESTS ======`n" -ForegroundColor Magenta

# Test 1: Admin Login
Write-Host "--- TEST 1: ADMIN LOGIN ---" -ForegroundColor Yellow
try {
    $adminCreds = @{email="admin@test.com";password="admin123"} | ConvertTo-Json
    $adminAuth = Invoke-RestMethod -Uri "$baseUrl/api/auth/login" -Method POST -Body $adminCreds -ContentType "application/json"
    $adminToken = $adminAuth.token
    Write-Host "  Email: $($adminAuth.email)" -ForegroundColor White
    Write-Host "  Role: $($adminAuth.role)" -ForegroundColor White
    Write-Host "  Message: $($adminAuth.message)" -ForegroundColor White
    Write-Host "[PASS] ADMIN LOGIN" -ForegroundColor Green
    $passedTests++
} catch {
    Write-Host "[FAIL] ADMIN LOGIN: $_" -ForegroundColor Red
    $failedTests++
}

# Test 2: Admin Add Course
Write-Host "`n--- TEST 2: ADMIN - ADD COURSE ---" -ForegroundColor Yellow
try {
    $courseData = @{name="Integration Testing";description="Testing integrated systems";duration=5} | ConvertTo-Json
    $addHeaders = @{Authorization="Bearer $adminToken"}
    $addResult = Invoke-RestMethod -Uri "$baseUrl/api/courses/add" -Method POST -Body $courseData -ContentType "application/json" -Headers $addHeaders
    $newCourseId = $addResult.data.id
    Write-Host "  Course ID: $newCourseId" -ForegroundColor White
    Write-Host "  Name: $($addResult.data.name)" -ForegroundColor White
    Write-Host "  Status: $($addResult.status)" -ForegroundColor White
    Write-Host "  Message: $($addResult.message)" -ForegroundColor White
    Write-Host "[PASS] ADD COURSE" -ForegroundColor Green
    $passedTests++
} catch {
    Write-Host "[FAIL] ADD COURSE: $_" -ForegroundColor Red
    $failedTests++
}

# Test 3: Admin Update Course
Write-Host "`n━━━ TEST 3: ADMIN - UPDATE COURSE ━━━" -ForegroundColor Yellow
try {
    $updateData = @{name="Integration Testing - Advanced";description="Advanced integration testing";duration=8} | ConvertTo-Json
    $updateHeaders = @{Authorization="Bearer $adminToken"}
    $updateResult = Invoke-RestMethod -Uri "$baseUrl/api/courses/$newCourseId" -Method PUT -Body $updateData -ContentType "application/json" -Headers $updateHeaders
    Write-Host "  Updated Name: $($updateResult.data.name)" -ForegroundColor White
    Write-Host "  Duration: $($updateResult.data.duration) weeks" -ForegroundColor White
    Write-Host "  Status: $($updateResult.status)" -ForegroundColor White
    Write-Host "  Message: $($updateResult.message)" -ForegroundColor White
    Write-Host "✓ PASSED" -ForegroundColor Green
    $passedTests++
} catch {
    Write-Host "✗ FAILED: $_" -ForegroundColor Red
    $failedTests++
}

# Test 4: Admin View All Courses
Write-Host "`n━━━ TEST 4: ADMIN - VIEW ALL COURSES ━━━" -ForegroundColor Yellow
try {
    $viewHeaders = @{Authorization="Bearer $adminToken"}
    $allCourses = Invoke-RestMethod -Uri "$baseUrl/api/courses/all" -Method GET -Headers $viewHeaders
    Write-Host "  Total Courses: $($allCourses.data.Count)" -ForegroundColor White
    Write-Host "  Status: $($allCourses.status)" -ForegroundColor White
    Write-Host "  Message: $($allCourses.message)" -ForegroundColor White
    Write-Host "✓ PASSED" -ForegroundColor Green
    $passedTests++
} catch {
    Write-Host "✗ FAILED: $_" -ForegroundColor Red
    $failedTests++
}

# Test 5: Admin Delete Course
Write-Host "`n━━━ TEST 5: ADMIN - DELETE COURSE ━━━" -ForegroundColor Yellow
try {
    $deleteHeaders = @{Authorization="Bearer $adminToken"}
    $deleteResult = Invoke-RestMethod -Uri "$baseUrl/api/courses/$newCourseId" -Method DELETE -Headers $deleteHeaders
    Write-Host "  Status: $($deleteResult.status)" -ForegroundColor White
    Write-Host "  Message: $($deleteResult.message)" -ForegroundColor White
    Write-Host "✓ PASSED" -ForegroundColor Green
    $passedTests++
} catch {
    Write-Host "✗ FAILED: $_" -ForegroundColor Red
    $failedTests++
}

# ========== STUDENT TESTS ==========
Write-Host "`n▓▓▓▓▓▓ STUDENT TESTS ▓▓▓▓▓▓`n" -ForegroundColor Magenta

# Test 6: Student Login
Write-Host "━━━ TEST 6: STUDENT LOGIN ━━━" -ForegroundColor Yellow
try {
    $studentCreds = @{email="bob.smith@example.com";password="password123"} | ConvertTo-Json
    $studentAuth = Invoke-RestMethod -Uri "$baseUrl/api/auth/login" -Method POST -Body $studentCreds -ContentType "application/json"
    $studentToken = $studentAuth.token
    Write-Host "  Email: $($studentAuth.email)" -ForegroundColor White
    Write-Host "  Role: $($studentAuth.role)" -ForegroundColor White
    Write-Host "  Message: $($studentAuth.message)" -ForegroundColor White
    Write-Host "✓ PASSED" -ForegroundColor Green
    $passedTests++
} catch {
    Write-Host "✗ FAILED: $_" -ForegroundColor Red
    $failedTests++
}

# Test 7: Student View Courses
Write-Host "`n━━━ TEST 7: STUDENT - VIEW COURSES ━━━" -ForegroundColor Yellow
try {
    $studentHeaders = @{Authorization="Bearer $studentToken"}
    $studentCourses = Invoke-RestMethod -Uri "$baseUrl/api/courses/all" -Method GET -Headers $studentHeaders
    Write-Host "  Available Courses: $($studentCourses.data.Count)" -ForegroundColor White
    Write-Host "  Status: $($studentCourses.status)" -ForegroundColor White
    Write-Host "  Message: $($studentCourses.message)" -ForegroundColor White
    if ($studentCourses.data.Count -gt 0) {
        $enrollCourseId = $studentCourses.data[0].id
        Write-Host "  Sample Course: $($studentCourses.data[0].name)" -ForegroundColor White
    }
    Write-Host "✓ PASSED" -ForegroundColor Green
    $passedTests++
} catch {
    Write-Host "✗ FAILED: $_" -ForegroundColor Red
    $failedTests++
}

# Test 8: Student Enroll in Course
Write-Host "`n━━━ TEST 8: STUDENT - ENROLL IN COURSE ━━━" -ForegroundColor Yellow
try {
    # Get student user ID
    $studentUsers = Invoke-RestMethod -Uri "$baseUrl/api/users/all" -Method GET -Headers @{Authorization="Bearer $adminToken"}
    $studentUser = $studentUsers.data | Where-Object { $_.email -eq "bob.smith@example.com" }
    $studentUserId = $studentUser.id
    
    # Get a course to enroll in
    $courses = Invoke-RestMethod -Uri "$baseUrl/api/courses/all" -Method GET -Headers $studentHeaders
    $enrollCourseId = $courses.data[0].id
    
    $enrollData = @{userId=$studentUserId;courseId=$enrollCourseId} | ConvertTo-Json
    $enrollHeaders = @{Authorization="Bearer $studentToken"}
    $enrollResult = Invoke-RestMethod -Uri "$baseUrl/api/enrollments/enroll" -Method POST -Body $enrollData -ContentType "application/json" -Headers $enrollHeaders
    Write-Host "  Enrollment ID: $($enrollResult.data.id)" -ForegroundColor White
    Write-Host "  Status: $($enrollResult.status)" -ForegroundColor White
    Write-Host "  Message: $($enrollResult.message)" -ForegroundColor White
    Write-Host "✓ PASSED" -ForegroundColor Green
    $passedTests++
} catch {
    if ($_.Exception.Message -like "*409*" -or $_.Exception.Message -like "*Conflict*") {
        Write-Host "  Already enrolled (expected behavior)" -ForegroundColor Yellow
        Write-Host "✓ PASSED" -ForegroundColor Green
        $passedTests++
    } else {
        Write-Host "✗ FAILED: $_" -ForegroundColor Red
        $failedTests++
    }
}

# ========== UNAUTHORIZED ACCESS TESTS ==========
Write-Host "`n▓▓▓▓▓▓ UNAUTHORIZED ACCESS TESTS ▓▓▓▓▓▓`n" -ForegroundColor Magenta

# Test 9: Student Cannot Add Course
Write-Host "━━━ TEST 9: STUDENT CANNOT ADD COURSE ━━━" -ForegroundColor Yellow
try {
    $courseData = @{name="Unauthorized Course";description="Should fail";duration=1} | ConvertTo-Json
    $headers = @{Authorization="Bearer $studentToken"}
    $result = Invoke-RestMethod -Uri "$baseUrl/api/courses/add" -Method POST -Body $courseData -ContentType "application/json" -Headers $headers
    Write-Host "✗ FAILED: Student was able to add course (security issue!)" -ForegroundColor Red
    $failedTests++
} catch {
    if ($_.Exception.Message -like "*403*" -or $_.Exception.Message -like "*Forbidden*") {
        Write-Host "  Access Denied (403 Forbidden) - Expected" -ForegroundColor White
        Write-Host "✓ PASSED" -ForegroundColor Green
        $passedTests++
    } else {
        Write-Host "✗ FAILED: Wrong error type: $_" -ForegroundColor Red
        $failedTests++
    }
}

# Test 10: Student Cannot Update Course
Write-Host "`n━━━ TEST 10: STUDENT CANNOT UPDATE COURSE ━━━" -ForegroundColor Yellow
try {
    $updateData = @{name="Hacked Course";description="Should fail";duration=99} | ConvertTo-Json
    $headers = @{Authorization="Bearer $studentToken"}
    $result = Invoke-RestMethod -Uri "$baseUrl/api/courses/1" -Method PUT -Body $updateData -ContentType "application/json" -Headers $headers
    Write-Host "✗ FAILED: Student was able to update course (security issue!)" -ForegroundColor Red
    $failedTests++
} catch {
    if ($_.Exception.Message -like "*403*" -or $_.Exception.Message -like "*Forbidden*") {
        Write-Host "  Access Denied (403 Forbidden) - Expected" -ForegroundColor White
        Write-Host "✓ PASSED" -ForegroundColor Green
        $passedTests++
    } else {
        Write-Host "✗ FAILED: Wrong error type: $_" -ForegroundColor Red
        $failedTests++
    }
}

# Test 11: Student Cannot Delete Course
Write-Host "`n━━━ TEST 11: STUDENT CANNOT DELETE COURSE ━━━" -ForegroundColor Yellow
try {
    $headers = @{Authorization="Bearer $studentToken"}
    $result = Invoke-RestMethod -Uri "$baseUrl/api/courses/1" -Method DELETE -Headers $headers
    Write-Host "✗ FAILED: Student was able to delete course (security issue!)" -ForegroundColor Red
    $failedTests++
} catch {
    if ($_.Exception.Message -like "*403*" -or $_.Exception.Message -like "*Forbidden*") {
        Write-Host "  Access Denied (403 Forbidden) - Expected" -ForegroundColor White
        Write-Host "✓ PASSED" -ForegroundColor Green
        $passedTests++
    } else {
        Write-Host "✗ FAILED: Wrong error type: $_" -ForegroundColor Red
        $failedTests++
    }
}

# Test 12: No Token Access Denied
Write-Host "`n━━━ TEST 12: NO TOKEN - ACCESS DENIED ━━━" -ForegroundColor Yellow
try {
    $result = Invoke-RestMethod -Uri "$baseUrl/api/courses/all" -Method GET
    Write-Host "✗ FAILED: Accessed without token (security issue!)" -ForegroundColor Red
    $failedTests++
} catch {
    if ($_.Exception.Message -like "*401*" -or $_.Exception.Message -like "*Unauthorized*" -or $_.Exception.Message -like "*403*") {
        Write-Host "  Access Denied - Expected" -ForegroundColor White
        Write-Host "✓ PASSED" -ForegroundColor Green
        $passedTests++
    } else {
        Write-Host "✗ FAILED: Wrong error type: $_" -ForegroundColor Red
        $failedTests++
    }
}

# ========== TEST SUMMARY ==========
Write-Host "`n╔══════════════════════════════════════════════════════════════╗" -ForegroundColor Cyan
Write-Host "║                     TEST SUMMARY                             ║" -ForegroundColor Cyan
Write-Host "╚══════════════════════════════════════════════════════════════╝" -ForegroundColor Cyan
Write-Host "`n  Total Tests: $($passedTests + $failedTests)" -ForegroundColor White
Write-Host "  Passed: $passedTests" -ForegroundColor Green
Write-Host "  Failed: $failedTests" -ForegroundColor $(if($failedTests -eq 0){"Green"}else{"Red"})

if ($failedTests -eq 0) {
    Write-Host "`n  ✓✓✓ ALL TESTS PASSED ✓✓✓" -ForegroundColor Green
} else {
    Write-Host "`n  ✗✗✗ SOME TESTS FAILED ✗✗✗" -ForegroundColor Red
}
Write-Host ""
