package com.phs.ewallet.repository;

import com.phs.ewallet.entity.Expense;
import com.phs.ewallet.entity.Income;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface IncomeRepo extends JpaRepository<Income, Long> {

    //lấy toàn bộ thu nhập của 1 profile
    List<Income> findByProfileIdOrderByDateDesc(Long profileId);

    //lấy 5 khoản thu nhập gần nhất của profile
    List<Income> findTop5ByProfileIdOrderByDateDesc(Long profileId);

    //tổng thu nhập
    @Query("SELECT SUM(i.amount) FROM Income i WHERE i.profile.id = :profileId ")
    BigDecimal findTotalIncomeByProfileId(@Param("profileId") Long profileId);

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
}
