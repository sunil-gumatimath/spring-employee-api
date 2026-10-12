package com.example.employeemanagementsystem.controller;

import com.example.employeemanagementsystem.dto.response.EmployeeResponse;
import com.example.employeemanagementsystem.exception.EmployeeNotFoundException;
import com.example.employeemanagementsystem.service.EmployeeService;
import com.example.employeemanagementsystem.utility.PageResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EmployeeController.class)
class EmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EmployeeService employeeService;

    private static final String VALID_BODY = """
            {"empName":"Ada Lovelace","mail":"ada@example.com","salary":90000,"designation":"Principal Engineer"}
            """;

    private static final String INVALID_BODY = """
            {"empName":"","mail":"not-an-email","salary":-1,"designation":""}
            """;

    private EmployeeResponse response(String id) {
        return new EmployeeResponse(id, "Ada Lovelace", "ada@example.com", 90_000, "Principal Engineer");
    }

    @Test
    void postEmployee_returns201_withEnvelope() throws Exception {
        when(employeeService.addEmployee(any())).thenReturn(response("uuid-1"));

        mockMvc.perform(post("/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_BODY))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value(201))
                .andExpect(jsonPath("$.message").value("Employee created"))
                .andExpect(jsonPath("$.data.empId").value("uuid-1"));
    }

    @Test
    void postEmployee_returns400_whenValidationFails() throws Exception {
        mockMvc.perform(post("/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(INVALID_BODY))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.errors").isArray())
                .andExpect(jsonPath("$.errors.length()").value(4));
    }

    @Test
    void postEmployee_returns400_whenBodyIsMalformed() throws Exception {
        mockMvc.perform(post("/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ this is not json "))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Malformed or unreadable request body"));
    }

    @Test
    void getEmployeeById_returns200() throws Exception {
        when(employeeService.findEmployee("uuid-1")).thenReturn(response("uuid-1"));

        mockMvc.perform(get("/employees/uuid-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Employee with id = uuid-1 found"))
                .andExpect(jsonPath("$.data.empId").value("uuid-1"));
    }

    @Test
    void getEmployeeById_returns404_whenMissing() throws Exception {
        when(employeeService.findEmployee("nope"))
                .thenThrow(new EmployeeNotFoundException("nope"));

        mockMvc.perform(get("/employees/nope"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Employee with id = nope not found"));
    }

    @Test
    void getEmployees_returnsPagedEnvelope() throws Exception {
        when(employeeService.displayAllEmployee(any()))
                .thenReturn(new PageResponse<>(List.of(response("uuid-1")), 0, 20, 1, 1, true, true));

        mockMvc.perform(get("/employees"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.content.length()").value(1))
                .andExpect(jsonPath("$.data.totalElements").value(1))
                .andExpect(jsonPath("$.data.first").value(true));
    }

    @Test
    void putEmployee_returns200() throws Exception {
        when(employeeService.updateEmployee(eq("uuid-1"), any())).thenReturn(response("uuid-1"));

        mockMvc.perform(put("/employees/uuid-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_BODY))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Employee with id = uuid-1 updated"));
    }

    @Test
    void putEmployee_returns400_whenValidationFails() throws Exception {
        mockMvc.perform(put("/employees/uuid-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(INVALID_BODY))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void deleteEmployee_returns204_noBody() throws Exception {
        doNothing().when(employeeService).deleteEmployee("uuid-1");

        mockMvc.perform(delete("/employees/uuid-1"))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));

        verify(employeeService).deleteEmployee("uuid-1");
    }

    @Test
    void deleteEmployee_returns404_whenMissing() throws Exception {
        doThrow(new EmployeeNotFoundException("nope"))
                .when(employeeService).deleteEmployee("nope");

        mockMvc.perform(delete("/employees/nope"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void unsupportedMethod_returns405() throws Exception {
        mockMvc.perform(patch("/employees/uuid-1"))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(jsonPath("$.status").value(405));
    }

    @Test
    void unknownPath_returns404() throws Exception {
        mockMvc.perform(get("/employees/uuid-1/segments"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }
}