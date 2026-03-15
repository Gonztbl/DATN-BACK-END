package com.vti.springdatajpa.controller;

import com.vti.springdatajpa.entity.Address;
import com.vti.springdatajpa.entity.User;
import com.vti.springdatajpa.repository.AddressRepository;
import com.vti.springdatajpa.repository.UserRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/addresses")
@RequiredArgsConstructor
public class AddressController {

    private final AddressRepository addressRepository;
    private final UserRepository userRepository;

    @GetMapping
    public ResponseEntity<List<AddressDTO>> getAddresses() {
        Integer userId = getCurrentUserId();
        List<Address> addresses = addressRepository.findByUserId(userId);
        List<AddressDTO> dtos = addresses.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @PostMapping
    public ResponseEntity<AddressDTO> createAddress(@RequestBody AddressRequest request) {
        Integer userId = getCurrentUserId();
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        // If new address is default, unset other defaults
        if (Boolean.TRUE.equals(request.getIsDefault())) {
            addressRepository.findByUserIdAndIsDefaultTrue(userId).ifPresent(addr -> {
                addr.setIsDefault(false);
                addressRepository.save(addr);
            });
        }

        Address address = new Address();
        address.setUser(user);
        address.setRecipientName(request.getRecipientName());
        address.setPhone(request.getPhone());
        address.setAddress(request.getAddress());
        address.setIsDefault(request.getIsDefault());

        Address saved = addressRepository.save(address);
        return ResponseEntity.ok(mapToDTO(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AddressDTO> updateAddress(@PathVariable Integer id, @RequestBody AddressRequest request) {
        Integer userId = getCurrentUserId();
        Address address = addressRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new RuntimeException("Address not found"));

        // If setting as default, unset others
        if (Boolean.TRUE.equals(request.getIsDefault()) && !Boolean.TRUE.equals(address.getIsDefault())) {
            addressRepository.findByUserIdAndIsDefaultTrue(userId).ifPresent(addr -> {
                addr.setIsDefault(false);
                addressRepository.save(addr);
            });
        }

        if (request.getRecipientName() != null) {
            address.setRecipientName(request.getRecipientName());
        }
        if (request.getPhone() != null) {
            address.setPhone(request.getPhone());
        }
        if (request.getAddress() != null) {
            address.setAddress(request.getAddress());
        }
        if (request.getIsDefault() != null) {
            address.setIsDefault(request.getIsDefault());
        }

        Address saved = addressRepository.save(address);
        return ResponseEntity.ok(mapToDTO(saved));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAddress(@PathVariable Integer id) {
        Integer userId = getCurrentUserId();
        Address address = addressRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new RuntimeException("Address not found"));

        // Don't allow deleting the only default address
        if (Boolean.TRUE.equals(address.getIsDefault())) {
            long count = addressRepository.findByUserId(userId).size();
            if (count <= 1) {
                throw new RuntimeException("Cannot delete the only address. Please add another address first.");
            }
        }

        addressRepository.delete(address);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/default")
    public ResponseEntity<AddressDTO> setDefaultAddress(@PathVariable Integer id) {
        Integer userId = getCurrentUserId();
        Address address = addressRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new RuntimeException("Address not found"));

        // Unset current default
        addressRepository.findByUserIdAndIsDefaultTrue(userId).ifPresent(addr -> {
            addr.setIsDefault(false);
            addressRepository.save(addr);
        });

        // Set new default
        address.setIsDefault(true);
        Address saved = addressRepository.save(address);
        return ResponseEntity.ok(mapToDTO(saved));
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

    private AddressDTO mapToDTO(Address address) {
        AddressDTO dto = new AddressDTO();
        dto.setId(address.getId());
        dto.setRecipientName(address.getRecipientName());
        dto.setPhone(address.getPhone());
        dto.setAddress(address.getAddress());
        dto.setIsDefault(address.getIsDefault());
        dto.setCreatedAt(address.getCreatedAt());
        return dto;
    }

    @Data
    public static class AddressRequest {
        private String recipientName;
        private String phone;
        private String address;
        private Boolean isDefault;
    }

    @Data
    public static class AddressDTO {
        private Integer id;
        private String recipientName;
        private String phone;
        private String address;
        private Boolean isDefault;
        private java.time.LocalDateTime createdAt;
    }
}
