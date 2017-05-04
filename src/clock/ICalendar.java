/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clock;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

/**
 * Reads and writes alarms to a .ics file in a valid iCalendar format.
 * Should be able to read any .ics file as well as its own and create alarms 
 * based on the times of the DTSTARTs, but it hasn't been tested as it is outwith
 * the scope of this assignment.
 * 
 * 
 * @author Vicki Maciver 14006476
 * @version 2016-12-03
 */
public class ICalendar {
    
    String calFile;
    
    int hour;
    int minute;
    
    public ICalendar()
    {
    }
    
    /**
     * Opens and reads .ics files as long as they are in the correct format and
     * contain a DTSTART. First, it counts the number of DTSTART lines (in other
     * words, the number of events in the file). Then, it creates an array to store
     * all of these times based on the number of DTSTARTs it found previously.
     * As this program only deals with hours and minutes in its alarms, a large
     * chunk of the date-time format is skipped and only the hour and minute values
     * (which should begin on the 17th character and end on the 21th character of the line)
     * are stored. It then returns the array of all of these hours and minutes back
     * to the controller.
     * 
     * 
     * @param pathString    The path of the file that is to be opened, which is
     *                      passed from the Controller's open dialog.
     * 
     * @return              Returns a string array of all alarms in the file to
     *                      the Controller, which allows them to be added to the
     *                      queue.
     * 
     * @throws IOException  If the file is not able to be read from.
     */
    public String[] openAlarms(String pathString) throws IOException
    {
        String[] alarms;
        int i = 0;
        int alarmCounter = 0;
        Path path = Paths.get(pathString);
        
        Charset charset = Charset.forName("US-ASCII");
        try (BufferedReader reader = Files.newBufferedReader(path, charset))
        {
            String line = null;
            while ((line = reader.readLine()) != null)
            {
                if("DTSTART:".equals(line.substring(0,8)))
                {
                    alarmCounter++;
                }
            }
        } catch (IOException x) {
            System.err.format("IOException %s%n", x);
        }
            
        
        alarms = new String[alarmCounter];
        
        
        try (BufferedReader reader = Files.newBufferedReader(path, charset))
        {    
            String line = null;
            while ((line = reader.readLine()) != null)
            {
                if("DTSTART:".equals(line.substring(0,8)))
                {
                    alarms[i] = line.substring(17, 21);
                    System.out.println("alarms["+i+"] = " + alarms[i]);
                    i++;
                }
            }
        } catch (IOException x) {
            System.err.format("IOException %s%n", x);
        }
        
        return alarms;
    }
    
    /**
     * Writes a .ics file to disk in a valid iCalendar format. First, it saves all
     * of the alarms array into two integer arrays. The first two characters of 
     * each string are saved in the hour array, the last two are saved in the
     * minute array, and then they are both parsed from strings to integers. 
     * 
     * It then sets up the file - setting the file name based on what the user 
     * wanted, setting up the BufferedWriter to allow writing to files, creating
     * two date formats - one for date and one for alarms, and then initialising
     * and beginning the calendar (which retrieves the current date and time). 
     * It writes an informational header at the top of the file that just tells 
     * any person / program that reads it more about the file.
     * 
     * It then loops through all of the alarms in the alarm array and writes a 
     * VEVENT for each one in the file. A unique ID is generated based on the 
     * current date time plus a random 6-digit number. The DSTART contains the
     * time of the alarm. It inserts todays date in the alarm format and converts 
     * the hour and minute arrays into a suitable string. 
     * 
     * A RRULE of daily frequency is written to each alarm - so that it doesn't
     * matter what day the alarm is written for, it will always ring at the
     * specified day. The DTEND does the same but adds +1 to the minute (which 
     * is why they are being stored as integers rather than strings).
     * 
     * Finally, it writes some information about each VEVENT that doesn't vary,
     * and once it has reached the end of the alarm time array it states the
     * end of the calendar and finishes.
     * 
     * 
     * @param path          The path that the user wishes to save to, based on
     *                      where they chose to save in the save dialog.
     * @param alarmTime     The string of alarms that are to be saved to file.
     * 
     * @throws IOException If the file is not able to be written to.
     */
    public void saveAlarms(String path, String[] alarmTime) throws IOException
    {
       int[] hour = new int[alarmTime.length];
       int[] min = new int[alarmTime.length];
       
       for (int i = 0; i < alarmTime.length; i++)
       {
           hour[i] = Integer.parseInt(alarmTime[i].substring(0,2));
           min[i] = Integer.parseInt(alarmTime[i].substring(2));
       }
       
        File file = new File(path+".ics");
        
        BufferedWriter out = new BufferedWriter(new FileWriter(file));
        

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd'T'hhmmss");
        SimpleDateFormat alarmFormat = new SimpleDateFormat("yyyyMMdd'T'");
        
        //Initialise Calendar
        Date today = Calendar.getInstance().getTime();
        out.write("BEGIN:VCALENDAR");
        out.newLine();
        out.write("VERSION:2.0");
        out.newLine();
        out.write("PRODID:-//Vicki Maciver/14006476//AlarmClock v1.0//EN");
        out.newLine();
        
        for (int i = 0; i < alarmTime.length; i++)
        {
            out.write("BEGIN:VEVENT");
            out.newLine();
            
            // UID
            Random random = new Random();
            int randomInt = random.nextInt(999999);
            String uid = dateFormat.format(today);
            out.write("UID:"+uid+"Z-"+randomInt);
            out.newLine();
            
            // DTSTAMP
            String dtStamp = dateFormat.format(today);
            out.write("DTSTAMP:" + dtStamp);
            out.newLine();
            
            // DTSTART
            String dtStart = alarmFormat.format(today);
            out.write("DTSTART:" + dtStart);
                if (hour[i] < 10)
                {
                    out.write("0" + hour[i]);
                }
                else
                {
                    out.write("" + hour[i]);
                }

                if (min[i] < 10)
                {
                    out.write("0" + min[i]);
                }
                else
                {
                    out.write("" + min[i]);
                }
            out.write("00Z");
            out.newLine();
            
            // RRULE
            out.write("RRULE:FREQ=DAILY");
            out.newLine();
            
            // DTEND;
            out.write("DTEND:" + dtStart);
                if (hour[i] < 10)
                {
                    out.write("0" + hour[i]);
                }
                else
                {
                    out.write("" + hour[i]);
                }

                if (min[i] < 10)
                {
                    out.write("0" + (min[i]+1));
                }
                else
                {
                    out.write("" + (min[i]+1));
                }
            out.write("00Z");
            out.newLine();
            
           // Rest of info
           out.write("SUMMARY:Alarm");
           out.newLine();
           out.write("CLASS:PUBLIC");
           out.newLine();
           out.write("CATEGORIES:ALARM");
           out.newLine();
           out.write("END:VEVENT");
           out.newLine();
        }
        out.write("END:VCALENDAR");
        out.close();
    }
    
}
