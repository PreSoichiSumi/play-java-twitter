package util;

import aacj.config.CharManager;
import aacj.config.ConfigManager;
import aacj.model.CharTable;
import aacj.model.PixelTable;
import aacj.model.Size;
import aacj.util.AAConvTask;
import aacj.util.ImageUtil;
import play.data.DynamicForm;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by s-sumi on 2016/03/25.
 */
public class ConvertionUtil {
    public static String aaConvertion(File file, DynamicForm form) {
        BufferedImage bi;
        try {
            bi = ImageIO.read(file);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
        ConfigManager cm = generateConfigManager(form);
        CharManager charm = new CharManager(cm);
        PixelTable tmp = img2LineImg(bi, cm);
        return lineImg2AA(tmp.data, tmp.width, tmp.height, charm, cm)[0];
    }

    public static ConfigManager generateConfigManager(DynamicForm form) {
        final int INDEX = 6;
        Map<String, String> tmp = new HashMap<>();
        if (form.get("sel1").equals("NORESIZE")) {
            tmp.put("sizeType", "0");
            tmp.put("sizeImage_w", "0");
            tmp.put("sizeImage_h", "0");
        } else {
            String num = form.get("sel1").substring(INDEX);//substring->NOTNULL

            if (num.equals(""))
                throw new RuntimeException();
            tmp.put("sizeType", "1");

            String w, h;
            switch (num) {
                case "1":
                    w = "640";
                    h = "480";
                    break;
                case "2":
                    w = "800";
                    h = "600";
                    break;
                case "3":
                    w = "960";
                    h = "640";
                    break;
                case "4":
                    w = "1024";
                    h = "768";
                    break;
                case "5":
                    w = "1280";
                    h = "960";
                    break;
                case "6":
                    w = "1920";
                    h = "1080";
                    break;
                default:
                    throw new RuntimeException("switch-default");
            }
            tmp.put("sizeImage_w", w);
            tmp.put("sizeImage_h", h);
        }
        tmp.put("accuracy", form.get("slider"));
        tmp.put("lapRange", form.get("sel2"));
        tmp.put("noiseLen", "20");
        tmp.put("connectRange", "1");
        tmp.put("fontName", "MS Gothic");
        tmp.put("fontSize", "9");
        tmp.put("pitch", "0");
        tmp.put("match", "2");
        tmp.put("score1", "80");
        tmp.put("score2", "100");
        tmp.put("multi", "true");
        tmp.put("matchCnt", "1");
        tmp.put("charSet", "2");
        if (form.get("tone") != null) {
            tmp.put("tone", "true");
        } else {
            tmp.put("tone", "false");
        }
        tmp.put("reversal", "false");
        tmp.put("toneValue", "220");
        tmp.put("toneTxt", ":＠: ＠:  ＠. ");
        tmp.put("textColor", "0,0,0");
        tmp.put("canvasColor", "255,255,255");
        tmp.put("angle", "2");
        tmp.put("useNotDir", "true");
        tmp.put("score3", "80");
        tmp.put("score4", "100");

        ConfigManager cm = new ConfigManager();
        cm.setConfig(tmp);


        //http://stackoverflow.com/questions/228477/how-do-i-programmatically
        // -determine-operating-system-in-java

        //http://www.mltlab.com/wp/archives/67
        String OS_NAME = System.getProperty("os.name").toLowerCase();
        if (OS_NAME.startsWith("windows")) {
            if (form.get("font").equals("monospaced")) {
                cm.fontName = "MS Gothic";
                //cm.fontName="VL Gothic Regular";
            } else {
                cm.fontName = "MS PGothic";
            }
        } else if (OS_NAME.startsWith("linux")) {
            if (form.get("font").equals("monospaced")) {
                //cm.fontName="Monospaced";
                cm.fontName = "VL Gothic Regular";
            } else {
                cm.fontName = "SansSerif";
            }
        } else {
            cm.fontName = "Arial";
        }


        cm.fontSize = 9;
        return cm;
    }

    public static PixelTable img2LineImg(BufferedImage img, ConfigManager cm) {
        int w = img.getWidth();
        int h = img.getHeight();
        if (cm.sizeType != ConfigManager.SizeType.NoResize) {
            w = cm.sizeImage.width;
            h = cm.sizeImage.height;
        }
        PixelTable rawImg = PixelTable.createFromBufferedImage(img);
        PixelTable zoomedImg = ImageUtil.zoomImage(rawImg, new Size(w, h), false);
        PixelTable lineImg = ImageUtil.getLineImage(zoomedImg,
                cm.accuracy, cm.lapRange, cm.noiseLen, cm.connectRange, cm);
        return ImageUtil.zoomImage(lineImg, new Size(w, h), true);
    }

    public static String[] lineImg2AA(int[] bmp, int width, int height, CharManager charm, ConfigManager cm) {
        CharTable table = getTable(bmp, width, height);  //2値化画像取得？
        CharTable toneTable = getToneTable(bmp, width, height, cm);    //トーン用

        int h = charm.lstData.get(0).height;
        int w = width;

        List<CharTable> lstSplitTable = new ArrayList<>();//並列処理のため分割　
        List<CharTable> lstSplitToneTable = new ArrayList<>();

        for (int top = 0; top < height; top += h)   //C#のgetlength()の引数は0スタート
        {
            lstSplitTable.add(trimTable(table, top, 0, h, w, 0));

            if (cm.tone && cm.toneTxt.length > 0) {
                lstSplitToneTable.add(trimTable(toneTable, top, 0, h, w, 0));
            } else {
                lstSplitToneTable.add(new CharTable(0, 0));
            }
        }

        StringBuilder sbAA = new StringBuilder();
        StringBuilder sbAAType = new StringBuilder();

        //スレッド処理　//プロセッサ数だけワークを並列処理 //webではスレッドプールも必要

        final ExecutorService pool;
        if (cm.multi) {
            pool = Executors.newCachedThreadPool();
        } else {
            pool = Executors.newSingleThreadExecutor();
        }
        try {
            List<Future<AAConvTask.Result>> futures = new ArrayList<>();
            for (int i = 0; i < lstSplitTable.size(); i++) {
                Future<AAConvTask.Result> future = pool.submit(new AAConvTask(cm, charm, lstSplitTable.get(i), lstSplitToneTable.get(i)));
                futures.add(future);
            }
            while (true) {
                long completed = futures.stream().filter(Future::isDone).count();

                if (completed == futures.size()) {
                    break;
                } else {
                    Thread.sleep(10);
                }
            }
            for (Future<AAConvTask.Result> future : futures) {
                AAConvTask.Result result = future.get();
                sbAA.append(result.aa);
                sbAA.append("\r\n");
                sbAAType.append(result.aatype);
                sbAAType.append("\r\n");
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        } finally {
            pool.shutdown();
        }

        return new String[]{sbAA.toString(), sbAAType.toString()};
    }

    private static CharTable trimTable(CharTable table, int top, int left, int height, int width, int margin) {
        //上下左右に1ピクセルの余白が必要なので+2
        CharTable subTable = new CharTable(width + margin * 2, height + margin * 2);
        for (int y = 0; y < subTable.height; y++) {
            for (int x = 0; x < subTable.width; x++) {
                if (y < margin || y > subTable.height - margin - 1 || x < margin || x > subTable.width - margin - 1 ||
                        y - margin + top >= table.height || x - margin + left >= table.width) {
                    subTable.set(x, y, '□');
                } else {
                    subTable.set(x, y, table.get(x - margin + left, y - margin + top));
                }
            }
        }
        return subTable;
    }

    private static CharTable getTable(int[] bmp, int width, int height) {
        CharTable table = new CharTable(width, height);
        for (int y = 0; y < table.height; y++) {
            for (int x = 0; x < table.width; x++) {
                if ((bmp[x + width * y] & 0xff) == 0) {
                    table.set(x, y, '■');
                } else {
                    table.set(x, y, '□');
                }
            }
        }
        return table;
    }

    private static CharTable getToneTable(int[] bmp, int width, int height, ConfigManager cm) {
        CharTable table = new CharTable(width, height);
        int val = 200 / cm.toneTxt.length;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int p = bmp[x + y * width] & 0xff;
                if (p == 0 || p == 255) {
                    table.set(x, y, ' ');
                } else {
                    table.set(x, y, Integer.toString(p / val - 1).charAt(0));
                }
            }
        }
        return table;
    }

    public static ConfigManager generateConfigManager() {
        final int INDEX = 6;
        Map<String, String> tmp = new HashMap<>();
        tmp.put("sizeType", "0");
        tmp.put("sizeImage_w", "0");
        tmp.put("sizeImage_h", "0");
        tmp.put("accuracy", "50");
        tmp.put("lapRange", "9");
        tmp.put("noiseLen", "20");
        tmp.put("connectRange", "1");
        tmp.put("fontName", "MS Gothic");
        tmp.put("fontSize", "9");
        tmp.put("pitch", "0");
        tmp.put("match", "2");
        tmp.put("score1", "80");
        tmp.put("score2", "100");
        tmp.put("multi", "true");
        tmp.put("matchCnt", "1");
        tmp.put("charSet", "2");
        tmp.put("tone", "false");
        tmp.put("reversal", "false");
        tmp.put("toneValue", "220");
        tmp.put("toneTxt", ":＠: ＠:  ＠. ");
        tmp.put("textColor", "0,0,0");
        tmp.put("canvasColor", "255,255,255");
        tmp.put("angle", "2");
        tmp.put("useNotDir", "true");
        tmp.put("score3", "80");
        tmp.put("score4", "100");

        ConfigManager cm = new ConfigManager();
        cm.setConfig(tmp);
        cm.fontName = "Monospaced";
        cm.fontSize = 9;
        return cm;
    }

}
