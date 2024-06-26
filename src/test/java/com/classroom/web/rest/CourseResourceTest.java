package com.classroom.web.rest;

import com.classroom.dto.course.CourseResponseDTO;
import com.classroom.dto.course.CreateCourseRequestDTO;
import com.classroom.entity.Course;
import com.classroom.enumartion.CourseType;
import com.classroom.repository.CourseRepository;
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

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
@Transactional
public class CourseResourceTest {

    private static final String COURSE_NAME_MATHEMATICS = "Mathematics";
    private static final String COURSE_NAME_HISTORY = "History";

    private static final String COURSE_NAME_SCIENCE = "Science";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        courseRepository.deleteAll();
        setUpTestData();
    }


    @Test
    void testGetAllCourses() throws Exception {
        // arrange

        // act
        MvcResult mvcResult = mockMvc.perform(
                get("/api/v1/courses"))
                .andExpect(status().isOk())
                .andReturn();

        // assert
        List<CourseResponseDTO> courses = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                new TypeReference<List<CourseResponseDTO>>() {
                });

        assertEquals(3, courses.size());
    }

    @Test
    void testGetCourseByNameReturnsValue() throws Exception {
        // arrange

        // act
        MvcResult mvcResult = mockMvc.perform(
                        MockMvcRequestBuilders.get("/api/v1/courses/{courseName}", COURSE_NAME_HISTORY))
                .andExpect(status().isOk())
                .andReturn();

        // assert
        CourseResponseDTO course = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                new TypeReference<CourseResponseDTO>() {
                });

        assertEquals(COURSE_NAME_HISTORY, course.getCourseName());
    }

    @Test
    void testGetCourseByNameReturnsNotFound() throws Exception {
        // arrange

        // act
        mockMvc.perform(
                        MockMvcRequestBuilders.get("/api/v1/courses/{courseName}", "Physics"))
                .andExpect(status().isNotFound());
        // assert
    }

    @Test
    void createCourseSuccess() throws Exception {
        // arrange
        CreateCourseRequestDTO createCourseRequest = CreateCourseRequestDTO.builder()
                        .courseName("Physics")
                        .courseTypeName("MAIN")
                        .build();
        // act
        MvcResult mvcResult = mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/v1/courses")
                                .content(objectMapper.writeValueAsString(createCourseRequest))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();

        // assert
        CourseResponseDTO course = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                new TypeReference<CourseResponseDTO>() {
                });

        assertEquals(createCourseRequest.getCourseName(), course.getCourseName());
        assertEquals(createCourseRequest.getCourseTypeName(), course.getCourseTypeName());
    }

    @Test
    void createCourseAlreadyExists() throws Exception {
        // arrange
        CreateCourseRequestDTO createCourseRequest = CreateCourseRequestDTO.builder()
                .courseName(COURSE_NAME_HISTORY)
                .courseTypeName("MAIN")
                .build();
        // act
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/v1/courses")
                                .content(objectMapper.writeValueAsString(createCourseRequest))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        // assert
    }

    private void setUpTestData() {
        Course course1 = new Course(COURSE_NAME_MATHEMATICS, CourseType.MAIN);
        Course course2 = new Course(COURSE_NAME_HISTORY, CourseType.SECONDARY);
        Course course3 = new Course(COURSE_NAME_SCIENCE, CourseType.MAIN);

        courseRepository.save(course1);
        courseRepository.save(course2);
        courseRepository.save(course3);
    }
}
