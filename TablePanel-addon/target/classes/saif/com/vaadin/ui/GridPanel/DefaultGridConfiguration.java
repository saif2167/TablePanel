package saif.com.vaadin.ui.GridPanel;

/**
 *
 * @author Saiful Islam<saifislam2167@gmail.com>
 */
public class DefaultGridConfiguration implements BeanFieldConfiguration {

    private final String[] columnIds;
    private final String[] headersNames;
    private final String[] collapsedColumnIds;

    public DefaultGridConfiguration() {
        this.columnIds = new String[]{"firstName","lastName","age","man","type","birthDate","allowed","price"};
        this.headersNames = new String[]{"Fi Name","La Name","Age","Man","Gender Type","Birth Date","Allowed","Price"};
        this.collapsedColumnIds = new String[]{"man"};
    }
    

    @Override
    public String[] getColumnIds() {
        return this.columnIds;
    }

    @Override
    public String[] getColumnHeaders() {
       return this.headersNames;
    }

    @Override
    public String[] getCollapsedColumnIds() {
        return collapsedColumnIds;
    }

}
