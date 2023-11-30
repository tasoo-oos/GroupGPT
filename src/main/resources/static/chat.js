import ApiService from './apiService.js';
import UiService from './uiservice.js';
import WebSocketService from './websocketService.js';

// Vanilla JavaScript for event handling
// Set up event listeners after the DOM content is fully loaded.
document.addEventListener('DOMContentLoaded',  function () {
    const apiService = new ApiService();
    const uiService = new UiService(apiService);
    uiService.attachEventListeners();

    const wsService = new WebSocketService('ws://localhost:8080/gs-guide-websocket');
    wsService.client.activate();

    try {
        uiService.fetchAndDisplayPreviousMessages();
    } catch (error) {
        console.error('Error fetching previous messages:', error);
    }
});

