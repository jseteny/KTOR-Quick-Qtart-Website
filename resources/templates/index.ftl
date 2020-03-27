<#-- @ftlvariable name="data" type="com.example.IndexData" -->
<#-- @ftlvariable name="userName" type="String" -->
<html>
<body>
<p>Username: ${userName}</p>
<ul>
    <#list data.items as item>
        <li>${item}</li>
    </#list>
</ul>
</body>
</html>
