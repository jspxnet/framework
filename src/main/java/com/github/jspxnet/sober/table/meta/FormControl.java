package com.github.jspxnet.sober.table.meta;

import com.github.jspxnet.sober.annotation.Column;
import com.github.jspxnet.sober.annotation.Id;
import com.github.jspxnet.sober.annotation.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Table(name = "jspx_form_control",caption = "表单控件")
public class FormControl extends ControlBase {
    @Id
    @Column(caption = "ID", notNull = true)
    private long id;

    @Column(caption = "表单Id", notNull = true)
    private long formId;


}
