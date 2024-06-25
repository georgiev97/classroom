package com.classroom.service;

import com.classroom.dto.teacher.CreateTeacherRequestDTO;
import com.classroom.dto.teacher.EnrollTeacherRequestDTO;
import com.classroom.dto.teacher.LeaveTeacherCourseRequestDTO;
import com.classroom.dto.teacher.TeacherResponseDTO;
import com.classroom.entity.Course;
import com.classroom.entity.Teacher;
import com.classroom.enumartion.CourseType;
import com.classroom.repository.CourseRepository;
import com.classroom.repository.TeacherRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.Set;
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
public class TeacherServiceTest {

    private static final UUID TEACHER_ID = UUID.randomUUID();
    private static final String TEACHER_NAME_1 = "Kiril";
    private static final String TEACHER_NAME_2 = "Petko";
    private static final String TEACHER_NAME_3 = "Rumyana";
    private static final UUID COURSE_ID = UUID.randomUUID(); 
    private static final String COURSE_NAME_MATHEMATICS = "Mathematics";
    private static final String COURSE_NAME_HISTORY = "History";
    private static final String GROUP_NAME_1 = "A1";
    private static final String GROUP_NAME_2 = "A2";

    private TeacherService teacherService;

    @Mock
    private CourseRepository courseRepository;
    @Mock
    private TeacherRepository teacherRepository;

    @BeforeEach
    public void setUp() {
        teacherService = new TeacherService(teacherRepository, courseRepository);
    }

    @Test
    public void testCreateTeacherSuccess() {
        // arrange
        CreateTeacherRequestDTO request = CreateTeacherRequestDTO.builder()
                .teacherName("Dimitar")
                .teacherGroupName("B1")
                .teacherAge(45)
                .build();
        Teacher savedTeacher = new Teacher(
                TEACHER_ID,
                request.getTeacherName(),
                request.getTeacherAge(),
                request.getTeacherGroupName()
        );

        when(teacherRepository.findByNameAndTeacherGroupAndAge(
                request.getTeacherName(),
                request.getTeacherGroupName(),
                request.getTeacherAge())
        ).thenReturn(Optional.empty());
        when(teacherRepository.save(any(Teacher.class))).thenReturn(savedTeacher);

        // act
        TeacherResponseDTO response = teacherService.createTeacher(request);

        // assert
        assertNotNull(response);
        assertEquals(request.getTeacherName(), response.getTeacherName());
        assertEquals(request.getTeacherAge(), response.getTeacherAge());
        verify(teacherRepository, times(1)).findByNameAndTeacherGroupAndAge(
                request.getTeacherName(),
                request.getTeacherGroupName(),
                request.getTeacherAge()
        );
        verify(teacherRepository, times(1)).save(any(Teacher.class));
    }

    @Test
    public void testCreateTeacherAlreadyExists() {
        // arrange
        CreateTeacherRequestDTO request = CreateTeacherRequestDTO.builder()
                .teacherName("Dimitar")
                .teacherGroupName("B1")
                .teacherAge(45)
                .build();

        Teacher savedTeacher = new Teacher(
                UUID.randomUUID(),
                request.getTeacherName(),
                request.getTeacherAge(),
                request.getTeacherGroupName()
        );

        when(teacherRepository.findByNameAndTeacherGroupAndAge(
                request.getTeacherName(),
                request.getTeacherGroupName(),
                request.getTeacherAge())
        ).thenReturn(Optional.of(savedTeacher));

        // act
        EntityExistsException exception = assertThrows(EntityExistsException.class, () -> {
            teacherService.createTeacher(request);
        });

        // assert
        assertEquals(String.format("Teacher with name %s already exists", request.getTeacherName()), exception.getMessage());
        verify(teacherRepository, times(1)).findByNameAndTeacherGroupAndAge(
                request.getTeacherName(),
                request.getTeacherGroupName(),
                request.getTeacherAge());
        verify(teacherRepository, never()).save(any(Teacher.class));
    }

    @Test
    public void testUpdateTeacherSuccess() {
        // arrange
        CreateTeacherRequestDTO request = CreateTeacherRequestDTO.builder()
                .teacherName("Dimitar")
                .teacherGroupName("B1")
                .teacherAge(45)
                .build();

        Teacher savedTeacher = new Teacher(
                TEACHER_ID,
                request.getTeacherName(),
                44,
                request.getTeacherGroupName()
        );

        Teacher updatedTeacher = new Teacher(
                TEACHER_ID,
                request.getTeacherName(),
                request.getTeacherAge(),
                request.getTeacherGroupName()
        );

        when(teacherRepository.findById(TEACHER_ID)).thenReturn(Optional.of(savedTeacher));
        when(teacherRepository.save(any(Teacher.class))).thenReturn(updatedTeacher);

        // act
        TeacherResponseDTO response = teacherService.updateTeacher(
                TEACHER_ID.toString(),
                request
        );

        // assert
        assertNotNull(response);
        assertEquals(request.getTeacherName(), response.getTeacherName());
        assertEquals(request.getTeacherAge(), response.getTeacherAge());
        verify(teacherRepository, times(1)).findById(TEACHER_ID);
        verify(teacherRepository, times(1)).save(any(Teacher.class));}

    @Test
    public void testUpdateTeacherFailure() {
        // arrange
        CreateTeacherRequestDTO request = CreateTeacherRequestDTO.builder()
                .teacherName("Dimitar")
                .teacherGroupName("B1")
                .teacherAge(45)
                .build();

        when(teacherRepository.findById(TEACHER_ID)).thenReturn(Optional.empty());

        // act
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            teacherService.updateTeacher(TEACHER_ID.toString(), request);
        });

        // assert
        assertEquals(String.format("Teacher with ID %s does not exists", TEACHER_ID), exception.getMessage());
        verify(teacherRepository, times(1)).findById(TEACHER_ID);
        verify(teacherRepository, never()).save(any(Teacher.class));
    }

    @Test
    public void testDeleteTeacherSuccess() {
        // arrange
        Teacher Teacher = new Teacher(TEACHER_ID, "Dimitar", 45, "B1");

        when(teacherRepository.findById(TEACHER_ID)).thenReturn(Optional.of(Teacher));

        // Act
        teacherService.deleteTeacher(TEACHER_ID.toString());

        // Assert
        verify(teacherRepository, times(1)).findById(TEACHER_ID);
        verify(teacherRepository, times(1)).delete(Teacher);
    }


    @Test
    public void testDeleteTeacherNotFound() {
        // Arrange
        when(teacherRepository.findById(TEACHER_ID)).thenReturn(Optional.empty());

        // Act
        EntityNotFoundException thrown = assertThrows(EntityNotFoundException.class, () -> {
            teacherService.deleteTeacher(TEACHER_ID.toString());
        });

        // assert
        assertEquals(String.format("Teacher with ID %s does not exists", TEACHER_ID), thrown.getMessage());
        verify(teacherRepository, times(1)).findById(TEACHER_ID);
        verify(teacherRepository, never()).delete(any(Teacher.class));
    }

    @Test
    public void testEnrollTeacherSuccess() {
        // Arrange
        EnrollTeacherRequestDTO request = EnrollTeacherRequestDTO.builder()
                .teacherId(TEACHER_ID.toString())
                .courseName(COURSE_NAME_MATHEMATICS)
                .courseType(CourseType.MAIN.name())
                .build();
        Teacher savedTeacher = new Teacher(
                TEACHER_ID,
                TEACHER_NAME_1,
                44,
                GROUP_NAME_1
        );
        Course course = new Course(COURSE_ID, COURSE_NAME_MATHEMATICS, CourseType.MAIN);
        Teacher updatedTeacher = new Teacher(
                TEACHER_ID,
                TEACHER_NAME_1,
                44,
                GROUP_NAME_1,
                Set.of(course)
        );

        when(teacherRepository.findById(TEACHER_ID)).thenReturn(Optional.of(savedTeacher));
        when(courseRepository.findByName(COURSE_NAME_MATHEMATICS)).thenReturn(Optional.of(course));
        when(teacherRepository.save(any(Teacher.class))).thenReturn(updatedTeacher);

        // Act
        TeacherResponseDTO response = teacherService.enrollToCourse(request);

        // assert
        assertNotNull(response);
        assertEquals(TEACHER_NAME_1, response.getTeacherName());
        assertEquals(44, response.getTeacherAge());
        assertEquals(GROUP_NAME_1, response.getTeacherGroupName());
        assertEquals(COURSE_NAME_MATHEMATICS, response.getTeacherCourses().get(0).getCourseName());
        assertEquals(CourseType.MAIN.name(), response.getTeacherCourses().get(0).getCourseTypeName());
        verify(teacherRepository, times(1)).findById(TEACHER_ID);
        verify(courseRepository, times(1)).findByName(COURSE_NAME_MATHEMATICS);
        verify(teacherRepository, times(1)).save(any(Teacher.class));
    }

    @Test
    public void testEnrollTeacherTeacherDoesNotExist() {
        // Arrange
        EnrollTeacherRequestDTO request = EnrollTeacherRequestDTO.builder()
                .teacherId(TEACHER_ID.toString())
                .courseName(COURSE_NAME_MATHEMATICS)
                .courseType(CourseType.MAIN.name())
                .build();

        when(teacherRepository.findById(TEACHER_ID)).thenReturn(Optional.empty());

        // act
        EntityExistsException exception = assertThrows(EntityExistsException.class, () -> {
            teacherService.enrollToCourse(request);
        });

        // assert
        assertEquals(String.format("Teacher with ID %s does not exists", TEACHER_ID), exception.getMessage());
        verify(teacherRepository, times(1)).findById(TEACHER_ID);
        verify(courseRepository, never()).findByName(COURSE_NAME_MATHEMATICS);
        verify(teacherRepository, never()).save(any(Teacher.class));
    }

    @Test
    public void testEnrollTeacherTeacherAlreadyEnrolled() {
        // Arrange
        EnrollTeacherRequestDTO request = EnrollTeacherRequestDTO.builder()
                .teacherId(TEACHER_ID.toString())
                .courseName(COURSE_NAME_MATHEMATICS)
                .courseType(CourseType.MAIN.name())
                .build();

        Course course = new Course(COURSE_ID, COURSE_NAME_MATHEMATICS, CourseType.MAIN);
        Teacher savedTeacher = new Teacher(
                TEACHER_ID,
                TEACHER_NAME_1,
                44,
                GROUP_NAME_1,
                Set.of(course)
        );

        when(teacherRepository.findById(TEACHER_ID)).thenReturn(Optional.of(savedTeacher));

        // act
        EntityExistsException exception = assertThrows(EntityExistsException.class, () -> {
            teacherService.enrollToCourse(request);
        });

        // assert
        assertEquals(String.format("Teacher with ID %s already enrolled for course with name: %s", TEACHER_ID, COURSE_NAME_MATHEMATICS), exception.getMessage());
        verify(teacherRepository, times(1)).findById(TEACHER_ID);
        verify(courseRepository, never()).findByName(COURSE_NAME_MATHEMATICS);
        verify(teacherRepository, never()).save(any(Teacher.class));
    }

    @Test
    public void testLeaveTeacherFromCourseSuccess() {
        // Arrange
        LeaveTeacherCourseRequestDTO request = LeaveTeacherCourseRequestDTO.builder()
                .teacherId(TEACHER_ID.toString())
                .courseName(COURSE_NAME_MATHEMATICS)
                .build();

        Course course = new Course(COURSE_ID, COURSE_NAME_MATHEMATICS, CourseType.MAIN);
        Teacher savedTeacher = new Teacher(
                TEACHER_ID,
                TEACHER_NAME_1,
                44,
                GROUP_NAME_1,
                Set.of(course)
        );
        Teacher updatedTeacher = new Teacher(
                TEACHER_ID,
                TEACHER_NAME_1,
                44,
                GROUP_NAME_1
        );

        when(teacherRepository.findById(TEACHER_ID)).thenReturn(Optional.of(savedTeacher));
        when(courseRepository.findByName(COURSE_NAME_MATHEMATICS)).thenReturn(Optional.of(course));
        when(teacherRepository.save(any(Teacher.class))).thenReturn(updatedTeacher);

        // Act
        TeacherResponseDTO response = teacherService.removeTeacherFromCourse(request);

        // assert
        assertNotNull(response);
        assertEquals(TEACHER_NAME_1, response.getTeacherName());
        assertEquals(44, response.getTeacherAge());
        assertEquals(GROUP_NAME_1, response.getTeacherGroupName());
        assertTrue(response.getTeacherCourses().isEmpty());
        verify(teacherRepository, times(1)).findById(TEACHER_ID);
        verify(courseRepository, times(1)).findByName(COURSE_NAME_MATHEMATICS);
        verify(teacherRepository, times(1)).save(any(Teacher.class));
    }

    @Test
    public void testLeaveTeacherFromCourseTeacherDoesNotExist() {
        // Arrange
        LeaveTeacherCourseRequestDTO request = LeaveTeacherCourseRequestDTO.builder()
                .teacherId(TEACHER_ID.toString())
                .courseName(COURSE_NAME_MATHEMATICS)
                .build();

        when(teacherRepository.findById(TEACHER_ID)).thenReturn(Optional.empty());

        // act
        EntityExistsException exception = assertThrows(EntityExistsException.class, () -> {
            teacherService.removeTeacherFromCourse(request);
        });

        // assert
        assertEquals(String.format("Teacher with ID %s does not exists", TEACHER_ID), exception.getMessage());
        verify(teacherRepository, times(1)).findById(TEACHER_ID);
        verify(courseRepository, never()).findByName(COURSE_NAME_MATHEMATICS);
        verify(teacherRepository, never()).save(any(Teacher.class));
    }

    @Test
    public void testLeaveTeacherFromCourseTeacherNotEnrolled() {
        // Arrange
        LeaveTeacherCourseRequestDTO request = LeaveTeacherCourseRequestDTO.builder()
                .teacherId(TEACHER_ID.toString())
                .courseName(COURSE_NAME_MATHEMATICS)
                .build();

        Teacher savedTeacher = new Teacher(
                TEACHER_ID,
                TEACHER_NAME_1,
                44,
                GROUP_NAME_1
        );

        when(teacherRepository.findById(TEACHER_ID)).thenReturn(Optional.of(savedTeacher));

        // Act
        EntityExistsException exception = assertThrows(EntityExistsException.class, () -> {
            teacherService.removeTeacherFromCourse(request);
        });

        // assert
        assertEquals(String.format("Teacher with ID %s is not enrolled for course with name: %s", TEACHER_ID, request.getCourseName()), exception.getMessage());
        verify(teacherRepository, times(1)).findById(TEACHER_ID);
        verify(courseRepository, never()).findByName(COURSE_NAME_MATHEMATICS);
        verify(teacherRepository, never()).save(any(Teacher.class));
    }
}
