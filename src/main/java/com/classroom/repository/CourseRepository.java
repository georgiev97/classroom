package com.classroom.repository;

import com.classroom.entity.Course;
import com.classroom.enumartion.CourseType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CourseRepository extends JpaRepository<Course, UUID> {

    @Query("SELECT COUNT(course) FROM Course course WHERE course.type = :courseType")
    long countCoursesByType(CourseType courseType);

    boolean existsByName(String courseName);
}
