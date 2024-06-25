package com.classroom.dto.report;

import com.classroom.dto.student.StudentResponseDTO;
import com.classroom.dto.teacher.TeacherResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudentAndTeacherReportResponseDTO {

    private Set<StudentResponseDTO> students = new HashSet<>();
    private Set<TeacherResponseDTO> teachers = new HashSet<>();

}
