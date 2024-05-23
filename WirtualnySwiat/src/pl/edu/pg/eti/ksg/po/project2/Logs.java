package pl.edu.pg.eti.ksg.po.project2;

public class Logs {
    private static String content = "";

    public static void DodajKomentarz(String log) {
        content += log + "\n";
    }

    public static String getContent() {
        return content;
    }

    public static void WyczyscKomentarzy() {
        content = "";
    }
}
