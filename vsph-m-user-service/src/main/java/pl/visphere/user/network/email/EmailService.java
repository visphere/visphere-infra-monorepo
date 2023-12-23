/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.user.network.email;

import pl.visphere.lib.BaseMessageResDto;
import pl.visphere.lib.security.user.AuthUserDetails;
import pl.visphere.user.network.email.dto.EmailAddressReqDto;
import pl.visphere.user.network.email.dto.SecondEmailAddressReqDto;

interface EmailService {
    BaseMessageResDto requestUpdateFirstEmailAdrress(EmailAddressReqDto reqDto, AuthUserDetails user);
    BaseMessageResDto requestResendUpdateFirstEmailAdrress(EmailAddressReqDto reqDto, AuthUserDetails user);
    BaseMessageResDto requestUpdateSecondEmailAdrress(SecondEmailAddressReqDto reqDto, AuthUserDetails user);
    BaseMessageResDto requestResendUpdateSecondEmailAdrress(SecondEmailAddressReqDto reqDto, AuthUserDetails user);
    BaseMessageResDto updateFirstEmailAddress(EmailAddressReqDto reqDto, String token, AuthUserDetails user);
    BaseMessageResDto updateSecondEmailAddress(SecondEmailAddressReqDto reqDto, String token, AuthUserDetails user);
    BaseMessageResDto deleteSecondEmailAddress(AuthUserDetails user);
}
