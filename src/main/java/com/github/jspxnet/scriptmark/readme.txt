______________________列表
<#list v=list>
 <li>${v.index}-${v}</li>
</#list>

________________________IF
       <#if v=='11'>
           out:aaaaaaa
          <#else>
           out:bbbbbbbb
           </#else>
        </#if>

宏调用 普通变量不使用引号,使用引号表示字符串,
如果是表达式可以使用${} 的格式.