INSERT INTO role(id, name) VALUES
(1, 'ADMIN'),
(2, 'JOURNALIST'),
(3, 'SUBSCRIBER');

INSERT INTO user(id, username, password) VALUES
(1, 'admin', '$2a$12$R2Yk6AGg9guPRvlRUi/MsOBwwY1no7ikjW/oO/tUiTBj8QCIr.w.a'),
(2, 'user1', '$2a$12$NDetGlcMW.s9NatomPv/Se/tVdvXI5Mu/oFSB6ckeS08647xYofNO'),
(3, 'journalist1', '$2a$12$NDetGlcMW.s9NatomPv/Se/tVdvXI5Mu/oFSB6ckeS08647xYofNO'),
(4, 'journalist2', '$2a$12$NDetGlcMW.s9NatomPv/Se/tVdvXI5Mu/oFSB6ckeS08647xYofNO');

INSERT INTO user_role(user_id, role_id) VALUES
(1, 1),
(2, 3),
(3, 2),
(4, 2);

INSERT INTO news (id, user_id, creation_date, title, text) VALUES
(1, 3, '2021-01-01', 'title1', 'text1'),
(2, 3, '2021-01-01', 'title2', 'text2'),
(3, 3, '2021-01-03', 'title3', 'text3'),
(4, 3, '2021-01-04', 'title4', 'text4'),
(5, 3, '2021-01-05', 'title5', 'text5'),
(6, 3, '2021-01-05', 'title5', 'text6');

INSERT INTO comments(id, news_id, creation_date, text, username) VALUES
(1, 1, '2021-01-01', 'comment1', 'user1'),
(2, 1, '2021-01-02', 'comment2', 'user2'),
(3, 1, '2021-01-03', 'comment3', 'user3'),
(4, 2, '2021-01-02', 'comment4', 'user1'),
(5, 2, '2021-01-03', 'comment5', 'user2'),
(6, 2, '2021-01-04', 'comment6', 'user3'),
(7, 3, '2021-01-03', 'comment7', 'user1'),
(8, 3, '2021-01-04', 'comment8', 'user2'),
(9, 3, '2021-01-05', 'comment9', 'user3'),
(10, 4, '2021-01-04', 'comment10', 'user1'),
(11, 4, '2021-01-05', 'comment11', 'user2'),
(12, 4, '2021-01-06', 'comment12', 'user3'),
(13, 5, '2021-01-05', 'comment13', 'user1'),
(14, 5, '2021-01-06', 'comment14', 'user2'),
(15, 5, '2021-01-07', 'comment15', 'user3');

