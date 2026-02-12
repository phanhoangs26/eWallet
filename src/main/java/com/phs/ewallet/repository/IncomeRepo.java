package com.phs.ewallet.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.phs.ewallet.entity.Income;

@Repository
public interface IncomeRepo extends JpaRepository<Income, Long> {

    //lấy toàn bộ thu nhập của 1 profile
    List<Income> findByProfileIdOrderByDateDesc(Long profileId);

    //lấy 5 khoản thu nhập gần nhất của profile
    List<Income> findTop5ByProfileIdOrderByDateDesc(Long profileId);

    //tổng thu nhập
    @Query("SELECT SUM(i.amount) FROM Income i WHERE i.profile.id = :profileId ")
    BigDecimal findTotalIncomeByProfileId(@Param("profileId") Long profileId);

    //tổng thu nhập từ ngày cụ thể
    @Query("SELECT SUM(i.amount) FROM Income i WHERE i.profile.id = :profileId AND i.date >= :date")
    BigDecimal findTotalIncomeByProfileIdAndDateAfter(@Param("profileId") Long profileId, @Param("date") LocalDate date);

    //tổng thu nhập trong khoảng ngày
    @Query("SELECT SUM(i.amount) FROM Income i WHERE i.profile.id = :profileId AND i.date BETWEEN :startDate AND :endDate")
    BigDecimal findTotalIncomeByProfileIdAndDateBetween(@Param("profileId") Long profileId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    //đếm số lượng thu nhập trong khoảng ngày
    @Query("SELECT COUNT(i) FROM Income i WHERE i.profile.id = :profileId AND i.date BETWEEN :startDate AND :endDate")
    Long countIncomeByProfileIdAndDateBetween(@Param("profileId") Long profileId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    //select * from expenses where profile_id =?1 and date between ?2 and ?3 order by date desc and name like %?4%
    List<Income> findByProfileIdAndDateBetweenAndNameContainingIgnoreCase(Long profileId,
                                                                          LocalDate startDate,
                                                                          LocalDate endDate,
                                                                          String keyword,
                                                                          Sort sort
    );

    List<Income> findByProfileIdAndDateBetween(Long profileId,
                                                LocalDate startDate,
                                                LocalDate endDate);

    List<Income> findByProfileIdAndDate(Long profileId, LocalDate date);

    //download
    @Query("SELECT i FROM Income i JOIN FETCH i.category")
    List<Income> findAllWithCategory();

    // email theo profile hiện tại
    @Query("SELECT i FROM Income i JOIN FETCH i.category WHERE i.profile.id = :profileId")
    List<Income> findAllWithCategoryByProfileId(@Param("profileId") Long profileId);

}
