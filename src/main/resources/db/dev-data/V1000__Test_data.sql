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
    -- Household 1
    ('2025-12-31 00:00:00', 10, 1, 1, FALSE),  -- Bottled Water
    ('2025-09-01 00:00:00', 5, 1, 2, TRUE),    -- Canned Beans
    ('2030-01-01 00:00:00', 2, 1, 3, TRUE),    -- Flashlight
    ('2025-12-15 00:00:00', 10, 1, 10, FALSE), -- Cracker
    ('2026-03-01 00:00:00', 4, 1, 4, TRUE),    -- Tuna Can
    ('2024-12-01 00:00:00', 3, 1, 9, FALSE),   -- Expired Granola Bar
    ('2026-04-15 00:00:00', 5, 1, 11, FALSE),  -- Rice Pack
    ('2027-02-02 00:00:00', 6, 1, 16, FALSE),  -- Multivitamin Pack
    ('2026-05-20 00:00:00', 15, 1, 2, FALSE),  -- Canned Beans
    ('2026-06-12 00:00:00', 8, 1, 7, TRUE),    -- Canned Soup
    ('2026-07-15 00:00:00', 12, 1, 10, FALSE), -- Cracker
    ('2026-11-30 00:00:00', 6, 1, 4, TRUE),    -- Tuna Can
    ('2026-01-15 00:00:00', 10, 1, 11, FALSE), -- Rice Pack
    ('2026-08-22 00:00:00', 4, 1, 15, TRUE),   -- Peanut Butter Jar
    ('2026-09-17 00:00:00', 18, 1, 9, FALSE),  -- Granola Bar
    ('2026-10-10 00:00:00', 20, 1, 5, TRUE),   -- Soda Can
    ('2026-02-28 00:00:00', 25, 1, 6, FALSE),  -- First Aid Kit
    ('2026-12-31 00:00:00', 30, 1, 8, TRUE),   -- Battery Pack
    ('2026-11-01 00:00:00', 7, 1, 12, FALSE),  -- Portable Stove
    ('2026-03-10 00:00:00', 9, 1, 14, TRUE),   -- Energy Drink

-- Household 2
    ('2026-08-15 00:00:00', 20, 2, 4, TRUE),   -- Tuna Can
    ('2027-11-01 00:00:00', 15, 2, 5, FALSE),  -- Soda Can
    ('2026-02-01 00:00:00', 2, 2, 6, FALSE),   -- First Aid Kit
    ('2025-11-30 00:00:00', 8, 2, 2, TRUE),    -- Canned Beans
    ('2026-06-01 00:00:00', 10, 2, 7, FALSE),  -- Canned Soup
    ('2026-08-01 00:00:00', 6, 2, 9, FALSE),   -- Granola Bar
    ('2029-10-01 00:00:00', 1, 2, 12, TRUE),   -- Portable Stove
    ('2026-04-25 00:00:00', 25, 2, 2, FALSE),  -- Canned Beans
    ('2026-10-15 00:00:00', 14, 2, 11, TRUE),  -- Rice Pack
    ('2026-03-18 00:00:00', 12, 2, 10, FALSE), -- Cracker
    ('2026-11-22 00:00:00', 8, 2, 15, TRUE),   -- Peanut Butter Jar
    ('2027-01-05 00:00:00', 16, 2, 7, FALSE),  -- Canned Soup
    ('2026-09-30 00:00:00', 22, 2, 9, FALSE),  -- Granola Bar
    ('2026-07-28 00:00:00', 18, 2, 4, TRUE),   -- Tuna Can
    ('2026-12-12 00:00:00', 20, 2, 10, FALSE), -- Cracker

-- Household 3
    ('2026-10-01 00:00:00', 3, 3, 6, TRUE),    -- First Aid Kit
    ('2026-01-20 00:00:00', 8, 3, 7, FALSE),   -- Canned Soup
    ('2027-03-10 00:00:00', 12, 3, 1, FALSE),  -- Bottled Water
    ('2027-05-05 00:00:00', 5, 3, 2, FALSE),   -- Canned Beans
    ('2026-12-01 00:00:00', 5, 3, 4, FALSE),   -- Tuna Can
    ('2030-12-31 00:00:00', 4, 3, 13, TRUE),   -- Blanket
    ('2026-04-10 00:00:00', 20, 3, 2, TRUE),   -- Canned Beans
    ('2026-06-25 00:00:00', 15, 3, 7, FALSE),  -- Canned Soup
    ('2026-05-12 00:00:00', 10, 3, 11, TRUE),  -- Rice Pack
    ('2026-08-20 00:00:00', 8, 3, 4, FALSE),   -- Tuna Can
    ('2026-11-15 00:00:00', 12, 3, 10, FALSE), -- Cracker
    ('2027-01-28 00:00:00', 6, 3, 15, TRUE),   -- Peanut Butter Jar
    ('2026-09-05 00:00:00', 14, 3, 9, FALSE),  -- Granola Bar
    ('2026-07-18 00:00:00', 25, 3, 10, TRUE),  -- Cracker

-- Household 4
    ('2025-07-10 00:00:00', 50, 4, 8, TRUE),   -- Battery Pack
    ('2026-09-01 00:00:00', 20, 4, 2, TRUE),   -- Canned Beans
    ('2027-07-10 00:00:00', 30, 4, 10, FALSE), -- Cracker
    ('2026-08-01 00:00:00', 10, 4, 4, TRUE),   -- Tuna Can
    ('2026-10-01 00:00:00', 15, 4, 9, TRUE),   -- Granola Bar
    ('2025-08-01 00:00:00', 8, 4, 14, FALSE),  -- Energy Drink
    ('2026-03-15 00:00:00', 35, 4, 2, FALSE),  -- Canned Beans
    ('2026-05-10 00:00:00', 25, 4, 7, TRUE),   -- Canned Soup
    ('2026-04-22 00:00:00', 18, 4, 11, FALSE), -- Rice Pack
    ('2026-06-18 00:00:00', 20, 4, 4, TRUE),   -- Tuna Can
    ('2026-11-30 00:00:00', 40, 4, 10, FALSE), -- Cracker
    ('2027-02-15 00:00:00', 12, 4, 15, TRUE),  -- Peanut Butter Jar
    ('2026-12-20 00:00:00', 30, 4, 9, FALSE),  -- Granola Bar
    ('2026-07-25 00:00:00', 45, 4, 11, TRUE),  -- Rice Pack

-- Household 5
    ('2027-06-30 00:00:00', 6, 5, 9, FALSE),   -- Granola Bar
    ('2026-03-15 00:00:00', 12, 5, 1, TRUE),   -- Bottled Water
    ('2025-12-01 00:00:00', 5, 5, 4, FALSE),   -- Tuna Can
    ('2027-01-01 00:00:00', 6, 5, 7, FALSE),   -- Canned Soup
    ('2027-11-01 00:00:00', 8, 5, 5, FALSE),   -- Soda Can
    ('2026-05-01 00:00:00', 2, 5, 15, TRUE),   -- Peanut Butter Jar
    ('2026-04-10 00:00:00', 18, 5, 2, FALSE),  -- Canned Beans
    ('2026-08-15 00:00:00', 10, 5, 11, TRUE),  -- Rice Pack
    ('2026-09-22 00:00:00', 15, 5, 10, FALSE), -- Cracker
    ('2026-06-05 00:00:00', 8, 5, 4, TRUE),    -- Tuna Can
    ('2026-07-18 00:00:00', 12, 5, 7, FALSE),  -- Canned Soup
    ('2026-10-30 00:00:00', 5, 5, 15, TRUE),   -- Peanut Butter Jar
    ('2026-11-25 00:00:00', 20, 5, 9, FALSE),  -- Granola Bar
    ('2027-02-28 00:00:00', 16, 5, 2, TRUE),   -- Canned Beans
    ('2026-12-15 00:00:00', 14, 5, 11, FALSE); -- Rice Pack

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