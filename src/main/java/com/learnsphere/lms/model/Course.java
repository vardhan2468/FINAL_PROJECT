package com.learnsphere.lms.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "courses")
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "instructor_name")
    private String instructorName;

    @Column(name = "photo_url")
    private String photoUrl;

    // Default constructor
    public Course() {
    }

    // Constructor without id
    public Course(String title, String description, String instructorName) {
        this.title = title;
        this.description = description;
        this.instructorName = instructorName;
    }

    // Constructor with all fields including photo
    public Course(Long id, String title, String description, String instructorName, String photoUrl) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.instructorName = instructorName;
        this.photoUrl = photoUrl;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getInstructorName() {
        return instructorName;
    }

    public void setInstructorName(String instructorName) {
        this.instructorName = instructorName;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    @Override
    public String toString() {
        return "Course{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", instructorName='" + instructorName + '\'' +
                ", photoUrl='" + photoUrl + '\'' +
                '}';
    }
}
