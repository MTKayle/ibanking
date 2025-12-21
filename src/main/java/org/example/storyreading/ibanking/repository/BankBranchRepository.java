package org.example.storyreading.ibanking.repository;

import org.example.storyreading.ibanking.entity.BankBranch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BankBranchRepository extends JpaRepository<BankBranch, Long> {

    /**
     * Tìm các chi nhánh gần nhất dựa trên công thức Haversine
     * Sắp xếp theo khoảng cách tăng dần
     */
    @Query(value = "SELECT *, " +
            "(6371 * acos(cos(radians(:latitude)) * cos(radians(latitude)) * " +
            "cos(radians(longitude) - radians(:longitude)) + " +
            "sin(radians(:latitude)) * sin(radians(latitude)))) AS distance " +
            "FROM bank_branches " +
            "ORDER BY distance ASC " +
            "LIMIT :limit",
            nativeQuery = true)
    List<BankBranch> findNearestBranches(
            @Param("latitude") Double latitude,
            @Param("longitude") Double longitude,
            @Param("limit") Integer limit
    );
}

