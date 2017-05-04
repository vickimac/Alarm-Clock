package clock;

import java.awt.BorderLayout;
import java.awt.event.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.Timer;
import queuemanager.QueueOverflowException;
import queuemanager.QueueUnderflowException;

/**
 * Controls the flow of data around the View, Model and ICalendar objects.
 * All the Dialogs and Action Listener code are in here, as well as other
 * various functionality methods and pieces of code.
 * 
 * @author Vicki Maciver 14006476
 * @version 2016-12-03
 */
public class Controller {
    
    ActionListener listener;
    ActionListener alarmListener;
    ActionListener addNewAlarmListener;
    ActionListener addEditAlarmListener;
    ActionListener addDeleteAlarmListener;
    Timer timer;
    Timer alarmTimer;
    
    JButton[] allButtons;
    JLabel[] allAlarms;
    JButton editButton;
    int allButtonsLength;
    
    JButton deleteButton;
    JButton[] allDeleteButtons;
    
    Model model;
    View view;
    ICalendar ical;
    
    /**
     * The constructor. 
     * Initialises and connects with the Model and View.
     * Also defines the Action Listeners and their Dialogs.
     * 
     * @param m The Model
     * @param v The View
     * @param ic The ICalendar (for saving / opening .ics files)
     */
    public Controller(Model m, View v, ICalendar ic) {
        model = m;
        view = v;
        ical = ic;
        
        listener = new ActionListener() {
           /**
            * Makes sure that all of the current time fields in the Model are
            * set to the current time.
            * 
            * @param e Timer tick (every 0.1 second)
            */
            public void actionPerformed(ActionEvent e) {
                model.update();
            }
        };
        
        timer = new Timer(100, listener);
        timer.start();
        
        alarmListener = new ActionListener()
        {
            /**
             * Checks to see if any of the alarms have a priority of 0 (in other
             * words, they are ringing). If they do, shows a dialog box to alert
             * the user and then updates the view so that it shows the updated
             * PriorityQueue.
             * 
             * @param e AlarmTimer Ticket (every 1 second)
             */
            public void actionPerformed(ActionEvent e)
            {
                try {
                        if (model.alarms.ringingAlarms())
                        {
                            JOptionPane.showMessageDialog(null, "Alarm Ringing", "RING RING RING!!!", JOptionPane.WARNING_MESSAGE);
                             view.alarmJustRang();
                            try {
                                alarmListUpdate();
                                nextAlarmLabel();
                            } catch (QueueOverflowException ex) {
                                Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                     
                      
                } catch (QueueUnderflowException ex) {
                    Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        };
        
        alarmTimer = new Timer(1000, alarmListener);
        alarmTimer.start();
        
        
        view.addAboutButtonListener(new ActionListener()
        {
            /**
             * A simple dialog box that informs the user of the program's creator.
             * 
             * @param e User mouse click on the "About" item of the "Help" menu 
             *          in the GUI.
             */
            public void actionPerformed(ActionEvent e)
            {
                JOptionPane.showMessageDialog(null, "By Vicki", "About", JOptionPane.PLAIN_MESSAGE);
            }
        });
        
        
        view.addExitListener(new ActionListener()
        {
            /**
             * Closes the program when clicked.
             * 
             * @param e User mouse click on the "Exit" item of the "Clock" menu
             *          in the GUI.
             */
            public void actionPerformed(ActionEvent e)
            {
                System.exit(0);
            }
        });
        
         
        addNewAlarmListener = new ActionListener()
        {
           /**
            * An input dialog where the user can input their desired alarm time
            * and it will add it to the priority queue for them. A JOptionPane
            * dialog box will pop up with two JSpinners, one for the alarms hour
            * and one for the alarms minute. The user will only be able to enter
            * valid inputs (0-23 for hours and 0-59 for minutes). When they click
            * OK the Controller sends the values to the model, which will then
            * format it correctly, calculate its priority based on the current
            * time, and then place it in the Priority Queue. It then gets the 
            * View to update its list of alarms and "Next Alarm" label in order
            * to show this addition.
            * 
            * Pressing the Cancel button just closes the dialog box.
            * 
            * @param e  User mouse click on either the "New Alarm" button in the 
            *           bottom right of the GUI, or on the "New Alarm" item of 
            *           the "Clock" menu.
            */
           public void actionPerformed(ActionEvent e)
           {
               JPanel alarmInput = new JPanel(new BorderLayout());
               JPanel hourInput = new JPanel(new BorderLayout());
               JPanel minInput = new JPanel(new BorderLayout());
               
               JLabel hourLabel = new JLabel("Hour:");
               SpinnerNumberModel hourSModel = new SpinnerNumberModel(0, 0, 23, 1);
               JSpinner hourSpinner = new JSpinner(hourSModel);
               
               JLabel minLabel = new JLabel("Minute:");
               SpinnerNumberModel minSModel = new SpinnerNumberModel(0, 0, 59, 1);
               JSpinner minSpinner = new JSpinner(minSModel);
               
               hourInput.add(hourLabel, BorderLayout.BEFORE_LINE_BEGINS);
               hourInput.add(hourSpinner, BorderLayout.CENTER);
               minInput.add(minLabel, BorderLayout.BEFORE_LINE_BEGINS);
               minInput.add(minSpinner, BorderLayout.CENTER);
               
               JLabel alarmLabel = new JLabel("Please enter the values for your new alarm");
               
               alarmInput.add(alarmLabel, BorderLayout.PAGE_START);
               alarmInput.add(hourInput, BorderLayout.CENTER);
               alarmInput.add(minInput, BorderLayout.PAGE_END);
               
               int inputOptions = JOptionPane.showOptionDialog(null, alarmInput, "New Alarm", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);
               
               if (inputOptions == JOptionPane.CANCEL_OPTION)
               {
                   
               }
               else if (inputOptions == JOptionPane.OK_OPTION)
               {
                   // Create string of the 2 ints
                   int hourValue = (int) hourSpinner.getValue();
                   int minValue = (int) minSpinner.getValue();
                                           
                   try {
                       model.addToQueue(model.timeToString(hourValue, minValue), model.findPriority(hourValue, minValue));
                       alarmListUpdate(); 
                   } catch (QueueOverflowException ex) {
                       Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
                   } catch (QueueUnderflowException ex) {
                       Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
                   }
                   
               }
           }
        };
            view.addNewAlarmButtonListener(addNewAlarmListener);
            
        view.addSaveListener(new ActionListener()
        {
            /**
             * Opens a file chooser for saving and passes the path that the user
             * decides on as well as an array of all of the alarms currently in 
             * the queue from the model to the ICalendar class.
             * 
             * No information is stored in the user clicks Cancel, the dialog is
             * just closed.
             * 
             * @param e User mouse click on the "Save Alarms" item of the "Clock"
             *          menu in the GUI.
             */
            public void actionPerformed(ActionEvent e)
            {
                JFileChooser save = new JFileChooser();

                int rVal = save.showSaveDialog(null);
                if (rVal == JFileChooser.APPROVE_OPTION)
                {
                    try {
                        ical.saveAlarms(save.getSelectedFile().getAbsolutePath(), model.alarms.getAlarmArray());
                    } catch (QueueUnderflowException ex) {
                        Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (FileNotFoundException ex) {
                        Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (IOException ex) {
                        Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
           
            }
        });
        
        view.addOpenListener(new ActionListener()
        {
            /**
             * Similar to the save feature, but finds the path of a file to open
             * rather than the path to save to. It passes the file into the 
             * ICalendar class, which returns an array of the alarm strings in
             * the file. It then passes it to the model to add it to the queue - 
             * getting integer values from the substrings of each of the alarm 
             * array strings in order to find the priority.
             * 
             * @param e User mouse click on the "Open" item of the "Clocks" menu
             *          in the GUI.
             */
           public void actionPerformed(ActionEvent e)
           {
               JFileChooser open = new JFileChooser();
               
               int rVal = open.showOpenDialog(null);
               
               if(rVal == JFileChooser.APPROVE_OPTION)
               {
                   try {
                       String openAlarms[] = ical.openAlarms(open.getSelectedFile().getAbsolutePath());
                       for(int i = 0; i < openAlarms.length; i++)
                       {
                           int hourmin[] = model.stringToTime(openAlarms[i]);
                           model.addToQueue(openAlarms[i], model.findPriority(hourmin[0], hourmin[1]));
                           alarmListUpdate();
                       }
                   } catch (IOException ex) {
                       Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
                   } catch (QueueOverflowException ex) {
                       Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
                   } catch (QueueUnderflowException ex) {
                       Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
                   }
                   
               }
           }
        });   
    }

    /**
     * Assigns every item in the priority queue its own panel, with its own "EDIT"
     * and "Delete" buttons and label. Retrieves a JLabel array of all of the alarms
     * in the queue, and then creates two JButton arrays (one for the Edit buttons and one
     * for the Delete buttons) based off the length of that returned array. It then
     * loops for the length in order to assign all of the buttons to the panels, as
     * well as assign an individual ID to each alarm. The ID is based on the place
     * in the queue (but it is calculated from the index of the loop).
     * 
     * @throws QueueUnderflowException  If it tries to edit or delete an alarm when 
     *                                  the queue is empty.
     * @throws QueueOverflowException   Technically it shouldn't happen, but as 
     *                                  the edit requires an alarm to be deleted and
     *                                  then re-added, it should be prepared to
     *                                  through this exception in case anything 
     *                                  goes wrong.
     */
    public void alarmListUpdate() throws QueueUnderflowException, QueueOverflowException
    {
        view.refreshAlarmList();
        
        allAlarms = model.alarms.getAlarmPanel(addEditAlarmListener);
        
        allButtons = new JButton[allAlarms.length];
        allDeleteButtons = new JButton[allAlarms.length];
        
        for (int i = 0; i < allAlarms.length; i++)
        {
            JPanel alarmPanel = new JPanel();
            editButton = new JButton("EDIT");
            editButton.putClientProperty("id", ""+i);
            deleteButton = new JButton("DELETE");
            allButtons[i] = editButton;
            allDeleteButtons[i] = deleteButton;
            alarmPanel.add(allAlarms[i], BorderLayout.BEFORE_LINE_BEGINS);
            alarmPanel.add(allButtons[i], BorderLayout.CENTER);
            alarmPanel.add(allDeleteButtons[i], BorderLayout.LINE_END);
            
            view.addToAlarmList(alarmPanel);
        }
        
            addEditAlarmListener = new ActionListener()
            {
                /**
                 * Shows the same input dialog with spinners as the edit alarm.
                 * Entering values and clicking on "OK" will delete the old alarm
                 * and replace it with a new one that uses the newly entered values
                 * instead. It finds the position of the alarm that needs to be 
                 * replaced by comparing all of the IDs in the array of edit buttons
                 * to the button that was clicked.
                 * 
                 * @param e User mouse click on the "EDIT" button of an alarm
                 *          panel.
                 */
                public void actionPerformed(ActionEvent e) {
                        JPanel alarmInput = new JPanel(new BorderLayout());
                        JPanel hourInput = new JPanel(new BorderLayout());
                        JPanel minInput = new JPanel(new BorderLayout());

                        JLabel hourLabel = new JLabel("Hour:");
                        SpinnerNumberModel hourSModel = new SpinnerNumberModel(0, 0, 23, 1);
                        JSpinner hourSpinner = new JSpinner(hourSModel);

                        JLabel minLabel = new JLabel("Minute:");
                        SpinnerNumberModel minSModel = new SpinnerNumberModel(0, 0, 59, 1);
                        JSpinner minSpinner = new JSpinner(minSModel);

                        hourInput.add(hourLabel, BorderLayout.BEFORE_LINE_BEGINS);
                        hourInput.add(hourSpinner, BorderLayout.CENTER);
                        minInput.add(minLabel, BorderLayout.BEFORE_LINE_BEGINS);
                        minInput.add(minSpinner, BorderLayout.CENTER);

                        JLabel alarmLabel = new JLabel("Please enter the new values for this alarm");

                        alarmInput.add(alarmLabel, BorderLayout.PAGE_START);
                        alarmInput.add(hourInput, BorderLayout.CENTER);
                        alarmInput.add(minInput, BorderLayout.PAGE_END);

                        int inputOptions = JOptionPane.showOptionDialog(null, alarmInput, "Edit Alarm", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);

                        if (inputOptions == JOptionPane.CANCEL_OPTION)
                        {

                        }
                        else if (inputOptions == JOptionPane.OK_OPTION)
                        {
                            int hourValue = (int) hourSpinner.getValue();
                            int minValue = (int) minSpinner.getValue();

                            for(int i = 0; i < allButtonsLength; i++)
                            {
                                 String labelText = allAlarms[i].getText().substring(0,2) + "" + allAlarms[i].getText().substring(3);   

                                if (e.getSource() == allButtons[i])
                                   {
                                        try {
                                            editAlarm(i, model.timeToString(hourValue, minValue), model.findPriority(hourValue, minValue));
                                            alarmListUpdate();
                                        } catch (QueueOverflowException ex) {
                                            Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
                                        } catch (QueueUnderflowException ex) {
                                         Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
                                     }
                                   }

                            }
                    }
            };
            };
        
            addDeleteAlarmListener = new ActionListener()
            {
                /**
                 * Opens a dialog window to make sure that the user intended to
                 * do this action. This prevents the annoying situation of 
                 * accidentally mis-clicking somewhere on the screen and losing
                 * an alarm without realising. If the user clicks yes, the alarm
                 * at that position (which is found by comparing all of the IDs
                 * of the button in the array to the button that was clicked on
                 * by the user) is deleted.
                 * 
                 * @param e User mouse click on the "Delete" button of an alarm
                 *          panel.
                 */
                public void actionPerformed(ActionEvent e) {

                         int inputOptions = JOptionPane.showOptionDialog(null, "Are you sure?", "Delete Alarm", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);

                        if (inputOptions == JOptionPane.NO_OPTION)
                        {
                        }
                        else if (inputOptions == JOptionPane.YES_OPTION)
                        {
                            for(int i = 0; i < allButtonsLength; i++)
                            {
                                if (e.getSource() == allDeleteButtons[i])
                                   {
                                        try {
                                            deleteAlarm(i);
                                            alarmListUpdate();
                                        } catch (QueueOverflowException ex) {
                                            Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
                                        } catch (QueueUnderflowException ex) {
                                         Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
                                     }
                                   }

                            }
                        }
                }
                }; 
            
        for (int i = 0; i < allAlarms.length; i++)
        {
            allButtonsLength = allButtons.length;
            allButtons[i].putClientProperty("id", ""+i);
            allButtons[i].addActionListener(addEditAlarmListener);
            allDeleteButtons[i].addActionListener(addDeleteAlarmListener);
        }

        nextAlarmLabel();
        view.updateAll();
    }
    
    /**
     * Passes the position of the alarm that needs to be deleted, as well as the
     * alarm time string and priority to replace it into the model.
     * 
     * @param pos           The position of the alarm to be deleted.
     * @param alarmTime     The new alarm time string that will replace the old one.
     * @param priority      The new priority, based on the time between the alarm
     *                      time and the current time.
     * 
     * @throws QueueUnderflowException  If it tries to edit or delete an alarm when 
     *                                  the queue is empty.
     * @throws QueueOverflowException   Technically it shouldn't happen, but as 
     *                                  the edit requires an alarm to be deleted and
     *                                  then re-added, it should be prepared to
     *                                  through this exception in case anything 
     *                                  goes wrong.
     */
    public void editAlarm(int pos, String alarmTime, int priority) throws QueueOverflowException, QueueUnderflowException
    {
        model.editAlarm(pos, alarmTime, priority);
    }
    
    /**
     * Passes the queue position of the alarm that needs to be deleted to the model.
     * 
     * @param pos   The position of the alarm to be deleted.
     * 
     * @throws QueueUnderflowException  If it tries to delete an alarm when the
     *                                  queue is empty.
     */
    public void deleteAlarm(int pos) throws QueueUnderflowException
    {
        model.deleteAlarm(pos);
    }
    
    /**
     * Displays the head of the queue in a label in the view. Refreshes every time
     * it is called so that it accurately shows if an alarm is added that becomes
     * the new head. If the queue is empty, just shows "Next Alarm:" with no alarm
     * label.
     * 
     * @throws QueueUnderflowException  If it tries to display a label when the queue
     *                                  is empty.
     */
    public void nextAlarmLabel() throws QueueUnderflowException
    {
        if (!model.alarms.isEmpty())
         {
            JLabel nextAlarm = model.alarms.getNextAlarm();
            view.nextAlarmPanel.removeAll();
            view.nextAlarmPanel.add(nextAlarm);
            view.nextAlarmPanel.validate();
            view.nextAlarmPanel.repaint();
          }
        else
        {
            JLabel noNextAlarm = new JLabel("Next Alarm: ");
            view.nextAlarmPanel.removeAll();
            view.nextAlarmPanel.add(noNextAlarm);
            view.nextAlarmPanel.validate();
            view.nextAlarmPanel.repaint();
        }
    }
    

 
}