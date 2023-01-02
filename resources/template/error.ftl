<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1" />
    <@import module="['mootools','jspxnet']" />
    <link rel="stylesheet" type="text/css" href="${scriptPath}/codemirror/codemirror.css"/>
    <script type="text/javascript" charset="utf-8" src="${scriptPath}/codemirror/codemirror.js"></script>
    <script type="text/javascript" charset="utf-8" src="${scriptPath}/codemirror/mode/xml/xml.js"></script>
    <title>错误提示</title>
    <style type="text/css">
        .wrapper {
            background-color: #fff;
            border: 2px solid #f8bb5b;
            margin: 10px;
            padding:10px;
            z-index: 10;
        }
        div, ul, li, h1, h2, h3, h4, form, input, p, button, dl, dt, dd, fieldset, textarea, label, del {
            margin: 0;
            padding: 0;
        }

        .question_text {
            color: #666;
            font-size: 14px;
            font-weight: normal;
            line-height: 21px;
        }
        .user_wrap a {
            color: #828282;
        }

        pre {
            font-family: "SimHei","SimSun",Arial,sans-serif;
            overflow-wrap: break-word;
            white-space: pre-wrap;
            word-break: break-all;
            padding-left: 10px;
        }

        a {
            color: #666;
            text-decoration: none;
        }

        .clearfix::after, .cf::after {
            clear: both;
            content: "";
            display: table;
        }

        .ask_autho {
            color: #828282;
            line-height: 30px;
            position: relative;
        }

        .pj_btn a {
            color: #4083a9;
            display: block;
            float:right;
            margin-left:50px;

            padding: 0 6px;
            white-space: nowrap;
        }

    </style>
</head>
<body>

<div class="wrapper">
    <h2>错误信息:<span style="color: #E0E0E0;">ERROR</span></h2>
    <hr/>
    <div class="syntaxhighlighter">
	<textarea id="errorMessage" name="code" class="xml">
<#if where="FieldInfoList!=null" ><#list key=FieldInfoList.keySet() ><#switch var=key><#case where='warning'>警告</#case><#case where='infoType'>提示</#case><#case where='message'>提示</#case><#case where='error'>错误</#case><#default>说明</#default></#switch>:${String(FieldInfoList.get(key))}</#list></#if>

        <#if where="message" >${message#('未知错误')}</#if>
	</textarea>
    </div>
    <div class="pj_btn cf">
        <a href="/user/login.jhtml">登陆</a>
        <a href="/" >返回首页</a>
    </div>
</div>
<script language="javascript">
    window.addEvent('domready', function() {
        var conEl = $("errorMessage");
        CodeMirror.fromTextArea(conEl, {
            mode: 'xml',
            lineNumbers: true,
            lineWrapping:true,
            theme: "default",
            extraKeys: {"Enter": "newlineAndIndentContinueMarkdownList"}
        });
    });
</script>
</body>
</html>