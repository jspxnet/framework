<a onclick="showFileList(1<#if querystring!=''>&${querystring}</#if>)" class="first" alt="第一页"></a>
<#if totalPage != currentPage>
  <a onclick="showFileList(${currentPage-1}<#if querystring!=''>&;${querystring}</#if>)" class="previous" alt="上一页"></a>
</#if>

<#if totalPage gt currentPage >
   <a onclick="showFileList(${currentPage+1}<#if querystring!=''>&;${querystring}</#if>)" class="next" alt="下一页"></a>
</#if>

<a onclick="showFileList(${totalPage}<#if querystring!=''>&;${querystring}</#if>)" class="last" alt="最后页"></a>

<a onclick="showFileList(1<#if querystring!=''>&${querystring}</#if>)" class="refresh" alt="刷新"></a>