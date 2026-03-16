CREATE TABLE IF NOT EXISTS transaction_statements (
    statement_id    BIGINT AUTO_INCREMENT PRIMARY KEY,
    statement_no    VARCHAR(50)    NOT NULL UNIQUE,
    issue_date      DATE           NOT NULL,
    customer_name   VARCHAR(100)   NOT NULL,
    customer_addr   VARCHAR(255),
    customer_tel    VARCHAR(50),
    customer_biz_no VARCHAR(50),
    total_amount    DECIMAL(15,2)  NOT NULL DEFAULT 0,
    tax_amount      DECIMAL(15,2)  NOT NULL DEFAULT 0,
    grand_total     DECIMAL(15,2)  NOT NULL DEFAULT 0,
    notes           TEXT,
    created_at      DATETIME       NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS transaction_statement_items (
    item_id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    statement_id    BIGINT         NOT NULL,
    item_name       VARCHAR(200)   NOT NULL,
    quantity        INT            NOT NULL DEFAULT 1,
    unit_price      DECIMAL(15,2)  NOT NULL DEFAULT 0,
    amount          DECIMAL(15,2)  NOT NULL DEFAULT 0,
    FOREIGN KEY (statement_id) REFERENCES transaction_statements(statement_id) ON DELETE CASCADE
);
