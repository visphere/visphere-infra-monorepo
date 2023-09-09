/*
 * Copyright (c) 2023 by MILOSZ GILGA <https://miloszgilga.pl>
 * Silesian University of Technology
 *
 *     File name: ExceptionsListener.java
 *     Last modified: 9/3/23, 7:12 PM
 *     Project name: moonsphere-infra-monorepo
 *     Module name: msph-misc-service
 *
 * This project is a part of "MoonSphere" instant messenger system. This system is a part of
 * completing an engineers degree in computer science at Silesian University of Technology.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this
 * file except in compliance with the License. You may obtain a copy of the License at
 *
 *     <http://www.apache.org/license/LICENSE-2.0>
 *
 * Unless required by applicable law or agreed to in writing, software distributed under
 * the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
 * OF ANY KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the license.
 */

package pl.moonsphere.misc.exception;

import org.springframework.web.bind.annotation.RestControllerAdvice;
import pl.moonsphere.lib.exception.AbstractBaseExceptionListener;
import pl.moonsphere.lib.i18n.I18nService;

@RestControllerAdvice
public class ExceptionsListener extends AbstractBaseExceptionListener {
    public ExceptionsListener(I18nService i18nService) {
        super(i18nService);
    }
}