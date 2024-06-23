package com.classroom.dto.report;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StudentAndTeacherReportResponseDTO {

    private String studentName;
    private int studentAge;
    private String teacherName;
    private int teacherAge;
    private String groupName;
    private String courseName;
}
