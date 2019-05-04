package biohive.utility;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

public class ConfigLoader
{
    public File configFile;

    public ConfigLoader(String configFileName)
    {
        configFile = new File(configFileName);
    }

    public Baseline load() throws Exception
    {
        JAXBContext jaxbContext = JAXBContext.newInstance(Baseline.class);
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        Baseline bInfo = (Baseline)jaxbUnmarshaller.unmarshal(configFile);
        bInfo.prepareOutputIdentifiers();
        return bInfo;
    }  
}