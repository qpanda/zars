Die folgende Reservierung wurde <#if notificationType = "NOTIFICATION_GROUPRESERVATION_ADD">angelegt<#elseif notificationType = "NOTIFICATION_GROUPRESERVATION_UPDATE">aktualisiert<#elseif notificationType = "NOTIFICATION_GROUPRESERVATION_DELETE">gelöscht</#if>.

 ID: ${groupReservationBean.groupReservationId}
 Buchungsdatum: ${groupReservationBean.booked?datetime}
 Buchungsperson: <#if groupReservationBean.booker.firstName?has_content && groupReservationBean.booker.lastName?has_content>${groupReservationBean.booker.username} (${groupReservationBean.booker.firstName} ${groupReservationBean.booker.lastName})<#elseif groupReservationBean.booker.emailAddress?has_content>${groupReservationBean.booker.username} <${groupReservationBean.booker.emailAddress}><#else>${groupReservationBean.booker.username}</#if>
 Ankunft: ${groupReservationBean.arrival?date}
 Abfahrt: ${groupReservationBean.departure?date}
 Gruppenverantwortlicher: <#if groupReservationBean.beneficiary.firstName?has_content && groupReservationBean.beneficiary.lastName?has_content>${groupReservationBean.beneficiary.username} (${groupReservationBean.beneficiary.firstName} ${groupReservationBean.beneficiary.lastName})<#elseif groupReservationBean.beneficiary.emailAddress?has_content>${groupReservationBean.beneficiary.username} <${groupReservationBean.beneficiary.emailAddress}><#else>${groupReservationBean.beneficiary.username}</#if>
 Verrechnungsperson: <#if groupReservationBean.accountant.firstName?has_content && groupReservationBean.accountant.lastName?has_content>${groupReservationBean.accountant.username} (${groupReservationBean.accountant.firstName} ${groupReservationBean.accountant.lastName})<#elseif groupReservationBean.accountant.emailAddress?has_content>${groupReservationBean.accountant.username} <${groupReservationBean.accountant.emailAddress}><#else>${groupReservationBean.accountant.username}</#if>
 Gäste: ${groupReservationBean.guests}
<#if groupReservationBean.reservations?has_content>

 Individual Reservierungen
   Ankunft		Abfahrt			Person
<#list groupReservationBean.reservations as reservation>
   ${reservation.arrival?date}		${reservation.departure?date}		${reservation.firstName} ${reservation.lastName}
</#list>  
</#if>
<#if notificationType = "NOTIFICATION_GROUPRESERVATION_ADD" || notificationType = "NOTIFICATION_GROUPRESERVATION_UPDATE">

Die Reservierung kann mit folgendem Link abgerufen werden.
http://${notificationDomain}/views/viewGroupReservation.jsf?groupReservationId=${groupReservationBean.groupReservationId}
</#if>

Der Empfang von ZARS Benachrichtigungen kann unter 'Einstellungen - Preferenzen Ändern' aktiviert und deaktiviert werden.
http://${notificationDomain}/views/changePreferences.jsf