package property;


import endpoint.GameServerEndpoint;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PropertyHandler {

    private static final Logger logger = Logger.getLogger(PropertyHandler.class.getName());
    
    private static PropertyHandler instance = null;

    private Properties props = new Properties();

    private PropertyHandler() {
        InputStream input = null;
        try {
            String filename = "gameserver.properties";
            input = PropertyHandler.class.getClassLoader().getResourceAsStream(filename);
            if (input == null) {
                logger.log(Level.WARNING, "Sorry, unable to find {0}", filename);
                return;
            }
            props.load(input);

        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public static synchronized PropertyHandler getInstance() {
        if (instance == null) {
            instance = new PropertyHandler();
        }
        return instance;
    }

    public String getValue(String propKey) {
        return this.props.getProperty(propKey);
    }
}
