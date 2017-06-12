

package saif.com.vaadin.ui.GridPanel;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Saiful Islam<saifislam2167@gmail.com>
 */
public class DefaultNotificationMsgProvider implements NotificationMsgProvider{

    @Override
    public String getInvalidPageNumberMsg() {
        return "Invalid Page Number!";
    }

    @Override
    public String getInvalidNumberFormatMsg() {
        return "Invalid number!";
    }

    @Override
    public String getMaxPageReachedMsg() {
        return "Max Page reached!";
    }

    @Override
    public String getInvalidDateFormatMsg() {
        return "Invalid Date Format!";
    }

    @Override
    public Map<String, String> getCommonLocaleMgs() {
        Map<String,String> map = new HashMap<>();
        map.put(KEY_EXPORT_MSG,"Export");
        map.put(KEY_REFRESH_MSG,"Refresh");
        map.put(GRID_EXPORT_HEADER,"Header");
        return map;
    }

}
