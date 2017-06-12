package saif.com.vaadin.ui.GridPanel.header.filter.utils;

import com.vaadin.client.renderers.ButtonRenderer;
import com.vaadin.client.renderers.NumberRenderer;
import com.vaadin.data.Binder;
import com.vaadin.event.ShortcutListener;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.UserError;
import com.vaadin.shared.ui.ValueChangeMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.DateField;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.PopupView;

import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import saif.com.vaadin.ui.GridPanel.GridPanel;
import saif.com.vaadin.ui.GridPanel.utils.DateInterval;

/**
 *
 * @author Saiful Islam<saifislam2167@gmail.com>
 */
public class FilterComponentManager {

    private GridPanel gridPanel;
    private String dateFormat = "yyyy-MM-dd hh:mm:ss";
    private Binder binder = new Binder();
    private List<Component> filterComponents = new ArrayList<>();

    public FilterComponentManager(GridPanel gridPanel) {
        this.gridPanel = gridPanel;
    }

    public Component createFilterComponent(Grid.Column col, Class<?> type) {
        Component c = null;
        if (type.isEnum()) {
            c = getEnumFilterComponent(col, type);
        } else if (type.equals(String.class)) {
            c = getTextFilterForString(col, type);
        } else if (type.equals(int.class) || type.equals(Integer.class)) {
            c = getTextFilterForIntegerColumn(col, type);
        } else if (type.equals(boolean.class) || type.equals(Boolean.class)) {
            c = getBooleanFilterComponent(col, type);
        } else if (type.equals(java.sql.Date.class) || type.equals(java.util.Date.class)) {
            c = getDateFilterComponent(col, type);
        }
        ///.......................
        if (c != null) {
            filterComponents.add(c);
        }
        return c;
    }

    private Component getTextFilterForIntegerColumn(final Grid.Column col, Class<?> type) {

        TextField t = new TextField();
        t.setData(col.getId());
        t.setHeight("33px");
        t.setPlaceholder("Filter..");
        // t.addShortcutListener(new FilterShortCutListener(type));
        t.addFocusListener(e -> t.addShortcutListener(new FilterShortCutListener(type)));
        t.addValueChangeListener(e -> {
            try {
                if (t.getValue() != null && !t.getValue().trim().isEmpty()) {
                    Integer.parseInt(t.getValue());
                }
                t.setComponentError(null);
            } catch (NumberFormatException ex) {
                t.setComponentError(new UserError("Invalid Number Format"));

            }
        });
        HorizontalLayout comp = new CustomFilterComponent() {
            @Override
            void clearValue() {
                t.setValue("");
            }
        };
        comp.addStyleName("textFilterComp");
        comp.setSpacing(false);
        comp.setMargin(false);
        comp.setSizeFull();
        comp.addComponentsAndExpand(t);
        return comp;
    }

    private Component getTextFilterForString(final Grid.Column col, Class<?> type) {
        TextField t = new TextField();
        t.setHeight("33px");
        t.setData(col.getId());
        t.setPlaceholder("Filter..");
        t.addFocusListener(e -> t.addShortcutListener(new FilterShortCutListener(type)));
        //t.addShortcutListener(new FilterShortCutListener(type));
        t.setValueChangeMode(ValueChangeMode.LAZY);
        HorizontalLayout comp = new CustomFilterComponent() {
            @Override
            void clearValue() {
                t.setValue("");
            }
        };
        comp.addStyleName("textFilterComp");
        comp.setSpacing(false);
        comp.setMargin(false);
        comp.addComponentsAndExpand(t);
        return comp;
    }

    private Component getBooleanFilterComponent(Grid.Column col, Class<?> type) {

        ComboBox<String> c = new ComboBox<>();
        c.setEmptySelectionCaption("All");
        c.setHeight("33px");
        c.setData(col.getId());
        List<String> booleanList = new ArrayList<>();
        //booleanList.add("All");
        booleanList.add("Yes");
        booleanList.add("No");
        c.setItems(booleanList);
        // c.setSelectedItem("All");
        c.addValueChangeListener(e -> {
            if (!gridPanel.getFooter().isOnRefresh()) {
                if (!Objects.equals(e.getOldValue(), e.getValue())) {
                    if (e.getValue() != null) {
                        if (e.getValue().equals("All")) {
                            if (gridPanel.getFilterMap().get((String) c.getData()) != null) {
                                gridPanel.getFilterMap().remove((String) c.getData());
                                gridPanel.fireFilterEvent();
                            }

                        } else {
                            gridPanel.getFilterMap().put((String) c.getData(), (e.getValue().equals("Yes")) ? Boolean.TRUE : Boolean.FALSE);
                            gridPanel.fireFilterEvent();
                        }
                    } else {
                        gridPanel.getFilterMap().remove((String) c.getData());
                        gridPanel.fireFilterEvent();
                    }

                }
            }
        });
        HorizontalLayout comp = new CustomFilterComponent() {
            @Override
            void clearValue() {
                c.setValue(null);
            }
        };
        comp.addStyleName("booelanComponent");
        comp.setSpacing(false);
        comp.setMargin(false);
        comp.setSizeFull();
        comp.addComponentsAndExpand(c);
        return comp;
    }

    private Component getEnumFilterComponent(Grid.Column col, Class<?> type) {

        ComboBox<Enum> c = new ComboBox<>();
        c.setEmptySelectionAllowed(true);
        c.setData(col.getId());
        c.setHeight("33px");
        c.setItems(Arrays.asList((Enum[]) type.getEnumConstants()));
        c.addValueChangeListener(e -> {
            if (!gridPanel.getFooter().isOnRefresh()) {
                if (e.getOldValue() != null && e.getValue() == null) {
                    gridPanel.getFilterMap().remove((String) c.getData());
                    gridPanel.fireFilterEvent();
                } else if (e.getOldValue() == null && e.getValue() == null) {
                    gridPanel.getFilterMap().remove((String) c.getData());
                } else if ((e.getOldValue() != null && e.getValue() != null) || (e.getOldValue() == null && e.getValue() != null)) {
                    gridPanel.getFilterMap().put((String) c.getData(), e.getValue());
                    gridPanel.fireFilterEvent();
                } else {
                    // nothing...
                }
            }
        });
        HorizontalLayout comp = new CustomFilterComponent() {
            @Override
            void clearValue() {
                c.setValue(null);
            }
        };
        comp.addStyleName("enumComponent");
        comp.setSpacing(false);
        comp.setMargin(false);
        comp.setSizeFull();
        comp.addComponentsAndExpand(c);
        return comp;
    }

    private Component getDateFilterComponent(Grid.Column col, Class<?> type) {
        PopupContent popupContent = new PopupContent();

        PopupView popUpView = new PopupView(null, popupContent);
        popUpView.setHideOnMouseOut(false);
        Button popupBtn = new Button("", e -> {
            popUpView.setPopupVisible(true);
        });

        popupBtn.setIcon(VaadinIcons.DATE_INPUT);
        popupBtn.addStyleName(ValoTheme.BUTTON_ICON_ONLY);
        popupBtn.addStyleName(ValoTheme.BUTTON_SMALL);
        //........................................//

        TextField datePlcHolder = new TextField();
        datePlcHolder.setHeight("33px");
        datePlcHolder.setPlaceholder(dateFormat);
        HorizontalLayout comp = new CustomFilterComponent() {
            @Override
            void clearValue() {
                datePlcHolder.setValue("");
                if (popupContent.getFrom() != null) {
                    popupContent.getFrom().setValue(null);
                }
                if (popupContent.getTo() != null) {
                    popupContent.getTo().setValue(null);
                }

            }
        };
        comp.setSizeFull();
        comp.setMargin(false);
        comp.setSpacing(false);
        comp.addComponent(popupBtn);
        comp.addComponentsAndExpand(datePlcHolder);
        comp.addComponent(popUpView);
        popupContent.setPopupContentUpdateListener((PopupContentChangeEvent e, Boolean isSet) -> {
            SimpleDateFormat df = new SimpleDateFormat(getDateFormat());
            String text = "";
            if (isSet) {

                Date from = e.getDateInterval().getFrom();
                Date to = e.getDateInterval().getTo();

                if (from != null && to != null) {
                    text = df.format(from) + " -" + df.format(to);
                    // datePlcHolder.setValue(df.format(from) +" -"+df.format(to));
                } else if (from != null && to == null) {
                    text = df.format(from) + " -";
                } else {

                }
                if (!datePlcHolder.getValue().trim().equals(text)) {
                    datePlcHolder.setValue(text);
                    gridPanel.getFilterMap().put(col.getId(), e.getDateInterval());
                    gridPanel.fireFilterEvent();
                }

            } else if (!datePlcHolder.getValue().trim().equals("")) {
                //fire remove filter event
                datePlcHolder.setValue(text);
                gridPanel.getFilterMap().remove(col.getId());
                gridPanel.fireFilterEvent();

            }
            popUpView.setPopupVisible(false);
        });

        return comp;
    }

    public void resetFilterValues() {
        filterComponents.forEach(this::resetComponentValue);

    }

    private void resetComponentValue(Component c) {
        if (c instanceof TextField) {
            ((TextField) c).setValue("");
        } else if (c instanceof ComboBox) {
            ((ComboBox) c).setValue(null);
        } else if (c instanceof CustomFilterComponent) {
            ((CustomFilterComponent) c).clearValue();
        }
    }

    public class FilterShortCutListener extends ShortcutListener {

        private Class<?> type;

        public FilterShortCutListener(Class<?> type) {
            super("TextFilterEnter", KeyCode.END, null);
            this.type = type;

        }

        @Override
        public void handleAction(Object sender, Object target) {
            try {
                if (target instanceof TextField) {
                    TextField t = ((TextField) target);
                    if (t.getComponentError() != null) {
                        return; //skip error Exists
                    }
                    String id = (String) t.getData();
                    if (!t.getValue().trim().equals("")) {
                        if (gridPanel.getFilterMap().get(id) == null) {
                            gridPanel.getFilterMap().put(id, t.getValue().trim());
                            gridPanel.fireFilterEvent();
                        } else if (!gridPanel.getFilterMap().get(id).equals(t.getValue())) {
                            gridPanel.getFilterMap().put(id, t.getValue().trim());
                            gridPanel.fireFilterEvent();
                        } else {
                            // nothing..
                        }

                    } else if (gridPanel.getFilterMap().get(id) != null) {
                        gridPanel.getFilterMap().remove(id);
                        gridPanel.fireFilterEvent();
                    }
                } else {
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public String getDateFormat() {
        return dateFormat;
    }

    public class PopupContent extends VerticalLayout {

        private PopupContentChangeListener listener;
        private DateField from;
        private DateField to;
        private VerticalLayout content;

        public PopupContent() {
            setHeightUndefined();
            setWidth("300px");
            content = new VerticalLayout();
            content.setMargin(false);
            content.setSpacing(false);
            content.setSizeFull();

            addComponent(content);
            initUI();
            //addStyleName(ValoTheme.PANEL_WELL);
        }

        public DateField getFrom() {
            return from;
        }

        public DateField getTo() {
            return to;
        }

        public void setPopupContentUpdateListener(PopupContentChangeListener l) {
            this.listener = l;
        }

        private void initUI() {

            FormLayout formLayout = new FormLayout();
            formLayout.setWidth("100%");
            HorizontalLayout buttonlayout = new HorizontalLayout();
            buttonlayout.setSizeFull();
            buttonlayout.setMargin(false);
            buttonlayout.setSpacing(true);

            from = new DateField("From");
            from.setDateFormat(getDateFormat());
            from.setLocale(UI.getCurrent().getLocale());
            from.setWidth("100%");

            to = new DateField("To");
            to.setDateFormat(getDateFormat());
            to.setLocale(UI.getCurrent().getLocale());
            to.setWidth("100%");

            formLayout.addComponents(from, to, buttonlayout);

            Button setbtn = new Button("set", e -> {
                fireEvent(true);
            });

            Button cancelBtn = new Button("Cancel", e -> {
                fireEvent(false);
            });
            buttonlayout.addComponents(setbtn, cancelBtn);
            buttonlayout.addComponentsAndExpand(new HorizontalLayout());

            content.addComponents(formLayout, buttonlayout);

        }

        private void fireEvent(boolean isSet) {
            if (listener != null) {
                try {
                    LocalDate fromLocateDate = from.getValue();
                    LocalDate toLocalDate = to.getValue();
                    Date fromDate = null;
                    Date toDate = null;
                    if (fromLocateDate != null && toLocalDate != null) {
                        fromDate = Date.from(fromLocateDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
                        toDate = Date.from(toLocalDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
                    } else if (fromLocateDate != null && toLocalDate == null) {
                        fromDate = Date.from(fromLocateDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
                    }
                    listener.onUpdate(new PopupContentChangeEvent(new DateInterval(fromDate, toDate)), isSet);

                } catch (Exception e) {
                } finally {
                    if (!isSet) {
                        from.setValue(null);
                        to.setValue(null);
                    }
                }

            }
        }

    }

    public interface PopupContentChangeListener {

        /**
         *
         * @param event
         * @return true is setButton clicked else guess cancel btn is clicked
         */
        public void onUpdate(PopupContentChangeEvent event, Boolean isSet);
    }

    public class PopupContentChangeEvent {

        private DateInterval dateInterval;

        public PopupContentChangeEvent(DateInterval dateInterval) {
            this.dateInterval = dateInterval;
        }

        public DateInterval getDateInterval() {
            return dateInterval;
        }

    }

}

abstract class CustomFilterComponent extends HorizontalLayout {

    public CustomFilterComponent() {
    }

    abstract void clearValue();

}
