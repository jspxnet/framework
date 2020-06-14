package com.github.jspxnet.txweb.table;

import com.github.jspxnet.json.JsonIgnore;
import com.github.jspxnet.sober.annotation.Column;
import com.github.jspxnet.sober.annotation.Id;
import com.github.jspxnet.sober.annotation.Table;
import com.github.jspxnet.sober.table.OperateTable;
import com.github.jspxnet.utils.ArrayUtil;
import com.github.jspxnet.utils.StringUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 这个表主要是为了保存角色和栏目的权限对应关系，因为每次要得到权限的逻辑关系要遍历所有会耗费较多资源
 * 在每次树结构发生变化后，将通过算法生成角色的权限对应关系保存在这个表里，查询列表数据的时候直接通过这个表查询
 * 来节约资源
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Table(name = "jspx_tree_role", caption = "浏览权限")
public class TreeRole extends OperateTable {
    @Id
    @Column(caption = "ID", notNull = true)
    private long id = 0;

    //总的常量，默认为 0
    @Column(caption = "角色", length = 32)
    private String roleId = StringUtil.empty;

    @Column(caption = "栏目ID", length = 10000, notNull = true)
    private String nodeIds = StringUtil.empty;

    @JsonIgnore
    @Column(caption = "命名空间", length = 50, dataType = "isLengthBetween(1,50)")
    private String namespace = StringUtil.empty;

    @JsonIgnore
    @Column(caption = "机构ID", length = 32)
    private String organizeId = StringUtil.empty;

    public void joinNodeIds(String nodeIds) {
        String[] lines = ArrayUtil.join(StringUtil.split(this.nodeIds, StringUtil.SEMICOLON), StringUtil.split(nodeIds, StringUtil.SEMICOLON));
        lines = ArrayUtil.remove(lines, "");
        lines = ArrayUtil.remove(lines, "root");
        lines = ArrayUtil.deleteRepeated(lines, true);
        ArrayUtil.sort(lines);
        this.nodeIds = ArrayUtil.toString(lines, StringUtil.SEMICOLON);
    }

    public boolean isInNodeId(String nodeId) {
        if (StringUtil.isNull(nodeIds) || (StringUtil.ASTERISK.equals(nodeIds))) {
            return true;
        }
        return ArrayUtil.inArray(StringUtil.split(nodeIds, StringUtil.SEMICOLON), nodeId, true);
    }

}
