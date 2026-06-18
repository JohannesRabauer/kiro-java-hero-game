# Implementation Plan: Java Hero Cards

## Overview

This plan implements the Java Hero Cards gamified learning application as a Spring Boot backend with a reactive frontend. Tasks are organized to build the data layer first, then core services, then controllers, then frontend views, wiring everything together incrementally.

## Tasks

- [x] 1. Set up project structure and core data models
  - [x] 1.1 Initialize Spring Boot project with Maven/Gradle, configure dependencies (Spring Web, Spring Data JPA, H2, jqwik)
    - Create project skeleton with `pom.xml` or `build.gradle`
    - Add dependencies: spring-boot-starter-web, spring-boot-starter-data-jpa, h2, jqwik
    - Configure application.properties for H2 in-memory database
    - _Requirements: 8.1, 8.2_

  - [x] 1.2 Create the LearningPath enum and core data model classes
    - Implement `LearningPath` enum with order and xpPerCard fields (BRONZE=10, SILVER=20, GOLD=30, SPRING_MASTER=50)
    - Implement `Player`, `HeroCard`, `HeroContent`, `CodeExample`, `QuizQuestion`, `AnswerOption` entities
    - Implement `PlayerState` and `PathProgress` DTOs
    - _Requirements: 3.1, 9.3_

  - [x] 1.3 Create JPA repositories for Player and Content data access
    - Implement `PlayerRepository` with methods for finding/saving player state
    - Implement `ContentRepository` (HeroCard, HeroContent, QuizQuestion lookups)
    - Add JPA annotations to entity classes
    - _Requirements: 8.1, 8.2_

  - [x] 1.4 Create database seed data for all hero cards, content, and quiz questions
    - Create SQL or Java-based data initializer with all 24 hero cards across 4 paths
    - Populate hero content (whatItIs, whyItMatters, howToUseIt, codeExamples) for each card
    - Populate 3-5 quiz questions per hero card with 3-4 answer options each
    - _Requirements: 3.1, 4.2, 4.3, 5.1, 5.2_

- [x] 2. Implement ProgressionService
  - [x] 2.1 Implement core progression logic (getPlayerState, unlockCard, getNextAvailableHero)
    - Implement `getPlayerState(playerId)` returning full player state
    - Implement `unlockCard(playerId, heroId)` with XP award and next-card unlock
    - Implement `getNextAvailableHero(playerId)` finding first non-unlocked card in earliest incomplete path
    - Implement `isHeroAccessible(playerId, heroId)` enforcing sequential order
    - Implement `completeOnboarding(playerId)`
    - _Requirements: 3.2, 3.3, 3.4, 3.5, 6.3, 6.4, 6.5, 1.4, 1.5_

  - [x] 2.2 Write property test: Card state rendering consistency
    - **Property 1: Card state rendering consistency**
    - For any player state and hero card, the display state equals "completed" iff card is in unlocked set
    - **Validates: Requirements 2.2, 2.3**

  - [x] 2.3 Write property test: Path completion detection
    - **Property 2: Path completion detection**
    - For any player state and learning path, path is "completed" iff every card in that path is unlocked
    - **Validates: Requirements 2.4, 7.5**

  - [x] 2.4 Write property test: Next available hero determination
    - **Property 3: Next available hero determination**
    - For any incomplete player state, exactly one hero is "active" — the first non-unlocked card in earliest incomplete path
    - **Validates: Requirements 2.5**

  - [x] 2.5 Write property test: Sequential progression invariant
    - **Property 4: Sequential progression invariant**
    - For any accessible card, all predecessors in the same path are unlocked; no card beyond active path is accessible
    - **Validates: Requirements 3.2, 3.4, 6.2, 6.4**

  - [x] 2.6 Write property test: Path transition unlocking
    - **Property 5: Path transition unlocking**
    - When all cards in a non-final path are unlocked, the first card of the next path is accessible
    - **Validates: Requirements 3.3, 6.5**

  - [x] 2.7 Write property test: XP award consistency
    - **Property 10: XP award consistency**
    - XP awarded equals the fixed value defined for the card's learning path
    - **Validates: Requirements 6.3, 9.3**

- [~] 3. Checkpoint - Core progression logic
  - Ensure all tests pass, ask the user if questions arise.

- [x] 4. Implement QuizService
  - [x] 4.1 Implement quiz logic (getQuestions, evaluateAnswer, isQuizComplete)
    - Implement `getQuestions(heroId)` returning 3-5 questions for a hero card
    - Implement `evaluateAnswer(heroId, questionId, answerId)` checking correctness
    - Implement `isQuizComplete(playerId, heroId)` tracking answered questions per session
    - Maintain quiz session state tracking which questions are answered correctly
    - _Requirements: 5.1, 5.2, 5.3, 5.4, 5.5, 5.6_

  - [x] 4.2 Write property test: Quiz structure validity
    - **Property 7: Quiz structure validity**
    - Each hero's quiz has 3-5 questions, each with 3-4 options and exactly one correct
    - **Validates: Requirements 5.1, 5.2**

  - [x] 4.3 Write property test: Quiz sequential advancement
    - **Property 8: Quiz sequential advancement**
    - Next question only available after current is answered correctly; incorrect leaves state unchanged
    - **Validates: Requirements 5.4, 5.6**

  - [x] 4.4 Write property test: Quiz completion logic
    - **Property 9: Quiz completion logic**
    - Quiz is "passed" iff every question has been answered correctly
    - **Validates: Requirements 5.5**

- [x] 5. Implement ContentService
  - [x] 5.1 Implement content retrieval (getHeroContent, getLearningPaths, getPathProgress)
    - Implement `getHeroContent(heroId)` returning structured learning content
    - Implement `getLearningPaths()` returning all paths with hero metadata and lock status
    - Implement `getPathProgress(playerId, pathId)` returning unlocked/total counts
    - _Requirements: 4.2, 4.3, 7.7_

  - [x] 5.2 Write property test: Hero content structure validity
    - **Property 6: Hero content structure validity**
    - Every hero card has non-empty whatItIs, whyItMatters, howToUseIt, and at least one code example
    - **Validates: Requirements 4.2, 4.3**

  - [x] 5.3 Write property test: Collection filtering correctness
    - **Property 11: Collection filtering correctness**
    - Filtered cards all belong to selected path; "All" filter returns every unlocked card
    - **Validates: Requirements 7.4**

  - [x] 5.4 Write property test: Progress calculation accuracy
    - **Property 12: Progress calculation accuracy**
    - Progress ratio matches actual unlocked count / total cards in path
    - **Validates: Requirements 7.7**

- [x] 6. Implement PlayerService and persistence with retry logic
  - [x] 6.1 Implement PlayerService (save, restore, retry logic)
    - Implement save method with retry mechanism (up to 3 attempts, exponential backoff)
    - Implement restore method loading full player state from database
    - Implement error handling: notify player on save failure, retain session state
    - Handle new player creation with initial state (first Bronze card unlocked only)
    - _Requirements: 8.1, 8.2, 8.3, 8.4, 3.5_

  - [x] 6.2 Write property test: Player state persistence round-trip
    - **Property 13: Player state persistence round-trip**
    - Serialize then deserialize produces equivalent player state
    - **Validates: Requirements 8.2**

- [~] 7. Checkpoint - All services complete
  - Ensure all tests pass, ask the user if questions arise.

- [x] 8. Implement REST API Controllers
  - [x] 8.1 Implement PlayerController endpoints
    - `GET /api/player/state` — returns full PlayerState DTO
    - `POST /api/player/onboarding/complete` — marks onboarding done
    - `GET /api/player/collection` — returns unlocked cards with details
    - _Requirements: 1.4, 1.5, 7.1, 9.1, 9.4_

  - [x] 8.2 Implement ContentController endpoints
    - `GET /api/content/{heroId}` — returns HeroContent for a card
    - `GET /api/paths` — returns all learning paths with hero cards and lock/unlock status
    - _Requirements: 4.2, 4.3, 2.1, 2.2, 2.3_

  - [x] 8.3 Implement QuizController endpoints
    - `GET /api/quiz/{heroId}` — returns quiz questions
    - `POST /api/quiz/{heroId}/answer` — evaluates answer, returns correct/incorrect within 1 second
    - `POST /api/quiz/{heroId}/complete` — marks quiz passed, triggers card unlock and progression
    - _Requirements: 5.1, 5.3, 5.5, 6.1, 6.2, 6.3_

  - [x] 8.4 Write integration tests for REST API endpoints
    - Test all controller endpoints with MockMvc
    - Test complete flow: learn → quiz → unlock → progression
    - Test error responses for locked content, invalid hero IDs
    - _Requirements: 8.1, 8.2, 5.3_

- [ ] 9. Implement Frontend - Onboarding and Roadmap Views
  - [-] 9.1 Implement OnboardingView
    - Create three sequential animations: "Collect Heroes", "Gain Skills", "Complete Your Deck"
    - Implement auto-advance between animations
    - Add skip control to bypass remaining animations
    - Call `POST /api/player/onboarding/complete` on completion/skip
    - Navigate to RoadmapView after onboarding
    - _Requirements: 1.1, 1.2, 1.3, 1.4, 1.5_

  - [-] 9.2 Implement RoadmapView
    - Display four learning paths (Bronze, Silver, Gold, Spring Master) in sequential order
    - Render hero cards with visual states: completed, active, locked
    - Highlight next available hero card with distinct "active" state
    - Display total XP and path completion indicators
    - _Requirements: 2.1, 2.2, 2.3, 2.4, 2.5, 9.1, 9.2, 9.4_

- [ ] 10. Implement Frontend - Learning and Quiz Views
  - [-] 10.1 Implement LearningView
    - Display intro animation (max 5 seconds) with fallback on failure
    - Show concept explanation in three sections: "What it is", "Why it matters", "How to use it"
    - Display Java code examples
    - Provide "Start Quiz" button navigating to QuizView
    - _Requirements: 4.1, 4.2, 4.3, 4.4, 4.5_

  - [-] 10.2 Implement QuizView
    - Present questions one at a time in sequence
    - Display 3-4 answer options per question
    - Show immediate correct/incorrect feedback (within 1 second)
    - Allow unlimited retries on incorrect answers
    - Advance to next question only after correct answer
    - On all correct, call `POST /api/quiz/{heroId}/complete`
    - _Requirements: 5.1, 5.2, 5.3, 5.4, 5.5, 5.6_

  - [-] 10.3 Implement CardUnlockView
    - Play flip and shine animation (max 3 seconds)
    - Display unlock confirmation and XP earned
    - Navigate to next available hero or path completion screen
    - _Requirements: 6.1, 6.2, 6.3, 6.4, 6.5_

- [ ] 11. Implement Frontend - Collection View
  - [~] 11.1 Implement CollectionView
    - Display grid of unlocked cards with hero name, concept title, and tier badge
    - Implement card flip animation showing back (concept explanation + code example)
    - Implement zoom/enlarged view with full content
    - Add filter controls by learning path (Bronze, Silver, Gold, Spring Master, All)
    - Show "set completed" badges for fully unlocked paths
    - Show progress indicator per path (unlocked/total)
    - Display empty state message directing to Roadmap when no cards unlocked
    - _Requirements: 7.1, 7.2, 7.3, 7.4, 7.5, 7.6, 7.7_

- [ ] 12. Implement XP display and real-time updates
  - [~] 12.1 Implement real-time XP tracking in frontend
    - Display total XP in RoadmapView, updated within same session without page reload
    - Update XP display immediately after quiz completion
    - Show zero XP for new players
    - _Requirements: 9.1, 9.2, 9.3, 9.4_

- [ ] 13. Implement error handling and persistence fallbacks
  - [~] 13.1 Implement persistence retry and fallback UI
    - Show non-blocking notification on save failure
    - Retain unsaved progress in client session state
    - Implement retry indicator during save attempts
    - _Requirements: 8.3, 8.4_

  - [~] 13.2 Implement animation and content loading error handling
    - Skip intro animation on load failure, show content directly
    - Show retry button on content load failure
    - Navigate back to learning view on quiz load failure with error notification
    - Static confirmation fallback for unlock animation failure
    - _Requirements: 4.5_

- [~] 14. Final checkpoint - Full integration
  - Ensure all tests pass, ask the user if questions arise.

## Notes

- Tasks marked with `*` are optional and can be skipped for faster MVP
- Each task references specific requirements for traceability
- Checkpoints ensure incremental validation
- Property tests use jqwik with minimum 100 iterations per property
- Unit tests and property tests are complementary — property tests validate universal invariants, unit tests cover specific edge cases
- Backend is built first (tasks 1-8), then frontend (tasks 9-13), enabling parallel frontend development once API is stable
- All hero card content (24 cards across 4 paths) must be seeded in task 1.4

## Task Dependency Graph

```json
{
  "waves": [
    { "id": 0, "tasks": ["1.1"] },
    { "id": 1, "tasks": ["1.2"] },
    { "id": 2, "tasks": ["1.3", "1.4"] },
    { "id": 3, "tasks": ["2.1", "4.1", "5.1", "6.1"] },
    { "id": 4, "tasks": ["2.2", "2.3", "2.4", "2.5", "2.6", "2.7", "4.2", "4.3", "4.4", "5.2", "5.3", "5.4", "6.2"] },
    { "id": 5, "tasks": ["8.1", "8.2", "8.3"] },
    { "id": 6, "tasks": ["8.4"] },
    { "id": 7, "tasks": ["9.1", "9.2", "10.1", "10.2", "10.3", "11.1", "12.1"] },
    { "id": 8, "tasks": ["13.1", "13.2"] }
  ]
}
```
