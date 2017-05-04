package clock;

/**
 * Holds an individual alarms time in a String.
 * 
 * @author Vicki Maciver 14006476
 * @version 2016-12-03
 */
class Alarm {
    
    /**
     * 
     */
    protected String time;
    
    /**
     * Takes a string (which has hopefully been formatted in the Model) and sets 
     * it as its time.
     * 
     * @param time The time that will be assigned to this alarm
     */
    public Alarm(String time)
    {
        this.time = time;
    }
    
    /**
     * Returns the time of the individual alarm.
     * 
     * @return time
     */
    public String getTime()
    {
        return time;
    }
    
    /**
     * Repetitive code of the getTime() method, but used to Override 
     * PriorityItem toString and other methods.
     * 
     * @return getTime()
     */
    @Override
    public String toString()
    {
        return getTime();
    }
}
