-- job_postings 테이블의 category 컬럼에서 NOT NULL 제약조건을 제거합니다.
ALTER TABLE job_postings MODIFY COLUMN category VARCHAR(255) NULL;

-- job_postings 테이블의 title 컬럼에서 NOT NULL 제약조건을 제거합니다.
ALTER TABLE job_postings MODIFY COLUMN title VARCHAR(255) NULL;