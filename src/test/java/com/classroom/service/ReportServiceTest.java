package com.classroom.service;

import com.classroom.dto.report.StudentAndTeacherReportResponseDTO;
import com.classroom.dto.student.StudentResponseDTO;
import com.classroom.entity.Course;
import com.classroom.entity.Student;
import com.classroom.entity.Teacher;
import com.classroom.enumartion.CourseType;
import com.classroom.repository.CourseRepository;
import com.classroom.repository.StudentRepository;
import com.classroom.repository.TeacherRepository;
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
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ReportServiceTest {

    private static final UUID STUDENT_ID = UUID.randomUUID();
    private static final String STUDENT_NAME_1 = "Mariya";
    private static final String STUDENT_NAME_2 = "Ivan";
    private static final String STUDENT_NAME_3 = "Georgi";
    private static final UUID TEACHER_ID = UUID.randomUUID();
    private static final String TEACHER_NAME_1 = "Kiril";
    private static final String TEACHER_NAME_2 = "Petko";
    private static final String TEACHER_NAME_3 = "Rumyana";
    private static final UUID COURSE_ID = UUID.randomUUID();
    private static final String COURSE_NAME_MATHEMATICS = "Mathematics";
    private static final String COURSE_NAME_HISTORY = "History";
    private static final String COURSE_NAME_SCIENCE = "Science";
    private static final String GROUP_NAME_1 = "A1";
    private static final String GROUP_NAME_2 = "A2";

    private ReportService reportService;

    @Mock
    private CourseRepository courseRepository;
    @Mock
    private StudentRepository studentRepository;
    @Mock
    private TeacherRepository teacherRepository;

    @BeforeEach
    public void setUp() {
        reportService = new ReportService(courseRepository,studentRepository, teacherRepository);
    }


    @Test
    public void testCountCoursesByTypeReturnsValue() {
        // arrange

        when(courseRepository.countCoursesByType(CourseType.MAIN)).thenReturn(2L);

        // act
        long result = reportService.countCoursesByType("main");

        // assert
        assertEquals(2L, result);
    }

    @Test
    public void testStudentCountReturnsValue() {
        // arrange
        when(studentRepository.countStudents()).thenReturn(2L);

        // act
        long result = reportService.countStudents();

        // assert
        assertEquals(2L, result);
    }

    @Test
    public void testTeacherCountReturnsValue() {
        // arrange
        when(teacherRepository.countTeachers()).thenReturn(2L);

        // act
        long result = reportService.countTeachers();

        // assert
        assertEquals(2L, result);
    }

    @Test
    public void testGetStudentsByCourseSuccess() {
        // arrange
        Course course = new Course(
                COURSE_ID,
                COURSE_NAME_MATHEMATICS,
                CourseType.MAIN
        );
        Student student = new Student(
                STUDENT_ID,
                STUDENT_NAME_1,
                22,
                GROUP_NAME_1,
                Set.of(course)
        );

        when(courseRepository.findByName(COURSE_NAME_MATHEMATICS)).thenReturn(Optional.of(course));
        when(studentRepository.findStudentsByCourse(COURSE_NAME_MATHEMATICS)).thenReturn(Set.of(student));

        // act
        Set<StudentResponseDTO> response = reportService.getStudentsByCourse(COURSE_NAME_MATHEMATICS);

        // assert
        assertNotNull(response);
        assertEquals(1, response.size());
        assertEquals(COURSE_NAME_MATHEMATICS, response.iterator().next().getStudentCourses().iterator().next().getCourseName());
        verify(studentRepository, times(1)).findStudentsByCourse(COURSE_NAME_MATHEMATICS);
    }

    @Test
    void testGetStudentsByGroupSuccess() {
        // arrange
        Course course = new Course(
                COURSE_ID,
                COURSE_NAME_MATHEMATICS,
                CourseType.MAIN
        );
        Student student = new Student(
                STUDENT_ID,
                STUDENT_NAME_1,
                22,
                GROUP_NAME_1,
                Set.of(course)
        );

        when(studentRepository.findStudentsByGroup(GROUP_NAME_1)).thenReturn(Set.of(student));

        // act
        Set<StudentResponseDTO> response = reportService.getStudentsByGroup(GROUP_NAME_1);

        // assert
        assertNotNull(response);
        assertEquals(1, response.size());
        assertEquals(GROUP_NAME_1, response.iterator().next().getStudentGroupName());
        verify(studentRepository, times(1)).findStudentsByGroup(GROUP_NAME_1);
    }

    @Test
    public void testGetStudentsByCourseAndOlderThanAge() {
        // arrange
        Course course = new Course(
                COURSE_ID,
                COURSE_NAME_MATHEMATICS,
                CourseType.MAIN
        );
        Student student1 = new Student(
                STUDENT_ID,
                STUDENT_NAME_1,
                22,
                GROUP_NAME_1,
                Set.of(course)
        );

        when(courseRepository.findByName(COURSE_NAME_MATHEMATICS)).thenReturn(Optional.of(course));
        when(studentRepository.findStudentsOlderThanAgeInCourse(21, COURSE_NAME_MATHEMATICS)).thenReturn(Set.of(student1));

        // act
        Set<StudentResponseDTO> response = reportService.getStudentsByAgeAndCourse(21, COURSE_NAME_MATHEMATICS);

        // assert
        assertNotNull(response);
        assertEquals(1, response.size());
        assertEquals(22, response.iterator().next().getStudentAge());
        assertEquals(COURSE_NAME_MATHEMATICS, response.iterator().next().getStudentCourses().iterator().next().getCourseName());
        verify(studentRepository, times(1)).findStudentsOlderThanAgeInCourse(21, COURSE_NAME_MATHEMATICS);
    }

    @Test
    public void testGetStudentsAndTeachersByCourseAndGroup() {
        // arrange
        Course course = new Course(
                COURSE_ID,
                COURSE_NAME_MATHEMATICS,
                CourseType.MAIN
        );
        Student student = new Student(
                STUDENT_ID,
                STUDENT_NAME_1,
                22,
                GROUP_NAME_1,
                Set.of(course)
        );
        Teacher teacher = new Teacher(
                TEACHER_ID,
                TEACHER_NAME_1,
                43,
                GROUP_NAME_1,
                Set.of(course)
        );

        when(courseRepository.findByName(COURSE_NAME_MATHEMATICS)).thenReturn(Optional.of(course));
        when(studentRepository.findStudentsByCourseAndGroup(COURSE_NAME_MATHEMATICS, GROUP_NAME_1)).thenReturn(Set.of(student));
        when(teacherRepository.findTeachersByCourseAndGroup(COURSE_NAME_MATHEMATICS, GROUP_NAME_1)).thenReturn(Set.of(teacher));

        // act
        StudentAndTeacherReportResponseDTO response = reportService.getStudentsAndTeachersByCourseAndGroup(COURSE_NAME_MATHEMATICS, GROUP_NAME_1);

        // assert
        assertNotNull(response);
        assertEquals(1, response.getStudents().size());
        assertEquals(1, response.getTeachers().size());
        assertEquals(COURSE_NAME_MATHEMATICS, response.getStudents().iterator().next().getStudentCourses().iterator().next().getCourseName());
        assertEquals(GROUP_NAME_1, response.getStudents().iterator().next().getStudentGroupName());
        assertEquals(COURSE_NAME_MATHEMATICS, response.getTeachers().iterator().next().getTeacherCourses().iterator().next().getCourseName());
        assertEquals(GROUP_NAME_1, response.getTeachers().iterator().next().getTeacherGroupName());
        verify(courseRepository, times(1)).findByName(COURSE_NAME_MATHEMATICS);
        verify(studentRepository, times(1)).findStudentsByCourseAndGroup(COURSE_NAME_MATHEMATICS, GROUP_NAME_1);
        verify(teacherRepository, times(1)).findTeachersByCourseAndGroup(COURSE_NAME_MATHEMATICS, GROUP_NAME_1);

    }

}
