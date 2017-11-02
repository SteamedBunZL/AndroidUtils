package com.clean.spaceplus.cleansdk.junk.engine.junk;

import android.content.Context;

import com.clean.spaceplus.cleansdk.app.SpaceApplication;
import com.clean.spaceplus.cleansdk.R;

import java.util.ArrayList;
import java.util.List;

/**
 * @author dongdong.huang
 * @Description:
 * @date 2016/5/5 14:15
 * @copyright TCL-MIG
 */
public class JunkStandardAdviceProvider {
    private List<String> mPicturePkgNameList = null;
    private List<String> mMusicPkgNameList = null;
    private List<String> mVideoPkgNameList = null;
    private List<String> mDocumentPkgNameList = null;
    private List<String> mChatPkgNameList = null;
    private List<String> mBookPkgNameList = null;
    Context mContext = null;
    public JunkStandardAdviceProvider(){
        mContext = SpaceApplication.getInstance().getContext();
        initData();
    }

    public String getAdviceStr(String pkgName){
        if(mPicturePkgNameList.contains(pkgName)){
            return mContext.getString(R.string.junk_tag_junk_clean_advice_no_picture);
        }else if(mMusicPkgNameList.contains(pkgName)){
            return mContext.getString(R.string.junk_tag_junk_clean_advice_no_audio);
        }else if(mVideoPkgNameList.contains(pkgName)){
            return mContext.getString(R.string.junk_tag_junk_clean_advice_no_video);
        }else if(mDocumentPkgNameList.contains(pkgName)){
            return mContext.getString(R.string.junk_tag_junk_clean_advice_no_document);
        }else if(mChatPkgNameList.contains(pkgName)){
            return mContext.getString(R.string.junk_tag_junk_clean_advice_no_chatlog);
        }else if(mBookPkgNameList.contains(pkgName)){
            return mContext.getString(R.string.junk_tag_junk_clean_advice_no_book);
        }
        return null;
    }

    public String getAdviceStrContent(String pkgName){
        if(mPicturePkgNameList.contains(pkgName)){
            return mContext.getString(R.string.junk_tag_junk_clean_advice_no_picture_content);
        }else if(mMusicPkgNameList.contains(pkgName)){
            return mContext.getString(R.string.junk_tag_junk_clean_advice_no_audio_content);
        }else if(mVideoPkgNameList.contains(pkgName)){
            return mContext.getString(R.string.junk_tag_junk_clean_advice_no_video_content);
        }else if(mDocumentPkgNameList.contains(pkgName)){
            return mContext.getString(R.string.junk_tag_junk_clean_advice_no_document_content);
        }else if(mChatPkgNameList.contains(pkgName)){
            return mContext.getString(R.string.junk_tag_junk_clean_advice_no_chatlog_content);
        }else if(mBookPkgNameList.contains(pkgName)){
            return mContext.getString(R.string.junk_tag_junk_clean_advice_no_book_content);
        }
        return null;
    }

    private void initData(){
        mChatPkgNameList = new ArrayList<String>();
        mChatPkgNameList.add("com.whatsapp");
        mChatPkgNameList.add("com.twitter.android");
        mChatPkgNameList.add("com.tencent.mm");
        mChatPkgNameList.add("jp.naver.line.android");
        mChatPkgNameList.add("com.facebook.katana");
        mChatPkgNameList.add("com.viber.voip");
        mChatPkgNameList.add("flipboard.app");
        mChatPkgNameList.add("com.sgiggle.production");
        mChatPkgNameList.add("com.kakao.talk");
        mChatPkgNameList.add("com.tencent.mobileqq");
        mChatPkgNameList.add("com.kakao.story");
        mChatPkgNameList.add("com.joelapenna.foursquared");
        mChatPkgNameList.add("ru.ok.android");
        mChatPkgNameList.add("com.google.android.apps.plus");
        mChatPkgNameList.add("com.snapchat.android");
        mChatPkgNameList.add("com.sina.weibo");
        mChatPkgNameList.add("com.immomo.momo");
        mChatPkgNameList.add("com.xiaomi.channel");
        mChatPkgNameList.add("im.yixin");
        mChatPkgNameList.add("com.duowan.mobile");
        mChatPkgNameList.add("com.snda.youni");
        mChatPkgNameList.add("cn.com.fetion");
        mChatPkgNameList.add("com.tencent.WBlog");
        mChatPkgNameList.add("com.tencent.minihd.qq");

        mMusicPkgNameList = new ArrayList<String>();
        mMusicPkgNameList.add("com.sds.android.ttpod");
        mMusicPkgNameList.add("com.soundcloud.android");
        mMusicPkgNameList.add("com.iloen.melon");
        mMusicPkgNameList.add("com.clearchannel.iheartradio.controller");
        mMusicPkgNameList.add("mp3.zing.vn");
        mMusicPkgNameList.add("cn.kuwo.player");
        mMusicPkgNameList.add("com.pandora.android");
        mMusicPkgNameList.add("com.kugou.android");
        mMusicPkgNameList.add("com.slacker.radio");
        mMusicPkgNameList.add("ht.nct");
        mMusicPkgNameList.add("com.duomi.android");
        mMusicPkgNameList.add("com.changba");
        mMusicPkgNameList.add("com.shoujiduoduo.ringtone");
        mMusicPkgNameList.add("com.miui.player");
        mMusicPkgNameList.add("com.android.mediacenter");
        mMusicPkgNameList.add("com.ting.mp3.android");
        mMusicPkgNameList.add("com.tencent.qqmusic");
        mMusicPkgNameList.add("com.ximalaya.ting.android");
        mMusicPkgNameList.add("fm.xiami.main");
        mMusicPkgNameList.add("com.youba.ringtones");

        mVideoPkgNameList = new ArrayList<String>();
        mVideoPkgNameList.add("com.funshion.video.mobile");
        mVideoPkgNameList.add("tv.pps.mobile");
        mVideoPkgNameList.add("com.qvod.player");
        mVideoPkgNameList.add("org.videolan.vlc.betav7neon");
        mVideoPkgNameList.add("com.google.android.youtube");
        mVideoPkgNameList.add("com.clov4r.android.nil");
        mVideoPkgNameList.add("com.qianxun.yingshi2");
        mVideoPkgNameList.add("me.abitno.vplayer.t");
        mVideoPkgNameList.add("com.cgv.android.movieapp");
        mVideoPkgNameList.add("tv.pps.tpad");
        mVideoPkgNameList.add("com.megogo.application");
        mVideoPkgNameList.add("com.tencent.qqlive");
        mVideoPkgNameList.add("com.baidu.video");
        mVideoPkgNameList.add("com.storm.smart");
        mVideoPkgNameList.add("com.youku.phone");
        mVideoPkgNameList.add("com.sohu.sohuvideo");
        mVideoPkgNameList.add("com.pplive.androidphone");
        mVideoPkgNameList.add("com.qiyi.video");
        mVideoPkgNameList.add("com.funshion.video.mobile");

        mDocumentPkgNameList = new ArrayList<String>();
        mDocumentPkgNameList.add("cn.wps.moffice_eng");
        mDocumentPkgNameList.add("cn.wps.moffice_i18n");
        mDocumentPkgNameList.add("cn.wps.moffice");

        mPicturePkgNameList = new ArrayList<String>();
        mPicturePkgNameList.add("com.instagram.android");
        mPicturePkgNameList.add("com.picsart.studio");
        mPicturePkgNameList.add("com.sonyericsson.album");
        mPicturePkgNameList.add("cn.jingling.motu.photowonder");
        mPicturePkgNameList.add("com.mt.mtxx.mtxx");
        mPicturePkgNameList.add("com.cyworld.camera");
        mPicturePkgNameList.add("vStudio.Android.Camera360");
        mPicturePkgNameList.add("com.aviary.android.feather");
        mPicturePkgNameList.add("jp.naver.linecamera.android");
        mPicturePkgNameList.add("com.wantu.activity");
        mPicturePkgNameList.add("com.roidapp.photogrid");
        mPicturePkgNameList.add("com.iudesk.android.photo.editor");
        mPicturePkgNameList.add("com.yahoo.mobile.client.android.flickr");
        mPicturePkgNameList.add("com.kth.PuddingCamera");
        mPicturePkgNameList.add("com.niksoftware.snapseed");
        mPicturePkgNameList.add("com.instamag.activity");
        mPicturePkgNameList.add("ymst.android.fxcamera");
        mPicturePkgNameList.add("com.mobli");
        mPicturePkgNameList.add("cn.ibuka.hw.ui");
        mPicturePkgNameList.add("cn.ibuka.manga.ui");
        mPicturePkgNameList.add("com.meitu.meiyancamera");
        mPicturePkgNameList.add("com.miui.gallery");
        mPicturePkgNameList.add("com.mt.mttt");

        mBookPkgNameList = new ArrayList<String>();
        mBookPkgNameList.add("com.nhn.android.search");
        mBookPkgNameList.add("org.geometerplus.zlibrary.ui.android");
        mBookPkgNameList.add("com.chaozh.iReaderFree");
        mBookPkgNameList.add("com.duokan.reader");
        mBookPkgNameList.add("com.mybook66");
        mBookPkgNameList.add("com.anyview");
        mBookPkgNameList.add("com.netease.pris");
        mBookPkgNameList.add("com.ireadercity");
        mBookPkgNameList.add("com.kingreader.framework.google");
        mBookPkgNameList.add("com.baidu.yuedu");
        mBookPkgNameList.add("com.shuqi.controller");
        mBookPkgNameList.add("com.chaozh.iReaderFree15");
        mBookPkgNameList.add("com.nd.android.pandareader");
        mBookPkgNameList.add("cn.htjyb.reader");
        mBookPkgNameList.add("com.iBookStar.activity");
        mBookPkgNameList.add("cn.htjyb.reader");
    }
}