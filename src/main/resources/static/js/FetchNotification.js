document.addEventListener('DOMContentLoaded', () => {
    connect()
    fetch('/notification/get-notification')
        .then(response => {
            // Kiểm tra nếu phản hồi thành công
            if (!response.ok) {
                throw new Error(`HTTP error! Status: ${response.status}`);
            }
            return response.json();
        })
        .then(data => {

            // Cập nhật số lượng thông báo chưa đọc
            const badge = document.querySelector('.notification-badge');
            if (badge) {
                // Lọc thông báo chưa đọc
                const unreadNotifications = data.notifications ? data.notifications.filter(notification => !notification.read) : [];
                const unreadCount = unreadNotifications.length;

                badge.textContent = unreadCount;
                // Ẩn badge nếu không có thông báo chưa đọc
                badge.style.display = unreadCount > 0 ? 'inline-block' : 'none';
            }

            // Cập nhật danh sách thông báo trong menu thả xuống
            const dropdownMenu = document.querySelector('.notification-dropdown');

            if (dropdownMenu) {
                // Xóa các mục thông báo cũ
                dropdownMenu.innerHTML = '';

                // Thêm "Notification System" ở đầu danh sách với kiểu dáng khác biệt
                const notificationSystemItem = document.createElement('li');
                const notificationSystemText = document.createElement('span');
                notificationSystemText.className = 'notification-system-title';
                notificationSystemText.textContent = 'Notification System';
                notificationSystemItem.appendChild(notificationSystemText);
                dropdownMenu.appendChild(notificationSystemItem);

                if (data.notifications && data.notifications.length > 0) {
                    // Sắp xếp thông báo theo thời gian giảm dần (mới nhất lên đầu)
                    data.notifications.sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt));

                    data.notifications.forEach(notification => {
                        const notificationItem = document.createElement('li');
                        notificationItem.className = 'dropdown-item notification-item';

                        // Tạo phần ảnh đại diện
                        const avatarDiv = document.createElement('div');
                        avatarDiv.className = 'notification-avatar';
                        const avatarImg = document.createElement('img');

                        avatarImg.src = notification.avatar || 'https://thumbs.dreamstime.com/b/capgemini-company-logo-sign-building-capgemini-company-logo-sign-building-capgemini-french-multinational-272177357.jpg';
                        avatarImg.alt = 'Notification Avatar';
                        avatarDiv.appendChild(avatarImg);

                        // Tạo phần nội dung thông báo
                        const contentDiv = document.createElement('div');
                        contentDiv.className = 'notification-content';

                        const messageDiv = document.createElement('div');
                        messageDiv.textContent = notification.title;
                        messageDiv.className = notification.read ? 'notification-read' : 'notification-unread';

                        if (notification.sender) {
                            const senderDiv = document.createElement('div');
                            senderDiv.textContent = `From: ${notification.sender}`;
                            senderDiv.className = notification.read ? 'notification-sender notification-read' : 'notification-sender notification-unread';

                            contentDiv.appendChild(senderDiv);
                        }

                        const timeDiv = document.createElement('div');
                        const formattedTime = new Date(notification.createdAt).toLocaleString();
                        timeDiv.textContent = formattedTime;
                        timeDiv.className = notification.read ? 'notification-time notification-read' : 'notification-time notification-unread';

                        contentDiv.appendChild(messageDiv);
                        contentDiv.appendChild(timeDiv);

                        // Nếu có link, bọc nội dung trong thẻ <a>
                        if (notification.href) {

                            const link = document.createElement('a');
                            link.href = notification.href;
                            link.className = 'notification-link';
                            link.appendChild(avatarDiv);
                            link.appendChild(contentDiv);
                            notificationItem.appendChild(link);
                        } else {
                            // Nếu không có link, chỉ thêm nội dung
                            notificationItem.appendChild(avatarDiv);
                            notificationItem.appendChild(contentDiv);
                        }

                        dropdownMenu.appendChild(notificationItem);
                    });
                } else {
                    const noNotificationItem = document.createElement('li');
                    const noNotificationText = document.createElement('span');
                    noNotificationText.className = 'dropdown-item';
                    noNotificationText.textContent = 'There are no notifications yet!';
                    noNotificationItem.appendChild(noNotificationText);
                    dropdownMenu.appendChild(noNotificationItem);
                }
            } else {
                console.error('Dropdown menu element not found.');
            }
        })
        .catch(error => {
            console.error('Error fetching notifications:', error);
        });
});
var stompClient = null ;

function connect() {
    // Fetch the current user ID from the backend
    fetch('/user/get-current-user')
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                // Log URL đăng ký WebSocket

                const userId = data.id; // Get the user ID from the response
                const userType = data.userType;

                const socket = new SockJS('/ws'); // URL for your WebSocket endpoint
                stompClient = Stomp.over(socket);

                stompClient.connect({}, function (frame) {
                    console.log('Connected: ' + frame);

                    // Subscribe to the WebSocket topic using the user ID
                    stompClient.subscribe(`/topic/${userType}/${userId}`, function (messageOutput) {
                        const message = JSON.parse(messageOutput.body);
                        displayReviewNotification(message);
                    });
                }, function (error) {
                    console.error("WebSocket connection error:", error);
                });
            } else {
                console.error("User not found or not authenticated");
            }
        })
        .catch(error => {
            console.error("Error fetching current user:", error);
        });
}
function displayReviewNotification(message) {
    // Lấy dropdown menu để hiển thị thông báo
    const dropdownMenu = document.querySelector('.notification-dropdown');
    if (!dropdownMenu) {
        console.error("Dropdown menu not found!");
        return;
    }

    // Tạo phần mục thông báo mới
    const notificationItem = document.createElement('li');
    notificationItem.className = 'dropdown-item notification-item';

    // Tạo phần ảnh đại diện
    const avatarDiv = document.createElement('div');
    avatarDiv.className = 'notification-avatar';
    const avatarImg = document.createElement('img');
    avatarImg.src = message.avatar || 'https://thumbs.dreamstime.com/b/capgemini-company-logo-sign-building-capgemini-company-logo-sign-building-capgemini-french-multinational-272177357.jpg';
    avatarImg.alt = 'Notification Avatar';
    avatarDiv.appendChild(avatarImg);

    // Tạo phần nội dung thông báo
    const contentDiv = document.createElement('div');
    contentDiv.className = 'notification-content';

    const messageDiv = document.createElement('div');
    messageDiv.textContent = message.message;  // Nội dung thông báo
    messageDiv.className = 'notification-unread';  // Mặc định là chưa đọc

    contentDiv.appendChild(messageDiv);

    // Thêm tên người gửi nếu có
    if (message.sender) {
        const senderDiv = document.createElement('div');
        senderDiv.textContent = `From: ${message.sender}`;
        senderDiv.className = 'notification-sender notification-unread';  // Mặc định là chưa đọc
        contentDiv.appendChild(senderDiv);
    }

    // Thêm thời gian thông báo
    const timeDiv = document.createElement('div');
    const formattedTime = new Date().toLocaleString();  // Thời gian hiện tại
    timeDiv.textContent = formattedTime;
    timeDiv.className = 'notification-time notification-unread';

    contentDiv.appendChild(timeDiv);

    // Nếu có link, bọc nội dung trong thẻ <a>
    if (message.href) {
        const link = document.createElement('a');
        link.href = message.href;
        link.className = 'notification-link';
        link.appendChild(avatarDiv);
        link.appendChild(contentDiv);
        notificationItem.appendChild(link);
    } else {
        // Nếu không có link, chỉ thêm nội dung
        notificationItem.appendChild(avatarDiv);
        notificationItem.appendChild(contentDiv);
    }

    // Tìm phần tử "Notification System"
    const notificationSystemItem = dropdownMenu.querySelector('.notification-system-title');

    // Tìm phần tử đầu tiên trong danh sách thông báo cũ (nếu có)
    let firstNotification = notificationSystemItem.parentElement.nextElementSibling;

    // Nếu có thông báo cũ, chèn vào trước nó, nếu không thì chèn sau Notification System
    if (firstNotification) {
        dropdownMenu.insertBefore(notificationItem, firstNotification);
    } else {
        dropdownMenu.appendChild(notificationItem);
    }

    // Cập nhật số lượng thông báo chưa đọc
    const badge = document.querySelector('.notification-badge');
    badge.textContent = parseInt(badge.textContent || '0') + 1;
    badge.style.display = 'inline-block'; // Đảm bảo hiển thị badge
}
