<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
        "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title id="title">${title}</title>
    <link href="${scriptPath}/highlighter/styles/shCore.css" rel="stylesheet" type="text/css"/>
    <link href="${scriptPath}/highlighter/styles/shThemeDefault.css" rel="stylesheet" type="text/css"/>
    <script type="text/javascript" src="${scriptPath}/mootools.js"></script>
    <script type="text/javascript" src="${scriptPath}/mootools-more.js"></script>
    <script type="text/javascript" src="${scriptPath}/highlighter/highlighter.js.${suffix}" charset="UTF-8"></script>
    <script type="text/javascript" type="text/javascript">
        window.addEvent('domready', function () {
            var memuPanel = $('memuPanel');
            var h1 = $$('#content h1');
            if (h1 && h1.length > 0)
                $('title').set('html', h1[0].get('html'));

            var hArray = $$('#content h2');
            hArray.each(function (e) {
                var caption = e.get('html');
                var id = e.get('id');
                var liEl = new Element('li');

                var aEl = new Element('a', {'href': '#' + id, 'html': caption});
                liEl.adopt(aEl);
                memuPanel.adopt(liEl);

            });

            SyntaxHighlighter.all();

            var clientWidth = window.innerWidth;
            if (window.innerWidth) {
                clientWidth = window.innerWidth;
            } else if (document.documentElement) {
                clientWidth = document.documentElement.clientWidth;
            }
            if (clientWidth < 500) clientWidth = 500;
            $(document.body).setStyle('width', (clientWidth - 250));

        });

    </script>
    <style type="text/css">
        sub, sup {
            position: relative;
            font-size: 75%;
            line-height: 0;
            vertical-align: baseline;
        }

        sup {
            top: -0.5em;
        }

        sub {
            bottom: -0.25em;
        }

        a:active {
            outline: 0;
        }

        a {
            color: #0088cc;
            text-decoration: none;
        }

        a, a:visited {
            text-decoration: underline;
        }

        tr,
        img {
            page-break-inside: avoid;
        }


        h2 {
            orphans: 3;
            widows: 3;
            border-top: 4px solid #e0e0e0;
            page-break-after: avoid;
        }

        body {
            font-family: "微软雅黑", "Helvetica Neue", Helvetica, Arial, sans-serif;
            font-size: 13px;
            min-width: 500px;
        }

        .img-rounded {
            -webkit-border-radius: 6px;
            -moz-border-radius: 6px;
            border-radius: 6px;
        }

        .img-polaroid {
            padding: 4px;
            background-color: #fff;
            border: 1px solid #ccc;
            border: 1px solid rgba(0, 0, 0, 0.2);
            -webkit-box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
            -moz-box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
            box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
        }

        .img-circle {
            -webkit-border-radius: 500px;
            -moz-border-radius: 500px;
            border-radius: 500px;
        }

        small {
            font-size: 85%;
        }

        strong {
            font-weight: bold;
        }

        em {
            font-style: italic;
        }

        cite {
            font-style: normal;
        }

        a.muted:hover,
        a.muted:focus {
            color: #808080;
        }

        .text-warning {
            color: #c09853;
        }

        a.text-warning:hover,
        a.text-warning:focus {
            color: #a47e3c;
        }

        .text-error {
            color: #b94a48;
        }

        a.text-error:hover,
        a.text-error:focus {
            color: #953b39;
        }

        .text-info {
            color: #3a87ad;
        }

        a.text-info:hover,
        a.text-info:focus {
            color: #2d6987;
        }

        .text-success {
            color: #468847;
        }

        a.text-success:hover,
        a.text-success:focus {
            color: #356635;
        }

        .text-left {
            text-align: left;
        }

        .text-right {
            text-align: right;
        }

        .text-center {
            text-align: center;
        }

        h1,
        h2,
        h3,
        h4,
        h5,
        h6 {
            margin: 10px 0;
            font-family: inherit;
            font-weight: bold;
            color: inherit;
            text-rendering: optimizelegibility;
            text-shadow: 0 2px 2px rgba(0, 0, 0, 0.1);
        }

        h1,
        h2,
        h3 {
            line-height: 26px;
        }

        h1 {
            font-size: 26px;
            text-align: center;
        }

        h2 {
            font-size: 20px;
        }

        h3 {
            font-size: 18px;
            margin: 0 0 5px;
            text-indent: 1em;
        }

        h4 {
            font-size: 16px;
            margin: 0 0 10px;
            text-indent: 2em;
        }

        h5 {
            font-size: 14px;
        }

        table {
            max-width: 100%;
            background-color: transparent;
            border-collapse: collapse;
            border-spacing: 0;
        }

        blockquote {
            font-family: georgia, serif;
            font-style: italic;
            margin-bottom: 18px;
            padding: 13px 13px 21px 15px;
        }

        blockquote:before {
            color: #EEEEEE;
            content: "\81 C";
            font-family: georgia, serif;
            font-size: 40px;
            margin-left: -10px;
        }

        blockquote p {
            font-size: 14px;
            font-style: italic;
            font-weight: 300;
            line-height: 18px;
            margin-bottom: 0;
        }

        code, pre {
            font-family: Monaco, Andale Mono, Courier New, monospace;
        }

        code {
            background-color: #FEE9CC;
            border-radius: 3px;
            color: rgba(0, 0, 0, 0.75);
            font-size: 12px;
            padding: 1px 3px;
        }

        pre {
            border: 1px solid #D9D9D9;
            display: block;
            font-size: 11px;
            line-height: 16px;
            margin: 0 0 18px;
            padding: 14px;
            white-space: pre-wrap;
            word-wrap: break-word;
        }

        pre code {
            background-color: #FFFFFF;
            color: #737373;
            font-size: 11px;
            padding: 0;
        }

        hr {
            border-top: 1px solid #ddd;
            margin: 30px 0;
        }

        table {
            margin-left: 24px;
            border: 1px solid #ddd;
        }

        table th,
        table td {
            padding: 0px;
            text-align: left;
            vertical-align: top;
            border: 1px solid #ddd;
        }

        table th {
            font-weight: bold;
            background-color: #F4F4EA;
        }

        table thead th {
            vertical-align: bottom;
        }

        table caption + thead tr:first-child th,
        table caption + thead tr:first-child td,
        table colgroup + thead tr:first-child th,
        table colgroup + thead tr:first-child td,
        table thead:first-child tr:first-child th,
        table thead:first-child tr:first-child td {
            border-top: 0;
        }

        table tbody + tbody {
            border-top: 2px solid #dddddd;
        }

        blockquote p {
            margin: 0;
            font-size: 17.5px;
            font-weight: 300;
            line-height: 1.25;
            background-color: #E9EADF;
        }

        blockquote {
            margin-bottom: 20px;
            font-size: 14px;
            line-height: 20px;
        }

        div p {
            margin: 0 0 10px;
            text-indent: 2em;
        }

        p img {
            border: 0;
            -ms-interpolation-mode: bicubic;
            clear: left;
        }

        #memuBox {
            position: fixed;
            _position: absolute;
            _right: expression(eval(document.documentElement.scrollRight));
            _top: expression(eval(document.documentElement.scrollTop));
            right: 0;
            top: 100px;
            border: 1px #333333 dotted;
            margin-left: 0px;
            padding-left: 0px;
            width: 200px;
            font-size: 12px;
            z-index: 100;
            background-color: #FFF;
        }

        #memuPanel {
            max-height: 400px;
            overflow: auto;
            overflow-x: hidden;
        }

        #memuPanel li a {
            margin-left: 0px;
            padding-left: 0px;
            color: #000;
            text-decoration: none;
            text-outline: none;
        }

        #memuPanel li {
            margin-left: 0px;
            padding-left: 0px;
        }

        #memuPanel li:hover, #memuPanel li:focus {
            background-color: #FFC;
        }

        #memuHead {
            margin: 0;
            padding: 0;
            height: 20px;
            background-color: #CCC;
        }
    </style>
</head>
<body>
<div id="content" style="padding:10px; margin:10px; ">
    ${content}
</div>
<br/>
<div id="memuBox">
    <div id="memuHead"></div>
    <ul id="memuPanel">
    </ul>
</div>

<div class="footer" align="right">
    <!-- script src="http://s14.cnzz.com/stat.php?id=5549716&web_id=5549716&show=pic" type="text/javascript">< / script --!>
</div>

</body>
</html>