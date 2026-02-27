package com.edms.notification.controller;

import com.edms.common.dto.CommonApiResponse;
import com.edms.notification.dto.NotificationDto;
import com.edms.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<CommonApiResponse<Page<NotificationDto.AlertResponse>>> getAlerts(
            @RequestParam(defaultValue = "false") boolean unreadOnly,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Authentication auth) {
        UUID userUuid = (UUID) auth.getPrincipal();
        return ResponseEntity.ok(CommonApiResponse.success(
                notificationService.getAlerts(userUuid, unreadOnly, page, size)));
    }

    @GetMapping("/unread-count")
    public ResponseEntity<CommonApiResponse<NotificationDto.UnreadCountResponse>> getUnreadCount(
            Authentication auth) {
        UUID userUuid = (UUID) auth.getPrincipal();
        return ResponseEntity.ok(CommonApiResponse.success(notificationService.getUnreadCount(userUuid)));
    }

    @PatchMapping("/{uuid}/read")
    public ResponseEntity<CommonApiResponse<Void>> markAsRead(@PathVariable UUID uuid) {
        notificationService.markAsRead(uuid);
        return ResponseEntity.ok(CommonApiResponse.success(null));
    }

    @PatchMapping("/read-all")
    public ResponseEntity<CommonApiResponse<Void>> markAllAsRead(Authentication auth) {
        UUID userUuid = (UUID) auth.getPrincipal();
        notificationService.markAllAsRead(userUuid);
        return ResponseEntity.ok(CommonApiResponse.success(null));
    }
}
