package com.classroom.service;

import com.classroom.dto.course.CourseResponseDTO;
import com.classroom.dto.report.StudentAndTeacherReportResponseDTO;
import com.classroom.dto.report.StudentDTO;
import com.classroom.dto.report.TeacherDTO;
import com.classroom.dto.student.StudentResponseDTO;
import com.classroom.entity.Course;
import com.classroom.entity.Student;
import com.classroom.entity.Teacher;
import com.classroom.enumartion.CourseType;
import com.classroom.repository.CourseRepository;
import com.classroom.repository.StudentRepository;
import com.classroom.repository.TeacherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final CourseRepository courseRepository;
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;

    public long countStudents() {
        return studentRepository.studentsCount();
    }

    public long countTeachers() {
        return teacherRepository.teachersCount();
    }

    public long countCoursesByType(String courseType) {
        CourseType type = CourseType.fromString(courseType);
        return courseRepository.countCoursesByType(type);
    }

    public List<StudentResponseDTO> getStudentsByCourse(String courseName) {
        Set<Student> students = studentRepository.findStudentsByCourse(courseName);
        return mapEntityToDTO(students);
    }

    public List<StudentResponseDTO> getStudentsByGroup(String groupName) {
        Set<Student> students = studentRepository.findStudentsByGroup(groupName);
        return mapEntityToDTO(students);
    }

    public List<StudentResponseDTO> getStudentsByAgeAndCourse(int age, String courseName) {
        Set<Student> students = studentRepository.findStudentsOlderThanAgeInCourse(age, courseName);
        return mapEntityToDTO(students);
    }

    public StudentAndTeacherReportResponseDTO getStudentsAndTeachersByCourseAndGroup(String courseName, String groupName) {
        Set<Student> students = studentRepository.findStudentsByCourseAndGroup(courseName, groupName);
        Set<Teacher> teachers = teacherRepository.findTeachersByCourseAndGroup(courseName, groupName);

        Set<StudentDTO> studentsResponse = mapStudentsToDTO(students);
        Set<TeacherDTO> teacherResponse = mapTeacherToDTO(teachers);

        return new StudentAndTeacherReportResponseDTO(studentsResponse, teacherResponse);
    }

    private List<StudentResponseDTO> mapEntityToDTO(Collection<Student> students) {
        return students.stream()
                .map(student -> StudentResponseDTO.builder()
                        .studentId(student.getId().toString())
                        .studentName(student.getName())
                        .studentAge(student.getAge())
                        .studentGroupName(student.getStudentGroup())
                        .studentCourses(student.getCourses().stream()
                                        .map(course -> CourseResponseDTO.builder()
                                                        .courseName(course.getName())
                                                        .courseTypeName(course.getType().name())
                                                        .build()
                                        ).toList()
                        )
                        .build())
                .toList();
    }

    private Set<StudentDTO> mapStudentsToDTO(Collection<Student> students) {
        return students.stream()
                .map(student -> StudentDTO.builder()
                        .studentId(student.getId().toString())
                        .studentName(student.getName())
                        .studentAge(student.getAge())
                        .studentGroupName(student.getStudentGroup())
                        .courseNames(student.getCourses().stream()
                                .map(Course::getName)
                                .collect(Collectors.toSet()))
                        .build()
                ).collect(Collectors.toSet());
    }

    private Set<TeacherDTO> mapTeacherToDTO(Collection<Teacher> teachers) {
        return teachers.stream()
                .map(teacher -> TeacherDTO.builder()
                        .teacherId(teacher.getId().toString())
                        .teacherName(teacher.getName())
                        .teacherAge(teacher.getAge())
                        .teacherGroupName(teacher.getTeacherGroup())
                        .courseNames(teacher.getCourses().stream()
                                .map(Course::getName)
                                .collect(Collectors.toSet()))
                        .build()
                ).collect(Collectors.toSet());
    }
}
