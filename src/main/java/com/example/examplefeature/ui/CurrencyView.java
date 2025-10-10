package com.example.examplefeature.ui;

import com.example.base.ui.MainLayout;
import com.example.examplefeature.forex.CurrencyService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;

import java.io.IOException;
import java.util.Map;

@Route(value = "currency", layout = MainLayout.class)
@PageTitle("Forex (EUR)")
@Menu(title = "Forex", icon = "la la-dollar-sign")
public class CurrencyView extends VerticalLayout {

    private final CurrencyService service;
    private final Grid<Row> grid = new Grid<>(Row.class, false);

    public static class Row {
        private final String currency;
        private final Double rate;
        public Row(String currency, Double rate) { this.currency = currency; this.rate = rate; }
        public String getCurrency() { return currency; }
        public Double getRate() { return rate; }
    }

    public CurrencyView(CurrencyService service) {
        this.service = service;

        setSizeFull();
        addClassNames(LumoUtility.Padding.MEDIUM, LumoUtility.Gap.SMALL);

        H2 title = new H2("💱 Taxas de câmbio (base EUR)");
        Button refresh = new Button("Atualizar", e -> loadData());

        grid.addColumn(Row::getCurrency).setHeader("Moeda").setAutoWidth(true);
        grid.addColumn(Row::getRate).setHeader("Taxa").setAutoWidth(true);

        HorizontalLayout actions = new HorizontalLayout(refresh);
        add(title, actions, grid);

        loadData(); // carrega ao abrir, mas agora com try/catch
    }

    private void loadData() {
        try {
            Map<String, Double> rates = service.getRates();
            grid.setItems(rates.entrySet().stream()
                    .map(e -> new Row(e.getKey(), e.getValue()))
                    .toList());

            if (rates.isEmpty()) {
                Notification.show("Sem dados de câmbio no momento.");
            }
        } catch (IOException ex) {
            Notification.show("Erro a obter câmbios: " + ex.getMessage(), 5000, Notification.Position.MIDDLE);
            grid.setItems(); // limpa grelha
        }
    }
}
