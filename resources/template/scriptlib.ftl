<!--# assign console=java.lang.System.out #-->
<#macro name=radios>${if (typeof(style)=='undefined') style='';list.radio(name,selected,style)}</#macro>
<#macro name=checkboxs>${if (typeof(style)=='undefined') style='';list.checkbox(name,selected,style)}</#macro>
<#macro name=options>${converter.getHtmlOptions(list,selected,undefined)}</#macro>
<#macro name=show>${converter.show(list,selected,undefined)}</#macro>
<#macro name=captcha><img id="captcha" src="/validate.${suffix}" onclick="this.src='/validate.${suffix}?r='+Math.floor(Math.random()*1000)" border="0" referrerpolicy="no-referrer" /></#macro>

<!--# 显示附件错误信息  #-->
<#macro name=attach>
    <#if where="value!=undefined" >
        <#assign downloadMap=value.toAttachMap() />
        <#if where="downloadMap.size() gt 0" >
        <span class="label"><em style="color:#660000;">${downloadMap.size()}</em>个关联:</span>
            <#list key=downloadMap.keySet() >
                <#if downloadMap.get(key).isImage() >
                <div style="width:150px; display:block;">
                    <a onclick="showImageDialog('${downUrl}${key}.${suffix}','${downloadMap.get(key)}');" ><img width="140" src="${downUrl}${key}.${suffix}" title="${downloadMap.get(key)}" border="0"  /></a>
                </div>
                <#else>
                    <#if where="key.startsWith('[')&key.endWith(']')" >
                    <br><a onclick="showLinkDialog('${key}','${downloadMap.get(key)}');" >${downloadMap.get(key)}</a></br>
                    <#else>
                    <br><a href="${downUrl}${key}.${suffix}" target="_blank"><img src="/share/pimg/filetype/${downloadMap.get(key).substringLastAfter('.')}.gif" border="0" />${downloadMap.get(key)}</a></br>
                   </#else></#if>
				</#else></#if>
			</#list>
	 </#if>
	<#else>&nbsp;</#else>
	</#if>
</#macro>

<!--# 显示页面提示信息 #-->
<#macro name=information>
<#if where="action.hasActionMessage()||action.hasFieldInfo()">
<div id="information" class="informationPanel" >
    <div class="tit"><span></span>系统提示：</div>
    <div class="con">
        <ul>
        <#if where="action.hasFieldInfo()" >
        <#list key=FieldInfo.keySet() >
            <li><b>
				<#switch var=key>
				  <#case where='warning'>警告</#case>
				  <#case where='infoType'>提示</#case>
				  <#case where='message'>提示</#case>
				  <#case where='error'>错误</#case>
				  <#default>说明</#default>
				</#switch>
		   </b>:${String(FieldInfo.get(key))}</li>
        </#list>
        </#if>
        <#if where="action.hasActionMessage()" >
        <#list msg=ActionMessages>
            <li>${String(msg)}</li>
        </#list>
        </#if>
        </ul>
    </div>
</div>
</#if>
</#macro>

<!--# 用来做为子页翻页 #-->
<#macro name=turnPage>
<#if where=rows==undefined><#assign rows=1 /></#if>
<#assign imax=(totalCount/rows).toInt() />
<#if where=imax*rows&lt;totalCount><#assign imax=imax+1 /></#if>
<#if where=imax&gt;1 >
<#if where=currentPage&gt;imax><a href="?id=${id}&currentPage=${currentPage-1}">上一页</a></#if>
<#list p=1..imax>
<#if where=p_index+1==currentPage >
<a href="?id=${id}&currentPage=${p_index+1}"><b>@${p_index+1}</b></a>
<#else>
<a href="?id=${id}&currentPage=${p_index+1}">${p_index+1}</a>
</#else>
</#if>
</#list>
<#if where=currentPage&lt;imax>
<#if where=currentPage-0==0 ><#assign nextp=2 /><#else><#assign nextp=currentPage-0+1 /></#else></#if>
<a href="?id=${id}&currentPage=${nextp}">下一页</a>
</#if>
</#if>
</#macro>

<#macro name=import>
    <script type="text/javascript" language="javascript">
		var scriptPath='${scriptPath}';
		var suffix = '${suffix}';
		var sitePath = '${sitePath}';
		var remoteHostUrl = '${remoteHostUrl}';
		var userLoginUrl = '${userLoginUrl}';
        <!--# var jspxDebug = ${jspxDebug}; #-->
		var jspxDebug = false;
	</script>
    <#if where='module.contains("mootools")'>
    <script type="text/javascript" language="javascript" src="${scriptPath}/mootools.js"></script>
    <script type="text/javascript" language="javascript" src="${scriptPath}/mootools-more.js"></script></#if>
    <#if where='module.contains("jquery")'><script type="text/javascript" language="javascript" src="${scriptPath}/jquery-1.12.4.js"></script></#if>
    <#if where='module.contains("handlebars")'>
    <script type="text/javascript" charset="utf-8" src="${scriptPath}/handlebars.js"></script></#if>
    <#if where='module.contains("jspxnet")'>
    <link rel="stylesheet" type="text/css" href="${scriptPath}/jspxnet-ui.css"/>
    <script type="text/javascript" language="javascript" src="${scriptPath}/jspxnet.js"></script>
    <script type="text/javascript" language="javascript" src="${scriptPath}/jspxnet-ui.js"></script>
    <script type="text/javascript" language="javascript" src="${scriptPath}/jspxnet-app.js.${suffix}"></script></#if>
    <#if where='module.contains("highlighter")'>
    <link href="${scriptPath}/highlighter/styles/shCore.css" rel="stylesheet" type="text/css"/>
    <link href="${scriptPath}/highlighter/styles/shThemeDefault.css" rel="stylesheet" type="text/css"/>
    <script type="text/javascript" language="javascript" src="${scriptPath}/highlighter/highlighter.js.${suffix}"></script></#if>
    <#if where='module.contains("upload")'>
	<script src="${scriptPath}/plupload/plupload.min.js"></script></#if>
    <#if where='module.contains("md5")'>
	<script type="text/javascript" src="/script/spark-md5.min.js"></script></#if>
    <#if where='module.contains("less")'>
    <script type="text/javascript" language="javascript" src="${scriptPath}/less/less.min.js"></script></#if>
    <#if where='module.contains("uvumicrop")'>
    <script type="text/javascript" language="javascript" src="${scriptPath}/uvumicrop/uvumicrop.js"></script>
    <link rel="stylesheet" type="text/css" media="screen" href="${scriptPath}/uvumicrop/uvumicrop.css" /></#if>
    <#if where='module.contains("echarts")'><script type="text/javascript" language="javascript" charset="utf-8" src="${scriptPath}/echarts/echarts-all.js"></script></#if>
    <#if where='module.contains("ueditor")'><script type="text/javascript" language="javascript" charset="utf-8" src="${scriptPath}/ueditor/ueditor.all.js"></script><script type="text/javascript" charset="utf-8" src="${scriptPath}/ueditor/lang/zh-cn/zh-cn.js"></script></#if>
    <#if where='module.contains("html5media")'><script type="text/javascript" charset="utf-8" src="${scriptPath}/html5media/html5media.min.js"></script></#if>
    <#if where='module.contains("jsencrypt")'><script type="text/javascript" language="javascript" charset="utf-8" src="${scriptPath}/jsencrypt.js.${suffix}"></script></#if>
</#macro>