package com.classroom.web.rest;

import com.classroom.dto.report.StudentAndTeacherReportResponseDTO;
import com.classroom.dto.student.StudentResponseDTO;
import com.classroom.entity.Course;
import com.classroom.entity.Student;
import com.classroom.entity.Teacher;
import com.classroom.enumartion.CourseType;
import com.classroom.repository.CourseRepository;
import com.classroom.repository.StudentRepository;
import com.classroom.repository.TeacherRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
@Transactional
public class ReportResourceTest {

    private static final String STUDENT_NAME_1 = "Mariya";
    private static final String STUDENT_NAME_2 = "Ivan";
    private static final String STUDENT_NAME_3 = "Georgi";
    private static final String TEACHER_NAME_1 = "Kiril";
    private static final String TEACHER_NAME_2 = "Petko";
    private static final String TEACHER_NAME_3 = "Rumyana";
    private static final String COURSE_NAME_MATHEMATICS = "Mathematics";
    private static final String COURSE_NAME_HISTORY = "History";
    private static final String COURSE_NAME_SCIENCE = "Science";
    private static final String GROUP_NAME_1 = "A1";
    private static final String GROUP_NAME_2 = "A2";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        courseRepository.deleteAll();
        studentRepository.deleteAll();
        teacherRepository.deleteAll();
        setUpTestData();
    }

    @Test
    void testCourseByTypeCountSuccess() throws Exception {
        // arrange
        String courseTypeRequest = CourseType.MAIN.toString();

        // act
        MvcResult mvcResult = mockMvc.perform(
                        get("/api/v1/reports/courses/count/{courseType}", courseTypeRequest))
                .andExpect(status().isOk())
                .andReturn();

        // assert
        Long courseCount = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                new TypeReference<Long>() {
                });

        assertEquals(1, courseCount);
    }

    @Test
    void testStudentCountSuccess() throws Exception {
        // arrange

        // act
        MvcResult mvcResult = mockMvc.perform(
                        get("/api/v1/reports/students/count"))
                .andExpect(status().isOk())
                .andReturn();

        // assert
        Long studentCount = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                new TypeReference<Long>() {
                });

        assertEquals(3, studentCount);
    }

    @Test
    void testTeacherCountSuccess() throws Exception {
        // arrange

        // act
        MvcResult mvcResult = mockMvc.perform(
                        get("/api/v1/reports/teachers/count"))
                .andExpect(status().isOk())
                .andReturn();

        // assert
        Long studentCount = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                new TypeReference<Long>() {
                });

        assertEquals(3, studentCount);
    }

    @Test
    void testFindAllStudentsByGroupSuccess() throws Exception {
        // arrange

        // act
        MvcResult mvcResult = mockMvc.perform(
                MockMvcRequestBuilders.get("/api/v1/reports/students/group/{groupName}", GROUP_NAME_1))
                .andExpect(status().isOk())
                .andReturn();

        // assert
        List<StudentResponseDTO> response = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                new TypeReference<List<StudentResponseDTO>>() {
                });

        assertEquals(2, response.size());

        assertEquals(GROUP_NAME_1, response.get(0).getStudentGroupName());
        assertEquals(GROUP_NAME_1, response.get(1).getStudentGroupName());
    }

    @Test
    void testFindAllStudentsByCourseSuccess() throws Exception {
        // arrange

        // act
        MvcResult mvcResult = mockMvc.perform(
                        MockMvcRequestBuilders.get("/api/v1/reports/students/course/{courseName}", COURSE_NAME_MATHEMATICS))
                .andExpect(status().isOk())
                .andReturn();

        // assert
        List<StudentResponseDTO> response = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                new TypeReference<List<StudentResponseDTO>>() {
                });

        assertEquals(2, response.size());

        assertTrue(response.get(0).getStudentCourses().stream().anyMatch(course -> course.getCourseName().equals(COURSE_NAME_MATHEMATICS)));
        assertTrue(response.get(1).getStudentCourses().stream().anyMatch(course -> course.getCourseName().equals(COURSE_NAME_MATHEMATICS)));
    }

    @Test
    void testFindAllStudentsOlderThanAndInCourseSuccess() throws Exception {
        // arrange

        // act
        MvcResult mvcResult = mockMvc.perform(
                        MockMvcRequestBuilders.get("/api/v1/reports/students/course/{courseName}/age/{age}", COURSE_NAME_MATHEMATICS, 21))
                .andExpect(status().isOk())
                .andReturn();

        // assert
        List<StudentResponseDTO> response = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                new TypeReference<List<StudentResponseDTO>>() {
                });

        assertEquals(1, response.size());

        assertTrue(response.get(0).getStudentCourses().stream().allMatch(course -> course.getCourseName().equals(COURSE_NAME_MATHEMATICS)));
        assertTrue(response.get(0).getStudentAge() > 21);
    }

    @Test
    void testFindAllStudentsAndTeachersByCourseAndGroup() throws Exception {
        // arrange

        // act
        MvcResult mvcResult = mockMvc.perform(
                        MockMvcRequestBuilders.get("/api/v1/reports/courses/{courseName}/groups/{groupName}", COURSE_NAME_MATHEMATICS, GROUP_NAME_1))
                .andExpect(status().isOk())
                .andReturn();

        // assert
        StudentAndTeacherReportResponseDTO response = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                new TypeReference<StudentAndTeacherReportResponseDTO>() {
                });

        assertEquals(1, response.getStudents().size());
        assertEquals(1, response.getTeachers().size());

        assertEquals(GROUP_NAME_1, response.getStudents().iterator().next().getStudentGroupName());
        assertEquals(GROUP_NAME_1, response.getTeachers().iterator().next().getTeacherGroupName());

        assertTrue(response.getStudents().iterator().next().getStudentCourses().stream().anyMatch(course -> course.getCourseName().equals(COURSE_NAME_MATHEMATICS)));
        assertTrue(response.getTeachers().iterator().next().getTeacherCourses().stream().anyMatch(course -> course.getCourseName().equals(COURSE_NAME_MATHEMATICS)));
    }

    private void setUpTestData() {
        // Create Courses
        Course mainCourse = new Course(COURSE_NAME_MATHEMATICS, CourseType.MAIN);
        Course secondaryCourse = new Course(COURSE_NAME_HISTORY, CourseType.SECONDARY);
        courseRepository.save(mainCourse);
        courseRepository.save(secondaryCourse);

        // Create Students
        Student student1 = new Student(STUDENT_NAME_1, 20, GROUP_NAME_1, Set.of(mainCourse, secondaryCourse));
        Student student2 = new Student(STUDENT_NAME_2, 22, GROUP_NAME_1, Set.of(secondaryCourse));
        Student student3 = new Student(STUDENT_NAME_3, 24, GROUP_NAME_2, Set.of(mainCourse));

        studentRepository.save(student1);
        studentRepository.save(student2);
        studentRepository.save(student3);

        // Create Teachers
        Teacher teacher1 = new Teacher(TEACHER_NAME_1, 38, GROUP_NAME_1, Set.of(mainCourse, secondaryCourse));
        Teacher teacher2 = new Teacher(TEACHER_NAME_2, 46, GROUP_NAME_1, Set.of(secondaryCourse));
        Teacher teacher3 = new Teacher(TEACHER_NAME_3, 29, GROUP_NAME_2, Set.of(mainCourse));

        teacherRepository.save(teacher1);
        teacherRepository.save(teacher2);
        teacherRepository.save(teacher3);
    }
}
