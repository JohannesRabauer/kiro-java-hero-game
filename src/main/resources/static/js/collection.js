/**
 * Collection View - Java Hero Cards
 *
 * Displays the player's unlocked hero cards with:
 * - Card grid with front (hero name, concept title, tier badge)
 * - Card flip animation showing back (concept explanation + code example)
 * - Zoom/enlarged modal view with full content
 * - Filter controls by learning path
 * - Set completed badges and progress indicators
 * - Empty state when no cards unlocked
 */
(function () {
    'use strict';

    const PATH_ORDER = ['BRONZE', 'SILVER', 'GOLD', 'SPRING_MASTER'];

    const PATH_DISPLAY = {
        BRONZE: { name: 'Bronze', color: '#CD7F32', cssClass: 'bronze' },
        SILVER: { name: 'Silver', color: '#C0C0C0', cssClass: 'silver' },
        GOLD: { name: 'Gold', color: '#FFD700', cssClass: 'gold' },
        SPRING_MASTER: { name: 'Spring Master', color: '#6DB33F', cssClass: 'spring' }
    };

    let playerState = null;
    let collectionData = null;
    let cardContentCache = {};
    let currentFilter = 'ALL';

    async function init() {
        try {
            const [stateResponse, collectionResponse] = await Promise.all([
                fetch('/api/player/state'),
                fetch('/api/player/collection')
            ]);

            if (!stateResponse.ok) {
                throw new Error('Failed to load player state');
            }
            if (!collectionResponse.ok) {
                throw new Error('Failed to load collection');
            }

            playerState = await stateResponse.json();
            collectionData = await collectionResponse.json();

            // Fetch content for each unlocked card (for flip back)
            await fetchCardContents();

            render();
            setupFilterListeners();
            setupModalListeners();
        } catch (error) {
            showError(error.message);
        }
    }

    async function fetchCardContents() {
        if (!collectionData || collectionData.length === 0) return;

        const fetchPromises = collectionData.map(function (card) {
            return fetch('/api/content/' + encodeURIComponent(card.id))
                .then(function (response) {
                    if (response.ok) return response.json();
                    return null;
                })
                .then(function (content) {
                    if (content) {
                        cardContentCache[card.id] = content;
                    }
                })
                .catch(function () {
                    // Silently skip content that fails to load
                });
        });

        await Promise.all(fetchPromises);
    }

    function render() {
        hideLoading();

        if (!collectionData || collectionData.length === 0) {
            showEmptyState();
            return;
        }

        // Show filter bar and update progress indicators
        const filterBar = document.getElementById('filter-bar');
        filterBar.style.display = 'flex';
        updateFilterProgress();

        // Render cards
        renderCards();
    }

    function updateFilterProgress() {
        var progressMap = playerState.pathProgressMap || {};

        PATH_ORDER.forEach(function (pathKey) {
            var progressEl = document.querySelector('.filter-progress[data-path="' + pathKey + '"]');
            if (!progressEl) return;

            var progress = progressMap[pathKey];
            if (progress) {
                var text = '(' + progress.unlockedCards + '/' + progress.totalCards + ')';
                progressEl.textContent = text;

                // Add set completed badge
                var btn = progressEl.closest('.filter-btn');
                var existingBadge = btn.querySelector('.set-completed-badge');
                if (progress.completed && !existingBadge) {
                    var badge = document.createElement('span');
                    badge.className = 'set-completed-badge';
                    badge.textContent = '✓ Complete';
                    btn.appendChild(badge);
                }
            } else {
                progressEl.textContent = '(0/0)';
            }
        });
    }

    function renderCards() {
        var container = document.getElementById('cards-container');
        container.style.display = 'grid';
        container.innerHTML = '';

        var filteredCards = getFilteredCards();

        if (filteredCards.length === 0) {
            container.style.display = 'none';
            // Show a contextual message if filter yields no results but collection has cards
            if (collectionData.length > 0) {
                var msg = document.createElement('div');
                msg.className = 'loading';
                msg.textContent = 'No cards in this path yet.';
                msg.id = 'no-filter-results';
                var existing = document.getElementById('no-filter-results');
                if (existing) existing.remove();
                container.parentNode.insertBefore(msg, container.nextSibling);
            }
            return;
        }

        // Remove filter-no-results message if present
        var noResults = document.getElementById('no-filter-results');
        if (noResults) noResults.remove();

        filteredCards.forEach(function (card) {
            var cardEl = createCardElement(card);
            container.appendChild(cardEl);
        });
    }

    function getFilteredCards() {
        if (currentFilter === 'ALL') {
            return collectionData;
        }
        return collectionData.filter(function (card) {
            return card.learningPath === currentFilter;
        });
    }

    function createCardElement(card) {
        var display = PATH_DISPLAY[card.learningPath] || PATH_DISPLAY.BRONZE;
        var content = cardContentCache[card.id];

        // Wrapper with perspective
        var wrapper = document.createElement('div');
        wrapper.className = 'card-wrapper';
        wrapper.setAttribute('data-card-id', card.id);

        // Inner container for flip
        var inner = document.createElement('div');
        inner.className = 'card-inner';

        // --- FRONT ---
        var front = document.createElement('div');
        front.className = 'card-front';

        var tierBadge = document.createElement('div');
        tierBadge.className = 'card-tier-badge ' + display.cssClass;
        tierBadge.textContent = display.name;
        front.appendChild(tierBadge);

        var heroIcon = document.createElement('div');
        heroIcon.className = 'card-hero-icon';
        heroIcon.textContent = '🦸';
        front.appendChild(heroIcon);

        var heroName = document.createElement('div');
        heroName.className = 'card-hero-name';
        heroName.textContent = card.name;
        front.appendChild(heroName);

        var conceptTitle = document.createElement('div');
        conceptTitle.className = 'card-concept-title';
        conceptTitle.textContent = card.conceptTitle;
        front.appendChild(conceptTitle);

        var zoomBtn = document.createElement('button');
        zoomBtn.className = 'card-zoom-btn';
        zoomBtn.textContent = '🔍 Enlarge';
        zoomBtn.addEventListener('click', function (e) {
            e.stopPropagation();
            openModal(card);
        });
        front.appendChild(zoomBtn);

        // --- BACK ---
        var back = document.createElement('div');
        back.className = 'card-back';

        var backTitle = document.createElement('div');
        backTitle.className = 'back-title';
        backTitle.textContent = card.name;
        back.appendChild(backTitle);

        if (content) {
            var explanation = document.createElement('div');
            explanation.className = 'back-explanation';
            explanation.textContent = content.whatItIs || '';
            back.appendChild(explanation);

            if (content.codeExamples && content.codeExamples.length > 0) {
                var codeBlock = document.createElement('pre');
                codeBlock.className = 'back-code';
                codeBlock.textContent = content.codeExamples[0].code || '';
                back.appendChild(codeBlock);
            }
        } else {
            var noContent = document.createElement('div');
            noContent.className = 'back-explanation';
            noContent.textContent = 'Content not available.';
            back.appendChild(noContent);
        }

        inner.appendChild(front);
        inner.appendChild(back);
        wrapper.appendChild(inner);

        // Click to flip
        inner.addEventListener('click', function () {
            wrapper.classList.toggle('flipped');
        });

        // Double-click to open modal
        inner.addEventListener('dblclick', function (e) {
            e.preventDefault();
            openModal(card);
        });

        return wrapper;
    }

    function openModal(card) {
        var content = cardContentCache[card.id];
        var display = PATH_DISPLAY[card.learningPath] || PATH_DISPLAY.BRONZE;
        var modal = document.getElementById('card-modal');
        var body = document.getElementById('modal-body');
        body.innerHTML = '';

        // Header
        var header = document.createElement('div');
        header.className = 'modal-header';

        var icon = document.createElement('div');
        icon.className = 'modal-hero-icon';
        icon.textContent = '🦸';
        header.appendChild(icon);

        var info = document.createElement('div');
        info.className = 'modal-hero-info';

        var h2 = document.createElement('h2');
        h2.textContent = card.name;
        info.appendChild(h2);

        var conceptSpan = document.createElement('div');
        conceptSpan.className = 'modal-concept';
        conceptSpan.textContent = card.conceptTitle + ' • ' + display.name;
        info.appendChild(conceptSpan);

        header.appendChild(info);
        body.appendChild(header);

        if (content) {
            // What it is
            if (content.whatItIs) {
                var section1 = createModalSection('What it is', content.whatItIs);
                body.appendChild(section1);
            }

            // Why it matters
            if (content.whyItMatters) {
                var section2 = createModalSection('Why it matters', content.whyItMatters);
                body.appendChild(section2);
            }

            // How to use it
            if (content.howToUseIt) {
                var section3 = createModalSection('How to use it', content.howToUseIt);
                body.appendChild(section3);
            }

            // All code examples
            if (content.codeExamples && content.codeExamples.length > 0) {
                var codeSection = document.createElement('div');
                codeSection.className = 'modal-section';

                var codeH3 = document.createElement('h3');
                codeH3.textContent = 'Code Examples';
                codeSection.appendChild(codeH3);

                content.codeExamples.forEach(function (example) {
                    if (example.title) {
                        var codeTitle = document.createElement('div');
                        codeTitle.className = 'modal-code-title';
                        codeTitle.textContent = example.title;
                        codeSection.appendChild(codeTitle);
                    }
                    var codeBlock = document.createElement('pre');
                    codeBlock.className = 'modal-code-block';
                    codeBlock.textContent = example.code || '';
                    codeSection.appendChild(codeBlock);
                });

                body.appendChild(codeSection);
            }
        } else {
            var noContentMsg = document.createElement('p');
            noContentMsg.style.color = '#aaa';
            noContentMsg.textContent = 'Full content not available for this card.';
            body.appendChild(noContentMsg);
        }

        modal.style.display = 'flex';
    }

    function createModalSection(title, text) {
        var section = document.createElement('div');
        section.className = 'modal-section';

        var h3 = document.createElement('h3');
        h3.textContent = title;
        section.appendChild(h3);

        var p = document.createElement('p');
        p.textContent = text;
        section.appendChild(p);

        return section;
    }

    function closeModal() {
        var modal = document.getElementById('card-modal');
        modal.style.display = 'none';
    }

    function setupFilterListeners() {
        var buttons = document.querySelectorAll('.filter-btn');
        buttons.forEach(function (btn) {
            btn.addEventListener('click', function () {
                buttons.forEach(function (b) { b.classList.remove('active'); });
                btn.classList.add('active');
                currentFilter = btn.getAttribute('data-filter');
                renderCards();
            });
        });
    }

    function setupModalListeners() {
        var modal = document.getElementById('card-modal');
        var overlay = modal.querySelector('.modal-overlay');
        var closeBtn = modal.querySelector('.modal-close');

        overlay.addEventListener('click', closeModal);
        closeBtn.addEventListener('click', closeModal);

        document.addEventListener('keydown', function (e) {
            if (e.key === 'Escape') {
                closeModal();
            }
        });
    }

    function showEmptyState() {
        document.getElementById('empty-state').style.display = 'block';
    }

    function hideLoading() {
        document.getElementById('loading').style.display = 'none';
    }

    function showError(message) {
        hideLoading();
        var errorEl = document.getElementById('error');
        errorEl.textContent = 'Error: ' + message;
        errorEl.style.display = 'block';
    }

    // Initialize on page load
    document.addEventListener('DOMContentLoaded', init);
})();
