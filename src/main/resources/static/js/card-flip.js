/**
 * card-flip.js — Java Hero Cards
 * Handles the 3D card flip on the collection page.
 * Click an unlocked hero card to reveal its explanation and code example on the back.
 */

document.addEventListener('DOMContentLoaded', () => {

    const flipCards = document.querySelectorAll('.flip-card');

    flipCards.forEach(card => {
        card.addEventListener('click', (e) => {
            // Don't flip if clicking on a link/button on the back face
            if (e.target.closest('a') || e.target.closest('button')) {
                return;
            }
            card.classList.toggle('flipped');
        });

        // Keyboard accessibility: flip on Enter or Space
        card.setAttribute('tabindex', '0');
        card.setAttribute('role', 'button');
        card.setAttribute('aria-label', 'Flip hero card');

        card.addEventListener('keydown', (e) => {
            if (e.key === 'Enter' || e.key === ' ') {
                e.preventDefault();
                card.classList.toggle('flipped');
            }
        });
    });

});
