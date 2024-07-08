package com.example.backend.repository.coupon;

import com.example.backend.entity.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CouponRepository extends JpaRepository<Coupon, Long> {

    @Query("SELECT COUNT(c) FROM Coupon c WHERE c.couponId = :couponId")
    long countByCouponId(Long couponId);
}