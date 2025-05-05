-- Insert emergency groups (no foreign key dependencies)
INSERT INTO emergency_groups (name)
VALUES ('Group A'),
       ('Group B'),
       ('Group C'),
       ('Group D'),
       ('Group E');

-- Insert households (no foreign key dependencies)
INSERT INTO households (name, longitude, latitude, emergency_group_id)
VALUES ('The Smiths', 10.75, 59.91, 1),
       ('Team Rocket', 11.00, 60.10, null),
       ('The Johnsons', 10.85, 59.95, 1),
       ('The Waltons', 11.20, 60.30, null),
       ('The Doyles', 10.60, 59.80, null);

-- Insert items (no foreign key dependencies)
INSERT INTO items (name, unit, calories, type)
VALUES ('Bottled Water', 'L', 0, 'drink'),
       ('Canned Beans', 'g', 120, 'food'),
       ('Flashlight', 'piece', 0, 'accessories'),
       ('Tuna Can', 'g', 150, 'food'),
       ('Soda Can', 'L', 200, 'drink'),
       ('First Aid Kit', 'piece', 0, 'accessories'),
       ('Canned Soup', 'g', 250, 'food'),
       ('Battery Pack', 'piece', 0, 'accessories'),
       ('Granola Bar', 'g', 100, 'food'),
       ('Cracker', 'piece', 10, 'food');

-- Insert users (references households)
INSERT INTO users (email, name, household_id, password, role)
VALUES ('admin@example.com', 'Alice Admin', 1, '$2b$12$SdWhhsz0kOz1/sv.PekCLe3FZTSBYsBbhEHHuP/g3rS9OC7.1uUB2', 'role_admin'),
       ('user@example.com', 'Bob User', 1, '$2b$12$gPjM8ZKJlPsl4qynzZkhMusekGptDpjEpFeteOTOsdJR5i6of9Nye', 'role_normal'),
       ('superadmin@example.com', 'Carol Superadmin', 2, '$2b$12$ifTYfS447fcgS.KIYQKdgeS13xkrJwFMm1kZebFSapTHNhL4Jc7he',
        'role_super_admin'),
       ('david@example.com', 'David Nolan', 1, '$2b$12$eyZGrA0HACsmJ/F5x1ryRuuK8vwrDujz2fw7mMToGpbgSo4uQITh5y',
        'role_normal'),
       ('emily@example.com', 'Emily Harper', 3, '$2b$12$Gse24F6aQ2mfPi8.fURFdCuBDWx9d/YzAi8NV8M70LxaOq0hg5p1W',
        'role_normal'),
       ('john@example.com', 'John Doe', 2, '$2b$12$vlpwmP5fh4DZj8tJIQToZQHgSzHgs0pf.n53gUqFjtQb4B54aKSm8', 'role_normal'),
       ('lucas@example.com', 'Lucas Reed', 4, '$2b$12$dzMdu7D9C8.XVuYsa2qzHFeIRuXtM0mf8Vq2oZQThs41v/ie0pA/C', 'role_normal'),
       ('sarah@example.com', 'Sarah Williams', 5, '$2b$12$JlgqpuU/6eB6V44nGFlY6q7BLXXqLbrcXvTz9wm2nKXZSHfw8tKxy',
        'role_normal'),
       ('nina@example.com', 'Nina Scott', 3, '$2b$12$gAcm80tB8Lkx1qgykVve0O.TdQ3.tIGf3pAmn.DpSKhyPHe7eopby', 'role_normal'),
       ('michael@example.com', 'Michael King', 2, '$2b$12$y.eu5T/Rff8R3YoBrJlknSz5u2zVvH1pz/jtt6y2Q6jRVs8rZdVX2',
        'role_normal');

-- Insert non-user members (references households)
INSERT INTO non_user_members (name, type, household_id)
VALUES ('Charlie', 'child', 1),
       ('Doggo', 'animal', 1),
       ('Luna', 'animal', 2),
       ('Max', 'child', 3),
       ('Simba', 'animal', 4),
       ('Daisy', 'child', 5);

-- Insert storage items (references households and items)
INSERT INTO storage_items (expiration_date, quantity, household_id, item_id)
VALUES ('2025-12-31 00:00:00', 10, 1, 1),
       ('2024-09-01 00:00:00', 5, 1, 2),
       ('2030-01-01 00:00:00', 2, 1, 3),
       ('2024-08-15 00:00:00', 20, 2, 4),
       ('2024-11-01 00:00:00', 15, 2, 5),
       ('2023-10-01 00:00:00', 3, 3, 6),
       ('2025-01-20 00:00:00', 8, 3, 7),
       ('2025-07-10 00:00:00', 50, 4, 8),
       ('2024-06-30 00:00:00', 6, 5, 9),
       ('2024-12-15 00:00:00', 10, 1, 10);

-- Insert points of interest (no foreign key dependencies)
INSERT INTO points_of_interest (longitude, latitude, type, opens_at, closes_at, contact_number, description)
VALUES (10.76, 59.91, 'shelter', '08:00:00', '18:00:00', '+47 123 45 678', 'A safe place to rest.'),
       (10.80, 59.90, 'defibrillator', NULL, NULL, NULL, 'Publicly accessible AED.'),
       (10.90, 59.95, 'food_central', '09:00:00', '20:00:00', '+47 987 65 432', 'Distribution point for food supplies.'),
       (10.85, 59.85, 'water_station', NULL, NULL, NULL, 'Source of clean drinking water.'),
       (10.95, 60.05, 'hospital', '00:00:00', '23:59:59', '+47 555 12 121', 'Medical facility.'),
       (10.70, 59.88, 'meeting_place', '10:00:00', '17:00:00', '+47 222 33 444', 'Community gathering point.');

-- Insert affected areas (no foreign key dependencies)
INSERT INTO affected_areas (longitude, latitude, high_danger_radius_km, medium_danger_radius_km, low_danger_radius_km, severity_level, description, start_time)
VALUES (10.77, 59.92, 1, 2, 3, 3, 'Evacuate immediately due to chemical spill.', '2023-10-01 12:00:00'),
       (10.90, 59.95, 2, 4, 7, 2, 'Flooding risk, move to higher ground.', '2023-10-02 14:00:00'),
       (10.85, 60.00, 3, 5.2, 5.7, 1, 'Tornado alert, stay inside.', '2023-10-03 16:00:00');

-- Insert join household request (references users and households)
INSERT INTO join_household_requests (user_id, household_id)
VALUES (2, 2),
       (3, 1),
       (2, 3),
       (1, 4);

-- Insert general info (no foreign key dependencies)
INSERT INTO general_info (theme, title, content) VALUES
                                                     ('BEFORE_CRISIS', 'Create an Emergency Plan', 'Make sure everyone in your household knows the emergency plan, including meeting points and emergency contacts.'),
                                                     ('BEFORE_CRISIS', 'Emergency Supplies', 'Store food, water, medicine, flashlights, and batteries that can last at least 72 hours.'),
                                                     ('DURING_CRISIS', 'Stay Informed', 'Listen to official updates from local authorities via radio, TV, or trusted apps.'),
                                                     ('DURING_CRISIS', 'Shelter in Place', 'If advised, stay indoors and away from windows. Use your emergency kit.'),
                                                     ('AFTER_CRISIS', 'Check for Injuries', 'Administer first aid if needed and call emergency services for serious injuries.'),
                                                     ('AFTER_CRISIS', 'Report Damages', 'Contact your insurance provider and local authorities to report damage or unsafe conditions.');


-- Insert emergency group invitations (references households and emergency groups)
INSERT INTO emergency_group_invitations (household_id, emergency_group_id)
VALUES (2, 2),
       (2, 4),
       (5, 4);