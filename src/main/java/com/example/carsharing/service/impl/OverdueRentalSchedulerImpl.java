package com.example.carsharing.service.impl;

import com.example.carsharing.model.rental.Rental;
import com.example.carsharing.repository.rental.RentalRepository;
import com.example.carsharing.service.OverdueRentalScheduler;
import com.example.carsharing.telegram.notification.NotificationService;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OverdueRentalSchedulerImpl implements OverdueRentalScheduler {
    private final RentalRepository rentalRepository;
    private final NotificationService notificationService;

    @Scheduled(cron = "0 0 8 * * *")
    @Override
    public void checkOverdueRentals() {
        LocalDate today = LocalDate.now();
        List<Rental> overdueRentals = rentalRepository
                .findAllByReturnDateBeforeAndActualReturnDateIsNull(today);
        for (Rental rental : overdueRentals) {
            notificationService.sendOverdueRentalNotification(rental);
        }
    }
}
