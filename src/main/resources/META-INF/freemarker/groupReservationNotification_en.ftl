The following reservation has been <#if operationType = "OPERATION_ADD">created<#elseif operationType = "OPERATION_UPDATE">updated<#elseif operationType = "OPERATION_DELETE">deleted</#if>:

 ID: ${groupReservationBean.groupReservationId}
 Booking Date: ${groupReservationBean.booked?datetime}
 Booker: <#if groupReservationBean.booker.firstName?has_content && groupReservationBean.booker.lastName?has_content>${groupReservationBean.booker.username} (${groupReservationBean.booker.firstName} ${groupReservationBean.booker.lastName})<#elseif groupReservationBean.booker.emailAddress?has_content>${groupReservationBean.booker.username} <${groupReservationBean.booker.emailAddress}><#else>${groupReservationBean.booker.username}</#if> 
 Arrival: ${groupReservationBean.arrival?date}
 Departure: ${groupReservationBean.departure?date}
 Group Organizer: <#if groupReservationBean.beneficiary.firstName?has_content && groupReservationBean.beneficiary.lastName?has_content>${groupReservationBean.beneficiary.username} (${groupReservationBean.beneficiary.firstName} ${groupReservationBean.beneficiary.lastName})<#elseif groupReservationBean.beneficiary.emailAddress?has_content>${groupReservationBean.beneficiary.username} <${groupReservationBean.beneficiary.emailAddress}><#else>${groupReservationBean.beneficiary.username}</#if>
 Accountant: <#if groupReservationBean.accountant.firstName?has_content && groupReservationBean.accountant.lastName?has_content>${groupReservationBean.accountant.username} (${groupReservationBean.accountant.firstName} ${groupReservationBean.accountant.lastName})<#elseif groupReservationBean.accountant.emailAddress?has_content>${groupReservationBean.accountant.username} <${groupReservationBean.accountant.emailAddress}><#else>${groupReservationBean.accountant.username}</#if>
 Guests: ${groupReservationBean.guests}
 
<#if operationType = "OPERATION_ADD" || operationType = "OPERATION_UPDATE">
The reservation can be accessed online at http://zars.soomsam.net/views/viewGroupReservation.jsf?groupReservationId=${groupReservationBean.groupReservationId}
</#if>

Dies ist eine automatisierte Email, bitte antworten Sie nicht auf diese Email.