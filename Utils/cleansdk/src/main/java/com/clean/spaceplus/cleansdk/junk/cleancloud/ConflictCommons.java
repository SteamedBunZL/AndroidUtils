package com.clean.spaceplus.cleansdk.junk.cleancloud;

public class ConflictCommons {
	
	public static final int PRODUCT_ID_CN 		= 1;	///< 国内版
	public static final int PRODUCT_ID_OU 		= 2;	///< 国际版
	public static final int PRODUCT_ID_CN_PAD 	= 3;	///< 国内Pad版
	public static final int PRODUCT_ID_OU_PAD 	= 4;	///< 国际Pad版
	
	/* BUILD_CTRL:IF:CN_VERSION_ONLY */
	public static final int PRODUCT_ID = PRODUCT_ID_CN;
	/* BUILD_CTRL:ENDIF:CN_VERSION_ONLY */
	/* BUILD_CTRL:IF:OU_VERSION_ONLY
	public static final int PRODUCT_ID = PRODUCT_ID_OU;
	BUILD_CTRL:ENDIF:OU_VERSION_ONLY */
	
    /* BUILD_CTRL:IF:CN_PAD_VERSION_ONLY
	public static final int PRODUCT_ID = PRODUCT_ID_CN_PAD;
    BUILD_CTRL:ENDIF:CN_PAD_VERSION_ONLY */
	
    /* BUILD_CTRL:IF:OU_PAD_VERSION_ONLY
	public static final int PRODUCT_ID = PRODUCT_ID_OU_PAD;
    BUILD_CTRL:ENDIF:OU_PAD_VERSION_ONLY */
	
	/**
	 * 只区分国内 和 国际， 不区分平台
	 * @return
	 */
	public static boolean isCNVersion(){
		return PRODUCT_ID == PRODUCT_ID_CN || PRODUCT_ID == PRODUCT_ID_CN_PAD;
	}
	
	/**
	 * 是否是pad版本，不区分国内和国际
	 * @return
	 */
	public static boolean isPadVersion(){
		return PRODUCT_ID == PRODUCT_ID_OU_PAD || PRODUCT_ID == PRODUCT_ID_CN_PAD;
	}
	
//   public static boolean isPhoneCNVersion(){
//        return PRODUCT_ID == PRODUCT_ID_CN;
//    }
	
//	public static boolean isPhoneOUVersion(){
//		return PRODUCT_ID == PRODUCT_ID_OU;
//	}
	
//	public static boolean isPadCNVersion(){
//		return PRODUCT_ID == PRODUCT_ID_CN_PAD;
//	}
	
//	public static boolean isPadOUVersion(){
//		return PRODUCT_ID == PRODUCT_ID_OU_PAD;
//	}

	public static String getWidgetPkgName() {
		switch (PRODUCT_ID) {
		case PRODUCT_ID_CN:
			return "com.cleanmaster.mguard_cn";
			
		case PRODUCT_ID_OU:
			return  "com.cleanmaster.mguard";
			
		case PRODUCT_ID_CN_PAD:
			return "com.cleanmaster.mguard_cn.pad.hd";
			
		case PRODUCT_ID_OU_PAD:
			return "com.cleanmaster.mguard.pad.hd";
			
		default:
			break;
		}
		
		return null;
	}
	
	public static String getCrashKey(){
       switch (PRODUCT_ID) {
           case PRODUCT_ID_CN:
               return "2097153";
               
           case PRODUCT_ID_OU:
               return  "2097152";
               
           case PRODUCT_ID_CN_PAD:
               return "2097169";
               
           case PRODUCT_ID_OU_PAD:
               return "2097168";
           default:
               return null;
           }
	}
	
	public static String getPostCrashLogUrl() {
       switch (PRODUCT_ID) {
           case PRODUCT_ID_CN:
               return "http://cmdump.upload.duba.net/dump_cn.php";
               
           case PRODUCT_ID_OU:
               return  "http://cmdump.upload.duba.net/dump.php";
               
           case PRODUCT_ID_CN_PAD:
               return "http://cmdump.upload.duba.net/paddump_cn.php";
               
           case PRODUCT_ID_OU_PAD:
               return "http://cmdump.upload.duba.net/paddump.php";
           default:
               return null;
       }
	}
	
	public static String getPostMiniDumpUrl() {
       switch (PRODUCT_ID) {
           case PRODUCT_ID_CN:
               return "http://cmdump.upload.duba.net/mdump_cn.php";
               
           case PRODUCT_ID_OU:
               return  "http://cmdump.upload.duba.net/mdump.php";
               
           case PRODUCT_ID_CN_PAD:
               return "http://cmdump.upload.duba.net/mdump_cn.php";
               
           case PRODUCT_ID_OU_PAD:
               return "http://cmdump.upload.duba.net/mdump.php";
           default:
               return null;
       }
	}
	
    public static String getPostANRDumpUrl() {
        switch (PRODUCT_ID) {
            case PRODUCT_ID_CN:
                return "http://cmdump.upload.duba.net/anrdump_cn.php";
                
            case PRODUCT_ID_OU:
                return  "http://cmdump.upload.duba.net/anrdump.php";
                
            case PRODUCT_ID_CN_PAD:
                return "http://cmdump.upload.duba.net/anrdump_cn.php";
                
            case PRODUCT_ID_OU_PAD:
                return "http://cmdump.upload.duba.net/anrdump.php";
            default:
                return null;
        }
     }
	
	public static int getPcCommonPortNum() {
        switch (PRODUCT_ID) {
            case PRODUCT_ID_CN:
                return 15697;
                
            case PRODUCT_ID_OU:
                return 15897;
                
            case PRODUCT_ID_CN_PAD:
                return 16097;
                
            case PRODUCT_ID_OU_PAD:
                return 16297;
            default:
                return 15697;
        }
	}
	
    public static String getRootName() {
        return isCNVersion() ? "ijinshan_cleanmaster_cn_rtsrv" : "ijinshan_cleanmaster_rtsrv";
    }
    
    /**
     * 对于类名，如Provider、authorities使用次后缀
     * @return
     */
    public static String getSuffix() {
    	return (ConflictCommons.isPadVersion() ? "_pad" : "") + (ConflictCommons.isCNVersion() ? "_cn" : "");
    }
    
//    public static String getPackageNameSuffix() {
//        return (ConflictCommons.isCNVersion() ? "_cn" : "") + (ConflictCommons.isPadVersion() ? ".pad.hd" : "");
//    }
    
    public static String getPackageNameSuffixRevertVersion() {
        return ((!ConflictCommons.isCNVersion()) ? "_cn" : "") + (ConflictCommons.isPadVersion() ? ".pad.hd" : "");
    }

	public static String getPostAppAnrLogUrl() {
		switch (PRODUCT_ID) {
		case PRODUCT_ID_CN:
			return "http://cmdump.upload.duba.net/common_dump.php?app_name=cmappanr&lang=cn&type=anr";

		case PRODUCT_ID_OU:
			return "http://cmdump.upload.duba.net/common_dump.php?app_name=cmappanr&lang=en&type=anr";

		case PRODUCT_ID_CN_PAD:
			return "http://cmdump.upload.duba.net/common_dump.php?app_name=cmappanr&lang=cn&type=anr";

		case PRODUCT_ID_OU_PAD:
			return "http://cmdump.upload.duba.net/common_dump.php?app_name=cmappanr&lang=en&type=anr";
		default:
			return null;
		}
	}
    
}
