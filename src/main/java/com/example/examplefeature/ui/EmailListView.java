package com.example.examplefeature.ui;

import com.example.base.ui.component.ViewToolbar;
import com.example.examplefeature.EmailService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;

/**
 * Página Vaadin para envio de emails simulados.
 */
@Route("email")
@PageTitle("Send Email")
@Menu(order = 1, icon = "vaadin:envelope", title = "Send Email")
public class EmailListView extends Main {

    private final EmailService emailService;

    final EmailField toField;
    final TextField subjectField;
    final TextArea bodyField;
    final Button sendBtn;

    public EmailListView(EmailService emailService) {
        this.emailService = emailService;

        // --- Campos de entrada ---
        toField = new EmailField();
        toField.setPlaceholder("Destinatário (ex: utilizador@teste.local)");
        toField.setLabel("To");
        toField.setMinWidth("20em");

        subjectField = new TextField();
        subjectField.setPlaceholder("Assunto do email");
        subjectField.setLabel("Subject");
        subjectField.setMinWidth("20em");

        bodyField = new TextArea();
        bodyField.setPlaceholder("Escreve a mensagem aqui...");
        bodyField.setLabel("Message");
        bodyField.setWidth("30em");
        bodyField.setHeight("10em");

        sendBtn = new Button("Send", event -> sendEmail());
        sendBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        // --- Layout e estilo ---
        setSizeFull();
        addClassNames(
                LumoUtility.BoxSizing.BORDER,
                LumoUtility.Display.FLEX,
                LumoUtility.FlexDirection.COLUMN,
                LumoUtility.Padding.MEDIUM,
                LumoUtility.Gap.SMALL
        );

        add(new ViewToolbar("Send Email", ViewToolbar.group(toField, subjectField, bodyField, sendBtn)));
    }

    private void sendEmail() {
        String to = toField.getValue();
        String subject = subjectField.getValue();
        String body = bodyField.getValue();

        if (to.isEmpty() || subject.isEmpty() || body.isEmpty()) {
            Notification.show("Please fill all fields", 3000, Notification.Position.BOTTOM_END)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
            return;
        }

        try {
            emailService.sendEmail(to, subject, body);
            Notification.show("Email sent (simulated)", 3000, Notification.Position.BOTTOM_END)
                    .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            toField.clear();
            subjectField.clear();
            bodyField.clear();
        } catch (Exception e) {
            Notification.show("Failed to send email: " + e.getMessage(), 3000, Notification.Position.BOTTOM_END)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }
}

