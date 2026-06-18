/**
 * Notification Utility — Java Hero Cards
 *
 * Provides non-blocking toast notifications that can be dismissed by the user.
 * Usage: showNotification(message, type)
 *   - message: string to display
 *   - type: 'error' | 'success' | 'info' (defaults to 'info')
 */
(function () {
    'use strict';

    var NOTIFICATION_DURATION_MS = 8000;

    /**
     * Ensure the notification container exists in the DOM.
     */
    function getOrCreateContainer() {
        var container = document.getElementById('notification-container');
        if (!container) {
            container = document.createElement('div');
            container.id = 'notification-container';
            container.setAttribute('aria-live', 'polite');
            container.setAttribute('role', 'status');
            document.body.appendChild(container);
        }
        return container;
    }

    /**
     * Show a non-blocking toast notification.
     * @param {string} message - The notification message
     * @param {string} [type='info'] - 'error', 'success', or 'info'
     */
    function showNotification(message, type) {
        type = type || 'info';
        var container = getOrCreateContainer();

        var toast = document.createElement('div');
        toast.className = 'notification-toast notification-' + type;
        toast.setAttribute('role', 'alert');

        var textSpan = document.createElement('span');
        textSpan.className = 'notification-message';
        textSpan.textContent = message;
        toast.appendChild(textSpan);

        var dismissBtn = document.createElement('button');
        dismissBtn.className = 'notification-dismiss';
        dismissBtn.textContent = '\u00D7';
        dismissBtn.setAttribute('aria-label', 'Dismiss notification');
        dismissBtn.addEventListener('click', function () {
            removeToast(toast);
        });
        toast.appendChild(dismissBtn);

        container.appendChild(toast);

        // Trigger enter animation
        requestAnimationFrame(function () {
            toast.classList.add('notification-visible');
        });

        // Auto-dismiss after duration
        var timer = setTimeout(function () {
            removeToast(toast);
        }, NOTIFICATION_DURATION_MS);

        toast._timer = timer;
    }

    /**
     * Remove a toast with exit animation.
     */
    function removeToast(toast) {
        if (toast._timer) {
            clearTimeout(toast._timer);
        }
        toast.classList.remove('notification-visible');
        toast.classList.add('notification-exit');
        setTimeout(function () {
            if (toast.parentNode) {
                toast.parentNode.removeChild(toast);
            }
        }, 300);
    }

    // Expose globally
    window.showNotification = showNotification;
})();
