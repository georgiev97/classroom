package com.classroom.dto.report;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudentAndTeacherReportResponseDTO {

    private Set<StudentDTO> students = new HashSet<>();
    private Set<TeacherDTO> teachers = new HashSet<>();

}
