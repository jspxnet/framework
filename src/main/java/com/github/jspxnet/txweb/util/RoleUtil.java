package com.github.jspxnet.txweb.util;

import com.github.jspxnet.enums.CongealEnumType;
import com.github.jspxnet.txweb.table.Role;
import com.github.jspxnet.utils.ArrayUtil;
import com.github.jspxnet.utils.NumberUtil;
import com.github.jspxnet.utils.ObjectUtil;
import com.github.jspxnet.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jspx.net
 *
 * author: chenYuan
 * date: 2020/5/28 21:59
 * description: 用户多角色合并Role
 **/
@Slf4j
public  class RoleUtil {
    private RoleUtil()
    {

    }

    /**
     *
     * @param roleList 角色列表
     * @return 合并角色
     */
    static public Role mergeRole(List<Role> roleList,String organizeId)
    {
        if (ObjectUtil.isEmpty(roleList))
        {
            return null;
        }
        if (roleList.size()==1)
        {
            //只有一个角色直接返回
            return roleList.get(0);
        }
        Role role = new Role();
        StringBuilder names = new StringBuilder();
        StringBuilder operateList = new StringBuilder();
        int userType = 0;
        int officeType = 0;
        int useUpload = 0;
        int uploadSize = 0;
        long diskSize = 0;
        String uploadFolder = "";
        String images = "";
        int uploadImageSize = 0;
        int uploadVideoSize = 0;
        StringBuilder uploadFileTypes = new StringBuilder();
        String namespace = null;

        String roleId = null;
        String organizeIdSelf = organizeId;


        for (Role roleTmp:roleList)
        {
            if (CongealEnumType.YES_CONGEAL.getValue()==roleTmp.getCongealType())
            {
                continue;
            }
            names.append(roleTmp.getName()).append("+");
            userType = NumberUtil.getMax(new int[]{roleTmp.getUserType(),userType});
            if (userType==roleTmp.getUserType())
            {
                images = roleTmp.getImages();
                roleId = roleTmp.getId();
            }

            officeType = NumberUtil.getMax(new int[]{roleTmp.getOfficeType(),officeType});

            useUpload = NumberUtil.getMax(new int[]{roleTmp.getUseUpload(),useUpload});

            uploadSize = NumberUtil.getMax(new int[]{roleTmp.getUploadSize(),uploadSize});

            uploadImageSize = NumberUtil.getMax(new int[]{roleTmp.getUploadImageSize(),uploadImageSize});

            uploadVideoSize = NumberUtil.getMax(new int[]{roleTmp.getUploadVideoSize(),uploadVideoSize});

            diskSize = NumberUtil.getMax(new long[]{roleTmp.getDiskSize(),diskSize});
            if (diskSize==roleTmp.getDiskSize())
            {
                uploadFolder = roleTmp.getUploadFolder();
            }

            uploadFileTypes.append(roleTmp.getUploadFileTypes()).append(";");
            operateList.append(roleTmp.getOperates()).append("\r\n");

            if (StringUtil.isNull(namespace))
            {
                namespace = roleTmp.getNamespace();
            }
            if (StringUtil.isNull(organizeIdSelf))
            {
                organizeIdSelf = roleTmp.getOrganizeId();
            }
        }
        String[] lines = StringUtil.split(StringUtil.replace(operateList.toString(),"\r\n","\n"),"\n");
        lines = ArrayUtil.deleteRepeated(lines,true);

        String updateFileType = "";
        if (uploadFileTypes.toString().endsWith(StringUtil.SEMICOLON))
        {
            uploadFileTypes.setLength(uploadFileTypes.length()-1);
            updateFileType = StringUtil.replace(uploadFileTypes.toString(),";;",";");
        }

        String[] updateFileTypes =  StringUtil.split(updateFileType,StringUtil.SEMICOLON);
        updateFileTypes = ArrayUtil.deleteRepeated(updateFileTypes,true);
        updateFileType = ArrayUtil.toString(updateFileTypes,StringUtil.SEMICOLON);


        if (names.toString().endsWith("+"))
        {
            names.setLength(names.length()-1);
        }
        role.setId(roleId);
        role.setName(names.toString());
        role.setUserType(userType);
        role.setUseUpload(useUpload);
        role.setOfficeType(officeType);
        role.setUploadSize(uploadSize);
        role.setUploadImageSize(uploadImageSize);
        role.setUploadVideoSize(uploadVideoSize);
        role.setUploadFolder(uploadFolder);
        role.setImages(images);
        role.setOperates(ArrayUtil.toString(lines,"\r\n"));
        role.setUploadFileTypes(updateFileType);
        role.setNamespace(namespace);
        role.setOrganizeId(organizeIdSelf);
        return role;
    }

    /**
     *  分组合并到列表
     * @param roleList 角色列表
     * @return 合并角色
     */
  static  public List<Role> mergeRoleList(List<Role> roleList)
    {
        Map<String,List<Role>> groupRoleList = new HashMap<>();
        for (Role roleTmp:roleList)
        {
            if (CongealEnumType.YES_CONGEAL.getValue() == roleTmp.getCongealType()) {
                continue;
            }
            String keyNamespace = (roleTmp.getNamespace()==null?StringUtil.empty:roleTmp.getNamespace()) + (roleTmp.getOrganizeId()==null?StringUtil.empty:roleTmp.getOrganizeId());
            List<Role> groupRole = groupRoleList.computeIfAbsent(keyNamespace, k -> new ArrayList<>());
            groupRole.add(roleTmp);
        }
        List<Role> result = new ArrayList<>();
        for (List<Role> list:groupRoleList.values())
        {
            if (!ObjectUtil.isEmpty(list))
            {
                result.add(mergeRole(list,null));
            }
        }
        return result;
    }


}
