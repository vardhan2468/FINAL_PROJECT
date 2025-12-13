package com.learnsphere.lms.controller;

import com.learnsphere.lms.model.Course;
import com.learnsphere.lms.service.CourseService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

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
     * @return ResponseEntity with the saved course and HTTP status
     */
    @PostMapping("/add")
    public ResponseEntity<Course> addCourse(@Valid @RequestBody Course course) {
        Course savedCourse = courseService.createCourse(course);
        return new ResponseEntity<>(savedCourse, HttpStatus.CREATED);
    }

    /**
     * Fetch all courses
     * 
     * @return ResponseEntity with list of all courses
     */
    @GetMapping("/all")
    public ResponseEntity<List<Course>> getAllCourses() {
        List<Course> courses = courseService.getAllCourses();
        return ResponseEntity.ok(courses);
    }

    /**
     * Fetch course by ID
     * 
     * @param id the course ID
     * @return ResponseEntity with the course if found, 404 otherwise
     */
    @GetMapping("/{id}")
    public ResponseEntity<Course> getCourseById(@PathVariable Long id) {
        Optional<Course> course = courseService.getCourseById(id);
        return course.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
