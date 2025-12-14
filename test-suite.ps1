# LMS Application - Full Test Suite
$baseUrl = "http://localhost:8080"
$passed = 0
$failed = 0

Write-Host "`n============================================" -ForegroundColor Cyan
Write-Host "   LMS APPLICATION - COMPREHENSIVE TESTS" -ForegroundColor Cyan
Write-Host "============================================`n" -ForegroundColor Cyan

# TEST 1: Admin Login
Write-Host "TEST 1: Admin Login" -ForegroundColor Yellow
try {
    $body = '{"email":"admin@test.com","password":"admin123"}'
    $r = Invoke-RestMethod -Uri "$baseUrl/api/auth/login" -Method POST -Body $body -ContentType "application/json"
    $script:adminToken = $r.data.token
    Write-Host "  Email: $($r.data.email), Role: $($r.data.role)" -ForegroundColor White
    Write-Host "  Status: $($r.status), Message: $($r.message)" -ForegroundColor White
    Write-Host "  [PASS]" -ForegroundColor Green
    $passed++
} catch {
    Write-Host "  [FAIL] $_" -ForegroundColor Red
    $failed++
}

# TEST 2: Admin Add Course
Write-Host "`nTEST 2: Admin Add Course" -ForegroundColor Yellow
try {
    $body = '{"title":"API Testing","description":"RESTful API testing","instructorName":"John Doe"}'
    $h = @{Authorization="Bearer $script:adminToken"}
    $r = Invoke-RestMethod -Uri "$baseUrl/api/courses/add" -Method POST -Body $body -ContentType "application/json" -Headers $h
    Write-Host "  Course ID: $($r.data.id), Title: $($r.data.title)" -ForegroundColor White
    Write-Host "  Status: $($r.status), Message: $($r.message)" -ForegroundColor White
    Write-Host "  [PASS]" -ForegroundColor Green
    $script:courseId = $r.data.id
    $passed++
} catch {
    Write-Host "  [FAIL] $_" -ForegroundColor Red
    $failed++
}

# TEST 3: Admin Update Course
Write-Host "`nTEST 3: Admin Update Course" -ForegroundColor Yellow
try {
    $body = '{"title":"API Testing - Advanced","description":"Advanced API testing","instructorName":"Jane Smith"}'
    $h = @{Authorization="Bearer $script:adminToken"}
    $r = Invoke-RestMethod -Uri "$baseUrl/api/courses/$script:courseId" -Method PUT -Body $body -ContentType "application/json" -Headers $h
    Write-Host "  Updated: $($r.data.title), Instructor: $($r.data.instructorName)" -ForegroundColor White
    Write-Host "  Status: $($r.status), Message: $($r.message)" -ForegroundColor White
    Write-Host "  [PASS]" -ForegroundColor Green
    $passed++
} catch {
    Write-Host "  [FAIL] $_" -ForegroundColor Red
    $failed++
}

# TEST 4: Admin View All Courses
Write-Host "`nTEST 4: Admin View All Courses" -ForegroundColor Yellow
try {
    $h = @{Authorization="Bearer $script:adminToken"}
    $r = Invoke-RestMethod -Uri "$baseUrl/api/courses/all" -Method GET -Headers $h
    Write-Host "  Total Courses: $($r.data.Count)" -ForegroundColor White
    Write-Host "  Status: $($r.status), Message: $($r.message)" -ForegroundColor White
    Write-Host "  [PASS]" -ForegroundColor Green
    $passed++
} catch {
    Write-Host "  [FAIL] $_" -ForegroundColor Red
    $failed++
}

# TEST 5: Admin Delete Course
Write-Host "`nTEST 5: Admin Delete Course" -ForegroundColor Yellow
try {
    $h = @{Authorization="Bearer $script:adminToken"}
    $r = Invoke-RestMethod -Uri "$baseUrl/api/courses/$script:courseId" -Method DELETE -Headers $h
    Write-Host "  Status: $($r.status), Message: $($r.message)" -ForegroundColor White
    Write-Host "  [PASS]" -ForegroundColor Green
    $passed++
} catch {
    Write-Host "  [FAIL] $_" -ForegroundColor Red
    $failed++
}

# TEST 6: Student Login
Write-Host "`nTEST 6: Student Login" -ForegroundColor Yellow
try {
    $body = '{"email":"student@test.com","password":"student123"}'
    $r = Invoke-RestMethod -Uri "$baseUrl/api/auth/login" -Method POST -Body $body -ContentType "application/json"
    $script:studentToken = $r.data.token
    Write-Host "  Email: $($r.data.email), Role: $($r.data.role)" -ForegroundColor White
    Write-Host "  Status: $($r.status), Message: $($r.message)" -ForegroundColor White
    Write-Host "  [PASS]" -ForegroundColor Green
    $passed++
} catch {
    Write-Host "  [FAIL] $_" -ForegroundColor Red
    $failed++
}

# TEST 7: Student View Courses
Write-Host "`nTEST 7: Student View Courses" -ForegroundColor Yellow
try {
    $h = @{Authorization="Bearer $script:studentToken"}
    $r = Invoke-RestMethod -Uri "$baseUrl/api/courses/all" -Method GET -Headers $h
    Write-Host "  Available Courses: $($r.data.Count)" -ForegroundColor White
    Write-Host "  Status: $($r.status), Message: $($r.message)" -ForegroundColor White
    Write-Host "  [PASS]" -ForegroundColor Green
    $script:enrollCourseId = $r.data[0].id
    $passed++
} catch {
    Write-Host "  [FAIL] $_" -ForegroundColor Red
    $failed++
}

# TEST 8: Student Enroll in Course
Write-Host "`nTEST 8: Student Enroll in Course" -ForegroundColor Yellow
try {
    # Get student ID
    $h = @{Authorization="Bearer $script:adminToken"}
    $users = Invoke-RestMethod -Uri "$baseUrl/api/users/all" -Method GET -Headers $h
    $student = $users.data | Where-Object { $_.email -eq "student@test.com" }
    
    $body = "{`"userId`":$($student.id),`"courseId`":$script:enrollCourseId}"
    $h = @{Authorization="Bearer $script:studentToken"}
    $r = Invoke-RestMethod -Uri "$baseUrl/api/enrollments/enroll" -Method POST -Body $body -ContentType "application/json" -Headers $h
    Write-Host "  Enrollment ID: $($r.data.id)" -ForegroundColor White
    Write-Host "  Status: $($r.status), Message: $($r.message)" -ForegroundColor White
    Write-Host "  [PASS]" -ForegroundColor Green
    $passed++
} catch {
    if ($_ -like "*409*" -or $_ -like "*Conflict*") {
        Write-Host "  Already enrolled (expected behavior)" -ForegroundColor Yellow
        Write-Host "  [PASS]" -ForegroundColor Green
        $passed++
    } else {
        Write-Host "  [FAIL] $_" -ForegroundColor Red
        $failed++
    }
}

# TEST 9: Student Cannot Add Course (Security)
Write-Host "`nTEST 9: Student Cannot Add Course (Security)" -ForegroundColor Yellow
try {
    $body = '{"title":"Hacked Course","description":"Should fail","instructorName":"Hacker"}'
    $h = @{Authorization="Bearer $script:studentToken"}
    $r = Invoke-RestMethod -Uri "$baseUrl/api/courses/add" -Method POST -Body $body -ContentType "application/json" -Headers $h
    Write-Host "  [FAIL] Security issue - student added course!" -ForegroundColor Red
    $failed++
} catch {
    if ($_ -like "*403*" -or $_ -like "*Forbidden*") {
        Write-Host "  Access denied (403) - Expected" -ForegroundColor White
        Write-Host "  [PASS]" -ForegroundColor Green
        $passed++
    } else {
        Write-Host "  [FAIL] Wrong error: $_" -ForegroundColor Red
        $failed++
    }
}

# TEST 10: Student Cannot Update Course (Security)
Write-Host "`nTEST 10: Student Cannot Update Course (Security)" -ForegroundColor Yellow
try {
    $body = '{"title":"Hacked","description":"Should fail","instructorName":"Hacker"}'
    $h = @{Authorization="Bearer $script:studentToken"}
    $r = Invoke-RestMethod -Uri "$baseUrl/api/courses/1" -Method PUT -Body $body -ContentType "application/json" -Headers $h
    Write-Host "  [FAIL] Security issue - student updated course!" -ForegroundColor Red
    $failed++
} catch {
    if ($_ -like "*403*" -or $_ -like "*Forbidden*") {
        Write-Host "  Access denied (403) - Expected" -ForegroundColor White
        Write-Host "  [PASS]" -ForegroundColor Green
        $passed++
    } else {
        Write-Host "  [FAIL] Wrong error: $_" -ForegroundColor Red
        $failed++
    }
}

# TEST 11: Student Cannot Delete Course (Security)
Write-Host "`nTEST 11: Student Cannot Delete Course (Security)" -ForegroundColor Yellow
try {
    $h = @{Authorization="Bearer $script:studentToken"}
    $r = Invoke-RestMethod -Uri "$baseUrl/api/courses/1" -Method DELETE -Headers $h
    Write-Host "  [FAIL] Security issue - student deleted course!" -ForegroundColor Red
    $failed++
} catch {
    if ($_ -like "*403*" -or $_ -like "*Forbidden*") {
        Write-Host "  Access denied (403) - Expected" -ForegroundColor White
        Write-Host "  [PASS]" -ForegroundColor Green
        $passed++
    } else {
        Write-Host "  [FAIL] Wrong error: $_" -ForegroundColor Red
        $failed++
    }
}

# TEST 12: No Token Access Denied (Security)
Write-Host "`nTEST 12: No Token Access Denied (Security)" -ForegroundColor Yellow
try {
    $r = Invoke-RestMethod -Uri "$baseUrl/api/courses/all" -Method GET
    Write-Host "  [FAIL] Security issue - accessed without token!" -ForegroundColor Red
    $failed++
} catch {
    if ($_ -like "*401*" -or $_ -like "*403*" -or $_ -like "*Unauthorized*") {
        Write-Host "  Access denied - Expected" -ForegroundColor White
        Write-Host "  [PASS]" -ForegroundColor Green
        $passed++
    } else {
        Write-Host "  [FAIL] Wrong error: $_" -ForegroundColor Red
        $failed++
    }
}

# Summary
Write-Host "`n============================================" -ForegroundColor Cyan
Write-Host "              TEST SUMMARY" -ForegroundColor Cyan
Write-Host "============================================" -ForegroundColor Cyan
Write-Host "Total: $($passed + $failed)" -ForegroundColor White
Write-Host "Passed: $passed" -ForegroundColor Green
Write-Host "Failed: $failed" -ForegroundColor $(if($failed -eq 0){"Green"}else{"Red"})

if ($failed -eq 0) {
    Write-Host "`n*** ALL TESTS PASSED ***" -ForegroundColor Green
} else {
    Write-Host "`n*** SOME TESTS FAILED ***" -ForegroundColor Red
}
Write-Host ""
