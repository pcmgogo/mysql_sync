package mysql_sync;
public class Mysql_sync {
    public static String version = "2.1";
    public static FileIOLibrary.SystemLogText log = new FileIOLibrary.SystemLogText("LOG_SYNC");
    public static boolean use_gui = false;
    public static sync_core sync;
    public static sync_form form;
    public static void main(String[] args) {
        try{
            log.add_log("MySQL Sync Başlıyor");;
            log.add_log("Versiyon: "+version);
            FileIOLibrary.read_ini sysini = new FileIOLibrary.read_ini("system.ini");
            if(sysini.section_exists("Settings", "UseGui")){
                if(sysini.get_section("Settings", "UseGui").equals("Enable") || sysini.get_section("Settings", "UseGui").equals("Aktif") || sysini.get_section("Settings", "UseGui").equals("True")){
                    use_gui = true;
                }
            }
            else{
                sysini.store_section("Settings", "UseGui", "Disable");
            }

            
            
            if(use_gui){
                log.add_log("GUI Kullanılıyor");
                form = new sync_form();
            }
            else{
                log.add_log("GUI kullanılmıyor");                
            }
            sync = new sync_core();
        }
        catch(Exception E){
            log.add_error_log("Açılış hatası::"+E.getMessage());
        }
        
    }
}
