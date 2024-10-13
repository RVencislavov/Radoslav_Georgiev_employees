package com.example.Radoslav_georgiev_task.service.serviceImpl;

import com.example.Radoslav_georgiev_task.mapper.EmployeeProjectMapper;
import com.example.Radoslav_georgiev_task.persistance.dto.EmployeeProjectDTO;
import com.example.Radoslav_georgiev_task.persistance.entity.EmployeeProject;
import com.example.Radoslav_georgiev_task.repository.EmployeeProjectRepository;
import com.example.Radoslav_georgiev_task.service.EmployeeProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class EmployeeProjectServiceImpl implements EmployeeProjectService {

    private static final Logger logger = LoggerFactory.getLogger(EmployeeProjectServiceImpl.class);
    private static final List<DateTimeFormatter> DATE_FORMATS = Arrays.asList(
            DateTimeFormatter.ofPattern("yyyy-MM-dd"),
            DateTimeFormatter.ofPattern("MM/dd/yyyy"),
            DateTimeFormatter.ofPattern("dd/MM/yyyy"),
            DateTimeFormatter.ofPattern("M/d/yyyy"),
            DateTimeFormatter.ofPattern("MM/d/yyyy"),
            DateTimeFormatter.ofPattern("M/dd/yyyy")
    );

    private final EmployeeProjectRepository employeeProjectRepository;
    private final EmployeeProjectMapper employeeProjectMapper;

    @Autowired
    public EmployeeProjectServiceImpl(EmployeeProjectRepository employeeProjectRepository, EmployeeProjectMapper employeeProjectMapper) {
        this.employeeProjectRepository = employeeProjectRepository;
        this.employeeProjectMapper = employeeProjectMapper;
    }

    @Override
    public List<EmployeeProjectDTO> loadCSVFile(MultipartFile file) throws IOException {
        List<EmployeeProjectDTO> employeeProjectDTOs = parseCSV(file);
        List<EmployeeProject> employeeProjects = employeeProjectRepository.saveAll(
                employeeProjectDTOs.stream()
                        .map(employeeProjectMapper::toEntity)
                        .toList()
        );
        return employeeProjects.stream()
                .map(employeeProjectMapper::toDTO)
                .toList();
    }

    private List<EmployeeProjectDTO> parseCSV(MultipartFile file) throws IOException {
        List<EmployeeProjectDTO> employeeProjects = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            String line = reader.readLine();

            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");

                if (data.length < 4) {
                    logger.warn("Skipping line due to insufficient data: {}", line);
                    continue;
                }

                try {
                    EmployeeProjectDTO dto = new EmployeeProjectDTO();
                    dto.setEmpId(Long.parseLong(data[0].trim()));
                    dto.setProjectId(Long.parseLong(data[1].trim()));
                    dto.setDateFrom(parseDate(data[2].trim()));

                    String dateToStr = data[3].trim();
                    dto.setDateTo("NULL".equalsIgnoreCase(dateToStr) || dateToStr.isEmpty() ? LocalDate.now() : parseDate(dateToStr));

                    employeeProjects.add(dto);
                } catch (NumberFormatException | DateTimeParseException e) {
                    logger.error("Error parsing line: {}. Error: {}", line, e.getMessage());
                }
            }
        }

        return employeeProjects;
    }

    private LocalDate parseDate(String dateStr) {
        for (DateTimeFormatter formatter : DATE_FORMATS) {
            try {
                return LocalDate.parse(dateStr, formatter);
            } catch (DateTimeParseException ignored) {
            }
        }
        throw new IllegalArgumentException("Unrecognized date format: " + dateStr);
    }

    @Override
    public String findLongestWorkingPair() {
        List<EmployeeProject> allProjects = employeeProjectRepository.findAll();
        Map<String, Long> pairDurationMap = calculateWorkingDurations(allProjects);
        return getLongestWorkingPair(pairDurationMap);
    }

    private Map<String, Long> calculateWorkingDurations(List<EmployeeProject> allProjects) {
        Map<String, Long> pairDurationMap = new HashMap<>();

        for (int i = 0; i < allProjects.size(); i++) {
            EmployeeProject first = allProjects.get(i);

            for (int j = i + 1; j < allProjects.size(); j++) {
                EmployeeProject second = allProjects.get(j);

                if (first.getProjectId().equals(second.getProjectId()) && !first.getEmpId().equals(second.getEmpId())) {
                    LocalDate overlapStart = first.getDateFrom().isAfter(second.getDateFrom()) ? first.getDateFrom() : second.getDateFrom();
                    LocalDate overlapEnd = first.getDateTo().isBefore(second.getDateTo()) ? first.getDateTo() : second.getDateTo();

                    if (!overlapStart.isAfter(overlapEnd)) {
                        long daysWorkedTogether = ChronoUnit.DAYS.between(overlapStart, overlapEnd) + 1;
                        String pairKey = createPairKey(first.getEmpId(), second.getEmpId());
                        pairDurationMap.put(pairKey, pairDurationMap.getOrDefault(pairKey, 0L) + daysWorkedTogether);
                    }
                }
            }
        }

        return pairDurationMap;
    }

    private String getLongestWorkingPair(Map<String, Long> pairDurationMap) {
        return pairDurationMap.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(entry -> entry.getKey() + ", Days Worked Together: " + entry.getValue())
                .orElse("No common projects found.");
    }

    private String createPairKey(Long empId1, Long empId2) {
        return empId1 < empId2 ? empId1 + "," + empId2 : empId2 + "," + empId1;
    }
}
