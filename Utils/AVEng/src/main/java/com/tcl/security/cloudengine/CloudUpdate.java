package com.tcl.security.cloudengine;


public class CloudUpdate {
    public static final String ENGINE_MCA = "ENGINE_MCA";
    public static final String ENGINE_AVIRA = "ENGINE_AVIRA";
    public static final String ENGINE_BD = "ENGINE_BD";
    public static final String ENGINE_AVL = "ENGINE_AVL";
    public static final String ENGINE_KPS = "ENGINE_KPS";
    public static final String ENGINE_AVAST = "ENGINE_AVAST";
    public static final String ENGINE_AVG = "ENGINE_AVG";

    public static final String SUBENGINE_LOCAL = "LOCAL";
    public static final String SUBENGINE_CLOUD = "CLOUD";

    CloudRequest.MetaInfo pkgInfo;
    String engine;
    String subengine;
    RecordInfo record;

    static final String pkgInfoTag = "pkgInfo";
    static final String engineTag = "engine";
    static final String subengineTag = "subengine";
    static final String recordTag = "record";

    public static class RecordInfo {
        int type;
        String name;
        String description;
        int level;
        String variant;

        static final String typeTag = "type";
        static final String nameTag = "name";
        static final String levelTag = "level";
        static final String variantTag = "variant";
        static final String descriptionTag = "description";

        public RecordInfo(int type, String name, String description, int level, String variant) {
            this.type = type;
            this.name = name;
            this.level = level;
            this.variant = variant;
            this.description = description;
        }
    }

    public CloudUpdate(CloudRequest.MetaInfo pkgInfo, String engine, String subengine, RecordInfo record) {
        this.pkgInfo = pkgInfo;
        this.engine = engine;
        this.subengine = subengine;
        this.record = record;
    }
}