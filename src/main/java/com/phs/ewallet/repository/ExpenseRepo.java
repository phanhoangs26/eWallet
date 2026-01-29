package com.phs.ewallet.repository;

import com.phs.ewallet.entity.Expense;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface ExpenseRepo extends JpaRepository<Expense, Long> {

    //lấy toàn bộ chi tiêu của 1 profile
    List<Expense> findByProfileIdOrderByDateDesc(Long profileId);

    //lấy 5 khoản chi tiêu gần nhất của profile
    List<Expense> findTop5ByProfileIdOrderByDateDesc(Long profileId);

    //tổng chi tiêu
    @Query("SELECT SUM(e.amount) FROM Expense e WHERE e.profile.id = :profileId ")
    BigDecimal findTotalExpenseByProfileId(@Param("profileId") Long profileId);

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
}


