package com.example.backend.repository.mypage;

import com.example.backend.entity.BookmarkProduct;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookmarkProductRepository extends JpaRepository<BookmarkProduct, Long> {

    // 회원의 모든 관심상품 productId 조회
    @Query("SELECT bp.product.productId FROM BookmarkProduct bp WHERE bp.user.userId = :userId ORDER BY bp.bookmarkProductId DESC")
    List<Long> findBookmarkProductIdList(Long userId, Pageable pageable);

    // 회원 관심상품과 같은 모델번호를 가진 전체 상품Id 조회
//    @Query("SELECT p.productId FROM Users u " +
//            "JOIN BookmarkProduct bp ON bp.user.userId = :userId " +
//            "JOIN Product p ON p.productId = bp.products.productId " +
//            "WHERE p.modelNum = (SELECT p.modelNum FROM p WHERE p.productId = bp.products.productId)"
//    )
//    @Query("SELECT p.productId FROM BookmarkProduct bp " +
//            "JOIN bp.products p " +
//            "WHERE bp.user.userId = :userId AND p.modelNum = bp.products.modelNum")
//    @Query("SELECT DISTINCT p2.productId FROM BookmarkProduct bp " +
//            "JOIN bp.user u " +
//            "JOIN bp.products p " +
//            "JOIN Product p2 ON p2.modelNum = p.modelNum " +
//            "WHERE u.userId = :userId")
//    List<Long> findProductIdListByModelNum(Long userId);

//    @Query("SELECT bp.products FROM BookmarkProduct bp WHERE bp.user.userId = :userId")
//    List<Product> findProductByUserId(Long userId);

}
