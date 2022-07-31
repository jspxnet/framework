<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <title>${title}列表</title>
    <script type="text/javascript" src="/script/mootools.js"></script>
    <script type="text/javascript" src="/script/mootools-more.js"></script>
    <link rel="stylesheet" type="text/css" href="/script/jspxnet-ui.css"/>
    <script type="text/javascript" src="/script/jspxnet.js"></script>
    <script type="text/javascript" src="/script/jspxnet-ui-roc.js"></script>
    <link rel="stylesheet" type="text/css" href="/script/jspxnet-ui.css"/>
    <link href="/share/mskin/default/devcenter.css" rel="stylesheet" type="text/css"/>
    <script type="text/javascript">
        var modelId = '${modelId}';
        window.addEvent('domready', function() {
            var columnModels = ${columnModels};
            var buttons = [];
            buttons.push(new Element('button', {'type':'button','class':'jTDSelectAll','html':'全选',
                'events':
                    {
                        'click': function(event)
                        {
                            jtable.selectAll();
                            return false; //google 浏览器要
                        }
                    }
            }));


            buttons.push(new Element('button', {'type':'button','class':'jTDInverse','html':'返选',
                'events':
                    {
                        'click': function(event)
                        {
                            jtable.selectedConvert();
                            return false; //google 浏览器要
                        }
                    }
            }));

            buttons.push(new Element('div', {'class':'btnseparator'}));
            buttons.push(new Element('button', {'type':'button','class':'jDTAdd','html':'添加',
                'events':
                    {
                        'click': function(event)
                        {
                            window.location = "/dynout/render/page/url/ae${varName.toLowerCase()}.jwc";
                            return false;
                        }
                    }
            }));


            //本页面为放入回收站
            buttons.push(new Element('button', {'type':'button','class':'jDTDelete','html':'删除',
                'events':
                    {
                        'click': function(event)
                        {
                            var selects = jtable.getSelecteds('id');
                            //提示操作 begin
                            if(selects.length < 1) {
                                var dialog = new JDialog.Alert({
                                    'message': '选择你要操作的行.'
                                });
                                dialog.show();
                                return false;
                            }
                            //提示操作 end
                            if(!confirm('你确定要删除吗?')) return;

                            var posts = {
                                modelId:modelId,
                                ids:selects
                            };
                            new Request.ROC({'url':'/dynout/call/delete.jwc','data':posts,
                                onSuccess:function(obj)
                                {
                                    if (obj.success) jtable.refresh();
                                    else {
                                        var dlg = new JDialog.Alert({
                                            title: '消息提示',
                                            message:obj.message
                                        });
                                        dlg.show();
                                    }
                                }
                            }).send();


                            return false;
                        }
                    }
            }));



            buttons.push(new Element('div', {'class':'btnseparator'}));
            var fen = new Element('label', {'html':'&nbsp;|&nbsp;','styles':{'float':'left','color':'#ffffff'}});
            buttons.push(fen);
            var findBox = new Element('input', {'type':'text','name':'find','id':'find'});
            var labelFind = new Element('label', {'html':'关键字','styles':{'float':'left'}});
            labelFind.adopt(findBox);
            buttons.push(labelFind);
            buttons.push(new Element('button', {'type':'button','class':'jTDRefresh','html':'搜索',
                'events':
                    {
                        'click': function(event)
                        {
                            jtable.loadData('/devcenter/${varName.toLowerCase()}/list/page.jwc');
                            return false;
                        }
                    }
            }));

            jtable = new JDataTable({table:"grid",title:'${title}列表',url:'/dynout/call/list/page.jwc',
                defaultParam:{'modelId':modelId},
                accordion:true,
                autoSectionToggle:false,
                showTog:true, //显示详细
                editMode:false,
                columnModels:columnModels,
                buttons:buttons,
                pagination:true,
                serverSort:true,
                showCaption: true,
                sortCaption: true,
                alternaterows: true,
                resizeColumns: true,
                multiselect:true,
                ondblclick:function (selects) {
                    var id = selects.rowdata.id;
                    window.location = "/devcenter/ae${varName.toLowerCase()}.html?id="+id;
                }
            });
        });
    </script>
</head>
<body jskin="true">
<div class="mWrap">
    <div class="mTitPanel jboardLogo">${title}管理</div>
    <div class="mConPanel">
        <div id="helpTip"></div>
        <div id="grid"></div>
    </div>
</div>
</body>
</html>