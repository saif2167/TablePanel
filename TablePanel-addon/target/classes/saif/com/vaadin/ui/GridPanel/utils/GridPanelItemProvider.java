package saif.com.vaadin.ui.GridPanel.utils;

import java.util.List;
import java.util.Map;

/**
 *
 * @author Saiful Islam<saifislam2167@gmail.com>
 */
public interface GridPanelItemProvider<T> {

    public List<T> supply(int offset, int limit, Map<String, Object> filters, Option option);

    public static interface Option {

        /**
         * if total count is require User need to return the total number of
         * items
         *
         * @param isRequire
         * @return int
         */
        boolean isTotalCountRequire();
        void setTotalItemCount(int total);
    }
}
