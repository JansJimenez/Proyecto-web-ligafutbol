CREATE TABLE technical_staff (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    team_id BIGINT NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    staff_role VARCHAR(32) NOT NULL,
    contact_email VARCHAR(254),
    phone VARCHAR(30),
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    CONSTRAINT fk_technical_staff_team FOREIGN KEY (team_id) REFERENCES teams(id) ON DELETE NO ACTION,
    CONSTRAINT ck_technical_staff_role CHECK (
        staff_role IN ('HEAD_COACH', 'ASSISTANT_COACH', 'GOALKEEPER_COACH', 'FITNESS_COACH',
            'PHYSIOTHERAPIST', 'DELEGATE', 'OTHER')
    ),
    CONSTRAINT ck_technical_staff_status CHECK (status IN ('ACTIVE', 'INACTIVE'))
);

CREATE INDEX ix_technical_staff_team_status ON technical_staff (team_id, status);
