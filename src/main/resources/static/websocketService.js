import UiService from './uiservice.js';

class WebSocketService {
    constructor(url) {
        this.client = new StompJs.Client({ brokerURL: url });
        this.setupEventHandlers();
    }

    setupEventHandlers() {
        this.client.onConnect = this.onConnect.bind(this);
        this.client.onWebSocketError = this.onWebSocketError.bind(this);
        this.client.onStompError = this.onStompError.bind(this);
        this.UiService = new UiService();
    }

    onConnect(frame) {
        console.log('Connected: ' + frame);

        this.client.subscribe('/topic/messages', (payload) => {
            // Parse the received data
            const data = JSON.parse(payload.body);

            // Check if the data is an array (list of messages)
            if (Array.isArray(data)) {
                // If it's an array, loop through each message and display it
                data.forEach(message => this.UiService.showMsg(message.id, message.name, message.content, message.timestamp));
            } else {
                // If it's a single message, display it directly
                this.UiService.showMsg(data.id, data.name, data.content, data.timestamp);
            }
        });

    }

    onWebSocketError(error) {
        console.error('Error with websocket', error);
    }

    onStompError(frame) {
        console.error('Broker reported error: ' + frame.headers['message']);
        console.error('Additional details: ' + frame.body);
    }

    // ...other methods
}

export default WebSocketService;