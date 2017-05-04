package clock;

import java.util.Calendar;
import java.util.Observable;
import java.util.logging.Level;
import java.util.logging.Logger;
import queuemanager.QueueOverflowException;
import queuemanager.QueueUnderflowException;

/**
 * Controls and manipulates the data that the program uses. Initialises and updates
 * the queue, as well as converting strings and numbers into a proper format for
 * the queue to handle.
 * 
 * @author Vicki Maciver 14006476
 * @version 2016-12-03
 */
public class Model extends Observable {
    
    LoopQueue<Alarm> alarms = new LoopQueue<Alarm>(10);
    
    int hour = 0;
    int minute = 0;
    int second = 0;
    
    int oldSecond = 0;
    int oldMinute = 0;
    
    /**
     * Constructor. Updates the time variables to the current clock time before
     * the program can begin to use them.
     */
    public Model() {
        update();
    }
    
    /**
     * Sets all of the time variables (hour, minute, second) to the current
     * time in the clock. There are also oldMinute and oldSecond, which detect
     * when there has been a change. oldSecond notifies the observer in the view
     * to repaint the clock every time a second passes. oldMinute updates the 
     * priorities of the alarms in the queue every time a minute passes.
     */
    public void update() {
        Calendar date = Calendar.getInstance();
        hour = date.get(Calendar.HOUR_OF_DAY);
        oldMinute = minute;
        minute = date.get(Calendar.MINUTE);
        oldSecond = second;
        second = date.get(Calendar.SECOND);
        if (oldSecond != second) {
            setChanged();
            notifyObservers();
        }
        
        if (oldMinute != minute)
        {
            if (!alarms.isEmpty())
            {
                try {
                    updateAlarms();
                } catch (QueueUnderflowException ex) {
                    Logger.getLogger(Model.class.getName()).log(Level.SEVERE, null, ex);
                } catch (QueueOverflowException ex) {
                    Logger.getLogger(Model.class.getName()).log(Level.SEVERE, null, ex);
                }
             }
        }
    }

    /**
     * Creates an alarm of a time string and then passes it to the queue.
     * 
     * @param alarmTime     String of an alarms time.
     * @param priority      The priority of an alarm (based on time until it rings)
     * 
     * @throws QueueOverflowException   If it tries to add to a full queue
     */
    public void addToQueue(String alarmTime, int priority) throws QueueOverflowException
    {
        Alarm newAlarm = new Alarm(alarmTime);
        alarms.add(newAlarm, priority);
    }
    
    /**
     * Deletes an alarm at the given position and then adds the new alarm. The
     * queue will automatically sort the new one into the correct position when it
     * adds it.
     * 
     * @param pos           The position of the alarm to be deleted.
     * @param alarmTime     The alarm time string of the new alarm.
     * @param priority      The priority of the new alarm.
     * 
     * @throws QueueOverflowException   If the delete doesn't work and the queue 
     *                                  is full.
     * @throws QueueUnderflowException  If it tries to edit and delete an empty
     *                                  queue
     */
    public void editAlarm(int pos, String alarmTime, int priority) throws QueueOverflowException, QueueUnderflowException
    {
        alarms.deleteAlarm(pos);
        Alarm newAlarm = new Alarm(alarmTime);
        alarms.add(newAlarm, priority);
    }
    
    /**
     * Deletes the alarm at the given position.
     * 
     * @param pos       Position of the alarm to be deleted
     * 
     * @throws QueueUnderflowException  If it tries to delete from an empty queue
     */
    public void deleteAlarm(int pos) throws QueueUnderflowException
    {
        alarms.deleteAlarm(pos);
    }

    /**
     * Puts all of the alarm strings into an array, then loops through the array
     * to assign new priorities to each one based on their string. This is more 
     * fail-safe than just taking one away, because if the method is called when 
     * a minute hasn't passed then it could ring at an unintended time.
     * 
     * @throws QueueUnderflowException  If the queue is empty.
     * @throws QueueOverflowException   If something goes wrong and it adds to a 
     *                                  full queue.
     */
    public void updateAlarms() throws QueueUnderflowException, QueueOverflowException
    {
        int[] newAlarmPrios = new int[alarms.getTailIndex()+1];
        String[] alarmArray = alarms.getAlarmArray();
        
        for (int i = 0; i <= alarms.getTailIndex(); i++)
        {
            int[] hourMin = stringToTime(alarmArray[i]);
            
            newAlarmPrios[i] = findPriority(hourMin[0], hourMin[1]);
            
            alarms.updateAlarmPrio(newAlarmPrios);
        }
    }
        
    /**
     * Converts an alarm time string into an hour and a minute value (stored in
     * an array so it can return both of the values together). Makes sure that
     * that string is in the right format (4 characters) before it begins taking
     * substrings to avoid any unnecessary errors.
     * 
     * @param alarmTime     The string of the alarms time.
     * 
     * @return hourMin[]    The integer array that contains the hour and minute
     *                      values. [0] is the hour and [1] is the minute.
     */
    public int[] stringToTime(String alarmTime)
    {
        
        int hourValue;
        int minValue;
        
       if(alarmTime.length() == 3)
        {
            alarmTime = "0" + alarmTime;
        }
        if (alarmTime.length() == 1)
        {
            alarmTime = "000" + alarmTime;
        }
        
        String alarmHour = alarmTime.substring(0, 2);
        String alarmMinute = alarmTime.substring(2);
        
        hourValue = Integer.parseInt(alarmHour);
        minValue = Integer.parseInt(alarmMinute);
        
        int[] hourMin = {hourValue, minValue};
        return hourMin;
    }
    
    /**
     * Converts an hour value and a minute value into a 4-character string. 
     * 
     * @param hourValue     The hour value
     * @param minValue      The minute value
     * 
     * @return              The 4-character string of the hour value + the minute
     *                      value, adding 0s if either are below 10 in order to 
     *                      maintain 4-character format.
     */
    public String timeToString(int hourValue, int minValue)
    {
         String hourMinString = null;
                           
                   if (hourValue == 0)
                   {
                       hourMinString = "00";
                   }
                   else if(hourValue < 10)
                   {
                       hourMinString = "0" + hourValue;
                   }
                   else
                   {
                       hourMinString = "" + hourValue;
                   }
                   if (minValue == 0)
                   {
                       hourMinString = hourMinString = hourMinString + "00";
                   }
                   else if (minValue < 10)
                   {
                       hourMinString = hourMinString + "0" + minValue;
                   }
                   else
                   {
                       hourMinString = hourMinString + minValue;
                   }
                   
        return hourMinString;
    }
    
    /**
     * Finds out the time until the alarm rings. Subtracts the current time from
     * the priority values in order to find how long to go until it rings. It fixes
     * it to make sure that any minutes over 60 become an hour, and any negative values
     * will become the next day.
     * 
     * @param prioHour      The hour value of the alarm.
     * @param prioMinute    The minute value of the alarm.
     * 
     * @return  tempPrio    The priority (time until the alarm rings / reaches 0)
     */
        public int findPriority(int prioHour, int prioMinute)
    {
        prioHour = prioHour - hour;
        prioMinute = prioMinute - minute;
        
        int tempPrio;
        
        // if 60 minutes, increase hour by 1
        if (prioMinute >= 60)
        {
            prioMinute = prioMinute - 60;
            prioHour = prioHour + 1;
        }
        
        // if negative minutes, decrease hour and fix minutes
        if (prioMinute < 0)
        {
            prioHour = prioHour - 1;
            prioMinute = prioMinute + 60;
        }
        
        // if alarm is before current time, make it tomorrow
        if (prioHour < 0)
        {
            prioHour = prioHour + 24;
        }
        
        String tempPrioString;

            tempPrioString = "" + prioHour;
        
        // if prioMinute is under 10, turn it from e.g. ("5" -> "05").
        if (prioMinute < 10)
        {
            tempPrioString = tempPrioString + "0" + prioMinute;
        }
        else
        {
            tempPrioString = tempPrioString + "" + prioMinute;
        }
        
        tempPrio = Integer.parseInt(tempPrioString);
        
        
        return tempPrio;
    }
        
    

}