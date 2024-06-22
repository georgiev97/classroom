package com.classroom.entity;

import com.classroom.enumartion.CourseType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Data
@Builder
@Entity
@Table(name = "course")
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private CourseType type;

    @ManyToMany(mappedBy = "courses")
    private Set<Student> students;

    @ManyToMany(mappedBy = "courses")
    private Set<Teacher> teachers;
}
