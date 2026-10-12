package com.example.employeemanagementsystem.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;

/**
 * Column lengths mirror the {@code @Size} constraints on
 * {@link com.example.employeemanagementsystem.dto.request.EmployeeRequest} so that an
 * oversized value is rejected as a 400 at the web boundary rather than as a
 * database constraint violation at persistence time.
 *
 * <p>Owned by Flyway ({@code src/main/resources/db/migration}); the schema is
 * validated, never generated, by Hibernate.
 */
@Entity
@Table(name = "employee", uniqueConstraints = {
        @UniqueConstraint(name = "uk_employee_mail", columnNames = "mail")
})
@Getter
@Setter
public class Employee {

    @Id
    @Column(name = "emp_id", nullable = false, length = 36)
    @GeneratedValue(strategy = GenerationType.UUID)
    private String empId;

    @Column(name = "emp_name", nullable = false, length = 120)
    private String empName;

    @Column(name = "mail", nullable = false, length = 254)
    private String mail;

    @Column(name = "salary", nullable = false)
    private int salary;

    @Column(name = "designation", nullable = false, length = 120)
    private String designation;
}