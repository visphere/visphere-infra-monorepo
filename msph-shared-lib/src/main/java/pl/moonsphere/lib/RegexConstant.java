/*
 * Copyright (c) 2023 by MoonSphere Systems
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.moonsphere.lib;

public class RegexConstant {
    public static final String USERNAME_REQ = "^[a-z\\d_-]+$";
    public static final String USERNAME_OR_EMAIL_REQ = "^(?![_.-])\\b(?![.-])[\\w.-]*(_\\w*)?\\b(@[a-z0-9]+([-_.][a-z0-9]+)*\\.[a-z]{2,100})?$";
    public static final String PASSWORD_REQ = "^(?=.*\\d)(?=.*[!@#$%^&*])(?=.*[a-z])(?=.*[A-Z]).{8,80}$";
    public static final String BIRTH_DATE_REQ = "^(0[1-9]|[12][0-9]|3[01])/(0[1-9]|1[0-2])/\\d{4}$";

    private RegexConstant() {
    }
}
