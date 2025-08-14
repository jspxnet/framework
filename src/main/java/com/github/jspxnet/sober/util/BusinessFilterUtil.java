package com.github.jspxnet.sober.util;

import com.github.jspxnet.boot.EnvFactory;
import com.github.jspxnet.sober.SoberSupport;
import com.github.jspxnet.sober.TableModels;
import com.github.jspxnet.sober.exception.RepeatBillNoException;
import com.github.jspxnet.txweb.context.ActionContext;
import com.github.jspxnet.txweb.context.ThreadContextHolder;
import com.github.jspxnet.txweb.enums.DocumentStatusEnumType;
import com.github.jspxnet.txweb.env.ActionEnv;
import com.github.jspxnet.txweb.model.container.AbstractBillObject;
import com.github.jspxnet.txweb.table.UserSession;
import com.github.jspxnet.txweb.table.meta.BaseBillType;
import com.github.jspxnet.txweb.util.RequestUtil;
import com.github.jspxnet.utils.CookieUtil;
import com.github.jspxnet.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Date;
@Slf4j
public final class BusinessFilterUtil {
    private BusinessFilterUtil()
    {

    }


    /**
     *
     * @param actionContext web线程
     * @param jdbcOperations 数据处理
     * @return 得到用户在线信息
     */
    public static UserSession getUserSession(ActionContext actionContext,SoberSupport jdbcOperations)
    {
        HttpServletRequest request = actionContext.getRequest();
        //才有头部传输sesionId的方式,这种方式比较标准一点
        //头部个数 Authorization: Bearer {seesionId}

        String token = null;
        if (request != null) {
            token = RequestUtil.getToken(request);
            if (StringUtil.isEmpty(token))
            {
                token = CookieUtil.getCookieString(request, ActionEnv.KEY_TOKEN, null);
            }
            if (token!=null&&!token.contains(StringUtil.DOT))
            {
                token = null;
            }
        }
        HttpSession session = null;
        if (request!=null)
        {
            session = request.getSession();
            if (session != null & StringUtil.isNull(token) ) {
                token = (String) session.getAttribute(ActionEnv.KEY_TOKEN);
            }
        }
        return jdbcOperations.load(UserSession.class,token,false);
    }

    /**
     *
     * @param jdbcOperations 数据处理
     * @param object 处理对象
     * @throws Exception 异常
     */
    public static void saveFilter(SoberSupport jdbcOperations,  Object object) throws Exception {
        if (object==null)
        {
            return;
        }
        if (!(object instanceof AbstractBillObject))
        {
            return;
        }

        //放入如果不是暂存方式放入单号又为空
        AbstractBillObject billObject = ((AbstractBillObject) object);
        if (StringUtil.isNull(billObject.getBillNo())&&DocumentStatusEnumType.Z.getValue()!=billObject.getDocumentStatus())
        {
            String tableName = "";
            TableModels tableModels = billObject.getTableModels();
            if (tableModels==null || StringUtil.isNull(tableModels.getName()))
            {
                tableName = AnnotationUtil.getTableName(object.getClass());
            } else
            {
                tableName = tableModels.getName();
            }

            if (StringUtil.isNull(tableName))
            {
                 return;
            }

            //放入单据类型 begin
            if (billObject.getBillTypeId()==0)
            {
                BaseBillType baseBillType = jdbcOperations.getDefaultBaseBillType(tableName);
                if (baseBillType!=null)
                {
                    billObject.setBillTypeId(baseBillType.getId());
                    if (DocumentStatusEnumType.UNKNOWN.getValue()==billObject.getDocumentStatus())
                    {
                        billObject.setDocumentStatus(baseBillType.getDefDocumentStatus());
                    }
                }
            }
            //放入单据类型 end


            //放入单据编号begin
            if (DocumentStatusEnumType.Z.getValue()!=billObject.getDocumentStatus())
            {
                //暂存 状态不创建单据编号
                SequenceFactory sequenceFactory = EnvFactory.getBeanFactory().getBean(SequenceFactory.class);
                try {
                    String billNo = sequenceFactory.getNextBillNo(tableName,jdbcOperations);
                    billObject.setBillNo(billNo);
                } catch (Exception e) {
                    e.printStackTrace();
                }


                //验证单据是否重复 begin
                if (jdbcOperations.existBillNo(tableName,billObject.getBillNo())>0)
                {
                   throw  new RepeatBillNoException(tableName,billObject.getBillNo());
                }
                //验证单据是否重复 end
            }

            //放入单据编号end

            billObject.setCreateDate(new Date());
            //-----------------------------
            //如果没有创建人,放入创建人
            ActionContext actionContext = ThreadContextHolder.getContext();
            if (actionContext==null)
            {
                return;
            }

            //得到用户信息
            UserSession userSession = getUserSession( actionContext,jdbcOperations);
            if (userSession!=null)
            {
                billObject.setPutName(userSession.getName());
                billObject.setPutUid(userSession.getUid());
            }

        }
    }
    /**
     *
     * @param jdbcOperations 数据处理
     * @param object 处理对象
     * @throws Exception 异常
     */
    public static void updateFilter(SoberSupport jdbcOperations, Object object) throws Exception {
        if (object==null)
        {
            return;
        }
        if (!(object instanceof AbstractBillObject))
        {
            return;
        }

        //放入如果不是暂存方式放入单号又为空
        AbstractBillObject billObject = ((AbstractBillObject) object);
        if (StringUtil.isNull(billObject.getBillNo())&&DocumentStatusEnumType.Z.getValue()!=billObject.getDocumentStatus())
        {
            String tableName = "";
            TableModels tableModels = billObject.getTableModels();
            if (tableModels==null || StringUtil.isNull(tableModels.getName()))
            {
                tableName = AnnotationUtil.getTableName(object.getClass());
            } else
            {
                tableName = tableModels.getName();
            }

            if (StringUtil.isNull(tableName))
            {
                return;
            }

            //放入单据类型 begin
            if (billObject.getBillTypeId()==0)
            {
                BaseBillType baseBillType = jdbcOperations.getDefaultBaseBillType(tableName);
                if (baseBillType!=null)
                {
                    billObject.setBillTypeId(baseBillType.getId());
                    if (DocumentStatusEnumType.UNKNOWN.getValue()==billObject.getDocumentStatus())
                    {
                        billObject.setDocumentStatus(baseBillType.getDefDocumentStatus());
                    }
                }
            }
            //放入单据类型 end

            billObject.setModifyDate(new Date());
            //放入单据编号begin
            if (DocumentStatusEnumType.Z.getValue()!=billObject.getDocumentStatus())
            {
                //暂存 状态不创建单据编号
                SequenceFactory sequenceFactory = EnvFactory.getBeanFactory().getBean(SequenceFactory.class);
                try {
                    String billNo = sequenceFactory.getNextBillNo(tableName,jdbcOperations);
                    billObject.setBillNo(billNo);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                //验证单据是否重复 begin
                if (jdbcOperations.existBillNo(tableName,billObject.getBillNo())>1)
                {
                    throw  new RepeatBillNoException(tableName,billObject.getBillNo());
                }
                //验证单据是否重复 end
            }
            //放入单据编号end
            //-----------------------------
            //如果没有创建人,放入创建人
            ActionContext actionContext = ThreadContextHolder.getContext();
            if (actionContext==null)
            {
                return;
            }

            //得到用户信息
            UserSession userSession = getUserSession( actionContext,jdbcOperations);
            if (userSession!=null)
            {

                billObject.setModifierId(userSession.getUid());
            }
        }
    }
}
