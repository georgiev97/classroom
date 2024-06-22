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
import lombok.Builder;
import lombok.Data;

import java.util.Set;
import java.util.UUID;

@Data
@Builder
@Entity
@Table(name = "student")
public class Student {

    @Id
    @NotNull
    @GeneratedValue(strategy = GenerationType.UUID, generator = "uuid")
    @Column(name = "student_id", nullable = false)
    private UUID id;

    @Column(name = "name", nullable = false)
    private String name;
    @Column(name = "age", nullable = false)

    private int age;
    @Column(name = "group", nullable = false)

    private String group;

    @ManyToMany(cascade = {
            CascadeType.PERSIST,
            CascadeType.MERGE
    })
    @JoinTable(
            name = "student_course",
            joinColumns = @JoinColumn(name = "student_id"),
            inverseJoinColumns = @JoinColumn(name = "course_id")
    )
    private Set<Course> courses;

}
