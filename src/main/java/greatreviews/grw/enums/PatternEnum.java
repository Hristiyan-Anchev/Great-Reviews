package greatreviews.grw.enums;

import javax.validation.constraints.PastOrPresent;
import java.text.SimpleDateFormat;

public enum PatternEnum {

//enums go here
//======================================================================================================================

    VALID_EMAIL_REGEX("(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])"),
    //username must be 1 to 15 chars alphanumeric and can contain _@! special symbols
    VALID_USERNAME_REGEX("^([a-zA-Z0-9_@!]{1,15})$"),
    //password must be 7 to 20 chars alphanumeric, with at least one digit, can contain !@#$% special symbols
    //it also cannot start with digit, underscore or special symbol
    VALID_PASSWORD_REGEX("^(?=[^\\d_].*?\\d)\\w(\\w|[!@#$%]){7,20}")
    ;
//======================================================================================================================


    String pattern;

    private PatternEnum(String pattern){
        this.pattern = pattern;
    }

    @Override
    public String toString() {
        return this.pattern;
    }
}
