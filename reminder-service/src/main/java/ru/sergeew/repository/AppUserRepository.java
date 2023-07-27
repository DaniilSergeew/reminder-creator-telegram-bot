package ru.sergeew.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.sergeew.entity.AppUser;

import java.util.Optional;

@Repository
public interface AppUserRepository extends JpaRepository<AppUser, Long> {
    Optional<AppUser> findByTelegramUserId(Long id);

    Optional<AppUser> findByTelegramUserIdAndChatId(long telegramUserId, long chatId);

    Optional<AppUser> findByChatId(long chatId);
}
