//File Information 
//14.01.2013 - date_now fonksiyonu eklendi, tarihi döndürür ggaayyyy formatında
//01.01.2013 - TimeToStr fonksiyonu eklendi


/*
Letter 	Date or Time Component 	Presentation 	Examples
G 	Era designator 	Text 	AD
y 	Year 	Year 	1996; 96
M 	Month in year 	Month 	July; Jul; 07
w 	Week in year 	Number 	27
W 	Week in month 	Number 	2
D 	Day in year 	Number 	189
d 	Day in month 	Number 	10
F 	Day of week in month 	Number 	2
E 	Day in week 	Text 	Tuesday; Tue
a 	Am/pm marker 	Text 	PM
H 	Hour in day (0-23) 	Number 	0
k 	Hour in day (1-24) 	Number 	24
K 	Hour in am/pm (0-11) 	Number 	0
h 	Hour in am/pm (1-12) 	Number 	12
m 	Minute in hour 	Number 	30
s 	Second in minute 	Number 	55
S 	Millisecond 	Number 	978
z 	Time zone 	General time zone 	Pacific Standard Time; PST; GMT-08:00
Z 	Time zone 	RFC 822 time zone 	-0800
 */
package mysql_sync;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class DateTime {

    public String time_now(){
        String result;
        Calendar takvim = new GregorianCalendar();
        Date now = takvim.getTime();
        DateFormat DF = new SimpleDateFormat("kk:mm:ss");
        DF.setTimeZone(TimeZone.getTimeZone("Europe/Istanbul"));
        result = DF.format(now);
        return result;
    }
    
    
    public String date_now(){
        String result;
        Calendar takvim = new GregorianCalendar();
        Date now = takvim.getTime();
        DateFormat DF = new SimpleDateFormat("ddMMyyyy");
        DF.setTimeZone(TimeZone.getTimeZone("Europe/Istanbul"));
        result = DF.format(now);
        return result;
    }

    
    public Date StrToTime(String time_str){
        DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
        Date date =  new Date(2012, 1, 1);
        try {
            date = sdf.parse(time_str);
        } catch (ParseException ex) {
            System.out.println("Wrong time format");            
        }
        return date;
    } 

    public String TimeToStr(Date time_str){
        DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
        String output =  sdf.format(time_str);
        return output;
    } 

    public Date TimeNow(){
        Calendar takvim =  new GregorianCalendar();
        takvim = new GregorianCalendar();
        Date now = takvim.getTime();
        return now;
    }

}
