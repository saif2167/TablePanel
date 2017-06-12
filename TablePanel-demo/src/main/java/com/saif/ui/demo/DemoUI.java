package com.saif.ui.demo;

import javax.servlet.annotation.WebServlet;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.components.grid.DetailsGenerator;
import com.vaadin.ui.renderers.TextRenderer;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import saif.com.vaadin.ui.GridPanel.DefaultGridConfiguration;
import saif.com.vaadin.ui.GridPanel.GridPanel;
import saif.com.vaadin.ui.GridPanel.utils.GridPanelItemProvider;

@Theme("demo")
@Title("TablePanel Test..")
@SuppressWarnings("serial")
public class DemoUI extends UI {

    @WebServlet(value = "/*", asyncSupported = true)
    @VaadinServletConfiguration(productionMode = false, ui = DemoUI.class)
    public static class Servlet extends VaadinServlet {
    }

    @Override
    protected void init(VaadinRequest request) {

        setLocale(new Locale("pl", "PL"));
        final VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();
        layout.setWidth("100%");

        List<Bean> list = new ArrayList();
        for (int x = 0; x < 20; x++) {
            Bean u = new Bean();
            u.setFirstName("Fname " + x);
            u.setLastName("Fname " + x);
            u.setMan(true);
            u.setAge(x);
            u.setType(Bean.Type.MAN);
            u.setPrice(1223);
            u.setAllowed(Boolean.FALSE);
            list.add(u);
        }
        GridPanel<Bean> grid = new GridPanel<Bean>(Bean.class, new DefaultGridConfiguration());
        //grid.getGrid().getColumn("firstName").setRenderer(new ButtonRenderer().);
        grid.addItems(list);
        layout.addComponent(grid);
        setContent(layout);
        setSizeFull();
        grid.setItemProvider(new GridPanelItemProvider<Bean>() {
            @Override
            public List<Bean> supply(int offset, int limit, Map<String, Object> filters, GridPanelItemProvider.Option option) {
                if (option.isTotalCountRequire()) {
                    int num = new Random().nextInt(2999);
                    System.out.println("Random count : " + num + " Filter : " + filters);
                    option.setTotalItemCount(num);

                }
                System.out.println("Updating list......");
                return list;

            }
        });
        grid.addColumnCollapsedListener((id, isCollapsed) -> {
            System.out.println(String.format("Collapsed Event id %s isCollapsed %s", id, isCollapsed));
        });
        grid.getGrid().setDetailsGenerator(new DetailsGenerator<Bean>() {
            @Override
            public Component apply(Bean t) {
                grid.getGrid().setDetailsVisible(t, true);
                return new Label(t.getFirstName());
            }
        });
        
        grid.pack();
    }
}
