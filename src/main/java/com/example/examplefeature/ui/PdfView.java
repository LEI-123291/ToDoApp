package com.example.examplefeature.ui;

import com.example.base.ui.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.theme.lumo.LumoUtility;

@Route(value = "pdf", layout = MainLayout.class)
@PageTitle("Exportar Tarefas (PDF)")
@Menu(title = "Exportar PDF", icon = "la la-file-pdf")
public class PdfView extends VerticalLayout {

    private final Anchor download;

    public PdfView() {
        setSizeFull();
        addClassNames(LumoUtility.Padding.MEDIUM, LumoUtility.Gap.SMALL);

        H2 title = new H2("🖨️ Exportar tarefas para PDF");

        Button gerar = new Button("Gerar PDF", e -> gerarPdf());

        download = new Anchor("Descarregar PDF");
        download.getElement().setAttribute("download", true);
        download.setVisible(false);

        HorizontalLayout actions = new HorizontalLayout(gerar, download);
        add(title, actions);
    }

    private void gerarPdf() {
        // Respeita o context path (caso a app não esteja em "/")
        String ctx = VaadinService.getCurrentRequest() != null
                ? VaadinService.getCurrentRequest().getContextPath() : "";
        // Param ts evita cache do browser
        String url = ctx + "/api/pdf/tasks?ts=" + System.currentTimeMillis();

        download.setHref(url);
        download.setText("Descarregar PDF");
        download.setVisible(true);

        // dispara o download automaticamente
        download.getElement().callJsFunction("click");

        Notification.show("PDF gerado.", 2000, Notification.Position.BOTTOM_END);
    }
}



