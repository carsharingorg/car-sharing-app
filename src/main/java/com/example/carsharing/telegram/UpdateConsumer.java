package com.example.carsharing.telegram;

import com.example.carsharing.model.user.User;
import com.example.carsharing.repository.user.UserRepository;
import java.util.Optional;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Component
public class UpdateConsumer implements LongPollingSingleThreadUpdateConsumer {
    private final TelegramClient telegramClient;
    private final UserRepository userRepository;

    public UpdateConsumer(UserRepository userRepository,
                          @Value("${telegram.bot.token}") String telegramBotToken) {
        this.userRepository = userRepository;
        this.telegramClient = new OkHttpTelegramClient(telegramBotToken);
    }

    @SneakyThrows
    @Override
    public void consume(Update update) {
        if (update.hasMessage()) {
            String message = update.getMessage().getText();
            Long chatId = update.getMessage().getChatId();

            if (message.equals("/start")) {
                SendMessage sendMessage = SendMessage.builder()
                        .text("Hello, to sign in, enter your email")
                        .chatId(chatId)
                        .build();
                telegramClient.execute(sendMessage);
            } else if (message.contains("@")) {
                Optional<User> optionalUserByEmail = userRepository.findByEmail(message);
                if (optionalUserByEmail.isPresent()) {
                    User user = optionalUserByEmail.get();
                    user.setTelegramChatId(chatId);
                    userRepository.save(user);

                    SendMessage linkedMessage = SendMessage.builder()
                            .text("Your email has been successfully linked.")
                            .chatId(chatId)
                            .build();
                    telegramClient.execute(linkedMessage);
                } else {
                    SendMessage linkedMessage = SendMessage.builder()
                            .text("Your email is not registered in our service")
                            .chatId(chatId)
                            .build();
                    telegramClient.execute(linkedMessage);
                }

            } else {
                SendMessage incorrectMessage = SendMessage.builder()
                        .text("I don't understand you.")
                        .chatId(chatId)
                        .build();
                telegramClient.execute(incorrectMessage);
            }

        }
    }
}
