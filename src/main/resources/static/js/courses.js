// API Base URL
const API_BASE_URL = 'http://localhost:8080/api';

// Global variables
let currentUserId = null;

/**
 * Get JWT token from localStorage
 */
function getToken() {
    return localStorage.getItem('token');
}

/**
 * Get user role from localStorage
 */
function getUserRole() {
    return localStorage.getItem('role');
}

/**
 * Get user email from localStorage
 */
function getUserEmail() {
    return localStorage.getItem('email');
}

/**
 * Handle API errors (401, 403) by redirecting to login
 */
function handleAuthError(status) {
    if (status === 401 || status === 403) {
        localStorage.removeItem('token');
        localStorage.removeItem('email');
        localStorage.removeItem('role');
        window.location.href = 'index.html';
        return true;
    }
    return false;
}

/**
 * Fetch all courses from API using JWT
 */
async function fetchCourses() {
    const token = getToken();
    
    try {
        const response = await fetch(`${API_BASE_URL}/courses/all`, {
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${token}`
            }
        });
        
        if (response.ok) {
            return await response.json();
        } else if (handleAuthError(response.status)) {
            return null;
        } else {
            throw new Error('Failed to load courses');
        }
    } catch (error) {
        console.error('Error fetching courses:', error);
        throw error;
    }
}

/**
 * Get user ID by email
 */
async function getUserId() {
    if (currentUserId) {
        return currentUserId;
    }
    
    const token = getToken();
    const email = getUserEmail();
    
    try {
        const response = await fetch(`${API_BASE_URL}/users/all`, {
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${token}`
            }
        });
        
        if (response.ok) {
            const users = await response.json();
            const user = users.find(u => u.email === email);
            if (user) {
                currentUserId = user.id;
                return currentUserId;
            }
        } else if (handleAuthError(response.status)) {
            return null;
        }
    } catch (error) {
        console.error('Error getting user ID:', error);
    }
    
    return null;
}

/**
 * Enroll user in a course
 */
async function enrollInCourse(courseId) {
    const token = getToken();
    const userId = await getUserId();
    
    if (!userId) {
        return { success: false, error: 'User not found' };
    }
    
    try {
        const response = await fetch(`${API_BASE_URL}/enrollments/enroll`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`
            },
            body: JSON.stringify({ userId, courseId })
        });
        
        if (response.ok) {
            return { success: true };
        } else if (response.status === 409) {
            return { success: false, error: 'Already enrolled in this course' };
        } else if (handleAuthError(response.status)) {
            return { success: false, error: 'Authentication failed' };
        } else if (response.status === 403) {
            return { success: false, error: 'Permission denied' };
        } else {
            return { success: false, error: 'Failed to enroll' };
        }
    } catch (error) {
        console.error('Error enrolling in course:', error);
        return { success: false, error: 'Failed to connect to server' };
    }
}

// Display courses in the grid
function displayCourses(courses) {
    const coursesList = document.getElementById('courses-list');
    
    if (courses.length === 0) {
        coursesList.innerHTML = '<p class="loading">No courses available</p>';
        return;
    }
    
    coursesList.innerHTML = courses.map(course => `
        <div class="course-card">
            <h3>${course.title}</h3>
            <p>${course.description}</p>
            <p class="instructor">Instructor: ${course.instructorName}</p>
            ${currentUser && currentUser.role === 'STUDENT' ? 
                `<button onclick="enrollInCourse(${course.id})" class="btn btn-success">Enroll</button>` 
                : ''}
        </div>
    `).join('');
}

// Show add course form
function showAddCourseForm() {
    document.getElementById('add-course-form').style.display = 'block';
    document.getElementById('add-course-btn').style.display = 'none';
    
    // Add form submit handler
    document.getElementById('course-form').addEventListener('submit', addCourse);
}

// Cancel add course
function cancelAddCourse() {
    document.getElementById('add-course-form').style.display = 'none';
    document.getElementById('add-course-btn').style.display = 'block';
    document.getElementById('course-form').reset();
}

// Add new course (ADMIN only)
async function addCourse(e) {
    e.preventDefault();
    
    const token = localStorage.getItem('token');
    const title = document.getElementById('title').value;
    const description = document.getElementById('description').value;
    const instructorName = document.getElementById('instructorName').value;
    
    try {
        const response = await fetch(`${API_BASE_URL}/courses/add`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`
            },
            body: JSON.stringify({ title, description, instructorName })
        });
        
        if (response.ok) {
            const newCourse = await response.json();
            showSuccess('Course added successfully!');
            
            // Reset form and hide it
            document.getElementById('course-form').reset();
            cancelAddCourse();
            
            // Reload courses
            loadCourses();
        } else if (response.status === 403) {
            showError('You do not have permission to add courses');
        } else {
            showError('Failed to add course');
        }
    } catch (error) {
        console.error('Error adding course:', error);
        showError('Failed to connect to server');
    }
}

// Enroll in course (STUDENT only)
async function enrollInCourse(courseId) {
    const token = localStorage.getItem('token');
    const email = localStorage.getItem('email');
    
    try {
        // First, get user ID by email
        const userResponse = await fetch(`${API_BASE_URL}/users/all`, {
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${token}`
            }
        });
        
        if (!userResponse.ok) {
            showError('Failed to get user information');
            return;
        }
        
        const users = await userResponse.json();
        const currentUserData = users.find(u => u.email === email);
        
        if (!currentUserData) {
            showError('User not found');
            return;
        }
        
        // Enroll in course
        const enrollResponse = await fetch(`${API_BASE_URL}/enrollments/enroll`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`
            },
            body: JSON.stringify({
                userId: currentUserData.id,
                courseId: courseId
            })
        });
        
        if (enrollResponse.ok) {
            showSuccess('Successfully enrolled in course!');
            loadEnrollments(); // Refresh enrollments
        } else if (enrollResponse.status === 409) {
            showError('You are already enrolled in this course');
        } else if (enrollResponse.status === 403) {
            showError('You do not have permission to enroll');
        } else {
            showError('Failed to enroll in course');
        }
    } catch (error) {
        console.error('Error enrolling in course:', error);
        showError('Failed to connect to server');
    }
}

// Load student enrollments
async function loadEnrollments() {
    const token = localStorage.getItem('token');
    const email = localStorage.getItem('email');
    
    try {
        // Get user ID
        const userResponse = await fetch(`${API_BASE_URL}/users/all`, {
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${token}`
            }
        });
        
        if (!userResponse.ok) return;
        
        const users = await userResponse.json();
        const currentUserData = users.find(u => u.email === email);
        
        if (!currentUserData) return;
        
        // Get enrolled courses
        const enrollResponse = await fetch(`${API_BASE_URL}/enrollments/user/${currentUserData.id}`, {
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${token}`
            }
        });
        
        if (enrollResponse.ok) {
            const enrolledCourses = await enrollResponse.json();
            displayEnrollments(enrolledCourses);
        }
    } catch (error) {
        console.error('Error loading enrollments:', error);
    }
}

// Display enrolled courses
function displayEnrollments(courses) {
    const enrollmentsList = document.getElementById('enrollments-list');
    
    if (courses.length === 0) {
        enrollmentsList.innerHTML = '<p class="loading">You are not enrolled in any courses yet</p>';
        return;
    }
    
    enrollmentsList.innerHTML = courses.map(course => `
        <div class="course-card">
            <h3>${course.title}</h3>
            <p>${course.description}</p>
            <p class="instructor">Instructor: ${course.instructorName}</p>
        </div>
    `).join('');
}

// Logout function
function logout() {
    localStorage.removeItem('token');
    localStorage.removeItem('email');
    localStorage.removeItem('role');
    window.location.href = 'index.html';
}

// Utility Functions
function showError(message) {
    const errorDiv = document.getElementById('error-message');
    if (errorDiv) {
        errorDiv.textContent = message;
        errorDiv.style.display = 'block';
        
        const successDiv = document.getElementById('success-message');
        if (successDiv) {
            successDiv.style.display = 'none';
        }
        
        setTimeout(() => {
            errorDiv.style.display = 'none';
        }, 5000);
    }
}

function showSuccess(message) {
    const successDiv = document.getElementById('success-message');
    if (successDiv) {
        successDiv.textContent = message;
        successDiv.style.display = 'block';
        
        const errorDiv = document.getElementById('error-message');
        if (errorDiv) {
            errorDiv.style.display = 'none';
        }
        
        setTimeout(() => {
            successDiv.style.display = 'none';
        }, 3000);
    }
}
