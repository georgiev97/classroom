package com.classroom.dto.student;

import com.classroom.dto.course.CourseResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StudentResponseDTO {

    private String studentId;
    private String studentName;
    private int studentAge;
    private String studentGroupName;
    private List<CourseResponseDTO> studentCourses;
}
