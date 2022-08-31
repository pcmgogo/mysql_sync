## English ##
### system.ini file configuration parameters ###
  **[Souce]** Source database settings
  - **Host** Source database host ip or url
  - **Port** Source database port of mysql database connection
  - **DB** Source database name
  - **User** Source database username
  - **Password** Source database password
  - **Exclude** Identifies the tables that do not want to be sent from Source to Destination. table1,table2 refers to tables that are not to be sent from source to destination. table1[column1,column2] refers to columns of table that are not to be sent from source to destination. Example: Exclude=table1,table2[column1,column2],table3
  
  **[Target]** Target database settings
  - **Host** Target database host ip or url
  - **Port** Target database port of mysql database connection
  - **DB** Target database name
  - **User** Targer database username
  - **Password** Target database password
  - **Include** Identifies the tables to be synchronized from destination to source. Example: Include=table1,table2
  
  **[Settings]** Application settings
  - **Time** Waiting time for synchronization of source and target databases. It will be repeated. Unit is seconds
  - **CheckNum** Number of database table rows to controlling back. When the number is too high, it unnecessarily busy the database. When the number is too low, there may be entries missed during the waiting period. 
  - **UseGui** Use graphical user interface instead of command line interface
  

## Türkçe ##
### system.ini dosyası konfigürasyon parametreleri ###
  **[Souce]** Kaynak veritabanı ayarları
  - **Host** Kaynak veritabanı ip adresi veya url adresi
  - **Port** Kaynak veritabanı mysql bağlantı portu
  - **DB** Kaynak veritabanı adı
  - **User** Kaynak veritabanı kullanıcı adı
  - **Password** Kaynak veritabanı şifresi
  - **Exclude** Kaynaktan hedefe gönderilmek istenmeyen tabloları veya sütunları ifade eder. table1,table2 gönderilmeyecek tabloları, table1[column1,column2] gönderilmeyecek sütunları tanımlamak için kullanılır. Örneğin: Exclude=table1,table2[column1,column2],table3
  
  **[Target]** Hedef veritabanı ayarları
  - **Host** Hedef veritabanı ip adresi veya url adresi
  - **Port** Hedef veritabanı mysql bağlantı portu
  - **DB** Hedef veritabanı adı
  - **User** Hedef veritabanı kullanıcı adı
  - **Password** Hedef veritabanı şifresi
  - **Include** Hedeften kaynağa alınması istenilen tabloları ifade eder. Örneğin: Include=table1,table2
  
  **[Settings]** Uygulama ayarları
  - **Time** Senkronizasyon bekleme süresi. Tekrarlanır. Bekleme süresini saniye olarak ifade eder. 
  - **CheckNum** Senkronizasyon esnasında tablo içerisinde geriye dönük taranacak satır sayısı. Numara çok büyük olduğunda veritabanını gereksiz meşgul eder. Numara çok küçük olduğunda, bekleme süresi içerisinde yapılan girişleri kaybedebilir. 
  - **UseGui** Grafik kullanıcı arayüzü kullanımını açar
