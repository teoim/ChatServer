INSERT INTO messages(timestamp,content,txt_from,txt_to) VALUES ('2024-06-26 14:40:01.548','pug life','hook@gogo.com','generalChat');
INSERT INTO messages(timestamp,content,txt_from,txt_to) VALUES ('2024-06-26 19:23:26.992','hi hook','teo@gogo.com','hook@gogo.com');
INSERT INTO messages(timestamp,content,txt_from,txt_to) VALUES ('2024-06-26 19:33:09.261','test timestamp 8','hook@gogo.com','teo@gogo.com');

INSERT INTO users(nick, name, surname, dob, phonenr, email, bio, password, profile_photo_link) VALUES ('Cooleanu' , 'Hook'  , 'Captain', '1990-01-01', '0040123456789', 'hook@gogo.com', 'I like chimcken',  'pass', '/images/icons/user-64.png');
INSERT INTO users(nick, name, surname, dob, phonenr, email, bio, password, profile_photo_link) VALUES ('GoldenBoy', 'Junior', 'Boy'    , '1990-01-01', '0040123456799', 'jr@gogo.com'  , 'I like pasta.'  ,  'pass', '/images/icons/user-64.png');
INSERT INTO users(nick, name, surname, dob, phonenr, email, bio, password, profile_photo_link) VALUES ('Teo'      , 'Teodor', 'Ionut'  , '1990-01-01', '0040123456755', 'teo@gogo.com'  , 'I like bread'  ,  'pass', '/images/icons/user-64.png');


INSERT INTO roles(name) VALUES ('ADMIN');
INSERT INTO roles(name) VALUES ('STAFF');
INSERT INTO roles(name) VALUES ('USER');


INSERT INTO user_roles(user_email, role_name) VALUES ('hook@gogo.com', 'ADMIN');
INSERT INTO user_roles(user_email, role_name) VALUES ('jr@gogo.com', 'STAFF');
INSERT INTO user_roles(user_email, role_name) VALUES ('teo@gogo.com', 'USER');


 INSERT INTO user_relationship(user_id, friend_id, status) VALUES ('jr@gogo.com', 'hook@gogo.com', 'FRIEND');
 INSERT INTO user_relationship(user_id, friend_id, status) VALUES ('jr@gogo.com', 'teo@gogo.com', 'FRIEND');
 INSERT INTO user_relationship(user_id, friend_id, status) VALUES ('teo@gogo.com', 'jr@gogo.com', 'FRIEND');
 INSERT INTO user_relationship(user_id, friend_id, status) VALUES ('teo@gogo.com', 'hook@gogo.com', 'FRIEND');
 INSERT INTO user_relationship(user_id, friend_id, status) VALUES ('hook@gogo.com', 'teo@gogo.com', 'FRIEND');
