import ApiService from './apiService.js';

class UiService {
    constructor(apiService) {
        this.apiService = new ApiService();
        this.messagesElem = document.getElementById("messages");
    }

    createMessageComponent(id, name, message, timestamp) {
        const docFragment = document.createDocumentFragment();

        const newMsg = this.createTableRow(id, name, message, timestamp, docFragment);
        const modal = this.createModal(id, message);

        docFragment.appendChild(modal);
        newMsg.querySelector(":last-child").appendChild(docFragment);

        return newMsg;
    }

    createTableRow(id, name, message, timestamp, docFragment) {
        const newMsg = document.createElement("tr");
        newMsg.id = `message${id}`;

        const newMsgId = this.createTableCell(id, ["d-none", "id"]);
        const newMsgName = this.createTableCell(name, ["nickname"]);
        const newMsgMessage = this.createTableCell(message, ["message"]);
        const newMsgTime = this.createTableCell(new Date(timestamp).toLocaleString(), ["time"]);
        const newMsgEdit = this.createEditButtonCell(id, docFragment, ["editBtn"]);

        newMsg.append(newMsgId, newMsgName, newMsgMessage, newMsgTime, newMsgEdit);

        return newMsg;
    }

    createTableCell(content, classes = []) {
        const cell = document.createElement("td");
        cell.innerText = content;
        classes.forEach(cls => cell.classList.add(cls));
        return cell;
    }

    createEditButtonCell(id, docFragment) {
        const cell = document.createElement("td");
        const button = document.createElement('button');

        button.type = 'button';
        button.classList.add('btn', 'btn-primary');
        button.setAttribute('data-bs-toggle', 'modal');
        button.setAttribute('data-bs-target', `#messageEditer${id}`);
        button.textContent = 'Edit';

        cell.appendChild(button);

        return cell;
    }

    createModal(id, message) {
        const modal = document.createElement('div');
        modal.classList.add('modal', 'fade');
        modal.id = `messageEditer${id}`;  // used for data-bs-target so do not change
        modal.setAttribute('data-bs-backdrop', 'static');
        modal.setAttribute('data-bs-keyboard', 'false');
        modal.setAttribute('tabindex', '-1');
        modal.setAttribute('aria-labelledby', 'staticBackdropLabel');
        modal.setAttribute('aria-hidden', 'true');

        const modalDialog = document.createElement('div');
        modalDialog.classList.add('modal-dialog');

        const modalContent = document.createElement('div');
        modalContent.classList.add('modal-content');

        const modalHeader = this.createModalHeader();
        const modalBody = this.createModalBody(message);
        const modalFooter = this.createModalFooter(id, modalBody.querySelector("textarea"));

        modalContent.appendChild(modalHeader);
        modalContent.appendChild(modalBody);
        modalContent.appendChild(modalFooter);

        modalDialog.appendChild(modalContent);
        modal.appendChild(modalDialog);

        return modal;
    }

    createModalHeader() {
        const modalHeader = document.createElement('div');
        modalHeader.classList.add('modal-header');

        const title = document.createElement('h5');
        title.classList.add('modal-title');
        title.textContent = 'Edit Message';

        const closeButton = document.createElement('button');
        closeButton.type = 'button';
        closeButton.classList.add('btn-close');
        closeButton.setAttribute('data-bs-dismiss', 'modal');
        closeButton.setAttribute('aria-label', 'Close');

        modalHeader.appendChild(title);
        modalHeader.appendChild(closeButton);

        return modalHeader;
    }

    createModalBody(message) {
        const modalBody = document.createElement('div');
        modalBody.classList.add('modal-body');

        const input = document.createElement('textarea');
        input.classList.add('form-control');
        input.setAttribute('rows', '3');
        input.value = message;

        modalBody.appendChild(input);

        return modalBody;
    }

    createModalFooter(id, textarea) {
        const modalFooter = document.createElement('div');
        modalFooter.classList.add('modal-footer');

        const cancelButton = document.createElement('button');
        cancelButton.type = 'button';
        cancelButton.classList.add('btn', 'btn-secondary');
        cancelButton.setAttribute('data-bs-dismiss', 'modal');
        cancelButton.textContent = 'Cancel';

        const saveButton = document.createElement('button');
        saveButton.type = 'button';
        saveButton.classList.add('btn', 'btn-primary');
        saveButton.textContent = 'Save';
        saveButton.addEventListener('click', () => {
            console.log(id);
            this.handleEditMessage(textarea.value, id);
            const modalElement = document.getElementById(`messageEditer${id}`);
            const modalInstance = bootstrap.Modal.getInstance(modalElement);
            modalInstance.hide();
        });

        modalFooter.appendChild(cancelButton);
        modalFooter.appendChild(saveButton);

        return modalFooter;
    }

    async showMsg(id, name, message, timestamp) {
        const newMsg = this.createMessageComponent(id, name, message, timestamp);
        this.messagesElem.appendChild(newMsg);
    }

    async showMsgAtTop(id, name, message, timestamp) {
        const newMsg = this.createMessageComponent(id, name, message, timestamp);
        this.messagesElem.insertBefore(newMsg, this.messagesElem.firstChild);
    }

    async fetchAndDisplayPreviousMessages() {
        try {
            const earliestTimestamp = this.getEarliestTimestamp();
            const messages = await this.apiService.fetchPreviousMessages(earliestTimestamp);
            messages.forEach(msg => {
                console.log(msg);
                this.showMsgAtTop(msg.id, msg.name, msg.content, msg.timestamp);
            });
        } catch (error) {
            console.error('Error fetching previous messages:', error);
        }
    }

    parseCustomDateString(dateString) {
        // Split the string into components
        const parts = dateString.match(/(\d{4})\. (\d{1,2})\. (\d{1,2})\. (오전|오후) (\d{1,2}):(\d{2}):(\d{2})/);

        if (!parts) {
            throw new Error('Invalid date string');
        }

        let [ , year, month, day, partOfDay, hours, minutes, seconds ] = parts;

        // Convert to numbers
        year = parseInt(year, 10);
        month = parseInt(month, 10) - 1; // Month is 0-indexed in JavaScript Date
        day = parseInt(day, 10);
        hours = parseInt(hours, 10);
        minutes = parseInt(minutes, 10);
        seconds = parseInt(seconds, 10);

        // Correct hours for AM/PM
        if (partOfDay === '오후' && hours < 12) {
            hours += 12;
        } else if (partOfDay === '오전' && hours === 12) {
            hours = 0;
        }

        // Create a Date object and return the timestamp
        const date = new Date(year, month, day, hours, minutes, seconds);
        return date.getTime();
    }

    getEarliestTimestamp() {
        let earliestTimestamp = new Date().valueOf();

        if (this.messagesElem && this.messagesElem.firstElementChild) {
            const firstRow = this.messagesElem.firstElementChild;
            const thirdCell = firstRow.querySelector(".time");
            if (thirdCell) {
                const dateString = thirdCell.innerText;
                try {
                    earliestTimestamp = this.parseCustomDateString(dateString);
                } catch (error) {
                    console.error(`Failed to parse date string "${dateString}": ${error}`);
                }
            } else {
                console.error("The first row does not have a third table cell.");
            }
        }

        return earliestTimestamp
    }

    attachEventListeners() {
        document.getElementById("send").addEventListener('click', () => {
            console.log("send a");
            this.handleSendMessage();
        });
        document.getElementById("content").addEventListener('keypress', (e) => {
            if (e.key === 'Enter' && !e.shiftKey) {
                console.log("send b");
                e.preventDefault();
                this.handleSendMessage();
            }
        });

        document.getElementById("get_previous").addEventListener('click', () => this.fetchAndDisplayPreviousMessages());
    }

    handleSendMessage() {
        const contentInput = document.getElementById("content");
        this.apiService.sendMessage(contentInput.value)
            .then(/* Success logic */)
            .catch(/* Error handling logic */);
        contentInput.value = "";
    }

    handleEditMessage(message, id) {
        this.apiService.editMessage(id, message)
            .then(response => {
                document.querySelector(`#message${id}`).querySelector(".message").innerText = response.content;
            })
            .catch((error) => {
                console.error('Error:', error);
                // Handle errors
            });
    }
}

export default UiService;
