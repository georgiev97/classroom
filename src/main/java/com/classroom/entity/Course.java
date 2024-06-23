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
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
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
    private Set<Student> students = new HashSet<>();

    @ManyToMany(mappedBy = "courses")
    private Set<Teacher> teachers = new HashSet<>();

    public Course(UUID id, String name, CourseType type, Set<Student> students, Set<Teacher> teachers) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.students = students;
        this.teachers = new HashSet<>(teachers);
    }

    public Course(UUID id, String name, CourseType type) {
        this.id = id;
        this.name = name;
        this.type = type;
    }

    public Course(String name, CourseType type) {
        this.name = name;
        this.type = type;
    }

    public Course() {}
}
