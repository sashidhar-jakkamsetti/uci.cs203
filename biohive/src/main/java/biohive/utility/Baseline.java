package biohive.utility;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="baseline")
public class Baseline
{
    public enum OpMode
    {
        reg,
        auth,
        attack
    }

    public String in_fingerprint;
    public String out_fingerprintAligned;
    public String out_minutiae;
    public String biodb;
    public String honeydb;
    public String userId;
    public OpMode mode;

    String minutiae_extractor;
    String tarp_location;
    String mindtct_location;
    String fingerprint_raw;
    String fingerprint_aligned;
    String minutiae;
    String minutiae_probdist;
    String database;
    String fingerprint;
    String action;
    Boolean clearDb;

    public String getMinutiae_extractor() 
    {
        return minutiae_extractor;
    }

    @XmlElement
    public void setMinutiae_extractor(String minutiae_extractor) 
    {
        this.minutiae_extractor = minutiae_extractor;
    }

    public String getTarp_location() 
    {
        return tarp_location;
    }

    @XmlElement
    public void setTarp_location(String tarp_location) 
    {
        this.tarp_location = tarp_location;
    }

    public String getMindtct_location() 
    {
        return mindtct_location;
    }

    @XmlElement
    public void setMindtct_location(String mindtct_location) 
    {
        this.mindtct_location = mindtct_location;
    }

    public String getFingerprint_raw() 
    {
        return fingerprint_raw;
    }

    @XmlElement
    public void setFingerprint_raw(String fingerprint_raw) 
    {
        this.fingerprint_raw = fingerprint_raw;
    }

    public String getFingerprint_aligned() 
    {
        return fingerprint_aligned;
    }

    @XmlElement
    public void setFingerprint_aligned(String fingerprint_aligned) 
    {
        this.fingerprint_aligned = fingerprint_aligned;
    }

    public String getMinutiae() 
    {
        return minutiae;
    }

    @XmlElement
    public void setMinutiae(String minutiae) 
    {
        this.minutiae = minutiae;
    }

    public String getFingerprint() 
    {
        return fingerprint;
    }

    @XmlElement
    public void setFingerprint(String fingerprint) 
    {
        this.fingerprint = fingerprint;
    }

    public String getDatabase() 
    {
        return database;
    }

    @XmlElement
    public void setDatabase(String database) 
    {
        this.database = database;
    }

    public String getAction() 
    {
        return action;
    }

    @XmlElement
    public void setAction(String action) 
    {
        this.action = action;
    }

    public Boolean getClearDb() 
    {
        return clearDb;
    }

    @XmlElement
    public void setClearDb(Boolean clearDb) 
    {
        this.clearDb = clearDb;
    }

    public String getMinutiae_probdist() 
    {
        return minutiae_probdist;
    }

    @XmlElement
    public void setMinutiae_probdist(String minutiae_probdist) 
    {
        this.minutiae_probdist = minutiae_probdist;
    }

    public void prepareOutputIdentifiers()
    {
        String infileTag = fingerprint.replace(".tif", "").replace(".jpeg", "").replace(".png", "");

        userId = infileTag.split("_")[0];
        in_fingerprint = String.format("%s/%s", fingerprint_raw, fingerprint);
        out_minutiae = String.format("%s/%s", minutiae, infileTag);
        out_fingerprintAligned = String.format("%s/%s.aligned.jpeg", fingerprint_aligned, infileTag);
        biodb = String.format("%s%s", database, Constants.DATABASE_BIO_FILENAME);
        honeydb = String.format("%s/%s", database, Constants.DATABASE_HONEY_FILENAME);

        if(action.equals(Constants.ACTION_REGISTER))
        {
            mode = OpMode.reg;
        }
        else if(action.equals(Constants.ACTION_AUTHENTICATE))
        {
            mode = OpMode.auth;
        }
        else
        {
            mode = OpMode.attack;
        }
    }
}
