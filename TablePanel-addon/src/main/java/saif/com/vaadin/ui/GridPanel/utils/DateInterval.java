

package saif.com.vaadin.ui.GridPanel.utils;

import java.util.Date;

/**
 *
 * @author Saiful Islam<saifislam2167@gmail.com>
 */
public class DateInterval {
    private Date from;
    private Date to;

    public DateInterval(Date from, Date to) {
        this.from = from;
        this.to = to;
    }

    public Date getFrom() {
        return from;
    }

    public void setFrom(Date from) {
        this.from = from;
    }

    public Date getTo() {
        return to;
    }

    public void setTo(Date to) {
        this.to = to;
    }
    @Override
     public String toString(){
         return "From : "+from+" To: "+to;
     }
    
}
