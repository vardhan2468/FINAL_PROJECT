package com.learnsphere.lms.exception;

public class DuplicateEnrollmentException extends RuntimeException {

    public DuplicateEnrollmentException(String message) {
        super(message);
    }

    public DuplicateEnrollmentException(Long userId, Long courseId) {
        super(String.format("User with ID '%s' is already enrolled in course with ID '%s'", userId, courseId));
    }
}
