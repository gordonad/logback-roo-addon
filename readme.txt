Welcome to the Roo LogBack addon!

09-Nov-10 - gDickens - Instructions for test release
In version 0.1.0:
1. Build the AddOn Project with Maven
2. Note your Maven file location and jar file name (you need this for the next step)
3. At the Roo Shell type the following:
   osgi start --url  file:///user_home/.m2/repository/com/gordondickens/roo/addon/roo.addon.logback/0.1.0.BUILD-SNAPSHOT/roo.addon.logback-0.1.0.BUILD-SNAPSHOT.jar
*** Set our maven directory in the above line first!

4. Verify with the following commmand:
   osgi ps

You should see a line similar to the following: 
   [  72] [Active     ] [    1] com-gordondickens-roo-addon-logback (0.1.0.BUILD-SNAPSHOT)

5. From the roo shell prompt, you can type:
logback setup



Verification:
1. New File: src/main/resources/logback.xml
2. Changes to Maven POM: new <property> logback.version, dependencies for logback-core, access, classic
3. Removed dependency: slf4j-log4j

Logging in Your Code:
1. Import the SLF4J Logger classes
   import org.slf4j.Logger;
   import org.slf4j.LoggerFactory;

2. Declare the logger
   public class MyClass {
	   Logger logger = LoggerFactory.getLogger(MyClass.class);
   ...

3. Write your log statements as desired
   logger.debug("****** YOUR DEBUG MESSAGE ******");

