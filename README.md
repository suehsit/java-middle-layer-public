MIT License
Copyright (c) 2017 The Board of Trustees of the Leland Stanford Junior University
Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

# Java Middle Layer (JML)

The Java Middle Layer (JML), also know as the Stanford Middle Layer, is an extensible, scalable and flexible middle layer framework that runs in J2EE-compliant containers.

## Architecture

The architecture of JML is a modified MVC implementation where the view, controller (controller, security and access adapters), and model components of JML works together as separate entities.

The following components are implemented in the current version:

-   Views
    -   JSON
    -   XML
    -   Simple HTML

-   Model functions
    -   Administration
    -   Log out a user remotely
    -   View users currently logged in
    -   Get Session and Request Information
    -   Execute Database Query
    -   Run stored procedure
    -   Login and Logout
    -   Echo and ping functionality

-   Controller
    -   Access adapters: HTTP Servlet, JSP Adapter and Java Standard Tag Library (JSTL)
    -   Security plug-ins: No Login, Shibboleth, WebAuth

The JML architecture can be run as a generic server (**Server JML**) that serves multiple web applications, mostly via the HTTP Servlet adapter or as part of a web application (**Embedded JML**), here including JSP applications. The concept of Embedded JML was introduced in version 2.0 and will replace the jml\_i00 servers.

| JML Model | Sample applications |
|---|---|
| Server JML | HTTP(S) queries from Office applications HTTP(S) queries from PHP and JSP applications |
| Embedded JML | JSP applications using the JML/JSP tag library |

-------

### Files

```
web-app/WEB-INF/web.xml
web-app/WEB-INF/conf/accounts.xml
web-app/WEB-INF/conf/log4j-configuration.xml
web-app/WEB-INF/lib/activation-1.0.2.jar
web-app/WEB-INF/lib/asm-1.0.2.jar
web-app/WEB-INF/lib/catalina.jar
web-app/WEB-INF/lib/commons-codec-1.10.jar
web-app/WEB-INF/lib/commons-discovery.jar
web-app/WEB-INF/lib/commons-lang3-3.4.jar
web-app/WEB-INF/lib/commons-logging.jar
web-app/WEB-INF/lib/commons-net-1.4.0.jar
web-app/WEB-INF/lib/cryptoj.jar
web-app/WEB-INF/lib/javax.servlet\_1.0.0.0\_2-5.jar
web-app/WEB-INF/lib/json-20141113.jar
web-app/WEB-INF/lib/json-smart-2.1.1.jar
web-app/WEB-INF/lib/jsp-api.jar
web-app/WEB-INF/lib/log4j-1.2.17.jar
web-app/WEB-INF/lib/log4j-api-2.3.jar
web-app/WEB-INF/lib/log4j-core-2.3.jar
web-app/WEB-INF/lib/mailapi.jar
web-app/WEB-INF/lib/odbc6.jar
web-app/WEB-INF/lib/pop.jar
web-app/WEB-INF/lib/servlet-api.jar
web-app/WEB-INF/lib/smtp.jar
web-app/WEB-INF/lib/tomcat-api.jar
web-app/WEB-INF/lib/xercesImpl.jar
```

### Web Application Descriptor

#### Server JML

```
<?xml version="1.0" encoding="ISO-8859-1"?>
<!DOCTYPE web-app
    PUBLIC "-//Sun Microsystems, Inc.//DTD Web Application 2.3//EN"
    "http://java.sun.com/dtd/web-app\_2\_3.dtd">
<web-app>
    <filter>
        <filter-name>ClickjackFilterDeny</filter-name>
        <filter-class>com.merchant.filters.ClickjackFilter</filter-class>
        <init-param>
            <param-name>mode</param-name>
            <param-value>DENY</param-value>
        </init-param>
    </filter>
    <filter>
        <filter-name>ClickjackFilterSameOrigin</filter-name>
        <filter-class>com.merchant.filters.ClickjackFilter</filter-class>
        <init-param>
            <param-name>mode</param-name>
            <param-value>SAMEORIGIN</param-value>
        </init-param>
    </filter>

    <!-- use the Deny version to prevent anyone, including yourself, from framing the page -->
    <!--filter-mapping>
        <filter-name>ClickjackFilterDeny</filter-name>
        <url-pattern>/\*</url-pattern>
    </filter-mapping-->
    
    <!-- use the SameOrigin version to allow your application to frame, but nobody else -->
    <display-name>Java Middle Layer (JML)</display-name>
    <description>My Company</description>
    <servlet>
        <servlet-name>log4j-init</servlet-name>
        <servlet-class>edu.stanford.ehs.jml.core.model.Log4jInit</servlet-class>
        <init-param>
            <param-name>log4j-init-file</param-name>
            <param-value>WEB-INF/conf/log4j-configuration.xml</param-value>
        </init-param>
        <load-on-startup>1</load-on-startup>
    </servlet>
    <servlet>
        <servlet-name>server</servlet-name>
        <servlet-class>edu.stanford.ehs.jml.core.model.Server</servlet-class>
        <init-param>
            <param-name>config-file</param-name>
            <param-value>WEB-INF/conf/jml-server.xml</param-value>
        </init-param>
    <init-param>
        <param-name>accounts-file</param-name>
        <param-value>WEB-INF/conf/accounts.xml</param-value>
    </init-param>
    <init-param>
        <param-name>min-thread-pool</param-name>
        <param-value>5</param-value>
    </init-param>
    <init-param>
        <param-name>max-thread-pool</param-name>
        <param-value>20</param-value>
    </init-param>
    <load-on-startup>2</load-on-startup>
</servlet>
<servlet>
    <servlet-name>jml</servlet-name>
    <servlet-class>edu.stanford.ehs.jml.core.controller.adapters.Servlet</servlet-class>
    <load-on-startup>3</load-on-startup>
</servlet>
<servlet>
    <servlet-name>SecurityManagerGC</servlet-name>
    <servlet-class>edu.stanford.ehs.jml.security.model.SecurityManagerGC</servlet-class>
    <init-param>
        <param-name>delay</param-name>
        <param-value>240</param-value>
        <description>The frequency in minutes where the garbage collection shall take place</description>
    </init-param>
    <load-on-startup>3</load-on-startup>
</servlet>
<servlet>
    <servlet-name>DatabasePing</servlet-name>
    <servlet-class>edu.stanford.ehs.jml.database.model.DatabasePing</servlet-class>
    <init-param>
        <param-name>active</param-name> 
        <param-value>false</param-value>    
        <description></description>
    </init-param>
    <init-param>
        <param-name>delay</param-name>
        <param-value>5</param-value>
        <description>The frequency in minutes where the all database accounts will be pinged</description>
    </init-param>
    <init-param>
        <param-name>active</param-name>
        <param-value>true</param-value>
        <description>Determine whether to activate the database pinger or not</description>
    </init-param>
    <init-param>
        <param-name>query</param-name>
        <param-value>SELECT SYSDATE FROM DUAL</param-value>
        <description>The query to be run on each account</description>
    </init-param>
    <load-on-startup>3</load-on-startup>
</servlet>
<servlet-mapping>
    <servlet-name>jml</servlet-name>
    <url-pattern>/jml</url-pattern>
</servlet-mapping>
<servlet-mapping>
    <servlet-name>LoginHandler</servlet-name>
    <url-pattern>/servlet/LoginHandler</url-pattern>
</servlet-mapping>
```

#### Embedded JML

Ensure that the jml and LoginHandler servlets are disabled in the embedded JML model:

```
<?xml version="1.0" encoding="ISO-8859-1"?>
<!DOCTYPE web-app
PUBLIC "-//Sun Microsystems, Inc.//DTD Web Application 2.3//EN"
"http://java.sun.com/dtd/web-app\_2\_3.dtd">
<web-app>
    <display-name>Java Middle Layer (JML)</display-name>
    <description>My Company</description>
    <taglib>
        <taglib-uri>http://ehs.stanford.edu/taglibs/jml</taglib-uri>
        <taglib-location>/WEB-INF/tld/jml.tld</taglib-location>
        </taglib>
        <taglib>
            <taglib-uri>http://java.sun.com/jstl/core</taglib-uri>
            <taglib-location>/WEB-INF/tld/c.tld</taglib-location>
        </taglib>
        <taglib>
            <taglib-uri>http://java.sun.com/jstl/core\_rt</taglib-uri>
            <taglib-location>/WEB-INF/tld/c-rt.tld</taglib-location>
        </taglib>
        <taglib>
            <taglib-uri>http://java.sun.com/jstl/xml</taglib-uri>
            <taglib-location>/WEB-INF/tld/x.tld</taglib-location>
        </taglib>
        <taglib>
            <taglib-uri>http://java.sun.com/jstl/xml\_rt</taglib-uri>
            <taglib-location>/WEB-INF/tld/x-rt.tld</taglib-location>
        </taglib>
        <taglib>
            <taglib-uri>http://java.sun.com/jstl/fmt</taglib-uri>
            <taglib-location>/WEB-INF/tld/fmt.tld</taglib-location>
        </taglib>
        <taglib>
            <taglib-uri>http://java.sun.com/jstl/fmt\_rt</taglib-uri>
            <taglib-location>/WEB-INF/tld/fmt-rt.tld</taglib-location>
        </taglib>
        <taglib>
            <taglib-uri>http://java.sun.com/jstl/sql</taglib-uri>
            <taglib-location>/WEB-INF/tld/sql.tld</taglib-location>
        </taglib>
        <taglib>
            <taglib-uri>http://java.sun.com/jstl/sql\_rt</taglib-uri>
            <taglib-location>/WEB-INF/tld/sql-rt.tld</taglib-location>
        </taglib>
        <servlet>
            <servlet-name>log4j-init</servlet-name>
            <servlet-class>edu.stanford.ehs.jml.core.model.Log4jInit</servlet-class>
            <init-param>
                <param-name>log4j-init-file</param-name>
                <param-value>WEB-INF/conf/log4j-configuration.xml</param-value>
            </init-param>
            <load-on-startup>1</load-on-startup>
        </servlet>
        <servlet>
            <servlet-name>server</servlet-name>
            <servlet-class>edu.stanford.ehs.jml.core.model.Server</servlet-class>
            <init-param>
                <param-name>config-file</param-name>
                <param-value>WEB-INF/conf/jml-server.xml</param-value>
            </init-param>
            <init-param>
                <param-name>accounts-file</param-name>
                <param-value>WEB-INF/conf/accounts.xml</param-value>
            </init-param>
            <init-param>
                <param-name>min-thread-pool</param-name>
                <param-value>5</param-value>
            </init-param>
            <init-param>
                <param-name>max-thread-pool</param-name>
                <param-value>20</param-value>
            </init-param>
            <load-on-startup>2</load-on-startup>
        </servlet>
        <servlet>
            <servlet-name>SecurityManagerGC</servlet-name>
            <servlet-class>edu.stanford.ehs.jml.security.model.SecurityManagerGC</servlet-class>
            <init-param>
                <param-name>delay</param-name>
                <param-value>240</param-value>
                <description>The frequency in minutes where the garbage collection shall take place</description>
            </init-param>
            <load-on-startup>3</load-on-startup>
        </servlet>
    <servlet>
        <servlet-name>DatabasePing</servlet-name>
        <servlet-class>edu.stanford.ehs.jml.database.model.DatabasePing</servlet-class>
        <init-param>
            <param-name>active</param-name>
            <param-value>false</param-value>
            <description></description>
        </init-param>
        <init-param>
            <param-name>delay</param-name>
            <param-value>5</param-value>
            <description>The frequency in minutes where the all database accounts will be pinged</description>
        </init-param>
        <init-param>
            <param-name>active</param-name>
            <param-value>false</param-value>
            <description>Determine whether to activate the database pinger or not</description>
        </init-param>
        <init-param>
            <param-name>query</param-name>
            <param-value>SELECT SYSDATE FROM DUAL</param-value>
            <description>The query to be run on each account</description>
        </init-param>
        <load-on-startup>3</load-on-startup>
    </servlet>
</web-app>
```

## Logging

Most logging is managed by Log4J in the configuration file located at `WEB-INF\conf\log4j-configuration.xml` It is highly recommend to using the tail.exe command from Windows 2003 resource kit to continuously display the content of a file.

**The debug level DEBUG will deliver a vast amount of detailed information from the JML which might be useful during development. However, it is recommended that the debug level for production servers is INFO, WARN or ERROR.**

When the JML is running as a Windows service note that the file location in `log4j.appender.jmlLogFile.file` is relative to `C:\WINDOWS\SYSTEM32`

```
#log4j.rootLogger=DEBUG, jmlConsole, jmlLogFile, jmlRollingLogFile
#log4j.rootLogger=DEBUG, jmlRollingLogFile
log4j.rootLogger=DEBUG, jmlLogFile
log4j.appender.jmlConsole=org.apache.log4j.ConsoleAppender
log4j.appender.jmlConsole.layout=org.apache.log4j.PatternLayout
log4j.appender.jmlConsole.layout.ConversionPattern=%d %-5p %c - %m%n

#
# FILE LOGGING (Works well with continuous tailing during development)
#
log4j.appender.jmlLogFile=org.apache.log4j.FileAppender
log4j.appender.jmlLogFile.file=../logs/jml.log
log4j.appender.jmlLogFile.append=true
log4j.appender.jmlLogFile.layout=org.apache.log4j.PatternLayout
log4j.appender.jmlLogFile.layout.ConversionPattern=%d %-5p %c - %m%n

#
# ROLL-OVER FILE LOGGING (Works well without tailing during production)
#

log4j.appender.jmlRollingLogFile=biz.minaret.log4j.DatedFileAppender
log4j.appender.jmlRollingLogFile.append=true
log4j.appender.jmlRollingLogFile.layout=org.apache.log4j.PatternLayout
log4j.appender.jmlRollingLogFile.layout.ConversionPattern=%d %-5p %c - %m%n
log4j.appender.jmlRollingLogFile.Directory=logs
log4j.appender.jmlRollingLogFile.Prefix=jml-
log4j.appender.jmlRollingLogFile.Suffix=.log
log4j.appender.jmlRollingLogFile.Append=true

#
# BufferedIO: true to use a buffered output stream to the log file (improves
# performance when logging a lot of data but not so good if the system
# crashes or you want to watch the logs in real time) or false to write
# flush each message out to the log file.
log4j.appender.jmlRollingLogFile.BufferedIO=false
#log4j.appender.jmlRollingLogFile.BufferSize=8192
```

### Oracle JDBC Logging

The Oracle JDBC logging can be applied by inserting and customizing the following lines into `bin/Catalina.sh`:

```
JAVA_OPTS="$JAVA_OPTS "-Doracle.jdbc.Trace=true
JAVA_OPTS="$JAVA_OPTS "-Doracle.jdbc.LogFile=/opt/tomcat/logs/jdbc.log
JAVA_OPTS="$JAVA_OPTS "-Doracle.jdbc.LogVolume=10
JAVA_OPTS="$JAVA_OPTS "-Doracle.jdbc.PrintFields=all
```

## Security

The JML runs in one of three discrete and customizable security modes which is set in `WEB-INF\conf\jml-server.xml`.

A server running in `OnlyLocalRequest` mode will only allow requests deriving from the server’s IP addresses, here including 127.0.0.1, in addition to the URI’s specified in the `<local-uri-definition>` tag.

The mode `AllRestrictedActions` will only allow local and remote requests with the actions specified in the `<allowed-remote-actions>`

`PartialRestrictedActions` will allow all local requests as defined in OnlyLocalRequests and remote actions as defined in the `<allowed-remote-actions>` tag

```
<?xml version="1.0" encoding="UTF-8" ?>
<jml-server xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="jml-server-1.7.xsd">
<security-mode>OnlyLocalRequests</security-mode>
<!-- <security-mode>AllRestrictedActions</security-mode>
-->
<!--
<security-mode>PartialRestrictedActions</security-mode>
-->
    <allowed-remote-actions>
        <action>login</action>
        <action>logout</action>
        <action>doUserStoredProcedure</action>
        <action>doEcho</action>
    </allowed-remote-actions>
</jml-server>
```

**Please ensure that the web descriptor reflects the JML model chosen (Server or Embedded)**

### WebAuth

In order to integrate WebAuth with JML, the Tomcat server needs to be started up with the following JVM parameters, specified in `bin\catalina.sh`:

```
JAVA_OPTS="$JAVA_OPTS "-Djava.security.auth.login.config="$CATALINA_HOME"/webapps/myapp/WEB-INF/conf/loginMyApp.conf
JAVA_OPTS="$JAVA_OPTS "-Djava.security.krb5.conf=/etc/krb5.conf
JAVA_OPTS="$JAVA_OPTS "-Djava.security.sasl.qop=auth-conf
```

Sample content of `loginMyApp.conf`:

```
MYJML {
    com.sun.security.auth.module.Krb5LoginModule required useKeyTab=true
    debug=true keyTab="/etc/httpd/conf/webauth/keytab" doNotPrompt=true
    storeKey=true principal="webauth/server.domain.com "
    useTicketCache=true;
};
```

### Shibboleth

In order to integrate Shibboleth with JML, the Tomcat server needs to be started up with the following JVM parameters, specified in `bin\catalina.sh`:

```
JAVA_OPTS="$JAVA_OPTS "-Djava.security.auth.login.config="$CATALINA_HOME"/webapps/myapp/WEB-INF/conf/loginMyApp.conf
JAVA_OPTS="$JAVA_OPTS "-Djava.security.krb5.conf=/etc/krb5.conf
JAVA_OPTS="$JAVA_OPTS "-Djava.security.sasl.qop=auth-conf
```

Sample content of `loginMyApp.conf`:

```
EHSJML {
    com.sun.security.auth.module.Krb5LoginModule required useKeyTab=true
    debug=true keyTab="/etc/httpd/conf/webauth/keytab" doNotPrompt=true
    storeKey=true principal="webauth/server.domain.com "
    useTicketCache=true;
};
```

#### Stored Procedure Whitelist

In order to restrict authenticated users to only be allowed to run certain stored procedures, a whitelist is being used. **Only stored procedures in the whitelist will be accessible by the user; no exceptions!**

There are 2 methods for executing Oracle Stored Procedures, `doStoredProcedure` and `doUserStoredProcedure`, and `doUserStoredProcedure` calls `doStoredProcedure` to actually call a stored procedure, but first will check:

- Is the user authenticated?
- Is the stored procedure being called in the whitelist?

If both are true, the call is allowed to proceed.

If a user attempts to access a stored procedure that is not in the whitelist, the following message will be logged to the JML log:

```
User tried to access `STORED_PROCEDURE_NAME` which is not in the whitelist
```

If you create a new stored procedure, or rename an existing one, remember to update the `jml-server.xml` file to have the stored procedure AND restart the Tomcat server for the changes to take effect.


## Model functions

### Adminstration :: logoutUser

The function logs out a user remotely, intended to be used in connection with administration purposes, e.g. session clean up.

**Fields**:

| **Name** | **Mandatory or Optional** | **Description** |
|---|---|---|
| action | Mandatory | "logoutUser" |
| userSessionId | Mandatory | The system session id for the user that will be logged out remotely |
| account | Mandatory | Specification of the account to access, see chapter on accounts for details. |
| view | Optional | "JSON" |

**Example using the servlet controller:**

```
https://myserver/myapp/jml?action=logoutUser&userSessionId=89BF6B0A583FFCFC8C7D65609BB92E6A&account=MyApp
```

### Adminstration :: getActiveUsers

Get active users including their session ID and last time where they access the middlelayer.

**Fields**:

| **Name** | **Mandatory or Optional** | **Description** |
|---|---|---|
| action | Mandatory | "getActiveUsers" |
| account | Mandatory | Specification of the account to access, see chapter on accounts for details. |
| view | Optional | "JSON" |

**Example using the servlet controller:**

```
https://myserver/myapp/jml?action=getActiveUsers&account=MyApp
```

### Security :: login

The login function logs in to the JML which will keep a session for the user until the session times out or the user logs out explicitly. The username and password is only needed where the security plug-in requires it. For example the WebAuth security plug-in does not require the user name and password since the user has already provided this credential to the campus-side WebAuth and has received a WebAuth session ticket which the plug-in will introspect. In fact, *when using WebAuth, the developer should not log in the user explicitly to the JML since this will be done seamlessly by the middle layer.*

For the GenericApplication security plug-in, the user name and passwords are required since this plug-in will validate the login against the GenericApplication security model.

**Fields**:

| **Name** | **Mandatory or Optional** | **Description** |
|---|---|---|
| action | Mandatory | "login" |
| username | Dependent on security plug-in | User name |
| password | Dependent on security plug-in | Password |
| account | Mandatory | Specification of the account to access, see chapter on accounts for details. |
| view | Optinal | "JSON" |

**Example using the servlet controller:**

```
https://myserver/myapp/jml?action=login&username=pop&password=bob&account= MyApp
(not a valid user name and password combination)
```

### Security :: logout

The function logs out the user in to the JML. It is good practice to log out the user from the JML whenever it is possible to capture such a user event.

**Fields**:

| **Name** | **Mandatory or Optional** | **Description** |
|---|---|---|
| action | Mandatory | "logout" |
| account | Mandatory | Specification of the account to access, see chapter on accounts for details. |
| view | Optional | "JSON" |

**Example using the servlet controller:**

```
https://myserver/myapp/jml?action=logout&account=MyApp
```

### Database :: doStoredProcedure

The function runs a stored procedure and returns a multi-set result comprising all simple values and cursors.

At this point in time, if the SQL query fails at the database server level, "null" will be returned.

**Fields**:

| **Name** | **Mandatory or Optional** | **Description** |
|---|---|---|
| action | Mandatory | "doStoredProcedure" |
| account | Mandatory | Specification of the account to access, see chapter on accounts for details. |
| proc | Mandatory | Name of stored procedure. Package and table space names can be pre-pended in normal dot-form. |
| param[1-16] | Dependent on stored procedure | Input parameters for the stored procedure named param1, param2, ... up to param16. |
| view | Optional | "JSON" |

**Example using the servlet controller:**

```
https://myserver/myapp/jml?action=doStoredProcedure&proc=PACKAGE_NAME.PROC_NAME&param1=sampleuser&account=MyApp
```

### Database :: doUserStoredProcedure

The function runs a stored procedure and returns a multi-set result comprising all simple values and cursors. The function will insert the user's username as the first parameter to the stored procedure. For example a stored procedure `sampleSP(userName, year, location)` expects to receive a user name as p1; the call to the procedure will look like `proc=sampleSP&param1=2007&param2=Oakland&account=MyApp`.

At this point in time, if the SQL query fails at the database server level, "null" will be returned.

**Fields**:

| **Name** | **Mandatory or Optional** | **Description** |
|---|---|---|
| action | Mandatory | "doStoredProcedure" |
| account | Mandatory | Specification of the account to access, see chapter on accounts for details. |
| proc | Mandatory | Name of stored procedure. Package and table space names can be pre-pended in normal dot-form. |
| param[1-16] | Dependent on stored procedure | Input parameters for the stored procedure named param1, param2, ... up to param16. |
| view | Mandatory | "JSON" |

### Email :: email

Email a message. This function will not return a response.

**Fields**:

| **Name** | **Mandatory or Optional** | **Description** |
|---|---|---|
| action | Mandatory | "email" |
| account | Mandatory | Specification of the account to access, see chapter on accounts for details. |
| to | Mandatory | Recipients of the email |
| from | Mandatory | Sender of the email, e.g. "My App <donotreply@server.com>" |
| cc | Optional |  |
| bcc | Optional |  |
| subject | Optional | Subject line |
| body | Optional | Body text |

**Example using the servlet controller:**

```
https://myserver/myapp/jml?action=email&from=user1@server.com&to=user2@server.com&subject=test&body=This%20is%20a%20test&account=MyApp
```

### Runtime :: getRequestInfo

Display the HTTP request data.

**Fields**:

| **Name** | **Mandatory or Optional** | **Description** |
|---|---|---|
| action | Mandatory | "getRequestInfo" |
| account | Mandatory | Specification of the account to access, see chapter on accounts for details. |
| view | Optional | "JSON" |

**Example using the servlet controller:**

```
https://myserver/myapp/jml?action=getRequestInfo&account=MyApp&view=XML
```

### Runtime :: getSessionInfo

Display the HTTP session data.

**Fields**:

| **Name** | **Mandatory or Optional** | **Description** |
|---|---|---|
| action | Mandatory | "getSessionInfo" |
| account | Mandatory | Specification of the account to access, see chapter on accounts for details. |
| view | Optional | "JSON" |

**Example using the servlet controller:**

```
https://myserver/myapp/jml?action=getSessionInfo&account=MyApp&view=XML
```

### Runtime :: resetApplication

Resets the session data.

**Fields**:

| **Name** | **Mandatory or Optional** | **Description** |
|---|---|---|
| action | Mandatory | "resetApplication" |
| account | Mandatory | Specification of the account to access, see chapter on accounts for details. |
| view | Optional | "JSON" |

**Example using the servlet controller:**

```
https://myserver/myapp/jml?action=resetApplication&account=MyApp&view=XML
```

### Runtime :: doEcho

Echo a string back to the client. Useful to verify round trip connectivity and that the user has a valid session.

**Fields**:

| **Name** | **Mandatory or Optional** | **Description** |
|---|---|---|
| action | Mandatory | "doEcho" |
| account | Mandatory | Specification of the account to access, see chapter on accounts for details. |
| xmlWrapping | Optional | Examines the XML string and wraps in ... where ... is the echo string (echoStr). This option will be ignored if the echo string is not well-defined XML. |
| xmlWrapIfNull | Optional | Values "true" |
| echoStr | Mandatory | The echo string |
| view | Mandatory | "JSON" |

**Examples using the servlet controller:**

```
https://myserver/myapp/jml?action=doEcho&echoStr=%3CBAH%3E%3Ckuk/%3E%3C/BAH%3E&xmlWrapping=-result&account=MyApp

returns
<BAH-result><BAH><kuk/></BAH></BAH-result>

https://myserver/myapp/jml?action=doEcho&echoStr=%3CBAH%3E%3Ckuk/%3E%3C/BAH%3E&account=MyApp

returns <BAH><kuk/></BAH>

https://myserver/myapp/jml?action=doEcho&echoStr=%3CBAH%3E%3Ckuk/%3E%3C/BAH%3E&xmlWrapping=&xmlWrapIfNull=&xmlWrapIfNull=true&account=MyApp

returns <BAH><kuk/></BAH>

https://myserver/myapp/jml?action=doEcho&echoStr=%3CBAH%3E%3Ckuk/%3E%3C/BAH%3E&xmlWrapIfNull=true&account=MyApp

returns <BAH><BAH><kuk/></BAH></BAH>
```

## Access adapters

### HTTP Servlet

The HTTP server adapter provides a simple access points for a variety of applications to access JML.

Please see the examples in the module function definitions.

Please be aware that the values of the parameters must be HTML-escaped.

### JSP Adapter

The JSP adapter provides a simple access points for the JML/JSP tag library to access JML.

## Security Plug-ins and Account Definitions

The main purpose of a security plug-in is to provide an authentication mechanism, e.g. WebAuth and the GenericApplication user security scheme. The security plug-in does not provide authorization; this is provided by the model controllers after successful authentication.

The security section of the accounts settings definition file also tells the JML if it should require CSRF tokens to be passed along with each request. For more information about the CSRF token, please see section 4.3 below.

```
<?xml version='1.0' encoding='windows-1252'?>
<accounts>
    <account id="MyApp" active="true">
        <name>Sample Application</name>
        <connection>
            <user-name>MyApp</user-name>
            <password>thisisnottherealpassword</password>
            <driver-type>thin</driver-type>
            <server-name>somedbserver.domain.com </server-name>
            <database-name>db1</database-name>
            <network-protocol>tcp</network-protocol>
            <port>1542</port>
            <min-limit>1</min-limit>
            <max-limit>5</max-limit>
            <initial-limit>2</initial-limit>
        </connection>
        <security>
            <access-control-class>edu.stanford.ehs.jml.security.model.plugins.WebAuth</access-control-class>
            <session-timeout>120</session-timeout> <!-- time out in minutes -->
            <use-csrf-token>true</use-csrf-token>
        </security>
        <email>
            <smtp-server>smtp.domain.com</smtp-server>
            <smtp-user/>
            <smtp-password/>
            <smtp-debug>true</smtp-debug>
        </email>
    </account>
</accounts>
```

This above accounts settings definition (`WEB-INF/conf/accounts.xml`) defines the accounts for the JML; each account security plug-in is to provide an authentication mechanism, e.g. WebAuth and the GenericApplication user security should always reference one security plug-in.

### Automatically Loading Updates to the Accounts Settings Definition File

The JML can automatically poll the accounts setting definition file for changes so that doing things like adding a new database account or disabling an instance will not require a Tomcat restart before it will take effect.

To use this feature, include this XML element under the accounts element and be sure that the active attribute contains the value "true":

```
<check-modified active="true">300000</check-modified>
```

The value is expressed in milliseconds. So, for example, a value of 300000 means that the JML will check the accounts file every 5 minutes for changes.

If any changes are found, the JML re-executes the portion of its bootstrapping process that relates to loading, parsing, and processing this file. Existing user sessions are preserved during this process, except in cases where a database connection that a user session had been using is removed or marked as inactive. Although the session would technically still exist, the user would not be able to interact with the database in anyway.

### CSRF Token

To help protect against cross site request forgery (CSRF) attacks, the JML will generate a new token with each successful user login and do two things with it:

-   Store it in the users session
-   Return it to the user so it can be included with each subsequent request

From that point until the session is terminated, the JML will require each request from that user to include that synchronization token or it will not be processed.

By default the CSRF token will be generated using the `SHA1PRNG` algorithm. If that algorithm is unavailable on the server, `java.security.SecureRandom` will be used instead.

The CSRF token can be enabled/disabled on a per account basis, but disabling it is not recommended in all but dev environments, and even then it should be enabled so that it is not accidentally disabled when promoting code changes from dev to prod.

### DBMS Output Logging

In order to help facilitate faster debugging of stored procedures, DBMS output from the PL/SQL code can be logged to the JML log file by adding this to the accounts settings definition file:

```
<log-dbms-output>true</log-dbms-output>
```

This type of logging must be enabled on a per account basis.

### Full Example: accounts.xml

```
<?xml version='1.0' encoding='windows-1252'?>
<accounts>
    <check-modified active="true">300000</check-modified>
    <application-name>MyApplication</application-name>
    <account id="ct_myapp" active="true">
        <name>ct_myapp</name>
        <connection>
            <user-name>db_username</user-name>
            <password>db_password</password>
            <driver-type>thin</driver-type>
            <server-name>somedbserver.domain.com</server-name>
            <database-name>db1</database-name>
            <network-protocol>tcp</network-protocol>
            <port>1521</port>
            <min-limit>1</min-limit>
            <max-limit>5</max-limit>
            <initial-limit>2</initial-limit>
        </connection>
        <security>
            <access-control-class>edu.stanford.ehs.jml.security.model.plugins.GenericApplication</access-control-class>
            <session-timeout>120</session-timeout>
            <use-csrf-token>true</use-csrf-token>
        </security>
        <email>
            <smtp-server>smtp.domain.com</smtp-server>
            <smtp-user/>
            <smtp-password/>
            <smtp-debug>true</smtp-debug>
        </email>
        <log-dbms-output>false</log-dbms-output>
    </account>
</accounts>
```

## JSP Tag Library

### Architecture

The JSP tag library allows the developer to use a defined set of JML tags in the JSP code. This allows for clean and reusable code.

In order to utilize the JSP Tag Library, the JSP application must include an embedded JML.

### Configuration

In order to start using the JML tag library, the JAR file `jmltaglibs-1.0.jar` must be located in `WEB-INF/lib` in addition to defining the tags in `WEB-INF/tld/jml.tld`.

Each JSP page should include the following import directive:

```
<%@ taglib uri="http://ehs.stanford.edu/taglibs/jml" prefix="jml"%>
```

### Implemented Model Tag Library

The login function logs in to the JML which will keep a session until the session times out or the user logs out explicitly.

**Attributes**:

| **Name** | **Mandatory or Optional** | **Description** |
|---|---|---|
| account | Mandatory | The JML account |
| user | Mandatory | The user name |
| password | Mandatory | The password for the user name |
| onSuccessPage | Optional | The relative or absolute URL for the page that the user should be forwarded to, when the login is successful. |

**Example using a non-WebAuth authentication plug-in:**

```
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jstl/xml" prefix="x"%>
<%@ taglib uri="http://ehs.stanford.edu/taglibs/jml" prefix="jml"%>
<c:if test="${param.username!=null && param.password!=null}">
    <jml:login loginResult="loginResult" account="ehs_hp" user="${param.username}" password="${param.password}" onSuccessPage="viewLogSheet.jsp" />
</c:if>
<c:set var="pageTitle" value="My App - Log in" scope="session" />
<jsp:include flush="true" page="includes/header.jsp" />
<jsp:include flush="true" page="js/login.js" />
<form name="login" method="post">
    <table class="loginWindow">
        <tr>
            <td class="loginText" colspan="2">Please enter your id and password to login.</td>
        </tr>
        <tr>
            <td width="100" class="loginInputLabel">User Name:</td>
            <td width="400"><input type="text" name="username" /></td>
        </tr>
        <tr>
            <td class="loginInputLabel">Password:</td>
            <td><input type="password" name="password" onKeyPress="return submitEnter(this,event)"/></td>
        </tr>
        <tr>
            <td></td>
            <td>
                <div id="ehsButton">
                <table>
                    <tr>
                        <td width="100"><a href="#" onClick="javascript:document.login.submit()">Connect</a></td>
                    </tr>
                </table>
                </div>
            </td>
        </tr>
        <tr>
            <td></td>
            <td>
                <c:if test="${loginResult=='false'}">
                    <div class="loginError">The user name and password were not correct</div>
                </c:if>
            </td>
        </tr>
    </table>
</form>
<jsp:include flush="true" page="includes/footer.jsp" />
```

The function logs out the user and ends the JML connection.

**Attributes**:

| **Name** | **Mandatory or Optional** | **Description** |
|---|---|---|
| account | Optional | Name of the JL account |

**Example:**

```
<%@ taglib uri="http://ehs.stanford.edu/taglibs/jml" prefix="jml"%>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c"%>
<c:set var="pageTitle" value="My App - Log out" scope="session" />
<jsp:include flush="true" page="includes/header.jsp" />
<jml:logout />
<p>You are now logged out</p>
<div id="ehsButton">
    <table>
        <tr>
            <td width="120"><a href="login.jsp">Login Again</a></td>
        </tr>
    </table>
</div>

<jsp:include flush="true" page="includes/footer.jsp" />
```

The function runs a stored procedure and returns a multi-set result comprising all simple values and cursors. The function will insert the user’s username as the first parameter to the stored procedure.

At this point in time, if the SQL query fails at the database server level, "null" will be returned.

```
<jml:doUserStoredProcedure proc="PACKAGE_NAME.PROC_NAME"

param1="${param1}" param2="${param2}" param3="${param2}"

param4="${param4}" var="sourceMyApp"/>
```

**Attributes**:

| **Name** | **Mandatory or Optional** | **Description** |
|---|---|---|
| proc | Mandatory | The full name of th Oracle stored procedure |
| var | Mandatory | The name of the JSP variable in which the text-based XML should be stored for later parsing |
| account | Mandatory | Name of the JML account |
| param[1-16] | Dependent on stored procedure | Input parameters for the stored procedure named param1, param2, ... up to param16. |

```
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jstl/xml" prefix="x"%>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://ehs.stanford.edu/taglibs/jml" prefix="jml"%>
<jml:authenticate loginPage="login.jsp" account="ehs_hp" />
<c:set var="pageTitle" value="My App" scope="session" />
<jsp:include flush="true" page="includes/header.jsp" />
<c:set var="fmtErrorList" value="" scope="page" />
<%
    int numberOfRooms = 0;

    try {

        numberOfRooms = nteger.parseInt(request.getParameter("numberOfRooms"));

    } catch (NumberFormatException e) {

    }

    for (int roomCounter = 0; roomCounter < numberOfRooms; roomCounter++)
    {

        pageContext.setAttribute("roomCounter", String.valueOf(roomCounter));

        pageContext.setAttribute("building", request.getParameter("building_" + roomCounter));
        pageContext.setAttribute("param1", request.getParameter("param1_" + roomCounter));
        pageContext.setAttribute("room", request.getParameter("room_" + roomCounter));
        pageContext.setAttribute("param2", request.getParameter("param2_" + roomCounter));
        pageContext.setAttribute("param3", request.getParameter("param3_" + roomCounter));
        pageContext.setAttribute("param4", request.getParameter("param4\_" + roomCounter));
%>

    <jml:doUserStoredProcedure proc="PACKAGE_NAME.PROC_NAME" param1="${param1}" param2="${param2}" param3="${param3}" param4="${param4}" var="sourceMyApp" />
    <x:parse xml="${sourceMyApp}" var="docUpdateLogSheet" />
    <x:if select="$docUpdateLogSheet/multi-result-set/P_UPDATE_RESULT-result/P_UPDATE_RESULT/P_UPDATE_RESULT!='OK'">
        <c:set var="Error" scope="page">
            <x:out select="$docUpdateLogSheet/multi-result-set/P_UPDATE_RESULT-result/P_UPDATE_RESULT/P_UPDATE_RESULT" />
        </c:set>
        <c:set var="fmtErrorList" value="${fmtErrorList}<tr><td>${building} - ${room}</td><td>${error}</td></tr>" scope="page" />
    </x:if>
<% 
    } 
%>

<jsp:useBean id="now" class="java.util.Date" />
<c:set var="counter" value="0" scope="page" />
<jml:doUserStoredProcedure proc="PACKAGE_NAME.PROC_NAME" var="sourceMyApp" />
<x:parse xml="${sourceMyApp}" var="docMyApp" />
<p>List of things as per <fmt:formatDate value="${now}" type="DATE" pattern="MM/dd/yyyy" /></p>
<p>Please contact us at 555-5555 if you need help</p>
<form name="myForm" method="post">
    <div class="myApp">
        <table>
            <tr>
                <th>Building</th>
                <th>Room</th>
                <th>Date 1<br /> (MM/DD/YYYY)</th>
            </tr>
            <x:forEach var="i" select="$docMyApp/multi-result-set/RESULTS-result/RESULTS">
                <tr>
                    <td>
                        <x:out select="$i/building" /> 
                        <input type="hidden" name="building_<c:out value="${counter}"/>" value="<x:out select="$i/building" />">
                    </td>
                    <td>
                        <x:out select="$i/room" />
                        <input type="hidden" name="param1_<c:out value="${counter}"/>" value="<x:out select="$i/param1" />">
                        <input type="hidden" name="room_<c:out value="${counter}"/>" value="<x:out select="$i/room" />">
                    </td>
                    <td>
                        <x:out select="$i/date_1" />
                    </td>
                </tr>
                <c:set var="counter" value="${counter+1}" scope="page" />
            </x:forEach>
        </table>
    </div>
    <c:if test="${fmtErrorList!=''}">
        <div id="updateErrorHeader">The update was not successful. Please see the below errors messages for details.</div>
        <div id="updateErrorList">
            <table>
                <c:out value="${fmtErrorList}" escapeXml="false" />
            </table>
        </div>
    </c:if>
    <div id="ehsButton">
        <table>
            <tr>
                <td width="60"><a href="javascript:document.myForm.submit()">Save</a></td>
                <th width="385" />
                <td width="130"><a href="emailComments.jsp">Email Comments</a></td>
                <th width="10" />
                <td width="80"><a href="logout.jsp">Logout</a></td>
            </tr>
        </table>
    </div>
    <input type="hidden" name="numberOfRooms" value="<c:out value="${counter}"/>" />
</form>
<jsp:include flush="true" page="includes/footer.jsp" />
```

Email a message. This function will not return a response.

**Attributes:**

| **Name** | **Mandatory or Optional** | **Description** |
|---|---|---|
| account | Optional | Name of the JML account |
| to | Mandatory | Recipients of the email |
| from | Mandatory | Sender of the email, e.g. "My App <donotreply@server.com>" |
| cc | Optional |  |
| bcc | Optional |  |
| subject | Optional | Subject line |
| body | Optional | Body text |

**Example:**

```
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jstl/xml" prefix="x"%>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://ehs.stanford.edu/taglibs/jml" prefix="jml"%>
<jml:authenticate loginPage="login.jsp" account="ehs_hp" />
<c:if test="${param.emailBody!=null && param.emailBody!=null}">
    <jml:email to="user2@server.com" from="My App <donotreply@server.com>" subject="Comments/Help Request [${param.urgency}]" body="From: ${param.emailAddress} Urgency: ${param.urgency} ${param.emailBody}" />
    <c:redirect url="viewLogSheet.jsp"/>
</c:if>
<jml:doUserStoredProcedure proc="PACKAGE_NAME.PROC_NAME

" var="sourceUserInfo" />
<x:parse xml="${sourceUserInfo}" var="docUserInfo" />
<c:set var="pageTitle" value="Email Comments" scope="session" />
<jsp:include flush="true" page="includes/header.jsp" />
<jsp:include flush="true" page="js/email.js" />
<form name="comments" method="post">
    <table class="emailWindow">
        <tr>
            <td class="emailInputLabel">Subject:</td>
            <td class="emailText">Comments/Help Request</td>
        </tr>
        <tr>
            <td class="emailInputLabel">Your E-Mail Address:</td>
            <td class="emailText"><input type="text" name="emailAddress" size="40" value="<x:out
select="$docUserInfo/multi-result-set/USER_INFO-result/USER_INFO/emailaddress"/>"/></td>
        </tr>
        <tr>
            <td class="emailInputLabel">Urgency:</td>
            <td class="emailText">
                <select name="urgency"
                    <option value="Normal">Normal (Can wait for a few days)</option>
                    <option value="Urgent">Urgent (Can wait for a few hours)</option>
                    <option value="Very Urgent">Very Urgent (Need Help Now)</option>
                </select>
            </td>
        </tr>
        <tr>
            <td class="emailInputLabel">Problem Description:</td>
            <td class="emailText"><textarea name="emailBody" wrap="soft" cols="70" rows="10"></textarea></td>
        </tr>
        <tr>
            <td></td>
            <td>
                <div id="ehsButton">
                    <table>
                        <tr>
                            <td width="100"><a href="\#" onClick="javascript:verifyAndSendEmail()">Send Message</a></td>
                            <th width="100"></th>
                            <td width="100"><a href="viewLogSheet.jsp">Return to List</a></td>
                        </tr>
                    </table>
                </div>
            </td>
        </tr>
    </table>
</form>
<jsp:include flush="true" page="includes/footer.jsp" />
```