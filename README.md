# BESTSELLER Backend Challenge
Java Spring Boot API for managing gamers and their game skill levels with search and matching features.

# Gaming Directory API

A Spring Boot REST API for managing gamers, their geographic locations, games they play, and skill levels. This application provides endpoints for linking gamers to games with skill levels and searching for matching gamers based on various criteria.

## Challenge Requirements

- **Data Model Design**: Clean entity relationships with proper JPA mappings  
- **Initial Data Load**: Test data with gamers across multiple countries  
- **Link Gamer to Game API**: POST endpoint with skill level assignment  
- **Search API**: Search by level, game, and geography for auto-matching  
- **Level-specific API**: Get gamers by specific level per game  
- **Full REST Convention**: HTTP methods, status codes, and resource naming  
- **Input Validation**: Bean validation with injection protection  
- **API Documentation**: Swagger/OpenAPI integration  
- **Unit Tests**: Comprehensive test coverage across all layers  

## Quick Start

### Prerequisites
- Java 21 or higher
- Maven 3.6+

### Run the Application
```bash
# Clone and navigate to project directory
cd gaming-directory

# Run the application
./mvnw.cmd spring-boot:run
```

The application will start on `http://localhost:8080`

**Note**: The GlobalExceptionHandler `@ControllerAdvice` needs to be commented out as it's not compatible with Swagger. When commented out, some tests will fail since they expect the global exception handling behavior.

I initially implemented error handling directly in the controllers but thought a global exception handler would be better. However, it didn't work as planned due to compatibility issues with Swagger.

### Access Points
- **API Documentation**: http://localhost:8080/swagger-ui.html
- **H2 Database Console**: http://localhost:8080/h2-console
  - JDBC URL: `jdbc:h2:mem:gamingdb`
  - Username: `sa`
  - Password: (leave empty)

## Data Model

### Entities
- **Gamer**: `id`, `username` (unique), `country`
- **Game**: `id`, `name` (unique)  
- **GamerSkill**: `id`, `gamer_id`, `game_id`, `level` (NOOB, PRO, INVINCIBLE)

### Relationships
- Gamer ↔ Game (Many-to-Many through GamerSkill)
- Each gamer-game combination has a unique skill level

## API Endpoints

#### 1. Link Gamer to Game
```http
POST /api/gamer-skills
Content-Type: application/json

{
  "username": "Joey",
  "gameName": "Counter-Strike",
  "level": "PRO"
}
```

#### 2. Search for Matching Gamers
```http
GET /api/gamer-skills/search?level=PRO&gameName=Counter-Strike&country=USA
```

#### 3. Get Gamers by Level per Game
```http
GET /api/gamer-skills/by-level?gameName=Counter-Strike&level=INVINCIBLE
```

#### 4. Gamer Management
```http
GET    /api/gamers           # Get all gamers
POST   /api/gamers           # Create new gamer
GET    /api/gamers/{id}      # Get gamer by ID
```

#### 5. Game Management
```http
GET    /api/games            # Get all games
POST   /api/games            # Create new game  
GET    /api/games/{id}       # Get game by ID
```

## Input Validation & Security

### Validation Features
- **Bean Validation**: `@NotBlank`, `@Size`, `@NotNull` annotations
- **Input Sanitization**: Automatic trimming of whitespace
- **Data Integrity**: Unique constraints on usernames and game names

### Error Handling
- **400 Bad Request**: Invalid input data
- **404 Not Found**: Resource not found
- **409 Conflict**: Duplicate entries
- **500 Internal Server Error**: Unexpected errors

## Testing

### Run All Tests
```bash
./mvnw.cmd test
```

### Test Coverage
- **Controller Tests**: API endpoint validation and error scenarios
- **Service Tests**: Business logic and validation rules  
- **Entity Tests**: Bean validation and constraint testing


## Development Process

As I am new to Spring Boot and Java, I started by creating a small test project locally to get familiar with the framework, then built the main project for this coding challenge.

Throughout development, I used Claude (claude.ai) as a coding assistant for guidance and problem-solving support.

**Development Steps (roughly):**

1. **Getting Started**: I began by implementing basic username functionality and setting up the project structure with folders.

2. **Testing Setup**: Added test data and SwaggerConfig so I could test the API endpoints using Swagger UI.

3. **Pipeline Attempt**: Tried to set up a CI/CD pipeline, but ran into some compatibility issues with the testing framework and Java 21.

4. **Adding Features**: Built out the remaining features like skill levels (NOOB, PRO, INVINCIBLE), game management, and country filtering.

5. **Fixing Swagger**: Had some messy JSON output in Swagger, so I cleaned it up with proper annotations to make the responses more readable.

6. **Testing**: Created unit tests for controllers, services, and entities to make sure everything works correctly.

7. **Better Architecture**: Realized I should add a proper service layer to separate business logic from controllers, so I refactored the code and updated my tests.


## Test and Project structure

### Test Structure

src/test/java/
├── controller/          # API layer tests
├── service/            # Business logic tests
├── entity/             # Entity validation tests
└── GamingDirectoryApplicationTests.java

### Project Structure

src/main/java/com/example/gaming_directory/
├── config/             # Configuration classes
├── controller/         # REST controllers
├── dto/               # Data Transfer Objects
├── entity/            # JPA entities
├── enums/             # Enums (Level)
├── exception/         # Global exception handling
├── repository/        # JPA repositories
└── service/           # Business logic services
