package com.example.employeemanagementsystem.service;

import com.example.employeemanagementsystem.dto.request.EmployeeRequest;
import com.example.employeemanagementsystem.dto.response.EmployeeResponse;
import com.example.employeemanagementsystem.utility.PageResponse;
import org.springframework.data.domain.Pageable;

public interface EmployeeService {

    EmployeeResponse addEmployee(EmployeeRequest employeeRequest);

    EmployeeResponse findEmployee(String id);

    PageResponse<EmployeeResponse> displayAllEmployee(Pageable pageable);

    void deleteEmployee(String id);

    EmployeeResponse updateEmployee(String id, EmployeeRequest employeeRequest);
}