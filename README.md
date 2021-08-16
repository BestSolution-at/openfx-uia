# openfx-uia

Full UIA Support for OpenJFX

## Getting started

To develop an application using openfx-uia you need the UIAPlatform.jar. It can be found in the following maven repo:

 https://maven.bestsolution.at/repos/releases/   
 Group ID: at.bestsolution.openfx-uia    
 Artifact ID: UIAPlatform    
 Version: 1.0.0-m2  

 For development it is sufficent to have the jar on the classpath. However to launch an application the following points need to be followed:

  * The jar needs to be put on the ext classpath next to the javafx jar
    * This can either be achived by copying the jar into your JDK installation or
    * by providing the system property `java.ext.dirs` This property takes a list of paths. for example `$YOURJDK/jre/lib/ext;$YOUR_FOLDER_WHERE_THE_JAR_IS`
      Note: if you use `java.ext.dirs` you need to point to the folder where UIAPlatform.jar is located, not to the jar itself
  * The platform needs to be selected by using the system property `glass.platform` it needs to set to `UIA`. => `-Dglass.platform=UIA`
  * Glass needs to be instructed to always use a11y, even if it does not know the platform. This is done by providing `-Dglass.accessible.force=true`


  Here is a full example:

  JDK8FX points to a java jdk with FX included    
  UIA points to a folder where UIAPlatform.jar is    

  ```
  $JDK8FX/bin/java -Djava.ext.dirs=$JDK8FX/jre/lib/ext;$UIA -Dglass.platform=UIA -Dglass.accessible.force=true your.application.MainClass
  ```

### Running the samples

The artifact UIAPlatform.fullsample includes all samples, the UIAPlatform and a launcher. It can be directly started with 
`java -jar UIAPlatform.fullsample.jar`.
However it requires the env variable `JDK8FX` to be set to your Java 8 with FX.
