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
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "teacher")
public class Teacher {

    @Id
    @NotNull
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "teacher_id", nullable = false)
    private UUID id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "age", nullable = false)
    private int age;

    @Column(name = "teacher_group", nullable = false)
    private String teacherGroup;

    @ManyToMany(cascade = {
            CascadeType.PERSIST,
            CascadeType.MERGE
    })
    @JoinTable(
            name = "teacher_course",
            joinColumns = @JoinColumn(name = "teacher_id"),
            inverseJoinColumns = @JoinColumn(name = "course_id")
    )
    private Set<Course> courses =  new HashSet<>();

    public Teacher(UUID id, String name, int age, String teacherGroup, Set<Course> courses) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.teacherGroup = teacherGroup;
        this.courses = new HashSet<>(courses);
    }

    public Teacher(UUID id, String name, int age, String teacherGroup) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.teacherGroup = teacherGroup;
    }

    public Teacher(String name, int age, String teacherGroup, Set<Course> courses) {
        this.name = name;
        this.age = age;
        this.teacherGroup = teacherGroup;
        this.courses = new HashSet<>(courses);
    }

    public Teacher(String name, int age, String teacherGroup) {
        this.name = name;
        this.age = age;
        this.teacherGroup = teacherGroup;
    }

    public Teacher(){}
}
