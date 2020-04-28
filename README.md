# cs122b-spring20-team-128
cs122b-spring20-team-128 created by GitHub Classroom

1.(Demo video on Google Drive -- accessible with an UCI.edu address)
https://drive.google.com/file/d/1oS_OvPczb2WqV7QAAe9KpVphNClV6uJC/view?usp=sharing

2.How to run this on Tomcat?

  Preconditions:
  
    - You need to populate the database first.
    - You provide(hard code) your database URI, username, password into databaseAuthentication class.
    - Your Tomcat server is clean(fresh out of the zips with no other webapps)
    
  Steps:
  
    1. cd under the Napflix director where the pom.xml lies
    2. mvn package
    3. cp ./target/*.war /your-tomcat-directory/webapps
    4. bash /your-tomcat-directory/bin/catalina.sh run(or start)
    5. your-public-addres:your port/Napflix(or what ever application context you decided to use)
    
3.Substring matching design

  For substring if the user search with just one letter then it will match the pattern in front of the term(like 'z%').
  If the length of the search is greater than just 1 letter, then the query will try to find the pattern anywhere in the term(like '%love%').
  Works for titles,directors, and stars.
  
4.Contributions

  Ganyu's Contribution:
  
    -Login Page
    -Login Filter
    -Failure banner
    -Cart
    -Integration of carts in other parts
    -Organized the looks of every page
    
  Boon's Contribution:
  
    -Main Menu(search and browse)
    -Reworked Movielist
    -N amount of items on page, Prev/next, sort by button
    -Reworked single movie and star
    -All Sorting
    -Jump fucntion 
