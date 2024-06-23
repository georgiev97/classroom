package com.classroom.dto.student;

import com.classroom.enumartion.CourseType;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EnrollStudentRequestDTO {

    @NotBlank(message = "Student ID cannot be empty")
    private String studentId;

    @NotBlank(message = "Course name cannot be empty")
    private String courseName;

    @NotBlank(message = "Course Type cannot be empty")
    private String courseType;

}
