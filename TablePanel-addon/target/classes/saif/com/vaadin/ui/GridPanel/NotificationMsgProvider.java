

package saif.com.vaadin.ui.GridPanel;

import java.util.Map;

/**
 *
 * @author Saiful Islam<saifislam2167@gmail.com>
 */
public interface NotificationMsgProvider {
    public static final String KEY_EXPORT_MSG="ui.msg.export";
    public static final String KEY_REFRESH_MSG="ui.msg.refresh";
    public static final String KEY_FIRST_MSG="ui.msg.first";
    public static final String KEY_LAST_MSG="ui.msg.last";
    public static final String KEY_FORWARD_MSG="ui.msg.forward";
    public static final String KEY_BACKWARD_MSG="ui.msg.backward";
    public static final String GRID_EXPORT_HEADER="ui.msg.backward";
    String getInvalidPageNumberMsg();
    String getInvalidNumberFormatMsg();
    String getMaxPageReachedMsg();
    String getInvalidDateFormatMsg();
    Map<String,String> getCommonLocaleMgs();

}
