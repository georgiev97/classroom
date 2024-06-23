package com.classroom.web.rest;

import com.classroom.dto.student.CreateStudentRequestDTO;
import com.classroom.dto.student.EnrollStudentRequestDTO;
import com.classroom.dto.student.LeaveStudentCourseRequestDTO;
import com.classroom.dto.student.StudentResponseDTO;
import com.classroom.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/students")
@RequiredArgsConstructor
public class StudentResource {

    private final StudentService studentService;

    @PostMapping
    public ResponseEntity<StudentResponseDTO> createStudent(
            @RequestBody CreateStudentRequestDTO createStudentRequest
    ) {
        StudentResponseDTO createdStudent = studentService.createStudent(createStudentRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdStudent);
    }

    @PutMapping("/{id}")
    public ResponseEntity<StudentResponseDTO> updateStudent(
            @PathVariable("id") String id,
            @RequestBody CreateStudentRequestDTO updateStudentRequest
    ) {
        StudentResponseDTO updatedStudent = studentService.updateStudent(id, updateStudentRequest);
        return ResponseEntity.status(HttpStatus.OK).body(updatedStudent);
    }

    @PostMapping("/enroll")
    public ResponseEntity<StudentResponseDTO> enrollStudentToCourse(
            @RequestBody EnrollStudentRequestDTO enrollStudentRequest
    ) {
        StudentResponseDTO enrolledStudent = studentService.enrollToCourse(enrollStudentRequest);
        return ResponseEntity.status(HttpStatus.OK).body(enrolledStudent);
    }

    @PostMapping("/leave")
    public ResponseEntity<StudentResponseDTO> leaveStudentFromCourse(
            @RequestBody LeaveStudentCourseRequestDTO leaveStudentCourseRequest
    ) {
        StudentResponseDTO studentResponse = studentService.removeStudentFromCourse(leaveStudentCourseRequest);
        return ResponseEntity.status(HttpStatus.OK).body(studentResponse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStudent(
            @PathVariable("id") String id
    ) {
        studentService.deleteStudent(id);
        return ResponseEntity.noContent().build();
    }
}
