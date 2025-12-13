package com.learnsphere.lms.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "enrollments")
public class Enrollment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Column(name = "enrolled_at", nullable = false)
    private LocalDateTime enrolledAt;

    // Default constructor
    public Enrollment() {
    }

    // Constructor without id
    public Enrollment(User user, Course course, LocalDateTime enrolledAt) {
        this.user = user;
        this.course = course;
        this.enrolledAt = enrolledAt;
    }

    // Constructor with all fields
    public Enrollment(Long id, User user, Course course, LocalDateTime enrolledAt) {
        this.id = id;
        this.user = user;
        this.course = course;
        this.enrolledAt = enrolledAt;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public LocalDateTime getEnrolledAt() {
        return enrolledAt;
    }

    public void setEnrolledAt(LocalDateTime enrolledAt) {
        this.enrolledAt = enrolledAt;
    }

    @Override
    public String toString() {
        return "Enrollment{" +
                "id=" + id +
                ", user=" + (user != null ? user.getId() : null) +
                ", course=" + (course != null ? course.getId() : null) +
                ", enrolledAt=" + enrolledAt +
                '}';
    }
}
