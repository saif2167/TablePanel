package saif.com.vaadin.ui.GridPanel.header;

import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.Page;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.PopupView;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import saif.com.vaadin.ui.GridPanel.GridPanel;
import saif.com.vaadin.ui.GridPanel.NotificationMsgProvider;
import saif.com.vaadin.ui.GridPanel.header.listeners.LimitChangeEvent;
import saif.com.vaadin.ui.GridPanel.header.listeners.LimitChangeListener;
import saif.com.vaadin.ui.GridPanel.header.listeners.PageChangeEvent;
import saif.com.vaadin.ui.GridPanel.header.listeners.PageNumberChangeListener;

/**
 *
 * @author Saiful Islam<saifislam2167@gmail.com>
 */
public class TablePanelHeader extends HorizontalLayout {

    private HorizontalLayout optionalPanel;
    private HorizontalLayout limitControlBar;
    private HorizontalLayout pageControlBar;
    private ComboBox<Integer> limitComboBox;
    private TextField pageNumberField;
    private Integer currentLimit = 50; //default
    private Integer currentPage = 1; //default
    private Integer totalNumberOfPages = 10; //default
    private Integer totalItemsCount = 1;
    private List<LimitChangeListener> limitChangeListeners = new ArrayList<>();
    private List<PageNumberChangeListener> pageChangeListeners = new ArrayList<>();
    private GridPanel gridPanel;
    private Button totalPagesButton;
    private Label itemsDescription;
    private List<BiConsumer<Object,Boolean>> collapseListener = new ArrayList<>(); 

    public TablePanelHeader(GridPanel gridPanel) {
        this.gridPanel = gridPanel;
        initUI();
        setWidth("100%");
    }

    private void initUI() {
        createLimitControlBar();
        addComponent(limitControlBar);
        optionalPanel = new HorizontalLayout(); // To add any optional Button or any component later...
        addComponentsAndExpand(optionalPanel);
        createPageControlBar();
        addComponent(pageControlBar);
    }

    private void createLimitControlBar() {
        limitControlBar = new HorizontalLayout();
        limitComboBox = new ComboBox<>("Rows Per Pages");
        limitComboBox.setWidth("100px");
        limitComboBox.setEmptySelectionAllowed(false);
        limitComboBox.addStyleName(ValoTheme.COMBOBOX_SMALL);
        limitComboBox.addStyleName(ValoTheme.COMBOBOX_ALIGN_CENTER);
        limitComboBox.setItems(5, 10, 20, 50, 80, 100);
        limitComboBox.setSelectedItem(currentLimit);
        limitComboBox.addValueChangeListener(e -> {
            if (!Objects.equals(e.getOldValue(), e.getValue())) {
                currentLimit = e.getValue();
                fireLimitChangeEvent();
                setTotalItemCount(getTotalItemsCount());
            }

        });
        limitControlBar.addComponent(limitComboBox);

    }

    private void fireLimitChangeEvent() {
        limitChangeListeners.forEach(e -> {
            e.onLimitChange(new LimitChangeEvent(getLimit()));
        });
        updateItemsDescription();
    }

    private void firePageChangeEvent() {
        pageChangeListeners.forEach(e -> {
            e.onPageChange(new PageChangeEvent(currentPage));
        });
        updateItemsDescription();

    }

    public Integer getLimit() {
        return currentLimit;
    }

    public Integer getPageNumber() {
        return currentPage;
    }

    public void addLimitChangeListener(LimitChangeListener listener) {
        if (!limitChangeListeners.contains(listener)) {
            limitChangeListeners.add(listener);
        }
    }

    public void removeLimitChangeListener(LimitChangeListener listener) {
        if (limitChangeListeners.contains(listener)) {
            limitChangeListeners.remove(listener);
        }
    }

    public void addPageChangeListener(PageNumberChangeListener listener) {
        if (!pageChangeListeners.contains(listener)) {
            pageChangeListeners.add(listener);
        }
    }

    public void removePageChangeListener(PageNumberChangeListener listener) {
        if (pageChangeListeners.contains(listener)) {
            pageChangeListeners.remove(listener);
        }
    }

    private void createPageControlBar() {
        pageControlBar = new HorizontalLayout();
        pageControlBar.setSpacing(false);
        pageControlBar.setHeight("100%");

        VerticalLayout controlBarContent = new VerticalLayout();
        controlBarContent.setSizeFull();
        controlBarContent.setSpacing(false);
        controlBarContent.setMargin(false);

        pageControlBar.addComponent(controlBarContent);
        HorizontalLayout firstRow = new HorizontalLayout();
        firstRow.setWidth("100%");
        firstRow.addStyleName("fit-to-right");
        HorizontalLayout secondRow = new HorizontalLayout();
        secondRow.addStyleName("fit-to-right");
        secondRow.setWidth("100%");
        firstRow.setSpacing(false);
        firstRow.setMargin(false);
        firstRow.setSizeFull();
        secondRow.setSpacing(false);
        secondRow.setMargin(false);
        secondRow.addStyleName("t-label-item-description");
        secondRow.setWidthUndefined();
        controlBarContent.addComponents(firstRow, secondRow);
        controlBarContent.setComponentAlignment(secondRow, Alignment.BOTTOM_RIGHT);

        itemsDescription = new Label("");
        itemsDescription.addStyleName(ValoTheme.LABEL_TINY);
        VerticalLayout popContent = new VerticalLayout();
        int size = gridPanel.getItemConfiguration().getColumnIds().length;
        for (int i = 0; i < size; i++) {
            CheckBox c = new CheckBox(gridPanel.getItemConfiguration().getColumnHeaders()[i]);
            c.setData(gridPanel.getItemConfiguration().getColumnIds()[i]);
            c.addStyleName(ValoTheme.CHECKBOX_SMALL);
            c.setValue(true);
            popContent.addComponent(c);

            for (String cid : gridPanel.getItemConfiguration().getCollapsedColumnIds()) {
                if (cid.equals(gridPanel.getItemConfiguration().getColumnIds()[i])) {
                    c.setValue(false);
                }
            }
            c.addValueChangeListener(e -> {
                if (e.getOldValue() != e.getValue()) {
                    CheckBox comp = (CheckBox) e.getComponent();
                    if (e.getValue()) {
                        //add column
                        gridPanel.addColumn((String)comp.getData(), e.getComponent().getCaption());
                        fireColumnCollapsedEvent(comp.getData(), !e.getValue());
                    } else {
                        try {
                            gridPanel.getGrid().removeColumn((String)comp.getData());
                            fireColumnCollapsedEvent(comp.getData(), !e.getValue());
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            });
        }

        PopupView popUpView = new PopupView(null, popContent);

        Button collapseMenu = new Button("");
        collapseMenu.setIcon(VaadinIcons.LIST_UL);
        collapseMenu.addStyleName(ValoTheme.BUTTON_TINY);
        collapseMenu.addStyleName(ValoTheme.BUTTON_BORDERLESS_COLORED);
        collapseMenu.addStyleName(ValoTheme.BUTTON_ICON_ONLY);
        collapseMenu.addClickListener(e -> {
            popUpView.setPopupVisible(true);
        });
        secondRow.addComponents(itemsDescription, collapseMenu, popUpView);
        secondRow.setComponentAlignment(itemsDescription, Alignment.MIDDLE_RIGHT);
        secondRow.setComponentAlignment(collapseMenu, Alignment.BOTTOM_RIGHT);

        Button first = new Button();
        first.setDescription(gridPanel.getMessageProvider().getCommonLocaleMgs().getOrDefault(NotificationMsgProvider.KEY_FIRST_MSG, "First"));
        first.setIcon(VaadinIcons.BACKWARDS);
        first.addStyleName(ValoTheme.BUTTON_TINY);
        first.addStyleName(ValoTheme.BUTTON_LINK);
        first.addClickListener(e -> {
            if (currentPage > 1) {
                currentPage = 1;
                pageNumberField.setValue("1");
                firePageChangeEvent();
            }
        });
        firstRow.addComponent(first);

        Button backward = new Button();
        backward.setDescription(gridPanel.getMessageProvider().getCommonLocaleMgs().getOrDefault(NotificationMsgProvider.KEY_BACKWARD_MSG, "Previous"));
        backward.setIcon(VaadinIcons.ARROW_BACKWARD);
        backward.addStyleName(ValoTheme.BUTTON_TINY);
        backward.addStyleName(ValoTheme.BUTTON_LINK);
        backward.addClickListener(e -> {
            if (currentPage > 1) {
                currentPage--;
                pageNumberField.setValue(String.valueOf(currentPage));
                firePageChangeEvent();
            }
        });
        firstRow.addComponent(backward);
        //page number field
        pageNumberField = new TextField();
        pageNumberField.setValue(String.valueOf(currentPage));
        pageNumberField.setWidth("40px");
        pageNumberField.addStyleName(ValoTheme.TEXTFIELD_TINY);
        // pageNumberField.addStyleName(ValoTheme.TEXTFIELD_BORDERLESS);
        pageNumberField.addStyleName(ValoTheme.TEXTFIELD_ALIGN_CENTER);
        pageNumberField.addValueChangeListener(e -> {
            if (!Objects.equals(e.getOldValue(), e.getValue().trim())) {
                Notification notification = new Notification("Warning", gridPanel.getMessageProvider().getInvalidPageNumberMsg(), Notification.Type.TRAY_NOTIFICATION);
                notification.setDelayMsec(300);

                try {
                    int pageNumber = Integer.parseInt(e.getValue());
                    if (pageNumber <= 0) {
                        pageNumberField.setValue(e.getOldValue());
                        notification.show(Page.getCurrent());
                    } else if (pageNumber > totalNumberOfPages) {
                        pageNumberField.setValue(String.valueOf(totalNumberOfPages));
                        notification.show(Page.getCurrent());
                    }
                } catch (NumberFormatException ex) {
                    pageNumberField.setValue(e.getOldValue());
                    notification.show(Page.getCurrent());

                }
            }
        });
        pageNumberField.addShortcutListener(new ShortcutListener("pageControlTextField", ShortcutAction.KeyCode.ENTER, null) {
            @Override
            public void handleAction(Object sender, Object target) {
                if (target != null && target instanceof TextField) {
                    if (currentPage != Integer.parseInt(pageNumberField.getValue().trim())) {
                        currentPage = Integer.parseInt(pageNumberField.getValue().trim());
                        firePageChangeEvent();
                    }
                }
            }
        });

        firstRow.addComponent(pageNumberField);

        totalPagesButton = new Button("<bold>/&nbsp;" + totalNumberOfPages + "</bold>");
        totalPagesButton.setCaptionAsHtml(true);
        totalPagesButton.addStyleName(ValoTheme.BUTTON_TINY);
        totalPagesButton.addStyleName(ValoTheme.BUTTON_BORDERLESS);
        totalPagesButton.addClickListener(e -> goToLastPage());
        //pageControlBar.addComponent(totalPagesLabel);
        firstRow.addComponent(totalPagesButton);

        Button forward = new Button();
        forward.setDescription(gridPanel.getMessageProvider().getCommonLocaleMgs().getOrDefault(NotificationMsgProvider.KEY_FORWARD_MSG, "Forward"));
        forward.setIcon(VaadinIcons.ARROW_FORWARD);
        forward.addStyleName(ValoTheme.BUTTON_TINY);
        forward.addStyleName(ValoTheme.BUTTON_LINK);
        forward.addClickListener(e -> {
            if (!(currentPage >= totalNumberOfPages)) {
                currentPage++;
                pageNumberField.setValue(String.valueOf(currentPage));
                firePageChangeEvent();
            }
        });
        firstRow.addComponent(forward);

        Button last = new Button();
        last.setDescription(gridPanel.getMessageProvider().getCommonLocaleMgs().getOrDefault(NotificationMsgProvider.KEY_LAST_MSG, "Last"));
        last.setIcon(VaadinIcons.FORWARD);
        last.addStyleName(ValoTheme.BUTTON_TINY);
        last.addStyleName(ValoTheme.BUTTON_LINK);
        last.addClickListener(e -> goToLastPage());
        firstRow.addComponent(last);

        firstRow.setComponentAlignment(first, Alignment.BOTTOM_RIGHT);
        firstRow.setComponentAlignment(backward, Alignment.BOTTOM_RIGHT);
        firstRow.setComponentAlignment(pageNumberField, Alignment.BOTTOM_RIGHT);
        //pageControlBar.setComponentAlignment(totalPagesLabel, Alignment.BOTTOM_RIGHT);
        firstRow.setComponentAlignment(totalPagesButton, Alignment.BOTTOM_RIGHT);

        firstRow.setComponentAlignment(forward, Alignment.BOTTOM_RIGHT);
        firstRow.setComponentAlignment(last, Alignment.BOTTOM_RIGHT);

    }

    private void setTotalNumberOfPages(Integer totalPages) {
        this.totalNumberOfPages = totalPages;
        totalPagesButton.setCaption("<bold>/&nbsp;" + totalPages + "</bold>");
    }

    public void setTotalItemCount(Integer items) {
        if (items < 1) {
            items = 1;
        }
        if (this.totalItemsCount != items) {
            this.totalItemsCount = items;
        }
        int totalPage = items / getLimit();
        if (items % getLimit() > 0) {
            totalPage++;
        }
        setTotalNumberOfPages(totalPage);
        updateCurrentPage();
        updateItemsDescription();
    }

    public Integer getTotalItemsCount() {
        return this.totalItemsCount;
    }

    private void goToLastPage() {
        if (currentPage < totalNumberOfPages) {
            currentPage = totalNumberOfPages;
            pageNumberField.setValue(String.valueOf(currentPage));
            firePageChangeEvent();
        }
    }

    private void updateItemsDescription() {
        int from = 1;
        int to = 1;
        if (getPageNumber() > 1) {
            from += (getPageNumber() - 1) * getLimit();
            to = getLimit() * getPageNumber();
        } else {
            // from default 0
            to = getLimit();

        }
        if (getTotalItemsCount() <= to) {
            to = getTotalItemsCount();
        }
        itemsDescription.setValue(String.format("Showing Result %d-%d of %d Items", from, to, getTotalItemsCount()));

    }

    public void requestFirstPage() {
        // fire a event for first page 
        // for item count and 
    }

    private void updateCurrentPage() {
        if (currentPage > totalNumberOfPages) {
            currentPage = totalNumberOfPages;
        }
        pageNumberField.setValue(String.valueOf(currentPage));
    }

    public void resetControlPanel() {
        currentPage = 1;
        pageNumberField.setValue(String.valueOf(currentPage));
    }
    private void fireColumnCollapsedEvent(Object columnId, Boolean isCollapsed ){
        if( !collapseListener.isEmpty()){
            collapseListener.forEach(e->e.accept(columnId, isCollapsed));
        }
    }

    public void addColumnCollapseListener(BiConsumer<Object, Boolean> listener) {
        if(!collapseListener.contains(listener)){
            collapseListener.add(listener);
        }
    }

}
