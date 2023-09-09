/*
 * Copyright (c) 2023 by MILOSZ GILGA <https://miloszgilga.pl>
 * Silesian University of Technology
 *
 *     File name: MessageExceptionResDto.java
 *     Last modified: 9/3/23, 7:57 PM
 *     Project name: moonsphere-infra-monorepo
 *     Module name: msph-shared-lib
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
package pl.moonsphere.lib.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class MessageExceptionResDto extends AbstractServerExceptionResDto {
    private final String message;

    public MessageExceptionResDto(HttpStatus httpStatus, HttpServletRequest req, String message) {
        super(httpStatus, req);
        this.message = message;
    }
}