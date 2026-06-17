/**
 * app.js — Java Hero Cards
 * Handles:
 * - Collection filter buttons
 * - Unlock page: auto-trigger card-unlock animation on load
 * - Quiz: highlight selected answers
 */

document.addEventListener('DOMContentLoaded', () => {

    // -------------------------------------------------------
    // Collection Filter Buttons
    // -------------------------------------------------------
    const filterButtons = document.querySelectorAll('.filter-btn');
    const collectionSections = document.querySelectorAll('.collection-section');
    const cardWrappers = document.querySelectorAll('.hero-card-flip-wrapper');

    if (filterButtons.length > 0) {
        filterButtons.forEach(btn => {
            btn.addEventListener('click', () => {
                const filter = btn.dataset.filter;

                // Update active button
                filterButtons.forEach(b => b.classList.remove('active', 'filter-btn--active'));
                btn.classList.add('active');

                if (filter === 'all') {
                    // Show all sections and wrappers
                    collectionSections.forEach(s => s.classList.remove('hidden'));
                    cardWrappers.forEach(w => w.classList.remove('hidden'));
                } else {
                    // Hide/show sections by path
                    collectionSections.forEach(section => {
                        const sectionPath = section.dataset.path;
                        if (sectionPath === filter) {
                            section.classList.remove('hidden');
                        } else {
                            section.classList.add('hidden');
                        }
                    });

                    // Also filter individual wrappers (if no section grouping)
                    cardWrappers.forEach(wrapper => {
                        const wrapperPath = wrapper.dataset.path;
                        if (wrapperPath === filter) {
                            wrapper.classList.remove('hidden');
                        } else {
                            wrapper.classList.add('hidden');
                        }
                    });
                }
            });
        });

        // Set initial state — highlight "All" button
        const allBtn = document.querySelector('[data-filter="all"]');
        if (allBtn) allBtn.classList.add('active');
    }

    // -------------------------------------------------------
    // Quiz: Highlight selected answer options
    // -------------------------------------------------------
    const answerOptions = document.querySelectorAll('.answer-option');

    answerOptions.forEach(label => {
        const radio = label.querySelector('.answer-radio');
        if (!radio) return;

        // Check if already selected (e.g. page reload with state)
        if (radio.checked) {
            label.classList.add('selected');
        }

        label.addEventListener('click', () => {
            // Deselect all in same question group
            const name = radio.name;
            document.querySelectorAll(`input[name="${name}"]`).forEach(r => {
                const parentLabel = r.closest('.answer-option');
                if (parentLabel) parentLabel.classList.remove('selected');
            });
            // Select this one
            label.classList.add('selected');
        });
    });

    // -------------------------------------------------------
    // Unlock Page: trigger card-unlock animation on load
    // -------------------------------------------------------
    const unlockPage = document.querySelector('.unlock-page');
    if (unlockPage) {
        const unlockCard = unlockPage.querySelector('.card-unlock');
        if (unlockCard) {
            // The CSS class already triggers the animation on load.
            // Ensure the animation restarts cleanly.
            unlockCard.style.animation = 'none';
            // Force reflow
            void unlockCard.offsetWidth;
            unlockCard.style.animation = '';
        }

        // Animate confetti items with staggered start
        const confettiItems = document.querySelectorAll('.unlock-confetti span');
        confettiItems.forEach((item, i) => {
            item.style.animationDelay = (i * 0.15) + 's';
            item.style.bottom = '0';
        });
    }

    // -------------------------------------------------------
    // Quiz form: prevent submit if not all questions answered
    // -------------------------------------------------------
    const quizForm = document.getElementById('quizForm');
    if (quizForm) {
        quizForm.addEventListener('submit', (e) => {
            const questionBlocks = quizForm.querySelectorAll('.question-block');
            let allAnswered = true;

            questionBlocks.forEach((block, i) => {
                const radios = block.querySelectorAll('input[type="radio"]');
                const anyChecked = Array.from(radios).some(r => r.checked);
                if (!anyChecked) {
                    allAnswered = false;
                    block.style.borderColor = 'rgba(255,80,80,0.5)';
                    block.style.boxShadow = '0 0 0 2px rgba(255,80,80,0.2)';
                } else {
                    block.style.borderColor = '';
                    block.style.boxShadow = '';
                }
            });

            if (!allAnswered) {
                e.preventDefault();
                // Scroll to first unanswered question
                const first = quizForm.querySelector('.question-block[style*="rgba(255,80,80"]');
                if (first) first.scrollIntoView({ behavior: 'smooth', block: 'center' });
            }
        });
    }

});
