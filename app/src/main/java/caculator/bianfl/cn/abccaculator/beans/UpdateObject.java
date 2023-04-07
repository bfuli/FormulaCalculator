package caculator.bianfl.cn.abccaculator.beans;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;

/**
 * Created by 福利 on 2016/9/29.
 */
public class UpdateObject extends BmobObject {
    private int versionCoad;
    private String updateMessage;
    private boolean mustUpdate;
    private boolean isDebug;
    private String downUrl;
    private String versionName;

    public boolean isDebug() {
        return isDebug;
    }

    public void setDebug(boolean debug) {
        isDebug = debug;
    }

    private long fileSize;
    private BmobFile file;

    public void setFile(BmobFile file) {
        this.file = file;
    }

    public void setDownUrl(String downUrl) {
        this.downUrl = downUrl;
    }

    public void setUpdateMessage(String updateMessage) {
        this.updateMessage = updateMessage;
    }

    public void setVersionCoad(int versionCoad) {
        this.versionCoad = versionCoad;
    }

    public void setMustUpdate(boolean mustUpdate) {
        this.mustUpdate = mustUpdate;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public String getVersionName() {
        return versionName;
    }

    public boolean isMustUpdate() {
        return mustUpdate;
    }

    public int getVersionCoad() {
        return versionCoad;
    }

    public String getDownUrl() {
        return downUrl;
    }
    public BmobFile getFile() {
        return file;
    }
    public String getUpdateMessage() {
        return updateMessage;
    }
    public long getFileSize(){
        return fileSize;
    }
}
