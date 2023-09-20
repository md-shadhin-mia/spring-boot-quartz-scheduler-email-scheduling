package com.example.quartzdemo.job;

import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.nio.charset.StandardCharsets;


@Component
public class EmailJob extends QuartzJobBean {

    private static final Logger logger = LoggerFactory.getLogger(EmailJob.class);

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private MailProperties mailProperties;

    /**
     * This method is executed when the scheduled job is triggered.
     *
     * @param jobExecutionContext The context of the job execution.
     * @throws JobExecutionException If an error occurs during job execution.
     */
    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        // Log the job execution
        logger.info("Executing Job with key {}", jobExecutionContext.getJobDetail().getKey());

        // Retrieve job data
        JobDataMap jobDataMap = jobExecutionContext.getMergedJobDataMap();
        String subject = jobDataMap.getString("subject");
        String body = jobDataMap.getString("body");
        String recipientEmail = jobDataMap.getString("email");

        // Send the email
        sendMail(mailProperties.getUsername(), recipientEmail, subject, body);
    }

    /**
     * Sends an email using the configured mail sender.
     *
     * @param fromEmail    The sender's email address.
     * @param toEmail      The recipient's email address.
     * @param subject      The email subject.
     * @param body         The email body.
     */
    private void sendMail(String fromEmail, String toEmail, String subject, String body) {
        try {
            // Log the email sending
            logger.info("Sending Email to {}", toEmail);

            // Create a MimeMessage for the email
            MimeMessage message = mailSender.createMimeMessage();

            // Use MimeMessageHelper to configure the email
            MimeMessageHelper messageHelper = new MimeMessageHelper(message, StandardCharsets.UTF_8.toString());
            messageHelper.setSubject(subject);
            messageHelper.setText(body, true);
            messageHelper.setFrom(fromEmail);
            messageHelper.setTo(toEmail);

            // Send the email
            mailSender.send(message);
        } catch (MessagingException ex) {
            // Log any errors during email sending
            logger.error("Failed to send email to {}", toEmail, ex);
        }
    }
}
