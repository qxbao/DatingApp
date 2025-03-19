document.addEventListener('DOMContentLoaded', function() {
    fetch('/api/active-convers')
        .then(response => response.json())
        .then(data => {
            const container = document.getElementById('chatContainer');
            container.innerHTML = '';
            if (data.len === 0) {
                container.innerHTML = '<p>No active conversations</p>';
                return;
            }
            for (let i = 0; i < data.len; i++) {
                const chatItem = createChatItem(data.avatarUrls[i], data.names[i], data.lastMessages[i]);
                container.appendChild(chatItem);
            }
        })
        .catch(error => {
            console.error('Error fetching active conversations:', error);
        });
});

const createChatItem = (avatar, name, lastMessage) => {
    const chatItem = document.createElement('div');
    chatItem.setAttribute('role', 'button');
    chatItem.className = 'chatField px-3 py-3 d-flex rounded-3 align-items-center';

    const avatarDiv = document.createElement('div');
    avatarDiv.className = 'rounded-circle chatAvatar me-2';
    avatarDiv.style.backgroundImage = `url('${avatar}')`;
    avatarDiv.style.width = '50px';

    const contentDiv = document.createElement('div');

    const nameDiv = document.createElement('div');
    nameDiv.className = 'fw-bold';
    nameDiv.textContent = name;

    const messageDiv = document.createElement('div');
    messageDiv.className = 'text-secondary small';
    messageDiv.textContent = lastMessage || 'Click to enter the chat';

    contentDiv.appendChild(nameDiv);
    contentDiv.appendChild(messageDiv);

    chatItem.appendChild(avatarDiv);
    chatItem.appendChild(contentDiv);

    return chatItem;
}