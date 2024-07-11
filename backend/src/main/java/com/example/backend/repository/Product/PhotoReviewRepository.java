package com.example.backend.repository.Product;

import com.example.backend.entity.PhotoReview;
import com.example.backend.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PhotoReviewRepository extends JpaRepository<PhotoReview,Long> {
    @Query("SELECT pr from PhotoReview pr LEFT JOIN FETCH pr.products p WHERE p.modelNum = :modelNum ")
    List<PhotoReview> findProductStyleReview(@Param("modelNum") String modelNum);
}
