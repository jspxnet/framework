<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>${title}</title>
    <script type="text/javascript" src="/script/mootools.js"></script>
    <script type="text/javascript" src="/script/mootools-more.js"></script>
    <link rel="stylesheet" type="text/css" href="/script/jspxnet-ui.css"/>
    <script type="text/javascript" src="/script/jspxnet.js"></script>
    <script type="text/javascript" src="/script/jspxnet-ui-roc.js"></script>
    <link rel="stylesheet" type="text/css" href="/script/jspxnet-ui.css"/>
    <link href="/share/mskin/default/css.css" rel="stylesheet" type="text/css"/>
    <script src="/script/vue.js"></script>
    <style>
        [v-cloak] {
            display: none;
        }
    </style>
</head>
<body>
<div class="formContainer mWrap">
    <div class="mTitPanel jboardLogo">
        ${title}
    </div>
    <div class="addConPanel mConPanel">
        <div id="helpTip"></div>
        <form id="${varName}Form" v-cloak>

            <#list column=columns >
            <#if where="jumpFields.contains(column.name)"><#continue /></#if>
            <#if where="'text'==column.input||'input'==column.input">

                <div class="row">
                    <label class="tit" for="${column.name}">${column.caption}</label>
                    <div class="con">
                        {{${column.name}}}
                    </div>
                </div>
                <#continue />
            </#if>

            <#if where="column.option.toBoolean()">
            <#if where="'radio'==column.input">
            <div class="row">
                <label class="tit" for="${column.name}">${column.caption}</label>
                <div class="con">
                    {{${column.name}}}
                </div>
            </div>
            <#else>
            <#if where="'checkbox'==column.input">
            <div class="row">
                <label class="tit" for="${column.name}">${column.caption}</label>
                <div class="con">
                    {{${column.name}}}
                </div>
                <#else>
                <div class="row">
                    <label class="tit" for="${column.name}">${column.caption}</label>
                    <div class="con">
                        {{${column.name}}}
                    </div>
                </div>
                </#else></#if>
                </#else>
                </#if>

                <#continue />
                </#if>

                <#if where="'input'==column.input">
                <div class="row">
                    <label class="tit" for="${column.name}">${column.caption}</label>
                    <div class="con">
                        {{${column.name}}}
                    </div>
                </div>
                <#continue />
                </#if>
                <#if where="'textarea'==column.input || column.length gt 1000">
                <div class="columnPanel">
                    <label class="tit" for="${column.name}">${column.caption}</label>
                    <div class="con">
                        {{${column.name}}}
                    </div>
                </div>
                <#continue />
                </#if>
                <#if where="column.classType.name=='java.util.Date'">
                <div class="row">
                    <label class="tit" for="${column.name}">${column.caption}</label>
                    <div class="con">
                        {{${column.name}}}
                    </div>
                </div>
                <#continue />
                </#if>
                </#list>
        </form>
    </div>
</div>
<script>
    var modelId = '${modelId}';
    var app = false;
    window.addEvent('domready', function () {
        var id = getUrlParam("id") - 0;
        if (id <= 0) id = 0;
        new Request.ROC({
            'url': '/dynout/call/detail/' + modelId + '/' + id + '.jwc',
            onSuccess: function (obj) {
                if (obj.success) {
                    new Vue({
                        el: '#${varName}Form',
                        data: obj.data
                    });
                }
            }
        }).send();
    });
</script>
</body>
</html>