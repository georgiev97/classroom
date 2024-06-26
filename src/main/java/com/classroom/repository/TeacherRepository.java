package com.classroom.repository;

import com.classroom.entity.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Repository
public interface TeacherRepository extends JpaRepository<Teacher, UUID> {

    @Query("SELECT COUNT(teacher) FROM Teacher teacher")
    long countTeachers();

    Optional<Teacher> findByNameAndTeacherGroupAndAge(String teacherName, String teacherGroup, int age);

    @Query("SELECT teacher FROM Teacher teacher JOIN FETCH teacher.courses courses WHERE courses.name = :courseName")
    Set<Teacher> findTeachersByCourse(String courseName);

    @Query("SELECT teacher FROM Teacher teacher WHERE teacher.teacherGroup = :groupName")
    Set<Teacher> findTeachersByGroup(String groupName);

    @Query("SELECT teacher FROM Teacher teacher " +
            "JOIN FETCH teacher.courses course " +
            "WHERE course.name = :courseName " +
            "AND  teacher.teacherGroup = :groupName " )
    Set<Teacher> findTeachersByCourseAndGroup(String courseName, String groupName);
}
