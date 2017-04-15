package gaia.backend;

/**
 * Created by jf2lin on 2017-02-14.
 */

public class AnalyzeIngredientsHealthCanada implements AnalyzeIngredientsAbstract {
    // static String sourceUrl = "http://webprod.hc-sc.gc.ca/nhpid-bdipsn/ingredsReq.do?srchRchTxt=%s&srchRchRole=-1&mthd=Search&lang=eng";
    static String sourceUrl = ""
    public static String generateUrl(String searchString) {
        return sourceUrl.replaceAll("%s", searchString);
    }

    public static loadUrlAndAnalyze(String searchString) {

    }
}
