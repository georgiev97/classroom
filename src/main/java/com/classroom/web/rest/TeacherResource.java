package com.classroom.web.rest;

import com.classroom.dto.teacher.CreateTeacherRequestDTO;
import com.classroom.dto.teacher.EnrollTeacherRequestDTO;
import com.classroom.dto.teacher.LeaveTeacherCourseRequestDTO;
import com.classroom.dto.teacher.TeacherResponseDTO;
import com.classroom.service.TeacherService;
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
@RequestMapping("/api/v1/teachers")
@RequiredArgsConstructor
public class TeacherResource {

    private final TeacherService teacherService;

    @PostMapping
    public ResponseEntity<TeacherResponseDTO> createTeacher(
            @RequestBody CreateTeacherRequestDTO createTeacherRequest
    ) {
        TeacherResponseDTO createdTeacher = teacherService.createTeacher(createTeacherRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTeacher);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TeacherResponseDTO> updateTeacher(
            @PathVariable("id") String id,
            @RequestBody CreateTeacherRequestDTO updateTeacherRequest
    ) {
        TeacherResponseDTO updatedTeacher = teacherService.updateTeacher(id, updateTeacherRequest);
        return ResponseEntity.status(HttpStatus.OK).body(updatedTeacher);
    }

    @PostMapping("/enroll")
    public ResponseEntity<TeacherResponseDTO> enrollTeacherToCourse(
            @RequestBody EnrollTeacherRequestDTO enrollTeacherRequest
    ) {
        TeacherResponseDTO enrolledTeacher = teacherService.enrollToCourse(enrollTeacherRequest);
        return ResponseEntity.status(HttpStatus.OK).body(enrolledTeacher);
    }

    @PostMapping("/leave")
    public ResponseEntity<TeacherResponseDTO> leaveTeacherFromCourse(
            @RequestBody LeaveTeacherCourseRequestDTO leaveTeacherCourseRequest
    ) {
        TeacherResponseDTO teacherResponse = teacherService.removeTeacherFromCourse(leaveTeacherCourseRequest);
        return ResponseEntity.status(HttpStatus.OK).body(teacherResponse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTeacher(
            @PathVariable("id") String id
    ) {
        teacherService.deleteTeacher(id);
        return ResponseEntity.noContent().build();
    }
}
