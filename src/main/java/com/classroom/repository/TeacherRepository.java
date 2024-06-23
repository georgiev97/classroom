package com.classroom.repository;

import com.classroom.entity.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Set;
import java.util.UUID;

@Repository
public interface TeacherRepository extends JpaRepository<Teacher, UUID> {

    @Query("SELECT COUNT(teacher) FROM Teacher teacher")
    long teachersCount();

    @Query("SELECT teacher FROM Teacher teacher JOIN teacher.courses courses WHERE courses.name = :courseName")
    Set<Teacher> findTeachersByCourse(String courseName);

    @Query("SELECT teacher FROM Teacher teacher WHERE teacher.teacherGroup = :groupName")
    Set<Teacher> findTeachersByGroup(String groupName);
}
