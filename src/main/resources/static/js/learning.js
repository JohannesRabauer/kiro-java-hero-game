/**
 * Learning View - Java Hero Cards
 *
 * Displays hero card learning content with intro animation,
 * three explanation sections, code examples, and a Start Quiz button.
 */
(function () {
    'use strict';

    const ANIMATION_MAX_DURATION_MS = 5000;
    const ANIMATION_DISPLAY_MS = 3500;

    // DOM elements
    const introAnimation = document.getElementById('intro-animation');
    const introHeroName = document.getElementById('intro-hero-name');
    const introConcept = document.getElementById('intro-concept');
    const learningContent = document.getElementById('learning-content');
    const heroTitle = document.getElementById('hero-title');
    const whatItIs = document.getElementById('what-it-is');
    const whyItMatters = document.getElementById('why-it-matters');
    const howToUseIt = document.getElementById('how-to-use-it');
    const codeExamples = document.getElementById('code-examples');
    const startQuizBtn = document.getElementById('start-quiz-btn');
    const errorState = document.getElementById('error-state');
    const errorMessage = document.getElementById('error-message');
    const retryBtn = document.getElementById('retry-btn');

    // Get heroId from URL query parameter
    function getHeroId() {
        const params = new URLSearchParams(window.location.search);
        return params.get('heroId');
    }

    // Fetch hero content from the API
    async function fetchHeroContent(heroId) {
        const response = await fetch('/api/content/' + encodeURIComponent(heroId));
        if (!response.ok) {
            throw new Error('Failed to load hero content (status: ' + response.status + ')');
        }
        return response.json();
    }

    // Play intro animation with timeout fallback (requirement 4.1, 4.5)
    function playIntroAnimation(heroId, content) {
        return new Promise(function (resolve) {
            var resolved = false;

            function finish() {
                if (resolved) return;
                resolved = true;
                introAnimation.classList.add('fade-out');
                setTimeout(function () {
                    introAnimation.style.display = 'none';
                    resolve();
                }, 500);
            }

            try {
                // Display hero name derived from heroId (e.g., "bronze-variable" -> "Variable")
                var displayName = heroId.split('-').slice(1).map(function (w) {
                    return w.charAt(0).toUpperCase() + w.slice(1);
                }).join(' ') || heroId;

                introHeroName.textContent = displayName;
                introConcept.textContent = content.whatItIs
                    ? content.whatItIs.substring(0, 80) + (content.whatItIs.length > 80 ? '...' : '')
                    : '';

                // Animation plays for a set duration then transitions to content
                setTimeout(finish, ANIMATION_DISPLAY_MS);

                // Hard timeout at max 5 seconds (requirement 4.1)
                setTimeout(finish, ANIMATION_MAX_DURATION_MS);
            } catch (e) {
                // If animation fails, skip directly to content (requirement 4.5)
                finish();
            }
        });
    }

    // Render the learning content sections
    function renderContent(heroId, content) {
        document.body.classList.add('learning-page');

        // Set hero title
        var displayName = heroId.split('-').slice(1).map(function (w) {
            return w.charAt(0).toUpperCase() + w.slice(1);
        }).join(' ') || heroId;
        heroTitle.textContent = displayName;

        // Section content (requirement 4.2)
        whatItIs.textContent = content.whatItIs || '';
        whyItMatters.textContent = content.whyItMatters || '';
        howToUseIt.textContent = content.howToUseIt || '';

        // Code examples (requirement 4.3)
        renderCodeExamples(content.codeExamples || []);

        // Show learning content
        learningContent.style.display = 'block';

        // Start Quiz button (requirement 4.4)
        startQuizBtn.addEventListener('click', function () {
            window.location.href = 'quiz.html?heroId=' + encodeURIComponent(heroId);
        });
    }

    // Render code examples with basic syntax highlighting
    function renderCodeExamples(examples) {
        if (examples.length === 0) return;

        examples.forEach(function (example) {
            var block = document.createElement('div');
            block.className = 'code-block';

            var header = document.createElement('div');
            header.className = 'code-block-header';

            var title = document.createElement('span');
            title.className = 'code-block-title';
            title.textContent = example.title || 'Example';

            var lang = document.createElement('span');
            lang.className = 'code-block-lang';
            lang.textContent = (example.language || 'java').toUpperCase();

            header.appendChild(title);
            header.appendChild(lang);

            var pre = document.createElement('pre');
            var code = document.createElement('code');
            code.innerHTML = highlightJava(example.code || '');

            pre.appendChild(code);
            block.appendChild(header);
            block.appendChild(pre);
            codeExamples.appendChild(block);
        });
    }

    // Basic Java syntax highlighting with CSS classes
    function highlightJava(source) {
        // Escape HTML first
        var escaped = source
            .replace(/&/g, '&amp;')
            .replace(/</g, '&lt;')
            .replace(/>/g, '&gt;');

        // Apply syntax highlighting rules in order
        // Comments (single-line)
        escaped = escaped.replace(/(\/\/[^\n]*)/g, '<span class="comment">$1</span>');

        // Multi-line comments
        escaped = escaped.replace(/(\/\*[\s\S]*?\*\/)/g, '<span class="comment">$1</span>');

        // Strings
        escaped = escaped.replace(/("(?:[^"\\]|\\.)*")/g, '<span class="string">$1</span>');

        // Annotations
        escaped = escaped.replace(/(@\w+)/g, '<span class="annotation">$1</span>');

        // Numbers
        escaped = escaped.replace(/\b(\d+\.?\d*[fFdDlL]?)\b/g, '<span class="number">$1</span>');

        // Keywords
        var keywords = [
            'abstract', 'assert', 'boolean', 'break', 'byte', 'case', 'catch',
            'char', 'class', 'const', 'continue', 'default', 'do', 'double',
            'else', 'enum', 'extends', 'final', 'finally', 'float', 'for',
            'if', 'implements', 'import', 'instanceof', 'int', 'interface',
            'long', 'native', 'new', 'package', 'private', 'protected',
            'public', 'return', 'short', 'static', 'strictfp', 'super',
            'switch', 'synchronized', 'this', 'throw', 'throws', 'transient',
            'try', 'var', 'void', 'volatile', 'while', 'record', 'sealed',
            'permits', 'yield', 'true', 'false', 'null'
        ];
        var keywordPattern = new RegExp('\\b(' + keywords.join('|') + ')\\b', 'g');
        escaped = escaped.replace(keywordPattern, '<span class="keyword">$1</span>');

        // Common types (capitalized words that look like types)
        escaped = escaped.replace(/\b([A-Z][a-zA-Z0-9]*)\b/g, '<span class="type">$1</span>');

        return escaped;
    }

    // Show error state with retry
    function showError(message) {
        introAnimation.style.display = 'none';
        learningContent.style.display = 'none';
        errorMessage.textContent = message || 'Failed to load content.';
        errorState.style.display = 'flex';
    }

    // Main initialization
    async function init() {
        var heroId = getHeroId();

        if (!heroId) {
            showError('No hero specified. Please select a hero from the roadmap.');
            return;
        }

        try {
            var content = await fetchHeroContent(heroId);

            // Play intro animation (max 5 seconds, with fallback on failure)
            await playIntroAnimation(heroId, content);

            // Show learning content
            renderContent(heroId, content);
        } catch (err) {
            showError('Unable to load learning content. Please try again.');
        }
    }

    // Retry button handler
    retryBtn.addEventListener('click', function () {
        errorState.style.display = 'none';
        introAnimation.style.display = 'flex';
        init();
    });

    // Start
    init();
})();
