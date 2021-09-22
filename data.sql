CREATE TABLE news(
    id INT PRIMARY KEY AUTO_INCREMENT,
    creation_date DATE,
    text VARCHAR(20000),
    title VARCHAR(100)
);

CREATE TABLE IF NOT EXISTS comments(
    id INT PRIMARY KEY AUTO_INCREMENT,
    creation_date DATE,
    text VARCHAR(2000),
    username VARCHAR(100),
    news_id INT,
    FOREIGN KEY (news_id) REFERENCES news(id) ON DELETE CASCADE
);

INSERT INTO news (creation_date, text, title) VALUES
(CURRENT_DATE(), 'Children who do not get vaccinated will catch Covid-19 at some point because waning immunity means the virus will never stop circulating, Chris Whitty has said.', 'Unvaccinated children will catch Covid, says Whitty');

