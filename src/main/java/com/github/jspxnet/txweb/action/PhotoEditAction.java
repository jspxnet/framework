package com.github.jspxnet.txweb.action;

import com.github.jspxnet.boot.environment.Environment;
import com.github.jspxnet.boot.res.LanguageRes;
import com.github.jspxnet.json.JSONObject;
import com.github.jspxnet.txweb.annotation.HttpMethod;
import com.github.jspxnet.txweb.annotation.Operate;
import com.github.jspxnet.txweb.table.IUploadFile;
import com.github.jspxnet.txweb.view.UploadFileView;
import com.github.jspxnet.util.StringList;

import com.github.jspxnet.utils.*;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Date;

/**
 * Created by yuan on 2014/6/29 0029.
 * 在线图片编辑器,绑定上传文件
 * 为了实现历史还原功能，以前操作的文件不删除，只有点击确定后才删除
 */
@Deprecated
@HttpMethod(caption = "图片编辑器")
public class PhotoEditAction extends UploadFileView {
    //支持的文件类型
    private String[] fileTypes = new String[]{"gif", "jpg", "jpeg", "png", "bmp"};
    private static final int maxHistory = 5;
    private JSONObject json = new JSONObject();

    public PhotoEditAction() {
        json.put(SUCCESS, 0);
        json.put(ERROR, 1);
        json.put(Environment.infoType, Environment.warningInfo);
    }

    private int top = 0;
    private int left = 0;
    private int width = 0;
    private int height = 0;
    private int degree = 0;
    private float scaleValue = 0;

    public int getTop() {
        return top;
    }

    public void setTop(int top) {
        this.top = top;
    }

    public int getLeft() {
        return left;
    }

    public void setLeft(int left) {
        this.left = left;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getDegree() {
        return degree;
    }

    public void setDegree(int degree) {
        this.degree = degree;
    }


    public float getScaleValue() {
        return scaleValue;
    }

    public void setScaleValue(float scaleValue) {
        this.scaleValue = scaleValue;
    }

    @Operate(caption = "剪切")
    public void cut() throws Exception {

        //读取图片begin
        Object uploadFileObject = uploadFileDAO.get(uploadFileDAO.getClassType(), getId());
        if (uploadFileObject == null) {
            json.put(Environment.MESSAGE, language.getLang(LanguageRes.invalidParameterNotFindFile));
            return;
        }

        IUploadFile uploadFile = (IUploadFile) uploadFileObject;
        File file = UploadFileAction.getUploadFile(config, uploadFile.getFileName());
        if (file == null || !file.exists() || !file.isFile() || !file.canRead()) {
            json.put(Environment.MESSAGE, language.getLang(LanguageRes.notFindImage));
            return;
        }

        String fileType = FileUtil.getTypePart(file.getName());
        if (!ArrayUtil.inArray(fileTypes, fileType, true)) {
            json.put(Environment.MESSAGE, language.getLang(LanguageRes.notFindImageFormat));
            return;
        }
        //读取图片end

        //先备份历史文件 begin
        File history = FileUtil.createFile(new File(file.getParent(), FileUtil.getNamePart(file.getName()) + "_" + FileUtil.getTypePart(file.getName()) + "_" + DateUtil.toString("ddHHmmss") + ".tmp"));
        if (!history.createNewFile()) {
            json.put(Environment.MESSAGE, "write " + language.getLang(LanguageRes.folderWriteError));
            return;
        }
        if (!FileUtil.copy(file, history, true)) {
            json.put(Environment.MESSAGE, "copy " + language.getLang(LanguageRes.folderWriteError));
            return;
        }
        if (!history.isFile()) {
            json.put(Environment.MESSAGE, language.getLang(LanguageRes.folderWriteError));
            return;
        }

        StringList list = new StringList();
        list.setString(uploadFile.getHistory());
        list.add(history.getName());
        if (list.size() > maxHistory) {
            File deleteHistoryFile = new File(file.getParent(), list.removeFirst());
            if (deleteHistoryFile.exists() && deleteHistoryFile.isFile()) {
                FileUtil.delete(deleteHistoryFile);
            }
        }
        uploadFile.setHistory(list.toString());
        //先备份历史文件 end

        uploadFile.setLastDate(new Date());

        boolean cutYes = ImageUtil.cut(new FileInputStream(history), new FileOutputStream(file), fileType, left, top, width, height);
        if (cutYes && uploadFileDAO.update(uploadFileObject, new String[]{"history", "lastDate"}) > 0) {
            setActionLogContent(file.getPath());
            json.put(SUCCESS, 1);
            json.put(ERROR, 0);
            json.put(Environment.infoType, Environment.promptInfo);
            json.put(Environment.MESSAGE, language.getLang(LanguageRes.cutImageSuccess));
        } else {
            if (file.length() < 10) {
                FileUtil.copy(history, file, true);
            }
            json.put(Environment.MESSAGE, language.getLang(LanguageRes.cutImageFailure));
        }
        list.clear();

    }

    @Operate(caption = "缩放")
    public void thumbnail() throws Exception {
        if (width < 1 || height < 0) {

            json.put(Environment.MESSAGE, language.getLang(LanguageRes.widthParameterError));
            return;
        }

        //读取图片begin
        Object uploadFileObject = uploadFileDAO.get(uploadFileDAO.getClassType(), getId());
        if (uploadFileObject == null) {
            json.put(Environment.MESSAGE, language.getLang(LanguageRes.notFindImage));
            return;
        }
        IUploadFile uploadFile = (IUploadFile) uploadFileObject;
        File file = UploadFileAction.getUploadFile(config, uploadFile.getFileName());
        if (file == null || !file.exists() || !file.isFile() || !file.canRead()) {
            json.put(Environment.MESSAGE, language.getLang(LanguageRes.notFindImage));
            return;
        }
        //读取图片end
        String fileType = FileUtil.getTypePart(file.getName());
        if (!ArrayUtil.inArray(fileTypes, fileType, true)) {
            json.put(Environment.MESSAGE, language.getLang(LanguageRes.notFindImageFormat));
            return;
        }

        //先备份历史文件 begin
        File history = FileUtil.createFile(new File(file.getParent(), FileUtil.getNamePart(file.getName()) + "_" + FileUtil.getTypePart(file.getName()) + "_" + DateUtil.toString("ddHHmmss") + ".tmp"));
        if (!FileUtil.copy(file, history, true)) {
            json.put(Environment.MESSAGE, language.getLang(LanguageRes.folderWriteError));
            return;
        }
        if (!history.isFile()) {
            json.put(Environment.MESSAGE, language.getLang(LanguageRes.folderWriteError));
            return;
        }

        StringList list = new StringList();
        list.setString(uploadFile.getHistory());
        list.add(history.getName());
        if (list.size() > maxHistory) {
            File deleteHistoryFile = new File(file.getParent(), list.removeFirst());
            if (deleteHistoryFile.exists() && deleteHistoryFile.isFile()) {
                FileUtil.delete(deleteHistoryFile);
            }
        }
        uploadFile.setHistory(list.toString());
        //先备份历史文件 end

        boolean thumbnailYes = ImageUtil.thumbnail(new FileInputStream(history), new FileOutputStream(file), fileType, width, height);
        uploadFile.setLastDate(new Date());
        if (thumbnailYes && uploadFileDAO.update(uploadFileObject, new String[]{"history", "lastDate"}) > 0) {
            setActionLogContent(file.getPath());
            json.put(SUCCESS, 1);
            json.put(ERROR, 0);
            json.put(Environment.infoType, Environment.promptInfo);
            json.put(Environment.MESSAGE, language.getLang(LanguageRes.operationSuccess));
        } else {
            json.put(Environment.MESSAGE, language.getLang(LanguageRes.operationFailure));
        }
        list.clear();
    }

    @Operate(caption = "旋转")
    public void rotate() throws Exception {

        //读取图片begin
        Object uploadFileObject = getUploadFile();
        if (uploadFileObject == null) {
            json.put(Environment.MESSAGE, language.getLang(LanguageRes.widthParameterError));
            return;
        }
        IUploadFile uploadFile = (IUploadFile) uploadFileObject;
        File file = UploadFileAction.getUploadFile(config, uploadFile.getFileName());
        if (file == null || !file.exists() || !file.isFile() || !file.canRead()) {
            json.put(Environment.MESSAGE, language.getLang(LanguageRes.notFindImage));
            return;
        }
        //读取图片end
        String fileType = FileUtil.getTypePart(file.getName());
        if (!ArrayUtil.inArray(fileTypes, fileType, true)) {
            json.put(Environment.MESSAGE, language.getLang(LanguageRes.notFindImageFormat));
            return;
        }


        if (degree == 0) {
            return;
        }
        if (degree > 360) {
            return;
        }
        if (degree < 0) {
            degree = 360 - degree;
        }
        if (degree < 0) {
            return;
        }

        //先备份历史文件 begin
        File history = FileUtil.createFile(new File(file.getParent(), FileUtil.getNamePart(file.getName()) + "_" + FileUtil.getTypePart(file.getName()) + DateUtil.toString("ddHHmmss") + ".tmp"));
        if (!FileUtil.copy(file, history, true)) {
            json.put(Environment.MESSAGE, language.getLang(LanguageRes.folderWriteError));
            return;
        }
        if (!history.isFile()) {
            json.put(Environment.MESSAGE, language.getLang(LanguageRes.operationFailure) + ",copy");
            return;
        }

        StringList list = new StringList();
        list.setString(uploadFile.getHistory());
        list.add(history.getName());
        if (list.size() > maxHistory) {
            File deleteHistoryFile = new File(file.getParent(), list.removeFirst());
            if (deleteHistoryFile.exists() && deleteHistoryFile.isFile()) {
                FileUtil.delete(deleteHistoryFile);
            }
        }
        uploadFile.setHistory(list.toString());

        //先备份历史文件 end
        boolean rotateYes = ImageUtil.rotate(new FileInputStream(history), new FileOutputStream(file), fileType, degree);
        uploadFile.setLastDate(new Date());
        if (rotateYes && uploadFileDAO.update(uploadFileObject, new String[]{"history", "lastDate"}) > 0) {
            setActionLogContent(file.getPath());
            json.put(SUCCESS, 1);
            json.put(ERROR, 0);
            json.put(Environment.infoType, Environment.promptInfo);
            json.put(Environment.MESSAGE, language.getLang(LanguageRes.rotateOperationSuccess));
        } else {
            json.put(Environment.MESSAGE, language.getLang(LanguageRes.rotateOperationFailure));
        }
        list.clear();
    }


    @Operate(caption = "黑白")
    public void gray() throws Exception {
        //读取图片begin
        Object uploadFileObject = getUploadFile();
        if (uploadFileObject == null) {
            json.put(Environment.MESSAGE, language.getLang(LanguageRes.widthParameterError));
            return;
        }
        IUploadFile uploadFile = (IUploadFile) uploadFileObject;
        File file = UploadFileAction.getUploadFile(config, uploadFile.getFileName());
        if (file == null || !file.exists() || !file.isFile() || !file.canRead()) {
            json.put(Environment.MESSAGE, language.getLang(LanguageRes.notFindImage));
            return;
        }
        //读取图片end
        String fileType = FileUtil.getTypePart(file.getName());
        if (!ArrayUtil.inArray(fileTypes, fileType, true)) {
            json.put(Environment.MESSAGE, language.getLang(LanguageRes.notFindImageFormat));
            return;
        }

        //先备份历史文件 begin
        File history = FileUtil.createFile(new File(file.getParent(), FileUtil.getNamePart(file.getName()) + "_" + FileUtil.getTypePart(file.getName()) + "_" + DateUtil.toString("ddHHmmss") + ".tmp"));
        if (!FileUtil.copy(file, history, true)) {
            json.put(Environment.MESSAGE, language.getLang(LanguageRes.folderWriteError));
            return;
        }
        if (!history.isFile()) {
            json.put(Environment.MESSAGE, language.getLang(LanguageRes.operationFailure) + ",copy");
            return;
        }

        StringList list = new StringList();
        list.setString(uploadFile.getHistory());
        list.add(history.getName());
        if (list.size() > maxHistory) {
            File deleteHistoryFile = new File(file.getParent(), list.removeFirst());
            if (deleteHistoryFile.exists() && deleteHistoryFile.isFile()) {
                FileUtil.delete(deleteHistoryFile);
            }
        }
        uploadFile.setHistory(list.toString());

        //先备份历史文件 end
        boolean grayYes = ImageUtil.gray(new FileInputStream(history), new FileOutputStream(file), fileType);
        uploadFile.setLastDate(new Date());
        if (grayYes && uploadFileDAO.update(uploadFileObject, new String[]{"history", "lastDate"}) > 0) {
            setActionLogContent(file.getPath());
            json.put(SUCCESS, 1);
            json.put(ERROR, 0);
            json.put(Environment.infoType, Environment.promptInfo);
            json.put(Environment.MESSAGE, language.getLang(LanguageRes.grayOperationSuccess));
        } else {
            json.put(Environment.MESSAGE, language.getLang(LanguageRes.grayOperationFailure));
        }
        list.clear();
    }

    @Operate(caption = "加亮")
    public void highlight() throws Exception {

        //读取图片begin
        Object uploadFileObject = getUploadFile();
        if (uploadFileObject == null) {
            json.put(Environment.MESSAGE, language.getLang(LanguageRes.widthParameterError));
            return;
        }
        IUploadFile uploadFile = (IUploadFile) uploadFileObject;
        File file = UploadFileAction.getUploadFile(config, uploadFile.getFileName());
        if (file == null || !file.exists() || !file.isFile() || !file.canRead()) {
            json.put(Environment.MESSAGE, language.getLang(LanguageRes.notFindImage));
            return;
        }
        //读取图片end
        String fileType = FileUtil.getTypePart(file.getName());
        if (!ArrayUtil.inArray(fileTypes, fileType, true)) {
            json.put(Environment.MESSAGE, language.getLang(LanguageRes.notFindImageFormat));
            return;
        }

        //先备份历史文件 begin
        File history = FileUtil.createFile(new File(file.getParent(), FileUtil.getNamePart(file.getName()) + "_" + FileUtil.getTypePart(file.getName()) + "_" + DateUtil.toString("ddHHmmss") + ".tmp"));
        if (!FileUtil.copy(file, history, true)) {
            json.put(Environment.MESSAGE, language.getLang(LanguageRes.folderWriteError));
            return;
        }
        if (!history.isFile()) {
            json.put(Environment.MESSAGE, language.getLang(LanguageRes.operationFailure) + ",copy");
            return;
        }


        StringList list = new StringList();
        list.setString(uploadFile.getHistory());
        list.add(history.getName());
        if (list.size() > maxHistory) {
            File deleteHistoryFile = new File(file.getParent(), list.removeFirst());
            if (deleteHistoryFile.exists() && deleteHistoryFile.isFile()) {
                FileUtil.delete(deleteHistoryFile);
            }
        }
        uploadFile.setHistory(list.toString());


        //先备份历史文件 end
        boolean grayYes = ImageUtil.filter(new FileInputStream(history), new FileOutputStream(file), fileType, 1.1f, 1.3f);
        uploadFile.setLastDate(new Date());
        if (grayYes && uploadFileDAO.update(uploadFileObject, new String[]{"history", "lastDate"}) > 0) {
            setActionLogContent(file.getPath());
            json.put(SUCCESS, 1);
            json.put(ERROR, 0);
            json.put(Environment.infoType, Environment.promptInfo);
            json.put(Environment.MESSAGE, language.getLang(LanguageRes.highlightOperationSuccess));
        } else {
            json.put(Environment.MESSAGE, language.getLang(LanguageRes.highlightOperationFailure));
        }

        list.clear();
    }

    @Operate(caption = "变暗")
    public void darkened() throws Exception {
        //读取图片begin
        Object uploadFileObject = getUploadFile();
        if (uploadFileObject == null) {
            json.put(Environment.MESSAGE, language.getLang(LanguageRes.widthParameterError));
            return;
        }
        IUploadFile uploadFile = (IUploadFile) uploadFileObject;
        File file = UploadFileAction.getUploadFile(config, uploadFile.getFileName());
        if (file == null || !file.exists() || !file.isFile() || !file.canRead()) {
            json.put(Environment.MESSAGE, language.getLang(LanguageRes.notFindImage));
            return;
        }
        //读取图片end
        String fileType = FileUtil.getTypePart(file.getName());
        if (!ArrayUtil.inArray(fileTypes, fileType, true)) {
            json.put(Environment.MESSAGE, language.getLang(LanguageRes.notFindImageFormat));
            return;
        }

        //先备份历史文件 begin
        File history = FileUtil.createFile(new File(file.getParent(), FileUtil.getNamePart(file.getName()) + "_" + FileUtil.getTypePart(file.getName()) + "_" + DateUtil.toString("ddHHmmss") + ".tmp"));
        if (!FileUtil.copy(file, history, true)) {
            json.put(Environment.MESSAGE, language.getLang(LanguageRes.folderWriteError));
            return;
        }
        if (!history.isFile()) {
            json.put(Environment.MESSAGE, language.getLang(LanguageRes.operationFailure) + ",copy");
            return;
        }


        StringList list = new StringList();
        list.setString(uploadFile.getHistory());
        list.add(history.getName());
        if (list.size() > maxHistory) {
            File deleteHistoryFile = new File(file.getParent(), list.removeFirst());
            if (deleteHistoryFile.exists() && deleteHistoryFile.isFile()) {
                FileUtil.delete(deleteHistoryFile);
            }
        }
        uploadFile.setHistory(list.toString());
        //先备份历史文件 end

        boolean grayYes = ImageUtil.filter(new FileInputStream(history), new FileOutputStream(file), fileType, 0.9f, 0.9f);
        uploadFile.setLastDate(new Date());
        if (grayYes && uploadFileDAO.update(uploadFileObject, new String[]{"history", "lastDate"}) > 0) {
            setActionLogContent(file.getPath());
            json.put(SUCCESS, 1);
            json.put(ERROR, 0);
            json.put(Environment.infoType, Environment.promptInfo);
            json.put(Environment.MESSAGE, language.getLang(LanguageRes.darkenedOperationSuccess));
        } else {
            json.put(Environment.MESSAGE, language.getLang(LanguageRes.darkenedOperationFailure));
        }
        list.clear();
    }


    @Operate(caption = "比例")
    public void scale() throws Exception {

        if (scaleValue <= 0 || scaleValue == 1 || scaleValue == 100 || scaleValue > 300) {
            json.put(Environment.MESSAGE, language.getLang(LanguageRes.scaleParameterError));
            return;
        }

        if (scaleValue > 5) {
            scaleValue = scaleValue / 100;
        }
        if (scaleValue <= 0 || scaleValue == 1 || scaleValue == 100) {
            json.put(Environment.MESSAGE, language.getLang(LanguageRes.scaleParameterError));
            return;
        }

        //读取图片begin
        Object uploadFileObject = getUploadFile();
        if (uploadFileObject == null) {
            json.put(Environment.MESSAGE, language.getLang(LanguageRes.invalidParameter));
            return;
        }
        IUploadFile uploadFile = (IUploadFile) uploadFileObject;
        File file = UploadFileAction.getUploadFile(config, uploadFile.getFileName());
        if (file == null || !file.exists() || !file.isFile() || !file.canRead()) {
            json.put(Environment.MESSAGE, language.getLang(LanguageRes.notFindImage));
            return;
        }
        //读取图片end
        String fileType = FileUtil.getTypePart(file.getName());
        if (!ArrayUtil.inArray(fileTypes, fileType, true)) {
            json.put(Environment.MESSAGE, language.getLang(LanguageRes.notFindImageFormat));
            return;
        }

        //先备份历史文件 begin
        File history = FileUtil.createFile(new File(file.getParent(), FileUtil.getNamePart(file.getName()) + "_" + FileUtil.getTypePart(file.getName()) + "_" + DateUtil.toString("ddHHmmss") + ".tmp"));
        if (!FileUtil.copy(file, history, true)) {
            json.put(Environment.MESSAGE, language.getLang(LanguageRes.folderWriteError));
            return;
        }
        if (!history.isFile()) {
            json.put(Environment.MESSAGE, language.getLang(LanguageRes.operationFailure) + ",copy");
            return;
        }

        StringList list = new StringList();
        list.setString(uploadFile.getHistory());
        list.add(history.getName());
        if (list.size() > maxHistory) {
            File deleteHistoryFile = new File(file.getParent(), list.removeFirst());
            if (deleteHistoryFile.exists() && deleteHistoryFile.isFile()) {
                FileUtil.delete(deleteHistoryFile);
            }
        }
        uploadFile.setHistory(list.toString());
        //先备份历史文件 end


        boolean scaleYes = ImageUtil.scale(new FileInputStream(history), new FileOutputStream(file), fileType, scaleValue);
        uploadFile.setLastDate(new Date());
        if (scaleYes && uploadFileDAO.update(uploadFileObject, new String[]{"history", "lastDate"}) > 0) {
            setActionLogContent(file.getPath());
            json.put(SUCCESS, 1);
            json.put(ERROR, 0);
            json.put(Environment.infoType, Environment.promptInfo);
            json.put(Environment.MESSAGE, language.getLang(LanguageRes.scaleOperationSuccess));
        } else {
            json.put(Environment.MESSAGE, language.getLang(LanguageRes.scaleOperationFailure));
        }
        list.clear();
    }


    @Operate(caption = "回退")
    public void history() throws Exception {
        //读取图片begin
        Object uploadFileObject = getUploadFile();
        if (uploadFileObject == null) {
            json.put(Environment.MESSAGE, language.getLang(LanguageRes.invalidParameter));
            return;
        }
        IUploadFile uploadFile = (IUploadFile) uploadFileObject;
        File file = UploadFileAction.getUploadFile(config, uploadFile.getFileName());
        if (file == null || !file.exists() || !file.isFile() || !file.canRead()) {
            json.put(Environment.MESSAGE, language.getLang(LanguageRes.notFindImage));
            return;
        }
        //读取图片end
        String fileType = FileUtil.getTypePart(file.getName());
        if (!ArrayUtil.inArray(fileTypes, fileType, true)) {
            json.put(Environment.MESSAGE, language.getLang(LanguageRes.notFindImageFormat));
            return;
        }

        //先备份历史文件 begin
        if (StringUtil.isNull(uploadFile.getHistory())) {
            json.put(Environment.MESSAGE, language.getLang(LanguageRes.notDataResult));
            return;
        }
        StringList list = new StringList();
        list.setString(uploadFile.getHistory());
        if (list.isEmpty()) {
            json.put(Environment.MESSAGE, language.getLang(LanguageRes.notDataResult));
            return;
        }
        String fileName = list.removeLast();
        if (StringUtil.isNull(fileName)) {
            json.put(Environment.SUCCESS, 1);
            uploadFile.setHistory(StringUtil.empty);
            json.put(Environment.MESSAGE, language.getLang(LanguageRes.notDataResult) + ":" + fileName);
            return;
        }
        File history = new File(file.getParent(), fileName);
        if (!history.isFile()) {
            json.put(Environment.MESSAGE, language.getLang(LanguageRes.historyFileNotFind));
            return;
        }
        uploadFile.setHistory(list.toString());
        uploadFile.setLastDate(new Date());
        if (FileUtil.copy(history, file, true) && uploadFileDAO.update(uploadFileObject, new String[]{"history", "lastDate"}) > 0) {
            setActionLogContent(file.getPath());
            FileUtil.delete(history);
            json.put(Environment.SUCCESS, 1);
            json.put(Environment.infoType, Environment.promptInfo);
            json.put(Environment.MESSAGE, language.getLang(LanguageRes.operationSuccess));
        } else {

            json.put(Environment.SUCCESS, 0);
            json.put(Environment.MESSAGE, language.getLang(LanguageRes.operationFailure));
        }
        //先备份历史文件 end
        list.clear();
    }

    @Operate(caption = "确定")
    public void quit() throws Exception {
        //读取图片begin
        Object uploadFileObject = getUploadFile();
        if (uploadFileObject == null) {
            json.put(Environment.MESSAGE, language.getLang(LanguageRes.invalidParameter));
            return;
        }
        IUploadFile uploadFile = (IUploadFile) uploadFileObject;
        File file = UploadFileAction.getUploadFile(config, uploadFile.getFileName());
        if (file == null || !file.exists() || !file.isFile() || !file.canRead()) {
            json.put(Environment.MESSAGE, language.getLang(LanguageRes.notFindImage));
            return;
        }
        //读取图片end
        String fileType = FileUtil.getTypePart(file.getName());
        if (!ArrayUtil.inArray(fileTypes, fileType, true)) {
            json.put(Environment.MESSAGE, language.getLang(LanguageRes.notFindImageFormat));
            return;
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
                if (ImageUtil.thumbnail(new FileInputStream(file), new FileOutputStream(thumbnailFile), FileUtil.getTypePart(file), tWidth, tHeight)) {
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
                if (ImageUtil.thumbnail(new FileInputStream(file), new FileOutputStream(mobileFile), FileUtil.getTypePart(file), mobileWidth, mobileHeight)) {
                    json.put("mobile", 1);
                } else {
                    json.put("mobile", 0);
                }
            }

            json.put(Environment.SUCCESS, 1);
            json.put(Environment.infoType, Environment.promptInfo);
            json.put(Environment.MESSAGE, language.getLang(LanguageRes.editFinishClearTemp));
        } else {
            json.put(Environment.MESSAGE, language.getLang(LanguageRes.clearTempFileFailure));
        }
        //先备份历史文件 end
        list.clear();
    }

    @Override
    public String execute() throws Exception {
        if (isMethodInvoked()) {
            setResult(json);
            setActionResult(ROC);
            uploadFileDAO.evict(uploadFileDAO.getClassType());
        }
        put("namespace", uploadFileDAO.getNamespace());
        return super.execute();
    }

}