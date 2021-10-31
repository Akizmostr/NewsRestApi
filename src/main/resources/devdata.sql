INSERT INTO news (id, creation_date, title, text) VALUES
(1, '2021-09-22', 'Unvaccinated children will catch Covid, says Whitty', 'Children who do not get vaccinated will catch Covid-19 at some point because waning immunity means the virus will never stop circulating, Chris Whitty has said.'),
(2, '2021-09-23', 'US Haiti envoy quits over ''inhumane'' deportations', 'The US special envoy for Haiti has resigned in protest over the deportation of Haitian migrants.'),
(3, '2021-09-17', 'High-speed internet via airborne beams of light', 'A novel way of delivering high-speed internet via beams of light through the air has successfully transmitted data across the Congo River.'),
(4, '2021-09-21', 'Why Iceland’s volcanoes may aid future Mars exploration', 'Scientists are using Iceland as a testbed for deploying drone technology that they hope may one day feature on missions exploring Mars.'),
(5, '2021-09-23', 'EU rules to force USB-C chargers for all phones','Manufacturers will be forced to create a universal charging solution for phones and small electronic devices, under a new rule proposed by the European Commission (EC).'),
(6, '2021-09-22', 'NFT-based fantasy football card firm raises $680m', 'French firm Sorare, which sells football trading cards in the form of non-fungible tokens (NFTs), has raised $680m (£498m).'),
(7, '2021-09-22', 'US will be ‘arsenal of vaccines’ to world - Biden', 'The US is to donate 500 million more doses of the Pfizer vaccine to developing nations from next year. President Joe Biden made the pledge at a virtual Covid-19 summit on the sidelines of the UN General Assembly, promising an "arsenal of vaccines". The additional jabs will see the total US commitment on vaccine sharing exceed one billion jabs.'),
(8, '2021-09-18', 'United States sorry for Kabul drone strike that killed children, not terrorists', 'The US military has admitted that it made a mistake when it killed ten innocent members of the same family in a drone strike in Kabul last month.'),
(9, '2021-09-18', 'Thousands of migrants on treacherous path to life in US', 'President Biden was facing a new crisis after more than 10,000 migrants set up camp on the American side of the border with Mexico.'),
(10,'2021-09-23', 'У РБ и РФ будет новое соглашение о научном сотрудничестве', 'Беларусь и Россия подготовят новое межправительственное двухстороннее соглашение о научно-техническом сотрудничестве. Об этом сообщается на сайте Государственного комитета по науке и технологиям (ГКНТ) Беларуси.'),
(11, '2021-09-19', 'Казахстан и Беларусь будут создавать вооружение для усиления ОДКБ', 'Оборонно-промышленный комплекс Беларуси и Казахстана будет использован в наращивании возможностей по обеспечению безопасности границ ОДКБ на фоне ситуации в Афганистане, заявил вице-премьер Юрий Борисов'),
(12, '2021-09-23', 'Борис Джонсон заявил, что может взять имя в честь бога северного ветра', 'Выступление премьер-министра Британии было посвящено проблемам экологии и борьбе с изменениями климата, однако выстроил он его в шуточном стиле.'),
(13, '2021-08-15', 'В МЧС за сутки поступило 5 сообщений о потерявшихся в лесу', 'Работниками МЧС обнаружены 2 человека. Не найдены 5 человек в Столинском районе Брестской области, поиски которых продолжаются.');

INSERT INTO comments(id, creation_date, text, username, news_id) VALUES
(1, '2021-09-22', 'stay safe', 'jon21', 1),
(2, '2021-09-22', 'agree', 'lolsvc', 1),
(3, '2021-09-23', 'poor children', 'molj', 1),
(4, '2023-04-19', 'who watches it in 2023?', 'guyfromfuture', 1),
(5, '2021-09-23', 'I hate migrants', 'uslover51', 2),
(6, '2021-09-25', 'deport them all!', 'uslover51', 2),
(7, '2021-09-18', 'future is here :)', 'ltech22', 3),
(8, '2021-09-24', 'шутник', 'kolyain1995', 12);

INSERT INTO role(id, name) VALUES
(1, 'ADMIN'),
(2, 'JOURNALIST'),
(3, 'SUBSCRIBER');

INSERT INTO user(id, username, password) VALUES
(1, 'admin', '$2a$12$R2Yk6AGg9guPRvlRUi/MsOBwwY1no7ikjW/oO/tUiTBj8QCIr.w.a'),
(2, 'user1', '$2a$12$NDetGlcMW.s9NatomPv/Se/tVdvXI5Mu/oFSB6ckeS08647xYofNO');

INSERT INTO user_role(user_id, role_id) VALUES
(1, 1),
(2, 3);