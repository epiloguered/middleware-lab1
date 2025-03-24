let userId = "";
let webSocket = null;


function logout() {
    localStorage.removeItem('userId');
    window.location.href = 'index.html';
}
// 登录逻辑
function login() {
    userId = document.getElementById('userId').value;
    if (!userId) {
        alert("Please enter your ID.");
        return;
    }

    // 保存用户 ID，并跳转到聊天页面
    localStorage.setItem('userId', userId);
    window.location.href = 'chat.html';
}

// 页面加载时初始化用户
window.onload = function() {
    if (window.location.pathname.includes("chat.html")) {
        userId = localStorage.getItem('userId');
        if (!userId) {
            window.location.href = 'index.html';  // 如果用户未登录，跳转到登录页
        }
        document.getElementById('userName').innerText = userId;

        // 初始化 WebSocket（或通过 AJAX 获取实时数据）
        initializeWebSocket();

    }
};

function initializeWebSocket() {
    webSocket = new WebSocket('ws://localhost:8080/chat');

    webSocket.onopen = function() {
        webSocket.send(userId); // Send user ID to identify the client
    };

    webSocket.onmessage = function(event) {
        displayMessage(event.data);
        showOnlineUsers();
    };

    webSocket.onerror = function(error) {
        console.error("WebSocket Error: ", error);
    };
}

// 显示消息
function displayMessage(message) {
    const messagesDiv = document.getElementById('messages');
    const newMessage = document.createElement('div');
    newMessage.textContent = message;
    messagesDiv.appendChild(newMessage);
}

// 发送私聊消息
function sendPrivateMessage() {
    const receiverId = document.getElementById('receiverId').value;
    const message = document.getElementById('privateMessage').value;

    if (!receiverId || !message) {
        alert("Please enter a valid receiver ID and message.");
        return;
    }
    if(receiverId === 'AI')
        fetch('/aiChat?msg='+message, )
        .then(response => response.text())
        .then(data => {
            displayMessage(data);  // 显示发送的消息
        });

    // 发送消息到后端 API
    fetch(`/user/privateChat?senderId=${userId}&receiverId=${receiverId}&message=${message}`, {
        method: 'POST'
    }).then(response => {
        return response.text();
    }).then(data => {
        displayMessage(data);  // 显示发送的消息
    });
}

// 发送群聊消息
function sendGroupMessage() {
    const groupName = document.getElementById('groupName').value;
    const message = document.getElementById('groupMessage').value;

    if (!groupName || !message) {
        alert("Please enter a valid group name and message.");
        return;
    }

    // 发送消息到后端 API
    fetch(`/user/groupChat?senderId=${userId}&groupName=${groupName}&message=${message}`, {
        method: 'POST'
    }).then(response => {
        return response.text();
    }).then(data => {
        displayMessage(data);  // 显示发送的消息
    });
}
function showOnlineUsers() {
    fetch('/user/onlineUsers')
        .then(response => response.json())
        .then(data => {
            const onlineUsersDiv = document.getElementById('onlineUsers');
            onlineUsersDiv.innerHTML = '';
            data.forEach(userId => {
                const p = document.createElement('p');
                p.textContent = userId;
                onlineUsersDiv.appendChild(p);
            });
        });
}
function createGroup() {
    const groupName = document.getElementById('newGroupName').value;
    if (!groupName) {
        alert("请输入群名。");
        return;
    }
    fetch(`/user/createGroup?groupName=${groupName}`, { method: 'POST' })
        .then(response => response.text())
        .then(data => alert(data));
}

function joinGroup() {
    const groupName = document.getElementById('joinGroupName').value;
    if (!groupName) {
        alert("请输入群名。");
        return;
    }

    fetch(`/user/joinGroup?groupName=${groupName}&userId=${userId}`, { method: 'POST' })
        .then(response => response.text())
        .then(data => alert(data));
}

function showGroupList() {
    fetch('/user/groupList')
        .then(response => response.json())
        .then(data => {
            const groupListDiv = document.getElementById('groupList');
            groupListDiv.innerHTML = '';
            data.forEach(groupName => {
                const p = document.createElement('p');
                p.textContent = groupName;
                groupListDiv.appendChild(p);
            });
        });
}
function loadMessageHistory(type, id) {
    fetch(`/user/messageHistory?type=${type}&id=${id}`)
        .then(response => response.json())
        .then(data => {
            data.forEach(msg => {
                displayMessage(msg); // 假设已有 displayMessage 函数用于显示消息
            });
        });
}

function loadPrivateChatHistory(receiverId) {
    const id = userId < receiverId ? userId + "_" + receiverId : receiverId + "_" + userId;
    loadMessageHistory("private", id);
}

function loadGroupChatHistory(groupName) {
    loadMessageHistory("group", groupName);
}
