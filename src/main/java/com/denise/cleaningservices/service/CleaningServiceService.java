package com.denise.cleaningservices.service;

import com.denise.cleaningservices.model.CleaningService;
import com.denise.cleaningservices.exception.InternalServerException;
import com.denise.cleaningservices.exception.ResourceNotFoundException;
import com.denise.cleaningservices.repository.CleaningServiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.sql.rowset.serial.SerialBlob;
import java.io.IOException;
import java.sql.Blob;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class CleaningServiceService implements ICleaningServiceService {

    private final CleaningServiceRepository cleaningServiceRepository;
    @Override
    public CleaningService addNewCleaningService(String name, String description, Double price, MultipartFile photo) throws IOException, SQLException {
        CleaningService cleaningService = new CleaningService();
        cleaningService.setName(name);
        cleaningService.setDescription(description);
        cleaningService.setPrice(price);
        if (!photo.isEmpty()) {
            byte[] photoBytes = photo.getBytes();
            Blob photoBlob = new SerialBlob(photoBytes);
            cleaningService.setPhoto(photoBlob);
        }
        return cleaningServiceRepository.save(cleaningService);
    }

    @Override
    public List<String> getAllCleaningServiceNames() {
        return cleaningServiceRepository.findDistinctCleaningServiceNames();
    }

    @Override
    public List<CleaningService> getAllCleaningServices() {
        return cleaningServiceRepository.findAll();
    }

    @Override
    public byte[] getCleaningServicePhotoById(Long id) throws SQLException {
        Optional<CleaningService> theservice = cleaningServiceRepository.findById(id);
        if(theservice.isEmpty()){
            throw new ResourceNotFoundException("Sorry, Cleaning Service Not Found!");
        }
        Blob photoBlob = theservice.get().getPhoto();
        if(photoBlob != null){
            return photoBlob.getBytes(1,(int) photoBlob.length());
        }

        return null;
    }

    @Override
    public void deleteCleaningService(Long id) {
        Optional<CleaningService> theService = cleaningServiceRepository.findById(id);
        if(theService.isPresent()){
            cleaningServiceRepository.deleteById(id);
        }
    }

    @Override
    public CleaningService updateCleaningService(Long id, String name, String description, Double price, byte[] photoBytes) {
        CleaningService cleaningService = cleaningServiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("CleaningService not found with id " + id));
        if (name != null) cleaningService.setName(name);
        if (description != null) cleaningService.setDescription(description);
        if (price != null) cleaningService.setPrice(price);
        if (photoBytes != null && photoBytes.length > 0) {
            try {
                cleaningService.setPhoto(new SerialBlob(photoBytes));
            } catch (SQLException ex) {
                throw new InternalServerException("Fail updating cleaning service");
            }
        }
        return cleaningServiceRepository.save(cleaningService);

    }

    @Override
    public Optional<CleaningService> getCleaningServiceById(Long id) {
        return Optional.of(cleaningServiceRepository.findById(id).get());
    }

    @Override
    public List<CleaningService> getAvailableCleaningServices(LocalDate startDate, LocalDate endDate, String name) {
        return cleaningServiceRepository.findAvailableCleaningServicesByDatesAndName(startDate, endDate, name);
    }


}
