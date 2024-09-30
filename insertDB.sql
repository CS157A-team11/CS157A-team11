INSERT INTO Users (Username, Email, PasswordHash, CommonName)
VALUES
('john_doe', 'john@example.com', 'hashedpassword1', 'John Doe'),
('jane_smith', 'jane@example.com', 'hashedpassword2', 'Jane Smith'),
('alice_johnson', 'alice@example.com', 'hashedpassword3', 'Alice Johnson'),
('bob_brown', 'bob@example.com', 'hashedpassword4', 'Bob Brown'),
('charlie_white', 'charlie@example.com', 'hashedpassword5', 'Charlie White'),
('daisy_green', 'daisy@example.com', 'hashedpassword6', 'Daisy Green'),
('eve_black', 'eve@example.com', 'hashedpassword7', 'Eve Black'),
('frank_red', 'frank@example.com', 'hashedpassword8', 'Frank Red'),
('grace_yellow', 'grace@example.com', 'hashedpassword9', 'Grace Yellow'),
('hank_orange', 'hank@example.com', 'hashedpassword10', 'Hank Orange');


INSERT INTO EmailVerification (UserID, Code, ExpirationTime)
VALUES
(1, 'code123', '2024-12-31 23:59:59'),
(2, 'code456', '2024-12-31 23:59:59'),
(3, 'code789', '2024-12-31 23:59:59'),
(4, 'code111', '2024-12-31 23:59:59'),
(5, 'code222', '2024-12-31 23:59:59'),
(6, 'code333', '2024-12-31 23:59:59'),
(7, 'code444', '2024-12-31 23:59:59'),
(8, 'code555', '2024-12-31 23:59:59'),
(9, 'code666', '2024-12-31 23:59:59'),
(10, 'code777', '2024-12-31 23:59:59');


INSERT INTO Ingredients (IngredientName, Kcals, Alias, Macros)
VALUES
('Tomato', 18, 'Tomatoe', 'Carbs: 4g, Protein: 1g'),
('Chicken Breast', 165, 'Chicken', 'Protein: 31g, Fat: 3.6g'),
('Lettuce', 5, 'Leafy Green', 'Carbs: 1g'),
('Pasta', 130, 'Noodles', 'Carbs: 25g, Protein: 5g'),
('Olive Oil', 119, 'Oil', 'Fat: 14g'),
('Parmesan Cheese', 431, 'Cheese', 'Fat: 29g, Protein: 38g'),
('Pepper', 1, 'Spice', 'Carbs: 0g, Protein: 0g'),
('Garlic', 4, 'Clove', 'Carbs: 1g'),
('Basil', 1, 'Herb', 'Carbs: 0g'),
('Onion', 40, 'Bulb', 'Carbs: 9g, Protein: 1g');


INSERT INTO Recipes (Name, Instructions, Time, Servings)
VALUES
('Spaghetti Bolognese', 'Boil pasta and cook sauce...', 30, 4),
('Grilled Chicken Salad', 'Grill chicken and mix...', 20, 2),
('Vegetable Stir Fry', 'Stir fry vegetables...', 15, 3),
('Pasta Primavera', 'Cook pasta and add vegetables...', 25, 4),
('Olive Oil Dressing','Mix olive oil with spices...',5 ,4 ),
('Garlic Bread','Toast bread with garlic butter...',10 ,2 ),
('Classic Caesar Salad','Mix lettuce and dressing...',15 ,2 ),
('Pepperoni Pizza','Prepare dough and add toppings...',40 ,4 ),
('Vegetable Soup','Boil vegetables and serve...',30 ,6 ),
('Caprese Salad','Layer tomatoes and mozzarella...',10 ,2 );


INSERT INTO SpicyRecipes (RecipeID, SpicinessLevel)
VALUES 
(1,'Medium'), 
(3,'High'), 
(5,'Low'), 
(7,'Medium'), 
(9,'High');


INSERT INTO CookingMethods (Name)
VALUES 
('Boiling'), 
('Grilling'), 
('Frying'), 
('Baking'), 
('Roasting'), 
('Steaming'), 
('Sauteing'), 
('Broiling'), 
('Poaching'), 
('Simmering');


INSERT INTO RecipeCategory (CategoryName)
VALUES 
('Italian'), 
('Salad'), 
('Soup'), 
('Dessert'), 
('Appetizer'), 
('Main Course'), 
('Side Dish'), 
('Breakfast'), 
('Snack'), 
('Vegan');


INSERT INTO Users_Ingredients (UserID, IngredientID)
VALUES
(1, 2), -- John cannot eat Chicken Breast
(2, 3), -- Jane cannot eat Lettuce
(3, 4), -- Alice cannot eat Pasta
(4, 5), -- Bob cannot eat Olive Oil
(5, 6), -- Charlie cannot eat Parmesan Cheese
(6, 7), -- Daisy cannot eat Pepper
(7,8), -- Eve cannot eat Garlic
(8 ,9), -- Frank cannot eat Basil
(9 ,10), -- Grace cannot eat Onion
(10 ,1); -- Hank cannot eat Tomato


INSERT INTO Recipe_Ingredients (RecipeID, IngredientID)
VALUES
(1 ,1), -- Spaghetti Bolognese uses Tomato
(2 ,2), -- Grilled Chicken Salad uses Chicken Breast
(3 ,3), -- Vegetable Stir Fry uses Lettuce
(4 ,4), -- Pasta Primavera uses Pasta
(5 ,5), -- Olive Oil Dressing uses Olive Oil
(6 ,6), -- Garlic Bread uses Parmesan Cheese
(7 ,7), -- Classic Caesar Salad uses Pepper
(8 ,8), -- Pepperoni Pizza uses Garlic
(9 ,9), -- Vegetable Soup uses Basil
(10 ,10); -- Caprese Salad uses Onion


INSERT INTO Recipe_CookingMethods (RecipeID, MethodID)
VALUES
(1 ,1), -- Spaghetti Bolognese requires Boiling
(2 ,2), -- Grilled Chicken Salad requires Grilling
(3 ,3), -- Vegetable Stir Fry requires Frying
(4 ,1), -- Pasta Primavera requires Boiling
(5 ,7), -- Olive Oil Dressing requires Sauteing
(6 ,8), -- Garlic Bread requires Broiling
(7 ,2), -- Classic Caesar Salad requires Grilling
(8 ,9), -- Pepperoni Pizza requires Poaching
(9 ,10),-- Vegetable Soup requires Simmering  
(10 ,5);-- Caprese Salad requires Roasting  


INSERT INTO Recipe_RecipeCategory (RecipeID, CategoryID)
VALUES 
(1 ,1),-- Spaghetti Bolognese belongs to Italian category  
 (2 ,2),-- Grilled Chicken Salad belongs to Salad category  
 (3 ,3) ,-- Vegetable Stir Fry belongs to Soup category  
 (4 ,1) ,-- Pasta Primavera belongs to Italian category  
 (5 ,6) ,-- Olive Oil Dressing belongs to Main Course category  
 (6 ,7) ,-- Garlic Bread belongs to Side Dish category  
 (7 ,2) ,-- Classic Caesar Salad belongs to Salad category  
 (8 ,8) ,-- Pepperoni Pizza belongs to Breakfast category  
 (9 ,3) ;-- Vegetable Soup belongs to Soup category  

