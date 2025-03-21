document.addEventListener('DOMContentLoaded', function() {
    (function() {
        const interBubble = document.querySelector(".interactive");
        let curX = 0;
        let curY = 0;
        let tgX = 0;
        let tgY = 0;

        function move() {
            curX += (tgX - curX) / 20;
            curY += (tgY - curY) / 20;
            interBubble.style.transform = `translate(${Math.round(curX)}px, ${Math.round(curY)}px)`;
            requestAnimationFrame(move);
        }

        window.addEventListener('mousemove', (event) => {
            tgX = event.clientX;
            tgY = event.clientY;
        });

        move();
        return ()  => window.removeEventListener('mousemove', (event) => {
            tgX = event.clientX;
            tgY = event.clientY;
        });
    }());
    fetch('/api/active-convers')
        .then(response => response.json())
        .then(data => {
            const container = document.getElementById('chatUserContainer');
            const mobileContainer = document.getElementById('chatUserContainerMobile');
            container.innerHTML = '';
            mobileContainer.innerHTML = '';
            if (data.len === 0) {
                container.innerHTML = '<p class="px-3">No active conversations</p>';
                mobileContainer.innerHTML = '<div class="text-black mx-auto my-auto">No active conversations</div>';
                return;
            }
            for (let i = 0; i < data.len; i++) {
                const chatItem = createChatItem(data.avatarUrls[i], data.names[i], data.lastMessages[i],  data.ids[i]);
                container.appendChild(chatItem);
                const chatItemMobile = createChatItemMobile(data.avatarUrls[i], data.names[i], data.lastMessages[i],  data.ids[i]);
                mobileContainer.appendChild(chatItemMobile);
            }
        })
        .catch(error => {
            console.error('Error fetching active conversations:', error);
        });
});

const createChatItem = (avatar, name, lastMessage, id) => {
    const chatItem = document.createElement('div');
    chatItem.setAttribute('role', 'button');
    chatItem.className = 'chatField px-3 py-3 d-flex rounded-3 align-items-center';
    chatItem.role = "button";
    chatItem.addEventListener("click", () =>  {
        location.href = `/chat/${id}`;
    })
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

const createChatItemMobile = (avatar, name, lastMessage, id) => {
    const chatItem = document.createElement('div');
    chatItem.setAttribute('role', 'button');
    chatItem.className = 'chatAvatar border-3 border-white border rounded-circle';
    chatItem.addEventListener("click", () =>  {
        location.href = `/chat/${id}`;
    })
    chatItem.style.backgroundImage = `url('${avatar}')`;
    chatItem.style.width = '50px';
    chatItem.style.boxShadow ='0 0 0 3px #1cb91c';

    return chatItem;
}