package com.sbai.finance.utils;

/**
 * 代码所属市场
 */
public class StockCodeUtil {
    /**
     * 深市：
     * 创业板的代码是300打头的股票代码。
     * 深市A股的代码是以000打头。
     * 中小板的代码是002打头。
     * 深圳B股的代码是以200打头。
     * 配股代码：深市以080打头。
     * 权证，深市是031打头。
     * <p>
     * 沪市：
     * 沪市A股的代码是以600、601或603打头。
     * 沪市B股的代码是以900打头。
     * 沪市新股申购的代码是以730打头。
     * 配股代码：沪市以700打头
     * 权证，沪市是580打头
     *
     * @param stockCode
     * @return
     */
    public static String getExchangeType(String stockCode) {
        String result = "";
        switch (stockCode.substring(0, 3)) {
            //深市
            case "000":
            case "002":
            case "031":
            case "080":
            case "200":
            case "300":
                result = "SZ";
                break;
            //沪市
            case "580":
            case "600":
            case "601":
            case "603":
            case "700":
            case "730":
            case "900":
                result = "SH";
                break;
            default:
                break;

        }
        return result;
    }
}
