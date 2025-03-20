let stompClient = null;
document.addEventListener('DOMContentLoaded', function() {
    connectWs();
    loadMessage(0, 10);
    $("#messageInput").on('keydown', function(e) {
        if(e.keyCode === 13 && !e.shiftKey) {
            e.preventDefault();
            sendMessage();
        }
    });
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
    const messageContent = document.getElementById('messageInput').textContent;
    if (messageContent) {
        $.ajax({
            type: "POST",
            url:  "/chat/sendMessage",
            data: JSON.stringify({ senderId : uid, receiverId: tuid, content: messageContent }),
            contentType: "application/json",
            complete: function () {
                document.getElementById('messageInput').textContent = '';
                const chatContainer = document.getElementById('chatContainer');
                chatContainer.scrollTop = chatContainer.scrollHeight;
            },
        });
    }
}

const loadMessage = (from, to) => {
    $.ajax({
        type: "GET",
        url:  "/chat/getMessage",
        data: { receiverId: tuid, fromIndex: from, toIndex: to },
        contentType: "application/json",
    }).done(function (data) {
        let h = 0;
        for (let i = 0; i < data.messages.length; i++) {
            const msgEl = createMessageElement(data.messages[i], data.senders[i]);
            document.getElementById('chatContainer').prepend(msgEl);
            h += msgEl.offsetHeight;
        }
        if (!data.overflow) {
            const loadMoreEl = document.createElement('div');
            loadMoreEl.className = 'text-decoration-underline text-center mb-2 text-primary';
            loadMoreEl.textContent = 'Load previous messages';
            loadMoreEl.style.cursor = 'pointer';
            loadMoreEl.onclick = function() {
                loadMessage(to, to + 10);
                loadMoreEl.remove();
            }
            document.getElementById('chatContainer').prepend(loadMoreEl)
            h += loadMoreEl.offsetHeight;
        }
        document.getElementById('chatContainer').scrollTop = h;
    });
}

const createMessageElement = (content, sender) => {
    const messageDiv = document.createElement('div');
    messageDiv.className = 'message px-3 py-2';
    if (sender === uid) {
        messageDiv.classList.add('sent');
    } else {
        messageDiv.classList.add('received');
    }
    messageDiv.innerHTML = content;
    return messageDiv;
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
        const msgEl = createMessageElement(message.content, message.sender);
        document.getElementById('chatContainer').appendChild(msgEl);
        chatContainer.scrollTop = chatContainer.scrollHeight;
    } else {
        if (message.sender === tuid) {
            document.getElementById("targetStatus").textContent = "Offline";
            document.getElementById("targetStatus").className = "small text-secondary";
        }
    }
}