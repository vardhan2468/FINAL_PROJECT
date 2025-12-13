package com.learnsphere.lms.service;

import com.learnsphere.lms.model.Course;
import com.learnsphere.lms.repository.CourseRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CourseService {

    private final CourseRepository courseRepository;

    // Constructor injection
    public CourseService(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    /**
     * Create a new course
     * 
     * @param course the course to create
     * @return the saved course
     */
    public Course createCourse(Course course) {
        return courseRepository.save(course);
    }

    /**
     * Fetch all courses from the database
     * 
     * @return list of all courses
     */
    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    /**
     * Fetch a course by its ID
     * 
     * @param id the course ID
     * @return Optional containing the course if found, empty otherwise
     */
    public Optional<Course> getCourseById(Long id) {
        return courseRepository.findById(id);
    }
}
