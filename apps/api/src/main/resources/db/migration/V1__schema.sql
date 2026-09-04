CREATE TABLE users (
    id UUID PRIMARY KEY,
    email VARCHAR(320) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(32) NOT NULL DEFAULT 'STUDENT',
    tier VARCHAR(16) NOT NULL DEFAULT 'FREE',
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL
);

CREATE TABLE refresh_tokens (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    token_hash VARCHAR(128) NOT NULL UNIQUE,
    expires_at TIMESTAMPTZ NOT NULL,
    revoked_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL
);

CREATE INDEX idx_refresh_tokens_user ON refresh_tokens (user_id);

CREATE TABLE student_profiles (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL UNIQUE REFERENCES users (id) ON DELETE CASCADE,
    first_name VARCHAR(120) NOT NULL,
    last_name VARCHAR(120) NOT NULL,
    date_of_birth DATE,
    age_confirmed BOOLEAN NOT NULL DEFAULT FALSE,
    age_confirmed_at TIMESTAMPTZ,
    weekly_study_hours INTEGER NOT NULL DEFAULT 10,
    monthly_budget NUMERIC(12, 2),
    intensity VARCHAR(32) NOT NULL DEFAULT 'STANDARD',
    max_concurrent INTEGER NOT NULL DEFAULT 2,
    competency_willing BOOLEAN NOT NULL DEFAULT TRUE,
    pla_willing BOOLEAN NOT NULL DEFAULT FALSE,
    exam_willing BOOLEAN NOT NULL DEFAULT TRUE,
    target_graduation_date DATE,
    start_date DATE,
    unavailable_weeks TEXT,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL
);

CREATE TABLE consent_records (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    purpose VARCHAR(64) NOT NULL,
    granted BOOLEAN NOT NULL,
    policy_version VARCHAR(32) NOT NULL,
    acknowledgment_text TEXT,
    created_at TIMESTAMPTZ NOT NULL,
    withdrawn_at TIMESTAMPTZ
);

CREATE INDEX idx_consent_user ON consent_records (user_id);

CREATE TABLE institutions (
    id UUID PRIMARY KEY,
    code VARCHAR(32) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    short_name VARCHAR(120),
    tier VARCHAR(8) NOT NULL,
    delivery_model VARCHAR(32) NOT NULL,
    marketplace_visibility_level INTEGER NOT NULL DEFAULT 0,
    source_url TEXT,
    last_verified_at DATE,
    verified_by VARCHAR(120),
    verification_status VARCHAR(32) NOT NULL,
    notes TEXT,
    version INTEGER NOT NULL DEFAULT 1,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL
);

CREATE TABLE programs (
    id UUID PRIMARY KEY,
    institution_id UUID NOT NULL REFERENCES institutions (id),
    code VARCHAR(32) NOT NULL,
    name VARCHAR(255) NOT NULL,
    degree_family VARCHAR(64) NOT NULL,
    catalog_year VARCHAR(32) NOT NULL,
    total_credits INTEGER NOT NULL,
    source_url TEXT,
    last_verified_at DATE,
    verified_by VARCHAR(120),
    verification_status VARCHAR(32) NOT NULL,
    notes TEXT,
    version INTEGER NOT NULL DEFAULT 1,
    UNIQUE (institution_id, code, catalog_year)
);

CREATE TABLE requirement_slots (
    id UUID PRIMARY KEY,
    program_id UUID NOT NULL REFERENCES programs (id),
    slot_code VARCHAR(32) NOT NULL,
    title VARCHAR(255) NOT NULL,
    credits INTEGER NOT NULL,
    level VARCHAR(16) NOT NULL,
    residency_required BOOLEAN NOT NULL DEFAULT FALSE,
    sort_order INTEGER NOT NULL,
    group_name VARCHAR(64) NOT NULL,
    UNIQUE (program_id, slot_code)
);

CREATE INDEX idx_slots_program ON requirement_slots (program_id);

CREATE TABLE policy_rules (
    id UUID PRIMARY KEY,
    institution_id UUID NOT NULL REFERENCES institutions (id),
    program_id UUID REFERENCES programs (id),
    catalog_year VARCHAR(32) NOT NULL,
    rule_type VARCHAR(64) NOT NULL,
    rule_value VARCHAR(64) NOT NULL,
    unit VARCHAR(32) NOT NULL,
    effective_date DATE,
    expiration_date DATE,
    source_url TEXT,
    last_verified_at DATE,
    verified_by VARCHAR(120),
    verification_status VARCHAR(32) NOT NULL,
    notes TEXT,
    version INTEGER NOT NULL DEFAULT 1
);

CREATE INDEX idx_policy_program ON policy_rules (program_id);

CREATE TABLE credit_providers (
    id UUID PRIMARY KEY,
    code VARCHAR(32) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    delivery_platform VARCHAR(32) NOT NULL,
    evaluator VARCHAR(64) NOT NULL,
    default_hours_per_credit NUMERIC(6, 2) NOT NULL,
    pricing_model VARCHAR(32) NOT NULL,
    monthly_price NUMERIC(12, 2) NOT NULL DEFAULT 0,
    source_url TEXT,
    last_verified_at DATE,
    verified_by VARCHAR(120),
    verification_status VARCHAR(32) NOT NULL,
    version INTEGER NOT NULL DEFAULT 1
);

CREATE TABLE credit_opportunities (
    id UUID PRIMARY KEY,
    provider_id UUID NOT NULL REFERENCES credit_providers (id),
    code VARCHAR(64) NOT NULL UNIQUE,
    title VARCHAR(255) NOT NULL,
    opportunity_type VARCHAR(32) NOT NULL,
    ace_id VARCHAR(64),
    nccrs_id VARCHAR(64),
    credits INTEGER NOT NULL,
    level VARCHAR(16) NOT NULL,
    hours_per_credit NUMERIC(6, 2),
    effort_hours_override NUMERIC(6, 2),
    price NUMERIC(12, 2) NOT NULL DEFAULT 0,
    exam_fee NUMERIC(12, 2) NOT NULL DEFAULT 0,
    recommendation_start DATE,
    recommendation_expires DATE,
    source_url TEXT,
    last_verified_at DATE,
    verified_by VARCHAR(120),
    verification_status VARCHAR(32) NOT NULL,
    version INTEGER NOT NULL DEFAULT 1
);

CREATE TABLE equivalencies (
    id UUID PRIMARY KEY,
    source_type VARCHAR(32) NOT NULL,
    source_identifier VARCHAR(128) NOT NULL,
    institution_id UUID NOT NULL REFERENCES institutions (id),
    program_id UUID NOT NULL REFERENCES programs (id),
    catalog_year VARCHAR(32) NOT NULL,
    requirement_slot_id UUID NOT NULL REFERENCES requirement_slots (id),
    status VARCHAR(32) NOT NULL,
    source_url TEXT,
    last_verified_at DATE,
    verified_by VARCHAR(120),
    verification_status VARCHAR(32) NOT NULL,
    version INTEGER NOT NULL DEFAULT 1,
    UNIQUE (source_type, source_identifier, institution_id, program_id, catalog_year, requirement_slot_id)
);

CREATE INDEX idx_eq_lookup ON equivalencies (source_type, source_identifier, program_id, catalog_year);

CREATE TABLE academic_assets (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    source_type VARCHAR(32) NOT NULL,
    source_identifier VARCHAR(128),
    institution_name VARCHAR(255),
    course_code VARCHAR(64),
    course_title VARCHAR(255) NOT NULL,
    credits NUMERIC(6, 2) NOT NULL,
    grade VARCHAR(16),
    term VARCHAR(32),
    completion_date DATE,
    transcript_origin VARCHAR(255),
    original_learning_source VARCHAR(255),
    evaluator VARCHAR(128),
    ace_id VARCHAR(64),
    nccrs_id VARCHAR(64),
    verified_by_student BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL
);

CREATE INDEX idx_assets_user ON academic_assets (user_id);

CREATE TABLE degree_goals (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    degree_family VARCHAR(64) NOT NULL,
    target_institution_id UUID REFERENCES institutions (id),
    target_program_id UUID REFERENCES programs (id),
    created_at TIMESTAMPTZ NOT NULL
);

CREATE TABLE degree_plans (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    goal_id UUID NOT NULL REFERENCES degree_goals (id),
    program_id UUID NOT NULL REFERENCES programs (id),
    catalog_year VARCHAR(32) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL
);

CREATE TABLE plan_matches (
    id UUID PRIMARY KEY,
    plan_id UUID NOT NULL REFERENCES degree_plans (id) ON DELETE CASCADE,
    asset_id UUID REFERENCES academic_assets (id),
    equivalency_id UUID REFERENCES equivalencies (id),
    slot_id UUID REFERENCES requirement_slots (id),
    classification VARCHAR(32) NOT NULL,
    reason TEXT,
    confidence VARCHAR(32) NOT NULL,
    policy_rule_ids TEXT
);

CREATE TABLE plan_scenarios (
    id UUID PRIMARY KEY,
    plan_id UUID NOT NULL REFERENCES degree_plans (id) ON DELETE CASCADE,
    scenario_type VARCHAR(32) NOT NULL,
    projected_completion_date DATE,
    total_cost NUMERIC(12, 2),
    weeks_remaining INTEGER,
    subscription_months INTEGER,
    course_count INTEGER,
    feasibility VARCHAR(32),
    confidence VARCHAR(32),
    blocked_reason TEXT,
    stale_blocked BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMPTZ NOT NULL
);

CREATE TABLE plan_courses (
    id UUID PRIMARY KEY,
    scenario_id UUID NOT NULL REFERENCES plan_scenarios (id) ON DELETE CASCADE,
    slot_id UUID REFERENCES requirement_slots (id),
    opportunity_id UUID REFERENCES credit_opportunities (id),
    start_week INTEGER,
    end_week INTEGER,
    cost NUMERIC(12, 2),
    provider_code VARCHAR(32)
);

CREATE TABLE cost_items (
    id UUID PRIMARY KEY,
    scenario_id UUID NOT NULL REFERENCES plan_scenarios (id) ON DELETE CASCADE,
    category VARCHAR(64) NOT NULL,
    label VARCHAR(255) NOT NULL,
    amount NUMERIC(12, 2) NOT NULL,
    as_of DATE,
    source_url TEXT
);

CREATE TABLE recommendations (
    id UUID PRIMARY KEY,
    plan_id UUID NOT NULL REFERENCES degree_plans (id) ON DELETE CASCADE,
    opportunity_id UUID REFERENCES credit_opportunities (id),
    rank INTEGER NOT NULL,
    rationale TEXT NOT NULL,
    credits INTEGER,
    estimated_hours NUMERIC(8, 2),
    applies_to_slot VARCHAR(64),
    weeks_saved INTEGER
);

CREATE TABLE audit_events (
    id UUID PRIMARY KEY,
    actor_id UUID,
    entity_type VARCHAR(64) NOT NULL,
    entity_id UUID,
    action VARCHAR(64) NOT NULL,
    before_value TEXT,
    after_value TEXT,
    source_url TEXT,
    created_at TIMESTAMPTZ NOT NULL
);

CREATE INDEX idx_audit_entity ON audit_events (entity_type, entity_id);

-- Class 2: prior-learning schema only
CREATE TABLE pla_assessments (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    title VARCHAR(255) NOT NULL,
    estimated_credits INTEGER,
    estimated_cost NUMERIC(12, 2),
    estimated_hours NUMERIC(8, 2),
    status VARCHAR(32) NOT NULL DEFAULT 'STUB',
    created_at TIMESTAMPTZ NOT NULL
);

-- Class 2: payments abstraction schema only
CREATE TABLE payment_intents (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    product_code VARCHAR(64) NOT NULL,
    amount NUMERIC(12, 2) NOT NULL,
    currency VARCHAR(8) NOT NULL DEFAULT 'USD',
    status VARCHAR(32) NOT NULL DEFAULT 'STUB',
    provider VARCHAR(32) NOT NULL DEFAULT 'NONE',
    created_at TIMESTAMPTZ NOT NULL
);

-- Class 2: institution marketplace visibility levels, schema fields only
CREATE TABLE institution_marketplace_settings (
    id UUID PRIMARY KEY,
    institution_id UUID NOT NULL UNIQUE REFERENCES institutions (id),
    visibility_level INTEGER NOT NULL DEFAULT 0,
    notes TEXT
);
