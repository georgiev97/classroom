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
public class CreateTeacherRequestDTO {

    @NotBlank(message = "Name cannot be empty")
    private String teacherName;

    private int teacherAge;

    @NotBlank(message = "Group cannot be empty")
    private String teacherGroupName;
}
