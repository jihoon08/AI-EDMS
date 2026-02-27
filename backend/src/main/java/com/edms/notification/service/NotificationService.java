package com.edms.notification.service;

import com.edms.notification.domain.Alert;
import com.edms.notification.dto.NotificationDto;
import com.edms.notification.repository.AlertRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final AlertRepository alertRepository;

    // 알림 생성 (내부에서 호출)
    @Transactional
    public void createAlert(UUID userUuid, String alertType, String title, String message,
                            String link, String referenceType, UUID referenceUuid) {
        Alert alert = Alert.builder()
                .userUuid(userUuid)
                .alertType(alertType)
                .title(title)
                .message(message)
                .link(link)
                .referenceType(referenceType)
                .referenceUuid(referenceUuid)
                .build();
        alertRepository.save(alert);
        log.info("알림 생성: user={}, type={}, title={}", userUuid, alertType, title);
    }

    // 결재 관련 알림
    @Transactional
    public void notifyApprovalRequest(UUID approverUuid, UUID approvalUuid, String documentTitle) {
        createAlert(approverUuid, "APPROVAL_REQUEST",
                "결재 요청",
                documentTitle + " 문서의 결재 요청이 도착했습니다.",
                "/workflows",
                "APPROVAL", approvalUuid);
    }

    @Transactional
    public void notifyApprovalApproved(UUID requesterUuid, UUID approvalUuid, String documentTitle) {
        createAlert(requesterUuid, "APPROVAL_APPROVED",
                "결재 승인",
                documentTitle + " 문서가 승인되었습니다.",
                "/workflows",
                "APPROVAL", approvalUuid);
    }

    @Transactional
    public void notifyApprovalRejected(UUID requesterUuid, UUID approvalUuid, String documentTitle) {
        createAlert(requesterUuid, "APPROVAL_REJECTED",
                "결재 반려",
                documentTitle + " 문서가 반려되었습니다.",
                "/workflows",
                "APPROVAL", approvalUuid);
    }

    // 알림 목록 조회
    @Transactional(readOnly = true)
    public Page<NotificationDto.AlertResponse> getAlerts(UUID userUuid, boolean unreadOnly, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        if (unreadOnly) {
            return alertRepository.findByUserUuidAndReadFlagFalseOrderByCreatedAtDesc(userUuid, pageable)
                    .map(NotificationDto.AlertResponse::from);
        }
        return alertRepository.findByUserUuidOrderByCreatedAtDesc(userUuid, pageable)
                .map(NotificationDto.AlertResponse::from);
    }

    // 읽지 않은 알림 수
    @Transactional(readOnly = true)
    public NotificationDto.UnreadCountResponse getUnreadCount(UUID userUuid) {
        long count = alertRepository.countByUserUuidAndReadFlagFalse(userUuid);
        return NotificationDto.UnreadCountResponse.builder().count(count).build();
    }

    // 개별 읽음 처리
    @Transactional
    public void markAsRead(UUID alertUuid) {
        alertRepository.findById(alertUuid).ifPresent(Alert::markAsRead);
    }

    // 전체 읽음 처리
    @Transactional
    public void markAllAsRead(UUID userUuid) {
        alertRepository.markAllAsRead(userUuid);
    }
}
