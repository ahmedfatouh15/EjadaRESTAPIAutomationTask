package ConfigReader;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class ConfigFileReader {

    private Properties properties;
    private final String propertyFilePath= "config//config.properties";

    public ConfigFileReader(){
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(propertyFilePath));
            properties = new Properties();
            try {
                properties.load(reader);
                reader.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public String getClientName() {
        String clientName = properties.getProperty("clientName");
        if(clientName != null) return clientName;
        else throw new RuntimeException("Please Add the clientName to the config file.");
    }

    public String getEmailDomain() {
        String emailDomain = properties.getProperty("emailDomain");
        if(emailDomain != null) return emailDomain;
        else throw new RuntimeException("Please Add the clientEmail to the config file.");
    }
}
