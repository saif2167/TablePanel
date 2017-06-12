package saif.com.vaadin.ui.GridPanel;

import saif.com.vaadin.ui.GridPanel.footer.TablePanelFooter;
import saif.com.vaadin.ui.GridPanel.header.TablePanelHeader;
import com.vaadin.ui.Component;
import com.vaadin.ui.Grid;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.components.grid.HeaderRow;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import saif.com.vaadin.ui.GridPanel.header.filter.utils.FilterComponentManager;
import saif.com.vaadin.ui.GridPanel.utils.GridPanelItemProvider;

/**
 *
 * @author Saiful Islam<saifislam2167@gmail.com>
 */
public class GridPanel<T> extends VerticalLayout {

    private Map<String, Object> filterMap = new HashMap<>();
    private Class<? extends T> beanType;
    private TablePanelHeader header;
    private TablePanelFooter footer;
    private Grid<T> grid;
    private HeaderRow headerRow;
    private BeanFieldConfiguration configuration;
    private GridPanelItemProvider<T> itemProvider;
    private NotificationMsgProvider msgProvider;
    private ExportDataProvider<T> exportDataProvider;
    private boolean isFilterVisible = true;
    private FilterComponentManager filterCompoenentManager;

    public GridPanel(Class<? extends T> beanType, BeanFieldConfiguration con) {
        this.beanType = beanType;
        this.configuration = con;
        this.filterCompoenentManager = new FilterComponentManager(this);
        setSizeFull();
        setWidth("100%");
        setSpacing(true);
        initUI();
    }

    public void setFilterVisible(boolean isVisible) {
        this.isFilterVisible = isVisible;
    }

    public void setExportDataProvider(ExportDataProvider edp) {
        this.exportDataProvider = edp;
    }

    public ExportDataProvider getExportDataProvider() {
        return this.exportDataProvider;
    }

    public NotificationMsgProvider getMessageProvider() {
        if (this.msgProvider == null) {
            setMessageProvider(new DefaultNotificationMsgProvider());
        }
        return this.msgProvider;
    }

    public void setMessageProvider(NotificationMsgProvider mpro) {
        this.msgProvider = mpro;
    }

    public BeanFieldConfiguration getItemConfiguration() {
        return configuration;
    }

    public GridPanel(Class<? extends T> beanType) {
        this(beanType, null);
    }

    private void initUI() {
        createHeader();
        createGrid();
        createFooter();
        addComponent(getHeader());
        addComponentsAndExpand(getGrid());
        addComponent(getFooter());
    }

    private void createHeader() {
        header = new TablePanelHeader(this);
        header.addLimitChangeListener(e -> {
            Integer limit = e.getLimit();
            System.out.println("LIMIT : " + limit);
            updateGridData(((header.getPageNumber() - 1) * e.getLimit()) + 1, e.getLimit(), false);
        });
        header.addPageChangeListener(e -> {
            Integer pageNumber = e.getPageNumber();
            System.out.println("Page changed : " + pageNumber);
            updateGridData(((e.getPageNumber() - 1) * header.getLimit()) + 1, header.getLimit(), false);
        });
    }

    private void createGrid() {
        grid = new Grid(this.beanType);
        grid.setColumnReorderingAllowed(true);
        grid.addStyleName("t-panel-grid");
        grid.setWidth("100%");
        if (configuration != null) {
            headerRow = grid.appendHeaderRow();
            if (!isFilterVisible) {

            }
            grid.removeAllColumns();
            List<String> collapsIds = Arrays.asList(configuration.getCollapsedColumnIds());
            for (int x = 0; x < configuration.getColumnIds().length; x++) {

                try {
                    if (!collapsIds.contains(configuration.getColumnIds()[x])) {
                        Field field = beanType.getDeclaredField(configuration.getColumnIds()[x]);
                        Grid.Column col = grid.addColumn(configuration.getColumnIds()[x]);
                        col.setCaption(configuration.getColumnHeaders()[x]);
                        addFilterColumn(col, field.getType());

                    }
                } catch (NoSuchFieldException ex) {
                    ex.printStackTrace();
                } catch (SecurityException ex) {
                    ex.printStackTrace();
                }

            }
        }

    }

    private void addFilterColumn(Grid.Column col, Class<?> type) {
        headerRow.getCell(col).setComponent(getFilterComponentByType(col, type));
    }

    private Component getFilterComponentByType(Grid.Column col, Class<?> type) {
        return filterCompoenentManager.createFilterComponent(col, type);
    }

    private void createFooter() {
        footer = new TablePanelFooter(this);
    }

    public TablePanelHeader getHeader() {
        return header;
    }

    public TablePanelFooter getFooter() {
        return footer;
    }

    public Grid<T> getGrid() {
        return grid;
    }

    public void addItems(Collection<T> items) {
        grid.setItems(items);
        //getHeader().setTotalItemCount(items.size());
    }

    public void fireFilterEvent() {
        System.out.println("Filter Event fired" + filterMap);
        updateGridData(((header.getPageNumber() - 1) * header.getLimit()) + 1, header.getLimit(), true);
    }

    public void refreshContent() {
        filterMap.clear();
        updateGridData(((header.getPageNumber() - 1) * header.getLimit()) + 1, header.getLimit(), true);
        filterCompoenentManager.resetFilterValues();
        System.out.println("Refresh..");
    }

    public void export(TablePanelFooter.EXPORT_FORMAT format,boolean header, String fileName) {
        System.out.println("Export format : " + format+" File name : "+fileName);
    }

    public Map<String, Object> getFilterMap() {
        return filterMap;
    }

    public void pack() {
        updateGridData(0, header.getLimit(), true);
    }

    public GridPanelItemProvider<T> getItemProvider() {
        return itemProvider;
    }

    public void setItemProvider(GridPanelItemProvider<T> itemProvider) {
        this.itemProvider = itemProvider;
    }

    private void updateGridData(int offset, int limit, boolean isTotalRequire) {
        if (getItemProvider() != null) {
            getItemProvider().supply(offset, limit, filterMap, new GridPanelItemProvider.Option() {
                @Override
                public boolean isTotalCountRequire() {
                    return isTotalRequire;
                }

                @Override
                public void setTotalItemCount(int total) {
                    if (isTotalRequire) {
                        header.setTotalItemCount(total);
                    }
                }
            });
        }
    }

    public void addColumn(String data, String caption) {

        Field field = null;
        try {
            field = beanType.getDeclaredField(data);
            Grid.Column col = grid.addColumn(data);
            col.setCaption(caption);
            addFilterColumn(col, field.getType());
        } catch (NoSuchFieldException ex) {
            Logger.getLogger(GridPanel.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SecurityException ex) {
            Logger.getLogger(GridPanel.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    public void addColumnCollapsedListener(BiConsumer<Object,Boolean> listener){
        header.addColumnCollapseListener(listener);
    }
}
