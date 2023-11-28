// Create a new STOMP client instance with the WebSocket broker URL.
const stompClient = new StompJs.Client({
    brokerURL: 'ws://localhost:8080/gs-guide-websocket'
});

// Function to execute when the client successfully connects to the WebSocket server.
stompClient.onConnect = (frame) => {
    console.log('Connected: ' + frame);

    // Subscribe to a topic and define a callback to handle incoming messages.
    stompClient.subscribe('/topic/greetings', (greeting) => {
        // Parse the received data
        const data = JSON.parse(greeting.body);

        // Check if the data is an array (list of messages)
        if (Array.isArray(data)) {
            // If it's an array, loop through each message and display it
            data.forEach(message => showMsg(message.name, message.content, message.timestamp));
        } else {
            // If it's a single message, display it directly
            showMsg(data.name, data.content, data.timestamp);
        }
    });
};

// Function to handle WebSocket errors.
stompClient.onWebSocketError = (error) => {
    console.error('Error with websocket', error);
};

// Function to handle errors from the STOMP broker.
stompClient.onStompError = (frame) => {
    console.error('Broker reported error: ' + frame.headers['message']);
    console.error('Additional details: ' + frame.body);
};

// Publish a message to the '/app/hello' destination on the WebSocket server.
function sendMsg() {
    const contentInput = document.getElementById("content");

    const csrfToken = document.querySelector('meta[name="_csrf"]').getAttribute('content');
    const csrfHeaderName = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');

    // Create a JSON object with the name and message content.
    const payload = JSON.stringify({
        content: contentInput.value
    });

    console.log(payload);

    // Use Fetch API to submit the form data
    // Replace with your server endpoint
    fetch('/api/message', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            [csrfHeaderName]: csrfToken // Include CSRF token as header
        },
        body: payload
    })
        .then(response => response.json())
        .then(data => {
            console.log('Success:', data);
            // Handle success
        })
        .catch((error) => {
            console.error('Error:', error);
            // Handle errors
        });

    contentInput.value = "";
    contentInput.style.height = "auto";
}

let recentTimestamp = 0;


// Create a new MutationObserver instance
const greetingsElem = document.getElementById("messages");
const observer = new MutationObserver(function(mutations) {
    mutations.forEach(function(mutation) {
        if (mutation.type === 'childList') {
            // Scroll the chat container to the bottom
            greetingsElem.scrollTop = greetingsElem.scrollHeight;
        }
    });
});

// Start observing the '#messages' element for child list changes
observer.observe(greetingsElem, { childList: true });

// Function to display a new message in the UI.
function showMsg(name, message, timestamp) {
    if(timestamp <= recentTimestamp) {
        return;
    }
    recentTimestamp = timestamp;

    const newMsg = document.createElement("tr");

    // Create and populate table cells for the message components.
    const newMsgName = document.createElement("td");
    const newMsgMessage = document.createElement("td");
    const newMsgTime = document.createElement("td");
    newMsgName.innerText = name;
    newMsgMessage.innerText = message;
    newMsgTime.innerText = new Date(timestamp).toLocaleString();

    // Append the cells to the message row and the row to the greeting element.
    newMsg.appendChild(newMsgName);
    newMsg.appendChild(newMsgMessage);
    newMsg.appendChild(newMsgTime);
    greetingsElem.appendChild(newMsg);
}

let isFetchingPreviousMessages = true;

// Assuming you have a function to fetch previous messages
async function fetchPreviousMessages() {
    if(!isFetchingPreviousMessages) {
        return;
    }
    const earliestTimestamp = document.getElementById("messages").firstChild.lastChild.innerText || 0;
    console.log(earliestTimestamp);

    // Fetch previous messages from the server
    // This is just a placeholder, replace with your actual AJAX call
    const response = await fetch(`/api/messages/previous?lastReceivedTimestamp=${earliestTimestamp}`);
    const messages = await response.json();

    if(messages.length === 0) {
        isFetchingPreviousMessages = false;
        return;
    }

    messages.forEach(message => {
        showMsg(message.name, message.content, message.timestamp);
    });
}

// Vanilla JavaScript for event handling
// Set up event listeners after the DOM content is fully loaded.
document.addEventListener('DOMContentLoaded', function () {
    stompClient.activate();
    // Prevent the default form submission behavior.
    document.querySelector("form").addEventListener('submit', function(e) {
        e.preventDefault();
    });

    // Attach event listeners to buttons for connect, disconnect, and send actions.
    document.getElementById("send").addEventListener('click', sendMsg);
    document.getElementById("content").addEventListener('keypress', function(e) {
        if (e.key === 'Enter' && !e.shiftKey) {
            e.preventDefault();
            sendMsg();
        }
    });

    // Add scroll event listener to the chat container
    document.getElementById("messages").addEventListener("scroll", function() {
        if (this.scrollTop === 0) {
            fetchPreviousMessages().then(r => console.log(r));
        }
    });
});

