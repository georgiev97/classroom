package com.classroom.service;

import com.classroom.enumartion.CourseType;
import com.classroom.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final CourseRepository courseRepository;

    public long countCoursesByType(String courseType) {
        CourseType type = CourseType.fromString(courseType);
        return courseRepository.countCoursesByType(type);
    }
}
