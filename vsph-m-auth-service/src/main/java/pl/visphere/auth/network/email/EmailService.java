/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.auth.network.email;

import pl.visphere.auth.network.email.dto.EmailAddressReqDto;
import pl.visphere.auth.network.email.dto.SecondEmailAddressReqDto;
import pl.visphere.lib.BaseMessageResDto;
import pl.visphere.lib.security.user.AuthUserDetails;

interface EmailService {
    BaseMessageResDto requestUpdateFirstEmailAdrress(EmailAddressReqDto reqDto, AuthUserDetails user);
    BaseMessageResDto requestResendUpdateFirstEmailAdrress(EmailAddressReqDto reqDto, AuthUserDetails user);
    BaseMessageResDto requestUpdateSecondEmailAdrress(SecondEmailAddressReqDto reqDto, AuthUserDetails user);
    BaseMessageResDto requestResendUpdateSecondEmailAdrress(SecondEmailAddressReqDto reqDto, AuthUserDetails user);
    BaseMessageResDto updateFirstEmailAddress(EmailAddressReqDto reqDto, String token, AuthUserDetails user);
    BaseMessageResDto updateSecondEmailAddress(SecondEmailAddressReqDto reqDto, String token, AuthUserDetails user);
    BaseMessageResDto deleteSecondEmailAddress(AuthUserDetails user);
}
