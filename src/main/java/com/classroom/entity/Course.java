package com.classroom.entity;

import com.classroom.enumartion.CourseType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Data;

import java.util.Set;
import java.util.UUID;

@Data
@Builder
@Entity
@Table(name = "course")
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "course_id", nullable = false)
    private UUID id;

    private String name;

    @Enumerated(value = EnumType.STRING)
    private CourseType type;

    @ManyToMany(mappedBy = "courses")
    private Set<Student> students;

    @ManyToMany(mappedBy = "courses")
    private Set<Teacher> teachers;
}
