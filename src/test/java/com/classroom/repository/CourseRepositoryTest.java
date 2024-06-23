package com.classroom.repository;

import com.classroom.entity.Course;
import com.classroom.enumartion.CourseType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@DataJpaTest
public class CourseRepositoryTest {

    private static final String COURSE_NAME_MATHEMATICS = "Mathematics";
    private static final String COURSE_NAME_HISTORY = "History";

    private static final String COURSE_NAME_SCIENCE = "Science";

    @Autowired
    private CourseRepository courseRepository;

    @BeforeEach
    void setUp() {
        Course course1 = Course.builder()
                        .name(COURSE_NAME_MATHEMATICS)
                        .type(CourseType.MAIN)
                        .build();
        Course course2 = Course.builder()
                .name(COURSE_NAME_HISTORY)
                .type(CourseType.SECONDARY)
                .build();
        Course course3 = Course.builder()
                .name(COURSE_NAME_SCIENCE)
                .type(CourseType.MAIN)
                .build();

        courseRepository.save(course1);
        courseRepository.save(course2);
        courseRepository.save(course3);
    }

    @Test
    public void whenCountCoursesByTypeThenReturnCorrectCount() {
        long mainCount = courseRepository.countCoursesByType(CourseType.MAIN);
        long secondaryCount = courseRepository.countCoursesByType(CourseType.SECONDARY);

        assertEquals(2, mainCount);
        assertEquals(1, secondaryCount);
    }

    @Test
    public void whenFindByNameThenReturnExistingCourse() {
        Optional<Course> course = courseRepository.findByName(COURSE_NAME_HISTORY);

        assertTrue(course.isPresent());
    }

    @Test
    public void whenFindByNameWithNonExistingNameReturnEmpty() {
        Optional<Course> course = courseRepository.findByName("Geography");

        assertTrue(course.isEmpty());
    }

    @Test
    public void whenExistsByNameThenReturnTrueForExistingCourse() {
        boolean exists = courseRepository.existsByName(COURSE_NAME_MATHEMATICS);

        assertTrue(exists);
    }

    @Test
    public void whenExistsByNameThenReturnFalseForNonExistingCourse() {
        boolean exists = courseRepository.existsByName("Geography");

        assertFalse(exists);
    }
}
