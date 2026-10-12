CREATE TABLE employee (
    emp_id      VARCHAR(36)  NOT NULL,
    emp_name    VARCHAR(120) NOT NULL,
    mail        VARCHAR(254) NOT NULL,
    salary      INT          NOT NULL,
    designation VARCHAR(120) NOT NULL,
    CONSTRAINT pk_employee PRIMARY KEY (emp_id),
    CONSTRAINT uk_employee_mail UNIQUE (mail)
);