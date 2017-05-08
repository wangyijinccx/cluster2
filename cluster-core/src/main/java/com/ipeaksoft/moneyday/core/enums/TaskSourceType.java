package com.ipeaksoft.moneyday.core.enums;

public enum TaskSourceType {
    SELF("自有"),
	RUANLIE("软猎"),
	XINGZHETIANXIA("行者天下"),
	YOUMIN("上海游民网络有限公司"), 
	ZHIMENG("北京众橙世纪传媒技术有限公司|指盟iOS平台"),
	GUANGZHOUZHIAO("广州指奥信息科技有限公司"),
    PAIRUIWEIXING("北京派瑞威行广告有限公司"),
    KEJINXINXI("氪金信息科技（天津）有限公司"),
    PEIKE("深圳市培科网络科技有限公司"),
    TIANTIANLEXUN("北京天天乐讯科技有限公司"),
    YHSHENMO("广州银汉科技有限公司"),
    YOUMI("淮安有米信息科技有限公司"), 
    HUAI("北京互爱科技有限公司"), 
    YOULEHUO("北京友乐活网络科技有限公司"),
    MOPAN("北京磨盘时代科技有限公司"),
    MEX("上海摩邑诚广告有限公司"), 
    DKE("北京点客网络科技有限公司"), 
    YOUGUU("北京金汇盛世移动科技有限公司"),
    QINCHENGHUDONG("北京勤诚互动广告有限公司"), 
    HUDONG("北京掌上互动科技有限公司"), 
    GAIT("广州巨途信息技术有限公司"),
    TUAN800("北京团博百众科技有限公司"), 
    ALLDK("北京点开科技有限公司"),
    SHUNRUI("广州瞬锐网络科技有限公司"),
    NUOMI("北京百度网讯科技有限公司"),
    KUAIYOU("北京快友世纪科技有限公司"),
    TIANYOU("深圳天游网络科技有限公司"),
    DIANLE("北京无限点乐科技有限公司"),
    Gamesky("成都吉乾科技有限公司"),
    GUANGMANG("北京光芒星空信息技术有限公司"),
//    FUTUREINST("北京嗨未来网络科技有限公司"),
    XUSHENG("江苏旭升网络科技有限公司"),
    DIANGAO("杭州点告网络技术有限公司"),
    XINGLIAN("北京星联时空科技有限公司"),
    LANGYI("朗亿广告投放"),
    QIANMAMA("钱妈妈理财"),
    QIANZHUANGLICAI("钱庄理财"),
    SANWEIDU("三维度"),
    WLYJS("未来研究所"),
    BAIHE("百合"),
    JIAWO("加沃"),
    QIZHUAN("7赚"),
    HUIZHI("汇智纵横"),
    MIDIG("米迪广告主"),
    QICAI("七彩"),
    PLAY("play800"),
    DIANRU("点入"),
    JUDIAN("聚点"),
    BATMOBI("BatMobi"),
    Mobvista("Mobvista"),
    YeahMOBI("YeahMobi"),
    CHUANGQIG("闯齐广告主"),
    LANMAOG("懒猫广告主"),
    Avazu("Avazu"),
    YOUMIT("YOUMIT"),
    TTGQJ("TTGQJ"),
    Appcoach("Appcoach"),
    WanPu("WanPu"),
    REYUN("热云"),
    TalkingData("TalkingData"),
    DIANZHIKJ("DIANZHIKJ"),
    CHANDASHI("CHANDASHI"),
    ZAKER("ZAKER"),
    WABANG("WABANG"),
    WIFIGUANJIA("腾讯Wifi管家"),
    ;

    private final String key;

    private TaskSourceType(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}