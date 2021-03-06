package greatreviews.grw.enums;

public enum PatternEnum {

//enums go here
//======================================================================================================================


//     VALID_EMAIL_REGEX(Constants.EMAIL_PATTERN),

    //username must be 1 to 15 chars alphanumeric and can contain _@! special symbols

//    VALID_USERNAME_REGEX(Constants.USERNAME_PATTERN),

    //password must be 7 to 20 chars alphanumeric, with at least one digit, can contain !@#$% special symbols
    //it also cannot start with digit, underscore or special symbol

//    VALID_PASSWORD_REGEX(Constants.PASSWORD_PATTERN),

//    BIRTH_DATE_FORMAT(Constants.BIRTH_DATE_PATTERN),


    ;
//======================================================================================================================


    private final String pattern;

    private PatternEnum(String pattern) {
        this.pattern = pattern;
    }

    @Override
    public String toString() {
        return pattern;
    }

    public static class Constants {
        public static final String EMAIL_PATTERN = "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])";
        public static final String USERNAME_PATTERN = "^([a-zA-Z0-9_@!]{4,15})$";
        public static final String PASSWORD_PATTERN = "^(?=[^\\d_].*?\\d)\\w(\\w|[!@#$%]){5,20}";
        public static final String BIRTH_DATE_PATTERN = "yyyy-MM-dd";
        public static final String VALID_URL_PATTERN = "[(http(s)?):\\/\\/(www\\.)?a-zA-Z0-9@:%._\\+~#=]{2,256}\\.[a-z]{2,6}\\b([-a-zA-Z0-9@:%_\\+.~#?&//=]*)";
        public static final String VALID_FQDN_PATTERN = "^(?=.{1,254}$)((?=[a-z0-9-]{1,63}\\.)(xn--+)?[a-z0-9]+(-[a-z0-9]+)*\\.)+[a-z]{2,63}$";
    }
}
