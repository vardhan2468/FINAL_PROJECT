package com.learnsphere.lms.service;

import com.learnsphere.lms.exception.DuplicateEnrollmentException;
import com.learnsphere.lms.model.Course;
import com.learnsphere.lms.model.Enrollment;
import com.learnsphere.lms.model.User;
import com.learnsphere.lms.repository.EnrollmentRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;

    // Constructor injection
    public EnrollmentService(EnrollmentRepository enrollmentRepository) {
        this.enrollmentRepository = enrollmentRepository;
    }

    /**
     * Enroll a user into a course
     * Prevents duplicate enrollments
     * 
     * @param user   the user to enroll
     * @param course the course to enroll in
     * @return the saved enrollment
     * @throws DuplicateEnrollmentException if user is already enrolled in the
     *                                      course
     */
    public Enrollment enrollUser(User user, Course course) {
        // Check if user is already enrolled
        List<Enrollment> existingEnrollments = enrollmentRepository.findByUserId(user.getId());
        boolean alreadyEnrolled = existingEnrollments.stream()
                .anyMatch(e -> e.getCourse().getId().equals(course.getId()));

        if (alreadyEnrolled) {
            throw new DuplicateEnrollmentException(user.getId(), course.getId());
        }

        // Create new enrollment
        Enrollment enrollment = new Enrollment();
        enrollment.setUser(user);
        enrollment.setCourse(course);
        enrollment.setEnrolledAt(LocalDateTime.now());

        return enrollmentRepository.save(enrollment);
    }

    /**
     * Fetch all courses enrolled by a user
     * 
     * @param userId the user ID
     * @return list of courses the user is enrolled in
     */
    public List<Course> getCoursesEnrolledByUser(Long userId) {
        List<Enrollment> enrollments = enrollmentRepository.findByUserId(userId);
        return enrollments.stream()
                .map(Enrollment::getCourse)
                .collect(Collectors.toList());
    }

    /**
     * Fetch all enrollments for a user
     * 
     * @param userId the user ID
     * @return list of enrollments
     */
    public List<Enrollment> getEnrollmentsByUserId(Long userId) {
        return enrollmentRepository.findByUserId(userId);
    }

    /**
     * Fetch all enrollments for a course
     * 
     * @param courseId the course ID
     * @return list of enrollments
     */
    public List<Enrollment> getEnrollmentsByCourseId(Long courseId) {
        return enrollmentRepository.findByCourseId(courseId);
    }
}
