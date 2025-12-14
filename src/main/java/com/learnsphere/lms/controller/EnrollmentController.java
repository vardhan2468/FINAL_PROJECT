package com.learnsphere.lms.controller;

import com.learnsphere.lms.dto.ApiResponse;
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
     * @return ResponseEntity with standardized API response
     */
    @PostMapping("/enroll")
    public ResponseEntity<ApiResponse<Enrollment>> enrollUser(@RequestBody EnrollmentRequest request) {
        // Get user and course (will throw ResourceNotFoundException if not found)
        User user = userService.getUserByIdOrThrow(request.getUserId());
        Course course = courseService.getCourseById(request.getCourseId());

        // Enroll user (will throw DuplicateEnrollmentException if already enrolled)
        Enrollment enrollment = enrollmentService.enrollUser(user, course);
        return new ResponseEntity<>(
                ApiResponse.success("Enrollment successful", enrollment),
                HttpStatus.CREATED);
    }

    /**
     * Get all courses enrolled by a user
     * 
     * @param userId the user ID
     * @return ResponseEntity with standardized API response
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<Course>>> getCoursesEnrolledByUser(@PathVariable Long userId) {
        // Validate user exists (will throw ResourceNotFoundException if not found)
        userService.getUserByIdOrThrow(userId);

        List<Course> courses = enrollmentService.getCoursesEnrolledByUser(userId);
        return ResponseEntity.ok(
                ApiResponse.success("Enrolled courses retrieved successfully", courses));
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
