//mysql_connection.java 1.0.3
//15.01.2013 - mysql_real_escape_string fonksiyonu eklendi
//10.01.2013 - mysql_num_rows fonksiyonu tekrar güncellendi, çıkışta ve girişte if(next()) denetlemesi yapılıyor
//02.01.2013 - mysql_num_rows fonksiyonu güncellendi 
package mysql_sync;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.Statement;
import java.util.Properties;

public class mysql_connection{
    String hostname;
    String db_name;
    String username;
    String password;
    public Connection sql_connection;
    public Statement sql_statement;
    public ResultSet sql_resultset;
    
    boolean connected;
            
    public mysql_connection(String hostname, String db_name, String username, String password) throws ClassNotFoundException, InstantiationException, IllegalAccessException{
        this.hostname = hostname;
        this.db_name = db_name;
        this.username = username;
        this.password = password;
        Class.forName("com.mysql.jdbc.Driver").newInstance();

    }
    
    
    public ResultSet mysql_query(String sql_query) throws SQLException{
        sql_statement = sql_connection.createStatement();
        sql_resultset = null;
        sql_resultset = sql_statement.executeQuery(sql_query);
        return sql_resultset;
    }

    public int mysql_execute(String sql_query) throws SQLException{
        sql_statement = sql_connection.createStatement();
        int query;
        query =sql_statement.executeUpdate(sql_query); 
        return query;
    }

    public void disconnect() throws SQLException{
        try {
            if (sql_resultset != null) {
                sql_resultset.close();
            }
            if (sql_statement != null) {
                sql_statement.close();
            }
            if (sql_connection != null) {
                sql_connection.close();
            }
        } catch (Exception e) {
        System.out.println("MySQL Error:"+e.getMessage());
        }
        connected = false;
    }        

    public void connect() throws SQLException{        
        Properties info = new Properties();
        info.put("user", username);
        info.put("password", password);
        info.put("characterEncoding", "ISO8859_9");
        String url = "jdbc:mysql://"+hostname+"/"+db_name;
        sql_connection = DriverManager.getConnection(url, info);
        //sql_connection = DriverManager.getConnection("jdbc:mysql://"+hostname+"/"+db_name,username,password);
        sql_statement = sql_connection.createStatement();
    }
    
    public int mysql_num_rows(ResultSet RS) throws SQLException{
        int num_row = 0;
        //RS.first();
        //if(RS.next()){
            RS.last();
            num_row = RS.getRow();  
            RS.first();
        //s}
        return num_row;
    }
    
    
    public static String mysql_real_escape_string(String str) throws Exception{
        String clean_string = str;
        clean_string = clean_string.replaceAll("\\\\", "\\\\\\\\");
        clean_string = clean_string.replaceAll("\\\\", "\\\\\\\\");
        clean_string = clean_string.replaceAll("\\n","\\\\n");
        clean_string = clean_string.replaceAll("\\r", "\\\\r");
        clean_string = clean_string.replaceAll("\\t", "\\\\t");
        clean_string = clean_string.replaceAll("\\00", "\\\\0");
        clean_string = clean_string.replaceAll("'", "\\\\'");
        clean_string = clean_string.replaceAll("\\\"", "\\\\\"");
        return clean_string;
     }

    
}