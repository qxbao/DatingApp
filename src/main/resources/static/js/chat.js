let stompClient = null;
document.addEventListener('DOMContentLoaded', function() {
    connectWs();
})

const connectWs = () => {
    const socket = new SockJS('/ws');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function(frame) {
        stompClient.subscribe('/user/queue/chat', function(message) {
            console.log("Received message");
            onMessageReceived(message);
        });
        stompClient.send('/app/chat.addUser', {}, JSON.stringify({
            sender: uid,
            receiver: tuid,
            type: 'JOIN',
        }));
    });
}

const sendMessage = () => {
    const messageContent = document.getElementById('messageInput').value;
    if (stompClient && messageContent) {
        stompClient.send('/app/chat.sendMessage', {}, JSON.stringify({
            sender: uid,
            receiver: tuid,
            content: messageContent,
            type: 'CHAT',
        }));
        document.getElementById('messageInput').value = '';
        messageContent.value = '';
    }
}

const onMessageReceived = (payload) => {
    const message = JSON.parse(payload.body);
    console.log(message);
    const chatContainer = document.getElementById('chatContainer');
    const messageDiv = document.createElement('div');
    messageDiv.className = 'message';
    if (message.type === "JOIN") {
        if (message.sender === tuid) {
            document.getElementById("targetStatus").className = "small fw-semibold text-success";
            document.getElementById("targetStatus").textContent = "Online";
            if (message.content === 'Reply') return;
            stompClient.send('/app/chat.addUser', {}, JSON.stringify({
                sender: uid,
                receiver: tuid,
                type: 'JOIN',
                content: 'Reply'
            }));
        }
    } else if (message.type === "CHAT") {
        messageDiv.innerHTML = message.content;
        chatContainer.appendChild(messageDiv);
        chatContainer.scrollTop = chatContainer.scrollHeight;
    } else {
        if (message.sender === tuid) {
            document.getElementById("targetStatus").textContent = "Offline";
            document.getElementById("targetStatus").className = "small text-secondary";
        }
    }
}