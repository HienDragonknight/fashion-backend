package com.fashion.controller;

import com.fashion.dto.request.AddressRequest;
import com.fashion.dto.response.ApiResponse;
import com.fashion.entity.Address;
import com.fashion.exception.ResourceNotFoundException;
import com.fashion.repository.AddressRepository;
import com.fashion.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/addresses")
@RequiredArgsConstructor
public class AddressController {

    private final AddressRepository addressRepository;
    private final UserRepository userRepository;

    private Long getUserId(UserDetails userDetails) {
        return userRepository.findByEmailOrPhone(userDetails.getUsername()).orElseThrow().getId();
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Address>>> getAddresses(@AuthenticationPrincipal UserDetails userDetails) {
        Long userId = getUserId(userDetails);
        return ResponseEntity.ok(ApiResponse.success(
                addressRepository.findByUserIdOrderByIsDefaultDescCreatedAtAsc(userId)));
    }

    @PostMapping
    @Transactional
    public ResponseEntity<ApiResponse<Address>> createAddress(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody AddressRequest request) {
        Long userId = getUserId(userDetails);
        var user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User", userId));

        if (request.getIsDefault()) {
            addressRepository.clearDefaultForUser(userId);
        }

        Address address = Address.builder()
                .user(user)
                .fullName(request.getFullName())
                .phone(request.getPhone())
                .provinceId(request.getProvinceId())
                .province(request.getProvince())
                .districtId(request.getDistrictId())
                .district(request.getDistrict())
                .wardCode(request.getWardCode())
                .ward(request.getWard())
                .detail(request.getDetail())
                .isDefault(request.getIsDefault())
                .build();

        return ResponseEntity.ok(ApiResponse.success("Đã thêm địa chỉ", addressRepository.save(address)));
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<ApiResponse<Void>> deleteAddress(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id) {
        Long userId = getUserId(userDetails);
        addressRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Địa chỉ không tìm thấy"));
        addressRepository.deleteById(id);
        return ResponseEntity.ok(ApiResponse.success("Đã xóa địa chỉ", null));
    }

    @PutMapping("/{id}/default")
    @Transactional
    public ResponseEntity<ApiResponse<Void>> setDefault(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id) {
        Long userId = getUserId(userDetails);
        addressRepository.clearDefaultForUser(userId);
        Address address = addressRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Địa chỉ không tìm thấy"));
        address.setIsDefault(true);
        addressRepository.save(address);
        return ResponseEntity.ok(ApiResponse.success("Đã đặt địa chỉ mặc định", null));
    }
}
