package com.example.employeemanagementsystem.mapper;

import com.example.employeemanagementsystem.dto.request.EmployeeRequest;
import com.example.employeemanagementsystem.dto.response.EmployeeResponse;
import com.example.employeemanagementsystem.entity.Employee;
import org.springframework.stereotype.Component;

@Component
public class EmployeeMapper {

    public EmployeeResponse toResponse(Employee employee) {
        return new EmployeeResponse(
                employee.getEmpId(),
                employee.getEmpName(),
                employee.getMail(),
                employee.getSalary(),
                employee.getDesignation()
        );
    }

    public Employee toEntity(EmployeeRequest request) {
        Employee employee = new Employee();
        update(employee, request);
        return employee;
    }

    /**
     * Copies the mutable fields of {@code request} onto an existing entity. Kept
     * separate from {@link #toEntity} so that create and update cannot drift apart
     * when a field is added to {@link Employee}.
     */
    public void update(Employee employee, EmployeeRequest request) {
        employee.setEmpName(request.empName());
        employee.setMail(request.mail());
        employee.setSalary(request.salary());
        employee.setDesignation(request.designation());
    }
}