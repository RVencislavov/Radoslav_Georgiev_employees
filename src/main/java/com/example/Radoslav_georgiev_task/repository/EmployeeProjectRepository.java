package com.example.Radoslav_georgiev_task.repository;

import com.example.Radoslav_georgiev_task.persistance.entity.EmployeeProject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmployeeProjectRepository extends JpaRepository<EmployeeProject, Long> {

    @Query("SELECT e FROM EmployeeProject e WHERE e.empId IN :empIds AND e.projectId = :projectId")
    List<EmployeeProject> findByEmpIdsAndProjectId(@Param("empIds") List<Long> empIds, @Param("projectId") Long projectId);
}
