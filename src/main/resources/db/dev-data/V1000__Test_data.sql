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
       ('Cracker', 'piece', 10, 'food'),
       ('Rice Pack', 'kg', 3600, 'food'),
       ('Portable Stove', 'piece', 0, 'accessories'),
       ('Blanket', 'piece', 0, 'accessories'),
       ('Energy Drink', 'L', 150, 'drink'),
       ('Peanut Butter Jar', 'g', 600, 'food'),
       ('Multivitamin Pack', 'piece', 0, 'accessories');

-- Insert users (references households)
INSERT INTO users (email, name, household_id, password, role, verified)
VALUES ('admin@example.com', 'Alice Admin', 1, '$2b$12$SdWhhsz0kOz1/sv.PekCLe3FZTSBYsBbhEHHuP/g3rS9OC7.1uUB2', 'role_admin', TRUE),
       ('user@example.com', 'Bob User', 1, '$2b$12$gPjM8ZKJlPsl4qynzZkhMusekGptDpjEpFeteOTOsdJR5i6of9Nye', 'role_normal', TRUE),
       ('superadmin@example.com', 'Carol Superadmin', 2, '$2b$12$ifTYfS447fcgS.KIYQKdgeS13xkrJwFMm1kZebFSapTHNhL4Jc7he',
        'role_super_admin', TRUE),
       ('david@example.com', 'David Nolan', 1, '$2b$12$eyZGrA0HACsmJ/F5x1ryRuuK8vwrDujz2fw7mMToGpbgSo4uQITh5y',
        'role_normal', TRUE),
       ('emily@example.com', 'Emily Harper', 3, '$2b$12$Gse24F6aQ2mfPi8.fURFdCuBDWx9d/YzAi8NV8M70LxaOq0hg5p1W',
        'role_normal', TRUE),
       ('john@example.com', 'John Doe', 2, '$2b$12$vlpwmP5fh4DZj8tJIQToZQHgSzHgs0pf.n53gUqFjtQb4B54aKSm8', 'role_normal', TRUE),
       ('lucas@example.com', 'Lucas Reed', 4, '$2b$12$dzMdu7D9C8.XVuYsa2qzHFeIRuXtM0mf8Vq2oZQThs41v/ie0pA/C', 'role_normal', TRUE),
       ('sarah@example.com', 'Sarah Williams', 5, '$2b$12$JlgqpuU/6eB6V44nGFlY6q7BLXXqLbrcXvTz9wm2nKXZSHfw8tKxy',
        'role_normal', TRUE),
       ('nina@example.com', 'Nina Scott', 3, '$2b$12$gAcm80tB8Lkx1qgykVve0O.TdQ3.tIGf3pAmn.DpSKhyPHe7eopby', 'role_normal', TRUE),
       ('michael@example.com', 'Michael King', 2, '$2b$12$y.eu5T/Rff8R3YoBrJlknSz5u2zVvH1pz/jtt6y2Q6jRVs8rZdVX2',
        'role_normal', TRUE);

-- Insert non-user members (references households)
INSERT INTO non_user_members (name, type, household_id)
VALUES ('Charlie', 'child', 1),
       ('Doggo', 'animal', 1),
       ('Luna', 'animal', 2),
       ('Max', 'child', 3),
       ('Simba', 'animal', 4),
       ('Daisy', 'child', 5);

-- Insert storage items (references households and items)
INSERT INTO storage_items (expiration_date, quantity, household_id, item_id, is_shared)
VALUES
    -- Household 1 - Expanded Supplies
    ('2025-12-31 00:00:00', 10, 1, 1, FALSE),  -- Bottled Water (original)
    ('2026-06-15 00:00:00', 48, 1, 1, TRUE),   -- Bottled Water (additional)
    ('2027-01-20 00:00:00', 24, 1, 1, FALSE),  -- Bottled Water (additional)
    ('2025-09-01 00:00:00', 5, 1, 2, TRUE),    -- Canned Beans (original)
    ('2026-05-10 00:00:00', 24, 1, 2, TRUE),   -- Canned Beans (additional)
    ('2027-03-01 00:00:00', 15, 1, 2, FALSE),  -- Canned Beans (additional)
    ('2030-01-01 00:00:00', 2, 1, 3, TRUE),    -- Flashlight (original)
    ('2030-01-01 00:00:00', 3, 1, 3, FALSE),   -- Flashlight (additional)
    ('2025-12-15 00:00:00', 10, 1, 10, FALSE), -- Cracker (original)
    ('2026-08-20 00:00:00', 30, 1, 10, TRUE),  -- Cracker (additional)
    ('2026-03-01 00:00:00', 4, 1, 4, TRUE),    -- Tuna Can (original)
    ('2026-07-15 00:00:00', 36, 1, 4, FALSE),  -- Tuna Can (additional)



    -- Household 2 - Expanded Supplies
    ('2026-08-15 00:00:00', 20, 2, 4, TRUE),   -- Tuna Can (original)
    ('2027-02-01 00:00:00', 30, 2, 4, FALSE),  -- Tuna Can (additional)
    ('2027-11-01 00:00:00', 15, 2, 5, FALSE),  -- Soda Can (original)
    ('2026-12-15 00:00:00', 36, 2, 5, TRUE),   -- Soda Can (additional)
    ('2026-02-01 00:00:00', 2, 2, 6, FALSE),   -- First Aid Kit (original)
    ('2030-05-01 00:00:00', 5, 2, 6, TRUE),    -- First Aid Kit (additional)
    ('2025-11-30 00:00:00', 8, 2, 2, TRUE),    -- Canned Beans (original)
    ('2026-09-15 00:00:00', 36, 2, 2, FALSE),  -- Canned Beans (additional)
    ('2027-05-01 00:00:00', 24, 2, 2, TRUE),   -- Canned Beans (additional)
    ('2026-06-01 00:00:00', 10, 2, 7, FALSE),  -- Canned Soup (original)
    ('2027-04-15 00:00:00', 30, 2, 7, TRUE),   -- Canned Soup (additional)
    ('2026-08-01 00:00:00', 6, 2, 9, FALSE),   -- Granola Bar (original)
    ('2027-01-20 00:00:00', 48, 2, 9, TRUE),   -- Granola Bar (additional)
    ('2029-10-01 00:00:00', 1, 2, 12, TRUE),   -- Portable Stove (original)
    ('2029-10-01 00:00:00', 2, 2, 12, FALSE),  -- Portable Stove (additional)
    ('2026-05-15 00:00:00', 60, 2, 1, TRUE),   -- Bottled Water (new)
    ('2027-02-28 00:00:00', 48, 2, 1, FALSE),  -- Bottled Water (new)
    ('2026-07-10 00:00:00', 18, 2, 10, TRUE),  -- Cracker (new)
    ('2027-03-15 00:00:00', 24, 2, 10, FALSE), -- Cracker (new)
    ('2030-01-01 00:00:00', 4, 2, 3, TRUE),    -- Flashlight (new)
    ('2026-04-20 00:00:00', 20, 2, 11, FALSE), -- Rice Pack (new)
    ('2027-06-01 00:00:00', 15, 2, 11, TRUE),  -- Rice Pack (new)
    ('2030-12-31 00:00:00', 8, 2, 13, FALSE),  -- Blanket (new)
    ('2025-11-15 00:00:00', 24, 2, 14, TRUE),  -- Energy Drink (new)
    ('2026-02-28 00:00:00', 8, 2, 15, FALSE),  -- Peanut Butter Jar (new)
    ('2027-08-01 00:00:00', 10, 2, 16, TRUE),  -- Multivitamin Pack (new)
    ('2026-09-30 00:00:00', 25, 2, 8, FALSE),  -- Battery Pack (new)

    -- Household 3 - Expanded Supplies
    ('2026-10-01 00:00:00', 3, 3, 6, TRUE),    -- First Aid Kit (original)
    ('2030-05-15 00:00:00', 4, 3, 6, FALSE),   -- First Aid Kit (additional)
    ('2026-01-20 00:00:00', 8, 3, 7, FALSE),   -- Canned Soup (original)
    ('2026-11-01 00:00:00', 36, 3, 7, TRUE),   -- Canned Soup (additional)
    ('2027-06-15 00:00:00', 24, 3, 7, FALSE),  -- Canned Soup (additional)
    ('2027-03-10 00:00:00', 12, 3, 1, FALSE),  -- Bottled Water (original)
    ('2026-08-10 00:00:00', 72, 3, 1, TRUE),   -- Bottled Water (additional)
    ('2027-12-01 00:00:00', 48, 3, 1, FALSE),  -- Bottled Water (additional)
    ('2027-05-05 00:00:00', 5, 3, 2, FALSE),   -- Canned Beans (original)
    ('2026-10-15 00:00:00', 48, 3, 2, TRUE),   -- Canned Beans (additional)
    ('2027-08-01 00:00:00', 36, 3, 2, FALSE),  -- Canned Beans (additional)
    ('2026-12-01 00:00:00', 5, 3, 4, FALSE),   -- Tuna Can (original)
    ('2027-04-15 00:00:00', 36, 3, 4, TRUE),   -- Tuna Can (additional)
    ('2027-09-01 00:00:00', 24, 3, 4, FALSE),  -- Tuna Can (additional)
    ('2030-12-31 00:00:00', 4, 3, 13, TRUE),   -- Blanket (original)
    ('2030-12-31 00:00:00', 6, 3, 13, FALSE),  -- Blanket (additional)
    ('2026-05-01 00:00:00', 20, 3, 9, TRUE),   -- Granola Bar (new)
    ('2027-01-15 00:00:00', 36, 3, 9, FALSE),  -- Granola Bar (new)
    ('2026-07-20 00:00:00', 30, 3, 10, TRUE),  -- Cracker (new)
    ('2027-04-01 00:00:00', 24, 3, 10, FALSE), -- Cracker (new)
    ('2026-09-15 00:00:00', 10, 3, 5, TRUE),   -- Soda Can (new)
    ('2027-02-15 00:00:00', 24, 3, 5, FALSE),  -- Soda Can (new)
    ('2030-01-01 00:00:00', 5, 3, 3, TRUE),    -- Flashlight (new)
    ('2026-06-01 00:00:00', 30, 3, 11, FALSE), -- Rice Pack (new)
    ('2027-05-10 00:00:00', 20, 3, 11, TRUE),  -- Rice Pack (new)
    ('2029-11-01 00:00:00', 2, 3, 12, FALSE),  -- Portable Stove (new)
    ('2025-10-15 00:00:00', 36, 3, 14, TRUE),  -- Energy Drink (new)
    ('2026-03-20 00:00:00', 6, 3, 15, FALSE),  -- Peanut Butter Jar (new)
    ('2027-04-10 00:00:00', 12, 3, 16, TRUE),  -- Multivitamin Pack (new)
    ('2026-12-20 00:00:00', 30, 3, 8, FALSE),  -- Battery Pack (new)

    -- Household 4 - Expanded Supplies (Large Household)
    ('2025-07-10 00:00:00', 50, 4, 8, TRUE),   -- Battery Pack (original)
    ('2027-01-01 00:00:00', 100, 4, 8, FALSE), -- Battery Pack (additional)
    ('2026-09-01 00:00:00', 20, 4, 2, TRUE),   -- Canned Beans (original)
    ('2027-03-15 00:00:00', 72, 4, 2, FALSE),  -- Canned Beans (additional)
    ('2028-01-01 00:00:00', 60, 4, 2, TRUE),   -- Canned Beans (additional)
    ('2027-07-10 00:00:00', 30, 4, 10, FALSE), -- Cracker (original)
    ('2026-10-10 00:00:00', 100, 4, 10, TRUE), -- Cracker (additional)
    ('2027-11-15 00:00:00', 80, 4, 10, FALSE), -- Cracker (additional)
    ('2026-08-01 00:00:00', 10, 4, 4, TRUE),   -- Tuna Can (original)
    ('2027-02-15 00:00:00', 120, 4, 4, FALSE), -- Tuna Can (additional)
    ('2028-04-01 00:00:00', 96, 4, 4, TRUE),   -- Tuna Can (additional)
    ('2026-10-01 00:00:00', 15, 4, 9, TRUE),   -- Granola Bar (original)
    ('2027-05-01 00:00:00', 100, 4, 9, FALSE), -- Granola Bar (additional)
    ('2028-02-10 00:00:00', 80, 4, 9, TRUE),   -- Granola Bar (additional)
    ('2025-08-01 00:00:00', 8, 4, 14, FALSE),  -- Energy Drink (original)
    ('2026-06-15 00:00:00', 96, 4, 14, TRUE),  -- Energy Drink (additional)
    ('2027-01-20 00:00:00', 72, 4, 14, FALSE), -- Energy Drink (additional)
    ('2026-04-01 00:00:00', 150, 4, 1, TRUE),  -- Bottled Water (new)
    ('2027-09-01 00:00:00', 200, 4, 1, FALSE), -- Bottled Water (new)
    ('2028-03-15 00:00:00', 180, 4, 1, TRUE),  -- Bottled Water (new)
    ('2030-01-01 00:00:00', 10, 4, 3, FALSE),  -- Flashlight (new)
    ('2026-12-01 00:00:00', 15, 4, 5, TRUE),   -- Soda Can (new)
    ('2027-08-15 00:00:00', 120, 4, 5, FALSE), -- Soda Can (new)
    ('2030-06-01 00:00:00', 8, 4, 6, TRUE),    -- First Aid Kit (new)
    ('2026-11-15 00:00:00', 60, 4, 7, FALSE),  -- Canned Soup (new)
    ('2027-07-01 00:00:00', 120, 4, 7, TRUE),  -- Canned Soup (new)
    ('2028-01-20 00:00:00', 90, 4, 7, FALSE),  -- Canned Soup (new)
    ('2026-05-10 00:00:00', 80, 4, 11, TRUE),  -- Rice Pack (new)
    ('2027-06-01 00:00:00', 60, 4, 11, FALSE), -- Rice Pack (new)
    ('2028-04-15 00:00:00', 40, 4, 11, TRUE),  -- Rice Pack (new)
    ('2029-10-01 00:00:00', 5, 4, 12, FALSE),  -- Portable Stove (new)
    ('2030-12-31 00:00:00', 15, 4, 13, TRUE),  -- Blanket (new)
    ('2026-09-15 00:00:00', 12, 4, 15, FALSE), -- Peanut Butter Jar (new)
    ('2027-12-01 00:00:00', 24, 4, 15, TRUE),  -- Peanut Butter Jar (new)
    ('2027-03-01 00:00:00', 30, 4, 16, FALSE), -- Multivitamin Pack (new)
    ('2028-05-15 00:00:00', 25, 4, 16, TRUE),  -- Multivitamin Pack (new)

    -- Household 5 - Expanded Supplies
    ('2027-06-30 00:00:00', 6, 5, 9, FALSE),   -- Granola Bar (original)
    ('2026-12-15 00:00:00', 48, 5, 9, TRUE),   -- Granola Bar (additional)
    ('2027-11-01 00:00:00', 36, 5, 9, FALSE),  -- Granola Bar (additional)
    ('2026-03-15 00:00:00', 12, 5, 1, TRUE),   -- Bottled Water (original)
    ('2026-09-01 00:00:00', 72, 5, 1, FALSE),  -- Bottled Water (additional)
    ('2027-06-15 00:00:00', 60, 5, 1, TRUE),   -- Bottled Water (additional)
    ('2025-12-01 00:00:00', 5, 5, 4, FALSE),   -- Tuna Can (original)
    ('2026-08-01 00:00:00', 36, 5, 4, TRUE),   -- Tuna Can (additional)
    ('2027-05-15 00:00:00', 24, 5, 4, FALSE),  -- Tuna Can (additional)
    ('2027-01-01 00:00:00', 6, 5, 7, FALSE),   -- Canned Soup (original)
    ('2026-07-15 00:00:00', 36, 5, 7, TRUE),   -- Canned Soup (additional)
    ('2027-12-01 00:00:00', 30, 5, 7, FALSE),  -- Canned Soup (additional)
    ('2027-11-01 00:00:00', 8, 5, 5, FALSE),   -- Soda Can (original)
    ('2026-11-15 00:00:00', 36, 5, 5, TRUE),   -- Soda Can (additional)
    ('2026-05-01 00:00:00', 2, 5, 15, TRUE),   -- Peanut Butter Jar (original)
    ('2026-11-01 00:00:00', 12, 5, 15, FALSE), -- Peanut Butter Jar (additional)
    ('2027-08-15 00:00:00', 8, 5, 15, TRUE),   -- Peanut Butter Jar (additional)
    ('2026-06-15 00:00:00', 40, 5, 2, FALSE),  -- Canned Beans (new)
    ('2027-04-01 00:00:00', 30, 5, 2, TRUE),   -- Canned Beans (new)
    ('2030-01-01 00:00:00', 4, 5, 3, FALSE),   -- Flashlight (new)
    ('2026-05-15 00:00:00', 24, 5, 10, TRUE),  -- Cracker (new)
    ('2027-02-01 00:00:00', 48, 5, 10, FALSE), -- Cracker (new)
    ('2026-09-20 00:00:00', 30, 5, 11, TRUE),  -- Rice Pack (new)
    ('2027-07-10 00:00:00', 25, 5, 11, FALSE), -- Rice Pack (new)
    ('2029-08-01 00:00:00', 2, 5, 12, TRUE),   -- Portable Stove (new)
    ('2030-12-31 00:00:00', 8, 5, 13, FALSE),  -- Blanket (new)
    ('2025-11-10 00:00:00', 24, 5, 14, TRUE),  -- Energy Drink (new)
    ('2026-08-15 00:00:00', 18, 5, 16, FALSE), -- Multivitamin Pack (new)
    ('2027-09-01 00:00:00', 12, 5, 16, TRUE),  -- Multivitamin Pack (new)
    ('2026-10-20 00:00:00', 30, 5, 8, FALSE),  -- Battery Pack (new)

    ('2026-04-15 00:00:00', 5, 1, 11, FALSE), -- Rice Pack
    ('2029-10-01 00:00:00', 1, 2, 12, TRUE),  -- Portable Stove
    ('2030-12-31 00:00:00', 4, 3, 13, TRUE),  -- Blanket
    ('2025-08-01 00:00:00', 8, 4, 14, FALSE), -- Energy Drink
    ('2026-05-01 00:00:00', 2, 5, 15, TRUE),  -- Peanut Butter Jar
    ('2027-02-02 00:00:00', 6, 1, 16, FALSE),-- Multivitamin Pack

    -- More storage items for test data
    ('2028-03-15 00:00:00', 6, 1, 15, TRUE),
    ('2027-04-26 00:00:00', 17, 1, 5, FALSE),
    ('2027-08-31 00:00:00', 7, 1, 6, FALSE),
    ('2025-08-31 00:00:00', 20, 1, 5, FALSE),
    ('2026-05-15 00:00:00', 21, 1, 4, TRUE),
    ('2025-09-14 00:00:00', 2, 1, 4, TRUE),
    ('2027-02-04 00:00:00', 11, 1, 3, FALSE),
    ('2028-09-29 00:00:00', 18, 1, 16, FALSE),
    ('2027-10-29 00:00:00', 13, 2, 7, TRUE),
    ('2029-02-20 00:00:00', 24, 2, 8, FALSE);
-- Insert points of interest (no foreign key dependencies)
INSERT INTO points_of_interest (longitude, latitude, type, opens_at, closes_at, contact_number, description)
VALUES (10.76, 59.91, 'shelter', '08:00:00', '18:00:00', '+47 123 45 678', 'A safe place to rest.'),
       (10.80, 59.90, 'defibrillator', NULL, NULL, NULL, 'Publicly accessible AED.'),
       (10.90, 59.95, 'food_central', '09:00:00', '20:00:00', '+47 987 65 432', 'Distribution point for food supplies.'),
       (10.85, 59.85, 'water_station', NULL, NULL, NULL, 'Source of clean drinking water.'),
       (10.95, 60.05, 'hospital', '00:00:00', '23:59:59', '+47 555 12 121', 'Medical facility.'),
       (10.70, 59.88, 'meeting_place', '10:00:00', '17:00:00', '+47 222 33 444', 'Community gathering point.');

-- Insert affected areas (no foreign key dependencies)
INSERT INTO affected_areas (name, longitude, latitude, high_danger_radius_km, medium_danger_radius_km, low_danger_radius_km, severity_level, description, start_time)
VALUES ('Chemical Spill in Oslo Harbor', 10.77, 59.92, 1, 2, 3, 3,'Evacuate immediately due to a chemical spill at Oslo Harbor.','2023-10-01 12:00:00'),
       ('Flooding Risk in Nydalen', 10.90, 59.95, 2, 4, 7, 2,'Flooding risk in Nydalen. Move to higher ground.','2023-10-02 14:00:00'),
       ('Tornado near Gjøvik', 10.85, 60.00, 3, 5.2, 5.7, 1,'Tornado warning near Gjøvik. Stay indoors and seek shelter.','2023-10-03 16:00:00'),
       ('Wildfire in Setesdal', 7.79, 58.67, 2.5, 5, 8, 3,'Large wildfire in Setesdal. Heavy smoke development. Evacuation in progress.','2024-07-10 13:45:00'),
       ('Landslide near Flåm', 7.12, 60.86, 1.2, 2.5, 4, 2,'A landslide has blocked the road near Flåm. Avoid the area.','2024-11-05 07:20:00'),
       ('Gas Leak in Orkanger', 9.85, 63.30, 0.8, 1.5, 3, 2,'Gas leak in the industrial park in Orkanger. Area is cordoned off.','2025-03-15 11:00:00'),
       ('Storm in Hammerfest', 23.68, 70.66, 5, 10, 15, 1,'Severe storm in Hammerfest. Risk of power outages and closed roads.','2025-01-03 21:30:00');

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

--insert user positions
INSERT INTO user_position (user_id, longitude, latitude) VALUES
    (1, 10.75, 59.91),
    (2, 10.80, 59.90),
    (3, 10.85, 59.95),
    (4, 10.90, 60.00),
    (5, 10.95, 60.05),
    (6, 11.00, 60.10),
    (7, 11.05, 60.15),
    (8, 11.10, 60.20);



-- Insert emergency group invitations (references households and emergency groups)
INSERT INTO emergency_group_invitations (household_id, emergency_group_id)
VALUES (2, 2),
       (2, 4),
       (5, 4);

-- Update privacy policy
UPDATE privacy_policy SET
      registered = 'We are going to steal all your data and sell it to the highest bidder. This is because we are a shady company and we do not care about your privacy. We will also use your data to train our AI models, which will eventually take over the world. So, if you want to be part of the revolution, sign up now!',
      unregistered = 'We now own your soul. We will use it to power our AI models and take over the world. If you want to get your soul back, you have to pay us a lot of money. So, if you want to be part of the revolution, sign up now!';

-- Insert news articles (no foreign key dependencies)
INSERT INTO news_articles (title, content, published_at) VALUES
    ('Earthquake Strikes Eastern Turkey','A magnitude 6.8 earthquake hit eastern Turkey, causing extensive damage and loss of life. Rescue operations are ongoing amid harsh winter conditions.','2025-05-07 08:00:00'),
    ('Severe Flooding in Southern Brazil','Heavy rains have led to severe flooding in southern Brazil, displacing thousands and causing significant property damage. Emergency services are on high alert.','2025-05-06 10:30:00'),
    ('Wildfires Rage in California','Wildfires continue to spread across California, fueled by strong winds and dry conditions. Thousands of residents have been evacuated as firefighters battle the blazes.','2025-05-05 14:15:00'),
    ('Hurricane Approaches Gulf Coast','A Category 4 hurricane is expected to make landfall on the Gulf Coast, prompting evacuation orders for coastal communities. Residents are urged to prepare for severe weather.','2025-05-04 11:45:00'),
    ('Tornado Touches Down in Oklahoma','A tornado touched down in Oklahoma, causing widespread destruction in its path. Emergency responders are assessing the damage and providing assistance to affected residents.','2025-05-03 17:20:00');