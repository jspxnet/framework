
// power by ：chenYuan   2.0
//debug
//var console = java.lang.System.out;
//------------------------------字符串
String.prototype.deleteHtml = function (len, end) {
    if (len === undefined) {
        len = -1;
        end = "";
    }
    return converter.deleteHtml(this, len, end);
};
String.prototype.escapeEncoderHTML = function () {
    return converter.escapeEncoderHTML(this);
};
String.prototype.escapeDecodeHtml = function () {
    return converter.escapeDecodeHtml(this);
};

//清理wordTag
String.prototype.cleanWord = function () {
    var html = this.replace(/<\/?chsdate[^>]*>/gi, "");
    // Remove all SPAN tags
    html = html.replace(/<\/?SPAN[^>]*>/gi, "");
    // Remove Class attributes
    html = html.replace(/<(\w[^>]*) class=([^ |>]*)([^>]*)/gi, "<$1$3");
    // Remove Style attributes
    html = html.replace(/<(\w[^>]*) style="([^"]*)"([^>]*)/gi, "<$1$3");
    // Remove Lang attributes
    html = html.replace(/<(\w[^>]*) lang=([^ |>]*)([^>]*)/gi, "<$1$3");
    // Remove XML elements and declarations
    html = html.replace(/<\\?\?xml[^>]*>/gi, "");
    html = html.replace(/&lt;\\?\?xml:namespace[^>]*>/gi, "");
    // Remove Tags with XML namespace declarations: <o:p></o:p>
    html = html.replace(/<\/?\w+:[^>]*&gt;/gi, "");
    // Replace the &nbsp;
    html = html.replace(/&nbsp;/, " ");
    // Transform  transfer <DIV>
    var re = new RegExp("(<P)([^>]*>.*?)(<\/P>)", "gi"); // Different because of a IE 5.0 error
    return html.replace(re, "<div$2</div>").replace(/<p \/>/gi, "");
};
//切断
String.prototype.cut = function (len, send) {
    return converter.cut(this, len, send);
};
//切断 显示最后一个 / 没有就显示所有
String.prototype.cutBefore = function (f) {
    return converter.cutBefore(this, f);
};
//切断并且保留长度返回，用于生成ID
Number.prototype.keepLength = function (length) {
    return converter.getKeepLength(this, length);
};
//得到文件类型
String.prototype.getFileType = function () {
    return converter.getFileType(this);
};
//按照c的方式得到,中文算两个
String.prototype.csubstring = function (ibein, iend) {
    return converter.substring(this, ibein, iend);
};
//得到长度,中文算两个
String.prototype.getCLength = function () {
    return converter.getCLength(this);
};
//加密
String.prototype.getEncode = function () {
    return converter.getEncode(this);
};
//解密
String.prototype.getDecode = function () {
    return converter.getDecode(this);
};
//得到中文金额
String.prototype.toChineseCurrency = function () {
    return converter.toChineseCurrency(this);
};
//得到中文数字
String.prototype.toChineseNumber = function (type) {
    if (!type) return converter.toChineseNumber(this);
    else return converter.toChineseNumber(this, type);
};

//得到得到缩图文件名称
String.prototype.getThumbnailFileName = function () {
    return converter.getThumbnailFileName(this);
};

//得到手机图片文件名称
String.prototype.getMobileFileName = function () {
    return converter.getMobileFileName(this);
};

//首字母大写
String.prototype.firstUpperCase = function () {
    return this.substring(0, 1).toUpperCase() + this.substring(1, this.length);
};
//首字母小写
String.prototype.firstLowerCase = function () {
    return this.substring(0, 1).toLowerCase() + this.substring(1, this.length);
};
//得到长度,为了兼容java
String.prototype.getLength = function () {
    var o = this;
    var elen;
    if (typeof (o.length) == "number")
        elen = o.length;
    else elen = o.length();
    return elen;
};
//得到字符前的
String.prototype.substringBefore = function (prefix) {
    if (this == null || prefix == undefined) return "";
    var pos = this.indexOf(prefix);
    if (pos == -1)
        return "";
    return this.substring(0, pos);
};
//得到字符后的
String.prototype.substringAfter = function (prefix) {
    if (this == null || prefix == undefined) return "";
    var pos = this.indexOf(prefix);
    if (pos == -1)
        return "";
    return this.substring(pos + 1, this.getLength());
};
//得到
String.prototype.substringLastAfter = function (prefix) {
    if (this == null || prefix == undefined) return "";
    var pos = this.lastIndexOf(prefix);
    if (pos == -1) return "";
    return this.substring(pos + prefix.getLength(), this.getLength());
};

//得到字符传出现的次数
String.prototype.countMatches = function (c) {
    var elen = this.getLength();
    var clen = c.getLength();
    if (!this || elen < 1) return 0;
    if (elen < clen) return 0;
    var result = 0;
    for (var i = 0; i < elen - clen; i++) {
        if (c == this.substring(i, i + clen)) result++;
    }
    return result;
};
//转换为整数
String.prototype.toInt = function () {
    var result = parseInt(this.replace("px", "").trim());
    if (isNaN(result)) return 0;
    return result;
};
//转换为数字,有小数点
String.prototype.toNumber = function (dec) {
    var f = this;
    if (dec <= 0) return parseInt(this);
    var result = parseInt(this) + (dec == 0 ? "" : ".");
    f -= parseInt(f);
    if (f == 0)
        for (var i = 0; i < dec; i++) result += '0';
    else {
        for (i = 0; i < dec; i++) f *= 10;
        result += parseInt(Math.round(f));
    }
    return result;
};
//字符串转换为数字，保留小数位数
String.prototype.round = function (dec) {
    return this.toNumber(dec);
};
//去出空格
String.prototype.trim = function () {
    return this.replace(/^\s+|\s+$/g, '');
};
String.prototype.toBoolean = function () {
    return converter.toBoolean(this);
};
String.prototype.getHttp = function () {
    return converter.getHttp(this, null);
};
String.prototype.getHttp = function (encode) {
    return converter.getHttp(this, encode);
};

//是否为中文
String.prototype.isChinese = function () {
    return /^[\u0391-\uFFE5]+$/.test(this);
};
//是否为日期
String.prototype.isDate = function () {
    var r = this.match(/^(\d{1,4})(-|\/)(\d{1,2})\2(\d{1,2})$/);
    if (this.isInteger() && this.getLength() == 6) return r = this.match(/^(\d{1,2})(\d{1,2})(\d{1,2})$/);
    if (this.isInteger() && this.getLength() == 8) return r = this.match(/^(\d{1,4})(\d{1,2})(\d{1,2})$/);

    if (r == null) return false;
    var d = new Date(r[1], r[3] - 1, r[4]);
    if (this.getLength() == 6)
        return (d.getYear() == r[1] && (d.getMonth() + 1) == r[3] && d.getDate() == r[4]);
    return (d.getFullYear() == r[1] && (d.getMonth() + 1) == r[3] && d.getDate() == r[4]);
};
//判断文本输入是不是时间格式,如13:25
String.prototype.isTime = function () {
    var a = this.match(/^(\d{1,2})(:)?(\d{1,2})$/);
    if (a == null) return false;
    return !(a[1] > 24 || a[3] > 60);
};
//是否为email
String.prototype.isEmail = function () {
    if (isEmpty(this)) return false;
    var re = /^[^\s()<>@,;:\/]+@\w[\w\.-]+\.[a-z]{2,}$/i;
    return re.test(this);
};

//判断字符串中是否存在数组中的字符
String.prototype.indexOfArray = function (array) {
    if (typeof (array) == "string") return this.indexOf(array);
    for (var i = 0; i < array.length; i++)
        if (this.indexOf(array[i]) != -1) return true;
    return false;
};
//判断是否为正常的名称
String.prototype.isGoodName = function () {
    if (!this.isLengthBetween(2, 26)) return false;
    return !this.indexOfArray(['.', '\\', '/', ',', ';', '.', ' ', '^', '&', '|', '[', ']', '!', '=', '(', ')', '{', '}', '<', '>', '%']);
};
//判断URL http格式的
String.prototype.isURL = function () {
    var reg = /^http:\/\/.{0,93}/;
    var reg2 = /^https:\/\/.{0,93}/;
    var reg3 = /^rtsp:\/\/.{0,93}/;
    var reg4 = /^mms:\/\/.{0,93}/;
    return reg.test(this) || reg2.test(this) || reg3.test(this) || reg4.test(this);
};
//比较开始是否相等
String.prototype.startsWith = function (prefix, formI) {
    if (!formI)
        formI = 0;
    if (this == null) return false;
    var iend = 0;
    if (typeof (prefix) == "string") {
        iend = formI + prefix.getLength();
        return iend <= this.getLength() && this.substring(formI, iend) == prefix;
    } else {
        for (var i = 0; i < prefix.length; i++) {
            iend = formI + prefix[i].getLength();
            if ((iend <= this.getLength()) && this.substring(formI, iend) == prefix[i])
                return true;
        }
        return false;
    }
};
//比较结束是否相等
String.prototype.endWith = function (prefix) {
    if (this == null) return false;
    if (typeof (prefix) == "string") {
        return this.substring(this.getLength() - prefix.getLength(), this.getLength()) == prefix;
    } else {
        for (var i = 0; i < prefix.length; i++) {
            if (this.substring(this.getLength() - prefix[i].getLength(), this.getLength()) == prefix[i])
                return true;
        }
        return false;
    }
};
//校验手机号码：必须以数字开头，除数字外，可含有“-”
String.prototype.isMobile = function () {
    var part = /^[+]{0,1}(\d){1,3}[ ]?([-]?((\d)|[ ]){1,12})+$/;
    return part.test(this);
};
//校验QQ
String.prototype.isQQ = function () {
    var part = /^[1-9]\d{4,8}$/;
    return part.test(this);
};
//校验普通电话、传真号码：可以“+”开头，除数字外，可含有“-”
String.prototype.isPhone = function () {
    var part = /^[+]{0,1}(\d){1,3}[ ]?([-]?((\d)|[ ]){1,12})+$/;
    return part.test(this);
};
//是否为邮编
String.prototype.isPostcode = function () {
    var part = /^[a-zA-Z0-9 ]{3,12}$/;
    return part.test(this);
};
//是否为整数
String.prototype.isInteger = function () {
    var part = /^[-\+]?\d+$/;
    return part.test(this);
};
//是否为双精度数
String.prototype.isDouble = function () {
    var part = /^[-\+]?\d+(\.\d+)?$/;
    return part.test(this);
};
//是否为英文
String.prototype.isEnglish = function () {
    var part = /^[A-Za-z]+$/;
    return part.test(this);
};
//是否为正整数
String.prototype.isSafe = function () {
    var part = /^(([A-Z]*|[a-z]*|\d*|[-_\~!@#\$%\^&\*\.\(\)\[\]\{\}<>\?\\\/\'\"]*)|.{0,5})$|\s/;
    return !part.test(this);
};
//IP地址转换成对应数值
String.prototype.isIP = function () {
    var re = /^(\d+)\.(\d+)\.(\d+)\.(\d+)$/g; //匹配IP地址的正则表达式
    if (re.test(this)) {
        if (RegExp.$1 < 256 && RegExp.$2 < 256 && RegExp.$3 < 256 && RegExp.$4 < 256) return true;
    }
    return false;
};
//表达式验证
String.prototype.test = function (regex, params) {
    return ((typeof regex == 'string') ? new RegExp(regex, params) : regex).test(this);
};
//是否相等,兼容java 和 js类型字符串比较
String.prototype.equals = function (v) {
    if (v == undefined) return false;
    if (this == null && v == null) return true;
    if (typeof (v) == 'string' || typeof (v) == 'object') {
        return (this.indexOf(v) == 0 && this.getLength() == v.getLength());
    }
    return this == v;
};
//判断是否为数字
String.prototype.isNumber = function () {
    var objExp = /(^-?\d\d*\.\d*$)|(^-?\d\d*$)|(^-?\.\d\d*$)/;
    return objExp.test(this);
};
//转换为数字验证是否在范围内
String.prototype.isBetween = function (min, max) {
    if (!this.isNumber()) return false;
    return this.toNumber().isBetween(min, max);
};
// 判断长度是否在之内
String.prototype.isLengthBetween = function (min, max) {
    return (this.length >= min) && (this.length <= max);
};
//判断为日期时间 2002-1-31 12:34:56
String.prototype.isDateTime = function () {
    var r, d;
    if (this.countMatches(':') == 2) {
        r = this.match(/^(\d{1,4})(-|\/)(\d{1,2})\2(\d{1,2}) (\d{1,2}):(\d{1,2}):(\d{1,2})$/);
        d = new Date(r[1], r[3] - 1, r[4], r[5], r[6], r[7]);
        return (d.getFullYear() == r[1] && (d.getMonth() + 1) == r[3] && d.getDate() == r[4] && d.getHours() == r[5] && d.getMinutes() == r[6] && d.getSeconds() == r[7]);
    }
    if (this.countMatches(':') == 1) {
        r = this.match(/^(\d{1,4})(-|\/)(\d{1,2})\2(\d{1,2}) (\d{1,2}):(\d{1,2})$/);
        d = new Date(r[1], r[3] - 1, r[4], r[5], r[6], 0);
        return (d.getFullYear() == r[1] && (d.getMonth() + 1) == r[3] && d.getDate() == r[4] && d.getHours() == r[5] && d.getMinutes() == r[6]);
    }
    if (this.countMatches(':') == 0)
        return this.isDate();
};
String.prototype.isCardCode = function () {
    var intStrLen = this.getLength();
    if ((intStrLen != 15) && (intStrLen != 18)) return false;
    var factorArr = [7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2, 1];
    var varArray = [];
    var lngProduct = 0;
    var intCheckDigit;
    var idNumber = this;
    // check and set value
    for (var i = 0; i < intStrLen; i++) {
        varArray[i] = idNumber.charAt(i);
        if ((varArray[i] < '0' || varArray[i] > '9') && (i != 17)) {
            return false;
        } else if (i < 17) {
            varArray[i] = varArray[i] * factorArr[i];
        }
    }
    if (intStrLen == 18) {
        var date8 = idNumber.substring(6, 14);
        if (!date8.isDate()) return false;
        for (i = 0; i < 17; i++) lngProduct = lngProduct + varArray[i];
        intCheckDigit = 12 - lngProduct % 11;
        switch (intCheckDigit) {
            case 10:
                intCheckDigit = 'X';
                break;
            case 11:
                intCheckDigit = 0;
                break;
            case 12:
                intCheckDigit = 1;
                break;
        }
        if (varArray[17].toUpperCase() != intCheckDigit) return false;
    } else {
        var date6 = idNumber.substring(6, 12);
        if (!date6.isDate())
            return false;
    }
    return true;
};
String.prototype.toArray = function (sp) {
    if (sp == undefined) sp = ";";
    if (sp == '\n') return this.replace(/\r\n/g, '\n').split('\n');
    return this.split(sp);
};
String.prototype.jsonToArray = function () {
    return converter.jsonToArray(this);
};
String.prototype.toDate = function () {
    return converter.toDate(this);
};
String.prototype.url = function () {
    return encodeURI(this);
};
String.prototype.escape = function () {
    return converter.escape(this);
};
String.prototype.lines = function () {
    return converter.toBrLine(this);
};
String.prototype.quote = function (dub) {
    if (dub)
        return converter.quote(this, dub);
    else
        return converter.quote(this, false);
};
String.prototype.toScript = function () {
    return converter.toScript(this);
};
String.prototype.deleteQuote = function () {
    return converter.deleteQuote(this);
};
String.prototype.substringBetween = function (b, e) {
    return converter.substringBetween(this, b, e);
};
//\r\n转换为br
String.prototype.toBrLine = function () {
    return converter.toBrLine(this);
};
String.prototype.toBrLine = function () {
    return converter.toBrLine(this);
};
//------------------------------外部可用部分 end
String.prototype.toImage = function () {
    return this.toImage(200, 200, 0, "");
};
String.prototype.toImage = function (w, h, alt) {
    if (typeof(alt) == 'undefined') alt = '';
    return this.toImage(w, h, 0, alt);
};
String.prototype.toImage = function (w, h, b, alt) {
    var result = '<img src="' + this + '" title="' + alt + '" border="' + b + '"';
    if (w > 0) result = result + ' width="' + w + '"';
    if (h > 0) result = result + ' height="' + h + '"';
    return result + ' />';
};
//判断是否为图片后缀
String.prototype.isImage = function () {
    return converter.isImage(this);
};
//转换为附件列表
String.prototype.toAttachMap = function () {
    return converter.toAttachMap(this);
};
String.prototype.show = function (sel) {
    return converter.show(this, sel, ":");
};
String.prototype.show = function (sel, keyf) {
    return converter.show(this, sel, keyf);
};
String.prototype.checkbox = function (rname, sel) {
    return this.split(";").checkbox(rname, sel, null, ":");
};
String.prototype.checkbox = function (rname, sel, style, keyf) {
    return this.split(";").checkbox(rname, sel, style, keyf);
};
String.prototype.radio = function (rname, sel) {
    return this.radio(rname, sel, null, ":");
};
String.prototype.radio = function (rname, sel, style, keyf) {
    return this.split(";").radio(rname, sel, style, keyf);
};
String.prototype.options = function (sel) {
    return this.options(sel, ":");
};

String.prototype.options = function (sel, keyf) {
    return converter.getHtmlOptions(this, sel, keyf);
};
String.prototype.toUbb = function () {
    return converter.toUbb(this);
};
String.prototype.toSha = function () {
    return converter.toSha(this);
};
String.prototype.toMd5 = function () {
    return converter.toMd5(this);
};
String.prototype.toJstring = function () {
    return converter.toScript(this);
};
String.prototype.getSafeHtmlFilter = function () {
    return converter.getSafeHtmlFilter(this);
};
String.prototype.format = function (format) {
    return converter.format(this.toNumber(), format);
};
String.prototype.highlight = function (world) {
    return converter.getHighlight(this, world);
};
String.prototype.markdown = function () {
    return converter.getMarkdownHtml(this);
};
//创建java对象 相当 new
String.prototype.create = function (/* ... */) {
    var objArray = [];
    objArray.push(this);
    for (var i = 0; i < arguments.length; i++) {
        objArray.push(arguments[i]);
    }
    return converter.create(objArray);
};
//根据TXWeb配置得到ioc对象
String.prototype.ioc = function (/* ... */) {
    var objArray = [];
    objArray.push(this);
    for (var i = 0; i < arguments.length; i++) {
        objArray.push(arguments[i]);
    }
    return converter.getIoc(objArray);
};
//根据TXWeb配置得到action对象
String.prototype.action = function (/* ... */) {
    var objArray = [];
    objArray.push(action);
    objArray.push(this);
    for (var i = 0; i < arguments.length; i++) {
        objArray.push(arguments[i]);
    }
    return converter.getActon(objArray);
};
//---------------------------------------数字
Number.prototype.toInt = function () {
    return this.round(0);
};
Number.prototype.round = function (pos) {
    return Math.round(this * Math.pow(10, pos)) / Math.pow(10, pos);
};
Number.prototype.toDate = function () {
    return new Date(this);
};
Number.prototype.toDateString = function (fm) {
    return new Date(this).string(fm);
};
Number.prototype.string = function (y, n) {
    if (this > 0) return y;
    else return n;
};
Number.prototype.abs = function () {
    return Math.abs(this);
};
Number.prototype.limit = function (mi, mx) {
    if (this < mi) return mi;
    if (this > mx) return mx;
    return this;
};
Number.prototype.format = function (format) {
    return converter.format(this, format);
};
//切断并且保留长度返回，用于生成ID
Number.prototype.keepLength = function (length) {
    return converter.getKeepLength(this, length);
};
//判断一个数值是否在范围内
Number.prototype.isBetween = function (imin, imax) {
    return imin <= this & this <= imax;
};
Number.prototype.toFloat = function () {
    return parseFloat(this);
};
//转换为日期比较,返回小时
Number.prototype.compareHour = function (beginDate) {
    var begin = 0;
    if (typeof (beginDate) == "number") begin = beginDate;
    else begin = beginDate.getTime();
    var diffMillis = this - begin;
    return diffMillis / (60 * 60 * 1000);
};
//转换为日期比较,返回天数
Number.prototype.compareDay = function (beginDate) {
    return this.compareHour(beginDate) / 24;
};
//判断是否存在数组中
Number.prototype.indexOfArray = function (array) {
    for (var i = 0; i < array.length; i++) {
        if (this == array[i]) return true;
    }
    return false;
};
Number.prototype.toChineseCurrency = function () {
    return (this + '').toChineseCurrency();
};
//得到中文数字
Number.prototype.toChineseNumber = function (type) {
    return (this + '').toChineseNumber(type);
};
Number.prototype.equals = function (v) {
    if (v == undefined) return false;
    if (this == 0 && v == 0) return true;
    return (this == v)
};
Number.prototype.toBoolean = function () {
    return converter.toBoolean(this);
};
//--------------------------------------逻辑
Boolean.prototype.string = function (y, n) {
    if (this && ("true" == this.toString() || "t" == this.toString() || "T" == this.toString() || "1" == this.toString())) return y;
    return n;
};
Boolean.prototype.equals = function (n) {
    return this == n;
};
//-------------------------------------日期
Date.prototype.string = function (format, def) {
    //1800-01-01
    if (this.getFullYear() == 1800 && this.getMonth() < 2 && this.getDate() == 1) return def;
    return this.string(format);
};
Date.prototype.string = function (format) {
    return converter.dateFormat(this, format);
};
Date.prototype.string = function (format, def) {
    return converter.dateFormat(this, format, def);
};
Date.prototype.compareHour = function (beginDate) {
    var diffMillis = this.getTime() - beginDate.getTime();
    return diffMillis / (60 * 60 * 1000);
};
Date.prototype.compareDay = function (beginDate) {
    var diffMillis = this.getTime() - beginDate.getTime();
    return diffMillis / (24 * 60 * 60 * 1000);
};
Date.prototype.isToDay = function () {
    return converter.isToDay(this);
};
//比较日期，返回毫秒
Date.prototype.getTimeInMillis = function () {
    return converter.getTimeInMillis(this)
};
Date.prototype.addMilliseconds = function (value) {
    this.setMilliseconds(this.getMilliseconds() + value);
    return this;
};
Date.prototype.addSeconds = function (value) {
    return this.addMilliseconds(value * 1000);
};
Date.prototype.addMinutes = function (value) {
    return this.addMilliseconds(value * 60000);
};
Date.prototype.addHours = function (value) {
    return this.addMilliseconds(value * 3600000);
};
Date.prototype.addDays = function (value) {
    return this.addMilliseconds(value * 86400000);
};
Date.prototype.addWeeks = function (value) {
    return this.addMilliseconds(value * 604800000);
};
Date.prototype.addMonths = function (value) {
    var n = this.getDate();
    this.setDate(1);
    this.setMonth(this.getMonth() + value);
    this.setDate(Math.min(n, this.getDaysInMonth()));
    return this;
};
Date.prototype.addYears = function (value) {
    return this.addMonths(value * 12);
};
Date.prototype.clearTime = function () {
    this.setHours(0);
    this.setMinutes(0);
    this.setSeconds(0);
    this.setMilliseconds(0);
    return this;
};
Date.prototype.isLeapYear = function () {
    var y = this.getFullYear();
    return (((y % 4 == 0) && (y % 100 != 0)) || (y % 400 == 0));
};
Date.prototype.between = function (beginDate, end) {
    var ltime = this.getTime();
    var lbegin = 0;
    if (typeof (beginDate) == "number") lbegin = beginDate;
    else lbegin = beginDate.getTime();
    var lend = 0;
    if (typeof (end) == "number") lend = end;
    else lend = end.getTime();
    return lbegin < ltime && ltime < lend;
};
//得到星座
Date.prototype.toBirthStar = function () {
    return converter.getBirthStar(this);
};
//和现在比较日期，返回数组 年，月，日
Date.prototype.getCompareDate = function () {
    return converter.getCompareDate(this);
};
Date.prototype.getTimeFormatText = function () {
    return converter.getTimeFormatText(this, "zh");
};
Date.prototype.getTimeFormatText = function (lan) {
    return converter.getTimeFormatText(this, lan);
};
//sql中使用
Date.prototype.quote = function (format) {
    return converter.dateFormatQuote(this,format);
};
//--------------------------------------数组
//向数组中添加一项, 如果该项在数组中已经存在,则不再添加.
Array.prototype.include = function (item) {
    if (!this.contains(item)) this.push(item);
    return this;
};
//将主调数组和另一个数组进行组合(重复的项将不会加入)
Array.prototype.combine = function (array) {
    for (var i = 0, l = array.length; i < l; i++) this.include(array[i]);
    return this;
};
//myArray.combine(array);
Array.prototype.sum = function () {
    var a = this;
    if ((a instanceof Array) || // if array
        (a && typeof a == "object" && "length" in a)) { // or array like
        var total = 0;
        for (var i = 0; i < a.length; i++) {
            var element = a[i];
            if (!element) continue;  // ignore null and undefined elements
            if (typeof element == "number") total += element;
            else throw new Error("sum(): all array elements must be numbers");
        }
        return total;
    } else throw new Error("sum(): argument must be an array");
};
Array.prototype.max = function () {
    var m = Number.NEGATIVE_INFINITY;
    for (var i = 0; i < this.length; i++)
        if (this[i] > m) m = this[i];
};
Array.prototype.min = function () {
    var m = Number.MAX_VALUE;
    for (var i = 0; i < this.length; i++)
        if (this[i] < m) m = this[i];
    return m;
};
Array.prototype.avg = function () {
    var a = this;
    if ((a instanceof Array) || // if array
        (a && typeof a == "object" && "length" in a)) { // or array like
        var m = 0;
        for (var i = 0; i < a.length; i++)
            m = a[i] + m;
        return m / a.length;
    } else throw new Error("avg(): argument must be an array");
};
//判断是否在数组
Array.prototype.contains = function (item) {
    return this.indexOf(item) !== -1;
};
//判断是否在数组,并返回位置
Array.prototype.indexOf = function (item, from) {
    if (from === undefined) from = 0;
    var len = this.length;
    for (var i = (from < 0) ? Math.max(0, len + from) : from || 0; i < len; i++) {
        if (this[i].equals(item)) return i;
    }
    return -1;
};
Array.prototype.getLast = function () {
    return (this.length) ? this[this.length - 1] : null;
};
Array.prototype.erase = function (item) {
    for (var i = this.length; i--; i) {
        if (this[i] === item) this.splice(i, 1);
    }
};
Array.prototype.empty = function () {
    this.length = 0;
    return this;
};
Array.prototype.options = function (sel) {

    return this.options(sel, null);
};
Array.prototype.options = function (sel, keyf) {
    return converter.getHtmlOptions(this, sel, keyf);
};
Array.prototype.string = function (sel) {
    if (sel === undefined) sel = ';';
    var result = "";
    for (var i = 0; i < this.length; i++) {
        result = result + sel + this[i];
    }
    if (result.length > sel.length) result = result.substring(sel.length, result.length);
    return result;
};
Array.prototype.show = function (sel, keyf) {
    return converter.show(this, sel, keyf);
};
Array.prototype.radio = function (rname, sel) {
    return this.radio(rname, sel, null, ":");
};
Array.prototype.radio = function (rname, sel, style, keyf) {
    if (!keyf || keyf === undefined) keyf = ":";
    var out = "";
    for (var i = 0; i < this.length; i++) {
        if (!this[i]) continue;
        var hav = this[i].indexOf(keyf);
        if (hav === -1) {
            out = out + '<label class="radioLabel' + rname + '">';
            out = out + ' <input id="' + rname + i + '" name="' + rname + '" type="radio" value="' + this[i] + '"';
            if (this[i].equals(sel)) out = out + ' checked="checked"';
            if (style && style !== "") out = out + ' style="' + style + '" ';
            out = out + ' />' + this[i] + '</label>';
        } else {
            var keys = this[i].substring(0, hav);
            var vars = this[i].substring(hav + 1, this[i].getLength());
            out = out + '<label class="radioLabel' + rname + '">';
            out = out + '<input id="' + rname + i + '" name="' + rname + '" type="radio" value="' + keys + '" ';
            if (keys === sel || keys.equals(sel)) out = out + 'checked="checked" ';
            if (style && style !== "") out = out + 'style="' + style + '" ';
            out = out + ' />' + vars + '</label>';
        }
    }
    return out;
};
Array.prototype.checkbox = function (name, sel) {
    return this.checkbox(name, sel, null, ":");
};
Array.prototype.checkbox = function (rname, selected, style, keyf) {
    if (!keyf || keyf === undefined) keyf = ":";
    var sel;
    if (typeof (selected) == "string") sel = selected.split(";");
    else sel = selected;
    var out = "";
    for (var i = 0; i < this.length; i++) {
        if (!this[i]) continue;
        var hav = this[i].indexOf(keyf);
        if (hav === -1) {
            out = out + '<label class="checkLabel' + rname + '">';
            out = out + ' <input id="' + rname + i + '" name="' + rname + '" type="checkbox" value="' + this[i] + '" ';
            if (sel && sel.indexOf(this[i]) !== -1) out = out + 'checked="checked" ';
            if (style && style != "") out = out + 'style="' + style + '" ';
            out = out + ' />' + this[i] + '</label>';
        } else {
            var keys = this[i].substring(0, hav);
            var vars = this[i].substring(hav + 1, this[i].getLength());
            out = out + '<label class="checkLabel' + rname + '">';
            out = out + '<input id="' + rname + i + '" name="' + rname + '" type="checkbox" value="' + keys + '"';
            if (sel && sel.indexOf(keys) !== -1) out = out + ' checked="checked" ';
            if (style && style != "") out = out + 'style="' + style + '" ';
            out = out + ' />' + vars + '</label>';
        }
    }
    return out;
};
//-------------------------对象
Object.prototype.Clone = function () {
    var objClone;
    if (this.constructor === Object) objClone = new this.constructor();
    else objClone = new this.constructor(this.valueOf());
    for (var key in this) {
        if (objClone[key] !== this[key]) {
            if (typeof (this[key]) == "object") objClone[key] = this[key].Clone();
            else objClone[key] = this[key];
        }
    }
    objClone.toString = this.toString;
    objClone.valueOf = this.valueOf;
    return objClone;
};
Object.prototype.options = function (sel, keyF) {
    return converter.getHtmlOptions(this, sel, keyF);
};
Object.prototype.show = function (sel) {
    return converter.show(this, sel);
};
Object.prototype.toBoolean = function () {
    return converter.toBoolean(this);
};
Object.prototype.toString = function () {
    return converter.toJson(this);
};
//--------------------------------------全局函数
//判断是否为空
function isEmpty(v) {
    if("undefined" === typeof(v)) return false;
    return converter.isEmpty(v);
}
//最大值
//noinspection JSDuplicatedDeclaration
function max(/* ... */) {
    var m = Number.NEGATIVE_INFINITY;
    for (var i = 0; i < arguments.length; i++)
        if (arguments[i] > m) m = arguments[i];
    return m;
}

//最小值
//noinspection JSDuplicatedDeclaration
function min(/* ... */) {
    var m = Number.MAX_VALUE;
    for (var i = 0; i < arguments.length; i++)
        if (arguments[i] < m) m = arguments[i];
    return m;
}

//合计
//noinspection JSDuplicatedDeclaration
function sum(/* ... */) {
    var m = 0;
    for (var i = 0; i < arguments.length; i++) m = arguments[i] + m;
    return m;
}

//平均
//noinspection JSDuplicatedDeclaration
function avg(/* ... */) {
    var m = 0;
    for (var i = 0; i < arguments.length; i++)
        m = arguments[i] + m;
    return m / arguments.length;
}

//noinspection JSDuplicatedDeclaration
function json(o) {
    return converter.toJson(o);
}

//noinspection JSDuplicatedDeclaration
function xml(o) {
    return converter.toXml(o);
}

//得到 FusionChart XML showName 表示是否显示标题
function getFusionChartXML(o, showName) {
    if (!showName) showName = 1;
    return converter.getFusionChartXML(o, showName);
}

//noinspection JSDuplicatedDeclaration
function jsonXml(o) {
    return converter.toJsonXml(o);
}

//得到一个随机数
function random(low, high) {
    return Math.floor(Math.random() * (1 + high - low) + low);
}

//action 整合，得到字段选项
function getFields(cla) {
    return converter.getFields(cla);
}

//得到class table 里边配置的选项
function getOptions(cla, field) {
    return converter.getOptions(cla, field);
}

function isJson(str) {
    return converter.isJson(str);
}

//----------------------------------------------------------------------------------------------------------------------
String.prototype.trim = function () {
    return this.replace(/(^\s*)|(\s*$)/g, "");
};
/*global module:true*/
/*
 * Basic table support with re-entrant parsing, where cell content
 * can also specify markdown.
 *
 * Tables
 * ======
 *
 * | Col 1   | Col 2                                              |
 * |======== |====================================================|
 * |**bold** | ![Valid XHTML] (http://w3.org/Icons/valid-xhtml10) |
 * | Plain   | Value                                              |
 *
 */
var tableFilter = function (tableConverter) {
    var tables = {}, style = 'text-align:left;';
    tables.th = function (header) {
        if (header.trim() === "") {
            return "";
        }
        return '<th style="' + style + '">' + header + '</th>';
    };
    tables.td = function (cell) {
        return '<td style="' + style + '">' + tableConverter.makeHtml(cell) + '</td>';
    };
    tables.ths = function () {
        var out = "", i = 0, hs = [].slice.apply(arguments);
        for (i; i < hs.length; i += 1) {
            out += tables.th(hs[i]) + '\n';
        }
        return out;
    };
    tables.tds = function () {
        var out = "", i = 0, ds = [].slice.apply(arguments);
        for (i; i < ds.length; i += 1) {
            out += tables.td(ds[i]);
        }
        return out;
    };
    tables.thead = function () {
        var out, hs = [].slice.apply(arguments);
        out = "<thead>\n";
        out += "<tr>\n";
        out += tables.ths.apply(this, hs);
        out += "</tr>\n";
        out += "</thead>\n";
        return out;
    };
    tables.tr = function () {
        var out, cs = [].slice.apply(arguments);
        out = "<tr>";
        out += tables.tds.apply(this, cs);
        out += "</tr>";
        return out;
    };
    filter = function (text) {
        var i = 0, lines = text.split('\n'), tbl = [], line, hs, rows, out = [];
        for (i; i < lines.length; i += 1) {
            line = lines[i];
            // looks like a table heading

            if (line.trim().match(/^[|]{1}.*[|]{1}$/)) {

                line = line.trim();
                tbl.push('<table>');
                hs = line.substring(1, line.length - 1).split('|');
                tbl.push(tables.thead.apply(this, hs));
                line = lines[++i];

                if (!line.trim().match(/^[|]{1}[-=| ]+[|]{1}$/)) {
                    // not a table rolling back
                    //line = lines[--i];
                    line = lines[++i];

                    tbl.push('<tbody>');
                    while (line.trim().match(/^[|]{1}.*[|]{1}$/)) {
                        line = line.trim();
                        tbl.push(tables.tr.apply(this, line.substring(1, line.length - 1).split('|')));
                        line = lines[++i];
                    }
                    tbl.push('</tbody>');
                    tbl.push('</table>');

                    // we are done with this table and we move along
                    out.push(tbl.join('\n'));
                    tbl = [];
                    continue;
                }
            }
            out.push(line);
        }
        return out.join('\n');
    };
    return [
        {
            type: 'lang',
            filter: filter
        }
    ];
};
//----------------------------------------------------------------------------------
//
//  Github Extension (WIP)
//  ~~strike-through~~   ->  <del>strike-through</del>
//
var githubFilter = function (converter) {
    return [
        {
            // strike-through
            // NOTE: showdown already replaced "~" with "~T", so we need transfer adjust accordingly.
            type: 'lang',
            regex: '(~T){2}([^~]+)(~T){2}',
            replace: function (match, prefix, content, suffix) {
                return '<del>' + content + '</del>';
            }
        }
    ];
};
//------------------------------------------------------------------
// Showdown usage:
//
//   var text = "Markdown *rocks*.";
//
//   var converter = new Showdown.converter();
//   var html = showdownConverter.makeHtml(text);
//   alert(html);
//
// Note: move the sample code transfer the bottom of this
// file before uncommenting it.
//
//
// Showdown namespace
//
var Showdown = {extensions: {}};

//
// forEach
//
var forEach = Showdown.forEach = function (obj, callback) {
    if (typeof obj.forEach === 'function') {
        obj.forEach(callback);
    } else {
        var i, len = obj.length;
        for (i = 0; i < len; i++) {
            callback(obj[i], i, obj);
        }
    }
};

//
// Standard extension naming
//
var stdExtName = function (s) {
    return s.replace(/[_-]||\s/g, '').toLowerCase();
};

//
// converter
//
// Wraps all "globals" so that the only thing
// exposed is makeHtml().
//
Showdown.converter = function () {
    var showdownConverter = this;
// Global hashes, used by various utility routines
    var g_urls;
    var g_titles;
    var g_html_blocks;

// Used transfer track when we're inside an ordered or unordered list
// (see _ProcessListItems() for details):
    var g_list_level = 0;

// Global extensions
    var g_lang_extensions = ['tables', 'github'];
    var g_output_modifiers = [];

    this.makeHtml = function (text) {
//
// Main function. The order in which other subs are called here is
// essential. Link and image substitutions need transfer happen before
// _EscapeSpecialCharsWithinTagAttributes(), so that any *'s or _'s in the <a>
// and <img> tags get encoded.
//

        // Clear the global hashes. If we don't clear these, you get conflicts
        // from other articles when generating a page which contains more than
        // one article (e.g. an index page that shows the N most recent
        // articles):
        g_urls = {};
        g_titles = {};
        g_html_blocks = [];

        // attacklab: Replace ~ with ~T
        // This lets us use tilde as an escape char transfer avoid md5 hashes
        // The choice of character is arbitray; anything that isn't
        // magic in Markdown will work.
        text = text.replace(/~/g, "~T");

        // attacklab: Replace $ with ~D
        // RegExp interprets $ as a special character
        // when it's in a replacement string
        text = text.replace(/\$/g, "~D");

        // Standardize line endings
        text = text.replace(/\r\n/g, "\n"); // DOS transfer Unix
        text = text.replace(/\r/g, "\n"); // Mac transfer Unix

        // Make sure text begins and ends with a couple of newlines:
        text = "\n\n" + text + "\n\n";

        // Convert all tabs transfer spaces.
        text = _Detab(text);

        // Strip any lines consisting only of spaces and tabs.
        // This makes subsequent regexen easier transfer write, because we can
        // match consecutive blank lines with /\n+/ instead of something
        // contorted like /[ \t]*\n+/ .
        text = text.replace(/^[ \t]+$/mg, "");

        // Run language extensions

        Showdown.forEach(g_lang_extensions, function (x) {
            text = _ExecuteExtension(x, text);
        });

        // Handle github codeblocks prior transfer running HashHTML so that
        // HTML contained within the codeblock gets escaped propertly
        text = _DoGithubCodeBlocks(text);

        // Turn block-level HTML blocks into hash entries
        text = _HashHTMLBlocks(text);

        // Strip link definitions, store in hashes.
        text = _StripLinkDefinitions(text);

        text = _RunBlockGamut(text);

        text = _UnescapeSpecialChars(text);

        // attacklab: Restore dollar signs
        text = text.replace(/~D/g, "$$");

        // attacklab: Restore tildes
        text = text.replace(/~T/g, "~");

        // Run output modifiers
        Showdown.forEach(g_output_modifiers, function (x) {
            text = _ExecuteExtension(x, text);
        });
        return text;
    };
//
// Options:
//

    var _ExecuteExtension = function (ext, text) {
        if (ext == 'tables') return tableFilter(showdownConverter)[0].filter(text);
        if (ext == 'github') {
            var re = new RegExp(githubFilter(showdownConverter)[0].regex, 'g');
            return text.replace(re, githubFilter(showdownConverter)[0].replace);
        }
        /*
         if (ext.regex) {
         var re = new RegExp(ext.regex, 'g');
         return text.replace(re, ext.replace);
         } else if (ext.filter) {
         return ext.filter(text);
         }
         */
        return text;
    };

    var _StripLinkDefinitions = function (text) {
//
// Strips link definitions from text, stores the URLs and titles in
// hash references.
//

        // Link defs are in the form: ^[id]: url "optional title"

        /*
         var text = text.replace(/
         ^[ ]{0,3}\[(.+)\]:  // id = $1  attacklab: g_tab_width - 1
         [ \t]*
         \n?				// maybe *one* newline
         [ \t]*
         <?(\S+?)>?			// url = $2
         [ \t]*
         \n?				// maybe one newline
         [ \t]*
         (?:
         (\n*)				// any lines skipped = $3 attacklab: lookbehind removed
         ["(]
         (.+?)				// title = $4
         [")]
         [ \t]*
         )?					// title is optional
         (?:\n+|$)
         /gm,
         function(){...});
         */


        text = text.replace(/^[ ]{0,3}\[(.+)\]:[ \t]*\n?[ \t]*<?(\S+?)>?[ \t]*\n?[ \t]*(?:(\n*)["(](.+?)[")][ \t]*)?(?:\n+|(?=~0))/gm,
            function (wholeMatch, m1, m2, m3, m4) {
                m1 = m1.toLowerCase();
                g_urls[m1] = _EncodeAmpsAndAngles(m2);  // Link IDs are case-insensitive
                if (m3) {
                    // Oops, found blank lines, so it's not a title.
                    // Put back the parenthetical statement we stole.
                    return m3 + m4;
                } else if (m4) {
                    g_titles[m1] = m4.replace(/"/g, "&quot;");
                }

                // Completely remove the definition from the text
                return "";
            }
        );
        return text;
    };


    var _HashHTMLBlocks = function (text) {
        // attacklab: Double up blank lines transfer reduce lookaround
        text = text.replace(/\n/g, "\n\n");

        // Hashify HTML blocks:
        // We only want transfer do this for block-level HTML tags, such as headers,
        // lists, and tables. That's because we still want transfer wrap s around
        // "paragraphs" that are wrapped in non-block-level tags, such as anchors,
        // phrase emphasis, and spans. The list of tags we're looking for is
        // hard-coded:
        var block_tags_a = "p|div|h[1-6]|blockquote|pre|table|dl|ol|ul|script|noscript|form|fieldset|iframe|math|ins|del|style|section|header|footer|nav|article|aside|menu|footer";
        var block_tags_b = "p|div|h[1-6]|blockquote|pre|table|dl|ol|ul|script|noscript|form|fieldset|iframe|math|style|section|header|footer|nav|article|aside|menu|footer";

        // First, look for nested blocks, e.g.:
        //   <div>
        //     <div>
        //     tags for inner block must be indented.
        //     </div>
        //   </div>
        //
        // The outermost tags must start at the left margin for this transfer match, and
        // the inner nested divs must be indented.
        // We need transfer do this before the next, more liberal match, because the next
        // match will start at the first `<div>` and stop at the first `</div>`.

        // attacklab: This regex can be expensive when it fails.
        /*
         var text = text.replace(/
         (						// save in $1
         ^					// start of line  (with /m)
         <($block_tags_a)	// start tag = $2
         \b					// word break
         // attacklab: hack around khtml/pcre bug...
         [^\r]*?\n			// any number of lines, minimally matching
         </\2>				// the matching end tag
         [ \t]*				// trailing spaces/tabs
         (?=\n+)				// followed by a newline
         )						// attacklab: there are sentinel newlines at end of document
         /gm,function(){...}};
         */
        text = text.replace(/^(<(p|div|h[1-6]|blockquote|pre|table|dl|ol|ul|script|noscript|form|fieldset|iframe|math|ins|del)\b[^\r]*?\n<\/\2>[ \t]*(?=\n+))/gm, hashElement);

        //
        // Now match more liberally, simply from `\n<tag>` transfer `</tag>\n`
        //

        /*
         var text = text.replace(/
         (						// save in $1
         ^					// start of line  (with /m)
         <($block_tags_b)	// start tag = $2
         \b					// word break
         // attacklab: hack around khtml/pcre bug...
         [^\r]*?				// any number of lines, minimally matching
         </\2>				// the matching end tag
         [ \t]*				// trailing spaces/tabs
         (?=\n+)				// followed by a newline
         )						// attacklab: there are sentinel newlines at end of document
         /gm,function(){...}};
         */
        text = text.replace(/^(<(p|div|h[1-6]|blockquote|pre|table|dl|ol|ul|script|noscript|form|fieldset|iframe|math|style|section|header|footer|nav|article|aside)\b[^\r]*?<\/\2>[ \t]*(?=\n+)\n)/gm, hashElement);

        // Special case just for <hr />. It was easier transfer make a special case than
        // transfer make the other regex more complicated.

        /*
         text = text.replace(/
         (						// save in $1
         \n\n				// Starting after a blank line
         [ ]{0,3}
         (<(hr)				// start tag = $2
         \b					// word break
         ([^<>])*?			//
         \/?>)				// the matching end tag
         [ \t]*
         (?=\n{2,})			// followed by a blank line
         )
         /g,hashElement);
         */
        text = text.replace(/(\n[ ]{0,3}(<(hr)\b([^<>])*?\/?>)[ \t]*(?=\n{2,}))/g, hashElement);

        // Special case for standalone HTML comments:

        /*
         text = text.replace(/
         (						// save in $1
         \n\n				// Starting after a blank line
         [ ]{0,3}			// attacklab: g_tab_width - 1
         <!
         (--[^\r]*?--\s*)+
         >
         [ \t]*
         (?=\n{2,})			// followed by a blank line
         )
         /g,hashElement);
         */
        text = text.replace(/(\n\n[ ]{0,3}<!(--[^\r]*?--\s*)+>[ \t]*(?=\n{2,}))/g, hashElement);

        // PHP and ASP-style processor instructions (<?...?> and <%...%>)

        /*
         text = text.replace(/
         (?:
         \n\n				// Starting after a blank line
         )
         (						// save in $1
         [ ]{0,3}			// attacklab: g_tab_width - 1
         (?:
         <([?%])			// $2
         [^\r]*?
         \2>
         )
         [ \t]*
         (?=\n{2,})			// followed by a blank line
         )
         /g,hashElement);
         */
        text = text.replace(/(?:\n\n)([ ]{0,3}(?:<([?%])[^\r]*?\2>)[ \t]*(?=\n{2,}))/g, hashElement);

        return text.replace(/\n\n/g, "\n");
    };

    var hashElement = function (wholeMatch, m1) {
        var blockText = m1;

        // Undo double lines
        blockText = blockText.replace(/\n\n/g, "\n");
        blockText = blockText.replace(/^\n/, "");

        // strip trailing blank lines
        blockText = blockText.replace(/\n+$/g, "");

        // Replace the element text with a marker ("~KxK" where x is its key)
        blockText = "\n\n~K" + (g_html_blocks.push(blockText) - 1) + "K\n\n";

        return blockText;
    };

    var _RunBlockGamut = function (text) {
//
// These are all the transformations that form block-level
// tags like paragraphs, headers, and list items.
//
        text = _DoHeaders(text);

        // Do Horizontal Rules:
        var key = hashBlock("<hr />");
        text = text.replace(/^[ ]{0,2}([ ]?\*[ ]?){3,}[ \t]*$/gm, key);
        text = text.replace(/^[ ]{0,2}([ ]?\-[ ]?){3,}[ \t]*$/gm, key);
        text = text.replace(/^[ ]{0,2}([ ]?\_[ ]?){3,}[ \t]*$/gm, key);

        text = _DoLists(text);
        text = _DoCodeBlocks(text);
        text = _DoBlockQuotes(text);

        // We already ran _HashHTMLBlocks() before, in Markdown(), but that
        // was transfer escape raw HTML in the original Markdown source. This time,
        // we're escaping the markup we've just created, so that we don't wrap
        //  tags around block-level tags.
        text = _HashHTMLBlocks(text);
        text = _FormParagraphs(text);

        return text;
    };


    var _RunSpanGamut = function (text) {
//
// These are all the transformations that occur *within* block-level
// tags like paragraphs, headers, and list items.
//

        text = _DoCodeSpans(text);
        text = _EscapeSpecialCharsWithinTagAttributes(text);
        text = _EncodeBackslashEscapes(text);

        // Process anchor and image tags. Images must come first,
        // because ![foo][f] looks like an anchor.
        text = _DoImages(text);
        text = _DoAnchors(text);

        // Make links out of things like `<http://example.com/>`
        // Must come after _DoAnchors(), because you can use < and >
        // delimiters in inline links like [this](<url>).
        text = _DoAutoLinks(text);
        text = _EncodeAmpsAndAngles(text);
        text = _DoItalicsAndBold(text);

        // Do hard breaks:
        text = text.replace(/  +\n/g, " \n");

        return text;
    };

    var _EscapeSpecialCharsWithinTagAttributes = function (text) {
//
// Within tags -- meaning between < and > -- encode [\ ` * _] so they
// don't conflict with their use in Markdown for code, italics and strong.
//

        // Build a regex transfer find HTML tags and comments.  See Friedl's
        // "Mastering Regular Expressions", 2nd Ed., pp. 200-201.
        var regex = /(<[a-z\/!$]("[^"]*"|'[^']*'|[^'">])*>|<!(--.*?--\s*)+>)/gi;

        text = text.replace(regex, function (wholeMatch) {
            var tag = wholeMatch.replace(/(.)<\/?code>(?=.)/g, "$1`");
            tag = escapeCharacters(tag, "\\`*_");
            return tag;
        });

        return text;
    };

    var _DoAnchors = function (text) {
//
// Turn Markdown link shortcuts into XHTML <a> tags.
//
        //
        // First, handle reference-style links: [link text] [id]
        //

        /*
         text = text.replace(/
         (							// wrap whole match in $1
         \[
         (
         (?:
         \[[^\]]*\]		// allow brackets nested one level
         |
         [^\[]			// or anything else
         )*
         )
         \]

         [ ]?					// one optional space
         (?:\n[ ]*)?				// one optional newline followed by spaces

         \[
         (.*?)					// id = $3
         \]
         )()()()()					// pad remaining backreferences
         /g,_DoAnchors_callback);
         */
        text = text.replace(/(\[((?:\[[^\]]*\]|[^\[\]])*)\][ ]?(?:\n[ ]*)?\[(.*?)\])()()()()/g, writeAnchorTag);

        //
        // Next, inline-style links: [link text](url "optional title")
        //

        /*
         text = text.replace(/
         (						// wrap whole match in $1
         \[
         (
         (?:
         \[[^\]]*\]	// allow brackets nested one level
         |
         [^\[\]]			// or anything else
         )
         )
         \]
         \(						// literal paren
         [ \t]*
         ()						// no id, so leave $3 empty
         <?(.*?)>?				// href = $4
         [ \t]*
         (						// $5
         (['"])				// quote char = $6
         (.*?)				// Title = $7
         \6					// matching quote
         [ \t]*				// ignore any spaces/tabs between closing quote and )
         )?						// title is optional
         \)
         )
         /g,writeAnchorTag);
         */
        text = text.replace(/(\[((?:\[[^\]]*\]|[^\[\]])*)\]\([ \t]*()<?(.*?(?:\(.*?\).*?)?)>?[ \t]*((['"])(.*?)\6[ \t]*)?\))/g, writeAnchorTag);

        //
        // Last, handle reference-style shortcuts: [link text]
        // These must come last in case you've also got [link testaio][1]
        // or [link testaio](/foo)
        //

        /*
         text = text.replace(/
         (		 					// wrap whole match in $1
         \[
         ([^\[\]]+)				// link text = $2; can't contain '[' or ']'
         \]
         )()()()()()					// pad rest of backreferences
         /g, writeAnchorTag);
         */
        text = text.replace(/(\[([^\[\]]+)\])()()()()()/g, writeAnchorTag);

        return text;
    };

    var writeAnchorTag = function (wholeMatch, m1, m2, m3, m4, m5, m6, m7) {
        if (m7 == undefined) m7 = "";
        var whole_match = m1;
        var link_text = m2;
        var link_id = m3.toLowerCase();
        var url = m4;
        var title = m7;

        if (url == "") {
            if (link_id == "") {
                // lower-case and turn embedded newlines into spaces
                link_id = link_text.toLowerCase().replace(/ ?\n/g, " ");
            }
            url = "#" + link_id;

            if (g_urls[link_id] != undefined) {
                url = g_urls[link_id];
                if (g_titles[link_id] != undefined) {
                    title = g_titles[link_id];
                }
            } else {
                if (whole_match.search(/\(\s*\)$/m) > -1) {
                    // Special case for explicit empty url
                    url = "";
                } else {
                    return whole_match;
                }
            }
        }

        url = escapeCharacters(url, "*_");
        var result = "<a href=\"" + url + "\"";

        if (title != "") {
            title = title.replace(/"/g, "&quot;");
            title = escapeCharacters(title, "*_");
            result += " title=\"" + title + "\"";
        }

        result += ">" + link_text + "</a>";

        return result;
    };


    var _DoImages = function (text) {
//
// Turn Markdown image shortcuts into <img> tags.
//
        //
        // First, handle reference-style labeled images: ![alt text][id]
        //

        /*
         text = text.replace(/
         (						// wrap whole match in $1
         !\[
         (.*?)				// alt text = $2
         \]

         [ ]?				// one optional space
         (?:\n[ ]*)?			// one optional newline followed by spaces

         \[
         (.*?)				// id = $3
         \]
         )()()()()				// pad rest of backreferences
         /g,writeImageTag);
         */
        text = text.replace(/(!\[(.*?)\][ ]?(?:\n[ ]*)?\[(.*?)\])()()()()/g, writeImageTag);

        //
        // Next, handle inline images:  ![alt text](url "optional title")
        // Don't forget: encode * and _

        /*
         text = text.replace(/
         (						// wrap whole match in $1
         !\[
         (.*?)				// alt text = $2
         \]
         \s?					// One optional whitespace character
         \(					// literal paren
         [ \t]*
         ()					// no id, so leave $3 empty
         <?(\S+?)>?			// src url = $4
         [ \t]*
         (					// $5
         (['"])			// quote char = $6
         (.*?)			// title = $7
         \6				// matching quote
         [ \t]*
         )?					// title is optional
         \)
         )
         /g,writeImageTag);
         */
        text = text.replace(/(!\[(.*?)\]\s?\([ \t]*()<?(\S+?)>?[ \t]*((['"])(.*?)\6[ \t]*)?\))/g, writeImageTag);
        return text;
    };

    var writeImageTag = function (wholeMatch, m1, m2, m3, m4, m5, m6, m7) {
        var whole_match = m1;
        var alt_text = m2;
        var link_id = m3.toLowerCase();
        var url = m4;
        var title = m7;

        if (!title) title = "";

        if (url == "") {
            if (link_id == "") {
                // lower-case and turn embedded newlines into spaces
                link_id = alt_text.toLowerCase().replace(/ ?\n/g, " ");
            }
            url = "#" + link_id;

            if (g_urls[link_id] != undefined) {
                url = g_urls[link_id];
                if (g_titles[link_id] != undefined) {
                    title = g_titles[link_id];
                }
            } else {
                return whole_match;
            }
        }

        alt_text = alt_text.replace(/"/g, "&quot;");
        url = escapeCharacters(url, "*_");
        var result = "<img src=\"" + url + "\" alt=\"" + alt_text + "\"";

        // attacklab: Markdown.pl adds empty title attributes transfer images.
        // Replicate this bug.

        //if (title != "") {
        title = title.replace(/"/g, "&quot;");
        title = escapeCharacters(title, "*_");
        result += " title=\"" + title + "\"";
        //}

        result += " />";

        return result;
    };


    var _DoHeaders = function (text) {

        // Setext-style headers:
        //	Header 1
        //	========
        //
        //	Header 2
        //	--------
        //
        text = text.replace(/^(.+)[ \t]*\n=+[ \t]*\n+/gm,
            function (wholeMatch, m1) {
                return hashBlock('<h1 id="' + headerId(m1) + '">' + _RunSpanGamut(m1) + "</h1>");
            });

        text = text.replace(/^(.+)[ \t]*\n-+[ \t]*\n+/gm,
            function (matchFound, m1) {
                return hashBlock('<h2 id="' + headerId(m1) + '">' + _RunSpanGamut(m1) + "</h2>");
            });

        // atx-style headers:
        //  # Header 1
        //  ## Header 2
        //  ## Header 2 with closing hashes ##
        //  ...
        //  ###### Header 6
        //

        /*
         text = text.replace(/
         ^(\#{1,6})				// $1 = string of #'s
         [ \t]*
         (.+?)					// $2 = Header text
         [ \t]*
         \#*						// optional closing #'s (not counted)
         \n+
         /gm, function() {...});
         */

        text = text.replace(/^(\#{1,6})[ \t]*(.+?)[ \t]*\#*\n+/gm,
            function (wholeMatch, m1, m2) {
                var h_level = m1.length;
                return hashBlock("<h" + h_level + ' id="' + headerId(m2) + '">' + _RunSpanGamut(m2) + "</h" + h_level + ">");
            });

        function headerId(m) {
            return converter.makePinYinName(m);
        }

        return text;
    };

// This declaration keeps Dojo compressor from outputting garbage:
    var _ProcessListItems;

    var _DoLists = function (text) {
//
// Form HTML ordered (numbered) and unordered (bulleted) lists.
//
        // Re-usable pattern transfer match any entirel ul or ol list:
        /*
         var whole_list = /
         (									// $1 = whole list
         (								// $2
         [ ]{0,3}					// attacklab: g_tab_width - 1
         ([*+-]|\d+[.])				// $3 = first list item marker
         [ \t]+
         )
         [^\r]+?
         (								// $4
         ~0							// sentinel for workaround; should be $
         |
         \n{2,}
         (?=\S)
         (?!							// Negative lookahead for another list item marker
         [ \t]*
         (?:[*+-]|\d+[.])[ \t]+
         )
         )
         )/g
         */
        var whole_list = /^(([ ]{0,3}([*+-]|\d+[.])[ \t]+)[^\r]+?(~0|\n{2,}(?=\S)(?![ \t]*(?:[*+-]|\d+[.])[ \t]+)))/gm;

        if (g_list_level) {
            text = text.replace(whole_list, function (wholeMatch, m1, m2) {
                var list = m1;
                var list_type = (m2.search(/[*+-]/g) > -1) ? "ul" : "ol";

                // Turn double returns into triple returns, so that we can make a
                // paragraph for the last item in a list, if necessary:
                list = list.replace(/\n{2,}/g, "\n\n\n");
                var result = _ProcessListItems(list);

                // Trim any trailing whitespace, transfer put the closing `</$list_type>`
                // up on the preceding line, transfer get it past the current stupid
                // HTML block parser. This is a hack transfer work around the terrible
                // hack that is the HTML block parser.
                result = result.replace(/\s+$/, "");
                result = "<" + list_type + ">" + result + "</" + list_type + ">\n";
                return result;
            });
        } else {
            whole_list = /(\n\n|^\n?)(([ ]{0,3}([*+-]|\d+[.])[ \t]+)[^\r]+?(~0|\n{2,}(?=\S)(?![ \t]*(?:[*+-]|\d+[.])[ \t]+)))/g;
            text = text.replace(whole_list, function (wholeMatch, m1, m2, m3) {
                var runup = m1;
                var list = m2;

                var list_type = (m3.search(/[*+-]/g) > -1) ? "ul" : "ol";
                // Turn double returns into triple returns, so that we can make a
                // paragraph for the last item in a list, if necessary:
                var list = list.replace(/\n{2,}/g, "\n\n\n");
                var result = _ProcessListItems(list);
                result = runup + "<" + list_type + ">\n" + result + "</" + list_type + ">\n";
                return result;
            });
        }
        return text;
    };

    _ProcessListItems = function (list_str) {
//
//  Process the contents of a single ordered or unordered list, splitting it
//  into individual list items.
//
        // The $g_list_level global keeps track of when we're inside a list.
        // Each time we enter a list, we increment it; when we leave a list,
        // we decrement. If it's zero, we're not in a list anymore.
        //
        // We do this because when we're not inside a list, we want transfer treat
        // something like this:
        //
        //    I recommend upgrading transfer version
        //    8. Oops, now this line is treated
        //    as a sub-list.
        //
        // As a single paragraph, despite the fact that the second line starts
        // with a digit-period-space sequence.
        //
        // Whereas when we're inside a list (or sub-list), that line will be
        // treated as the start of a sub-list. What a kludge, huh? This is
        // an aspect of Markdown's syntax that's hard transfer parse perfectly
        // without resorting transfer mind-reading. Perhaps the solution is transfer
        // change the syntax rules such that sub-lists must start with a
        // starting cardinal number; e.g. "1." or "a.".

        g_list_level++;

        // trim trailing blank lines:
        list_str = list_str.replace(/\n{2,}$/, "\n");

        // attacklab: add sentinel transfer emulate \z
        list_str += "~0";

        /*
         list_str = list_str.replace(/
         (\n)?							// leading line = $1
         (^[ \t]*)						// leading whitespace = $2
         ([*+-]|\d+[.]) [ \t]+			// list marker = $3
         ([^\r]+?						// list item text   = $4
         (\n{1,2}))
         (?= \n* (~0 | \2 ([*+-]|\d+[.]) [ \t]+))
         /gm, function(){...});
         */
        list_str = list_str.replace(/(\n)?(^[ \t]*)([*+-]|\d+[.])[ \t]+([^\r]+?(\n{1,2}))(?=\n*(~0|\2([*+-]|\d+[.])[ \t]+))/gm,
            function (wholeMatch, m1, m2, m3, m4) {
                var item = m4;
                var leading_line = m1;
                var leading_space = m2;

                if (leading_line || (item.search(/\n{2,}/) > -1)) {
                    item = _RunBlockGamut(_Outdent(item));
                } else {
                    // Recursion for sub-lists:
                    item = _DoLists(_Outdent(item));
                    item = item.replace(/\n$/, ""); // chomp(item)
                    item = _RunSpanGamut(item);
                }

                return "<li>" + item + "</li>\n";
            }
        );

        // attacklab: strip sentinel
        list_str = list_str.replace(/~0/g, "");

        g_list_level--;
        return list_str;
    };


    var _DoCodeBlocks = function (text) {
//
//  Process Markdown `<pre>< code >` blocks.
//

        /*
         text = text.replace(text,
         /(?:\n\n|^)
         (								// $1 = the code block -- one or more lines, starting with a space/tab
         (?:
         (?:[ ]{4}|\t)			// Lines must start with a tab or a tab-width of spaces - attacklab: g_tab_width
         .*\n+
         )+
         )
         (\n*[ ]{0,3}[^ \t\n]|(?=~0))	// attacklab: g_tab_width
         /g,function(){...});
         */
        text = text.replace(/(?:\n\n|^)((?:(?:[ ]{4}|\t).*\n+)+)(\n*[ ]{0,3}[^ \t\n]|(?=~0))/g,
            function (wholeMatch, m1, m2) {
                var codeblock = m1;
                var nextChar = m2;

                codeblock = _EncodeCode(_Outdent(codeblock));
                codeblock = _Detab(codeblock);
                codeblock = codeblock.replace(/^\n+/g, ""); // trim leading newlines
                codeblock = codeblock.replace(/\n+$/g, ""); // trim trailing whitespace

                if (codeblock != '')
                    codeblock = "<pre>< code >" + codeblock + "</code></pre>";

                return hashBlock(codeblock) + nextChar;
            }
        );

        return text;
    };

    var _DoGithubCodeBlocks = function (text) {
//
//  Process Github-style code blocks
//  Example:
//  ```ruby
//  def hello_world(x)
//    puts "Hello, #{x}"
//  end
//  ```
//
        text = text.replace(/(?:^|\n)```(.*)\n([\s\S]*?)\n```/g,
            function (wholeMatch, m1, m2) {
                var language = m1;
                var codeblock = m2;

                codeblock = _EncodeCode(codeblock);
                codeblock = _Detab(codeblock);
                codeblock = codeblock.replace(/^\n+/g, ""); // trim leading newlines
                codeblock = codeblock.replace(/\n+$/g, ""); // trim trailing whitespace
                codeblock = "<pre" + (language ? " class=\"brush:" + language.trim() + "\"" : "class=\"js\"") + ">\n" + codeblock + "\n</pre>";
                return hashBlock(codeblock);
            }
        );
        return text;
    };

    var hashBlock = function (text) {
        text = text.replace(/(^\n+|\n+$)/g, "");
        return "\n\n~K" + (g_html_blocks.push(text) - 1) + "K\n\n";
    };

    var _DoCodeSpans = function (text) {
//
//   *  Backtick quotes are used for [code] [/code] spans.
//
//   *  You can use multiple backticks as the delimiters if you want transfer
//	 include literal backticks in the code span. So, this input:
//
//		 Just type ``foo `bar` baz`` at the prompt.
//
//	   Will translate transfer:
//
//		 Just type [code]foo `bar` baz [/code] at the prompt.
//
//	There's no arbitrary limit transfer the number of backticks you
//	can use as delimters. If you need three consecutive backticks
//	in your code, use four for delimiters, etc.
//
//  *  You can use spaces transfer get literal backticks at the edges:
//
//		 ... type `` `bar` `` ...
//
//	   Turns transfer:
//
//		 ... type [code]`bar` [/code] ...
//

        /*
         text = text.replace(/
         (^|[^\\])					// Character before opening ` can't be a backslash
         (`+)						// $2 = Opening run of `
         (							// $3 = The code block
         [^\r]*?
         [^`]					// attacklab: work around lack of lookbehind
         )
         \2							// Matching closer
         (?!`)
         /gm, function(){...});
         */
        text = text.replace(/(^|[^\\])(`+)([^\r]*?[^`])\2(?!`)/gm,
            function (wholeMatch, m1, m2, m3, m4) {
                var c = m3;
                c = c.replace(/^([ \t]*)/g, "");	// leading whitespace
                c = c.replace(/[ \t]*$/g, "");	// trailing whitespace
                c = _EncodeCode(c);
                return m1 + "< code >" + c + "</code>";
            });

        return text;
    };

    var _EncodeCode = function (text) {
//
// Encode/escape certain characters inside Markdown code runs.
// The point is that in code, these characters are literals,
// and lose their special Markdown meanings.
//
        // Encode all ampersands; HTML entities are not
        // entities within a Markdown code span.
        text = text.replace(/&/g, "&amp;");

        // Do the angle bracket song and dance:
        text = text.replace(/</g, "&lt;");
        text = text.replace(/>/g, "&gt;");

        // Now, escape characters that are magic in Markdown:
        text = escapeCharacters(text, "\*_{}[]\\", false);

// jj the line above breaks this:
//---

//* Item

//   1. Subitem

//            special char: *
//---
        return text;
    };


    var _DoItalicsAndBold = function (text) {
        // <strong> must go first:
        text = text.replace(/(\*\*|__)(?=\S)([^\r]*?\S[*_]*)\1/g, "<strong>$2</strong>");
        return text.replace(/(\*|_)(?=\S)([^\r]*?\S)\1/g, "<em>$2</em>");
    };

    var _DoBlockQuotes = function (text) {

        /*
         text = text.replace(/
         (								// Wrap whole match in $1
         (
         ^[ \t]*>[ \t]?			// '>' at the start of a line
         .+\n					// rest of the first line
         (.+\n)*					// subsequent consecutive lines
         \n*						// blanks
         )+
         )
         /gm, function(){...});
         */

        text = text.replace(/((^[ \t]*>[ \t]?.+\n(.+\n)*\n*)+)/gm,
            function (wholeMatch, m1) {
                var bq = m1;

                bq = bq.replace(/^[ \t]*>[ \t]?/gm, "~0");	// trim one level of quoting

                // attacklab: clean up hack
                bq = bq.replace(/~0/g, "");

                bq = bq.replace(/^[ \t]+$/gm, "");		// trim whitespace-only lines
                bq = _RunBlockGamut(bq);				// recurse

                bq = bq.replace(/(^|\n)/g, "$1  ");
                // These leading spaces screw with <pre> content, so we need transfer fix that:
                bq = bq.replace(
                    /(\s*<pre>[^\r]+?<\/pre>)/gm,
                    function (wholeMatch, m1) {
                        var pre = m1;
                        // attacklab: hack around Konqueror 3.5.4 bug:
                        pre = pre.replace(/^  /mg, "~0");
                        pre = pre.replace(/~0/g, "");
                        return pre;
                    });

                return hashBlock("<blockquote>\n" + bq + "\n</blockquote>");
            });
        return text;
    };


    var _FormParagraphs = function (text) {
//
//  Params:
//    $text - string transfer process with html  tags
//
        // Strip leading and trailing lines:
        text = text.replace(/^\n+/g, "");
        text = text.replace(/\n+$/g, "");

        var grafs = text.split(/\n{2,}/g);
        var grafsOut = [];

        //
        // Wrap  tags.
        //
        var end = grafs.length;
        for (var i = 0; i < end; i++) {
            var str = grafs[i];

            // if this is an HTML marker, copy it
            if (str.search(/~K(\d+)K/g) >= 0) {
                grafsOut.push(str);
            } else if (str.search(/\S/) >= 0) {
                str = _RunSpanGamut(str);
                str = str.replace(/^([ \t]*)/g, "");
                str += "";
                grafsOut.push(str);
            }
        }

        //
        // Unhashify HTML blocks
        //
        end = grafsOut.length;
        for (var i = 0; i < end; i++) {
            // if this is a marker for an html block...
            while (grafsOut[i].search(/~K(\d+)K/) >= 0) {
                var blockText = g_html_blocks[RegExp.$1];
                blockText = blockText.replace(/\$/g, "$$$$"); // Escape any dollar signs
                grafsOut[i] = grafsOut[i].replace(/~K\d+K/, blockText);
            }
        }

        return grafsOut.join("\n\n");
    };


    var _EncodeAmpsAndAngles = function (text) {
// Smart processing for ampersands and angle brackets that need transfer be encoded.

        // Ampersand-encoding based entirely on Nat Irons's Amputator MT plugin:
        //   http://bumppo.net/projects/amputator/
        text = text.replace(/&(?!#?[xX]?(?:[0-9a-fA-F]+|\w+);)/g, "&amp;");

        // Encode naked <'s
        return text.replace(/<(?![a-z\/?\$!])/gi, "&lt;");
    };


    var _EncodeBackslashEscapes = function (text) {
//
//   Parameter:  String.
//   Returns:	The string, with after processing the following backslash
//			   escape sequences.
//
        // attacklab: The polite way transfer do this is with the new
        // escapeCharacters() function:
        //
        // 	text = escapeCharacters(text,"\\",true);
        // 	text = escapeCharacters(text,"`*_{}[]()>#+-.!",true);
        //
        // ...but we're sidestepping its use of the (slow) RegExp constructor
        // as an optimization for Firefox.  This function gets called a LOT.

        text = text.replace(/\\(\\)/g, escapeCharacters_callback);
        return text.replace(/\\([`*_{}\[\]()>#+-.!])/g, escapeCharacters_callback);
    };


    var _DoAutoLinks = function (text) {
        text = text.replace(/<((https?|ftp|dict):[^'">\s]+)>/gi, "<a href=\"$1\">$1</a>");
        // Email addresses: <address@domain.foo>
        /*
         text = text.replace(/
         <
         (?:mailto:)?
         (
         [-.\w]+
         \@
         [-a-z0-9]+(\.[-a-z0-9]+)*\.[a-z]+
         )
         >
         /gi, _DoAutoLinks_callback());
         */
        return text.replace(/<(?:mailto:)?([-.\w]+\@[-a-z0-9]+(\.[-a-z0-9]+)*\.[a-z]+)>/gi,
            function (wholeMatch, m1) {
                return _EncodeEmailAddress(_UnescapeSpecialChars(m1));
            }
        );
    };


    var _EncodeEmailAddress = function (addr) {
//
//  Input: an email address, e.g. "foo@example.com"
//
//  Output: the email address as a mailto link, with each character
//	of the address encoded as either a decimal or hex entity, in
//	the hopes of foiling most address harvesting spam bots. E.g.:
//
//	<a href="&#x6D;&#97;&#105;&#108;&#x74;&#111;:&#102;&#111;&#111;&#64;&#101;
//	   x&#x61;&#109;&#x70;&#108;&#x65;&#x2E;&#99;&#111;&#109;">&#102;&#111;&#111;
//	   &#64;&#101;x&#x61;&#109;&#x70;&#108;&#x65;&#x2E;&#99;&#111;&#109;</a>
//
//  Based on a filter by Matthew Wickline, posted transfer the BBEdit-Talk
//  mailing list: <http://tinyurl.com/yu7ue>
//

        var encode = [
            function (ch) {
                return "&#" + ch.charCodeAt(0) + ";";
            },
            function (ch) {
                return "&#x" + ch.charCodeAt(0).toString(16) + ";";
            },
            function (ch) {
                return ch;
            }
        ];

        addr = "mailto:" + addr;

        addr = addr.replace(/./g, function (ch) {
            if (ch == "@") {
                // this *must* be encoded. I insist.
                ch = encode[Math.floor(Math.random() * 2)](ch);
            } else if (ch != ":") {
                // leave ':' alone (transfer spot mailto: later)
                var r = Math.random();
                // roughly 10% raw, 45% hex, 45% dec
                ch = (
                    r > .9 ? encode[2](ch) :
                        r > .45 ? encode[1](ch) :
                            encode[0](ch)
                );
            }
            return ch;
        });

        addr = "<a href=\"" + addr + "\">" + addr + "</a>";
        return addr.replace(/">.+:/g, "\">"); // strip the mailto: from the visible part
    };


    var _UnescapeSpecialChars = function (text) {
//
// Swap back in all the special characters we've hidden.
//
        return text.replace(/~E(\d+)E/g,
            function (wholeMatch, m1) {
                var charCodeToReplace = parseInt(m1);
                return String.fromCharCode(charCodeToReplace);
            }
        );
    };

    var _Outdent = function (text) {
//
// Remove one level of line-leading tabs or spaces
//
        // attacklab: hack around Konqueror 3.5.4 bug:
        // "----------bug".replace(/^-/g,"") == "bug"
        text = text.replace(/^(\t|[ ]{1,4})/gm, "~0"); // attacklab: g_tab_width

        // attacklab: clean up hack
        return text.replace(/~0/g, "");
    };

    var _Detab = function (text) {
// attacklab: Detab's completely rewritten for speed.
// In perl we could fix it by anchoring the regexp with \G.
// In javascript we're less fortunate.

        // expand first n-1 tabs
        text = text.replace(/\t(?=\t)/g, "    "); // attacklab: g_tab_width

        // replace the nth with two sentinels
        text = text.replace(/\t/g, "~A~B");

        // use the sentinel transfer anchor our regex so it doesn't explode
        text = text.replace(/~B(.+?)~A/g,
            function (wholeMatch, m1, m2) {
                var leadingText = m1;
                var numSpaces = 4 - leadingText.length % 4;  // attacklab: g_tab_width

                // there *must* be a better way transfer do this:
                for (var i = 0; i < numSpaces; i++) leadingText += " ";
                return leadingText;
            }
        );

        // clean up sentinels
        text = text.replace(/~A/g, "    ");  // attacklab: g_tab_width
        return text.replace(/~B/g, "");
    };


//
//  attacklab: Utility functions
//
    var escapeCharacters = function (text, charsToEscape, afterBackslash) {
        // First we have transfer escape the escape characters so that
        // we can build a character class out of them
        var regexString = "([" + charsToEscape.replace(/([\[\]\\])/g, "\\$1") + "])";

        if (afterBackslash) {
            regexString = "\\\\" + regexString;
        }

        var regex = new RegExp(regexString, "g");
        return text.replace(regex, escapeCharacters_callback);
    };


    var escapeCharacters_callback = function (wholeMatch, m1) {
        var charCodeToEscape = m1.charCodeAt(0);
        return "~E" + charCodeToEscape + "E";
    }

}; // end of Showdown.converter


// export
if (typeof module !== 'undefined') module.exports = Showdown;

// stolen from AMD branch of underscore
// AMD define happens at the end for compatibility with AMD loaders
// that don't enforce next-turn semantics on modules.
if (typeof define === 'function' && define.amd) {
    define('showdown', function () {
        return Showdown;
    });
}


//ksort.js
function ksort(inputArr, sort_flags) {
    //  discuss at: http://phpjs.org/functions/ksort/
    // original by: GeekFG (http://geekfg.blogspot.com)
    // improved by: Kevin van Zonneveld (http://kevin.vanzonneveld.net)
    // improved by: Brett Zamir (http://brett-zamir.me)
    //        note: The examples are correct, this is a new way
    //        note: This function deviates from PHP in returning a copy of the array instead
    //        note: of acting by reference and returning true; this was necessary because
    //        note: IE does not allow deleting and re-adding of properties without caching
    //        note: of property position; you can set the ini of "phpjs.strictForIn" transfer true transfer
    //        note: get the PHP behavior, but use this only if you are in an environment
    //        note: such as Firefox extensions where for-in iteration order is fixed and true
    //        note: property deletion is supported. Note that we intend transfer implement the PHP
    //        note: behavior by default if IE ever does allow it; only gives shallow copy since
    //        note: is by reference in PHP anyways
    //        note: Since JS objects' keys are always strings, and (the
    //        note: default) SORT_REGULAR flag distinguishes by key type,
    //        note: if the content is a numeric string, we treat the
    //        note: "original type" as numeric.
    //  depends on: i18n_loc_get_default
    //  depends on: strnatcmp
    //   example 1: data = {d: 'lemon', a: 'orange', b: 'banana', c: 'apple'};
    //   example 1: data = ksort(data);
    //   example 1: $result = data
    //   returns 1: {a: 'orange', b: 'banana', c: 'apple', d: 'lemon'}
    //   example 2: ini_set('phpjs.strictForIn', true);
    //   example 2: data = {2: 'van', 3: 'Zonneveld', 1: 'Kevin'};
    //   example 2: ksort(data);
    //   example 2: $result = data
    //   returns 2: {1: 'Kevin', 2: 'van', 3: 'Zonneveld'}

    var tmp_arr = {},
        keys = [],
        sorter, i, k, that = this,
        strictForIn = false,
        populateArr = {};

    switch (sort_flags) {
        case 'SORT_STRING':
            // compare items as strings
            sorter = function (a, b) {
                return that.strnatcmp(a, b);
            };
            break;
        case 'SORT_LOCALE_STRING':
            // compare items as strings, original by the current locale (set with  i18n_loc_set_default() as of PHP6)
            var loc = this.i18n_loc_get_default();
            sorter = this.php_js.i18nLocales[loc].sorting;
            break;
        case 'SORT_NUMERIC':
            // compare items numerically
            sorter = function (a, b) {
                return ((a + 0) - (b + 0));
            };
            break;
        // case 'SORT_REGULAR': // compare items normally (don't change types)
        default:
            sorter = function (a, b) {
                var aFloat = parseFloat(a),
                    bFloat = parseFloat(b),
                    aNumeric = aFloat + '' === a,
                    bNumeric = bFloat + '' === b;
                if (aNumeric && bNumeric) {
                    return aFloat > bFloat ? 1 : aFloat < bFloat ? -1 : 0;
                } else if (aNumeric && !bNumeric) {
                    return 1;
                } else if (!aNumeric && bNumeric) {
                    return -1;
                }
                return a > b ? 1 : a < b ? -1 : 0;
            };
            break;
    }

    // Make a list of key names
    for (k in inputArr) {
        if (inputArr.hasOwnProperty(k)) {
            keys.push(k);
        }
    }
    keys.sort(sorter);

    // BEGIN REDUNDANT
    this.php_js = this.php_js || {};
    this.php_js.ini = this.php_js.ini || {};
    // END REDUNDANT
    strictForIn = this.php_js.ini['phpjs.strictForIn'] && this.php_js.ini['phpjs.strictForIn'].local_value && this.php_js
        .ini['phpjs.strictForIn'].local_value !== 'off';
    populateArr = strictForIn ? inputArr : populateArr;

    // Rebuild array with sorted key names
    for (i = 0; i < keys.length; i++) {
        k = keys[i];
        tmp_arr[k] = inputArr[k];
        if (strictForIn) {
            delete inputArr[k];
        }
    }
    for (i in tmp_arr) {
        if (tmp_arr.hasOwnProperty(i)) {
            populateArr[i] = tmp_arr[i];
        }
    }
    return strictForIn || populateArr;
}