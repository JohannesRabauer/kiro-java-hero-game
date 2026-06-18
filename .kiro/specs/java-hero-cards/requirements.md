# Requirements Document

## Introduction

Java Hero Cards is a gamified learning application that teaches Java and Spring concepts through collectible hero cards. Players progress through structured learning paths (Bronze, Silver, Gold, Spring Master), unlocking hero cards by studying programming concepts and passing quizzes. The system combines game mechanics (experience points, card collections, animations) with educational content to create an engaging learning experience.

## Glossary

- **Game_System**: The overall Java Hero Cards application responsible for managing player progression, content delivery, and game mechanics
- **Onboarding_Module**: The component that presents the welcome animations and introduces new players to the game
- **Roadmap_View**: The component that displays the player's learning path, showing completed and locked heroes
- **Learning_Module**: The component that presents hero card content including explanations, animations, and code examples
- **Quiz_Engine**: The component responsible for presenting questions, evaluating answers, and determining pass/fail status
- **Card_Unlock_System**: The component that handles card unlock animations and adds cards to the player's collection
- **Collection_View**: The component that displays the player's unlocked cards with filtering, flipping, and zoom capabilities
- **Progression_Engine**: The component that manages experience points, path completion, and path unlocking logic
- **Hero_Card**: A collectible card representing a single Java or Spring programming concept
- **Learning_Path**: An ordered sequence of hero cards grouped by difficulty tier (Bronze, Silver, Gold, Spring Master)
- **Experience_Points**: Numeric rewards earned by the player upon successfully completing a quiz
- **Player**: The user interacting with the application to learn Java and Spring concepts

## Requirements

### Requirement 1: Player Onboarding

**User Story:** As a Player, I want to see an animated introduction when I first open the game, so that I understand how the game works before starting.

#### Acceptance Criteria

1. WHEN a Player opens the game for the first time, THE Onboarding_Module SHALL display three sequential animations in fixed order: "Collect Heroes", "Gain Skills", and "Complete Your Deck"
2. WHEN an onboarding animation finishes playing, THE Onboarding_Module SHALL automatically advance to the next animation in the sequence without requiring Player input
3. THE Onboarding_Module SHALL display a skip control that allows the Player to bypass the remaining onboarding animations and navigate directly to the Roadmap_View
4. WHEN the Player views or skips past all three onboarding animations, THE Onboarding_Module SHALL navigate the Player to the Roadmap_View and record the onboarding as completed
5. IF the Player has previously completed onboarding, THEN THE Game_System SHALL skip the Onboarding_Module and navigate directly to the Roadmap_View

### Requirement 2: Learning Path Roadmap

**User Story:** As a Player, I want to see my learning progress on a roadmap, so that I know which concepts I have completed and which are next.

#### Acceptance Criteria

1. THE Roadmap_View SHALL display four learning paths in sequential order: Bronze, Silver, Gold, Spring Master
2. THE Roadmap_View SHALL display each unlocked and completed Hero_Card with a visible "completed" indicator
3. THE Roadmap_View SHALL display each locked Hero_Card with a "locked" visual state indicating the card is not yet available
4. WHEN a Player has completed all Hero_Cards in a Learning_Path, THE Roadmap_View SHALL display that path with a "completed" indicator
5. THE Roadmap_View SHALL highlight the next available Hero_Card for the Player to learn with a distinct "active" visual state

### Requirement 3: Learning Path Structure

**User Story:** As a Player, I want learning paths organized by difficulty, so that I build knowledge progressively from basic to advanced.

#### Acceptance Criteria

1. THE Game_System SHALL organize Hero_Cards into four Learning_Paths in this fixed order: Bronze (Variable, String, Number, Boolean, If, Loop, Method), Silver (Class, Object, Constructor, Collection, Exception), Gold (Interface, Generic, Stream, Lambda, Optional, Record), Spring Master (Controller, Service, Repository, Bean, Dependency Injection)
2. THE Progression_Engine SHALL enforce sequential order within each Learning_Path, requiring the Player to pass the quiz for a Hero_Card before the next Hero_Card in the same path becomes accessible
3. WHEN a Player completes all Hero_Cards in a Learning_Path that is not the final path, THE Progression_Engine SHALL unlock the first Hero_Card of the next Learning_Path
4. THE Progression_Engine SHALL lock all Hero_Cards in paths beyond the Player's current active path
5. WHEN a new Player starts the game for the first time, THE Progression_Engine SHALL unlock only the first Hero_Card of the Bronze Learning_Path and lock all other Hero_Cards

### Requirement 4: Hero Card Learning Content

**User Story:** As a Player, I want each hero card to teach me a programming concept clearly, so that I understand what it is, why it matters, and how to use it.

#### Acceptance Criteria

1. WHEN a Player selects the next available Hero_Card, THE Learning_Module SHALL display an introduction animation for that concept lasting no longer than 5 seconds
2. THE Learning_Module SHALL present the concept explanation in three distinct sections: "What it is" (definition of the concept), "Why it matters" (practical relevance), and "How to use it" (usage guidance with context)
3. THE Learning_Module SHALL display at least one Java code example demonstrating the concept
4. WHEN the Player activates a "Start Quiz" control after the learning content is fully displayed, THE Learning_Module SHALL navigate the Player to the Quiz_Engine for that Hero_Card
5. IF the introduction animation fails to load, THEN THE Learning_Module SHALL skip the animation and display the concept explanation directly

### Requirement 5: Quiz Questions

**User Story:** As a Player, I want to answer quiz questions about a concept, so that I can test my understanding before unlocking the card.

#### Acceptance Criteria

1. WHEN a Player enters the quiz for a Hero_Card, THE Quiz_Engine SHALL present between 3 and 5 multiple-choice questions related to that Hero_Card's concept
2. THE Quiz_Engine SHALL provide between 3 and 4 answer options for each question, with exactly one correct answer
3. WHEN the Player selects an answer, THE Quiz_Engine SHALL indicate whether the answer is correct or incorrect within 1 second
4. THE Quiz_Engine SHALL present questions one at a time in sequence, advancing to the next question only after the Player answers the current question correctly
5. WHEN the Player answers all questions correctly, THE Quiz_Engine SHALL mark the quiz as passed
6. IF the Player answers a question incorrectly, THEN THE Quiz_Engine SHALL allow the Player unlimited retries on that question until answered correctly

### Requirement 6: Card Unlock and Rewards

**User Story:** As a Player, I want to see a satisfying unlock animation when I pass a quiz, so that I feel rewarded for learning.

#### Acceptance Criteria

1. WHEN a Player passes a quiz, THE Card_Unlock_System SHALL display a flip and shine animation on the Hero_Card that completes within 3 seconds
2. WHEN the unlock animation completes, THE Card_Unlock_System SHALL add the Hero_Card to the Player's collection and display a visible confirmation that the card has been unlocked
3. WHEN a Player passes a quiz, THE Progression_Engine SHALL award a fixed number of Experience_Points to the Player, where the amount is consistent for all Hero_Cards within the same Learning_Path
4. WHEN the card is added to the collection and the Learning_Path contains additional Hero_Cards, THE Game_System SHALL make the next Hero_Card in the Learning_Path available
5. WHEN the card is added to the collection and the card is the last Hero_Card in the current Learning_Path, THE Game_System SHALL display a path completion indicator and unlock the next Learning_Path if one exists

### Requirement 7: Card Collection View

**User Story:** As a Player, I want to browse my card collection, so that I can review concepts I have learned and see my progress.

#### Acceptance Criteria

1. THE Collection_View SHALL display all unlocked Hero_Cards belonging to the Player, showing the card front with the hero name, concept title, and Learning_Path tier badge
2. WHEN a Player selects a Hero_Card in the collection, THE Collection_View SHALL display a flip animation showing the card back containing the concept explanation and Java code example
3. WHEN a Player zooms into a Hero_Card, THE Collection_View SHALL display the card in an enlarged view showing the full concept explanation, all code examples, and the "What it is / Why it matters / How to use it" sections
4. THE Collection_View SHALL provide filtering options to display Hero_Cards by Learning_Path category (Bronze, Silver, Gold, Spring Master, or All)
5. WHEN a Player completes all Hero_Cards in a Learning_Path, THE Collection_View SHALL display a "set completed" badge for that path
6. WHEN the Player has no unlocked Hero_Cards, THE Collection_View SHALL display an empty state message directing the Player to the Roadmap_View
7. THE Collection_View SHALL display a progress indicator for each Learning_Path showing the number of unlocked cards versus total cards in that path

### Requirement 8: Player Progression Persistence

**User Story:** As a Player, I want my progress saved automatically, so that I can continue where I left off.

#### Acceptance Criteria

1. WHEN a Player passes a quiz, THE Game_System SHALL persist the Player's updated progress within 5 seconds, including unlocked Hero_Cards, Experience_Points, current Learning_Path, and onboarding completion status
2. WHEN a Player returns to the game after closing it, THE Game_System SHALL restore the Player's unlocked Hero_Cards, accumulated Experience_Points, current Learning_Path position, and onboarding completion status
3. IF the Game_System fails to persist progress, THEN THE Game_System SHALL notify the Player that progress could not be saved and retry the save operation up to 3 attempts
4. IF the Game_System fails to persist progress after all retry attempts are exhausted, THEN THE Game_System SHALL display a notification to the Player indicating that progress was not saved and retain the unsaved progress in the current session

### Requirement 9: Experience Points Tracking

**User Story:** As a Player, I want to see my total experience points, so that I have a sense of overall achievement.

#### Acceptance Criteria

1. THE Game_System SHALL display the Player's total accumulated Experience_Points in the Roadmap_View
2. WHEN a Player earns Experience_Points, THE Game_System SHALL update the displayed total within the same session without requiring a page reload
3. THE Progression_Engine SHALL award a fixed number of Experience_Points per Hero_Card completion that is consistent across all Hero_Cards within the same Learning_Path
4. WHEN the Player has not yet earned any Experience_Points, THE Game_System SHALL display a total of zero in the Roadmap_View
