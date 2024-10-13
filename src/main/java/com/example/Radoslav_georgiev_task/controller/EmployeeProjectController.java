package com.example.Radoslav_georgiev_task.controller;

import com.example.Radoslav_georgiev_task.persistance.dto.EmployeeProjectDTO;
import com.example.Radoslav_georgiev_task.service.EmployeeProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/employee-projects")
public class EmployeeProjectController {

    private final EmployeeProjectService employeeProjectService;

    @Autowired
    public EmployeeProjectController(EmployeeProjectService employeeProjectService) {
        this.employeeProjectService = employeeProjectService;
    }

    @PostMapping("/upload")
    public ResponseEntity<List<EmployeeProjectDTO>> uploadCSV(@RequestParam("file") MultipartFile file) {
        try {
            List<EmployeeProjectDTO> uploadedData = employeeProjectService.loadCSVFile(file);
            return ResponseEntity.ok(uploadedData);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.emptyList());
        }
    }

    @GetMapping("/longest-working-pair")
    public ResponseEntity<String> getLongestWorkingPair() {
        String result = employeeProjectService.findLongestWorkingPair();
        return ResponseEntity.ok(result);
    }
}
