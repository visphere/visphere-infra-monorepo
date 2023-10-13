/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.lib;

import org.springframework.core.env.Environment;

public interface ISpringProp {
    String getKey(Environment environment);
}
