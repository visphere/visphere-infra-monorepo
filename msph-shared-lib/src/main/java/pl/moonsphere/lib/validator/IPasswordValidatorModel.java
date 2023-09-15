/*
 * Copyright (c) 2023 by MoonSphere Systems
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.moonsphere.lib.validator;

public interface IPasswordValidatorModel {
    String getPassword();
    String getConfirmedPassword();
}
