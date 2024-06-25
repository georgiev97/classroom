package com.classroom.repository;

import com.classroom.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Repository
public interface StudentRepository extends JpaRepository<Student, UUID> {

    @Query("SELECT COUNT(student) FROM Student student")
    long studentsCount();

    Optional<Student> findByNameAndStudentGroupAndAge(String studentName, String studentGroup, int age);

    @Query("SELECT student FROM Student student JOIN FETCH student.courses courses WHERE courses.name = :courseName")
    Set<Student> findStudentsByCourse(String courseName);

    @Query("SELECT student FROM Student student WHERE student.studentGroup = :groupName")
    Set<Student> findStudentsByGroup(String groupName);

    @Query("SELECT student FROM Student student JOIN FETCH student.courses courses WHERE student.age > :age AND courses.name = :courseName")
    Set<Student> findStudentsOlderThanAgeInCourse(int age, String courseName);

    @Query("SELECT student FROM Student student " +
            "JOIN FETCH student.courses course " +
            "WHERE course.name = :courseName " +
            "AND  student.studentGroup = :groupName " )
    Set<Student> findStudentsByCourseAndGroup(String courseName, String groupName);
}
