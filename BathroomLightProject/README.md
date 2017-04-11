## Bathroom light project Overview 

The BathroomLight Project uses both Device twins and Device Methods to update a bathroom light in Java 

### Step 1: Run the device under the the_device folder. 
It does the following: 
1) Turns the device homekit on, but the bathroom light will be set to "OFF"
2) Sets the reported property of the bathroom-light in the device twin to "OFF"

         To build the class execute - mvn clean package -DskipTests
         To run the class execute  - mvn exec:java -Dexec.mainClass="com.mycompany.app.App"
         
         
### Step 2: Run the service side under the_cloud folder
It does the following:
1) Prints out the Device twin. Notice that the Reported property of bathroom-light will be set to "OFF"
2) Sets the desired property of bathroom light to "ON"
3) Checks to see if the reported state of the bathroom light matches the desired state of the bathroom light. In our case, since the  reported state of the bathroom-light is "OFF" and the desired state of the bathroom light is "ON", there is a mismatch. 
4) Using Device Methods, turn the bathroom-light of the homekit device to "ON"

         To build the class execute - mvn clean package -DskipTests
         To run the class execute  - mvn exec:java -Dexec.mainClass="com.mycompany.app.App"
         
### Step 3: Notice that the_device bathroom light is now turned "ON"
Also notice that the reported state of the bathroom-light is now turned "ON"

