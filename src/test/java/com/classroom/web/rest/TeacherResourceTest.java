package com.classroom.web.rest;

import com.classroom.dto.course.CourseResponseDTO;
import com.classroom.dto.teacher.CreateTeacherRequestDTO;
import com.classroom.dto.teacher.EnrollTeacherRequestDTO;
import com.classroom.dto.teacher.LeaveTeacherCourseRequestDTO;
import com.classroom.dto.teacher.TeacherResponseDTO;
import com.classroom.entity.Course;
import com.classroom.entity.Teacher;
import com.classroom.enumartion.CourseType;
import com.classroom.repository.CourseRepository;
import com.classroom.repository.TeacherRepository;
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
public class TeacherResourceTest {

    private static final String TEACHER_NAME_1 = "Kiril";
    private static final String TEACHER_NAME_2 = "Petko";
    private static final String TEACHER_NAME_3 = "Rumyana";

    private static final String COURSE_NAME_MATHEMATICS = "Mathematics";
    private static final String COURSE_NAME_HISTORY = "History";

    private static final String GROUP_NAME_1 = "A1";
    private static final String GROUP_NAME_2 = "A2";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        courseRepository.deleteAll();
        teacherRepository.deleteAll();
        setUpTestData();
    }

    @Test
    void createTeacherSuccess() throws Exception {
        // arrange
        CreateTeacherRequestDTO createTeacherRequest = CreateTeacherRequestDTO.builder()
                .teacherName("Dimitar")
                .teacherGroupName("B1")
                .teacherAge(45)
                .build();
        // act
        MvcResult mvcResult = mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/v1/teachers")
                                .content(objectMapper.writeValueAsString(createTeacherRequest))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();

        // assert
        TeacherResponseDTO teacher = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                new TypeReference<TeacherResponseDTO>() {
                });

        assertEquals(createTeacherRequest.getTeacherName(), teacher.getTeacherName());
        assertEquals(createTeacherRequest.getTeacherAge(), teacher.getTeacherAge());
        assertEquals(createTeacherRequest.getTeacherGroupName(), teacher.getTeacherGroupName());
    }

    @Test
    void createTeacherAlreadyExists() throws Exception {
        // arrange
        CreateTeacherRequestDTO createTeacherRequest = CreateTeacherRequestDTO.builder()
                .teacherName(TEACHER_NAME_1)
                .teacherGroupName(GROUP_NAME_1)
                .teacherAge(38)
                .build();

        // act
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/v1/teachers")
                                .content(objectMapper.writeValueAsString(createTeacherRequest))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        // assert
    }

    @Test
    void updateTeacherSuccess() throws Exception {
        // arrange
        Teacher teacher = teacherRepository.findByNameAndTeacherGroupAndAge(TEACHER_NAME_1, GROUP_NAME_1, 38).get();
        CreateTeacherRequestDTO updateTeacherRequest = CreateTeacherRequestDTO.builder()
                .teacherName(TEACHER_NAME_1)
                .teacherGroupName(GROUP_NAME_1)
                .teacherAge(39)
                .build();
        // act
        MvcResult mvcResult = mockMvc.perform(
                        MockMvcRequestBuilders.put("/api/v1/teachers/{id}", teacher.getId())
                                .content(objectMapper.writeValueAsString(updateTeacherRequest))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        // assert
        TeacherResponseDTO updatedTeacher = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                new TypeReference<TeacherResponseDTO>() {
                });

        assertEquals(updateTeacherRequest.getTeacherName(), updatedTeacher.getTeacherName());
        assertEquals(updateTeacherRequest.getTeacherAge(), updatedTeacher.getTeacherAge());
        assertEquals(updateTeacherRequest.getTeacherGroupName(), updatedTeacher.getTeacherGroupName());
    }

    @Test
    void updateTeacherFailure() throws Exception {
        // arrange
        Teacher teacher = teacherRepository.findByNameAndTeacherGroupAndAge(TEACHER_NAME_1, GROUP_NAME_1, 38).get();
        CreateTeacherRequestDTO updateTeacherRequest = CreateTeacherRequestDTO.builder()
                .teacherName(TEACHER_NAME_1)
                .teacherGroupName(GROUP_NAME_1)
                .teacherAge(33)
                .build();
        // act
        mockMvc.perform(
                        MockMvcRequestBuilders.put("/api/v1/teachers/{id}", UUID.randomUUID().toString())
                                .content(objectMapper.writeValueAsString(updateTeacherRequest))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        // assert
    }

    @Test
    void deleteTeacherSuccess() throws Exception {
        // arrange
        Teacher teacher = teacherRepository.findByNameAndTeacherGroupAndAge(TEACHER_NAME_1, GROUP_NAME_1, 38).get();

        // act
        mockMvc.perform(
                        MockMvcRequestBuilders.delete("/api/v1/teachers/{id}", teacher.getId())
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        // assert
    }

    @Test
    void deleteTeacherFailure() throws Exception {
        // arrange

        // act
        mockMvc.perform(
                        MockMvcRequestBuilders.delete("/api/v1/teachers/{id}", UUID.randomUUID().toString())
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        // assert
    }

    @Test
    void enrollTeacherForCourseTestSuccess() throws Exception {
        // arrange
        Teacher teacher = teacherRepository.findByNameAndTeacherGroupAndAge(TEACHER_NAME_1, GROUP_NAME_1, 38).get();
        EnrollTeacherRequestDTO enrollTeacherRequest = EnrollTeacherRequestDTO.builder()
                .teacherId(teacher.getId().toString())
                .courseName(COURSE_NAME_MATHEMATICS)
                .courseType(CourseType.MAIN.name())
                .build();
        // act
        MvcResult mvcResult = mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/v1/teachers/enroll")
                                .content(objectMapper.writeValueAsString(enrollTeacherRequest))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        // assert
        TeacherResponseDTO enrolledTeacher = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                new TypeReference<TeacherResponseDTO>() {
                });

        assertEquals(enrollTeacherRequest.getTeacherId(), enrolledTeacher.getTeacherId());
        assertTrue( enrolledTeacher.getTeacherCourses().stream().map(CourseResponseDTO::getCourseName)
                .toList().contains(enrollTeacherRequest.getCourseName()));
        assertTrue( enrolledTeacher.getTeacherCourses().stream().map(CourseResponseDTO::getCourseTypeName)
                .toList().contains(enrollTeacherRequest.getCourseType()));
    }

    @Test
    void leaveTeacherFromCourseTestSuccess() throws Exception {
        // arrange
        Course course = courseRepository.findByName(COURSE_NAME_MATHEMATICS).get();
        Teacher teacher = teacherRepository.findByNameAndTeacherGroupAndAge(TEACHER_NAME_1, GROUP_NAME_1, 38).get();
        teacher.getCourses().add(course);
        course.getTeachers().add(teacher);
        teacherRepository.save(teacher);

        LeaveTeacherCourseRequestDTO leaveTeacherCourseRequest = LeaveTeacherCourseRequestDTO.builder()
                .teacherId(teacher.getId().toString())
                .courseName(COURSE_NAME_MATHEMATICS)
                .build();
        // act
        MvcResult mvcResult = mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/v1/teachers/leave")
                                .content(objectMapper.writeValueAsString(leaveTeacherCourseRequest))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        // assert
        TeacherResponseDTO teacherResponse = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                new TypeReference<TeacherResponseDTO>() {
                });

        assertEquals(leaveTeacherCourseRequest.getTeacherId(), teacherResponse.getTeacherId());
        assertTrue(teacherResponse.getTeacherCourses().isEmpty());
    }

    private void setUpTestData() {
        // Create Courses
        Course mainCourse = new Course(COURSE_NAME_MATHEMATICS, CourseType.MAIN);
        Course secondaryCourse = new Course(COURSE_NAME_HISTORY, CourseType.SECONDARY);
        courseRepository.save(mainCourse);
        courseRepository.save(secondaryCourse);

        // Create Teachers
        Teacher teacher1 = new Teacher(TEACHER_NAME_1, 38, GROUP_NAME_1);
        Teacher teacher2 = new Teacher(TEACHER_NAME_2, 46, GROUP_NAME_1);
        Teacher teacher3 = new Teacher(TEACHER_NAME_3, 29, GROUP_NAME_2);

        teacherRepository.save(teacher1);
        teacherRepository.save(teacher2);
        teacherRepository.save(teacher3);
    }
}
