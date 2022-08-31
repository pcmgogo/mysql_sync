// background_form V.1.0.1
// 18.04.2013 - Error log kayıtlarında normal kayıtlara da ekleme eklendi. Error olarak eklenen kayıt normal log dosyasınada eklenir.

package mysql_sync;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;


public class SystemLogText {
    String file_name;

    public SystemLogText(){
        System.out.println("Log başlatılıyor");
        create_log_file();
    }

    public boolean check_log_date(){
        String date_now = new DateTime().date_now();
        String nameOS = System.getProperty("os.name");
        String path;
        if(nameOS.equals("Linux")){
            path = "LOGS/";
            }
        else{
            path = ".\\LOGS\\";
        }
        String now_file_name = path+date_now+".txt";
        if(now_file_name.equals(file_name)){
            return true;
        }
        else{
            return false;
        }
        
    }
    
    public void create_log_file(){
        String date_now = new DateTime().date_now();
        String nameOS = System.getProperty("os.name");
        String path;
        if(nameOS.equals("Linux")){
            path = "LOGS/";
            }
        else{
            path = ".\\LOGS\\";
        }
        file_name = path+date_now+".txt";

        File yourFile = new File(file_name);
        if(!yourFile.exists()) {
            try{          
                System.out.println("Log dosyası yaratılıyor,Dosya:"+file_name);
                yourFile.createNewFile();
            }
            catch(Exception E){
                System.out.println("Log dosyası "+file_name+" yaratılamadı, Hata:"+E.getMessage());
            }
        }
    }
    
     public void add_log(String mes){
         try{
             if(check_log_date()){
                 //Date and file is same
             }
             else{
                 create_log_file();
             }
             mes = new DateTime().time_now()+" - "+mes;
             try{
                BufferedWriter bw = new BufferedWriter(new FileWriter(file_name,true));
                bw.write(mes);
                bw.newLine();
                bw.close();
             }
             catch(Exception E){
                 System.out.println("Log mesajı eklenirken hata oluştu, Seviye2:"+E.getMessage());
             }
         }
         catch(Exception full_ex){
             System.out.println("Log mesajı eklenirken hata oluştu, Seviye1:"+full_ex.getMessage());
         }
     }
    

    public void add_error_log(String mes){
        String nameOS = System.getProperty("os.name");
        String path;
        add_log(mes);
        if(nameOS.equals("Linux")){
            path = "LOGS/";
            }
        else{
            path = ".\\LOGS\\";
        }
        file_name = path+"ERRORS.txt";

        File yourFile = new File(file_name);
        if(!yourFile.exists()) {
            try{          
                System.out.println("ERROR Log dosyası yaratılıyor,Dosya:"+file_name);
                yourFile.createNewFile();
            }
            catch(Exception E){
                System.out.println("Log dosyası "+file_name+" yaratılamadı, Hata:"+E.getMessage());
            }
        }

        if(yourFile.exists()) {
            mes = new DateTime().date_now()+" "+new DateTime().time_now()+" - "+mes;
            try{
                BufferedWriter bw = new BufferedWriter(new FileWriter(file_name,true));
                bw.write(mes);
                bw.newLine();
                bw.close();
            }
            catch(Exception E){
                System.out.println("Log mesajı eklenirken hata oluştu:"+E.getMessage());
            }
        }
        System.out.println(mes);
     }

        
}
