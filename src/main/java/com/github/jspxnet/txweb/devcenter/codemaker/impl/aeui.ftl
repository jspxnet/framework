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
    <link href="/share/mskin/default/devcenter.css" rel="stylesheet" type="text/css"/>
    <script src="/script/vue.js"></script>
    <style>
        [v-cloak] {
            display: none;
        }
    </style>
</head>
<body>
<div class="formContainer mWrap">
    <div class="mTitPanel addBoardLogo">
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
                        <input id="${column.name}" name="${column.name}" type="text" v-model:value="${column.name}"/>
                        <span id="${column.name}Msg" class="note"></span>
                    </div>
                </div>
                <#continue />
            </#if>
            <#if where="column.option.toBoolean()">
            <#if where="'radio'==column.input">
            <div class="row">
                <label class="tit" for="${column.name}">${column.caption}</label>
                <div class="con">
                    <#list op=column.optionList >
                        <label><input name="${column.name}" v-model="${column.name}" type="radio" value="${op.value}" >${op.name}</label>
                    </#list>
                </div>
            </div>
            <#else>
            <#if where="'checkbox'==column.input">
            <div class="row">
                <label class="tit" for="${column.name}">${column.caption}</label>
                <div class="con">
                    <#list op=column.optionList >
                        <label><input name="${column.name}" v-model="${column.name}" type="checkbox" value="${op.value}">${op.name}</label>
                    </#list>
                </div>
                <#else>
                <div class="row">
                    <label class="tit" for="${column.name}">${column.caption}</label>
                    <div class="con">

                        <select name="${column.name}" id="${column.name}" v-model="${column.name}">
                            <#list op=column.optionList >
                                <option value="${op.value}">${op.name}</option>
                            </#list>
                        </select>
                        <span id="${column.name}Msg" class="note"></span>
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
                        <input id="${column.name}" name="${column.name}" v-model:value="${column.name}" />
                        <span id="${column.name}Msg" class="note"></span>
                    </div>
                </div>
                <#continue />
                </#if>
                <#if where="'textarea'==column.input || column.length gt 1000">
                <div class="columnPanel" >
                    <label class="tit" for="${column.name}">${column.caption}</label>
                    <div class="con">
                        <textarea id="${column.name}" name="${column.name}" v-model:value="${column.name}"></textarea>
                        <span id="${column.name}Msg" class="note"></span>
                    </div>
                </div>
                <#continue />
                </#if>
                <#if where="column.classType.name=='java.util.Date'">
                <div class="row">
                    <label class="tit" for="${column.name}">${column.caption}</label>
                    <div class="con">
                        <input id="${column.name}" name="${column.name}" type="date" v-model:value="${column.name}"/>
                        <span id="${column.name}Msg" class="note"></span>
                    </div>
                </div>
                <#continue />
                </#if>
                </#list>

                <div class="buttonPanel">
                    <button id="submitBtn" name="method" value="save" type="button" v-on:click="save" class="jDefButton"><i class="jIcoSave"></i>确定</button>
                    <input id="id" name="id" type="hidden" :value="id"/>
                </div>
        </form>
    </div>
</div>
<script>
    //实体模型id
    var modelId = '${modelId}';
    var app = false;
    window.addEvent('domready', function () {
        // new Calendar().cssInit();
        app = new Vue({
            el: '#${varName}Form',
            data: function () {
                var data = getFormJson("${varName}Form");
                console.log(data);
                return data;
            },
            methods: {
                loadData: function () {
                    var that = this;
                    var id = getUrlParam("id") - 0;
                    if (id <= 0) id = 0;
                    new Request.ROC({
                        'url': '/dynout/call/detail/' + modelId + '/' + id + '.jwc',
                        onSuccess: function (obj) {
                            if (obj.success) {
                                Object.assign(that.$data, obj.data);
                                that._data = obj.data;
                                that.$forceUpdate();
                            }
                        }
                    }).send();
                },
                save: function () {
                    var posts = getFormJson("${varName}Form");
                    posts.modelId = modelId;
                    //添加接口
                    var url = '/dynout/call/save.jwc';
                    new Request.ROC({
                        'url': url,data:posts,
                        onSuccess: function (obj) {
                            if (obj.success == 1) {
                                new JDialog.Pop({
                                    title: '提示信息',
                                    message: obj.message
                                }).show();
                            } else {
                                new JDialog.Alert({
                                    title: '提示信息',
                                    message: obj.message
                                }).show();
                            }
                        }
                    }).post();
                }
            }
            ,
            mounted: function () {
                this.loadData();
            }
        });

    });
</script>
</body>
</html>