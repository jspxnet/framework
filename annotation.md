#Jspx.net Framework 6.3X注释标签

##一.简介

 Jspx.net Framework 6.3X 支持传统方式的页面模版方式和微服务方式,注释标签涉及数据库,容器,WEB和JSON三部分.

##二. sober数据库注释标签

*	**@Column**标签,用于配置字段属性


|标签属性      |类型       |  参数说明                                                                     |
|=============|:==========|:==============================================================================|
|caption      |String     |字段文字说明                                                                    |
|notNull      |boolean    |字段是否可以为空                                                                |
|option       |String     |选择范围，例如:"小;中;大"或者 "1:小;2:中;3:大",不建议是用,尽量是用enumType代替  |
|dataType     |String     |验证表达式,配置和TXWeb里边的验证配置是一样的，这里配置好后可以自动生成web页面的配置 |
|defaultValue |String     |默认值,映射到数据库,如果是数字等，例如："1.14"                                    |
|length       |int        |字段长度,映射到数据库,日期类型不需要配置                                          |
|input        |String     |输入框类型,对应html表单  的type 属性，例如:text，number,date                     |
|hidden       |boolean    |表单中是否隐藏，例如密码或者一些辅助字段就可以设置为true,传统方式是用                       |
|enumType     |枚举类型   |当这个字段保存的为枚举类型的时候,这里为枚举类的class,枚举类value,name两个字段必须,会到API文档 |
|dataType       |boolean    |在导出的时候是否隐藏，例如密码或者一些辅助字段就可以设置为true                     |

*	**@Id**标签,用于标记是否为数据库关键字


|标签属性      |类型       |  参数说明                                                                     |
|=============|:==========|:==============================================================================|
|auto         |boolean    |是否sober生成ID                                                                 |
|type         |String     |ID号的生成方式,详细见下表                                                       |
|length       |int        |ID号长度,推荐最好小于18，                                                       |
|max          |long       |id的最大值，到达后会循环到最小值，默认Integer.MAX_VALUE                          |
|min          |int        |id的最小值，根据next 递增                                                       |
|next         |int        |id递增量  id=id+nex                                                            |
|dateStart    |boolean    |使用sober生成id时，是否使用日期开头，例如yyyyMMddhhss + 序列                     |


*	**@Id**标签中的，**type** 选项说明

|标签属性      |java类型          | 参数说明                                                      |
|=============|:=================|:==============================================================|
|uid          |String            |默认生成格式如 b2a53eef-3a29-44ff-bca5-72abc9568785             |
|uid          |long              |UUID getMostSignificantBits 生成                               |
|serial       |long 或 int       |数据库自动增加,auto必须为false                                  |
|seq          |String或long或int |交由sober配合上边的相关参数生成                                  |


*	**@CalcUnique**标签,用于数据库计算调用,是用前先考量性能

|标签属性      | 参数说明                                                      |
|=============|:==============================================================|
|entity       |是一个数组，包括了你在本sql中会用到的实体bean                    |
|sql          | 统计时用到的sql                                                |

例如：

```java
@EqualsAndHashCode(callSuper = true)
@Data
@Table(name = "数据库表明", caption = "中文说明")
public class ActionLog extends OperateTable {
    @Id(auto = true, length = 20, type = IDType.seq, dateStart = true)
    @Column(caption = "ID", length = 24, notNull = true)
    private String id = StringUtil.empty;

    @Column(caption = "名称", length = 200, dataType = "isLengthBetween(2,200)", notNull = true)
    private String caption = StringUtil.empty;
}
```
sober会更具上边的标签自动生成表结构,@Data 是插件功能



*	**@Nexus**标签,表示映射关系,mapping关系可以在MappingType得到定义

|标签属性      | 参数说明                                      |
|=============|:==============================================|
|mapping       |映射关系                                      |
|name          |自己表的字段                                   |
|targetName    |对应的外部表字段                               |
|targetEntity  |对应的实体类                                   |
|term          |条件 使用 ssql表达式                           |
|where         |映射条件,这里为表达式，如果成立，才载入映射     |
|orderBy       |排序 ,ssql表达式                              |
|delete        |关联删除                                      |
|update        |关联更新                                      |
|chain         |多层关联，查询                                |


*	**@Nexus**标签,mapping备选定义在com.jspx.boot.sign.MappingType.java

关联关系配置后,查询中load=true,就会带出关联数据,注意考虑到性能

|mapping属性  | 参数说明  |
|=============|:=========|
|ManyToOne    |多对一    |
|OneToOne     |一对一    |
|OneToMany    |一对多    |


##三. TXWeb web注释标签


TXWeb配置可以是XML方式也,也可以免XML配置,
如果不在XML中配置,可以自动扫描得到配置

*	**@HttpMethod**标签,HTTP对外访问配置

```xml

 <scan package="jspx.jbbs(包路径)" />

```

|HttpMethod属性  | 参数说明  |
|=============|:=========|
|caption	|动作名称,会提取到API文档	  |
|mobile         |是否支持手机页面，如果是,那么模板读取的时候，是手机端将读取 name.mobile.ftl的文件   |
|secret		|强制要求加密传输  如果使用https,这里没毕业设置为true  |
|namespace	|命名空间,属于那个软件,一遍为软件的根目录名称  |
|actionName	|入口名称 roc方式配置为'*',传统方式为页面名称,不要后缀  |
|resultClass	|roc方式默认返回方式,指定一个默认的返回执行类  |


*	**@MulRequest**标签,上传配置,加上这个注释,可以和用普通的request一样处理变量



|MulRequest属性  | 参数说明  |
|=============|:=========|
|covering	|是否覆盖  |
|saveDirectory  |上传到那个目录|
|fileTypes	|上次文件类型,默认为 '*',多个为;号分割 |
|maxPostSize	|最大上传大小  |

```java

    /**
     * @param multipartRequest 请求接口
     */
    @Override
    @Param(request = false)
    @MulRequest(covering = "@covering", saveDirectory = "@saveDirectory", fileTypes = "@fileTypes", maxPostSize = "@maxPostSize")
    public void setMultipartRequest(MultipartRequest multipartRequest) {
        request = this.multipartRequest = multipartRequest;
    }

```

*	**@Operate**标签,动作标签,表示一个动作或者一个返回



|Operate属性    | 参数说明  |
|===============|:=========|
|method		|默认@,表示就是当前的方法名称,也是resetfull 的调用路径  |
|post		|是否只有post方式才触发  |
|caption	|中文描述,会提取到API文档  |

```java
    /*
       路径作为参数
       直接访问地址 : http://127.0.0.1:8080/demo/persion/pname/cy/2343.jhtml
       返回:
        {
            "protocol": "jspx.net-remote",
                "data": {
                    "name": "cy",
                    "id": "2343"
                },
            "success": 1,
            "version": "3.0"
    }
   */
    @Operate(caption = "路径参数", method = "/pname/${name}/${id}", post = false)
    public RocResponse getPathValue(@PathVar(name = "name") String name, @PathVar(name = "id") String id) {
        //
    }


    /**
     * 请求：无参数,直接访问地址得到
     * http://127.0.0.1:8080/demo/persion/getPersion.jhtml
     * //但这里要注意,浏览器里边直接访问是不行的，如果直接访问将变为传统模式,需要模版文件支持
     * 必须使用ajax Roc 协议头调用
     * 返回：
     * {
     * "lastName": "",
     * "isBoss": true,
     * "lists": ["1","2","3"],
     * "name": "张三",
     * "birth": "2018-02-11 00:00:00",
     * "publicKey": "演示标签注释方式载入",
     * "age": 18
     * }
     *
     * @return Roc方式自动转换为json，传统模版方式显示对象也是json，模版内调用是对象
     */
    @Override
    @Operate(caption = "得到")
    public Persion getPersion()
    {
        System.out.println("---------------------run-----------getPersion");
        return persion;
    }

```


*	**@Param**标签,请求参数标签

参数可以接收传统方式参数或json请求参数,根据请求头识别

|Param属性    | 参数说明  |
|===============|:=========|
|caption	|中文描述,会提取到API文档  |
|min		|当字段为数字类型,表示最小值,字符串类型表示最小长度 |
|max		|当字段为数字类型,表示最大值,字符串类型表示最大长度 |
|level		|安全等级,1:表示基本的特殊字符<>,2:表示特殊的sql关键字和html特殊脚本;3:html中的脚本和事件 |
|enumType	|当这个字段保存的为枚举类型的时候,这里为枚举类的class,枚举类value,name两个字段必须,会到API文档 |
|required	|是否为必填 |
|request	|是否接受请求方式的参数，false 将不会接收外部请求进入的参数 |
|type		|如果参数为签名加密数据等封装对象,这里为真实的类对象 |


*	**@PathVar**标签,URL路径变量作为参数,6.4版本后在使用


|Param属性    | 参数说明  |
|===============|:=========|
|caption	|中文描述,会提取到API文档  |
|min		|当字段为数字类型,表示最小值,字符串类型表示最小长度 |
|max		|当字段为数字类型,表示最大值,字符串类型表示最大长度,字符串时默认最大为 50000 |
|level		|安全等级,1:表示基本的特殊字符<>,2:表示特殊的sql关键字和html特殊脚本;3:html中的脚本和事件 |

level对应枚举类型SafetyEnumType

```java
    //高
    HEIGHT(3, "高"),
    //中
    MIDDLE(2, "中"),
    //低
    LOW(1, "低"),
    //无
    NONE(0, "无");
```