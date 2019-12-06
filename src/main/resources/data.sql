REPLACE INTO `role` VALUES (0,'STUDENT');
REPLACE INTO `role` VALUES (1,'INSTRUCTOR');
REPLACE INTO material (material_name, flamability, heat_of_combustion, ph, radioactivity, temperature, volume, dtype, material_id) values ('HCl', 2 , 2, 0, 4, 20, 30, 'Solution',1);
REPLACE INTO material (material_name, flamability, heat_of_combustion, ph, radioactivity, temperature, volume, dtype, material_id) values ('NaCl', 2 , 2, 0, 4, 20, 30, 'Solution',2);
REPLACE INTO material (material_name, flamability, heat_of_combustion, ph, radioactivity, temperature, volume, dtype, material_id) values ('NaOH', 2 , 2, 0, 4, 20, 30, 'Solution',3);
REPLACE INTO material (material_name, capacity, dtype, material_id) values ('Beaker', 50, 'Container', 4);
REPLACE INTO material (material_name, capacity, dtype, material_id) values ('Flask', 30, 'Container', 5);
REPLACE INTO material (material_name, attribute1, attribute2, attribute3, description, dtype, material_id) values ('Scale', "Weigh", null, null, "Weigh your thingies", 'Tool', 6);