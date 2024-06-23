package com.classroom.service;

import com.classroom.dto.course.CourseResponseDTO;
import com.classroom.dto.teacher.CreateTeacherRequestDTO;
import com.classroom.dto.teacher.EnrollTeacherRequestDTO;
import com.classroom.dto.teacher.LeaveTeacherCourseRequestDTO;
import com.classroom.dto.teacher.TeacherResponseDTO;
import com.classroom.entity.Course;
import com.classroom.entity.Teacher;
import com.classroom.repository.CourseRepository;
import com.classroom.repository.TeacherRepository;
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
public class TeacherService {

    private static final String TEACHER_ALREADY_EXISTS = "Teacher with name %s already exists";
    private static final String TEACHER_DOES_NOT_EXISTS = "Teacher with ID %s does not exists";
    private static final String TEACHER_ALREADY_ENROLLED_FOR_THIS_COURSE =
            "Teacher with ID %s already enrolled for course with name: %s";

    private static final String TEACHER_NOT_ENROLLED_FOR_THIS_COURSE =
            "Teacher with ID %s is not enrolled for course with name: %s";
    private static final String COURSE_DOES_NOT_EXISTS = "Course with name %s does not exists";


    private final TeacherRepository teacherRepository;
    private final CourseRepository courseRepository;

    public TeacherResponseDTO createTeacher(CreateTeacherRequestDTO createTeacherRequest) {
        String teacherName = createTeacherRequest.getTeacherName();
        int age = createTeacherRequest.getTeacherAge();
        String groupName = createTeacherRequest.getTeacherGroupName();
        Optional<Teacher> existingTeacher = teacherRepository.findByNameAndTeacherGroupAndAge(
                teacherName,
                groupName,
                age
        );
        if (existingTeacher.isPresent()) {
            throw new EntityExistsException(String.format(TEACHER_ALREADY_EXISTS, teacherName));
        }
        Teacher teacher = new Teacher(teacherName, age, groupName);

        teacherRepository.save(teacher);
        return TeacherResponseDTO.builder()
                .teacherId(teacher.getId().toString())
                .teacherName(teacher.getName())
                .teacherAge(teacher.getAge())
                .teacherGroupName(teacher.getTeacherGroup())
                .build();
    }

    public TeacherResponseDTO updateTeacher(String id, CreateTeacherRequestDTO updateTeacherRequest) {
        Optional<Teacher> existingTeacher = teacherRepository.findById(UUID.fromString(id));
        if (existingTeacher.isEmpty()) {
            throw new EntityNotFoundException(String.format(TEACHER_DOES_NOT_EXISTS, id));
        }
        Teacher teacher = existingTeacher.get();
        teacher.setName(updateTeacherRequest.getTeacherName());
        teacher.setAge(updateTeacherRequest.getTeacherAge());
        teacher.setTeacherGroup(updateTeacherRequest.getTeacherGroupName());

        return TeacherResponseDTO.builder()
                .teacherId(teacher.getId().toString())
                .teacherName(teacher.getName())
                .teacherAge(teacher.getAge())
                .teacherGroupName(teacher.getTeacherGroup())
                .build();
    }

    public TeacherResponseDTO enrollToCourse(EnrollTeacherRequestDTO enrollTeacherRequest) {
        Optional<Teacher> existingTeacher = teacherRepository.findById(UUID.fromString(enrollTeacherRequest.getTeacherId()));
        if (existingTeacher.isEmpty()) {
            throw new EntityExistsException(String.format(TEACHER_DOES_NOT_EXISTS, enrollTeacherRequest.getTeacherId()));
        }
        Teacher teacher = existingTeacher.get();
        Set<Course> teacherCourses = teacher.getCourses();
        if (teacherAlreadyEnrolledForCourse(teacherCourses, enrollTeacherRequest.getCourseName())) {
            throw new EntityExistsException(
                    String.format(TEACHER_ALREADY_ENROLLED_FOR_THIS_COURSE,
                            enrollTeacherRequest.getTeacherId(),
                            enrollTeacherRequest.getCourseName())
            );
        }

        Optional<Course> existingCourse = courseRepository.findByName(enrollTeacherRequest.getCourseName());
        if (existingCourse.isEmpty()) {
            throw new EntityExistsException(String.format(COURSE_DOES_NOT_EXISTS, enrollTeacherRequest.getCourseName()));
        }
        Course course = existingCourse.get();
        course.getTeachers().add(teacher);
        teacher.getCourses().add(course);
        teacherRepository.save(teacher);

        List<CourseResponseDTO> teacherCoursesList = teacherCourses.stream()
                .map(c -> CourseResponseDTO.builder()
                        .courseName(c.getName())
                        .courseTypeName(c.getType().name())
                        .build()
                ).toList();
        return TeacherResponseDTO.builder()
                .teacherId(teacher.getId().toString())
                .teacherName(teacher.getName())
                .teacherAge(teacher.getAge())
                .teacherGroupName(teacher.getTeacherGroup())
                .teacherCourses(teacherCoursesList)
                .build();
    }

    public TeacherResponseDTO removeTeacherFromCourse(LeaveTeacherCourseRequestDTO leaveTeacherCourseRequest) {
        Optional<Teacher> existingTeacher = teacherRepository.findById(UUID.fromString(leaveTeacherCourseRequest.getTeacherId()));
        if (existingTeacher.isEmpty()) {
            throw new EntityExistsException(String.format(TEACHER_DOES_NOT_EXISTS, leaveTeacherCourseRequest.getTeacherId()));
        }
        Teacher teacher = existingTeacher.get();
        Set<Course> teacherCourses = teacher.getCourses();
        if (teacherIsNotEnrolledForCourse(teacherCourses, leaveTeacherCourseRequest.getCourseName())) {
            throw new EntityExistsException(
                    String.format(TEACHER_NOT_ENROLLED_FOR_THIS_COURSE,
                            leaveTeacherCourseRequest.getTeacherId(),
                            leaveTeacherCourseRequest.getCourseName())
            );
        }

        Optional<Course> existingCourse = courseRepository.findByName(leaveTeacherCourseRequest.getCourseName());
        if (existingCourse.isEmpty()) {
            throw new EntityExistsException(String.format(COURSE_DOES_NOT_EXISTS, leaveTeacherCourseRequest.getCourseName()));
        }
        Course course = existingCourse.get();
        teacher.getCourses().remove(course);
        course.getTeachers().remove(teacher);
        teacherRepository.save(teacher);
        List<CourseResponseDTO> teacherCoursesList = teacherCourses.stream()
                .map(c -> CourseResponseDTO.builder()
                        .courseName(c.getName())
                        .courseTypeName(c.getType().name())
                        .build()
                ).toList();
        return TeacherResponseDTO.builder()
                .teacherId(teacher.getId().toString())
                .teacherName(teacher.getName())
                .teacherAge(teacher.getAge())
                .teacherGroupName(teacher.getTeacherGroup())
                .teacherCourses(teacherCoursesList)
                .build();
    }

    public void deleteTeacher(String id) {
        Optional<Teacher> existingTeacher = teacherRepository.findById(UUID.fromString(id));
        if (existingTeacher.isEmpty()) {
            throw new EntityNotFoundException(String.format(TEACHER_DOES_NOT_EXISTS, id));
        }
        Teacher teacher = existingTeacher.get();
        teacherRepository.delete(teacher);
    }

    private boolean teacherAlreadyEnrolledForCourse(Set<Course> teacherCourses, String enrollmentCourse) {
        return teacherCourses.stream()
                .map(Course::getName)
                .toList()
                .stream()
                .anyMatch(name -> name.equals(enrollmentCourse));
    }

    private boolean teacherIsNotEnrolledForCourse(Set<Course> teacherCourses, String leaveCourse) {
        return teacherCourses.stream()
                .map(Course::getName)
                .toList()
                .stream().noneMatch(name -> name.equals(leaveCourse));
    }

}
