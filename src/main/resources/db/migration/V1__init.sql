-- 1. Create parent tables first (no foreign keys)
CREATE TABLE members
(
    member_id  bigint auto_increment primary key,
    name       varchar(255) not null,
    created_at datetime(6)  not null
);

CREATE TABLE job_postings
(
    job_posting_id        bigint auto_increment primary key,
    title                 varchar(255),
    category              varchar(255),
    salaryInfo            varchar(100) null,
    location              varchar(255) null,
    preferredAgeGroup     varchar(50)  null,
    industry              varchar(100) null,
    brandName             varchar(100) null,
    workDays              varchar(50)  null,
    workHours             varchar(50)  null,
    workRegion            varchar(100) null,
    recruitmentConditions longtext     null,
    detailedDescription   longtext     null
);

-- 2. Create child tables that reference the parent tables
CREATE TABLE resumes
(
    resume_id             bigint auto_increment primary key,
    member_id             bigint       not null,
    mbti                  varchar(10)  null,
    education             varchar(255) null,
    preferentialTreatment varchar(255) null,
    experience            longtext     null,
    licensesAbilities     longtext     null,
    portfolioUrls         longtext     null,
    preferredConditions   longtext     null,
    selfIntroduction      longtext     null,
    skills                longtext     null,
    strengths             longtext     null,
    constraint UK_resumes_member_id unique (member_id),
    constraint FK_resumes_to_members foreign key (member_id) references members (member_id)
);

CREATE TABLE job_applications
(
    application_id  bigint auto_increment primary key,
    member_id       bigint        not null,
    job_posting_id  bigint        not null,
    applicationDate date          not null,
    companyName     varchar(100)  not null,
    status          varchar(20)   not null,
    constraint FK_job_applications_to_members foreign key (member_id) references members (member_id),
    constraint FK_job_applications_to_job_postings foreign key (job_posting_id) references job_postings (job_posting_id)
);

CREATE TABLE saved_job_postings
(
    saved_job_posting_id bigint auto_increment primary key,
    member_id            bigint not null,
    job_posting_id       bigint not null,
    created_at           datetime(6) not null,
    constraint FK_saved_postings_to_members foreign key (member_id) references members (member_id),
    constraint FK_saved_postings_to_job_postings foreign key (job_posting_id) references job_postings (job_posting_id)
);