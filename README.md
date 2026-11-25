# Teammate App (Java, OOP)

A team management system built for my Object-Oriented Design coursework (CM2601),
focused on applying clean OOP principles.

## Design
| Class | Role |
|---|---|
| `User` | Base class for all users |
| `Organizer` | Creates and manages teams (extends `User`) |
| `Player` | Joins teams (extends `User`) |
| `Team` | Holds players and team details |
| `enums/` | Fixed values (roles, statuses, etc.) |

## OOP concepts applied
- **Inheritance**: `Organizer` and `Player` extend `User`
- **Encapsulation**: private fields with getters/setters
- **Polymorphism**: shared behaviour overridden per user type
- **Enums** for type-safe fixed values

## Run it
Open the `Teammate app/Teammate` folder in IntelliJ / Eclipse and run the main class.
