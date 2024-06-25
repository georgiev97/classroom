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

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@DataJpaTest
public class StudentRepositoryTest {

    private static final String COURSE_NAME_MATHEMATICS = "Mathematics";
    private static final String COURSE_NAME_HISTORY = "History";
    private static final String COURSE_NAME_SCIENCE = "Science";
    private static final String STUDENT_NAME_1 = "Mariya";
    private static final String STUDENT_NAME_2 = "Ivan";
    private static final String STUDENT_NAME_3 = "Georgi";
    private static final String GROUP_NAME_1 = "A1";
    private static final String GROUP_NAME_2 = "A2";

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private StudentRepository studentRepository;

    @BeforeEach
    void setUp() {
        // Create Courses
        Course course1 = new Course(COURSE_NAME_MATHEMATICS, CourseType.MAIN);
        Course course2 = new Course(COURSE_NAME_HISTORY, CourseType.SECONDARY);
        Course course3 = new Course(COURSE_NAME_SCIENCE, CourseType.MAIN);

        courseRepository.save(course1);
        courseRepository.save(course2);
        courseRepository.save(course3);

        // Create Students
        Student student1 = new Student(STUDENT_NAME_1, 20, GROUP_NAME_2, Set.of(course1));
        Student student2 = new Student(STUDENT_NAME_2, 22, GROUP_NAME_1, Set.of(course2));
        Student student3 = new Student(STUDENT_NAME_3, 24, GROUP_NAME_2, Set.of(course3, course1));

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
        assertEquals(1, secondaryCourseStudents.size());

        assertTrue(mainCourseStudents.stream().anyMatch(student -> student.getName().equals(STUDENT_NAME_1)));
        assertTrue(mainCourseStudents.stream().anyMatch(student -> student.getName().equals(STUDENT_NAME_3)));
        assertTrue(secondaryCourseStudents.stream().anyMatch(student -> student.getName().equals(STUDENT_NAME_2)));
    }

    @Test
    public void whenFindStudentsByGroupThenReturnCorrectStudents() {
        Set<Student> groupA1Students = studentRepository.findStudentsByGroup(GROUP_NAME_1);
        Set<Student> groupA2Students = studentRepository.findStudentsByGroup(GROUP_NAME_2);

        assertEquals(1, groupA1Students.size());
        assertEquals(2, groupA2Students.size());

        assertTrue(groupA2Students.stream().anyMatch(student -> student.getName().equals(STUDENT_NAME_1)));
        assertTrue(groupA1Students.stream().anyMatch(student -> student.getName().equals(STUDENT_NAME_2)));
        assertTrue(groupA2Students.stream().anyMatch(student -> student.getName().equals(STUDENT_NAME_3)));
    }

    @Test
    public void whenFindStudentsOlderThanAgeInCourseThenReturnCorrectStudents() {
        Set<Student> olderStudentsInMainCourse = studentRepository.findStudentsOlderThanAgeInCourse(21, COURSE_NAME_MATHEMATICS);

        assertEquals(1, olderStudentsInMainCourse.size());

        assertTrue(olderStudentsInMainCourse.stream().anyMatch(student -> student.getName().equals(STUDENT_NAME_3)));
    }

    @Test
    public void whenFindByNameAndAgeAndGroupCorrectResult() {
        Optional<Student> result = studentRepository.findByNameAndStudentGroupAndAge(STUDENT_NAME_3, GROUP_NAME_2,   24);

        assertTrue(result.isPresent());
        assertEquals(STUDENT_NAME_3, result.get().getName());
    }

    @Test
    public void whenFindByNameAndAgeAndGroupNoResult() {
        Optional<Student> result = studentRepository.findByNameAndStudentGroupAndAge(STUDENT_NAME_3, GROUP_NAME_2,   20);

        assertTrue(result.isEmpty());
    }

    @Test
    public void whenFindStudentsByCourseAndGroupReturnCorrectResults() {
        Set<Student> result = studentRepository.findStudentsByCourseAndGroup(COURSE_NAME_MATHEMATICS, GROUP_NAME_2);

        assertEquals(2, result.size());
        assertTrue(result.stream().map(Student::getStudentGroup).allMatch(GROUP_NAME_2::equals));
        assertTrue(result.stream().map(Student::getCourses).anyMatch(courses -> courses.stream().anyMatch(course -> course.getName().equals(COURSE_NAME_SCIENCE))));

    }
}
