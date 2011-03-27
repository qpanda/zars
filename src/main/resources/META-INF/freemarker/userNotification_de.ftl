<#if notificationType = "NOTIFICATION_USER_ADD">
Der folgende Benutzer wurde vom Administrator für Sie für die Verwendung von ZARS angelegt.

 ID: ${userBean.userId}
 Benutzername: ${userBean.username}
 Passwort: ${userBean.password}
 Email Adresse: ${userBean.emailAddress}
 Vorname: ${userBean.firstName}
 Nachname: ${userBean.lastName}
 Rollen: <#list userBean.roles as role><#if role.name = "ROLE_USER">Benutzer<#elseif role.name = "ROLE_ADMIN">Administrator<#elseif role.name = "ROLE_ACCOUNTANT">Verrechner</#if><#if role_has_next>, </#if></#list>

Um ZARS zu verwenden melden Sie sich bitte mit dem angegebenen Benutzernamen und Passwort unter folgendem Link an.
http://${notificationDomain}/

Das Passwort sollte bei erster Verwendung unter 'Einstellungen - Passwort Ändern' geändert werden.
http://${notificationDomain}/views/changePassword.jsf

Der Empfang von ZARS Benachrichtigungen kann unter 'Einstellungen - Preferenzen Ändern' aktiviert und deaktiviert werden.
http://${notificationDomain}/views/changePreferences.jsf
<#elseif notificationType = "NOTIFICATION_USER_UPDATE">
Der folgende Benutzer und / oder dessen Preferenzen wurde vom Administrator aktualisiert.

 ID: ${userBean.userId}
 Benutzername: ${userBean.username}
 Email Adresse: ${userBean.emailAddress}
 Vorname: ${userBean.firstName}
 Nachname: ${userBean.lastName}
 Rollen: <#list userBean.roles as role><#if role.name = "ROLE_USER">Benutzer<#elseif role.name = "ROLE_ADMIN">Administrator<#elseif role.name = "ROLE_ACCOUNTANT">Verrechner</#if><#if role_has_next>, </#if></#list>
<#elseif notificationType = "NOTIFICATION_USER_RESET">
Der folgende Benutzer wurde vom Administrator zurücksetzen.

 ID: ${userBean.userId}
 Benutzername: ${userBean.username}
 Passwort: ${userBean.password}
 Email Adresse: ${userBean.emailAddress}
 Vorname: ${userBean.firstName}
 Nachname: ${userBean.lastName}
 Rollen: <#list userBean.roles as role><#if role.name = "ROLE_USER">Benutzer<#elseif role.name = "ROLE_ADMIN">Administrator<#elseif role.name = "ROLE_ACCOUNTANT">Verrechner</#if><#if role_has_next>, </#if></#list>  

Um ZARS verwenden zu können melden Sie sich bitte mit dem zurücksetzen Benutzernamen und Passwort unter folgendem Link an.
http://${notificationDomain}/

Das Passwort sollte bei erster Verwendung unter 'Einstellungen - Passwort Ändern' geändert werden.
http://${notificationDomain}/views/changePassword.jsf

Der Empfang von ZARS Benachrichtigungen kann unter 'Einstellungen - Preferenzen Ändern' aktiviert und deaktiviert werden.
http://${notificationDomain}/views/changePreferences.jsf
<#elseif notificationType = "NOTIFICATION_USER_ENABLE">
Der folgende Benutzer wurde vom Administrator aktiviert.

 ID: ${userBean.userId}
 Benutzername: ${userBean.username}
 Email Adresse: ${userBean.emailAddress}
 Vorname: ${userBean.firstName}
 Nachname: ${userBean.lastName}
 Rollen: <#list userBean.roles as role><#if role.name = "ROLE_USER">Benutzer<#elseif role.name = "ROLE_ADMIN">Administrator<#elseif role.name = "ROLE_ACCOUNTANT">Verrechner</#if><#if role_has_next>, </#if></#list>

Der Empfang von ZARS Benachrichtigungen kann unter 'Einstellungen - Preferenzen Ändern' aktiviert und deaktiviert werden.
http://${notificationDomain}/views/changePreferences.jsf
<#elseif notificationType = "NOTIFICATION_USER_DISABLE">
Der folgende Benutzer wurde vom Administrator deaktiviert.

 ID: ${userBean.userId}
 Benutzername: ${userBean.username}
 Email Adresse: ${userBean.emailAddress}
 Vorname: ${userBean.firstName}
 Nachname: ${userBean.lastName}
 Rollen: <#list userBean.roles as role><#if role.name = "ROLE_USER">Benutzer<#elseif role.name = "ROLE_ADMIN">Administrator<#elseif role.name = "ROLE_ACCOUNTANT">Verrechner</#if><#if role_has_next>, </#if></#list>
</#if>
