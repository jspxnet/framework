<ul class="pagination">
    <#if where="currentPage!=1" >
        <li><a href="?currentPage=1<#if querystring!=''>&${querystring}</#if>" >1</a></li>
        <li class="noStyle">..</li>
    <#else>
        <li class="currentState">1</li>
    </#else>
    </#if>

    <#if where="currentPage gt 1" >
    <li class="previousPage"><a
                href="?currentPage=${currentPage-1}<#if where=querystring >&${querystring}</#if>">上一页</a></li>
    </#if>

    <#list i=beginPage..endPage equals="false" >
    <#if where="i==currentPage">
    <li class="currentState">${i}</li>
    <#else>
    <li><a href="?currentPage=${i}<#if where=querystring>&${querystring}</#if>">${i}</a></li>
    </#else>
    </#if>
    </#list>

    <#if where="totalPage gt currentPage" >
    <li class="nextPage"><a href="?currentPage=${currentPage+1}<#if where=querystring>&${querystring}</#if>">下一页</a>
    </li>
    <li class="noStyle">..</li>
    </#if>
    <li><a href="?currentPage=${totalPage}<#if where=querystring>&${querystring}</#if>"
           title="每页显示${count}条，共${totalPage}页">${currentPage}/${totalPage}</a></li>
</ul>