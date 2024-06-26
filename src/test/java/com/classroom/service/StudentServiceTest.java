package com.classroom.service;

import com.classroom.dto.student.CreateStudentRequestDTO;
import com.classroom.dto.student.EnrollStudentRequestDTO;
import com.classroom.dto.student.LeaveStudentCourseRequestDTO;
import com.classroom.dto.student.StudentResponseDTO;
import com.classroom.entity.Course;
import com.classroom.entity.Student;
import com.classroom.enumartion.CourseType;
import com.classroom.exception.UnprocessableEntityException;
import com.classroom.repository.CourseRepository;
import com.classroom.repository.StudentRepository;
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
public class StudentServiceTest {

    private static final UUID STUDENT_ID = UUID.randomUUID();
    private static final String STUDENT_NAME_1 = "Mariya";
    private static final String STUDENT_NAME_2 = "Ivan";
    private static final String STUDENT_NAME_3 = "Georgi";
    private static final UUID COURSE_ID = UUID.randomUUID();

    private static final String COURSE_NAME_MATHEMATICS = "Mathematics";
    private static final String COURSE_NAME_HISTORY = "History";
    private static final String COURSE_NAME_SCIENCE = "Science";

    private static final String GROUP_NAME_1 = "A1";
    private static final String GROUP_NAME_2 = "A2";

    private StudentService studentService;

    @Mock
    private CourseRepository courseRepository;
    @Mock
    private StudentRepository studentRepository;

    @BeforeEach
    public void setUp() {
        studentService = new StudentService(studentRepository, courseRepository);
    }

    @Test
    public void testCreateStudentSuccess() {
        // arrange
        CreateStudentRequestDTO request = CreateStudentRequestDTO.builder()
                .studentName("Kremena")
                .studentGroupName("B1")
                .studentAge(25)
                .build();
        Student savedStudent = new Student(
                STUDENT_ID,
                request.getStudentName(),
                request.getStudentAge(),
                request.getStudentGroupName()
        );

        when(studentRepository.findByNameAndStudentGroupAndAge(
                request.getStudentName(),
                request.getStudentGroupName(),
                request.getStudentAge())
        ).thenReturn(Optional.empty());
        when(studentRepository.save(any(Student.class))).thenReturn(savedStudent);

        // act
        StudentResponseDTO response = studentService.createStudent(request);

        // assert
        assertNotNull(response);
        assertEquals(request.getStudentName(), response.getStudentName());
        assertEquals(request.getStudentAge(), response.getStudentAge());
        verify(studentRepository, times(1)).findByNameAndStudentGroupAndAge(
                request.getStudentName(),
                request.getStudentGroupName(),
                request.getStudentAge()
        );
        verify(studentRepository, times(1)).save(any(Student.class));
    }

    @Test
    public void testCreateStudentAlreadyExists() {
        // arrange
        CreateStudentRequestDTO request = CreateStudentRequestDTO.builder()
                .studentName("Kremena")
                .studentGroupName("B1")
                .studentAge(25)
                .build();

        Student savedStudent = new Student(
                UUID.randomUUID(),
                request.getStudentName(),
                request.getStudentAge(),
                request.getStudentGroupName()
        );

        when(studentRepository.findByNameAndStudentGroupAndAge(
                request.getStudentName(),
                request.getStudentGroupName(),
                request.getStudentAge())
        ).thenReturn(Optional.of(savedStudent));

        // act
        EntityExistsException exception = assertThrows(EntityExistsException.class, () -> {
            studentService.createStudent(request);
        });

        // assert
        assertEquals(String.format("Student with name %s already exists", request.getStudentName()), exception.getMessage());
        verify(studentRepository, times(1)).findByNameAndStudentGroupAndAge(
                request.getStudentName(),
                request.getStudentGroupName(),
                request.getStudentAge());
        verify(studentRepository, never()).save(any(Student.class));
    }

    @Test
    public void testUpdateStudentSuccess() {
        // arrange
        CreateStudentRequestDTO request = CreateStudentRequestDTO.builder()
                .studentName("Kremena")
                .studentGroupName("B1")
                .studentAge(25)
                .build();

        Student savedStudent = new Student(
                STUDENT_ID,
                request.getStudentName(),
                22,
                request.getStudentGroupName()
        );

        Student updatedStudent = new Student(
                STUDENT_ID,
                request.getStudentName(),
                request.getStudentAge(),
                request.getStudentGroupName()
        );

        when(studentRepository.findById(STUDENT_ID)).thenReturn(Optional.of(savedStudent));
        when(studentRepository.save(any(Student.class))).thenReturn(updatedStudent);

        // act
        StudentResponseDTO response = studentService.updateStudent(
                STUDENT_ID.toString(),
                request
        );

        // assert
        assertNotNull(response);
        assertEquals(request.getStudentName(), response.getStudentName());
        assertEquals(request.getStudentAge(), response.getStudentAge());
        verify(studentRepository, times(1)).findById(STUDENT_ID);
        verify(studentRepository, times(1)).save(any(Student.class));
    }

    @Test
    public void testUpdateStudentFailure() {
        // arrange
        CreateStudentRequestDTO request = CreateStudentRequestDTO.builder()
                .studentName("Kremena")
                .studentGroupName("B1")
                .studentAge(25)
                .build();

        when(studentRepository.findById(STUDENT_ID)).thenReturn(Optional.empty());

        // act
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            studentService.updateStudent(STUDENT_ID.toString(), request);
        });

        // assert
        assertEquals(String.format("Student with ID %s does not exists", STUDENT_ID), exception.getMessage());
        verify(studentRepository, times(1)).findById(STUDENT_ID);
        verify(studentRepository, never()).save(any(Student.class));
    }

    @Test
    public void testDeleteStudentSuccess() {
        // arrange
        Student student = new Student(STUDENT_ID, "Kremena", 22, "B1");

        when(studentRepository.findById(STUDENT_ID)).thenReturn(Optional.of(student));

        // Act
        studentService.deleteStudent(STUDENT_ID.toString());

        // Assert
        verify(studentRepository, times(1)).findById(STUDENT_ID);
        verify(studentRepository, times(1)).delete(student);
    }


    @Test
    public void testDeleteStudentNotFound() {
        // Arrange
        when(studentRepository.findById(STUDENT_ID)).thenReturn(Optional.empty());

        // Act
        EntityNotFoundException thrown = assertThrows(EntityNotFoundException.class, () -> {
            studentService.deleteStudent(STUDENT_ID.toString());
        });

        // assert
        assertEquals(String.format("Student with ID %s does not exists", STUDENT_ID), thrown.getMessage());
        verify(studentRepository, times(1)).findById(STUDENT_ID);
        verify(studentRepository, never()).delete(any(Student.class));
    }

    @Test
    public void testEnrollStudentSuccess() {
        // Arrange
        EnrollStudentRequestDTO request = EnrollStudentRequestDTO.builder()
                .studentId(STUDENT_ID.toString())
                .courseName(COURSE_NAME_MATHEMATICS)
                .courseType(CourseType.MAIN.name())
                .build();
        Student savedStudent = new Student(
                STUDENT_ID,
                STUDENT_NAME_1,
                22,
                GROUP_NAME_1
        );
        Course course = new Course(COURSE_ID, COURSE_NAME_MATHEMATICS, CourseType.MAIN);
        Student updatedStudent = new Student(
                STUDENT_ID,
                STUDENT_NAME_1,
                22,
                GROUP_NAME_1,
                Set.of(course)
        );

        when(studentRepository.findById(STUDENT_ID)).thenReturn(Optional.of(savedStudent));
        when(courseRepository.findByName(COURSE_NAME_MATHEMATICS)).thenReturn(Optional.of(course));
        when(studentRepository.save(any(Student.class))).thenReturn(updatedStudent);

        // Act
        StudentResponseDTO response = studentService.enrollToCourse(request);

        // assert
        assertNotNull(response);
        assertEquals(STUDENT_NAME_1, response.getStudentName());
        assertEquals(22, response.getStudentAge());
        assertEquals(GROUP_NAME_1, response.getStudentGroupName());
        assertEquals(COURSE_NAME_MATHEMATICS, response.getStudentCourses().iterator().next().getCourseName());
        assertEquals(CourseType.MAIN.name(), response.getStudentCourses().iterator().next().getCourseTypeName());
        verify(studentRepository, times(1)).findById(STUDENT_ID);
        verify(courseRepository, times(1)).findByName(COURSE_NAME_MATHEMATICS);
        verify(studentRepository, times(1)).save(any(Student.class));
    }

    @Test
    public void testEnrollStudentStudentDoesNotExist() {
        // Arrange
        EnrollStudentRequestDTO request = EnrollStudentRequestDTO.builder()
                .studentId(STUDENT_ID.toString())
                .courseName(COURSE_NAME_MATHEMATICS)
                .courseType(CourseType.MAIN.name())
                .build();

        when(studentRepository.findById(STUDENT_ID)).thenReturn(Optional.empty());

        // act
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            studentService.enrollToCourse(request);
        });

        // assert
        assertEquals(String.format("Student with ID %s does not exists", STUDENT_ID), exception.getMessage());
        verify(studentRepository, times(1)).findById(STUDENT_ID);
        verify(courseRepository, never()).findByName(COURSE_NAME_MATHEMATICS);
        verify(studentRepository, never()).save(any(Student.class));
    }

    @Test
    public void testEnrollStudentStudentAlreadyEnrolled() {
        // Arrange
        EnrollStudentRequestDTO request = EnrollStudentRequestDTO.builder()
                .studentId(STUDENT_ID.toString())
                .courseName(COURSE_NAME_MATHEMATICS)
                .courseType(CourseType.MAIN.name())
                .build();

        Course course = new Course(COURSE_ID, COURSE_NAME_MATHEMATICS, CourseType.MAIN);
        Student savedStudent = new Student(
                STUDENT_ID,
                STUDENT_NAME_1,
                22,
                GROUP_NAME_1,
                Set.of(course)
        );

        when(studentRepository.findById(STUDENT_ID)).thenReturn(Optional.of(savedStudent));

        // act
        UnprocessableEntityException exception = assertThrows(UnprocessableEntityException.class, () -> {
            studentService.enrollToCourse(request);
        });

        // assert
        assertEquals(String.format("Student with ID %s already enrolled for course with name: %s", STUDENT_ID, COURSE_NAME_MATHEMATICS), exception.getMessage());
        verify(studentRepository, times(1)).findById(STUDENT_ID);
        verify(courseRepository, never()).findByName(COURSE_NAME_MATHEMATICS);
        verify(studentRepository, never()).save(any(Student.class));
    }

    @Test
    public void testLeaveStudentFromCourseSuccess() {
        // Arrange
        LeaveStudentCourseRequestDTO request = LeaveStudentCourseRequestDTO.builder()
                .studentId(STUDENT_ID.toString())
                .courseName(COURSE_NAME_MATHEMATICS)
                .build();

        Course course = new Course(COURSE_ID, COURSE_NAME_MATHEMATICS, CourseType.MAIN);
        Student savedStudent = new Student(
                STUDENT_ID,
                STUDENT_NAME_1,
                22,
                GROUP_NAME_1,
                Set.of(course)
        );
        Student updatedStudent = new Student(
                STUDENT_ID,
                STUDENT_NAME_1,
                22,
                GROUP_NAME_1
        );

        when(studentRepository.findById(STUDENT_ID)).thenReturn(Optional.of(savedStudent));
        when(courseRepository.findByName(COURSE_NAME_MATHEMATICS)).thenReturn(Optional.of(course));
        when(studentRepository.save(any(Student.class))).thenReturn(updatedStudent);

        // Act
        StudentResponseDTO response = studentService.removeStudentFromCourse(request);

        // assert
        assertNotNull(response);
        assertEquals(STUDENT_NAME_1, response.getStudentName());
        assertEquals(22, response.getStudentAge());
        assertEquals(GROUP_NAME_1, response.getStudentGroupName());
        assertTrue(response.getStudentCourses().isEmpty());
        verify(studentRepository, times(1)).findById(STUDENT_ID);
        verify(courseRepository, times(1)).findByName(COURSE_NAME_MATHEMATICS);
        verify(studentRepository, times(1)).save(any(Student.class));
    }

    @Test
    public void testLeaveStudentFromCourseStudentDoesNotExist() {
        // Arrange
        LeaveStudentCourseRequestDTO request = LeaveStudentCourseRequestDTO.builder()
                .studentId(STUDENT_ID.toString())
                .courseName(COURSE_NAME_MATHEMATICS)
                .build();

        when(studentRepository.findById(STUDENT_ID)).thenReturn(Optional.empty());

        // act
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            studentService.removeStudentFromCourse(request);
        });

        // assert
        assertEquals(String.format("Student with ID %s does not exists", STUDENT_ID), exception.getMessage());
        verify(studentRepository, times(1)).findById(STUDENT_ID);
        verify(courseRepository, never()).findByName(COURSE_NAME_MATHEMATICS);
        verify(studentRepository, never()).save(any(Student.class));
    }

    @Test
    public void testLeaveStudentFromCourseStudentNotEnrolled() {
        // Arrange
        LeaveStudentCourseRequestDTO request = LeaveStudentCourseRequestDTO.builder()
                .studentId(STUDENT_ID.toString())
                .courseName(COURSE_NAME_MATHEMATICS)
                .build();

        Student savedStudent = new Student(
                STUDENT_ID,
                STUDENT_NAME_1,
                22,
                GROUP_NAME_1
        );

        when(studentRepository.findById(STUDENT_ID)).thenReturn(Optional.of(savedStudent));

        // Act
        UnprocessableEntityException exception = assertThrows(UnprocessableEntityException.class, () -> {
            studentService.removeStudentFromCourse(request);
        });

        // assert
        assertEquals(String.format("Student with ID %s is not enrolled for course with name: %s", STUDENT_ID, request.getCourseName()), exception.getMessage());
        verify(studentRepository, times(1)).findById(STUDENT_ID);
        verify(courseRepository, never()).findByName(COURSE_NAME_MATHEMATICS);
        verify(studentRepository, never()).save(any(Student.class));
    }
}
