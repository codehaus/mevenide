
package example;


import org.apache.log4j.Logger;


public class OtherExample {

    private static Logger logger = Logger.getLogger(OtherExample.class);


    public OtherExample() {

    }


    public void doItAgain() {
        logger.warn("Doing it again.");
    }

}

