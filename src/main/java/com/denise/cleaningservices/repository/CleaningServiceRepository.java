package com.denise.cleaningservices.repository;

import com.denise.cleaningservices.model.CleaningService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface CleaningServiceRepository extends JpaRepository<CleaningService, Long> {

    @Query("SELECT DISTINCT c.name FROM CleaningService c")
    List<String> findDistinctCleaningServiceNames();


    @Query(" SELECT c FROM CleaningService c " +
            " WHERE c.name LIKE %:name " +
            " AND c.id NOT IN (" +
            "  SELECT br.cleaningService.id FROM Booking br " +
            "  WHERE ((br.endDate <= :endDate) AND (br.startDate >= :startDate))" +
            ")")
    List<CleaningService> findAvailableCleaningServicesByDatesAndName(LocalDate startDate, LocalDate endDate, String name);
}
