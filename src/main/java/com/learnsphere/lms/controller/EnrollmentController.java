package com.learnsphere.lms.controller;

import com.learnsphere.lms.model.Course;
import com.learnsphere.lms.model.Enrollment;
import com.learnsphere.lms.model.User;
import com.learnsphere.lms.service.CourseService;
import com.learnsphere.lms.service.EnrollmentService;
import com.learnsphere.lms.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/enrollments")
public class EnrollmentController {

    private final EnrollmentService enrollmentService;
    private final UserService userService;
    private final CourseService courseService;

    // Constructor injection
    public EnrollmentController(EnrollmentService enrollmentService,
            UserService userService,
            CourseService courseService) {
        this.enrollmentService = enrollmentService;
        this.userService = userService;
        this.courseService = courseService;
    }

    /**
     * Enroll a user into a course
     * 
     * @param request enrollment request containing userId and courseId
     * @return ResponseEntity with enrollment details
     */
    @PostMapping("/enroll")
    public ResponseEntity<?> enrollUser(@RequestBody EnrollmentRequest request) {
        // Validate user exists
        Optional<User> user = userService.getUserById(request.getUserId());
        if (user.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("User not found with id: " + request.getUserId());
        }

        // Validate course exists
        Optional<Course> course = courseService.getCourseById(request.getCourseId());
        if (course.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Course not found with id: " + request.getCourseId());
        }

        try {
            // Enroll user
            Enrollment enrollment = enrollmentService.enrollUser(user.get(), course.get());
            return new ResponseEntity<>(enrollment, HttpStatus.CREATED);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(e.getMessage());
        }
    }

    /**
     * Get all courses enrolled by a user
     * 
     * @param userId the user ID
     * @return ResponseEntity with list of courses
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getCoursesEnrolledByUser(@PathVariable Long userId) {
        // Validate user exists
        Optional<User> user = userService.getUserById(userId);
        if (user.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("User not found with id: " + userId);
        }

        List<Course> courses = enrollmentService.getCoursesEnrolledByUser(userId);
        return ResponseEntity.ok(courses);
    }

    /**
     * Inner class for enrollment request
     */
    public static class EnrollmentRequest {
        private Long userId;
        private Long courseId;

        public EnrollmentRequest() {
        }

        public EnrollmentRequest(Long userId, Long courseId) {
            this.userId = userId;
            this.courseId = courseId;
        }

        public Long getUserId() {
            return userId;
        }

        public void setUserId(Long userId) {
            this.userId = userId;
        }

        public Long getCourseId() {
            return courseId;
        }

        public void setCourseId(Long courseId) {
            this.courseId = courseId;
        }
    }
}
