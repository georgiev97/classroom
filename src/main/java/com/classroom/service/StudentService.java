package com.classroom.service;

import com.classroom.dto.course.CourseResponseDTO;
import com.classroom.dto.student.CreateStudentRequestDTO;
import com.classroom.dto.student.EnrollStudentRequestDTO;
import com.classroom.dto.student.LeaveStudentCourseRequestDTO;
import com.classroom.dto.student.StudentResponseDTO;
import com.classroom.entity.Course;
import com.classroom.entity.Student;
import com.classroom.repository.CourseRepository;
import com.classroom.repository.StudentRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class StudentService {

    private static final String STUDENT_ALREADY_EXISTS = "Student with name %s already exists";
    private static final String STUDENT_DOES_NOT_EXISTS = "Student with ID %s does not exists";
    private static final String STUDENT_ALREADY_ENROLLED_FOR_THIS_COURSE =
            "Student with ID %s already enrolled for course with name: %s";

    private static final String STUDENT_NOT_ENROLLED_FOR_THIS_COURSE =
            "Student with ID %s is not enrolled for course with name: %s";
    private static final String COURSE_DOES_NOT_EXISTS = "Course with name %s does not exists";


    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;

    public StudentResponseDTO createStudent(CreateStudentRequestDTO createStudentRequest) {
        String studentName = createStudentRequest.getStudentName();
        int age = createStudentRequest.getStudentAge();
        String groupName = createStudentRequest.getStudentGroupName();
        Optional<Student> existingStudent = studentRepository.findByNameAndStudentGroupAndAge(
                studentName,
                groupName,
                age
        );
        if (existingStudent.isPresent()) {
            throw new EntityExistsException(String.format(STUDENT_ALREADY_EXISTS, studentName));
        }
        Student student = new Student(studentName, age, groupName);

        studentRepository.save(student);
        return StudentResponseDTO.builder()
                .studentId(student.getId().toString())
                .studentName(student.getName())
                .studentAge(student.getAge())
                .studentGroupName(student.getStudentGroup())
                .build();
    }

    public StudentResponseDTO updateStudent(String id, CreateStudentRequestDTO updateStudentRequest) {
        Optional<Student> existingStudent = studentRepository.findById(UUID.fromString(id));
        if (existingStudent.isEmpty()) {
            throw new EntityNotFoundException(String.format(STUDENT_DOES_NOT_EXISTS, id));
        }
        Student student = existingStudent.get();
        student.setName(updateStudentRequest.getStudentName());
        student.setAge(updateStudentRequest.getStudentAge());
        student.setStudentGroup(updateStudentRequest.getStudentGroupName());

        return StudentResponseDTO.builder()
                .studentId(student.getId().toString())
                .studentName(student.getName())
                .studentAge(student.getAge())
                .studentGroupName(student.getStudentGroup())
                .build();
    }

    public StudentResponseDTO enrollToCourse(EnrollStudentRequestDTO enrollStudentRequest) {
        Optional<Student> existingStudent = studentRepository.findById(UUID.fromString(enrollStudentRequest.getStudentId()));
        if (existingStudent.isEmpty()) {
            throw new EntityExistsException(String.format(STUDENT_DOES_NOT_EXISTS, enrollStudentRequest.getStudentId()));
        }
        Student student = existingStudent.get();
        Set<Course> studentCourses = student.getCourses();
        if (studentAlreadyEnrolledForCourse(studentCourses, enrollStudentRequest.getCourseName())) {
            throw new EntityExistsException(String.format(STUDENT_ALREADY_ENROLLED_FOR_THIS_COURSE, enrollStudentRequest.getStudentId(), enrollStudentRequest.getCourseName()));
        }

        Optional<Course> existingCourse = courseRepository.findByName(enrollStudentRequest.getCourseName());
        if (existingCourse.isEmpty()) {
            throw new EntityExistsException(String.format(COURSE_DOES_NOT_EXISTS, enrollStudentRequest.getCourseName()));
        }
        Course course = existingCourse.get();
        course.getStudents().add(student);
        student.getCourses().add(course);
        studentRepository.save(student);

        List<CourseResponseDTO> studentCoursesList = studentCourses.stream()
                .map(c -> CourseResponseDTO.builder()
                        .courseName(c.getName())
                        .courseTypeName(c.getType().name())
                        .build()
                ).toList();
        return StudentResponseDTO.builder()
                .studentId(student.getId().toString())
                .studentName(student.getName())
                .studentAge(student.getAge())
                .studentGroupName(student.getStudentGroup())
                .studentCourses(studentCoursesList)
                .build();
    }

    public StudentResponseDTO removeStudentFromCourse(LeaveStudentCourseRequestDTO leaveStudentCourseRequest) {
        Optional<Student> existingStudent = studentRepository.findById(UUID.fromString(leaveStudentCourseRequest.getStudentId()));
        if (existingStudent.isEmpty()) {
            throw new EntityExistsException(String.format(STUDENT_DOES_NOT_EXISTS, leaveStudentCourseRequest.getStudentId()));
        }
        Student student = existingStudent.get();
        Set<Course> studentCourses = student.getCourses();
        if (studentIsNotEnrolledForCourse(studentCourses, leaveStudentCourseRequest.getCourseName())) {
            throw new EntityExistsException(String.format(STUDENT_NOT_ENROLLED_FOR_THIS_COURSE, leaveStudentCourseRequest.getStudentId(), leaveStudentCourseRequest.getCourseName()));
        }

        Optional<Course> existingCourse = courseRepository.findByName(leaveStudentCourseRequest.getCourseName());
        if (existingCourse.isEmpty()) {
            throw new EntityExistsException(String.format(COURSE_DOES_NOT_EXISTS, leaveStudentCourseRequest.getCourseName()));
        }
        Course course = existingCourse.get();
        student.getCourses().remove(course);
        course.getStudents().remove(student);
        studentRepository.save(student);
        List<CourseResponseDTO> studentCoursesList = studentCourses.stream()
                .map(c -> CourseResponseDTO.builder()
                        .courseName(c.getName())
                        .courseTypeName(c.getType().name())
                        .build()
                ).toList();
        return StudentResponseDTO.builder()
                .studentId(student.getId().toString())
                .studentName(student.getName())
                .studentAge(student.getAge())
                .studentGroupName(student.getStudentGroup())
                .studentCourses(studentCoursesList)
                .build();
    }

    public void deleteStudent(String id) {
        Optional<Student> existingStudent = studentRepository.findById(UUID.fromString(id));
        if (existingStudent.isEmpty()) {
            throw new EntityNotFoundException(String.format(STUDENT_DOES_NOT_EXISTS, id));
        }
        Student student = existingStudent.get();
        studentRepository.delete(student);
    }

    private boolean studentAlreadyEnrolledForCourse(Set<Course> studentCourses, String enrollmentCourse) {
        return studentCourses.stream()
                .map(Course::getName)
                .toList()
                .stream()
                .anyMatch(name -> name.equals(enrollmentCourse));
    }

    private boolean studentIsNotEnrolledForCourse(Set<Course> studentCourses, String leaveStudentCourse) {
        return studentCourses.stream()
                .map(Course::getName)
                .toList()
                .stream().noneMatch(name -> name.equals(leaveStudentCourse));
    }

}
