package com.example.carsharing.telegram.notification;

import com.example.carsharing.model.rental.Rental;
import com.example.carsharing.model.user.User;

public interface NotificationService {

    void sendRentalReturnedNotification(Rental rental, String car);

    void sendRentalCreatedNotification(Rental rental, User user, String car);

    void sendOverdueRentalNotification(Rental rental);
}
