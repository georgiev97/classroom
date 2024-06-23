package com.classroom.repository;

import com.classroom.entity.Course;
import com.classroom.entity.Student;
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
public class StudentRepositoryTest {

    private static final String STUDENT_NAME_1 = "Mariya";
    private static final String STUDENT_NAME_2 = "Ivan";
    private static final String STUDENT_NAME_3 = "Georgi";

    private static final String COURSE_NAME_MATHEMATICS = "Mathematics";
    private static final String COURSE_NAME_HISTORY = "History";

    private static final String GROUP_NAME_1 = "A1";
    private static final String GROUP_NAME_2 = "A2";

    @Autowired
    private StudentRepository studentRepository;

    @BeforeEach
    void setUp() {
        // Create Courses
        Course mainCourse = Course.builder()
                .name(COURSE_NAME_MATHEMATICS)
                .type(CourseType.MAIN)
                .build();
        Course secondaryCourse = Course.builder()
                .name(COURSE_NAME_HISTORY)
                .type(CourseType.SECONDARY)
                .build();

        // Create Students
        Student student1 = Student.builder()
                        .name(STUDENT_NAME_1)
                        .age(20)
                        .studentGroup(GROUP_NAME_1)
                        .courses(Set.of(mainCourse))
                        .build();
        Student student2 = Student.builder()
                        .name(STUDENT_NAME_2)
                        .age(22)
                        .studentGroup(GROUP_NAME_1)
                        .courses(Set.of(secondaryCourse))
                        .build();
        Student student3 = Student.builder()
                        .name(STUDENT_NAME_3)
                        .age(24)
                        .studentGroup(GROUP_NAME_2)
                        .courses(Set.of(mainCourse, secondaryCourse))
                        .build();

        studentRepository.save(student1);
        studentRepository.save(student2);
        studentRepository.save(student3);
    }

    @Test
    public void whenCountStudentsThenReturnCorrectCount() {
        long count = studentRepository.studentsCount();
        assertEquals(3, count);
    }

    @Test
    public void whenFindStudentsByCourseThenReturnCorrectStudents() {
        Set<Student> mainCourseStudents = studentRepository.findStudentsByCourse(COURSE_NAME_MATHEMATICS);
        Set<Student> secondaryCourseStudents = studentRepository.findStudentsByCourse(COURSE_NAME_HISTORY);

        assertEquals(2, mainCourseStudents.size());
        assertEquals(2, secondaryCourseStudents.size());

        assertTrue(mainCourseStudents.stream().anyMatch(student -> student.getName().equals(STUDENT_NAME_1)));
        assertTrue(mainCourseStudents.stream().anyMatch(student -> student.getName().equals(STUDENT_NAME_3)));
        assertTrue(secondaryCourseStudents.stream().anyMatch(student -> student.getName().equals(STUDENT_NAME_3)));
        assertTrue(secondaryCourseStudents.stream().anyMatch(student -> student.getName().equals(STUDENT_NAME_2)));
    }

    @Test
    public void whenFindStudentsByGroupThenReturnCorrectStudents() {
        Set<Student> groupA1Students = studentRepository.findStudentsByGroup(GROUP_NAME_1);
        Set<Student> groupA2Students = studentRepository.findStudentsByGroup(GROUP_NAME_2);

        assertEquals(2, groupA1Students.size());
        assertEquals(1, groupA2Students.size());

        assertTrue(groupA1Students.stream().anyMatch(student -> student.getName().equals(STUDENT_NAME_1)));
        assertTrue(groupA1Students.stream().anyMatch(student -> student.getName().equals(STUDENT_NAME_2)));
        assertTrue(groupA2Students.stream().anyMatch(student -> student.getName().equals(STUDENT_NAME_3)));
    }

    @Test
    public void whenFindStudentsOlderThanAgeInCourseThenReturnCorrectStudents() {
        Set<Student> olderStudentsInMainCourse = studentRepository.findStudentsOlderThanAgeInCourse(21, COURSE_NAME_MATHEMATICS);

        assertEquals(1, olderStudentsInMainCourse.size());

        assertTrue(olderStudentsInMainCourse.stream().anyMatch(student -> student.getName().equals(STUDENT_NAME_3)));
    }
}
