package ru.sergeew.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.sergeew.entity.AppUser;
import ru.sergeew.entity.Reminder;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReminderRepository extends JpaRepository<Reminder, Long> {
    @Query("SELECT r FROM Reminder r WHERE r.sendTime <= :currentDateTime AND r.isSent = false")
    List<Reminder> findUnsentRemindersBeforeDateTime(LocalDateTime currentDateTime);

    @Modifying
    @Query("UPDATE Reminder r SET r.isSent = true WHERE r IN :reminders")
    void markRemindersAsSent(List<Reminder> reminders);

    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END FROM Reminder r WHERE r.id = :id AND r.isSent = true")
    boolean isReminderSent(Long id);

    @Query("SELECT COUNT(r) < 30 FROM Reminder r WHERE r.appUser = :appUser AND r.isSent = false")
    boolean canCreateNewReminder(AppUser appUser);

    @Query("SELECT r FROM Reminder r WHERE r.appUser = :appUser AND r.isSent = false")
    List<Reminder> findUnsentRemindersByAppUser(AppUser appUser);

    void deleteAllByAppUser(AppUser appUser);
}
