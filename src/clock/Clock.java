package clock;

/**
 * The main of the program, the glue that holds everything together.
 * 
 * @author Vicki Maciver 14006476
 * @version 2016-12-03
 */

public class Clock {
    
    /**
     * Initialises MVC and iCalendar objects.
     * 
     * @param args
     */
    public static void main(String[] args) {
        Model model = new Model();
        View view = new View(model);
        model.addObserver(view);
        ICalendar ical = new ICalendar();
        Controller controller = new Controller(model, view, ical);
    }
}
