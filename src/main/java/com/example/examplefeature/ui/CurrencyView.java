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

@PageTitle("Câmbios")
@Menu(order = 80, icon = "line-awesome/svg/money-bill-wave-solid.svg")
@Route(value = "currency", layout = MainLayout.class)
public class CurrencyView extends VerticalLayout {

    record Row(String moeda, Double taxa) {}

    private final CurrencyService service;
    private final Grid<Row> grid = new Grid<>(Row.class, false);

    public CurrencyView(CurrencyService service) {
        this.service = service;

        setSizeFull();
        addClassNames(LumoUtility.Padding.LARGE, LumoUtility.Gap.LARGE);

        var title = new H2("Taxas de Câmbio (base EUR)");
        var refresh = new Button("Atualizar", e -> loadData());

        grid.addColumn(Row::moeda).setHeader("Moeda").setAutoWidth(true);
        grid.addColumn(Row::taxa).setHeader("Taxa").setAutoWidth(true);

        var actions = new HorizontalLayout(refresh);
        actions.setAlignItems(Alignment.CENTER);
        add(title, actions, grid);

        loadData();
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
            grid.setItems(); // limpar grelha
        }
    }
}
