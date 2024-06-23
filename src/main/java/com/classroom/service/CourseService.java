package com.classroom.service;

import com.classroom.dto.course.CourseResponseDTO;
import com.classroom.dto.course.CreateCourseRequestDTO;
import com.classroom.entity.Course;
import com.classroom.enumartion.CourseType;
import com.classroom.repository.CourseRepository;
import jakarta.persistence.EntityExistsException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CourseService {

    private static final String COURSE_ALREADY_EXISTS = "Course with name %s already exists";

    private final CourseRepository courseRepository;

    public CourseResponseDTO createCourse(CreateCourseRequestDTO createCourseRequest) {
        String courseName = createCourseRequest.getCourseName();
        String courseTypeName = createCourseRequest.getCourseTypeName();
        if (courseRepository.existsByName(courseName)) {
            throw new EntityExistsException(String.format(COURSE_ALREADY_EXISTS, courseName));
        }
        CourseType courseType = CourseType.fromString(courseTypeName);
        Course course = new Course(courseName, courseType);
        courseRepository.save(course);

        return CourseResponseDTO.builder()
                .courseName(courseName)
                .courseTypeName(courseTypeName)
                .build();
    }

    public List<CourseResponseDTO> getAllCourses() {
        return courseRepository
                .findAll()
                .stream()
                .map(course ->
                        CourseResponseDTO.builder()
                                .courseName(course.getName())
                                .courseTypeName(course.getType().toString())
                                .build()
                )
                .toList();
    }

    public Optional<CourseResponseDTO> getCourseByName(String courseName) {
        return courseRepository
                .findByName(courseName)
                .map(course ->
                        CourseResponseDTO.builder()
                                .courseName(course.getName())
                                .courseTypeName(course.getType().toString())
                                .build()
                );
    }
}
