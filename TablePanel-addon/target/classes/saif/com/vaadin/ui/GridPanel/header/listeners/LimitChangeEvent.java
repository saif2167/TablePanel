package saif.com.vaadin.ui.GridPanel.header.listeners;

/**
 *
 * @author Saiful Islam<saifislam2167@gmail.com>
 */
public class LimitChangeEvent {

    private Integer limit;

    public LimitChangeEvent(Integer limit) {
        this.limit = limit;
    }

    public Integer getLimit() {
        return limit;
    }

}
