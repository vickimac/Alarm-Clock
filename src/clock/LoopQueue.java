package clock;

import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import queuemanager.PriorityItem;
import queuemanager.QueueOverflowException;
import queuemanager.QueueUnderflowException;
import queuemanager.SortedArrayPriorityQueue;

/**
 * A subclass of SortedArrayPriorityQueue. Allows retrieving of queue items in
 * a loop, which is required of many features in the program. Also allows the 
 * Controller and Model to update the queue in interesting ways (for example,
 * updating all of the priorities every minute).
 * 
 * @author Vicki Maciver 14006476
 * @version 2016-12-03
 */
public class LoopQueue<T> extends SortedArrayPriorityQueue<T>{
    
    /**
     * The constructor. Very little is needed here as most of the values that are
     * needed are already stored in the superclass.
     * 
     * @param size  Copies the size of the superclass.
     */
    public LoopQueue(int size) {
        super(size);
    }
    
    /**
     * Overrides the add method of its superclass, as a min priority queue is
     * needed for this program - but the superclass is a max priority queue.
     * The only change is that the comparator symbol in the while loop is
     * switches from a less than to a greater than.
     * 
     * @param item      The alarm item.
     * @param priority  The priority of the alarm.
     * 
     * @throws QueueOverflowException   If an item tries to be added when the 
     *                                  queue is full.
     */
    @Override
    public void add(T item, int priority) throws QueueOverflowException {
        tailIndex = tailIndex + 1;
        if (tailIndex >= capacity) {
            /* No resizing implemented, but that would be a good enhancement. */
            tailIndex = tailIndex - 1;
            throw new QueueOverflowException();
        } else {
            /* Scan backwards looking for insertion point */
            int i = tailIndex;
            while (i > 0 && ((PriorityItem<T>) storage[i - 1]).getPriority() > priority) {
                storage[i] = storage[i - 1];
                i = i - 1;
            }
            storage[i] = new PriorityItem<>(item, priority);
        }
    }
    
    /**
     * Deletes the alarm at the given position. It does this by using a loop
     * that shifts all the items in the array down one place and subtracting
     * 1 from the tailIndex to make it 1 item shorter.
     * 
     * @param pos   The position of the item to be deleted.
     * 
     * @throws QueueUnderflowException  If it tries to delete an item when the
     *                                  queue is empty.
     */
    public void deleteAlarm(int pos) throws QueueUnderflowException
    {
        if (isEmpty())
        {
            throw new QueueUnderflowException();
        }
        else
        {
            
            for (int i = pos; i < tailIndex; i++)
            {  
                storage[i] = storage[i+1];
            }
            tailIndex = tailIndex - 1;
        }
    }
   
    /**
     * Loops through the entire queue and replaces it with the item in the array
     * of the index equal to the same index in the queue. This is used to count down
     * how many minutes until the queue rings.
     * 
     * @param prios     Array of new priorities, calculated by the model.
     * 
     * @throws QueueUnderflowException  If it tries to update priorities of an empty
     *                                  queue.
     */
    public void updateAlarmPrio(int[] prios) throws QueueUnderflowException
    {
        for (int i = 0; i <= tailIndex; i++)
        {
            String alarmTime = ""+((PriorityItem<Alarm>) storage[i]).getItem();
            storage[i] = new PriorityItem(alarmTime, prios[i]);
        }
    }
    
    /**
     * Returns all of the times of the alarms in the queue in an array.
     * 
     * @return  alarmArray  A string array of all the alarm times.
     * 
     * @throws  QueueUnderflowException  If it tries to get an array of an empty
     *                                  queue.
     */
    public String[] getAlarmArray() throws QueueUnderflowException
    {
        String[] alarmArray = new String[tailIndex + 1];
       
        for(int i = 0; i <= tailIndex; i++)
        {
            alarmArray[i] = ""+((PriorityItem<T>) storage[i]).getItem();
        }
        
        return alarmArray;
    }
    
    
    /**
     * Returns an array of all of the priorities of the alarms in the queue in
     * an array.
     * 
     * @return  alarmPrios   An integer array of all the alarm priorities.
     */
    public int[] getAlarmPrios()
    {
        int[] alarmPrios = null;
        
        for (int i=0; i<= tailIndex; i++)
        {
            alarmPrios[0] = ((PriorityItem<Alarm>) storage[i]).getPriority();
        }
        return alarmPrios;
    }
    
    /**
     * Creates and returns an array of panels for each item in the queue. Each alarm
     * has its time displayed in a label in a nice 24-hour format. It uses 
     * the edit button listener that was created by the controller and assigns
     * it to each edit button.
     * 
     * @param   listener    The edit button listener created by the Controller that
     *                      allows the user to edit the values of the alarm.
     * @return  allAlarms   An array of JPanels - one for each alarm.
     * 
     * @throws QueueUnderflowException  If the queue is empty.
     */
    public JLabel[] getAlarmPanel(ActionListener listener) throws QueueUnderflowException
    {
        JLabel[] allAlarms = new JLabel[tailIndex+1];

        for (int i = 0; i <= tailIndex; i++)
        {
            JPanel alarmPanel = new JPanel();
            String alarmTime = "" + ((PriorityItem<Alarm>) storage[i]).getItem();
            
            if (alarmTime.length() == 3)
            {
                alarmTime = "0" + alarmTime;
            }
            else if (alarmTime.length() == 1)
            {
                alarmTime = "000" + alarmTime;
            }
            
            String alarmHour = alarmTime.substring(0, 2);
            String alarmMinute = alarmTime.substring(2);
            
            JLabel alarmLabel = new JLabel(alarmHour + ":" + alarmMinute);
            JButton alarmButton = new JButton("EDIT");

            alarmButton.addActionListener(listener);

            alarmPanel.add(alarmLabel, BorderLayout.BEFORE_LINE_BEGINS);
            alarmPanel.add(alarmButton, BorderLayout.CENTER);
            allAlarms[i] = alarmLabel;

        }
        
        return allAlarms;
    }
    
    /**
     * Creates a label of the head of the queue to be displayed on the GUI.
     * 
     * @return  nextAlarm   Label of the next alarm (head of the queue).
     */
    public JLabel getNextAlarm()
    {
        String hourmin = ""+((PriorityItem<T>) storage[0]).getItem();
        if (hourmin.length() == 3)
        {
            hourmin = "0" + hourmin;
        }
        else if (hourmin.length() == 1)
        {
            hourmin = "000" + hourmin;
        }
        
        String hourValue = hourmin.substring(0, 2);
            String minValue = hourmin.substring(2);

            JLabel nextAlarm = new JLabel("Next Alarm: " + hourValue + ":" + minValue);
            return nextAlarm;
    }
    
    /**
     * Allows other classes to get the tailIndex of the super class. This is useful
     * for looping and finding the actual size of the queue.
     * 
     * @return  tailIndex
     */
    public int getTailIndex()
    {
        return tailIndex;
    }

    /**
     * Checks to see if there are any alarms that are ringing. Loops through the
     * alarm to see if there are any alarms at or below 0 priority (which means
     * that there are 0 minutes until it rings - so it is currently ringing)
     * 
     * @return alarmRang    True if there are any alarms that are at priority 0
     * 
     * @throws QueueUnderflowException  If it checks while the queue is empty.
     */
    public boolean ringingAlarms() throws QueueUnderflowException
    {
        boolean alarmRang = false;
        
        for (int i=0; i<= tailIndex; i++)
        {
            if (((PriorityItem<T>) storage[i]).getPriority() <= 0)
            {
                remove();
                alarmRang = true;
            }
        }
        
        return alarmRang;
    }
}
