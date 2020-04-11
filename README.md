# cs122b-spring20-team-128
cs122b-spring20-team-128 created by GitHub Classroom

1.
(Demo video on Google Drive -- accessible with an UCI.edu address)
Project1 Demo: https://drive.google.com/file/d/10Sd875ILTk9jSd6qhfL7swqnfdbdC8PM/view?usp=sharing

2.
How to run this on Tomcat?
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

3.Contributions
  Ganyu's Contribution:
    -MovieList Page
    -Unified Database sources
    -Setup AWS(MySQL + Tomcat)
    -Demostration and documentation
    -Troubleshoot all sorts of weird issues(dependency wouldn't load itself? How to use git? What's application context and how that effect our program? Small but confusing task like this.)

  Boon's Contribution:
    -Single Movie Page
    -Single Star Page
    -Links between them and MovieList Page
