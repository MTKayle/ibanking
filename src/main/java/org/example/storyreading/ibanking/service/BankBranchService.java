package org.example.storyreading.ibanking.service;

import org.example.storyreading.ibanking.dto.BankBranchResponse;
import org.example.storyreading.ibanking.entity.BankBranch;
import org.example.storyreading.ibanking.repository.BankBranchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class BankBranchService {

    @Autowired
    private BankBranchRepository bankBranchRepository;

    /**
     * Tìm các chi nhánh gần nhất dựa trên tọa độ
     * Sử dụng công thức Haversine để tính khoảng cách
     */
    public List<BankBranchResponse> findNearestBranches(Double latitude, Double longitude, Integer limit) {
        if (limit == null || limit <= 0) {
            limit = 3; // Mặc định lấy 3 chi nhánh
        }

        limit =3; // Giới hạn tối đa 3 chi nhánh

        List<BankBranch> branches = bankBranchRepository.findNearestBranches(latitude, longitude, limit);

        List<BankBranchResponse> responses = new ArrayList<>();
        for (BankBranch branch : branches) {
            // Tính khoảng cách bằng công thức Haversine
            Double distance = calculateDistance(
                latitude, longitude,
                branch.getLatitude(), branch.getLongitude()
            );

            BankBranchResponse response = new BankBranchResponse(
                branch.getBranchId(),
                branch.getName(),
                branch.getAddress(),
                branch.getLatitude(),
                branch.getLongitude(),
                Math.round(distance * 100.0) / 100.0 // Làm tròn 2 chữ số thập phân
            );
            responses.add(response);
        }

        return responses;
    }

    /**
     * Công thức Haversine để tính khoảng cách giữa 2 tọa độ
     * Trả về khoảng cách tính bằng km
     */
    private Double calculateDistance(Double lat1, Double lon1, Double lat2, Double lon2) {
        final int EARTH_RADIUS = 6371; // Bán kính Trái Đất tính bằng km

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS * c;
    }

    /**
     * Lấy tất cả chi nhánh
     */
    public List<BankBranch> getAllBranches() {
        return bankBranchRepository.findAll();
    }

    /**
     * Lấy chi nhánh theo ID
     */
    public BankBranch getBranchById(Long branchId) {
        return bankBranchRepository.findById(branchId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy chi nhánh với ID: " + branchId));
    }
}

