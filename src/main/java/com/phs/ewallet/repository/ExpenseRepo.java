package com.phs.ewallet.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.phs.ewallet.entity.Expense;

@Repository
public interface ExpenseRepo extends JpaRepository<Expense, Long> {

    //lấy toàn bộ chi tiêu của 1 profile
    List<Expense> findByProfileIdOrderByDateDesc(Long profileId);

    //lấy 5 khoản chi tiêu gần nhất của profile
    List<Expense> findTop5ByProfileIdOrderByDateDesc(Long profileId);

    //tổng chi tiêu
    @Query("SELECT SUM(e.amount) FROM Expense e WHERE e.profile.id = :profileId ")
    BigDecimal findTotalExpenseByProfileId(@Param("profileId") Long profileId);

    //tổng chi tiêu từ ngày cụ thể
    @Query("SELECT SUM(e.amount) FROM Expense e WHERE e.profile.id = :profileId AND e.date >= :date")
    BigDecimal findTotalExpenseByProfileIdAndDateAfter(@Param("profileId") Long profileId, @Param("date") LocalDate date);

    //tổng chi tiêu trong khoảng ngày
    @Query("SELECT SUM(e.amount) FROM Expense e WHERE e.profile.id = :profileId AND e.date BETWEEN :startDate AND :endDate")
    BigDecimal findTotalExpenseByProfileIdAndDateBetween(@Param("profileId") Long profileId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    //đếm số lượng chi tiêu trong khoảng ngày
    @Query("SELECT COUNT(e) FROM Expense e WHERE e.profile.id = :profileId AND e.date BETWEEN :startDate AND :endDate")
    Long countExpenseByProfileIdAndDateBetween(@Param("profileId") Long profileId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    //select * from expenses where profile_id =?1 and date between ?2 and ?3 order by date desc and name like %?4%
    List<Expense> findByProfileIdAndDateBetweenAndNameContainingIgnoreCase(Long profileId,
                                                                           LocalDate startDate,
                                                                           LocalDate endDate,
                                                                           String keyword,
                                                                           Sort sort
    );

    //xem tổng chi tiêu bắt đầu từ ngày bắt đầu -> ngày ket thúc (1 tháng)
    List<Expense> findByProfileIdAndDateBetween(Long profileId,
                                                LocalDate startDate,
                                                LocalDate endDate);
    List<Expense> findByProfileIdAndDate(Long profileId, LocalDate date);

    //download
    @Query("SELECT e FROM Expense e JOIN FETCH e.category")
    List<Expense> findAllWithCategory();

    // download/email theo profile hiện tại
    @Query("SELECT e FROM Expense e JOIN FETCH e.category WHERE e.profile.id = :profileId")
    List<Expense> findAllWithCategoryByProfileId(@Param("profileId") Long profileId);

}


