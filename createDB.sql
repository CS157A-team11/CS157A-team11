-- Create Users Table
CREATE TABLE Users (
    UserID INT PRIMARY KEY AUTO_INCREMENT,
    Username VARCHAR(50) UNIQUE NOT NULL,
    Email VARCHAR(100) UNIQUE NOT NULL,
    PasswordHash VARCHAR(255) NOT NULL,
    CommonName VARCHAR(100)
);


-- Create EmailVerification Table as a weak entity
CREATE TABLE EmailVerification (
    UserID INT,
    Code VARCHAR(100),
    ExpirationTime DATETIME,
    PRIMARY KEY (UserID, Code),
    FOREIGN KEY (UserID) REFERENCES Users(UserID)
);

-- Create Ingredients Table
CREATE TABLE Ingredients (
    IngredientID INT PRIMARY KEY AUTO_INCREMENT,
    IngredientName VARCHAR(100) NOT NULL,
    Kcals INT,
    Alias VARCHAR(100),
    Macros TEXT
);

-- Create Recipes Table
CREATE TABLE Recipes (
    RecipeID INT PRIMARY KEY AUTO_INCREMENT,
    Name VARCHAR(100) NOT NULL,
    Instructions TEXT NOT NULL,
    Time INT,
    Servings INT
);

-- Create SpicyRecipes Table (ISA relationship)
CREATE TABLE SpicyRecipes (
    RecipeID INT PRIMARY KEY,
    SpicinessLevel VARCHAR(50),
    FOREIGN KEY (RecipeID) REFERENCES Recipes(RecipeID)
);

-- Create CookingMethods Table
CREATE TABLE CookingMethods (
    MethodID INT PRIMARY KEY AUTO_INCREMENT,
    Name VARCHAR(50) NOT NULL
);

-- Create RecipeCategory Table
CREATE TABLE RecipeCategory (
    CategoryID INT PRIMARY KEY AUTO_INCREMENT,
    CategoryName VARCHAR(50) NOT NULL
);

-- Join Tables

-- Users_Ingredients (Cannot Eat)
CREATE TABLE Users_Ingredients (
    UserID INT,
    IngredientID INT,
    PRIMARY KEY (UserID, IngredientID),
    FOREIGN KEY (UserID) REFERENCES Users(UserID),
    FOREIGN KEY (IngredientID) REFERENCES Ingredients(IngredientID)
);

-- Recipe_Ingredients (Uses)
CREATE TABLE Recipe_Ingredients (
    RecipeID INT,
    IngredientID INT,
    PRIMARY KEY (RecipeID, IngredientID),
    FOREIGN KEY (RecipeID) REFERENCES Recipes(RecipeID),
    FOREIGN KEY (IngredientID) REFERENCES Ingredients(IngredientID)
);

-- Recipe_CookingMethods (Requires)
CREATE TABLE Recipe_CookingMethods (
    RecipeID INT,
    MethodID INT,
    PRIMARY KEY (RecipeID, MethodID),
    FOREIGN KEY (RecipeID) REFERENCES Recipes(RecipeID),
    FOREIGN KEY (MethodID) REFERENCES CookingMethods(MethodID)
);

-- Recipe_RecipeCategory (Belong To)
CREATE TABLE Recipe_RecipeCategory (
    RecipeID INT,
    CategoryID INT,
    PRIMARY KEY (RecipeID, CategoryID),
    FOREIGN KEY (RecipeID) REFERENCES Recipes(RecipeID),
    FOREIGN KEY (CategoryID) REFERENCES RecipeCategory(CategoryID)
);
