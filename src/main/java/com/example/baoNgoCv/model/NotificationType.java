package com.example.baoNgoCv.model;

public enum NotificationType {
    // Thông báo về đơn ứng tuyển
    NEW_APPLICATION,         // Thông báo khi có đơn ứng tuyển mới
    APPLICATION_UPDATED,     // Thông báo khi có sự thay đổi trong đơn ứng tuyển

    // Thông báo về trạng thái ứng tuyển
    INTERVIEW_INVITATION,    // Thông báo khi ứng viên được mời phỏng vấn
    APPLICATION_REJECTED,    // Thông báo khi đơn ứng tuyển bị từ chối
    APPLICATION_ACCEPTED,    // Thông báo khi đơn ứng tuyển được chấp nhận
    INTERVIEW_RESCHEDULED,   // Thông báo khi phỏng vấn được lên lịch lại
    APPLICATION_REVIEWED,

    NEW_JOB_POSTING,         // Thông báo khi có công việc mới được đăng
    JOB_CLOSED,              // Thông báo khi công việc bị đóng

    // Thông báo về thay đổi tài khoản
    PASSWORD_CHANGED,        // Thông báo khi mật khẩu tài khoản bị thay đổi
    NEW_DEVICE_LOGIN,        // Thông báo khi có đăng nhập từ thiết bị mới

    // Thông báo về sự kiện đặc biệt
    EVENT_NOTIFICATION,      // Thông báo về sự kiện đặc biệt
    DEADLINE_REMINDER,       // Thông báo nhắc nhở về hạn chót

    // Thông báo về hành chính
    DOCUMENT_REQUEST,        // Thông báo yêu cầu tài liệu
    BACKGROUND_CHECK,        // Thông báo kiểm tra lý lịch

    // Thông báo về bảo trì hệ thống
    SYSTEM_MAINTENANCE,      // Thông báo bảo trì hệ thống
    POLICY_UPDATE,           // Thông báo cập nhật chính sách

    PROFILE_UPDATE, // Thông báo về ưu đãi hoặc chương trình khuyến mãi
    PROMOTION_NOTIFICATION   // Thông báo về ưu đãi hoặc khuyến mãi
}