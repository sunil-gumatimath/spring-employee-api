package com.example.employeemanagementsystem.controller;

import com.example.employeemanagementsystem.dto.request.EmployeeRequest;
import com.example.employeemanagementsystem.dto.response.EmployeeResponse;
import com.example.employeemanagementsystem.service.EmployeeService;
import com.example.employeemanagementsystem.utility.PageResponse;
import com.example.employeemanagementsystem.utility.ResponseStructure;
import com.example.employeemanagementsystem.utility.RestResponseBuilder;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    /** POST /employees */
    @PostMapping
    public ResponseEntity<ResponseStructure<EmployeeResponse>> addEmployee(
            @Valid @RequestBody EmployeeRequest employeeRequest) {

        EmployeeResponse created = employeeService.addEmployee(employeeRequest);
        return RestResponseBuilder.build(HttpStatus.CREATED, "Employee created", created);
    }

    /** GET /employees/{id} */
    @GetMapping("/{id}")
    public ResponseEntity<ResponseStructure<EmployeeResponse>> findEmployee(@PathVariable String id) {
        EmployeeResponse found = employeeService.findEmployee(id);
        return RestResponseBuilder.build(HttpStatus.OK, "Employee with id = " + id + " found", found);
    }

    /** GET /employees?page=0&size=20&sort=empName,asc */
    @GetMapping
    public ResponseEntity<ResponseStructure<PageResponse<EmployeeResponse>>> displayAllEmployee(
            @PageableDefault(size = 20, sort = "empName") Pageable pageable) {

        PageResponse<EmployeeResponse> employees = employeeService.displayAllEmployee(pageable);
        return RestResponseBuilder.build(HttpStatus.OK, "Employees fetched", employees);
    }

    /** DELETE /employees/{id} */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable String id) {
        employeeService.deleteEmployee(id);
        return ResponseEntity.noContent().build();
    }

    /** PUT /employees/{id} */
    @PutMapping("/{id}")
    public ResponseEntity<ResponseStructure<EmployeeResponse>> updateEmployee(
            @PathVariable String id,
            @Valid @RequestBody EmployeeRequest employeeRequest) {

        EmployeeResponse updated = employeeService.updateEmployee(id, employeeRequest);
        return RestResponseBuilder.build(HttpStatus.OK, "Employee with id = " + id + " updated", updated);
    }
}