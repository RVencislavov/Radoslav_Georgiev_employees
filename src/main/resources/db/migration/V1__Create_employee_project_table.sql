CREATE TABLE employee_project (
                                  id BIGINT IDENTITY PRIMARY KEY,
                                  emp_id BIGINT NOT NULL,
                                  project_id BIGINT NOT NULL,
                                  date_from DATE NOT NULL,
                                  date_to DATE NULL
);
