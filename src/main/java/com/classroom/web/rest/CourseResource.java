package com.classroom.web.rest;

import com.classroom.dto.course.CourseResponseDTO;
import com.classroom.dto.course.CreateCourseRequestDTO;
import com.classroom.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/courses")
@RequiredArgsConstructor
public class CourseResource {

    private final CourseService courseService;

    @GetMapping
    public ResponseEntity<List<CourseResponseDTO>> getAllCourses() {
        List<CourseResponseDTO> students = courseService.getAllCourses();
        return ResponseEntity.ok(students);
    }

    @GetMapping("/course/{courseName}")
    public ResponseEntity<CourseResponseDTO> getCourseByName(
            @PathVariable String courseName
    ) {
        Optional<CourseResponseDTO> course = courseService.getCourseByName(courseName);
        return course.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<CourseResponseDTO> createCourse(
            @RequestBody CreateCourseRequestDTO createCourseRequest
    ) {
        CourseResponseDTO createdCourse = courseService.createCourse(createCourseRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdCourse);
    }

}
