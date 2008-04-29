
package intellij.example.foo;


import org.apache.log4j.Logger;
import example.OtherExample;


public class FooClass {

    public OtherExample other = new OtherExample();
    private static Logger logger = Logger.getLogger(FooClass.class);


    public FooClass() {

    }


    public void doIt() {

        logger.warn("doing it");
    }

}

