package com.vti.springdatajpa.controller;

import com.vti.springdatajpa.entity.Notification;
import com.vti.springdatajpa.entity.User;
import com.vti.springdatajpa.repository.NotificationRepository;
import com.vti.springdatajpa.repository.UserRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    /**
     * GET /api/notifications - Get user notifications
     */
    @GetMapping
    public ResponseEntity<NotificationPageResponse> getNotifications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "false") boolean unreadOnly) {

        Integer userId = getCurrentUserId();
        Pageable pageable = PageRequest.of(page, size);

        Page<Notification> notificationsPage;
        if (unreadOnly) {
            notificationsPage = notificationRepository.findByUserIdAndIsReadFalse(userId, pageable);
        } else {
            notificationsPage = notificationRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
        }

        List<NotificationDTO> content = notificationsPage.getContent().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());

        NotificationPageResponse response = new NotificationPageResponse();
        response.setContent(content);
        response.setPageNumber(notificationsPage.getNumber());
        response.setPageSize(notificationsPage.getSize());
        response.setTotalElements(notificationsPage.getTotalElements());
        response.setTotalPages(notificationsPage.getTotalPages());

        return ResponseEntity.ok(response);
    }

    /**
     * PUT /api/notifications/{id}/read - Mark notification as read
     */
    @PutMapping("/{id}/read")
    public ResponseEntity<NotificationDTO> markAsRead(@PathVariable Integer id) {
        Integer userId = getCurrentUserId();

        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification not found"));

        if (!notification.getUser().getId().equals(userId)) {
            throw new RuntimeException("Access denied");
        }

        notification.setRead(true);
        Notification saved = notificationRepository.save(notification);
        return ResponseEntity.ok(mapToDTO(saved));
    }

    /**
     * PUT /api/notifications/read-all - Mark all notifications as read
     */
    @PutMapping("/read-all")
    public ResponseEntity<String> markAllAsRead() {
        Integer userId = getCurrentUserId();

        List<Notification> unreadNotifications = notificationRepository.findByUserIdAndIsReadFalse(userId);
        for (Notification notification : unreadNotifications) {
            notification.setRead(true);
        }
        notificationRepository.saveAll(unreadNotifications);

        return ResponseEntity.ok("All notifications marked as read");
    }

    private Integer getCurrentUserId() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String identity;

        if (principal instanceof User) {
            return ((User) principal).getId();
        } else if (principal instanceof String) {
            identity = (String) principal;
        } else {
            throw new RuntimeException("Unsupported identity type");
        }

        return userRepository.findByUserName(identity)
                .or(() -> userRepository.findByEmail(identity))
                .map(User::getId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    private NotificationDTO mapToDTO(Notification notification) {
        NotificationDTO dto = new NotificationDTO();
        dto.setId(notification.getId());
        dto.setType(notification.getType());
        dto.setTitle(notification.getTitle());
        dto.setContent(notification.getContent());
        dto.setRead(notification.isRead());
        dto.setCreatedAt(notification.getCreatedAt());
        return dto;
    }

    @Data
    public static class NotificationDTO {
        private Integer id;
        private String type;
        private String title;
        private String content;
        private boolean isRead;
        private LocalDateTime createdAt;
    }

    @Data
    public static class NotificationPageResponse {
        private List<NotificationDTO> content;
        private int pageNumber;
        private int pageSize;
        private long totalElements;
        private int totalPages;
    }
}
