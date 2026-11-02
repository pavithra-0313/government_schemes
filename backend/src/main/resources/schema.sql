-- Database schema for Student Scholarship and Education Scheme Finder
-- Supports MySQL 8 / H2 compatibility

CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL DEFAULT 'USER',
    preferred_language VARCHAR(10) DEFAULT 'en',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS student_profiles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,
    date_of_birth DATE,
    gender VARCHAR(20),
    state VARCHAR(100),
    district VARCHAR(100),
    community_category VARCHAR(50),
    religion_minority_flag BOOLEAN DEFAULT FALSE,
    annual_family_income DOUBLE,
    disability_status BOOLEAN DEFAULT FALSE,
    single_girl_child BOOLEAN DEFAULT FALSE,
    first_graduate BOOLEAN DEFAULT FALSE,
    orphan_or_single_parent BOOLEAN DEFAULT FALSE,
    hosteller BOOLEAN DEFAULT FALSE,
    education_level VARCHAR(50) NOT NULL,
    current_class_or_year VARCHAR(50),
    course VARCHAR(255),
    stream VARCHAR(255),
    institution_name VARCHAR(255),
    institution_type VARCHAR(50),
    institution_location_type VARCHAR(50),
    board_or_university VARCHAR(255),
    previous_exam_percentage DOUBLE,
    entrance_exam_score DOUBLE,
    admission_type VARCHAR(50),
    CONSTRAINT fk_profile_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS schemes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name_en VARCHAR(500) NOT NULL,
    name_ta VARCHAR(500) NOT NULL,
    description_en TEXT,
    description_ta TEXT,
    benefits TEXT,
    benefit_amount DOUBLE,
    benefit_type VARCHAR(100) NOT NULL,
    department VARCHAR(255),
    level VARCHAR(50) NOT NULL,
    state VARCHAR(100),
    apply_link VARCHAR(500),
    open_date DATE,
    last_date DATE,
    status VARCHAR(50) DEFAULT 'ACTIVE',
    renewal_required BOOLEAN DEFAULT FALSE
);

CREATE TABLE IF NOT EXISTS scheme_education_levels (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    scheme_id BIGINT NOT NULL,
    education_level VARCHAR(50) NOT NULL,
    CONSTRAINT fk_sel_scheme FOREIGN KEY (scheme_id) REFERENCES schemes(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS scheme_eligibility_rules (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    scheme_id BIGINT NOT NULL,
    min_age INT,
    max_age INT,
    max_income DOUBLE,
    gender VARCHAR(20),
    community_category VARCHAR(100),
    minority_required BOOLEAN,
    min_percentage DOUBLE,
    institution_type VARCHAR(50),
    institution_location_type VARCHAR(50),
    course_stream VARCHAR(255),
    state VARCHAR(100),
    disability_required BOOLEAN,
    single_girl_child_required BOOLEAN,
    first_graduate_required BOOLEAN,
    govt_school_only BOOLEAN,
    CONSTRAINT fk_ser_scheme FOREIGN KEY (scheme_id) REFERENCES schemes(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS documents (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name_en VARCHAR(255) NOT NULL,
    name_ta VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS scheme_documents (
    scheme_id BIGINT NOT NULL,
    document_id BIGINT NOT NULL,
    PRIMARY KEY (scheme_id, document_id),
    CONSTRAINT fk_sd_scheme FOREIGN KEY (scheme_id) REFERENCES schemes(id) ON DELETE CASCADE,
    CONSTRAINT fk_sd_document FOREIGN KEY (document_id) REFERENCES documents(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS saved_schemes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    scheme_id BIGINT NOT NULL,
    application_status VARCHAR(50) NOT NULL DEFAULT 'Saved',
    saved_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_ss_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_ss_scheme FOREIGN KEY (scheme_id) REFERENCES schemes(id) ON DELETE CASCADE,
    CONSTRAINT uq_user_scheme UNIQUE (user_id, scheme_id)
);

CREATE TABLE IF NOT EXISTS document_checklist (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    scheme_id BIGINT NOT NULL,
    document_id BIGINT NOT NULL,
    is_ready BOOLEAN DEFAULT FALSE,
    CONSTRAINT fk_dc_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_dc_scheme FOREIGN KEY (scheme_id) REFERENCES schemes(id) ON DELETE CASCADE,
    CONSTRAINT fk_dc_doc FOREIGN KEY (document_id) REFERENCES documents(id) ON DELETE CASCADE,
    CONSTRAINT uq_user_scheme_doc UNIQUE (user_id, scheme_id, document_id)
);

