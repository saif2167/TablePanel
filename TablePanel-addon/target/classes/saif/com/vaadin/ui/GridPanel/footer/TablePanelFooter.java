package saif.com.vaadin.ui.GridPanel.footer;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.themes.ValoTheme;
import saif.com.vaadin.ui.GridPanel.GridPanel;
import saif.com.vaadin.ui.GridPanel.NotificationMsgProvider;

/**
 *
 * @author Saiful Islam<saifislam2167@gmail.com>
 */
public class TablePanelFooter extends HorizontalLayout {

    public static enum EXPORT_FORMAT {
        
        PDF("pdf"), CSV("csv"), XLS("xls");
        private String ext;

        private EXPORT_FORMAT(String ext) {
            this.ext = ext;
        }
        
        public String getExtension(){
            return ext;
        }
    }
    private HorizontalLayout refreshLayout;
    private HorizontalLayout optionalLayout;
    private HorizontalLayout exportLayout;
    private GridPanel gridPanel;
    private boolean onRefresh = false;
    private CheckBox requireHeader;
    private TextField exportFileName;

    public TablePanelFooter(GridPanel gridPanel) {
        this.gridPanel = gridPanel;
        setWidth("100%");
        addStyleName("t-panel-footer");
        initUI();
    }

    private void initUI() {
        refreshLayout = new HorizontalLayout();
        optionalLayout = new HorizontalLayout();
        exportLayout = new HorizontalLayout();
        addComponent(refreshLayout);
        addComponentsAndExpand(optionalLayout);
        addComponent(exportLayout);

        Button refresh = new Button(gridPanel.getMessageProvider().getCommonLocaleMgs().getOrDefault(NotificationMsgProvider.KEY_REFRESH_MSG, "Refresh"));
        refresh.setDescription(gridPanel.getMessageProvider().getCommonLocaleMgs().getOrDefault(NotificationMsgProvider.KEY_REFRESH_MSG, "Refresh"));
        refresh.addClickListener(e -> {
            onRefresh = true;
            gridPanel.getHeader().resetControlPanel();
            gridPanel.refreshContent();
            onRefresh = false;
        });
        refreshLayout.addComponent(refresh);
        exportFileName = new TextField();
       // exportFileName.addStyleName(ValoTheme.TEXTFIELD_SMALL);
        requireHeader = new CheckBox(gridPanel.getMessageProvider().getCommonLocaleMgs().getOrDefault(NotificationMsgProvider.GRID_EXPORT_HEADER, "Header"));
        requireHeader.setValue(true);
        ComboBox<EXPORT_FORMAT> exportFormat = new ComboBox();
        exportFormat.setWidth("100px");
        exportFormat.setItems(EXPORT_FORMAT.values());
        exportLayout.addStyleName(ValoTheme.COMBOBOX_SMALL);
        exportFormat.setEmptySelectionAllowed(false);
        //exportFormat.setValue(EXPORT_FORMAT.CSV);
        exportFormat.setSelectedItem(EXPORT_FORMAT.CSV);

        Button exportBtn = new Button("");
        exportBtn.setDescription(gridPanel.getMessageProvider().getCommonLocaleMgs().getOrDefault(NotificationMsgProvider.KEY_EXPORT_MSG, "Export"));
        exportBtn.addStyleName(ValoTheme.BUTTON_ICON_ONLY);
        exportBtn.setIcon(VaadinIcons.DOWNLOAD);
        exportBtn.addClickListener(e -> {
            gridPanel.export(exportFormat.getValue(),requireHeader.getValue(),getExportFileName()+"."+exportFormat.getValue().getExtension());
        });
        exportLayout.addComponent(requireHeader);
        exportLayout.addComponent(exportFileName);
        exportLayout.addComponent(exportFormat);
        exportLayout.addComponent(exportBtn);
       // exportLayout.setComponentAlignment(requireHeader, Alignment.MIDDLE_LEFT);

    }

    public boolean isOnRefresh() {
        return onRefresh;
    }
    
    private String getExportFileName(){
        return (exportFileName.getValue() == null ||  exportFileName.getValue().trim().isEmpty())? "Export": exportFileName.getValue();
    }

}
