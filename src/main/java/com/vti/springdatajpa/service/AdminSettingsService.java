package com.vti.springdatajpa.service;

import com.vti.springdatajpa.dto.SystemConfigDTO;
import com.vti.springdatajpa.entity.SystemConfig;
import com.vti.springdatajpa.repository.SystemConfigRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminSettingsService {

    private final SystemConfigRepository systemConfigRepository;

    public List<SystemConfigDTO> getAllSettings() {
        return systemConfigRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<SystemConfigDTO> updateSettings(List<SystemConfigDTO> settings) {
        for (SystemConfigDTO dto : settings) {
            SystemConfig config = systemConfigRepository.findByConfigKey(dto.getConfigKey())
                    .orElse(new SystemConfig());
            config.setConfigKey(dto.getConfigKey());
            config.setConfigValue(dto.getConfigValue());
            if (dto.getDescription() != null) {
                config.setDescription(dto.getDescription());
            }
            systemConfigRepository.save(config);
        }
        return getAllSettings();
    }

    private SystemConfigDTO mapToDTO(SystemConfig config) {
        return new SystemConfigDTO(config.getConfigKey(), config.getConfigValue(), config.getDescription());
    }
}
