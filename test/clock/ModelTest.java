/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clock;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.Calendar;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;

/**
 *
 * @author Vicki
 */
public class ModelTest {
    
    Model model;
    
    public ModelTest() {
    }

    @Before
    public void setUp()
    {
        model = new Model();
        ICalendar ical = new ICalendar();
    }
    
    @Test
    public void testStringToTime1()
    {
        System.out.println("stringToTime1");
        
            String alarmTime = "2359";
        
        int[] expResult = new int[]{23, 59};
        int[] result = model.stringToTime(alarmTime);
        assertEquals(expResult[0], result[0]);
        assertEquals(expResult[1], result[1]);
    }
    
    @Test
    public void testStringToTime2()
    {
        System.out.println("stringToTime2");
        
            String alarmTime = "0303";
        
        int[] expResult = new int[]{3, 3};
        int[] result = model.stringToTime(alarmTime);
        assertEquals(expResult[0], result[0]);
        assertEquals(expResult[1], result[1]);
    }
    
    @Test
    public void testStringToTime3()
    {
        System.out.println("stringToTime3");
        
            String alarmTime = "0000";
        
        int[] expResult = new int[]{0, 0};
        int[] result = model.stringToTime(alarmTime);
        assertEquals(expResult[0], result[0]);
        assertEquals(expResult[1], result[1]);
    }
    
    @Test
    public void testTimeToString1()
    {
        System.out.println("timeToString1");
        
            int hour = 23;
            int minute = 59;
            
        String expResult = "2359";
        String result = model.timeToString(hour, minute);
        assertEquals(expResult, result);
    }
    
    @Test
    public void testTimeToString2()
    {
        System.out.println("timeToString2");
        
            int hour = 3;
            int minute = 3;
            
        String expResult = "0303";
        String result = model.timeToString(hour, minute);
        assertEquals(expResult, result);
    }
    
        @Test
    public void testTimeToString3()
    {
        System.out.println("timeToString3");
        
            int hour = 0;
            int minute = 0;
            
        String expResult = "0000";
        String result = model.timeToString(hour, minute);
        assertEquals(expResult, result);
    }
    
    @Test
    public void testFindPriority()
    {
        System.out.println("findPriority");
        
            Calendar date = Calendar.getInstance();
            int hour = date.get(Calendar.HOUR_OF_DAY);
            int minute = date.get(Calendar.MINUTE);

            int prioHour = 13 - hour;
            int prioMinute = 45 - minute;

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
        
        int expResult = tempPrio;
        int result = model.findPriority(13, 45);
        assertEquals(expResult, result);      
    }
    
    @Test
    public void testAdd() throws Exception
    {
        System.out.println("add");

            model.addToQueue("0345", model.findPriority(3, 45));  
            
        String expResult = "[(0345, " + model.findPriority(3, 45) + ")]";
        String result = model.alarms.toString();
        assertEquals(expResult, result);  
    }
    
    @Test
    public void testAddMultiple() throws Exception
    {
        System.out.println("addMultiple");
        
            model.addToQueue("2359", model.findPriority(23,59));
            model.addToQueue("0000", model.findPriority(0, 0));
            
        String expResult = "[(2359, " + model.findPriority(23, 59) + "), (0000, " + model.findPriority(0, 0) + ")]";
        String result = model.alarms.toString();
        assertEquals(expResult, result);
    }
    
    @Test
    public void testEdit() throws Exception
    {
        System.out.println("edit");
        
            model.addToQueue("0345", model.findPriority(3, 45));
            model.editAlarm(0, "0330", model.findPriority(3, 45));
            
        String expResult = "[(0330, " + model.findPriority(3,45)+")]";
        String result = model.alarms.toString();
        assertEquals(expResult, result);
    }
    
    @Test
    public void testEditMultiple() throws Exception
    {
        System.out.println("editMultiple");
        
            model.addToQueue("2359", model.findPriority(23,59));
            model.addToQueue("0000", model.findPriority(0, 0));
            model.editAlarm(0, "0100", model.findPriority(01, 00));
            model.editAlarm(0, "0001", model.findPriority(0, 1));
            
        String expResult = "[(0001, " + model.findPriority(0, 1) + "), (0100, " + model.findPriority(1, 0) + ")]";
        String result = model.alarms.toString();
        assertEquals(expResult, result);
    }
    
    @Test
    public void testDelete() throws Exception
    {
        System.out.println("delete");
        
            model.addToQueue("0555", model.findPriority(5, 55));
            model.deleteAlarm(0);
            
        String expResult = "[]";
        String result = model.alarms.toString();
        assertEquals(expResult, result);
    }
    
    @Test
    public void testDeleteMultiple() throws Exception
    {
        System.out.println("deleteMultiple");
        
            model.addToQueue("0000", model.findPriority(0,0));
            model.addToQueue("2359", model.findPriority(23, 59));
            model.addToQueue("0001", model.findPriority(0, 1));
            model.deleteAlarm(1);
            
        String expResult = "[(2359, " + model.findPriority(23, 59) + "), (0001, " + model.findPriority(0, 1) + ")]";
        String result = model.alarms.toString();
        assertEquals(expResult, result);
    }
    
}
