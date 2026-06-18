/**
 * OnboardingView — Java Hero Cards
 *
 * Displays three sequential animation slides on first visit:
 *   1. "Collect Heroes"
 *   2. "Gain Skills"
 *   3. "Complete Your Deck"
 *
 * Auto-advances between slides after 3 seconds each.
 * Provides a skip button to bypass remaining slides.
 * Calls POST /api/player/onboarding/complete on finish or skip,
 * then navigates to the Roadmap view.
 */
(function () {
    'use strict';

    const SLIDE_DURATION_MS = 3000;
    const TRANSITION_DURATION_MS = 600;

    let currentSlide = 0;
    let autoAdvanceTimer = null;
    let isTransitioning = false;

    /**
     * Check player state and decide whether to show onboarding or redirect.
     */
    async function init() {
        try {
            const response = await fetch('/api/player/state');
            if (!response.ok) {
                throw new Error('Failed to fetch player state');
            }
            const state = await response.json();

            if (state.onboardingCompleted) {
                navigateToRoadmap();
                return;
            }

            showOnboarding();
        } catch (error) {
            console.error('Error fetching player state:', error);
            // On error, show onboarding anyway so the player isn't stuck
            showOnboarding();
        }
    }

    /**
     * Show the onboarding container and start the slide sequence.
     */
    function showOnboarding() {
        const loading = document.getElementById('loading');
        const onboarding = document.getElementById('onboarding');

        if (loading) loading.style.display = 'none';
        if (onboarding) onboarding.style.display = 'flex';

        showSlide(0);
        scheduleAutoAdvance();
    }

    /**
     * Display a specific slide by index.
     */
    function showSlide(index) {
        const slides = document.querySelectorAll('.slide');
        const dots = document.querySelectorAll('.dot');

        slides.forEach(function (slide, i) {
            if (i === index) {
                slide.classList.add('active');
                slide.classList.remove('fade-out');
            } else {
                slide.classList.remove('active');
                slide.classList.remove('fade-out');
            }
        });

        dots.forEach(function (dot, i) {
            if (i === index) {
                dot.classList.add('active');
            } else {
                dot.classList.remove('active');
            }
        });

        currentSlide = index;
    }

    /**
     * Advance to the next slide with a fade-out/fade-in transition.
     */
    function advanceSlide() {
        if (isTransitioning) return;

        const slides = document.querySelectorAll('.slide');
        const nextIndex = currentSlide + 1;

        if (nextIndex >= slides.length) {
            completeOnboarding();
            return;
        }

        isTransitioning = true;

        // Fade out current slide
        slides[currentSlide].classList.add('fade-out');

        setTimeout(function () {
            showSlide(nextIndex);
            isTransitioning = false;
            scheduleAutoAdvance();
        }, TRANSITION_DURATION_MS);
    }

    /**
     * Schedule the next auto-advance after SLIDE_DURATION_MS.
     */
    function scheduleAutoAdvance() {
        clearAutoAdvance();
        autoAdvanceTimer = setTimeout(function () {
            advanceSlide();
        }, SLIDE_DURATION_MS);
    }

    /**
     * Clear any pending auto-advance timer.
     */
    function clearAutoAdvance() {
        if (autoAdvanceTimer !== null) {
            clearTimeout(autoAdvanceTimer);
            autoAdvanceTimer = null;
        }
    }

    /**
     * Skip all remaining slides and complete onboarding immediately.
     */
    function skipOnboarding() {
        clearAutoAdvance();
        completeOnboarding();
    }

    /**
     * Call the backend to mark onboarding as complete, then navigate to roadmap.
     */
    async function completeOnboarding() {
        clearAutoAdvance();

        try {
            await fetch('/api/player/onboarding/complete', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' }
            });
        } catch (error) {
            console.error('Error completing onboarding:', error);
            // Navigate anyway — the player shouldn't be stuck
        }

        navigateToRoadmap();
    }

    /**
     * Navigate to the Roadmap view.
     */
    function navigateToRoadmap() {
        window.location.href = '/roadmap.html';
    }

    // --- DOM Ready ---
    document.addEventListener('DOMContentLoaded', function () {
        var skipBtn = document.getElementById('skip-btn');
        if (skipBtn) {
            skipBtn.addEventListener('click', skipOnboarding);
        }

        init();
    });
})();
