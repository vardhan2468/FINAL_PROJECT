package com.learnsphere.lms.controller;

import com.learnsphere.lms.dto.ApiResponse;
import com.learnsphere.lms.model.Course;
import com.learnsphere.lms.service.CourseService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/courses")
public class CourseController {

    private final CourseService courseService;

    // Constructor injection
    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    /**
     * Add a new course
     * 
     * @param course the course to add
     * @return ResponseEntity with standardized API response
     */
    @PostMapping("/add")
    public ResponseEntity<ApiResponse<Course>> addCourse(@Valid @RequestBody Course course) {
        Course savedCourse = courseService.createCourse(course);
        return new ResponseEntity<>(
                ApiResponse.success("Course created successfully", savedCourse),
                HttpStatus.CREATED);
    }

    /**
     * Fetch all courses
     * 
     * @return ResponseEntity with standardized API response
     */
    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<Course>>> getAllCourses() {
        List<Course> courses = courseService.getAllCourses();
        return ResponseEntity.ok(
                ApiResponse.success("Courses retrieved successfully", courses));
    }

    /**
     * Fetch course by ID
     * 
     * @param id the course ID
     * @return ResponseEntity with standardized API response
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Course>> getCourseById(@PathVariable Long id) {
        Course course = courseService.getCourseById(id);
        return ResponseEntity.ok(
                ApiResponse.success("Course retrieved successfully", course));
    }

    /**
     * Update an existing course (ADMIN only)
     * 
     * @param id            the course ID to update
     * @param courseDetails the updated course details
     * @return ResponseEntity with standardized API response
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Course>> updateCourse(@PathVariable Long id,
            @Valid @RequestBody Course courseDetails) {
        Course updatedCourse = courseService.updateCourse(id, courseDetails);
        return ResponseEntity.ok(
                ApiResponse.success("Course updated successfully", updatedCourse));
    }

    /**
     * Delete a course (ADMIN only)
     * 
     * @param id the course ID to delete
     * @return ResponseEntity with standardized API response
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCourse(@PathVariable Long id) {
        courseService.deleteCourse(id);
        return ResponseEntity.ok(
                ApiResponse.success("Course deleted successfully"));
    }
}
