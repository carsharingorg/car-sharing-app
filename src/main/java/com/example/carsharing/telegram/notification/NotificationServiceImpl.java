package com.example.carsharing.telegram.notification;

import com.example.carsharing.model.rental.Rental;
import com.example.carsharing.model.user.User;
import com.example.carsharing.repository.rental.RentalRepository;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Service
public class NotificationServiceImpl implements NotificationService {
    private final TelegramClient telegramClient;
    private final RentalRepository rentalRepository;

    public NotificationServiceImpl(@Value("${telegram.bot.token}")
                                   String telegramBotToken,
                                   RentalRepository rentalRepository) {
        this.telegramClient = new OkHttpTelegramClient(telegramBotToken);
        this.rentalRepository = rentalRepository;
    }

    @Override
    public void sendRentalCreatedNotification(Rental rental, User user, String car) {
        if (user.getTelegramChatId() != null) {
            String message = "✅ Your rental #" + rental.getId()
                    + " for the car " + car
                    + " has been successfully created!";
            sendMessage(user.getTelegramChatId(), message);
        }
    }

    @Override
    public void sendRentalReturnedNotification(Rental rental, String car) {
        User user = rental.getUser();

        if (user.getTelegramChatId() != null) {
            String status = rental.getActualReturnDate().isAfter(rental.getReturnDate())
                    ? " (overdue)" : "";
            String message = "🔄 Your rental #" + rental.getId() + " for " + car
                    + " has been returned successfully" + status + "!";
            sendMessage(user.getTelegramChatId(), message);
        }
    }

    @Override
    public void sendOverdueRentalNotification(Rental rental) {
        User user = rental.getUser();
        String car = rental.getCar().getBrand() + " " + rental.getCar().getModel();
        long daysOverdue = java.time.temporal.ChronoUnit.DAYS.between(
                rental.getReturnDate(), java.time.LocalDate.now());

        if (user.getTelegramChatId() != null) {
            String message = "⚠️ Your rental #" + rental.getId() + " for " + car
                    + " is " + daysOverdue + " days overdue! Please return the car ASAP.";
            sendMessage(user.getTelegramChatId(), message);
        }
    }

    @SneakyThrows
    private void sendMessage(Long telegramChatId, String message) {
        SendMessage sendMessage = SendMessage.builder()
                .text(message)
                .chatId(telegramChatId)
                .build();
        telegramClient.execute(sendMessage);
    }
}
