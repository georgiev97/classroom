package com.classroom.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
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
@Table(name = "student")
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "student_id", nullable = false)
    private UUID id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "age", nullable = false)
    private int age;

    @Column(name = "student_group", nullable = false)
    private String studentGroup;

    @ManyToMany(cascade = {
            CascadeType.PERSIST,
            CascadeType.MERGE
    })
    @JoinTable(
            name = "student_course",
            joinColumns = @JoinColumn(name = "student_id"),
            inverseJoinColumns = @JoinColumn(name = "course_id")
    )
    private Set<Course> courses = new HashSet<>();

    public Student(UUID id, String name, int age, String studentGroup, Set<Course> courses) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.studentGroup = studentGroup;
        this.courses = new HashSet<>(courses);
    }

    public Student(String name, int age, String studentGroup, Set<Course> courses) {
        this.name = name;
        this.age = age;
        this.studentGroup = studentGroup;
        this.courses = new HashSet<>(courses);
    }

    public Student(String name, int age, String studentGroup) {
        this.name = name;
        this.age = age;
        this.studentGroup = studentGroup;
    }

    public Student(){}
}
