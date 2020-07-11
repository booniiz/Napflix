Preconditions:  
  - You need to populate the database first.
  - Run the stored-procedure(IN THE NAPFLIX DIRECOTRY)
  - Encrypt passwords for both users and employees
  - You provide(hard code) your database URI, username, password into databaseAuthentication class.
  - Your Tomcat server is clean(fresh out of the zips with no other webapps)
    
 Steps:
  1. cd under the Napflix director where the pom.xml lies
  2. mvn package
  3. cp ./target/*.war /your-tomcat-directory/webapps
  4. bash /your-tomcat-directory/bin/catalina.sh run(or start
  5. your-public-addres:your port/Napflix(or what ever application context you decided to use)
