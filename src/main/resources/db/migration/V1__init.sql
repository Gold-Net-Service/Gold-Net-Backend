create table members
(
    created_at date         not null,
    member_id  bigint auto_increment
        primary key,
    name       varchar(255) not null
);

create table job_postings
(
    job_posting_id        bigint auto_increment
        primary key,
    preferredAgeGroup     varchar(50)  null,
    workDays              varchar(50)  null,
    workHours             varchar(50)  null,
    brandName             varchar(100) null,
    industry              varchar(100) null,
    salaryInfo            varchar(100) null,
    workRegion            varchar(100) null,
    category              varchar(255) not null,
    location              varchar(255) null,
    title                 varchar(255) not null,
    detailedDescription   longtext     null,
    recruitmentConditions longtext     null
);

create table job_applications
(
    applicationDate date                                                    not null,
    application_id  bigint auto_increment
        primary key,
    member_id       bigint                                                  not null,
    companyName     varchar(100)                                            not null,
    status          enum ('ACCEPTED', 'REJECTED', 'REVIEWING', 'SUBMITTED') not null,
    constraint FKfstedvlyjmgg0aajakil7oqn2
        foreign key (member_id) references goldnet.members (member_id)
);


create table resumes
(
    member_id             bigint       not null,
    resume_id             bigint auto_increment
        primary key,
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
    constraint UKp3p6k5m26935qawlrxsrx0722
        unique (member_id),
    constraint FKh7davpmtehs7xhuiwb3aetfvn
        foreign key (member_id) references goldnet.members (member_id)
);

create table saved_job_postings
(
    created_at           date   not null,
    job_posting_id       bigint not null,
    member_id            bigint not null,
    saved_job_posting_id bigint auto_increment
        primary key,
    constraint FKa4fuy0o2rvxnxkjil9b53f3nw
        foreign key (job_posting_id) references goldnet.job_postings (job_posting_id),
    constraint FKrkq3698k64jiw0kxqj521ryga
        foreign key (member_id) references goldnet.members (member_id)
);
