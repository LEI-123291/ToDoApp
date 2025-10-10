package com.example.examplefeature.ui;

import com.example.base.ui.MainLayout;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.theme.lumo.LumoUtility;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * View para gerar e visualizar QR Codes.
 */
@Route(value = "qrcode", layout = MainLayout.class)
@PageTitle("QR Code Generator")
@Menu(title = "QR Code", icon = "la la-qrcode")
public class QRCodeView extends VerticalLayout {

    private final TextField inputField = new TextField("Texto ou link para gerar QR Code");
    private final Image qrImage = new Image();

    public QRCodeView() {
        setSizeFull();
        addClassNames(LumoUtility.Padding.MEDIUM, LumoUtility.Gap.SMALL);

        H2 title = new H2("🧾 Gerador de QR Code");
        Button generateButton = new Button("Gerar QR Code", e -> generateQRCode());

        inputField.setWidth("400px");
        qrImage.setVisible(false); // esconde até gerar

        add(title, inputField, generateButton, qrImage);
    }

    private void generateQRCode() {
        String text = inputField.getValue();

        if (text == null || text.isBlank()) {
            Notification.show("Por favor, insere um texto antes de gerar o QR Code.");
            return;
        }

        try {
            byte[] imageBytes = createQRCodeImage(text, 300, 300);

            // Cria um recurso temporário para mostrar no Vaadin
            StreamResource resource = new StreamResource("qrcode.png",
                    () -> new ByteArrayInputStream(imageBytes));

            qrImage.setSrc(resource);
            qrImage.setVisible(true);
            qrImage.setWidth("300px");
            qrImage.setHeight("300px");

        } catch (Exception e) {
            Notification.show("Erro ao gerar QR Code: " + e.getMessage(), 5000, Notification.Position.MIDDLE);
        }
    }

    private byte[] createQRCodeImage(String text, int width, int height) throws Exception {
        BitMatrix matrix = new MultiFormatWriter()
                .encode(text, BarcodeFormat.QR_CODE, width, height);

        try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            MatrixToImageWriter.writeToStream(matrix, "PNG", output);
            return output.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Erroo ao criar imagem PNG", e);
        }
    }
}

