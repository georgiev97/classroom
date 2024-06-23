package com.classroom.web.rest;

import com.classroom.dto.course.CourseResponseDTO;
import com.classroom.dto.student.CreateStudentRequestDTO;
import com.classroom.dto.student.EnrollStudentRequestDTO;
import com.classroom.dto.student.LeaveStudentCourseRequestDTO;
import com.classroom.dto.student.StudentResponseDTO;
import com.classroom.entity.Course;
import com.classroom.entity.Student;
import com.classroom.enumartion.CourseType;
import com.classroom.repository.CourseRepository;
import com.classroom.repository.StudentRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
@Transactional
public class StudentResourceTest {

    private static final String STUDENT_NAME_1 = "Mariya";
    private static final String STUDENT_NAME_2 = "Ivan";
    private static final String STUDENT_NAME_3 = "Georgi";
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
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        courseRepository.deleteAll();
        studentRepository.deleteAll();
        setUpTestData();
    }

    @Test
    void createStudentSuccess() throws Exception {
        // arrange
        CreateStudentRequestDTO createStudentRequest = CreateStudentRequestDTO.builder()
                .studentName("Kremena")
                .studentGroupName("B1")
                .studentAge(25)
                .build();
        // act
        MvcResult mvcResult = mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/v1/students")
                                .content(objectMapper.writeValueAsString(createStudentRequest))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();

        // assert
        StudentResponseDTO student = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                new TypeReference<StudentResponseDTO>() {
                });

        assertEquals(createStudentRequest.getStudentName(), student.getStudentName());
        assertEquals(createStudentRequest.getStudentAge(), student.getStudentAge());
        assertEquals(createStudentRequest.getStudentGroupName(), student.getStudentGroupName());
    }

    @Test
    void createStudentAlreadyExists() throws Exception {
        // arrange
        CreateStudentRequestDTO createStudentRequest = CreateStudentRequestDTO.builder()
                .studentName(STUDENT_NAME_2)
                .studentGroupName(GROUP_NAME_1)
                .studentAge(22)
                .build();
        // act
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/v1/students")
                                .content(objectMapper.writeValueAsString(createStudentRequest))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        // assert
    }

    @Test
    void updateStudentSuccess() throws Exception {
        // arrange
        Student student = studentRepository.findByNameAndStudentGroupAndAge(STUDENT_NAME_1, GROUP_NAME_1, 20).get();
        CreateStudentRequestDTO updateStudentRequest = CreateStudentRequestDTO.builder()
                .studentName(STUDENT_NAME_1)
                .studentGroupName(GROUP_NAME_1)
                .studentAge(21)
                .build();
        // act
        MvcResult mvcResult = mockMvc.perform(
                        MockMvcRequestBuilders.put("/api/v1/students/{id}", student.getId())
                                .content(objectMapper.writeValueAsString(updateStudentRequest))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        // assert
        StudentResponseDTO updateStudent = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                new TypeReference<StudentResponseDTO>() {
                });

        assertEquals(updateStudentRequest.getStudentName(), updateStudent.getStudentName());
        assertEquals(updateStudentRequest.getStudentAge(), updateStudent.getStudentAge());
        assertEquals(updateStudentRequest.getStudentGroupName(), updateStudent.getStudentGroupName());
    }

    @Test
    void updateStudentFailure() throws Exception {
        // arrange
        CreateStudentRequestDTO updateStudentRequest = CreateStudentRequestDTO.builder()
                .studentName(STUDENT_NAME_1)
                .studentGroupName(GROUP_NAME_1)
                .studentAge(21)
                .build();
        // act
        mockMvc.perform(
                        MockMvcRequestBuilders.put("/api/v1/students/{id}", UUID.randomUUID().toString())
                                .content(objectMapper.writeValueAsString(updateStudentRequest))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        // assert
    }

    @Test
    void deleteStudentSuccess() throws Exception {
        // arrange
        Student student = studentRepository.findByNameAndStudentGroupAndAge(STUDENT_NAME_1, GROUP_NAME_1, 20).get();

        // act
        mockMvc.perform(
                        MockMvcRequestBuilders.delete("/api/v1/students/{id}", student.getId())
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        // assert
    }

    @Test
    void deleteStudentFailure() throws Exception {
        // arrange

        // act
        mockMvc.perform(
                        MockMvcRequestBuilders.delete("/api/v1/students/{id}", UUID.randomUUID().toString())
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        // assert
    }

    @Test
    void enrollStudentForCourseTestSuccess() throws Exception {
        // arrange
        Student student = studentRepository.findByNameAndStudentGroupAndAge(STUDENT_NAME_1, GROUP_NAME_1, 20).get();
        EnrollStudentRequestDTO enrollStudentRequest = EnrollStudentRequestDTO.builder()
                .studentId(student.getId().toString())
                .courseName(COURSE_NAME_MATHEMATICS)
                .courseType(CourseType.MAIN.name())
                .build();
        // act
        MvcResult mvcResult = mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/v1/students/enroll")
                                .content(objectMapper.writeValueAsString(enrollStudentRequest))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        // assert
        StudentResponseDTO enrolledStudent = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                new TypeReference<StudentResponseDTO>() {
                });

        assertEquals(enrollStudentRequest.getStudentId(), enrolledStudent.getStudentId());
        assertTrue( enrolledStudent.getStudentCourses().stream().map(CourseResponseDTO::getCourseName)
                .toList().contains(enrollStudentRequest.getCourseName()));
        assertTrue( enrolledStudent.getStudentCourses().stream().map(CourseResponseDTO::getCourseTypeName)
                .toList().contains(enrollStudentRequest.getCourseType()));
    }

    @Test
    void leaveStudentFromCourseTestSuccess() throws Exception {
        // arrange
        Course course = courseRepository.findByName(COURSE_NAME_MATHEMATICS).get();
        Student student = studentRepository.findByNameAndStudentGroupAndAge(STUDENT_NAME_1, GROUP_NAME_1, 20).get();
        student.getCourses().add(course);
        course.getStudents().add(student);
        studentRepository.save(student);

        LeaveStudentCourseRequestDTO leaveStudentCourseRequest = LeaveStudentCourseRequestDTO.builder()
                .studentId(student.getId().toString())
                .courseName(COURSE_NAME_MATHEMATICS)
                .build();
        // act
        MvcResult mvcResult = mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/v1/students/leave")
                                .content(objectMapper.writeValueAsString(leaveStudentCourseRequest))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        // assert
        StudentResponseDTO studentResponse = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                new TypeReference<StudentResponseDTO>() {
                });

        assertEquals(leaveStudentCourseRequest.getStudentId(), studentResponse.getStudentId());
        assertTrue(studentResponse.getStudentCourses().isEmpty());
    }

    private void setUpTestData() {
        // Create Courses
        Course mainCourse = new Course(COURSE_NAME_MATHEMATICS, CourseType.MAIN);
        Course secondaryCourse = new Course(COURSE_NAME_HISTORY, CourseType.SECONDARY);
        courseRepository.save(mainCourse);
        courseRepository.save(secondaryCourse);

        // Create Students
        Student student1 = new Student(STUDENT_NAME_1, 20, GROUP_NAME_1);
        Student student2 = new Student(STUDENT_NAME_2, 22, GROUP_NAME_1);
        Student student3 = new Student(STUDENT_NAME_3, 24, GROUP_NAME_2);

        studentRepository.save(student1);
        studentRepository.save(student2);
        studentRepository.save(student3);
    }
}
