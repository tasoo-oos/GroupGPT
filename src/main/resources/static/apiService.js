class ApiService {
    constructor() {
        this.csrfToken = document.querySelector('meta[name="_csrf"]').getAttribute('content');
        this.csrfHeaderName = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');
    }

    async sendRequest(url, method, data) {
        try {
            const response = await fetch(url, {
                method: method,
                headers: {
                    'Content-Type': 'application/json',
                    [this.csrfHeaderName]: this.csrfToken,
                },
                body: method !== 'GET' ? JSON.stringify(data) : null,
            });

            // Check if the response is OK (status in the range 200-299)
            if (!response.ok) {
                // Check if the response is in JSON format
                const contentType = response.headers.get('Content-Type');
                if (contentType && contentType.includes('application/json')) {
                    const errorData = await response.json();
                    throw new Error(errorData.message || 'Server responded with an error');
                } else {
                    // If not JSON, handle as text
                    const errorText = await response.text();
                    throw new Error(errorText || 'Server responded with an error');
                }
            }

            return response.status === 204 ? null : await response.json();
        } catch (error) {
            this.displayErrorMessage(error);
        }
    }


    sendMessage(content) {
        return this.sendRequest('/api/message', 'POST', { content });
    }

    async editMessage(id, content) {
        return await this.sendRequest(`/api/message/edit/${id}`, 'PUT', {content});
    }

    fetchPreviousMessages(earliestTimestamp) {
        return this.sendRequest(`/api/messages/previous?lastReceivedTimestamp=${earliestTimestamp}`, 'GET');
    }

    displayErrorMessage(message) {
        // Implementation depends on how you want to display the error
        // For example, using an alert or updating the DOM to show the error message
        alert(message); // Simple alert; replace with your UI implementation
    }
}

export default ApiService;
