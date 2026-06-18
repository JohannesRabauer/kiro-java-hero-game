/**
 * QuizView — Java Hero Cards
 *
 * Presents quiz questions one at a time for a given hero card.
 * - Reads heroId from URL query parameter
 * - Fetches questions from GET /api/quiz/{heroId}
 * - Displays questions sequentially with 3-4 answer options
 * - Provides immediate correct/incorrect feedback
 * - Allows unlimited retries on incorrect answers
 * - On all correct, calls POST /api/quiz/{heroId}/complete
 * - Navigates to unlock.html?heroId={heroId} on completion
 *
 * Persistence & Resilience:
 * - Saves quiz progress to sessionStorage after each correct answer
 * - Restores progress from sessionStorage on page load
 * - Shows loading/retry indicator on the complete button during save
 * - Shows non-blocking notification on save failure
 * - Clears sessionStorage on successful completion
 */
(function () {
    'use strict';

    var SESSION_KEY_PREFIX = 'quiz_progress_';

    var heroId = null;
    var questions = [];
    var currentQuestionIndex = 0;
    var isProcessing = false;
    var isCompleting = false;

    /**
     * Extract heroId from URL query parameters.
     */
    function getHeroIdFromUrl() {
        var params = new URLSearchParams(window.location.search);
        return params.get('heroId');
    }

    /**
     * Get the sessionStorage key for this quiz.
     */
    function getSessionKey() {
        return SESSION_KEY_PREFIX + heroId;
    }

    /**
     * Save current quiz progress to sessionStorage.
     */
    function saveProgressToSession() {
        try {
            var progress = {
                heroId: heroId,
                currentQuestionIndex: currentQuestionIndex,
                timestamp: Date.now()
            };
            sessionStorage.setItem(getSessionKey(), JSON.stringify(progress));
        } catch (e) {
            // sessionStorage might be unavailable — ignore silently
        }
    }

    /**
     * Restore quiz progress from sessionStorage if available.
     * Returns the saved question index, or 0 if none found.
     */
    function restoreProgressFromSession() {
        try {
            var saved = sessionStorage.getItem(getSessionKey());
            if (saved) {
                var progress = JSON.parse(saved);
                if (progress.heroId === heroId && typeof progress.currentQuestionIndex === 'number') {
                    return progress.currentQuestionIndex;
                }
            }
        } catch (e) {
            // Ignore parsing errors
        }
        return 0;
    }

    /**
     * Clear quiz progress from sessionStorage.
     */
    function clearSessionProgress() {
        try {
            sessionStorage.removeItem(getSessionKey());
        } catch (e) {
            // Ignore
        }
    }

    /**
     * Show a loading state.
     */
    function showLoading() {
        document.getElementById('loading').style.display = 'block';
        document.getElementById('quiz-card').style.display = 'none';
        document.getElementById('error').style.display = 'none';
    }

    /**
     * Show an error message.
     */
    function showError(message) {
        document.getElementById('loading').style.display = 'none';
        document.getElementById('quiz-card').style.display = 'none';
        document.getElementById('error').style.display = 'block';
        document.getElementById('error-text').textContent = message;
    }

    /**
     * Show the quiz card.
     */
    function showQuizCard() {
        document.getElementById('loading').style.display = 'none';
        document.getElementById('error').style.display = 'none';
        document.getElementById('quiz-card').style.display = 'block';
    }

    /**
     * Fetch quiz questions from the backend.
     */
    async function fetchQuestions() {
        var response = await fetch('/api/quiz/' + encodeURIComponent(heroId));
        if (!response.ok) {
            throw new Error('Failed to load quiz questions');
        }
        return response.json();
    }

    /**
     * Render the current question with its answer options.
     */
    function renderQuestion() {
        var question = questions[currentQuestionIndex];
        var totalQuestions = questions.length;

        // Update progress
        var progressText = document.getElementById('progress-text');
        progressText.textContent = 'Question ' + (currentQuestionIndex + 1) + ' of ' + totalQuestions;

        var progressBarFill = document.getElementById('progress-bar-fill');
        var progressPercent = (currentQuestionIndex / totalQuestions) * 100;
        progressBarFill.style.width = progressPercent + '%';

        // Update question text
        document.getElementById('question-text').textContent = question.questionText;

        // Clear feedback
        var feedback = document.getElementById('feedback');
        feedback.textContent = '';
        feedback.className = 'feedback';

        // Hide complete indicator if visible
        hideCompleteIndicator();

        // Render options
        var optionsList = document.getElementById('options-list');
        optionsList.innerHTML = '';

        question.options.forEach(function (option) {
            var btn = document.createElement('button');
            btn.className = 'option-btn';
            btn.textContent = option.text;
            btn.setAttribute('data-option-id', option.id);
            btn.addEventListener('click', function () {
                handleAnswerClick(option.id, btn);
            });
            optionsList.appendChild(btn);
        });
    }

    /**
     * Handle an answer button click.
     */
    async function handleAnswerClick(answerId, buttonElement) {
        if (isProcessing) return;
        isProcessing = true;

        // Disable all option buttons during processing
        var allButtons = document.querySelectorAll('.option-btn');
        allButtons.forEach(function (btn) {
            btn.disabled = true;
        });

        try {
            var response = await fetch('/api/quiz/' + encodeURIComponent(heroId) + '/answer', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({
                    questionId: questions[currentQuestionIndex].id,
                    answerId: answerId
                })
            });

            if (!response.ok) {
                throw new Error('Failed to submit answer');
            }

            var result = await response.json();
            var feedback = document.getElementById('feedback');

            if (result.correct) {
                // Correct answer
                buttonElement.classList.add('correct');
                feedback.textContent = '\u2713 Correct!';
                feedback.className = 'feedback correct';

                // Save progress to sessionStorage
                saveProgressToSession();

                // Advance after 1 second
                setTimeout(function () {
                    advanceToNextQuestion();
                }, 1000);
            } else {
                // Incorrect answer
                buttonElement.classList.add('incorrect');
                feedback.textContent = '\u2717 Try again!';
                feedback.className = 'feedback incorrect';

                // Re-enable buttons after a brief delay so user can retry
                setTimeout(function () {
                    resetOptionsForRetry(buttonElement);
                    isProcessing = false;
                }, 800);
            }
        } catch (error) {
            console.error('Error submitting answer:', error);
            var feedback = document.getElementById('feedback');
            feedback.textContent = 'Network error. Please try again.';
            feedback.className = 'feedback incorrect';

            // Re-enable buttons
            allButtons.forEach(function (btn) {
                btn.disabled = false;
            });
            isProcessing = false;
        }
    }

    /**
     * Reset option buttons for retry after an incorrect answer.
     * Keeps the incorrect highlight but re-enables other buttons.
     */
    function resetOptionsForRetry(incorrectButton) {
        var allButtons = document.querySelectorAll('.option-btn');
        allButtons.forEach(function (btn) {
            if (btn !== incorrectButton) {
                btn.disabled = false;
            }
        });
    }

    /**
     * Advance to the next question or complete the quiz.
     */
    function advanceToNextQuestion() {
        currentQuestionIndex++;

        if (currentQuestionIndex >= questions.length) {
            completeQuiz();
            return;
        }

        // Save updated index to session
        saveProgressToSession();

        // Animate transition
        var quizCard = document.getElementById('quiz-card');
        quizCard.classList.add('fade-out');

        setTimeout(function () {
            renderQuestion();
            quizCard.classList.remove('fade-out');
            quizCard.classList.add('fade-in');

            // Remove fade-in class after animation completes
            setTimeout(function () {
                quizCard.classList.remove('fade-in');
            }, 300);

            isProcessing = false;
        }, 300);
    }

    /**
     * Show the completing/retry indicator.
     */
    function showCompleteIndicator() {
        isCompleting = true;
        var indicator = document.getElementById('complete-indicator');
        if (indicator) {
            indicator.style.display = 'flex';
        }
        // Disable all option buttons while completing
        var allButtons = document.querySelectorAll('.option-btn');
        allButtons.forEach(function (btn) {
            btn.disabled = true;
        });
    }

    /**
     * Hide the completing/retry indicator.
     */
    function hideCompleteIndicator() {
        isCompleting = false;
        var indicator = document.getElementById('complete-indicator');
        if (indicator) {
            indicator.style.display = 'none';
        }
    }

    /**
     * Complete the quiz by calling the backend and navigating to unlock view.
     * On failure, shows a non-blocking notification and retains session progress.
     */
    async function completeQuiz() {
        showCompleteIndicator();

        try {
            var response = await fetch('/api/quiz/' + encodeURIComponent(heroId) + '/complete', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' }
            });

            if (!response.ok) {
                throw new Error('Failed to complete quiz');
            }

            // Success — clear session progress and navigate
            clearSessionProgress();
            window.location.href = '/unlock.html?heroId=' + encodeURIComponent(heroId);
        } catch (error) {
            console.error('Error completing quiz:', error);
            hideCompleteIndicator();

            // Show non-blocking notification — do NOT navigate away
            if (typeof window.showNotification === 'function') {
                window.showNotification(
                    'Progress could not be saved. Your session progress is retained.',
                    'error'
                );
            }

            // Keep progress in sessionStorage so it survives refresh
            saveProgressToSession();

            // Re-enable interaction so user can retry or continue
            isProcessing = false;

            // Show a retry button in the feedback area
            var feedback = document.getElementById('feedback');
            feedback.innerHTML = '';
            feedback.className = 'feedback';

            var retryBtn = document.createElement('button');
            retryBtn.className = 'option-btn retry-complete-btn';
            retryBtn.textContent = 'Retry Save';
            retryBtn.addEventListener('click', function () {
                feedback.innerHTML = '';
                feedback.className = 'feedback';
                completeQuiz();
            });
            feedback.appendChild(retryBtn);
        }
    }

    /**
     * Initialize the quiz view.
     */
    async function init() {
        heroId = getHeroIdFromUrl();

        if (!heroId) {
            showError('No hero specified. Please return to the roadmap.');
            return;
        }

        showLoading();

        try {
            questions = await fetchQuestions();

            if (!questions || questions.length === 0) {
                showError('No quiz questions found for this hero.');
                return;
            }

            // Restore progress from sessionStorage
            var savedIndex = restoreProgressFromSession();
            if (savedIndex > 0 && savedIndex < questions.length) {
                currentQuestionIndex = savedIndex;
            } else {
                currentQuestionIndex = 0;
            }

            showQuizCard();
            renderQuestion();
        } catch (error) {
            console.error('Error loading quiz:', error);
            showError('Failed to load quiz. Please try again.');
        }
    }

    // --- DOM Ready ---
    document.addEventListener('DOMContentLoaded', init);
})();
