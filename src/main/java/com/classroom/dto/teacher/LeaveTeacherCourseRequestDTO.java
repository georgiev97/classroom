package com.classroom.dto.teacher;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LeaveTeacherCourseRequestDTO {

    @NotBlank(message = "Student ID cannot be empty")
    private String teacherId;

    @NotBlank(message = "Course name cannot be empty")
    private String courseName;
}
