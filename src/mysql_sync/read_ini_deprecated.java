package mysql_sync;

import java.io.File;
import java.io.IOException;
import org.ini4j.Wini;

public class read_ini_deprecated {
    
    Wini config_ini;
    public Boolean file_found;

    public read_ini_deprecated(String file_name){
        file_found=true;
        try{
            File config_file = new File(file_name);
            config_ini = new Wini(config_file);
            file_found=true;
        }
        catch(IOException e){
            file_found=false;
        }
    }
    
    
    public String get_section(String sec_name, String item_name){
        String item_val = config_ini.get(sec_name, item_name);
        return item_val;
    }
        
}
