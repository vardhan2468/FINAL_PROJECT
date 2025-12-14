package com.learnsphere.lms.service;

import com.learnsphere.lms.exception.ResourceNotFoundException;
import com.learnsphere.lms.model.Course;
import com.learnsphere.lms.repository.CourseRepository;
import org.springframework.stereotype.Service;

import java.util.List;

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
     * @return the course if found
     * @throws ResourceNotFoundException if course not found
     */
    public Course getCourseById(Long id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course", "id", id));
    }

    /**
     * Update an existing course
     * 
     * @param id            the course ID to update
     * @param courseDetails the updated course details
     * @return the updated course
     * @throws ResourceNotFoundException if course not found
     */
    public Course updateCourse(Long id, Course courseDetails) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course", "id", id));

        course.setTitle(courseDetails.getTitle());
        course.setDescription(courseDetails.getDescription());
        course.setInstructorName(courseDetails.getInstructorName());
        return courseRepository.save(course);
    }

    /**
     * Delete a course by its ID
     * 
     * @param id the course ID to delete
     * @throws ResourceNotFoundException if course not found
     */
    public void deleteCourse(Long id) {
        if (!courseRepository.existsById(id)) {
            throw new ResourceNotFoundException("Course", "id", id);
        }
        courseRepository.deleteById(id);
    }
}
