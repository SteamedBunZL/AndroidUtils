package com.tcl.security.virusengine.entry;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;


/**
 * ScanInfo is the info of the scan result.
 *
 * Created by Steve on 2016/4/29.
 */
public class ScanInfo implements Parcelable{

    public ScanInfo(int fileType,String packageName, String virusName, String virusDescription, int state, String fileName, String type, int riskLevel, String suggest){
        this.fileType = fileType;
        this.packageName = packageName;
        this.virusDescription = virusDescription;
        this.virusName = virusName;
        this.state = state;
        if (!TextUtils.isEmpty(fileName))
            this.fileName = fileName;
        else
            this.fileName = "";
        if (TextUtils.isEmpty(type))
            this.riskType = "";
        else
            this.riskType = type;
        this.riskLevel = riskLevel;
        if (TextUtils.isEmpty((suggest)))
            this.suggest = "";
        else this.suggest = suggest;

    }

    public final int fileType;

    /**Application packagename*/
    public final String packageName;

    /**Virusname*/
    public final String virusName;

    /**Virusdescription*/
    public final String virusDescription;

    /**Virusstate {@link com.intel.security.vsm.ScanResult}*/
    public final int state;

    /**Appname return the app name or app packagename*/
    public final String fileName;

    public final String riskType;

    public final int riskLevel;

    public final String suggest;



    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(fileType);
        out.writeString(packageName);
        out.writeString(virusDescription);
        out.writeString(virusName);
        out.writeInt(state);
        out.writeString(fileName);
        out.writeString(riskType);
        out.writeInt(riskLevel);
        out.writeString(suggest);
    }

    public static final Creator<ScanInfo> CREATOR = new Creator<ScanInfo>(){

        @Override
        public ScanInfo createFromParcel(Parcel in) {
            return new ScanInfo(in);
        }

        @Override
        public ScanInfo[] newArray(int size) {
            return new ScanInfo[size];
        }
    };

    private ScanInfo(Parcel in){
        fileType = in.readInt();
        packageName = in.readString();
        virusDescription = in.readString();
        virusName = in.readString();
        state = in.readInt();
        fileName = in.readString();
        riskType = in.readString();
        riskLevel = in.readInt();
        suggest = in.readString();
    }

    @Override
    public String toString() {
        return "ScanInfo{" +
                "fileType=" + fileType +
                ", packageName='" + packageName + '\'' +
                ", virusName='" + virusName + '\'' +
                ", virusDescription='" + virusDescription + '\'' +
                ", state=" + state +
                ", fileName='" + fileName + '\'' +
                ", riskType='" + riskType + '\'' +
                ", riskLevel=" + riskLevel +
                ", suggest='" + suggest + '\'' +
                '}';
    }
}
