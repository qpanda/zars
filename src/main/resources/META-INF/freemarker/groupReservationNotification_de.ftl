Die folgende Reservierung wurde <#if operationType = "OPERATION_ADD">angelegt<#elseif operationType = "OPERATION_UPDATE">aktualisiert<#elseif operationType = "OPERATION_DELETE">geloescht</#if>:

 ID: ${groupReservationBean.groupReservationId}
 Buchungsdatum: ${groupReservationBean.booked?datetime}
 Buchungsperson: <#if groupReservationBean.booker.firstName?has_content && groupReservationBean.booker.lastName?has_content>${groupReservationBean.booker.username} (${groupReservationBean.booker.firstName} ${groupReservationBean.booker.lastName})<#elseif groupReservationBean.booker.emailAddress?has_content>${groupReservationBean.booker.username} <${groupReservationBean.booker.emailAddress}><#else>${groupReservationBean.booker.username}</#if>
 Ankunft: ${groupReservationBean.arrival?date}
 Abfahrt: ${groupReservationBean.departure?date}
 Gruppenverantwortlicher: <#if groupReservationBean.beneficiary.firstName?has_content && groupReservationBean.beneficiary.lastName?has_content>${groupReservationBean.beneficiary.username} (${groupReservationBean.beneficiary.firstName} ${groupReservationBean.beneficiary.lastName})<#elseif groupReservationBean.beneficiary.emailAddress?has_content>${groupReservationBean.beneficiary.username} <${groupReservationBean.beneficiary.emailAddress}><#else>${groupReservationBean.beneficiary.username}</#if>
 Verrechnungsperson: <#if groupReservationBean.accountant.firstName?has_content && groupReservationBean.accountant.lastName?has_content>${groupReservationBean.accountant.username} (${groupReservationBean.accountant.firstName} ${groupReservationBean.accountant.lastName})<#elseif groupReservationBean.accountant.emailAddress?has_content>${groupReservationBean.accountant.username} <${groupReservationBean.accountant.emailAddress}><#else>${groupReservationBean.accountant.username}</#if>
 Gäste: ${groupReservationBean.guests}

<#if operationType = "OPERATION_ADD" || operationType = "OPERATION_UPDATE">
Die Reservierung kann online unter http://zars.soomsam.net/views/viewGroupReservation.jsf?groupReservationId=${groupReservationBean.groupReservationId} abgerufen werden
</#if>

Dies ist eine automatisierte Email, bitte antworten Sie nicht auf diese Email.