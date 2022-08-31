package mysql_sync;

import java.awt.Color;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

public class sync_form extends JFrame {
    JLabel source_host,source_user,source_pass,source_db;
    JLabel status;
    JLabel target_host,target_user,target_pass,target_db;
    String source_mysql_host,source_mysql_user,source_mysql_pass,source_mysql_db;
    String target_mysql_host,target_mysql_user,target_mysql_pass,target_mysql_db;

    
    sync_form(){
        this.setTitle("MySQL Sync V."+Mysql_sync.version);
        this.setSize(600, 200);
        this.getContentPane().setBackground(Color.white);
        this.setAlwaysOnTop(false);
        this.setLocationRelativeTo(null);
        this.setResizable(false);
        this.setLayout(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //this.setUndecorated(true);
        this.setVisible(true);
        status = new JLabel();
        status.setText("Durum");
        
        source_host = new JLabel();
        source_host.setText("Source Host Name");
        source_user = new JLabel();
        source_user.setText("Source User Name");
        source_pass = new JLabel();
        source_pass.setText("Source Password");
        source_db = new JLabel();
        source_db.setText("Source DB");

        target_host = new JLabel();
        target_host.setText("Target host name");
        target_user = new JLabel();
        target_user.setText("Target User name");
        target_db = new JLabel();
        target_db.setText("Target DB");
        target_pass = new JLabel();
        target_pass.setText("Target Password");

        this.getContentPane().add(source_host);
        this.getContentPane().add(source_user);
        this.getContentPane().add(source_pass);
        this.getContentPane().add(source_db);
        
        this.getContentPane().add(target_host);
        this.getContentPane().add(target_user);
        this.getContentPane().add(target_pass);
        this.getContentPane().add(target_db);

        this.getContentPane().add(status);

        source_host.setBounds(20,10,300,source_host.getPreferredSize().height);
        source_user.setBounds(20,30,300,source_user.getPreferredSize().height);
        source_pass.setBounds(20,50,300,source_pass.getPreferredSize().height);
        source_db.setBounds(20,70,300,source_db.getPreferredSize().height);

        target_host.setBounds(300,10,300,target_host.getPreferredSize().height);
        target_user.setBounds(300,30,300,target_user.getPreferredSize().height);
        target_pass.setBounds(300,50,300,target_pass.getPreferredSize().height);
        target_db.setBounds(300,70,300,target_db.getPreferredSize().height);

        status.setBounds(0,110,600,status.getPreferredSize().height);
        status.setHorizontalAlignment(SwingConstants.CENTER);

    }
    
}
