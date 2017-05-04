package clock;

import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import javax.swing.*;
import java.util.Observer;
import java.util.Observable;

/**
 * The GUI that the user sees and interacts with.
 * 
 * @author Vicki Maciver 14006476
 * @version 2016-12-03
 */
public class View implements Observer {
    
    ClockPanel panel;
    JPanel alarmListPanel;
    
    JPanel bottomPanel;
    JPanel nextAlarmPanel;
    JLabel nextAlarmLabel = new JLabel("Next Alarm: ");
    
    JButton newAlarmButton;
    
    Container pane;
    
    JMenuBar menuBar;
    
    JMenu clockMenu;
    JMenu helpMenu;
    
    JMenuItem newAlarmClock;
    JMenuItem openAlarmClock;
    JMenuItem saveAlarmClock;
    JMenuItem exitClock;
    
    JMenuItem aboutHelp;
    
    JButton editAlarm;
    
    /**
     * Sets up the frame, panels, menus and buttons and places them in their
     * inteded layout position.
     * 
     * @param model Takes the values from the model in order to pass them to the
     *              ClockPanel - which draws the clock depending on the current
     *              time.
     */
    public View(Model model) {
        alarmListPanel = new JPanel();
        JFrame frame = new JFrame();
        panel = new ClockPanel(model);
        //frame.setContentPane(panel);
        frame.setTitle("Java Clock");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        pane = frame.getContentPane();
         
        panel.setPreferredSize(new Dimension(300, 300));
        pane.add(panel, BorderLayout.CENTER);
        
        alarmListPanel = new JPanel();
        alarmListPanel.setLayout(new BoxLayout(alarmListPanel, BoxLayout.Y_AXIS));
        JScrollPane alarmScroll = new JScrollPane(alarmListPanel);
        alarmScroll.setPreferredSize(new Dimension(200, 300));
        pane.add(alarmScroll, BorderLayout.LINE_END);
        
        bottomPanel = new JPanel();
            
        
            nextAlarmPanel = new JPanel();
            nextAlarmPanel.setPreferredSize(new Dimension(300, 30));
            nextAlarmPanel.add(nextAlarmLabel);
            
             newAlarmButton = new JButton("New Alarm");
            
            bottomPanel.add(nextAlarmPanel, BorderLayout.BEFORE_LINE_BEGINS);
            bottomPanel.add(newAlarmButton, BorderLayout.LINE_END);
        
        pane.add(bottomPanel, BorderLayout.PAGE_END);
        
            // Start of Menu Code
                menuBar = new JMenuBar();
                
                clockMenu = new JMenu("Clock");
                clockMenu.setMnemonic(KeyEvent.VK_C);
                
                helpMenu = new JMenu("Help");
                helpMenu.setMnemonic(KeyEvent.VK_H);
                
                // Start of Clock Menu Code
                    newAlarmClock = new JMenuItem("New Alarm");
                    clockMenu.add(newAlarmClock);
                    
                    openAlarmClock = new JMenuItem("Open Alarms");
                    clockMenu.add(openAlarmClock);
                    
                    saveAlarmClock = new JMenuItem("Save Alarms");
                    clockMenu.add(saveAlarmClock);
                    
                    clockMenu.addSeparator();
                    
                    exitClock = new JMenuItem("Exit");
                    clockMenu.add(exitClock);
                // End of Clock Menu Code
                
                // Start of Help Menu Code
                    aboutHelp = new JMenuItem("About");
                    helpMenu.add(aboutHelp);
                // End of Help Menu Code
                
                menuBar.add(clockMenu);
                menuBar.add(helpMenu);
                
                pane.add(menuBar, BorderLayout.PAGE_START);
                
            // End of Menu Code
            
        // End of borderlayout code
        
        frame.pack();
        frame.setVisible(true);
    }
    
    /**
     * Attaches the new alarm dialog (that lets users set the hour and minute
     * of a new alarm) to the "New Alarm" button and the "New Alarm" item of the
     * "Clock" menu.
     * 
     * @param listener  User mouse click opens a dialog box to input values to
     *                  the queue.
     */
    public void addNewAlarmButtonListener(ActionListener listener)
    {
        newAlarmClock.addActionListener(listener);
        newAlarmButton.addActionListener(listener);
    }
    
    /**
     * Attaches a dialog that gives information about the program to the "About"
     * item in the "Help" menu.
     * 
     * @param listener  User mouse click opens a dialog with some text in it.
     */
    public void addAboutButtonListener(ActionListener listener)
    {
        aboutHelp.addActionListener(listener);
    }
    
    /**
     * Closes the program when clicked.
     * 
     * @param listener  User mouse click terminates the program.
     */
    public void addExitListener(ActionListener listener)
    {
        exitClock.addActionListener(listener);
    }
    
    /**
     * Attaches a file chooser that allows the user to save their alarms to an
     * .ics file in their selected directory with the name they entered.
     * 
     * @param listener  User mouse click opens the saveDialog file chooser.
     */
    public void addSaveListener(ActionListener listener)
    {
        saveAlarmClock.addActionListener(listener);
    }
    
    /**
     * Attaches a file chooser that allows the user to choose a .ics file that
     * they wish to open in their selected directory. The program will read the 
     * file (if it is properly formatted) and load the alarms into the queue.
     * 
     * @param listener  User mouse click opens the openDialog file chooser.
     */
    public void addOpenListener(ActionListener listener)
    {
        openAlarmClock.addActionListener(listener);
    }
    
    /**
     * Repaints the clock when it is called (every time a second passes) so that 
     * it shows the correct time.
     * 
     * @param o     The observer that is notified when a second passes.
     * @param arg   
     */
    public void update(Observable o, Object arg) {
        panel.repaint();
    }
    
    /**
     * Updates all of the panels to make sure they are displaying the correct
     * information.
     */
    public void updateAll()
    {
        nextAlarmPanel.validate();
        bottomPanel.validate();
        alarmListPanel.validate();
        pane.validate();
        nextAlarmPanel.repaint();
        bottomPanel.repaint();
        alarmListPanel.repaint();
        pane.repaint();
    }
    
    /**
     * Updates the alarm panels to make sure that the deletion of the alarm is
     * reflected in the display.
     */
    public void alarmJustRang()
    {
        nextAlarmPanel.removeAll();
        nextAlarmPanel.validate();
        nextAlarmPanel.repaint();
        nextAlarmPanel.add(nextAlarmLabel);
        
        refreshAlarmList();
    }


    /**
     * Updates the alarm list panel so that it displays the correct information.
     */
    public void refreshAlarmList()
    {
        alarmListPanel.removeAll();
        alarmListPanel.validate();
        alarmListPanel.repaint();
        
    }
    
    /**
     * Adds a given alarm to the alarm list in the GUI.
     * 
     * @param pnl   A panel with the alarms label, edit button and delete button 
     *              that was generated by the queue.
     */
    public void addToAlarmList(JPanel pnl)
    {
        alarmListPanel.add(pnl);
        alarmListPanel.add(Box.createRigidArea(new Dimension(5, 10)));
    }
}
