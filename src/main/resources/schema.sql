CREATE TABLE IF NOT EXISTS news
(
    id            INT PRIMARY KEY AUTO_INCREMENT,
    creation_date DATE DEFAULT current_date(),
    title         VARCHAR(150),
    text          VARCHAR(20000)
);

CREATE TABLE IF NOT EXISTS comments
(
    id            INT PRIMARY KEY AUTO_INCREMENT,
    creation_date DATE DEFAULT CURRENT_DATE(),
    text          VARCHAR(2000),
    username      VARCHAR(100),
    news_id       INT,
    FOREIGN KEY (news_id) REFERENCES news (id) ON DELETE CASCADE
);