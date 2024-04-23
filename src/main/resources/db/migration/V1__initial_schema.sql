-- Create the table for monitoring jobs
CREATE TABLE monitoring_job (
                                id SERIAL PRIMARY KEY,
                                name VARCHAR(255) UNIQUE NOT NULL,
                                url VARCHAR(255) UNIQUE NOT NULL,
                                interval_in_minutes INTEGER UNIQUE NOT NULL,
                                creationDate TIMESTAMP NOT NULL
);


-- Create the table for monitoring results
CREATE TABLE monitoring_job_result (
                                   id SERIAL PRIMARY KEY,
                                   job_id INTEGER NOT NULL,
                                   status_name VARCHAR(255) NOT NULL,
                                   result VARCHAR(255) NOT NULL,
                                   response_time INTEGER NOT NULL,
                                   error_message VARCHAR(255),
                                   creationDate TIMESTAMP NOT NULL ,
                                   FOREIGN KEY (job_id) REFERENCES monitoring_job(id)
);

CREATE INDEX job_result_index ON monitoring_job_result (job_id, result);
CREATE INDEX job_status_index ON monitoring_job_result (job_id, status_name);
CREATE INDEX job_result_status_index ON monitoring_job_result (status_name, result);
