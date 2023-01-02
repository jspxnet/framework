<ul class="pagination">
<#if where="currentPage!=1" >
<li><a href="?page=1<#if querystring!=''>&${querystring}</#if>" >1</a></li>
    <li class="noStyle">..</li>  
   <#else>
 <li class="currentState">1</li>
   </#else>
</#if>

<#if where="currentPage gt 1" >
    <li class="previousPage"><a href="?page=${currentPage-1}<#if where=querystring >&${querystring}</#if>">上一页</a></li>
</#if>

<#list i=beginPage..endPage equals="false" >
  <#if where="i==currentPage">
   <li class="currentState">${i}</li>
  <#else>
      <li><a href="?page=${i}<#if where=querystring>&${querystring}</#if>">${i}</a></li>
  </#else>
  </#if>
</#list>

<#if where="totalPage gt currentPage" >
 <li class="nextPage"><a href="?page=${currentPage+1}<#if where=querystring>&${querystring}</#if>" >下一页</a></li>
 <li class="noStyle">..</li>
</#if>
 <li><a href="?page=${totalPage}<#if where=querystring>&${querystring}</#if>">${totalPage}/${count}</a></li>
</ul>