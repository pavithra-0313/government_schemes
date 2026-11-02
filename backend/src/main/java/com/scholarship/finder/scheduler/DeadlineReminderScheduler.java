package com.scholarship.finder.scheduler;

import com.scholarship.finder.entity.SavedScheme;
import com.scholarship.finder.repository.SavedSchemeRepository;
import com.scholarship.finder.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Component
public class DeadlineReminderScheduler {

    private static final Logger log = LoggerFactory.getLogger(DeadlineReminderScheduler.class);

    private final SavedSchemeRepository savedSchemeRepository;
    private final EmailService emailService;

    @Value("${app.scheduler.enabled:true}")
    private boolean schedulerEnabled;

    public DeadlineReminderScheduler(SavedSchemeRepository savedSchemeRepository, EmailService emailService) {
        this.savedSchemeRepository = savedSchemeRepository;
        this.emailService = emailService;
    }

    /**
     * Daily scheduled task running at 9:00 AM.
     */
    @Scheduled(cron = "${app.scheduler.cron:0 0 9 * * *}")
    @Transactional(readOnly = true)
    public void runDailyReminders() {
        if (!schedulerEnabled) {
            log.info("Deadline reminder scheduler is disabled.");
            return;
        }

        log.info("Starting daily scholarship deadline and renewal reminder job...");
        int emailsSent = process7DayClosingReminders();
        int renewalEmailsSent = processRenewalReminders();
        log.info("Reminder job finished. Dispatched {} closing reminders and {} renewal reminders.",
                emailsSent, renewalEmailsSent);
    }

    public int process7DayClosingReminders() {
        LocalDate today = LocalDate.now();
        LocalDate sevenDaysOut = today.plusDays(7);

        List<SavedScheme> expiringSavedSchemes = savedSchemeRepository.findSavedSchemesExpiringBetween(today, sevenDaysOut);
        int count = 0;

        for (SavedScheme ss : expiringSavedSchemes) {
            if ("Approved".equalsIgnoreCase(ss.getApplicationStatus()) || "Rejected".equalsIgnoreCase(ss.getApplicationStatus())) {
                continue; // No reminder needed for finalized applications
            }

            long daysLeft = ChronoUnit.DAYS.between(today, ss.getScheme().getLastDate());
            String subject = "Urgent: " + ss.getScheme().getNameEn() + " Deadline is in " + daysLeft + " day(s)!";
            String body = String.format(
                    "Dear %s,\n\n" +
                    "This is an automated reminder that your saved scheme '%s' is closing on %s (%d day(s) remaining).\n\n" +
                    "Your current application status is: %s.\n\n" +
                    "Please ensure all required documents from your checklist are ready and submit your application at:\n%s\n\n" +
                    "Best regards,\nStudent Scholarship and Education Scheme Finder Team",
                    ss.getUser().getName(),
                    ss.getScheme().getNameEn(),
                    ss.getScheme().getLastDate(),
                    daysLeft,
                    ss.getApplicationStatus(),
                    ss.getScheme().getApplyLink() != null ? ss.getScheme().getApplyLink() : "https://scholarships.gov.in"
            );

            emailService.sendReminderEmail(ss.getUser().getEmail(), subject, body);
            count++;
        }
        return count;
    }

    public int processRenewalReminders() {
        List<SavedScheme> renewalSchemes = savedSchemeRepository.findSavedSchemesRequiringRenewal();
        int count = 0;

        for (SavedScheme ss : renewalSchemes) {
            if (!"Applied".equalsIgnoreCase(ss.getApplicationStatus()) && !"Approved".equalsIgnoreCase(ss.getApplicationStatus())) {
                continue;
            }

            String subject = "Annual Renewal Reminder: " + ss.getScheme().getNameEn();
            String body = String.format(
                    "Dear %s,\n\n" +
                    "The scholarship '%s' requires annual renewal for continuing your financial benefits into the next academic year.\n\n" +
                    "Please verify your previous semester marks and renewal eligibility on the portal:\n%s\n\n" +
                    "Best regards,\nStudent Scholarship and Education Scheme Finder Team",
                    ss.getUser().getName(),
                    ss.getScheme().getNameEn(),
                    ss.getScheme().getApplyLink() != null ? ss.getScheme().getApplyLink() : "https://scholarships.gov.in"
            );

            emailService.sendReminderEmail(ss.getUser().getEmail(), subject, body);
            count++;
        }
        return count;
    }
}

