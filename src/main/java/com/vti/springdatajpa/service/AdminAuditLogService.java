package com.vti.springdatajpa.service;

import com.vti.springdatajpa.dto.AdminAuditLogDTO;
import com.vti.springdatajpa.entity.AdminAction;
import com.vti.springdatajpa.entity.User;
import com.vti.springdatajpa.repository.AdminActionRepository;
import com.vti.springdatajpa.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminAuditLogService {

    private final AdminActionRepository adminActionRepository;
    private final UserRepository userRepository;

    public Page<AdminAuditLogDTO> getAuditLogs(Pageable pageable) {
        return adminActionRepository.findAllByOrderByCreatedAtDesc(pageable).map(this::mapToDTO);
    }

    private AdminAuditLogDTO mapToDTO(AdminAction action) {
        String adminName = "Unknown";
        if (action.getAdminId() != null) {
            adminName = userRepository.findById(action.getAdminId())
                    .map(u -> u.getFullName() != null ? u.getFullName() : u.getUserName())
                    .orElse("Unknown Admin");
        }

        return AdminAuditLogDTO.builder()
                .id(action.getId())
                .adminId(action.getAdminId())
                .adminName(adminName)
                .actionType(action.getActionType())
                .targetType(action.getTargetType())
                .targetId(action.getTargetId())
                .reason(action.getReason())
                .metadata(action.getMetadata())
                .createdAt(action.getCreatedAt())
                .build();
    }
}
