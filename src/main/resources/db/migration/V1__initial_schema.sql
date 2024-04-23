-- Create the table for monitoring jobs
CREATE TABLE monitoring_job (
                                id SERIAL PRIMARY KEY,
                                job_name VARCHAR(255) UNIQUE NOT NULL,
                                url VARCHAR(255) NOT NULL,
                                interval_in_seconds INTEGER NOT NULL,
                                creation_date TIMESTAMP NOT NULL
);


-- Create the table for monitoring results
CREATE TABLE monitoring_job_result (
                                   id SERIAL PRIMARY KEY,
                                   job_id INTEGER NOT NULL,
                                   status_name VARCHAR(255) NOT NULL,
                                   result VARCHAR(255) NOT NULL,
                                   response_time_in_ms INTEGER NOT NULL,
                                   info VARCHAR(255),
                                   creation_date TIMESTAMP NOT NULL ,
                                   FOREIGN KEY (job_id) REFERENCES monitoring_job(id)
);

