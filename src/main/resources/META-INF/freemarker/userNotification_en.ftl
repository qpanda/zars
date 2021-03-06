<#if notificationType = "NOTIFICATION_USER_ADD">
The administrator created the following user for you for ZARS.

 ID: ${userBean.userId}
 Username: ${userBean.username}
 Password: ${userBean.password}
 Email Address: ${userBean.emailAddress}
 First Name: ${userBean.firstName}
 Last Name: ${userBean.lastName}
 Roles: <#list userBean.roles as role><#if role.name = "ROLE_USER">User<#elseif role.name = "ROLE_ADMIN">Administrator<#elseif role.name = "ROLE_ACCOUNTANT">Accountant</#if><#if role_has_next>, </#if></#list>

To start using ZARS login with the specified username and password using the following link.
http://${notificationDomain}/

The password should be changed under 'Settings - Change Password' when using the application the first time.
http://${notificationDomain}/views/changePassword.jsf

Receiving ZARS Notifications can be enabled and disabled under 'Settings - Change Preferences'.
http://${notificationDomain}/views/changePreferences.jsf
<#elseif notificationType = "NOTIFICATION_USER_UPDATE">
The following user and / or the preferences of the user have been updated by the administrator.

 ID: ${userBean.userId}
 Username: ${userBean.username}
 Email Address: ${userBean.emailAddress}
 First Name: ${userBean.firstName}
 Last Name: ${userBean.lastName}
 Roles: <#list userBean.roles as role><#if role.name = "ROLE_USER">User<#elseif role.name = "ROLE_ADMIN">Administrator<#elseif role.name = "ROLE_ACCOUNTANT">Accountant</#if><#if role_has_next>, </#if></#list>

Receiving ZARS Notifications can be enabled and disabled under 'Settings - Change Preferences'.
http://${notificationDomain}/views/changePreferences.jsf  
<#elseif notificationType = "NOTIFICATION_USER_RESET">
The following user was reset by the administrator.

 ID: ${userBean.userId}
 Username: ${userBean.username}
 Password: ${userBean.password}
 Email Address: ${userBean.emailAddress}
 First Name: ${userBean.firstName}
 Last Name: ${userBean.lastName}
 Roles: <#list userBean.roles as role><#if role.name = "ROLE_USER">User<#elseif role.name = "ROLE_ADMIN">Administrator<#elseif role.name = "ROLE_ACCOUNTANT">Accountant</#if><#if role_has_next>, </#if></#list>  

To use ZARS login with the reset username and password using the following link.
http://${notificationDomain}/

The password should be changed under 'Settings - Change Password' when using the application the first time.
http://${notificationDomain}/views/changePassword.jsf

Receiving ZARS Notifications can be enabled and disabled under 'Settings - Change Preferences'.
http://${notificationDomain}/views/changePreferences.jsf
<#elseif notificationType = "NOTIFICATION_USER_ENABLE">
The following user was enabled by the administrator.

 ID: ${userBean.userId}
 Username: ${userBean.username}
 Password: ${userBean.password}
 Email Address: ${userBean.emailAddress}
 First Name: ${userBean.firstName}
 Last Name: ${userBean.lastName}
 Roles: <#list userBean.roles as role><#if role.name = "ROLE_USER">User<#elseif role.name = "ROLE_ADMIN">Administrator<#elseif role.name = "ROLE_ACCOUNTANT">Accountant</#if><#if role_has_next>, </#if></#list>

Receiving ZARS Notifications can be enabled and disabled under 'Settings - Change Preferences'.
http://${notificationDomain}/views/changePreferences.jsf
<#elseif notificationType = "NOTIFICATION_USER_DISABLE">
The following user was disabled by the administrator.

 ID: ${userBean.userId}
 Username: ${userBean.username}
 Password: ${userBean.password}
 Email Address: ${userBean.emailAddress}
 First Name: ${userBean.firstName}
 Last Name: ${userBean.lastName}
 Roles: <#list userBean.roles as role><#if role.name = "ROLE_USER">User<#elseif role.name = "ROLE_ADMIN">Administrator<#elseif role.name = "ROLE_ACCOUNTANT">Accountant</#if><#if role_has_next>, </#if></#list>
</#if>
