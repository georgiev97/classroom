package com.classroom.web.rest;

import com.classroom.dto.report.StudentAndTeacherReportResponseDTO;
import com.classroom.dto.student.StudentResponseDTO;
import com.classroom.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
public class ReportResource {

    private final ReportService reportService;

    @GetMapping("/{courseName}/{groupName}")
    public ResponseEntity<StudentAndTeacherReportResponseDTO> getReportTeacherAndStudentsFroCourseAndGroup(
            @PathVariable String courseName,
            @PathVariable String groupName
    ) {
        return ResponseEntity.ok(reportService.getStudentsAndTeachersByCourseAndGroup(courseName, groupName));
    }

    @GetMapping("/students/count")
    public ResponseEntity<Long> getStudentsCount() {
        return ResponseEntity.ok(reportService.countStudents());
    }

    @GetMapping("/students/course/{courseName}")
    public ResponseEntity<List<StudentResponseDTO>> getStudentsByCourse(@PathVariable String courseName) {
        return ResponseEntity.ok(reportService.getStudentsByCourse(courseName));
    }

    @GetMapping("/students/group/{groupName}")
    public ResponseEntity<List<StudentResponseDTO>> getStudentsByGroup(@PathVariable String groupName) {
        return ResponseEntity.ok(reportService.getStudentsByGroup(groupName));
    }

    @GetMapping("/students/course/{courseName}/age/{age}")
    public ResponseEntity<List<StudentResponseDTO>> getStudentsInCourseOlderThan(@PathVariable String courseName, Integer age) {
        return ResponseEntity.ok(reportService.getStudentsByAgeAndCourse(age, courseName));
    }

    @GetMapping("/teachers/count")
    public ResponseEntity<Long> getTeachersCount() {
        return ResponseEntity.ok(reportService.countTeachers());
    }

    @GetMapping("/courses/count/{courseType}")
    public ResponseEntity<Long> getCoursesCountByType(@PathVariable String courseType) {
        return ResponseEntity.ok(reportService.countCoursesByType(courseType));
    }
}
