package com.classroom.service;

import com.classroom.dto.course.CourseResponseDTO;
import com.classroom.dto.course.CreateCourseRequestDTO;
import com.classroom.entity.Course;
import com.classroom.enumartion.CourseType;
import com.classroom.repository.CourseRepository;
import jakarta.persistence.EntityExistsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CourseServiceTest {

    private CourseService courseService;

    @Mock
    private CourseRepository courseRepository;

    @BeforeEach
    public void setUp() {
        courseService = new CourseService(courseRepository);
    }

    @Test
    public void testCreateCourseSuccess() {
        // arrange
        CreateCourseRequestDTO request = CreateCourseRequestDTO.builder()
                .courseName("Math")
                .courseTypeName("MAIN")
                .build();
        Course course = new Course(
                request.getCourseName(),
                CourseType.valueOf(request.getCourseTypeName())
        );
        Course savedCourse = new Course(
                UUID.randomUUID(),
                request.getCourseName(),
                CourseType.valueOf(request.getCourseTypeName())
        );


        when(courseRepository.existsByName(request.getCourseName())).thenReturn(false);
        when(courseRepository.save(any(Course.class))).thenReturn(savedCourse);

        // act
        CourseResponseDTO response = courseService.createCourse(request);

        // assert
        assertNotNull(response);
        assertEquals(request.getCourseName(), response.getCourseName());
        assertEquals(request.getCourseTypeName(), response.getCourseTypeName());
        verify(courseRepository, times(1)).existsByName(request.getCourseName());
        verify(courseRepository, times(1)).save(any(Course.class));
    }

    @Test
    public void testCreateCourseEntityExistsException() {
        // arrange
        CreateCourseRequestDTO request = CreateCourseRequestDTO.builder()
                .courseName("Math")
                .courseTypeName("MAIN")
                .build();

        when(courseRepository.existsByName(request.getCourseName())).thenReturn(true);

        // act
        EntityExistsException exception = assertThrows(EntityExistsException.class, () -> {
            courseService.createCourse(request);
        });

        // assert
        assertEquals(String.format("Course with name %s already exists", request.getCourseName()), exception.getMessage());
        verify(courseRepository, times(1)).existsByName(request.getCourseName());
        verify(courseRepository, never()).save(any(Course.class));
    }

    @Test
    public void testGetAllCourses() {
        // arrange
        List<Course> courses = new ArrayList<>();
        courses.add(new Course(UUID.randomUUID(),"Math", CourseType.MAIN));
        courses.add(new Course(UUID.randomUUID(),"Science", CourseType.SECONDARY));

        when(courseRepository.findAll()).thenReturn(courses);

        // act
        List<CourseResponseDTO> response = courseService.getAllCourses();

        // assert
        assertNotNull(response);
        assertEquals(courses.size(), response.size());
        for (int i = 0; i < courses.size(); i++) {
            Course course = courses.get(i);
            CourseResponseDTO dto = response.get(i);
            assertEquals(course.getName(), dto.getCourseName());
            assertEquals(course.getType().toString(), dto.getCourseTypeName());
        }
        verify(courseRepository, times(1)).findAll();
    }

    @Test
    public void testGetCourseByNameSuccess() {
        // arrange
        String courseName = "Math";
        Course course = new Course(UUID.randomUUID(),courseName, CourseType.MAIN);

        when(courseRepository.findByName(courseName)).thenReturn(Optional.of(course));

        // ac
        Optional<CourseResponseDTO> response = courseService.getCourseByName(courseName);

        // assert
        assertTrue(response.isPresent());
        assertEquals(course.getName(), response.get().getCourseName());
        assertEquals(course.getType().toString(), response.get().getCourseTypeName());
        verify(courseRepository, times(1)).findByName(courseName);
    }

    @Test
    public void testGetCourseByNameNotFound() {
        // arrange
        String courseName = "Math";

        when(courseRepository.findByName(courseName)).thenReturn(Optional.empty());

        // act
        Optional<CourseResponseDTO> response = courseService.getCourseByName(courseName);

        // assert
        assertTrue(response.isEmpty());
        verify(courseRepository, times(1)).findByName(courseName);
    }
}
