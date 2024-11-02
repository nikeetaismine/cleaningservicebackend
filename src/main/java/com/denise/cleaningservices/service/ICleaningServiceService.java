package com.denise.cleaningservices.service;

import com.denise.cleaningservices.model.CleaningService;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ICleaningServiceService {

    CleaningService addNewCleaningService(String name, String description, Double price, MultipartFile photo) throws IOException, SQLException;

    List<String> getAllCleaningServiceNames();

    List<CleaningService> getAllCleaningServices();

    byte[] getCleaningServicePhotoById(Long id) throws SQLException;

    void deleteCleaningService(Long id);

    CleaningService updateCleaningService(Long id, String name, String description, Double price, byte[] photoBytes);

    Optional<CleaningService> getCleaningServiceById(Long id);

    List<CleaningService> getAvailableCleaningServices(LocalDate startDate, LocalDate endDate, String name);
}

