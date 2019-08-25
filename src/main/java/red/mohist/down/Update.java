package red.mohist.down;

import red.mohist.i18n.Message;
import red.mohist.util.FileUtil;
import red.mohist.util.IOUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class Update {


    public static void hasLatestVersion() {
        String str = "https://api.github.com/repos/PFCraft/Mohist/commits";
        try {
            System.out.println(Message.getString("update.check"));
            URL url = new URL(str);
            URLConnection conn = url.openConnection();
            InputStream is = conn.getInputStream();

            String commits = IOUtil.readContent(is, "UTF-8");
            String sha = "\"sha\":\"";
            String date = "\"date\":\"";

            String s0 = commits.substring(commits.indexOf(sha));
            String s1 = s0.substring(s0.indexOf(sha) + 7);
            String s2 = s1.substring(0, 7);

            String oldver = Update.class.getPackage().getImplementationVersion();
            String time = commits.substring(commits.indexOf(date));
            String time1 = time.substring(time.indexOf(date) + 8);
            String time2 = time1.substring(0, 20);
            if (oldver.contains(s2)) {
                System.out.println(Message.getFormatString("update.latest", new Object[]{s2,oldver}));
            } else {
                System.out.println(Message.getFormatString("update.old", new Object[]{s2,time2,oldver}));
            }
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean isCheckVersion() {
        try {
            File f = new File("mohist.yml");
            String s = FileUtil.readContent(f, "UTF-8");
            if(s.contains("check_update: ")){
                String string = s.substring(s.indexOf("check_update: "));
                String s1 = string.substring(string.indexOf(":") + 1);
                String s2 = s1.substring(1, 5);
                String locale = s2.substring(0, 4);
                if (locale.equals("true")){
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}
