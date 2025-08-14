主题共${totalCount}个;共${totalPage}页;第${currentPage}页;每页${count}个:
<a href="?currentPage=1<#if querystring!=''>&${querystring}</#if>"   style="cursor:pointer;">第一页</a>
<#if totalPage != currentPage>
<a href="?currentPage=${currentPage-1}<#if where=querystring >&${querystring}</#if>" style="cursor:pointer;">
  上一页</a>
</#if>

<#list i=beginPage..endPage>
  <#if where=i==currentPage>
<span class="turnCurrentPage">[${i}]</span>
  <#else>
<span class="turnMiddlePage">
  <a href="?currentPage=${i}<#if where=querystring>&${querystring}</#if>" style="cursor:pointer;">[${i}]</a></span>
  </#else>
  </#if>
</#list>

<#if where=totalPage &gt; currentPage >
<a href="?currentPage=${currentPage+1}<#if where=querystring>&${querystring}</#if>" style="cursor:pointer;">
   下一页</a>
</#if>

<a href="?currentPage=${totalPage}<#if where=querystring>&${querystring}</#if>" style="cursor:pointer;">最后页</a>