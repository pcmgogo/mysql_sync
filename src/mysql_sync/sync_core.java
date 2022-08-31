package mysql_sync;

import FileIOLibrary.read_ini;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class sync_core {
    static FileIOLibrary.SystemLogText log = Mysql_sync.log;     
    String source_mysql_host,source_mysql_user,source_mysql_pass,source_mysql_db;
    String target_mysql_host,target_mysql_user,target_mysql_pass,target_mysql_db;
    List<String> exclude = new ArrayList<String>();
    List<String> include = new ArrayList<String>();
    
    int check_count;
    int check_time;
    
    sync_core(){
        log.add_log("Sync core başlatılıyor");
        log.add_log("Ini okunuyor");
        try{
            read_ini sysini = new read_ini("system.ini");
            if(sysini.file_found){
                log.add_log("Ini dosyası bulundu");
                
                String ex_st = sysini.get_section("Source", "Exclude");
                if(ex_st!=null && !ex_st.equals("")){
                    exclude = Arrays.asList(ex_st.split(","));
                    log.add_log("Exclude alındı:"+ex_st);                
                }
                else{
                    log.add_log("Exclude boş");                
                }
                String  in_st = sysini.get_section("Target", "Include");
                if(in_st != null && !in_st.equals("")){
                    include = Arrays.asList(in_st.split(","));
                    log.add_log("Include alındı:"+in_st);
                }
                else{
                    log.add_log("Include boş");
                }

                source_mysql_user = sysini.get_section("Source", "User");
                source_mysql_pass = sysini.get_section("Source", "Password");
                source_mysql_host = sysini.get_section("Source", "Host");
                source_mysql_db = sysini.get_section("Source", "DB");
                if(Mysql_sync.use_gui){
                    Mysql_sync.form.source_user.setText(Mysql_sync.form.source_user.getText()+":"+source_mysql_user);
                    Mysql_sync.form.source_pass.setText(Mysql_sync.form.source_pass.getText()+":"+source_mysql_pass);
                    Mysql_sync.form.source_host.setText(Mysql_sync.form.source_host.getText()+":"+source_mysql_host);
                    Mysql_sync.form.source_db.setText(Mysql_sync.form.source_db.getText()+":"+source_mysql_db);
                    Mysql_sync.form.status.setText("Source konfigurasyonu alındı");
                }
                log.add_log("Source konfigurasyonu alındı");
                
                target_mysql_user = sysini.get_section("Target", "User");
                target_mysql_pass = sysini.get_section("Target", "Password");
                target_mysql_host = sysini.get_section("Target", "Host");
                target_mysql_db = sysini.get_section("Target", "DB");
                if(Mysql_sync.use_gui){
                    Mysql_sync.form.target_user.setText(Mysql_sync.form.target_user.getText()+":"+target_mysql_user);
                    Mysql_sync.form.target_pass.setText(Mysql_sync.form.target_pass.getText()+":"+target_mysql_pass);
                    Mysql_sync.form.target_host.setText(Mysql_sync.form.target_host.getText()+":"+target_mysql_host);
                    Mysql_sync.form.target_db.setText(Mysql_sync.form.target_db.getText()+":"+target_mysql_db);                    
                    Mysql_sync.form.status.setText("Target konfigurasyonu alındı");
                }
                log.add_log("Target konfigurasyonu alındı");
                
                if(sysini.get_section("Settings", "CheckNum")==null){
                    check_count=100;
                }
                else{
                    check_count = Integer.parseInt(sysini.get_section("Settings", "CheckNum"));            
                }
                
                check_time = 60;
                if(sysini.get_section("Settings", "Time")==null){
                }
                else{
                    check_time = Integer.parseInt(sysini.get_section("Settings", "Time"));
                }
                log.add_log("Veritabanına bağlanıyor, Süre:"+check_time);
                if(Mysql_sync.use_gui){
                    Mysql_sync.form.status.setText("Veritabanına bağlanıyor, Süre:"+check_time);
                }
                new Reminder(check_time);
            }
        }
        catch(Exception E){
            log.add_error_log("sync_core::"+E.getMessage());
        }
    }


    public class Reminder {
    Timer timer;
    int remain_time;

    public Reminder(int seconds) {
        timer = new Timer();
        remain_time = seconds;
        timer.schedule(new RemindTask(), remain_time*1000, remain_time*1000);
	}

    class RemindTask extends TimerTask {
        public void run() { 
            if(Mysql_sync.use_gui){
                Mysql_sync.form.status.setText("Veritabanına Bağlanıyor...");
            }
            log.add_log("Veritabanına bağlanıyor...");
            try{
                mysql_connection source_mysql = new mysql_connection(source_mysql_host, source_mysql_db, source_mysql_user, source_mysql_pass);
                source_mysql.connect();
                System.out.println("Source connected");
                log.add_log("Kaynağa bağlandı");

                try{
                    mysql_connection target_mysql = new mysql_connection(target_mysql_host, target_mysql_db, target_mysql_user, target_mysql_pass);
                    target_mysql.connect();
                    log.add_log("Hedefe bağlandı");
                    ResultSet source_query = source_mysql.mysql_query("Show tables");
                    
                    source_query.last();
                    int table_num = source_query.getRow();
                    source_query.first();
                    int table_num_index = 0;
                    while(table_num_index<table_num){
                        String table_name = source_query.getString(1);
                        if(exclude.indexOf(table_name)>=0){
                            //System.out.println("EXCLUDED TABLE --> "+table_name);
                        }
                        else{
                            System.out.println("Check table:"+table_name);
                            ResultSet target_query = target_mysql.mysql_query("Show tables like '"+table_name+"'");
                            if(!target_query.first()){
                                //System.out.println("Describe table Name:"+table_name);
                                target_mysql.mysql_execute("Create table if not exists "+table_name+" (ID Int NOT NULL AUTO_INCREMENT, PRIMARY KEY(ID))");

                                ResultSet table_structure_query = source_mysql.mysql_query("Describe "+table_name);
                                table_structure_query.last();
                                int field_num = table_structure_query.getRow();
                                int field_num_index = 1;
                                table_structure_query.first();
                                table_structure_query.next();
                                while(field_num_index<field_num){
                                    String field_name = table_structure_query.getString(1);
                                    String field_type = table_structure_query.getString(2);                            
                                    //System.out.println("Field Name:"+field_name+" Field Type:"+field_type);

                                    target_mysql.mysql_execute("Alter table "+table_name+" ADD "+field_name+" "+field_type);

                                    table_structure_query.next();
                                    field_num_index++;
                                }      
                               log.add_log("Tablo denetlemesi tamamlandı");
                            }
                            else{
                                log.add_log("Tablo bulundu, yapı denetleniyor");
                                ResultSet table_structure_query = source_mysql.mysql_query("Describe "+table_name);
                                table_structure_query.last();
                                int field_num = table_structure_query.getRow();
                                int field_num_index = 1;
                                table_structure_query.first();
                                table_structure_query.next();
                                while(field_num_index<field_num){
                                    String field_name = table_structure_query.getString(1);
                                    String field_type = table_structure_query.getString(2);                            

                                    ResultSet target_structure_query = target_mysql.mysql_query("SELECT * FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = '"+target_mysql_db+"' AND TABLE_NAME = '"+table_name+"' AND COLUMN_NAME = '"+field_name+"'");

                                    if(target_mysql.mysql_num_rows(target_structure_query)==0){
                                        target_mysql.mysql_execute("Alter table "+table_name+" ADD "+field_name+" "+field_type);
                                    }
                                    table_structure_query.next();
                                    field_num_index++;
                                }      
                               log.add_log("Tablo denetlemesi tamamlandı");
                            }
                            
                            ResultSet source_data_query = source_mysql.mysql_query("Select ID from "+table_name+" order by ID DESC LIMIT 1");
                            ResultSet target_data_query = target_mysql.mysql_query("Select ID from "+table_name+" order by ID DESC LIMIT 1");
                            int source_id = 0;
                            int target_id = 0;
                            if(source_data_query.first()){
                                source_id = source_data_query.getInt("ID");
                            }
                            if(target_data_query.first()){
                                target_id = target_data_query.getInt("ID");
                            }
                            log.add_log("Hedef ve Kaynak id alındı, Hedef:"+target_id+" Kaynak:"+source_id);
                            
                            if(source_id == 0 && target_id != 0 ){
                                target_mysql.mysql_execute("Truncate table "+table_name);
                                System.out.println("Table flush "+table_name);
                                log.add_log("Hedef Tablo boşaltıldı");
                            }

                            //System.out.println(table_name+" Source ID:"+source_id+" Target ID:"+target_id);
                            List<String> exclude_field = new ArrayList<String>();
                            if(source_id>target_id){
                                for(int t=0;t<exclude.size();t++){
                                    if(exclude.get(t).startsWith(table_name+"[")){
                                        log.add_log(exclude.get(t)+" kontrol ediliyor");
                                        String fld = exclude.get(t).substring(exclude.get(t).indexOf("[")+1,exclude.get(t).length()-1);
                                        //fld = fld.substring(0, fld.length() -1);
                                        log.add_log(table_name+" için hariç tutulan kolon bulundu:"+fld);
                                        exclude_field = Arrays.asList(fld.split(":"));
                                    }
                                }
                                
                                log.add_log("Kaynak id Hedef id'den büyük");
                                source_data_query = source_mysql.mysql_query("Select * from "+table_name+" where ID > '"+target_id+"' ORDER BY ID ASC");
                                source_data_query.last();
                                log.add_log("Kaynağa bağlandı ve güncellemeler alındı");
                                int i=1;
                                int change_number = source_data_query.getRow();
                                log.add_log("Kaynağın ilk verisine ulaşılıyor");
                                source_data_query.first();
                                while(i<=change_number){
                                    int column_number = source_data_query.getMetaData().getColumnCount();
                                    int j = 1;
                                    String columns = "";
                                    String values = "";
                                    while(j<=column_number){
                                        String column_name = source_data_query.getMetaData().getColumnName(j);
                                        String column_value = source_data_query.getString(column_name);
                                        //Added V.2.1
                                        if(exclude_field.indexOf(column_name)>=0){
                                            log.add_log("Senkronizasyon için kolon atlandı, kolon hariç tutulmuş:"+column_name);
                                            j++;
                                        }
                                        else{
                                            //Added V.1.0.1
                                            columns = columns+column_name;
                                            if(column_value != null){
                                                column_value = mysql_connection.mysql_real_escape_string(column_value);
                                                column_value = "'"+column_value+"'";
                                            }
                                            values = values+column_value;
                                            j++;
                                            if(j<=column_number){
                                                columns = columns+',';
                                                values = values+',';
                                            }
                                        }
                                    }
                                    log.add_log("Kolon:"+columns+" Değer:"+values+" eklendi (güncellendi)");
                                    System.out.println("Col:"+columns+" Val:"+values+" inserted");
                                    target_mysql.mysql_execute("Insert into "+table_name+" ("+columns+") values ("+values+")");
                                    if(Mysql_sync.use_gui){
                                        Mysql_sync.form.status.setText(i+"/"+change_number+" updating");
                                    }                                    
                                    i++;
                                    source_data_query.next();
                                }
                            }
                        int check_id = source_id - check_count; 
                        //System.out.println("Check ID:"+check_id);
                        source_data_query = source_mysql.mysql_query("Select * from "+table_name+" where ID > '"+check_id+"' ORDER BY ID ASC");
                        target_data_query = target_mysql.mysql_query("Select * from "+table_name+" where ID > '"+check_id+"' ORDER BY ID ASC");
                        if(source_data_query.last()){
                            int i = 0;
                            int row_number = source_data_query.getRow();
                            //target_data_query.last();
                            //int target_row_number = target_data_query.getRow();
                            //System.out.println("Source row number is "+row_number+" Target is "+target_row_number);
                            source_data_query.first();
                            target_data_query.first();
                            while(i<row_number){
                                //Search for ID
                                if(source_data_query.getString(1).equals(target_data_query.getString(1))){
                                    int j = 2;
                                    int col_number = source_data_query.getMetaData().getColumnCount();
                                    while(j<=col_number){
                                        if(exclude_field.indexOf(source_data_query.getMetaData().getColumnLabel(j))>=0){
                                            log.add_log("Eşleşmeyen kolon denetimi atlandı, kolon ("+source_data_query.getMetaData().getColumnLabel(j)+") hariç tutulmuş");
                                        }
                                        else{
                                            if(source_data_query.getString(j) == null){
                                               if(target_data_query.getString(j) == null){
                                               }
                                               else{
                                                   target_mysql.mysql_execute("Update "+table_name+" set "+source_data_query.getMetaData().getColumnLabel(j)+"=null where ID = "+target_data_query.getString(1)+"");
                                                   //System.out.println(table_name+" table "+source_data_query.getMetaData().getColumnLabel(j)+" column balanced NULL");
                                                   log.add_log(table_name+" tablosu"+source_data_query.getMetaData().getColumnLabel(j)+" kolonu NULL ile dengelendi");
                                               }
                                            }
                                            else{
                                                if(source_data_query.getString(j).equals(target_data_query.getString(j))){
                                                }
                                                else{
                                                    //System.out.println(table_name+" table "+source_data_query.getMetaData().getColumnLabel(j)+" column balanced "+source_data_query.getString(j));
                                                    log.add_log(table_name+" tablosu "+source_data_query.getMetaData().getColumnLabel(j)+" kolonu "+source_data_query.getString(j)+" ile dengelendi");
                                                    //Add V.1.0.1
                                                    //target_mysql.mysql_execute("Update "+table_name+" set "+source_data_query.getMetaData().getColumnLabel(j)+"='"+mysql_connection.mysql_real_escape_string(source_data_query.getString(j))+"' where ID = "+target_data_query.getString(1)+"");
                                                    target_mysql.mysql_execute("Update "+table_name+" set "+source_data_query.getMetaData().getColumnLabel(j)+"='"+mysql_connection.mysql_real_escape_string(source_data_query.getString(j))+"' where ID = "+target_data_query.getString(1)+"");
                                                }
                                            }
                                        }
                                        j++;
                                    }
                                    target_data_query.next();                                    
                                    source_data_query.next();
                                    i++;
                                }
                                else{
                                    System.out.println("Source ID:"+source_data_query.getString(1)+" Target ID:"+target_data_query.getString(1));
                                    target_mysql.mysql_execute("Delete from "+table_name+" where ID = '"+target_data_query.getString(1)+"'");
                                    System.out.println(target_data_query.getString(1)+" deleted for ID matching");
                                    log.add_log(target_data_query.getString(1)+" verisi ID eşleştirme için silindi");                                    
                                    target_data_query.next();                                    
                                }
                            }
                            
                            if(target_data_query.isAfterLast()){
                                                            
                            }
                            else{
                                while(!target_data_query.isAfterLast()){
                                    target_mysql.mysql_execute("Delete from "+table_name+" where ID = '"+target_data_query.getString(1)+"'");
                                    System.out.println(target_data_query.getString(1)+" deleted for record count balancing");
                                    log.add_log(target_data_query.getString(1)+" verisi sayı eşleştirme için silindi");                                    
                                    target_data_query.next();                                                                        
                                }
                            }
                            
                        }                        
                        
                        }
                        source_query.next();
                        table_num_index++;
                        }
                    
                    //Ters denetleme başlatılıyor
                    if(Mysql_sync.use_gui){
                        Mysql_sync.form.status.setText("Geri senkronizasyon yapılıyor");
                    }
                    log.add_log("Geri senkronizasyon yapılıyor");
                    int h=0;
                    while(h<include.size()){
                        String inc_table_name = include.get(h);
                        System.out.println("Back syncronization table "+inc_table_name+" checking...");
                        System.out.println("Tablo "+inc_table_name+" geri senkronizasyon için denetleniyor");
                        ResultSet target_query = target_mysql.mysql_query("Show tables like '"+inc_table_name+"'");
                            if(!target_query.first()){
                                //System.out.println("Describe table Name:"+table_name);
                                target_mysql.mysql_execute("Create table if not exists "+inc_table_name+" (ID Int NOT NULL AUTO_INCREMENT, PRIMARY KEY(ID))");

                                ResultSet table_structure_query = source_mysql.mysql_query("Describe "+inc_table_name);
                                table_structure_query.last();
                                int field_num = table_structure_query.getRow();
                                int field_num_index = 1;
                                table_structure_query.first();
                                table_structure_query.next();
                                while(field_num_index<field_num){
                                    String field_name = table_structure_query.getString(1);
                                    String field_type = table_structure_query.getString(2);                            
                                    //System.out.println("Field Name:"+field_name+" Field Type:"+field_type);

                                    target_mysql.mysql_execute("Alter table "+inc_table_name+" ADD "+field_name+" "+field_type);

                                    table_structure_query.next();
                                    field_num_index++;
                                }                            
                            }
                            
                            
                            ResultSet target_data_query = target_mysql.mysql_query("Select ID from "+inc_table_name+" order by ID DESC LIMIT 1");
                            ResultSet source_data_query = source_mysql.mysql_query("Select ID from "+inc_table_name+" order by ID DESC LIMIT 1");
                            int source_id = 0;
                            int target_id = 0;
                            if(target_data_query.first()){
                                target_id = target_data_query.getInt("ID");
                            }
                            if(source_data_query.first()){
                                source_id = source_data_query.getInt("ID");
                            }
                            
                            if(target_id == 0 && source_id != 0 ){
                                source_mysql.mysql_execute("Truncate table "+inc_table_name);
                                System.out.println("BackSync Table flush "+inc_table_name);
                                log.add_log("Geri Senkronizasyon tablo temizleme, Tablo:"+inc_table_name);
                            }

                            //System.out.println(inc_table_name+" Source ID:"+source_id+" Target ID:"+target_id);

                            if(target_id>source_id){
                                target_data_query = target_mysql.mysql_query("Select * from "+inc_table_name+" where ID > '"+source_id+"' ORDER BY ID ASC");
                                target_data_query.last();
                                int i=1;
                                int change_number = target_data_query.getRow();
                                target_data_query.first();
                                while(i<=change_number){
                                    int column_number = target_data_query.getMetaData().getColumnCount();
                                    int j = 1;
                                    String columns = "";
                                    String values = "";
                                    while(j<=column_number){
                                        String column_name = target_data_query.getMetaData().getColumnName(j);
                                        String column_value = target_data_query.getString(column_name);
                                       //Added V.1.0.1
                                        columns = columns+column_name;
                                        if(column_value != null){
                                            column_value = mysql_connection.mysql_real_escape_string(column_value);                                        
                                            column_value = "'"+column_value+"'";
                                        }
                                        values = values+column_value;
                                        j++;
                                        if(j<=column_number){
                                            columns = columns+',';
                                            values = values+',';
                                        }
                                    }
                                    //System.out.println("Col:"+columns+" Val:"+values+" inserted");
                                    source_mysql.mysql_execute("Insert into "+inc_table_name+" ("+columns+") values ("+values+")");
                                    if(Mysql_sync.use_gui){
                                        Mysql_sync.form.status.setText(i+"/"+change_number+" updating");
                                    }
                                    i++;
                                    target_data_query.next();
                                }
                            }
                        int check_id = source_id - check_count; 
                        //System.out.println("Check ID:"+check_id);
                        source_data_query = source_mysql.mysql_query("Select * from "+inc_table_name+" where ID > '"+check_id+"' ORDER BY ID ASC");
                        target_data_query = target_mysql.mysql_query("Select * from "+inc_table_name+" where ID > '"+check_id+"' ORDER BY ID ASC");
                        if(target_data_query.last()){
                            int i = 0;
                            int row_number = target_data_query.getRow();
                            source_data_query.first();
                            target_data_query.first();
                            while(i<row_number){
                                //Search for ID
                                //System.out.println(i+" Checking"+target_data_query.getString(1)+" =? "+source_data_query.getString(1));
                                if(target_data_query.getString(1).equals(source_data_query.getString(1))){
                                    int j = 2;
                                    int col_number = target_data_query.getMetaData().getColumnCount();
                                    while(j<=col_number){
                                        if(target_data_query.getString(j) == null){
                                           if(source_data_query.getString(j) == null){
                                           }
                                           else{
                                               source_mysql.mysql_execute("Update "+inc_table_name+" set "+target_data_query.getMetaData().getColumnLabel(j)+"=null where ID = "+source_data_query.getString(1)+"");
                                               System.out.println(inc_table_name+" table "+target_data_query.getMetaData().getColumnLabel(j)+" column balanced NULL");                                               
                                               log.add_log(inc_table_name+" tablosu "+target_data_query.getMetaData().getColumnLabel(j)+" kolonu NULL ile geri senk. dengeleniyor");                                               
                                           }
                                        }
                                        else{
                                            if(target_data_query.getString(j).equals(source_data_query.getString(j))){
                                                //System.out.println(target_data_query.getString(j)+"Equal");
                                            }
                                            else{
                                                System.out.println(inc_table_name+" table "+target_data_query.getMetaData().getColumnLabel(j)+" column balanced "+target_data_query.getString(j));
                                                log.add_log(inc_table_name+" tablosu "+target_data_query.getMetaData().getColumnLabel(j)+" kolonu "+target_data_query.getString(j)+" verisi ile geri senk.dengeleniyor");
                                                //Added V.1.0.1
                                                //source_mysql.mysql_execute("Update "+inc_table_name+" set "+target_data_query.getMetaData().getColumnLabel(j)+"='"+target_data_query.getString(j)+"' where ID = "+source_data_query.getString(1)+"");
                                                source_mysql.mysql_execute("Update "+inc_table_name+" set "+target_data_query.getMetaData().getColumnLabel(j)+"='"+mysql_connection.mysql_real_escape_string(target_data_query.getString(j))+"' where ID = "+source_data_query.getString(1)+"");
                                            }
                                        }
                                        j++;
                                    }
                                    target_data_query.next();                                    
                                    source_data_query.next();
                                    i++;
                                }
                                else{
                                    System.out.println("Taregt ID:"+target_data_query.getString(1)+" Source ID:"+source_data_query.getString(1));
                                    log.add_log("Hedef ID:"+target_data_query.getString(1)+" Kaynak ID:"+source_data_query.getString(1));
                                    source_mysql.mysql_execute("Delete from "+inc_table_name+" where ID = '"+source_data_query.getString(1)+"'");
                                    System.out.println(source_data_query.getString(1)+" deleted for ID matching");
                                    log.add_log(source_data_query.getString(1)+" ID eşleştirme için siliniyor (Geri senk.)");
                                    source_data_query.next();                                    
                                }
                            }
                            
                            if(source_data_query.isAfterLast()){
                                                            
                            }
                            else{
                                while(!source_data_query.isAfterLast()){
                                    source_mysql.mysql_execute("Delete from "+inc_table_name+" where ID = '"+source_data_query.getString(1)+"'");
                                    System.out.println(source_data_query.getString(1)+" deleted for record count balancing");
                                    log.add_log(source_data_query.getString(1)+" kayıt sayısı dengesi için siliniyor (Geri senk.)");
                                    source_data_query.next();                                                                        
                                }
                            }
                            
                        }     
                        h++;
                        
                    }

                    
                    target_mysql.disconnect();
                    //System.out.println("Target disconnected");
                }
                catch(Exception Ex){
                    log.add_error_log("Hedefe bağlantı hatası,E:"+Ex.getMessage());
                    System.out.println("Fail to connect target,E:"+Ex.getMessage());
                }
            source_mysql.disconnect();
            //System.out.println("Source disconnected");
            }
            catch(Exception E){
                log.add_error_log("Kaynağa bağlantı hatası,E:"+E.getMessage());
                System.out.println("Fail to connect source,E:"+E.getMessage());
            }
            if(Mysql_sync.use_gui){
                Mysql_sync.form.status.setText("Denetleme tamamlandı, "+check_time+" saniye sonra tekrarlanacak");
            }
            log.add_log("Denetleme tamamlandı, "+check_time+" saniye sonra tekrar yapılacak");
        }
    }
    }
    
    
}
