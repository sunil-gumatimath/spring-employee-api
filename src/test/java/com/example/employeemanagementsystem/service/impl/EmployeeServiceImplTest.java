package com.example.employeemanagementsystem.service.impl;

import com.example.employeemanagementsystem.dto.request.EmployeeRequest;
import com.example.employeemanagementsystem.dto.response.EmployeeResponse;
import com.example.employeemanagementsystem.entity.Employee;
import com.example.employeemanagementsystem.exception.EmployeeNotFoundException;
import com.example.employeemanagementsystem.mapper.EmployeeMapper;
import com.example.employeemanagementsystem.repository.EmployeeRepository;
import com.example.employeemanagementsystem.utility.PageResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Intentionally uses {@link SpringBootTest} rather than {@code @DataJpaTest}.
 *
 * <p>{@code @DataJpaTest} wraps every test in a single transaction, which keeps the
 * persistence context open. That masks a missing-persist bug two ways: a later
 * {@code findById} returns the cached, already-mutated instance rather than re-reading
 * the row, and any {@code flush()} still writes the dirty entity out even when the
 * service never called {@code save()}. Under {@link SpringBootTest} there is no
 * ambient transaction, so each repository and service call manages and commits its own
 * — exactly matching production.
 */
@SpringBootTest
class EmployeeServiceImplTest {

    @Autowired
    private EmployeeServiceImpl employeeService;

    @Autowired
    private EmployeeRepository employeeRepository;

    private Employee existing;

    @BeforeEach
    void setUp() {
        employeeRepository.deleteAll();
        existing = new Employee();
        existing.setEmpName("Ada Lovelace");
        existing.setMail("ada@example.com");
        existing.setSalary(90_000);
        existing.setDesignation("Principal Engineer");
        employeeRepository.save(existing);
    }

    @Test
    @DisplayName("updateEmployee persists changes to the database")
    void updateEmployeePersistsChanges() {
        EmployeeRequest request = new EmployeeRequest(
                "Ada Byron", "ada.byron@example.com", 120_000, "Distinguished Engineer");

        EmployeeResponse response = employeeService.updateEmployee(existing.getEmpId(), request);

        Employee reloaded = employeeRepository.findById(existing.getEmpId()).orElseThrow();
        assertThat(reloaded.getEmpName()).isEqualTo("Ada Byron");
        assertThat(reloaded.getSalary()).isEqualTo(120_000);
        assertThat(reloaded.getDesignation()).isEqualTo("Distinguished Engineer");
        assertThat(response.empName()).isEqualTo("Ada Byron");
    }

    @Test
    @DisplayName("updateEmployee writes mail into mail, not into empName")
    void updateEmployeeMapsMailToMail() {
        EmployeeRequest request = new EmployeeRequest(
                "Ada Byron", "ada.byron@example.com", 120_000, "Distinguished Engineer");

        employeeService.updateEmployee(existing.getEmpId(), request);

        Employee reloaded = employeeRepository.findById(existing.getEmpId()).orElseThrow();
        assertThat(reloaded.getMail()).isEqualTo("ada.byron@example.com");
        assertThat(reloaded.getEmpName()).isEqualTo("Ada Byron");
    }

    @Test
    @DisplayName("addEmployee returns the generated id and persists the row")
    void addEmployeePersistsAndReturnsId() {
        EmployeeRequest request = new EmployeeRequest(
                "Grace Hopper", "grace@example.com", 150_000, "Rear Admiral");

        EmployeeResponse response = employeeService.addEmployee(request);

        assertThat(response.empId()).isNotBlank();
        assertThat(response.mail()).isEqualTo("grace@example.com");
        Employee reloaded = employeeRepository.findById(response.empId()).orElseThrow();
        assertThat(reloaded.getEmpName()).isEqualTo("Grace Hopper");
        assertThat(reloaded.getMail()).isEqualTo("grace@example.com");
    }

    @Test
    @DisplayName("findEmployee throws EmployeeNotFoundException for an unknown id")
    void findEmployeeThrowsWhenMissing() {
        assertThatThrownBy(() -> employeeService.findEmployee("does-not-exist"))
                .isInstanceOf(EmployeeNotFoundException.class)
                .hasMessageContaining("does-not-exist");
    }

    @Test
    @DisplayName("deleteEmployee removes the row")
    void deleteEmployeeRemovesRow() {
        employeeService.deleteEmployee(existing.getEmpId());

        assertThat(employeeRepository.findById(existing.getEmpId())).isEmpty();
    }

    @Test
    @DisplayName("displayAllEmployee returns a page and honours size")
    void displayAllEmployeeIsPaginated() {
        employeeRepository.save(newEmployee("Alan Turing"));
        employeeRepository.save(newEmployee("Katherine Johnson"));

        PageResponse<EmployeeResponse> page = employeeService.displayAllEmployee(
                PageRequest.of(0, 2, Sort.by("empName")));

        assertThat(page.content()).hasSize(2);
        assertThat(page.totalElements()).isEqualTo(3);
        assertThat(page.totalPages()).isEqualTo(2);
        assertThat(page.first()).isTrue();
        assertThat(page.last()).isFalse();
    }

    private Employee newEmployee(String name) {
        Employee employee = new Employee();
        employee.setEmpName(name);
        employee.setMail(name.toLowerCase().replace(' ', '.') + "@example.com");
        employee.setSalary(80_000);
        employee.setDesignation("Engineer");
        return employee;
    }
}