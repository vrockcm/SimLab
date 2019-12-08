REPLACE INTO `role` VALUES (0,'STUDENT');
REPLACE INTO `role` VALUES (1,'INSTRUCTOR');
REPLACE INTO solution (solution_name, flamability, heat_of_combustion, ph, radioactivity, temperature, volume, solution_id) values ('HCl', 2 , 2, 0, 4, 20, 30,1);
REPLACE INTO solution (solution_name, flamability, heat_of_combustion, ph, radioactivity, temperature, volume, solution_id) values ('NaCl', 2 , 2, 0, 4, 20, 30,2);
REPLACE INTO solution (solution_name, flamability, heat_of_combustion, ph, radioactivity, temperature, volume, solution_id) values ('NaOH', 2 , 2, 0, 4, 20, 30,3);
REPLACE INTO container (container_name, capacity, container_id) values ('Beaker', 50, 4);
REPLACE INTO container (container_name, capacity, container_id) values ('Flask', 30, 5);
REPLACE INTO tool (tool_name, attribute1, attribute2, attribute3, description, tool_id) values ('Scale', "Weigh", null, null, "Weigh your thingies", 6);