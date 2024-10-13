package com.example.Radoslav_georgiev_task.service;

import com.example.Radoslav_georgiev_task.persistance.dto.EmployeeProjectDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface EmployeeProjectService {
    public List<EmployeeProjectDTO> loadCSVFile(MultipartFile file) throws IOException;
    public String findLongestWorkingPair();
}
