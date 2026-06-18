/**
 * Card Unlock View - Java Hero Cards
 *
 * Displays a flip + shine animation for unlocked hero cards,
 * shows confirmation with XP earned, and navigates to the next hero or path completion.
 */
(function () {
    'use strict';

    var ANIMATION_DURATION_MS = 3000;
    var ANIMATION_TIMEOUT_MS = 3500; // Fallback if animation events don't fire

    var PATH_DISPLAY = {
        BRONZE: { name: 'Bronze', cssClass: 'bronze', xp: 10 },
        SILVER: { name: 'Silver', cssClass: 'silver', xp: 20 },
        GOLD: { name: 'Gold', cssClass: 'gold', xp: 30 },
        SPRING_MASTER: { name: 'Spring Master', cssClass: 'spring', xp: 50 }
    };

    var playerState = null;
    var heroCard = null;
    var heroId = null;

    function init() {
        heroId = getHeroIdFromUrl();

        if (!heroId) {
            showError('No hero card specified.');
            return;
        }

        loadData();
    }

    function getHeroIdFromUrl() {
        var params = new URLSearchParams(window.location.search);
        return params.get('heroId');
    }

    async function loadData() {
        try {
            var stateResponse = await fetch('/api/player/state');
            if (!stateResponse.ok) {
                throw new Error('Failed to load player state');
            }
            playerState = await stateResponse.json();

            // Fetch hero card details from paths data
            var pathsResponse = await fetch('/api/paths');
            if (!pathsResponse.ok) {
                throw new Error('Failed to load paths data');
            }
            var pathsData = await pathsResponse.json();
            heroCard = findHeroCard(pathsData, heroId);

            if (!heroCard) {
                throw new Error('Hero card not found');
            }

            showUnlockAnimation();
        } catch (error) {
            showError(error.message);
        }
    }

    function findHeroCard(pathsData, targetHeroId) {
        for (var i = 0; i < pathsData.length; i++) {
            var cards = pathsData[i].cards || [];
            for (var j = 0; j < cards.length; j++) {
                if (cards[j].id === targetHeroId) {
                    return cards[j];
                }
            }
        }
        return null;
    }

    function showUnlockAnimation() {
        hideLoading();

        var container = document.getElementById('unlock-container');
        container.style.display = 'flex';

        // Populate card front content
        populateCardContent();

        // Start animation with a small delay for smooth transition
        setTimeout(function () {
            startFlipAnimation();
        }, 300);
    }

    function populateCardContent() {
        var pathKey = heroCard.learningPath;
        var display = PATH_DISPLAY[pathKey];

        var tierBadge = document.getElementById('card-tier-badge');
        tierBadge.textContent = display ? display.name : pathKey;
        tierBadge.className = 'card-tier-badge ' + (display ? display.cssClass : '');

        document.getElementById('card-hero-name').textContent = heroCard.name || '';
        document.getElementById('card-concept-title').textContent = heroCard.conceptTitle || '';
    }

    function startFlipAnimation() {
        var card3d = document.querySelector('.card-3d');
        var shineOverlay = document.querySelector('.shine-overlay');

        // Start the flip
        card3d.classList.add('flipping');
        shineOverlay.classList.add('shining');

        // Set a fallback timeout in case animation events fail (Requirement 6.1 implied)
        var fallbackTimer = setTimeout(function () {
            onAnimationComplete();
        }, ANIMATION_TIMEOUT_MS);

        // Listen for the shine animation end (last animation to finish)
        shineOverlay.addEventListener('animationend', function handler() {
            clearTimeout(fallbackTimer);
            shineOverlay.removeEventListener('animationend', handler);
            onAnimationComplete();
        });
    }

    function onAnimationComplete() {
        showConfirmation();
    }

    function showConfirmation() {
        var confirmation = document.getElementById('unlock-confirmation');
        if (confirmation.style.display === 'flex') {
            return; // Already shown (prevent duplicate from fallback + event)
        }
        confirmation.style.display = 'flex';

        // Populate confirmation content
        document.getElementById('confirm-hero-name').textContent = heroCard.name || '';
        document.getElementById('confirm-concept').textContent = heroCard.conceptTitle || '';

        // XP info
        var pathKey = heroCard.learningPath;
        var display = PATH_DISPLAY[pathKey];
        var xpEarned = display ? display.xp : 10;
        var totalXp = playerState.totalExperiencePoints || 0;

        document.getElementById('xp-earned').textContent = '+' + xpEarned + ' XP';
        document.getElementById('total-xp').textContent = 'Total: ' + totalXp + ' XP';

        // Determine navigation and path completion state
        setupContinueButton();
    }

    function setupContinueButton() {
        var continueBtn = document.getElementById('continue-btn');
        var pathCompleteMsg = document.getElementById('path-complete-msg');

        var nextHeroId = playerState.nextAvailableHeroId;
        var allPathsComplete = isAllPathsComplete();
        var currentPathJustCompleted = isCurrentPathCompleted();

        if (allPathsComplete) {
            // All paths complete — congratulations!
            pathCompleteMsg.textContent = '🏆 Congratulations! All cards collected!';
            pathCompleteMsg.style.display = 'block';
            continueBtn.textContent = 'View Collection →';
            continueBtn.addEventListener('click', function () {
                window.location.href = 'roadmap.html';
            });
        } else if (currentPathJustCompleted) {
            // Current path just completed — show path complete message
            pathCompleteMsg.textContent = '✨ Path Complete!';
            pathCompleteMsg.style.display = 'block';
            continueBtn.textContent = 'Continue →';
            continueBtn.addEventListener('click', function () {
                if (nextHeroId) {
                    window.location.href = 'learning.html?heroId=' + encodeURIComponent(nextHeroId);
                } else {
                    window.location.href = 'roadmap.html';
                }
            });
        } else if (nextHeroId) {
            // Next hero available — navigate to learning
            continueBtn.textContent = 'Continue →';
            continueBtn.addEventListener('click', function () {
                window.location.href = 'learning.html?heroId=' + encodeURIComponent(nextHeroId);
            });
        } else {
            // Fallback: navigate to roadmap
            continueBtn.textContent = 'Back to Roadmap →';
            continueBtn.addEventListener('click', function () {
                window.location.href = 'roadmap.html';
            });
        }
    }

    function isAllPathsComplete() {
        var pathMap = playerState.pathProgressMap;
        if (!pathMap) return false;

        var paths = ['BRONZE', 'SILVER', 'GOLD', 'SPRING_MASTER'];
        for (var i = 0; i < paths.length; i++) {
            var progress = pathMap[paths[i]];
            if (!progress || !progress.completed) {
                return false;
            }
        }
        return true;
    }

    function isCurrentPathCompleted() {
        var pathMap = playerState.pathProgressMap;
        if (!pathMap) return false;

        var heroPathKey = heroCard.learningPath;
        var progress = pathMap[heroPathKey];
        return progress && progress.completed;
    }

    function hideLoading() {
        var loading = document.getElementById('loading');
        if (loading) {
            loading.style.display = 'none';
        }
    }

    function showError(message) {
        hideLoading();
        var container = document.getElementById('unlock-container');
        if (container) {
            container.style.display = 'none';
        }

        var errorState = document.getElementById('error-state');
        errorState.style.display = 'flex';
        document.getElementById('error-message').textContent = message;

        // Fallback: show static confirmation immediately (Requirement 6.1)
        document.getElementById('retry-btn').addEventListener('click', function () {
            errorState.style.display = 'none';
            window.location.reload();
        });
    }

    // Initialize on page load
    document.addEventListener('DOMContentLoaded', init);
})();
