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

CREATE TABLE IF NOT EXISTS user
(
    id INT PRIMARY KEY AUTO_INCREMENT,
    username  VARCHAR(100),
    password VARCHAR(1000)
);

CREATE TABLE IF NOT EXISTS role
(
    id INT PRIMARY KEY AUTO_INCREMENT,
    name  VARCHAR(100)
);

CREATE TABLE IF NOT EXISTS user_role
(
    user_id INT,
    role_id INT,
    PRIMARY KEY(user_id, role_id),
    FOREIGN KEY(user_id) REFERENCES user(id) ON DELETE CASCADE,
    FOREIGN KEY(role_id) REFERENCES  user(id) ON DELETE CASCADE
);