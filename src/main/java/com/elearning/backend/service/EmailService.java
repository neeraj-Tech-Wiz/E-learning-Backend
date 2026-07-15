package com.elearning.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class EmailService {

    @Value("${brevo.api.key}")
    private String brevoApiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    public void sendWarningEmail(String to, String studentName,
                                 int percentage, int month, int year) {

        // Plain text fallback (kept for clients that block/strip HTML)
        String textBody = String.format(
                "Dear %s,\n\n" +
                        "Your attendance for %d/%d has fallen below the required threshold.\n" +
                        "Current attendance: %d%%.\n\n" +
                        "Please improve your attendance to avoid further action.\n\n" +
                        "Regards,\nSchool Administration",
                studentName, month, year, percentage
        );

        String htmlBody = buildHtmlBody(studentName, percentage, month, year);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("api-key", brevoApiKey);

        Map<String, Object> payload = Map.of(
                "sender", Map.of(
                        "name", "eLearning Education",
                        "email", "elearningplatformedu@gmail.com"
                ),
                "to", List.of(
                        Map.of("email", to)
                ),
                "subject", "⚠️ Attendance Warning - Action Required",
                "htmlContent", htmlBody,
                "textContent", textBody
        );

        HttpEntity<Map<String, Object>> entity =
                new HttpEntity<>(payload, headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    "https://api.brevo.com/v3/smtp/email",
                    HttpMethod.POST,
                    entity,
                    String.class
            );
        }  catch (HttpStatusCodeException e) {
            System.out.println("Brevo Error Status: " + e.getStatusCode());
            System.out.println("Brevo Error Body: " + e.getResponseBodyAsString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Builds a self-contained, inline-CSS HTML email.
     * Inline styles are used deliberately — most email clients
     * (Gmail, Outlook) strip <style> blocks and external CSS.
     */
    private String buildHtmlBody(String studentName, int percentage, int month, int year) {

        String monthName = java.time.Month.of(month)
                .getDisplayName(java.time.format.TextStyle.FULL, java.util.Locale.ENGLISH);

        // Color and label shift based on severity
        String statusColor = percentage < 50 ? "#dc2626" : "#f59e0b"; // red vs amber
        String statusLabel = percentage < 50 ? "Critical" : "Below Threshold";
        String barColor = percentage < 50 ? "#dc2626" : "#f59e0b";

        return "<!DOCTYPE html>"
                + "<html lang=\"en\">"
                + "<head><meta charset=\"UTF-8\"><meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\"></head>"
                + "<body style=\"margin:0;padding:0;background-color:#f1f5f9;font-family:'Segoe UI',Roboto,Helvetica,Arial,sans-serif;\">"
                + "<table role=\"presentation\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" style=\"background-color:#f1f5f9;padding:32px 16px;\">"
                + "<tr><td align=\"center\">"
                + "<table role=\"presentation\" width=\"600\" cellpadding=\"0\" cellspacing=\"0\" style=\"max-width:600px;width:100%;background-color:#ffffff;border-radius:16px;overflow:hidden;box-shadow:0 4px 24px rgba(0,0,0,0.08);\">"

                // Header banner
                + "<tr><td style=\"background:linear-gradient(135deg,#4f46e5 0%,#7c3aed 100%);padding:36px 40px;text-align:center;\">"
                + "<div style=\"font-size:15px;letter-spacing:2px;color:rgba(255,255,255,0.75);text-transform:uppercase;margin-bottom:8px;\">eLearning Education</div>"
                + "<div style=\"font-size:24px;font-weight:700;color:#ffffff;\">Attendance Alert</div>"
                + "</td></tr>"

                // Status badge + greeting
                + "<tr><td style=\"padding:36px 40px 8px 40px;\">"
                + "<span style=\"display:inline-block;background-color:" + statusColor + "1A;color:" + statusColor + ";font-size:12px;font-weight:700;letter-spacing:1px;text-transform:uppercase;padding:6px 14px;border-radius:999px;\">" + statusLabel + "</span>"
                + "<h2 style=\"margin:18px 0 4px 0;font-size:20px;color:#0f172a;\">Hi " + studentName + ",</h2>"
                + "<p style=\"margin:0;font-size:15px;line-height:1.6;color:#475569;\">"
                + "Your attendance for <strong>" + monthName + " " + year + "</strong> has fallen below the required threshold. Please review the details below and take corrective action."
                + "</p>"
                + "</td></tr>"

                // Attendance percentage card with progress bar
                + "<tr><td style=\"padding:20px 40px;\">"
                + "<table role=\"presentation\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" style=\"background-color:#f8fafc;border:1px solid #e2e8f0;border-radius:12px;padding:24px;\">"
                + "<tr><td>"
                + "<table role=\"presentation\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\"><tr>"
                + "<td style=\"font-size:13px;color:#64748b;font-weight:600;text-transform:uppercase;letter-spacing:0.5px;\">Current Attendance</td>"
                + "<td align=\"right\" style=\"font-size:28px;font-weight:800;color:" + statusColor + ";\">" + percentage + "%</td>"
                + "</tr></table>"
                + "<div style=\"margin-top:14px;background-color:#e2e8f0;border-radius:999px;height:10px;width:100%;overflow:hidden;\">"
                + "<div style=\"background-color:" + barColor + ";height:10px;width:" + percentage + "%;border-radius:999px;\"></div>"
                + "</div>"
                + "<div style=\"margin-top:8px;font-size:12px;color:#94a3b8;\">Required minimum: 75%</div>"
                + "</td></tr>"
                + "</table>"
                + "</td></tr>"

                // Action note
                + "<tr><td style=\"padding:8px 40px 32px 40px;\">"
                + "<table role=\"presentation\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" style=\"background-color:#fef3c7;border-left:4px solid #f59e0b;border-radius:8px;\">"
                + "<tr><td style=\"padding:16px 18px;\">"
                + "<p style=\"margin:0;font-size:14px;line-height:1.6;color:#78350f;\">"
                + "<strong>Action required:</strong> Please improve your attendance to avoid further disciplinary action from the administration."
                + "</p>"
                + "</td></tr></table>"
                + "</td></tr>"

                // Footer
                + "<tr><td style=\"padding:24px 40px;background-color:#f8fafc;border-top:1px solid #e2e8f0;text-align:center;\">"
                + "<p style=\"margin:0 0 4px 0;font-size:13px;color:#94a3b8;\">Regards,<br><strong style=\"color:#475569;\">School Administration</strong></p>"
                + "<p style=\"margin:16px 0 0 0;font-size:11px;color:#cbd5e1;\">This is an automated message from eLearning Education. Please do not reply directly to this email.</p>"
                + "</td></tr>"

                + "</table>"
                + "</td></tr>"
                + "</table>"
                + "</body></html>";
    }
}