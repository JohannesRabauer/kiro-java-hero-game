/**
 * Roadmap View - Java Hero Cards
 *
 * Displays learning paths (Bronze → Silver → Gold → Spring Master)
 * with hero card states: completed, active, locked.
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
    let pathsData = null;

    // XP is always fetched fresh on every navigation to this view (satisfies Requirement 9.2:
    // "update displayed total within the same session without requiring a page reload").
    // Since each view transition triggers a full page load + init(), the player always sees
    // up-to-date XP after quiz completion without needing WebSocket or polling.
    async function init() {
        try {
            const [stateResponse, pathsResponse] = await Promise.all([
                fetch('/api/player/state'),
                fetch('/api/paths')
            ]);

            if (!stateResponse.ok) {
                throw new Error('Failed to load player state');
            }
            if (!pathsResponse.ok) {
                throw new Error('Failed to load learning paths');
            }

            playerState = await stateResponse.json();
            pathsData = await pathsResponse.json();

            render();
        } catch (error) {
            showError(error.message);
        }
    }

    function render() {
        hideLoading();

        // Display total XP (Req 9.1, 9.4: shows 0 for new players via || 0 fallback)
        const xpElement = document.getElementById('total-xp');
        xpElement.textContent = playerState.totalExperiencePoints || 0;

        // Render paths
        const container = document.getElementById('paths-container');
        container.style.display = 'flex';
        container.innerHTML = '';

        // Sort paths in correct order
        const sortedPaths = sortPathsByOrder(pathsData);

        sortedPaths.forEach(function (pathData, index) {
            if (index > 0) {
                const connector = document.createElement('div');
                connector.className = 'connector';
                container.appendChild(connector);
            }
            const section = createPathSection(pathData);
            container.appendChild(section);
        });
    }

    function sortPathsByOrder(paths) {
        return paths.slice().sort(function (a, b) {
            return PATH_ORDER.indexOf(a.path) - PATH_ORDER.indexOf(b.path);
        });
    }

    function createPathSection(pathData) {
        const pathKey = pathData.path;
        const display = PATH_DISPLAY[pathKey];
        const cards = pathData.cards || [];

        // Calculate progress from pathProgressMap
        const progress = getPathProgress(pathKey);
        const unlockedCount = progress ? progress.unlockedCards : 0;
        const totalCount = progress ? progress.totalCards : cards.length;
        const isCompleted = progress ? progress.completed : false;

        const section = document.createElement('div');
        section.className = 'path-section';

        // Header
        const header = document.createElement('div');
        header.className = 'path-header';

        const titleDiv = document.createElement('div');
        titleDiv.className = 'path-title';

        const badge = document.createElement('span');
        badge.className = 'tier-badge ' + display.cssClass;

        const nameSpan = document.createElement('span');
        nameSpan.className = 'path-name';
        nameSpan.textContent = display.name;

        titleDiv.appendChild(badge);
        titleDiv.appendChild(nameSpan);

        const progressSpan = document.createElement('span');
        progressSpan.className = 'path-progress';
        progressSpan.textContent = unlockedCount + '/' + totalCount + ' cards unlocked';

        header.appendChild(titleDiv);

        if (isCompleted) {
            const completedBadge = document.createElement('span');
            completedBadge.className = 'path-completed-badge';
            completedBadge.textContent = '✓ Completed';
            header.appendChild(completedBadge);
        } else {
            header.appendChild(progressSpan);
        }

        section.appendChild(header);

        // Cards grid
        const grid = document.createElement('div');
        grid.className = 'cards-grid';

        cards.forEach(function (card) {
            const tile = createCardTile(card);
            grid.appendChild(tile);
        });

        section.appendChild(grid);

        return section;
    }

    function createCardTile(card) {
        const tile = document.createElement('div');
        tile.className = 'hero-card-tile';

        const state = getCardState(card);
        tile.classList.add(state);

        if (state === 'completed') {
            const icon = document.createElement('div');
            icon.className = 'card-icon';
            icon.textContent = '🦸';
            tile.appendChild(icon);
        } else if (state === 'active') {
            const icon = document.createElement('div');
            icon.className = 'card-icon';
            icon.textContent = '⚡';
            tile.appendChild(icon);

            tile.addEventListener('click', function () {
                window.location.href = 'learning.html?heroId=' + encodeURIComponent(card.id);
            });
            tile.title = 'Click to start learning!';
        } else {
            const lockIcon = document.createElement('div');
            lockIcon.className = 'lock-icon';
            lockIcon.textContent = '🔒';
            tile.appendChild(lockIcon);
        }

        const name = document.createElement('div');
        name.className = 'card-name';
        name.textContent = card.name;
        tile.appendChild(name);

        const concept = document.createElement('div');
        concept.className = 'card-concept';
        concept.textContent = card.conceptTitle;
        tile.appendChild(concept);

        return tile;
    }

    function getCardState(card) {
        const unlockedIds = playerState.unlockedHeroIds || [];
        const nextHeroId = playerState.nextAvailableHeroId;

        // unlockedHeroIds is serialized as a JSON array from a Java Set<String>
        if (Array.isArray(unlockedIds) && unlockedIds.includes(card.id)) {
            return 'completed';
        }
        if (card.id === nextHeroId) {
            return 'active';
        }
        return 'locked';
    }

    function getPathProgress(pathKey) {
        if (!playerState.pathProgressMap) return null;
        return playerState.pathProgressMap[pathKey] || null;
    }

    function hideLoading() {
        document.getElementById('loading').style.display = 'none';
    }

    function showError(message) {
        hideLoading();
        const errorEl = document.getElementById('error');
        errorEl.textContent = 'Error: ' + message;
        errorEl.style.display = 'block';
    }

    // Initialize on page load
    document.addEventListener('DOMContentLoaded', init);
})();
