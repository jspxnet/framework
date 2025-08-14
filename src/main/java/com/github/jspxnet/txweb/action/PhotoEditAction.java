package com.github.jspxnet.txweb.action;

import com.github.jspxnet.boot.res.LanguageRes;
import com.github.jspxnet.enums.ErrorEnumType;
import com.github.jspxnet.json.JSONObject;
import com.github.jspxnet.txweb.annotation.HttpMethod;
import com.github.jspxnet.txweb.annotation.Operate;
import com.github.jspxnet.txweb.annotation.Param;
import com.github.jspxnet.txweb.result.RocResponse;
import com.github.jspxnet.txweb.table.IUploadFile;
import com.github.jspxnet.txweb.view.UploadFileView;
import com.github.jspxnet.upload.multipart.JspxNetFileRenamePolicy;
import com.github.jspxnet.util.StringList;
import com.github.jspxnet.utils.*;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.util.Date;

/**
 * Created by yuan on 2014/6/29 0029.
 * 在线图片编辑器,绑定上传文件
 * 为了实现历史还原功能，以前操作的文件不删除，只有点击确定后才删除
 */
@HttpMethod(caption = "图片编辑器")
public class PhotoEditAction extends UploadFileView {
    //支持的文件类型
    private final String[] fileTypes = new String[]{"gif", "jpg", "jpeg", "png", "bmp"};
    private static final int MAX_HISTORY = 5;

    public PhotoEditAction() {

    }

    @Operate(caption = "剪切")
    public RocResponse<?> cut(@Param(caption = "文件id", required = true) long id,
                           @Param(caption = "左", required = true) int left,
                           @Param(caption = "上", required = true) int top,
                           @Param(caption = "宽", required = true) int width,
                           @Param(caption = "高", required = true) int height) throws Exception {

        //读取图片begin
        Object uploadFileObject = uploadFileDAO.get(uploadFileDAO.getClassType(), id);
        if (uploadFileObject == null) {
            return RocResponse.error(ErrorEnumType.WARN.getValue(),language.getLang(LanguageRes.invalidParameterNotFindFile));
        }

        IUploadFile uploadFile = (IUploadFile) uploadFileObject;
        File file = UploadFileAction.getUploadFile(config, uploadFile.getFileName());
        if (file == null || !file.exists() || !file.isFile() || !file.canRead()) {
            return RocResponse.error(ErrorEnumType.WARN.getValue(),language.getLang(LanguageRes.notFindImage));
        }

        String fileType = FileUtil.getTypePart(file.getName());
        if (!ArrayUtil.inArray(fileTypes, fileType, true)) {
            return RocResponse.error(ErrorEnumType.WARN.getValue(),language.getLang(LanguageRes.notFindImageFormat));
        }
        //读取图片end

        //先备份历史文件 begin
        File history = FileUtil.createFile(new File(file.getParent(), FileUtil.getNamePart(file.getName()) + "_" + FileUtil.getTypePart(file.getName()) + "_" + DateUtil.toString("ddHHmmss") + ".tmp"));
        if (!history.createNewFile()) {
            return RocResponse.error(ErrorEnumType.WARN.getValue(), "write " + language.getLang(LanguageRes.folderWriteError));
        }
        if (!FileUtil.copy(file, history, new JspxNetFileRenamePolicy(),true,false)) {
            return RocResponse.error(ErrorEnumType.WARN.getValue(), "copy " + language.getLang(LanguageRes.folderWriteError));
        }
        if (!history.isFile()) {
            return RocResponse.error(ErrorEnumType.WARN.getValue(), language.getLang(LanguageRes.folderWriteError));
        }

        StringList list = new StringList();
        list.setString(uploadFile.getHistory());
        list.add(history.getName());
        if (list.size() > MAX_HISTORY) {
            File deleteHistoryFile = new File(file.getParent(), list.removeFirst());
            if (deleteHistoryFile.exists() && deleteHistoryFile.isFile()) {
                FileUtil.delete(deleteHistoryFile);
            }
        }
        uploadFile.setHistory(list.toString());
        //先备份历史文件 end

        uploadFile.setLastDate(new Date());

        boolean cutYes = ImageUtil.cut(Files.newInputStream(history.toPath()), Files.newOutputStream(file.toPath()), fileType, left, top, width, height);
        if (cutYes && uploadFileDAO.update(uploadFileObject, new String[]{"history", "lastDate"}) > 0) {
            setActionLogContent(file.getPath());
            list.clear();
            JSONObject json = new JSONObject();
            json.put("name",file.getName());
            json.put("namespace", uploadFileDAO.getNamespace());
            return RocResponse.success(json).setMessage(language.getLang(LanguageRes.cutImageSuccess));
       } else {
            if (file.length() < 10) {
                FileUtil.copy(history, file,  new JspxNetFileRenamePolicy(),true,false);
            }
            list.clear();
            return RocResponse.error(ErrorEnumType.WARN.getValue(), language.getLang(LanguageRes.cutImageFailure));
        }
    }

    @Operate(caption = "缩放")
    public RocResponse<?> thumbnail(@Param(caption = "文件id", required = true) long id,
                          @Param(caption = "宽", required = true) int width,
                          @Param(caption = "高", required = true) int height) throws Exception {
        if (width < 1 || height < 0) {
            return RocResponse.error(ErrorEnumType.WARN.getValue(),language.getLang(LanguageRes.widthParameterError));
        }

        //读取图片begin
        Object uploadFileObject = uploadFileDAO.get(uploadFileDAO.getClassType(),id);
        if (uploadFileObject == null) {
            return RocResponse.error(ErrorEnumType.WARN.getValue(),language.getLang(LanguageRes.notFindImage));
        }
        IUploadFile uploadFile = (IUploadFile) uploadFileObject;
        File file = UploadFileAction.getUploadFile(config, uploadFile.getFileName());
        if (file == null || !file.exists() || !file.canRead()) {
            return RocResponse.error(ErrorEnumType.WARN.getValue(),language.getLang(LanguageRes.notFindImage));
        }
        //读取图片end
        String fileType = FileUtil.getTypePart(file.getName());
        if (!ArrayUtil.inArray(fileTypes, fileType, true)) {
            return RocResponse.error(ErrorEnumType.WARN.getValue(),language.getLang(LanguageRes.notFindImageFormat));
        }

        //先备份历史文件 begin
        File history = FileUtil.createFile(new File(file.getParent(), FileUtil.getNamePart(file.getName()) + "_" + FileUtil.getTypePart(file.getName()) + "_" + DateUtil.toString("ddHHmmss") + ".tmp"));
        if (!FileUtil.copy(file, history,  new JspxNetFileRenamePolicy(),true,false)) {
            return RocResponse.error(ErrorEnumType.WARN.getValue(),language.getLang(LanguageRes.folderWriteError));
        }
        if (!history.isFile()) {
            return RocResponse.error(ErrorEnumType.WARN.getValue(),language.getLang(LanguageRes.folderWriteError));
        }

        StringList list = new StringList();
        list.setString(uploadFile.getHistory());
        list.add(history.getName());
        if (list.size() > MAX_HISTORY) {
            File deleteHistoryFile = new File(file.getParent(), list.removeFirst());
            if (deleteHistoryFile.exists() && deleteHistoryFile.isFile()) {
                FileUtil.delete(deleteHistoryFile);
            }
        }
        uploadFile.setHistory(list.toString());
        //先备份历史文件 end

        boolean thumbnailYes = ImageUtil.thumbnail(Files.newInputStream(history.toPath()), Files.newOutputStream(file.toPath()), fileType, width, height);
        uploadFile.setLastDate(new Date());
        if (thumbnailYes && uploadFileDAO.update(uploadFileObject, new String[]{"history", "lastDate"}) > 0) {
            setActionLogContent(file.getPath());
            list.clear();
            JSONObject json = new JSONObject();
            json.put("name",file.getName());
            json.put("namespace", uploadFileDAO.getNamespace());
            return RocResponse.success(json).setMessage(language.getLang(LanguageRes.operationSuccess));
        } else {
            list.clear();
            return RocResponse.error(ErrorEnumType.WARN.getValue(),language.getLang(LanguageRes.operationFailure));
        }
    }

    @Operate(caption = "旋转")
    public RocResponse<?> rotate(@Param(caption = "文件id", required = true) long id,
                                      @Param(caption = "角度", required = true) int degree) throws Exception {
        //读取图片begin
        Object uploadFileObject = getUploadFile(id);
        if (uploadFileObject == null) {
            return RocResponse.error(ErrorEnumType.WARN.getValue(),language.getLang(LanguageRes.widthParameterError));
        }
        IUploadFile uploadFile = (IUploadFile) uploadFileObject;
        File file = UploadFileAction.getUploadFile(config, uploadFile.getFileName());
        if (file == null || !file.exists()  || !file.canRead()) {
            return RocResponse.error(ErrorEnumType.WARN.getValue(),language.getLang(LanguageRes.notFindImage));
        }
        //读取图片end
        String fileType = FileUtil.getTypePart(file.getName());
        if (!ArrayUtil.inArray(fileTypes, fileType, true)) {
            return RocResponse.error(ErrorEnumType.WARN.getValue(),language.getLang(LanguageRes.notFindImageFormat));
        }


        if (degree == 0) {
            return RocResponse.error(ErrorEnumType.PARAMETERS);
        }
        if (degree > 360) {
            return RocResponse.error(ErrorEnumType.PARAMETERS);
        }
        if (degree < 0) {
            degree = 360 - degree;
        }
        if (degree < 0) {
            return RocResponse.error(ErrorEnumType.PARAMETERS);
        }

        //先备份历史文件 begin
        File history = FileUtil.createFile(new File(file.getParent(), FileUtil.getNamePart(file.getName()) + "_" + FileUtil.getTypePart(file.getName()) + DateUtil.toString("ddHHmmss") + ".tmp"));
        if (!FileUtil.copy(file, history,  new JspxNetFileRenamePolicy(),true,false)) {
            return RocResponse.error(ErrorEnumType.WARN.getValue(),language.getLang(LanguageRes.folderWriteError));
        }
        if (!history.isFile()) {
            return RocResponse.error(ErrorEnumType.WARN.getValue(),language.getLang(LanguageRes.operationFailure) + ",copy");
        }

        StringList list = new StringList();
        list.setString(uploadFile.getHistory());
        list.add(history.getName());
        if (list.size() > MAX_HISTORY) {
            File deleteHistoryFile = new File(file.getParent(), list.removeFirst());
            if (deleteHistoryFile.exists() && deleteHistoryFile.isFile()) {
                FileUtil.delete(deleteHistoryFile);
            }
        }
        uploadFile.setHistory(list.toString());

        //先备份历史文件 end
        boolean rotateYes = ImageUtil.rotate(Files.newInputStream(history.toPath()), Files.newOutputStream(file.toPath()), fileType, degree);
        uploadFile.setLastDate(new Date());
        if (rotateYes && uploadFileDAO.update(uploadFileObject, new String[]{"history", "lastDate"}) > 0) {
            setActionLogContent(file.getPath());
            list.clear();
            JSONObject json = new JSONObject();
            json.put("name",file.getName());
            json.put("namespace", uploadFileDAO.getNamespace());
            return RocResponse.success(json).setMessage(language.getLang(LanguageRes.rotateOperationSuccess) + ",copy");
        } else {
            list.clear();
            return RocResponse.error(ErrorEnumType.WARN.getValue(),language.getLang(LanguageRes.rotateOperationFailure));
        }

    }


    @Operate(caption = "黑白")
    public RocResponse<?> gray(@Param(caption = "文件id", required = true) long id) throws Exception {
        //读取图片begin
        Object uploadFileObject = getUploadFile(id);
        if (uploadFileObject == null) {
            return RocResponse.error(ErrorEnumType.WARN.getValue(),language.getLang(LanguageRes.widthParameterError));
        }
        IUploadFile uploadFile = (IUploadFile) uploadFileObject;
        File file = UploadFileAction.getUploadFile(config, uploadFile.getFileName());
        if (file == null || !file.exists() || !file.isFile() || !file.canRead()) {
            return RocResponse.error(ErrorEnumType.WARN.getValue(),language.getLang(LanguageRes.notFindImage));
        }
        //读取图片end
        String fileType = FileUtil.getTypePart(file.getName());
        if (!ArrayUtil.inArray(fileTypes, fileType, true)) {
            return RocResponse.error(ErrorEnumType.WARN.getValue(),language.getLang(LanguageRes.notFindImageFormat));
        }

        //先备份历史文件 begin
        File history = FileUtil.createFile(new File(file.getParent(), FileUtil.getNamePart(file.getName()) + "_" + FileUtil.getTypePart(file.getName()) + "_" + DateUtil.toString("ddHHmmss") + ".tmp"));
        if (!FileUtil.copy(file, history,  new JspxNetFileRenamePolicy(),true,false)) {
            return RocResponse.error(ErrorEnumType.WARN.getValue(),language.getLang(LanguageRes.folderWriteError));
        }
        if (!history.isFile()) {
            return RocResponse.error(ErrorEnumType.WARN.getValue(),language.getLang(LanguageRes.operationFailure) + ",copy");
        }

        StringList list = new StringList();
        list.setString(uploadFile.getHistory());
        list.add(history.getName());
        if (list.size() > MAX_HISTORY) {
            File deleteHistoryFile = new File(file.getParent(), list.removeFirst());
            if (deleteHistoryFile.exists() && deleteHistoryFile.isFile()) {
                FileUtil.delete(deleteHistoryFile);
            }
        }
        uploadFile.setHistory(list.toString());

        //先备份历史文件 end
        boolean grayYes = ImageUtil.gray(Files.newInputStream(history.toPath()), Files.newOutputStream(file.toPath()), fileType);
        uploadFile.setLastDate(new Date());
        if (grayYes && uploadFileDAO.update(uploadFileObject, new String[]{"history", "lastDate"}) > 0) {
            setActionLogContent(file.getPath());
            list.clear();
            JSONObject json = new JSONObject();
            json.put("name",file.getName());
            json.put("namespace", uploadFileDAO.getNamespace());
            return RocResponse.success(json).setMessage(language.getLang(LanguageRes.grayOperationSuccess));
        } else {
            list.clear();
            return RocResponse.error(ErrorEnumType.WARN.getValue(),language.getLang(LanguageRes.grayOperationFailure) + ",copy");
        }
    }

    @Operate(caption = "加亮")
    public RocResponse<?> highlight(@Param(caption = "文件id", required = true) long id) throws Exception {

        //读取图片begin
        Object uploadFileObject = getUploadFile(id);
        if (uploadFileObject == null) {
            return RocResponse.error(ErrorEnumType.WARN.getValue(),language.getLang(LanguageRes.widthParameterError));
        }
        IUploadFile uploadFile = (IUploadFile) uploadFileObject;
        File file = UploadFileAction.getUploadFile(config, uploadFile.getFileName());
        if (file == null || !file.exists()  || !file.canRead()) {
            return RocResponse.error(ErrorEnumType.WARN.getValue(),language.getLang(LanguageRes.notFindImage));
        }
        //读取图片end
        String fileType = FileUtil.getTypePart(file.getName());
        if (!ArrayUtil.inArray(fileTypes, fileType, true)) {
            return RocResponse.error(ErrorEnumType.WARN.getValue(),language.getLang(LanguageRes.notFindImageFormat));
        }

        //先备份历史文件 begin
        File history = FileUtil.createFile(new File(file.getParent(), FileUtil.getNamePart(file.getName()) + "_" + FileUtil.getTypePart(file.getName()) + "_" + DateUtil.toString("ddHHmmss") + ".tmp"));
        if (!FileUtil.copy(file, history,  new JspxNetFileRenamePolicy(),true,false)) {
            return RocResponse.error(ErrorEnumType.WARN.getValue(),language.getLang(LanguageRes.folderWriteError));
        }
        if (!history.isFile()) {
            return RocResponse.error(ErrorEnumType.WARN.getValue(),language.getLang(LanguageRes.operationFailure) + ",copy");
        }


        StringList list = new StringList();
        list.setString(uploadFile.getHistory());
        list.add(history.getName());
        if (list.size() > MAX_HISTORY) {
            File deleteHistoryFile = new File(file.getParent(), list.removeFirst());
            if (deleteHistoryFile.exists() && deleteHistoryFile.isFile()) {
                FileUtil.delete(deleteHistoryFile);
            }
        }
        uploadFile.setHistory(list.toString());

        //先备份历史文件 end
        boolean grayYes = ImageUtil.filter(Files.newInputStream(history.toPath()), Files.newOutputStream(file.toPath()), fileType, 1.1f, 1.3f);
        uploadFile.setLastDate(new Date());
        if (grayYes && uploadFileDAO.update(uploadFileObject, new String[]{"history", "lastDate"}) > 0) {
            setActionLogContent(file.getPath());
            list.clear();
            JSONObject json = new JSONObject();
            json.put("name",file.getName());
            json.put("namespace", uploadFileDAO.getNamespace());
            return RocResponse.success(json).setMessage(language.getLang(LanguageRes.grayOperationSuccess));
        } else {
            list.clear();
            return RocResponse.error(ErrorEnumType.WARN.getValue(),language.getLang(LanguageRes.highlightOperationFailure));
        }
    }

    @Operate(caption = "变暗")
    public RocResponse<?> darkened(@Param(caption = "文件id", required = true) long id) throws Exception {
        //读取图片begin
        Object uploadFileObject = getUploadFile(id);
        if (uploadFileObject == null) {
            return RocResponse.error(ErrorEnumType.WARN.getValue(),language.getLang(LanguageRes.widthParameterError));
        }
        IUploadFile uploadFile = (IUploadFile) uploadFileObject;
        File file = UploadFileAction.getUploadFile(config, uploadFile.getFileName());
        if (file == null || !file.exists() || !file.isFile() || !file.canRead()) {
            return RocResponse.error(ErrorEnumType.WARN.getValue(),language.getLang(LanguageRes.notFindImage));
        }
        //读取图片end
        String fileType = FileUtil.getTypePart(file.getName());
        if (!ArrayUtil.inArray(fileTypes, fileType, true)) {
            return RocResponse.error(ErrorEnumType.WARN.getValue(),language.getLang(LanguageRes.notFindImageFormat));
        }

        //先备份历史文件 begin
        File history = FileUtil.createFile(new File(file.getParent(), FileUtil.getNamePart(file.getName()) + "_" + FileUtil.getTypePart(file.getName()) + "_" + DateUtil.toString("ddHHmmss") + ".tmp"));
        if (!FileUtil.copy(file, history,  new JspxNetFileRenamePolicy(),true,false)) {
            return RocResponse.error(ErrorEnumType.WARN.getValue(),language.getLang(LanguageRes.folderWriteError));
        }
        if (!history.isFile()) {
            return RocResponse.error(ErrorEnumType.WARN.getValue(),language.getLang(LanguageRes.operationFailure) + ",copy");
        }


        StringList list = new StringList();
        list.setString(uploadFile.getHistory());
        list.add(history.getName());
        if (list.size() > MAX_HISTORY) {
            File deleteHistoryFile = new File(file.getParent(), list.removeFirst());
            if (deleteHistoryFile.exists() && deleteHistoryFile.isFile()) {
                FileUtil.delete(deleteHistoryFile);
            }
        }
        uploadFile.setHistory(list.toString());
        //先备份历史文件 end

        boolean grayYes = ImageUtil.filter(Files.newInputStream(history.toPath()), Files.newOutputStream(file.toPath()), fileType, 0.9f, 0.9f);
        uploadFile.setLastDate(new Date());
        if (grayYes && uploadFileDAO.update(uploadFileObject, new String[]{"history", "lastDate"}) > 0) {
            setActionLogContent(file.getPath());
            list.clear();
            JSONObject json = new JSONObject();
            json.put("name",file.getName());
            json.put("namespace", uploadFileDAO.getNamespace());
            return RocResponse.success(json).setMessage(language.getLang(LanguageRes.grayOperationSuccess));
        } else {
            list.clear();
            return RocResponse.error(ErrorEnumType.WARN.getValue(),language.getLang(LanguageRes.darkenedOperationFailure));
        }
    }


    @Operate(caption = "比例")
    public RocResponse<?> scale(@Param(caption = "文件id", required = true) long id,
                                @Param(caption = "比例", required = true) float scaleValue) throws Exception
    {

        if (scaleValue <= 0 || scaleValue == 1 || scaleValue == 100 || scaleValue > 300) {
            return RocResponse.error(ErrorEnumType.WARN.getValue(),language.getLang(LanguageRes.scaleParameterError));
        }

        if (scaleValue > 5) {
            scaleValue = scaleValue / 100;
        }
        if (scaleValue <= 0 || scaleValue == 1 || scaleValue == 100) {
            return RocResponse.error(ErrorEnumType.WARN.getValue(),language.getLang(LanguageRes.scaleParameterError));
        }

        //读取图片begin
        Object uploadFileObject = getUploadFile(id);
        if (uploadFileObject == null) {
            return RocResponse.error(ErrorEnumType.WARN.getValue(),language.getLang(LanguageRes.invalidParameter));
        }
        IUploadFile uploadFile = (IUploadFile) uploadFileObject;
        File file = UploadFileAction.getUploadFile(config, uploadFile.getFileName());
        if (file == null || !file.exists() || !file.isFile() || !file.canRead()) {
            return RocResponse.error(ErrorEnumType.WARN.getValue(),language.getLang(LanguageRes.notFindImage));
        }
        //读取图片end
        String fileType = FileUtil.getTypePart(file.getName());
        if (!ArrayUtil.inArray(fileTypes, fileType, true)) {
            return RocResponse.error(ErrorEnumType.WARN.getValue(),language.getLang(LanguageRes.notFindImageFormat));
        }

        //先备份历史文件 begin
        File history = FileUtil.createFile(new File(file.getParent(), FileUtil.getNamePart(file.getName()) + "_" + FileUtil.getTypePart(file.getName()) + "_" + DateUtil.toString("ddHHmmss") + ".tmp"));
        if (!FileUtil.copy(file, history,  new JspxNetFileRenamePolicy(),true,false)) {
            return RocResponse.error(ErrorEnumType.WARN.getValue(),language.getLang(LanguageRes.folderWriteError));
        }
        if (!history.isFile()) {
            return RocResponse.error(ErrorEnumType.WARN.getValue(),language.getLang(LanguageRes.operationFailure) + ",copy");
        }

        StringList list = new StringList();
        list.setString(uploadFile.getHistory());
        list.add(history.getName());
        if (list.size() > MAX_HISTORY) {
            File deleteHistoryFile = new File(file.getParent(), list.removeFirst());
            if (deleteHistoryFile.exists() && deleteHistoryFile.isFile()) {
                FileUtil.delete(deleteHistoryFile);
            }
        }
        uploadFile.setHistory(list.toString());
        //先备份历史文件 end


        boolean scaleYes = ImageUtil.scale(Files.newInputStream(history.toPath()), Files.newOutputStream(file.toPath()), fileType, scaleValue);
        uploadFile.setLastDate(new Date());
        if (scaleYes && uploadFileDAO.update(uploadFileObject, new String[]{"history", "lastDate"}) > 0) {
            setActionLogContent(file.getPath());
            list.clear();
            JSONObject json = new JSONObject();
            json.put("name",file.getName());
            json.put("namespace", uploadFileDAO.getNamespace());
            return RocResponse.success(json).setMessage(language.getLang(LanguageRes.scaleOperationSuccess));
        } else {
            list.clear();
            return RocResponse.error(ErrorEnumType.WARN.getValue(),language.getLang(LanguageRes.scaleOperationFailure));
        }

    }


    @Operate(caption = "回退")
    public RocResponse<?> history(@Param(caption = "文件id", required = true) long id) throws Exception {
        //读取图片begin
        Object uploadFileObject = getUploadFile(id);
        if (uploadFileObject == null) {
            return RocResponse.error(ErrorEnumType.WARN.getValue(),language.getLang(LanguageRes.invalidParameter));
        }
        IUploadFile uploadFile = (IUploadFile) uploadFileObject;
        File file = UploadFileAction.getUploadFile(config, uploadFile.getFileName());
        if (file == null || !file.exists() || !file.isFile() || !file.canRead()) {
            return RocResponse.error(ErrorEnumType.WARN.getValue(),language.getLang(LanguageRes.notFindImage));
        }
        //读取图片end
        String fileType = FileUtil.getTypePart(file.getName());
        if (!ArrayUtil.inArray(fileTypes, fileType, true)) {
            return RocResponse.error(ErrorEnumType.WARN.getValue(),language.getLang(LanguageRes.notFindImageFormat));
        }

        //先备份历史文件 begin
        if (StringUtil.isNull(uploadFile.getHistory())) {
            return RocResponse.error(ErrorEnumType.WARN.getValue(),language.getLang(LanguageRes.notDataResult));
        }
        StringList list = new StringList();
        list.setString(uploadFile.getHistory());
        if (list.isEmpty()) {
            return RocResponse.error(ErrorEnumType.WARN.getValue(),language.getLang(LanguageRes.notDataResult));
        }
        String fileName = list.removeLast();
        if (StringUtil.isNull(fileName)) {
            return RocResponse.error(ErrorEnumType.WARN.getValue(),language.getLang(LanguageRes.notDataResult) + ":" + fileName);
        }
        File history = new File(file.getParent(), fileName);
        if (!history.isFile()) {
            return RocResponse.error(ErrorEnumType.WARN.getValue(),language.getLang(LanguageRes.historyFileNotFind));
        }
        uploadFile.setHistory(list.toString());
        uploadFile.setLastDate(new Date());
        if (FileUtil.copy(history, file,  new JspxNetFileRenamePolicy(),true,false) && uploadFileDAO.update(uploadFileObject, new String[]{"history", "lastDate"}) > 0) {
            setActionLogContent(file.getPath());
            FileUtil.delete(history);
            list.clear();
            JSONObject json = new JSONObject();
            json.put("name",file.getName());
            json.put("namespace", uploadFileDAO.getNamespace());
            return RocResponse.success(json).setMessage(language.getLang(LanguageRes.operationSuccess));
        } else {
            list.clear();
            return RocResponse.error(ErrorEnumType.WARN.getValue(),language.getLang(LanguageRes.operationFailure));
        }
        //先备份历史文件 end
    }

    @Operate(caption = "确定")
    public RocResponse<?> quit(@Param(caption = "文件id", required = true) long id) throws Exception {
        //读取图片begin
        Object uploadFileObject = getUploadFile(id);
        if (uploadFileObject == null) {
            return RocResponse.error(ErrorEnumType.WARN.getValue(),language.getLang(LanguageRes.invalidParameter));
        }
        IUploadFile uploadFile = (IUploadFile) uploadFileObject;
        File file = UploadFileAction.getUploadFile(config, uploadFile.getFileName());
        if (file == null || !file.exists() || !file.isFile() || !file.canRead()) {
            return RocResponse.error(ErrorEnumType.WARN.getValue(),language.getLang(LanguageRes.notFindImage));
        }
        //读取图片end
        String fileType = FileUtil.getTypePart(file.getName());
        if (!ArrayUtil.inArray(fileTypes, fileType, true)) {
            return RocResponse.error(ErrorEnumType.WARN.getValue(),language.getLang(LanguageRes.notFindImageFormat));
        }
        //先备份历史文件 begin

        StringList list = new StringList();
        list.setString(uploadFile.getHistory());
        for (String fileName : list.toArray()) {
            if (StringUtil.isNull(fileName)) {
                continue;
            }
            File deleteHistoryFile = new File(file.getParent(), fileName);
            if (deleteHistoryFile.isFile()) {
                FileUtil.delete(deleteHistoryFile);
            }
        }

        JSONObject json = new JSONObject();
        BufferedImage image = ImageIO.read(file);
        uploadFile.setHistory(StringUtil.empty);
        uploadFile.setAttributes("width=" + image.getWidth() + "\r\nheight=" + image.getHeight());
        uploadFile.setFileSize(file.length());
        uploadFile.setHash(FileUtil.getHash(file, UploadFileAction.hashType));

        if (uploadFileDAO.update(uploadFileObject, new String[]{"attributes", "fileSize", "hash", "history"}) > 0) {
            setActionLogContent(file.getPath());
            //--------------------------------------
            String thumbnailFileName = FileUtil.getThumbnailFileName(file.getPath());
            File thumbnailFile = new File(thumbnailFileName);
            if (thumbnailFile.isFile()) {
                //如果有缩图，重新创建覆盖
                int tWidth = config.getInt("thumbnailWidth", 400);
                int tHeight = config.getInt("thumbnailHeight", 400);
                if (ImageUtil.thumbnail(Files.newInputStream(file.toPath()), Files.newOutputStream(thumbnailFile.toPath()), FileUtil.getTypePart(file), tWidth, tHeight)) {

                    json.put("thumbnail", 1);
                } else {
                    json.put("thumbnail", 0);
                }
            }
            String mobileFileName = FileUtil.getMobileFileName(file.getPath());
            File mobileFile = new File(mobileFileName);
            if (mobileFile.isFile()) {
                int mobileWidth = config.getInt("mobileWidth", 480);
                int mobileHeight = config.getInt("mobileHeight", 480);
                if (ImageUtil.thumbnail(Files.newInputStream(file.toPath()), Files.newOutputStream(mobileFile.toPath()), FileUtil.getTypePart(file), mobileWidth, mobileHeight)) {
                    json.put("mobile", 1);
                } else {
                    json.put("mobile", 0);
                }
            }
            json.put("name",file.getName());
            json.put("namespace", uploadFileDAO.getNamespace());
            list.clear();
            return RocResponse.success(json).setMessage(language.getLang(LanguageRes.editFinishClearTemp));
        } else {
            list.clear();
            return RocResponse.error(ErrorEnumType.WARN.getValue(),language.getLang(LanguageRes.clearTempFileFailure));

        }
        //先备份历史文件 end

    }

    @Override
    public String execute() throws Exception {
        if (isMethodInvoked()) {
            uploadFileDAO.evict(uploadFileDAO.getClassType());
        }
        put("namespace", uploadFileDAO.getNamespace());
        return super.execute();
    }

}