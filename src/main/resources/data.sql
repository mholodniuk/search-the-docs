-- users
INSERT INTO public.users(username, display_name, email, "password")
VALUES('maciek', 'Maciej Hołodniuk', 'maciekholo@gmail.com', 'assword');

INSERT INTO public.users(username, display_name, email, "password")
VALUES('adam', 'Adam Kowalski', 'adamkowal@gmail.com', 'pass');

-- rooms
INSERT INTO public.rooms
("name", private, owner_id, created_at, modified_at)
VALUES('room', false, 1, NOW(), null);

-- file storages
INSERT INTO public.file_locations
(storage_type, file_path)
VALUES('LOCAL', '/tmp/s3/mock/Bazy_danych_projekt.pdf');

INSERT INTO public.file_locations
(storage_type, file_path)
VALUES('LOCAL', '/tmp/s3/mock/PRACA-INŻYNIERSKA.docx');

-- documents
INSERT INTO public.documents
(id, "name", tags, content_type, room_id, owner_id, file_location_id, uploaded_at)
VALUES(gen_random_uuid(), 'Bazy danych projekt', '{}'::text[], 'application/pdf', 1, 1, 2, NOW());

INSERT INTO public.documents
(id, "name", tags, content_type, room_id, owner_id, file_location_id, uploaded_at)
VALUES(gen_random_uuid(), 'PRACA-INŻYNIERSKA.docx', '{}'::text[], 'application/vnd.openxmlformats-officedocument.wordprocessingml.document', 1, 1, 1, NOW());


-- access keys
INSERT INTO public.access_keys
(id, "name", rights, valid_from, valid_to, room_id, issuer_id, participant_id)
VALUES(gen_random_uuid(), 'test', 'VIEWER', NOW(), NULL, 1, 1, 2);