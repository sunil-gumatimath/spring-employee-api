package com.example.employeemanagementsystem.service.impl;

import com.example.employeemanagementsystem.dto.request.EmployeeRequest;
import com.example.employeemanagementsystem.dto.response.EmployeeResponse;
import com.example.employeemanagementsystem.entity.Employee;
import com.example.employeemanagementsystem.exception.EmployeeNotFoundException;
import com.example.employeemanagementsystem.mapper.EmployeeMapper;
import com.example.employeemanagementsystem.repository.EmployeeRepository;
import com.example.employeemanagementsystem.service.EmployeeService;
import com.example.employeemanagementsystem.utility.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final EmployeeMapper employeeMapper;

    @Override
    @Transactional
    public EmployeeResponse addEmployee(EmployeeRequest employeeRequest) {
        Employee saved = employeeRepository.save(employeeMapper.toEntity(employeeRequest));
        return employeeMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public EmployeeResponse findEmployee(String id) {
        return employeeMapper.toResponse(getEmployeeOrThrow(id));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<EmployeeResponse> displayAllEmployee(Pageable pageable) {
        Page<Employee> employees = employeeRepository.findAll(pageable);
        return PageResponse.from(employees, employeeMapper::toResponse);
    }

    @Override
    @Transactional
    public void deleteEmployee(String id) {
        employeeRepository.delete(getEmployeeOrThrow(id));
    }

    @Override
    @Transactional
    public EmployeeResponse updateEmployee(String id, EmployeeRequest employeeRequest) {
        Employee employee = getEmployeeOrThrow(id);
        employeeMapper.update(employee, employeeRequest);
        // The entity is already managed inside this transaction, so dirty checking
        // flushes the update at commit. No explicit save() is required.
        return employeeMapper.toResponse(employee);
    }

    private Employee getEmployeeOrThrow(String id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException(id));
    }
}