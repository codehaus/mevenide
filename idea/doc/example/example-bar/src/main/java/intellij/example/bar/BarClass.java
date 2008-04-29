
package intellij.example.bar;


import intellij.example.foo.FooClass;
import org.apache.log4j.Logger;


public class BarClass {

    private FooClass foo = new FooClass();

    private static Logger logger = Logger.getLogger(BarClass.class);


    public BarClass() {

        foo.doIt();
    }


    public void doIt() {

        logger.debug("bar");
    }

}

