package com.classroom.repository;

import com.classroom.entity.Course;
import com.classroom.entity.Teacher;
import com.classroom.enumartion.CourseType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@DataJpaTest
public class TeacherRepositoryTest {

    private static final String TEACHER_NAME_1 = "Kiril";
    private static final String TEACHER_NAME_2 = "Petko";
    private static final String TEACHER_NAME_3 = "Rumyana";

    private static final String COURSE_NAME_MATHEMATICS = "Mathematics";
    private static final String COURSE_NAME_HISTORY = "History";

    private static final String GROUP_NAME_1 = "A1";
    private static final String GROUP_NAME_2 = "A2";

    @Autowired
    private TeacherRepository teacherRepository;

    @BeforeEach
    void setUp() {
        // Create Courses
        Course mainCourse = new Course(COURSE_NAME_MATHEMATICS, CourseType.MAIN);
        Course secondaryCourse = new Course(COURSE_NAME_HISTORY, CourseType.SECONDARY);

        // Create Teachers
        Teacher teacher1 = new Teacher(TEACHER_NAME_1, 38, GROUP_NAME_1, Set.of(mainCourse));
        Teacher teacher2 = new Teacher(TEACHER_NAME_2, 46, GROUP_NAME_1, Set.of(secondaryCourse));
        Teacher teacher3 = new Teacher(TEACHER_NAME_3, 29, GROUP_NAME_2, Set.of(mainCourse, secondaryCourse));

        teacherRepository.save(teacher1);
        teacherRepository.save(teacher2);
        teacherRepository.save(teacher3);
    }

    @Test
    public void whenCountTeachersThenReturnCorrectCount() {
        long count = teacherRepository.teachersCount();
        assertEquals(3, count);
    }

    @Test
    public void whenFindTeachersByCourseThenReturnCorrectTeachers() {
        Set<Teacher> mainCourseTeachers = teacherRepository.findTeachersByCourse(COURSE_NAME_MATHEMATICS);
        Set<Teacher> secondaryCourseTeachers = teacherRepository.findTeachersByCourse(COURSE_NAME_HISTORY);

        assertEquals(2, mainCourseTeachers.size());
        assertEquals(2, secondaryCourseTeachers.size());

        assertTrue(mainCourseTeachers.stream().anyMatch(student -> student.getName().equals(TEACHER_NAME_1)));
        assertTrue(mainCourseTeachers.stream().anyMatch(student -> student.getName().equals(TEACHER_NAME_3)));
        assertTrue(secondaryCourseTeachers.stream().anyMatch(student -> student.getName().equals(TEACHER_NAME_3)));
        assertTrue(secondaryCourseTeachers.stream().anyMatch(student -> student.getName().equals(TEACHER_NAME_2)));
    }

    @Test
    public void whenFindTeachersByGroupThenReturnCorrectTeachers() {
        Set<Teacher> groupA1Teachers = teacherRepository.findTeachersByGroup(GROUP_NAME_1);
        Set<Teacher> groupA2Teachers= teacherRepository.findTeachersByGroup(GROUP_NAME_2);

        assertEquals(2, groupA1Teachers.size());
        assertEquals(1, groupA2Teachers.size());

        assertTrue(groupA1Teachers.stream().anyMatch(student -> student.getName().equals(TEACHER_NAME_1)));
        assertTrue(groupA1Teachers.stream().anyMatch(student -> student.getName().equals(TEACHER_NAME_2)));
        assertTrue(groupA2Teachers.stream().anyMatch(student -> student.getName().equals(TEACHER_NAME_3)));
    }
}
