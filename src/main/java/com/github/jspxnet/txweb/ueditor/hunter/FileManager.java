package com.github.jspxnet.txweb.ueditor.hunter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.github.jspxnet.utils.ArrayUtil;
import com.github.jspxnet.utils.FileSuffixUtil;
import com.github.jspxnet.utils.FileUtil;
import com.github.jspxnet.txweb.ueditor.define.AppInfo;
import com.github.jspxnet.txweb.ueditor.define.BaseState;
import com.github.jspxnet.txweb.ueditor.define.MultiState;
import com.github.jspxnet.txweb.ueditor.define.State;
import com.github.jspxnet.utils.ObjectUtil;

public class FileManager {

    private String saveDirectory = null;
    private String setupPath = null;
    private String[] allowFiles = null;
    private int count = 0;


    public FileManager(String saveDirectory, String setupPath, String[] allowFiles, int count) {

        this.saveDirectory = saveDirectory;
        this.setupPath = setupPath;
        this.allowFiles = allowFiles;
        this.count = count;

    }

    public State listFile(int index) {

        File dir = new File(saveDirectory);
        State state = null;

        if (!dir.exists()) {
            return new BaseState(false, AppInfo.NOT_EXIST);
        }

        if (!dir.isDirectory()) {
            return new BaseState(false, AppInfo.NOT_DIRECTORY);
        }

        List<File> list = getFileList(dir, this.allowFiles, true);
        if (index < 0 || index > list.size()) {
            state = new MultiState(true);
        } else {
            File[] fileList = null;
            for (int i = index; i < (index + this.count) && i < list.size(); i++) {
                fileList = FileUtil.append(fileList, list.get(i));
            }
            state = this.getState(fileList);
        }
        state.putInfo("start", index);
        state.putInfo("total", list.size());
        return state;

    }

    private State getState(File[] files) {
        MultiState state = new MultiState(true);
        BaseState fileState = null;
        if (!ObjectUtil.isEmpty(files))
        {
            for (File file : files) {
                if (file == null) {
                    break;
                }
                fileState = new BaseState(true);
                fileState.putInfo("url", FileUtil.mendFile("/" + FileUtil.getDecrease(setupPath, file.getPath())));
                fileState.putInfo("fileName", file.getName());
                fileState.putInfo("title", FileUtil.getNamePart(file.getName()));
                state.addState(fileState);
            }
        }
        return state;
    }

    static public List<File> getFileList(File dir, String[] types, boolean chid) {
        List<File> result = new ArrayList<>();
        if (!dir.exists()) {
            return new ArrayList<>(0);
        }
        File[] fileList = dir.listFiles();
        int I;
        if (fileList != null) {
            for (I = 0; I < fileList.length; I++) {
                if (fileList[I].isFile() && ArrayUtil.inArray(types, FileUtil.getTypePart(fileList[I].getName()), true)) {
                    //排除_m图片
                    String fileName = FileUtil.getNamePart(fileList[I].getName());
                    String fileType = FileUtil.getTypePart(fileList[I].getName());
                    if (FileSuffixUtil.isImageSuffix(fileType) && (fileName.endsWith("_m") || fileName.endsWith("_s"))) {
                        continue;
                    }
                    result.add(fileList[I]);
                } else if (fileList[I].isDirectory() && chid) {
                    result.addAll(getFileList(fileList[I], types, chid));
                }
            }
        }
        return result;
    }
}
