package com.classroom.repository;

import com.classroom.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Set;
import java.util.UUID;

@Repository
public interface StudentRepository extends JpaRepository<Student, UUID> {

    @Query("SELECT COUNT(student) FROM Student student")
    long studentsCount();

    @Query("SELECT student FROM Student student JOIN student.courses courses WHERE courses.name = :courseName")
    Set<Student> findStudentsByCourse(String courseName);

    @Query("SELECT student FROM Student student WHERE student.studentGroup = :groupName")
    Set<Student> findStudentsByGroup(String groupName);

    @Query("SELECT student FROM Student student JOIN student.courses courses WHERE student.age > :age AND courses.name = :courseName")
    Set<Student> findStudentsOlderThanAgeInCourse(int age, String courseName);
}
