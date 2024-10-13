package com.example.Radoslav_georgiev_task.mapper;

import com.example.Radoslav_georgiev_task.persistance.dto.EmployeeProjectDTO;
import com.example.Radoslav_georgiev_task.persistance.entity.EmployeeProject;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface EmployeeProjectMapper {
    EmployeeProjectDTO toDTO(EmployeeProject employeeProject);
    EmployeeProject toEntity(EmployeeProjectDTO employeeProjectDTO);
}
