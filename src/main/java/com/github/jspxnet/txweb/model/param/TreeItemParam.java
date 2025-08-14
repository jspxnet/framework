package com.github.jspxnet.txweb.model.param;

import com.github.jspxnet.enums.YesNoEnumType;
import com.github.jspxnet.json.JsonIgnore;
import com.github.jspxnet.txweb.annotation.Param;
import com.github.jspxnet.txweb.enums.SafetyEnumType;
import com.github.jspxnet.utils.StringUtil;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by jspx.net
 *
 * author: chenYuan
 * date: 2020/4/27 0:09
 * description: 树节点参数
 **/
@Data
public class TreeItemParam implements Serializable {
   
    @Param(caption = "ID")
    private long id = 0;

    @Param(caption = "节点ID", max = 50)
    private String nodeId = StringUtil.empty;

    @Param(caption = "节点值", max = 200)
    private String itemValue = StringUtil.empty;

    @Param(caption = "父ID", max = 100)
    private String parentNodeId = StringUtil.empty;

    @Param(caption = "名称", max = 100,required = true)
    private String caption = StringUtil.empty;

    @Param(caption = "节点动作", max = 100)
    private String nodeAction = StringUtil.empty;

    @Param(caption = "图标", max = 100)
    private String icon = StringUtil.empty;

    @Param(caption = "打开的图标", max = 100)
    private String openIcon = StringUtil.empty;

    @Param(caption = "隐藏",enumType = YesNoEnumType.class)
    private int hide = 0;

    @Param(caption = "录入框", max = 50)
    private String inputType = StringUtil.empty;

    @Param(caption = "排序")
    private int sortType = 0;

    @Param(caption = "页面排序")
    private int showSortType = 0;

    @Param(caption = "打开的方式", max = 40)
    private String target = "_self";

    //发布的格式 CMS  option = "0:html;1:markdown;2:link;3:img;4:layout;5:quote;6:电子报;10:公文;12:邮票"
    @Param(caption = "格式")
    private int formatType = 0;

    @Param(caption = "描述", max = 250)
    private String description = StringUtil.empty;

    @JsonIgnore
    @Param(caption = "密码", max = 200)
    private String password = StringUtil.empty;

    //保存 {76:管理员};{75:会员} (多个用;分开) xin新颁布改为{ } 表示角色，[] 表示用户
    @Param(caption = "查看角色", max = 240)
    private String roleIds = StringUtil.empty;

    @Param(caption = "允许发布", max = 2,enumType = YesNoEnumType.class)
    private int allowPost = 1;

    @Param(caption = "是否允许回复", max = 2,enumType = YesNoEnumType.class,value = "1")
    private int allowReply = 1;

    @Param(caption = "是否允许下载附件", max = 2, enumType = YesNoEnumType.class,value = "1")
    private int allowDownAttach = 1;

    @Param(caption = "是否允许上传附件", max = 2, enumType = YesNoEnumType.class)
    private int allowPostAttach = 1;

    @Param(caption = "回复数量")
    private int replies = 0;

    @Param(caption = "总发布数")
    private int posts = 1;

    @Param(caption = "精华数量")
    private int distillates = 1;

    //论坛等常用end
    @JsonIgnore
    @Param(caption = "样式", max = 50)
    private String styleName = StringUtil.empty;

    //"0:不显示;1:首页显示;2:专题"
    @Param(caption = "节点类型")
    private int nodeType = 1;

    @Param(caption = "图片URL", max = 200)
    private String showImage = StringUtil.empty;

    @Param(caption = "正文连接地址", max = 200,level = SafetyEnumType.NONE)
    private String linkPage = StringUtil.empty;

    @Param(caption = "显示模版", max = 200,level = SafetyEnumType.NONE)
    private String templatePage = StringUtil.empty;


    @Param(caption = "栏目连接地址", max = 200,level = SafetyEnumType.NONE)
    private String nodeLinkPage = StringUtil.empty;

    @Param(caption = "列表模版", max = 200,level = SafetyEnumType.NONE)
    private String templateListPage = StringUtil.empty;

    //CMS里边判断是否静态化，各个软件作用不同
    @Param(caption = "模版类型")
    private int templateType = 0;

    //做点菜时候的物品单位
    @Param(caption = "单位", max = 250)
    private String units = StringUtil.empty;

    @Param(caption = "栏目广告", max = 250)
    private String drumbeating = StringUtil.empty;

    //格式[id:name]多个使用;分开
    @Param(caption = "管理员", max = 250)
    private String manager = StringUtil.empty;

    @Param(caption = "文书", max = 250)
    private String paperwork = StringUtil.empty;

    @JsonIgnore
    @Param(caption = "排序时间")
    private Date sortDate = new Date();

    //论坛做最后发布时间
    @Param(caption = "操作时间")
    private Date lastDate = new Date();

    @Param(caption = "命名空间", max = 50)
    private String namespace = StringUtil.empty;

    @Param(caption = "机构ID", max = 32)
    private String organizeId = StringUtil.empty;


    @Param(caption = "删除标识")
    private int delete = 1;
}
