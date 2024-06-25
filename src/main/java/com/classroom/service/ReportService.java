package com.classroom.service;

import com.classroom.dto.course.CourseResponseDTO;
import com.classroom.dto.report.StudentAndTeacherReportResponseDTO;
import com.classroom.dto.student.StudentResponseDTO;
import com.classroom.dto.teacher.TeacherResponseDTO;
import com.classroom.entity.Student;
import com.classroom.entity.Teacher;
import com.classroom.enumartion.CourseType;
import com.classroom.repository.CourseRepository;
import com.classroom.repository.StudentRepository;
import com.classroom.repository.TeacherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
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

    public Set<StudentResponseDTO> getStudentsByCourse(String courseName) {
        Set<Student> students = studentRepository.findStudentsByCourse(courseName);
        return mapStudentsToDTO(students);
    }

    public Set<StudentResponseDTO> getStudentsByGroup(String groupName) {
        Set<Student> students = studentRepository.findStudentsByGroup(groupName);
        return mapStudentsToDTO(students);
    }

    public Set<StudentResponseDTO> getStudentsByAgeAndCourse(int age, String courseName) {
        Set<Student> students = studentRepository.findStudentsOlderThanAgeInCourse(age, courseName);
        return mapStudentsToDTO(students);
    }

    public StudentAndTeacherReportResponseDTO getStudentsAndTeachersByCourseAndGroup(String courseName, String groupName) {
        Set<Student> students = studentRepository.findStudentsByCourseAndGroup(courseName, groupName);
        Set<Teacher> teachers = teacherRepository.findTeachersByCourseAndGroup(courseName, groupName);

        Set<StudentResponseDTO> studentsResponse = mapStudentsToDTO(students);
        Set<TeacherResponseDTO> teacherResponse = mapTeachersToDTO(teachers);

        return new StudentAndTeacherReportResponseDTO(studentsResponse, teacherResponse);
    }

    private Set<StudentResponseDTO> mapStudentsToDTO(Collection<Student> students) {
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
                                        ).collect(Collectors.toSet())
                        )
                        .build())
                .collect(Collectors.toSet());
    }

    private Set<TeacherResponseDTO> mapTeachersToDTO(Collection<Teacher> teachers) {
        return teachers.stream()
                .map(teacher -> TeacherResponseDTO.builder()
                        .teacherId(teacher.getId().toString())
                        .teacherName(teacher.getName())
                        .teacherAge(teacher.getAge())
                        .teacherGroupName(teacher.getTeacherGroup())
                        .teacherCourses(teacher.getCourses().stream()
                                .map(course -> CourseResponseDTO.builder()
                                        .courseName(course.getName())
                                        .courseTypeName(course.getType().name())
                                        .build()
                                ).collect(Collectors.toSet())
                        )
                        .build())
                .collect(Collectors.toSet());
    }
}
