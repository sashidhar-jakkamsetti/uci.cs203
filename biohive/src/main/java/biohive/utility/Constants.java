package biohive.utility;

public class Constants
{
    public static final Integer NUMBER_OF_MINUTIAE = 15;
    public static final Integer NUMBER_OF_CHAFF = 200;
    
    public static final Integer BITS_OF_X_MINUTIAE = 9;
    public static final Integer CAP_BITS_OF_X_MINUTIAE = 5;
    public static final Integer BITS_OF_Y_MINUTIAE = 9;
    public static final Integer CAP_BITS_OF_Y_MINUTIAE = 5;
    public static final Integer BITS_OF_O_MINUTIAE = 9;
    public static final Integer CAP_BITS_OF_O_MINUTIAE = 6;

    public static final Integer FIELD_ORDER_16 = 65537;
    public static final Integer FIELD_ORDER_5 = 31;
    public static final Integer FIELD_ORDER_6 = 61;
    public static final Integer FIELD_OREDER_NUM_OF_BYTES = 2;
    public static final Integer POLY_DEGREE = 6;

    public static final Integer NUMBER_OF_HONEY_VAULTS = 9;

    public static final Double STD_DEV_1 = 0.68;
    public static final Double STD_DEV_2 = 0.95;

    public static final String DATABASE_BIO_FILENAME = "bio.db";
    public static final String DATABASE_HONEY_FILENAME = "honey.db";
    
    public static final String ACTION_REGISTER = "reg";
    public static final String ACTION_AUTHENTICATE = "auth";
    public static final String ACTION_ATTACK = "attack";

    public static final Integer ALARM_EXIT_STATUS = -1;
}