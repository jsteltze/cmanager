package cmanager.oc;

import cmanager.geo.Geocache;
import cmanager.global.Constants;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ShadowList {

    private static final String SHADOWLIST_FOLDER = Constants.CACHE_FOLDER + "OC.shadowlist";
    private static final String SHADOWLIST_PATH = SHADOWLIST_FOLDER + "/gc2oc.gz";
    private static final String SHADOWLIST_POSTED_FOLDER =
            Constants.CACHE_FOLDER + "OC.shadowlist.posted";

    public static void updateShadowList() throws IOException {
        // TODO: Enable once the API is working again.
        // Delete list if it is older than 1 month.
        /*final File file = new File(SHADOWLIST_PATH);
        if (file.exists()) {
            DateTime fileTime = new DateTime(file.lastModified());
            final DateTime now = new DateTime();
            fileTime = fileTime.plusMonths(1);
            if (fileTime.isAfter(now)) {
                return;
            }

            if (!file.delete()) {
                System.out.println("Error deleting file " + SHADOWLIST_PATH + ".");
            }
        }

        final boolean success = new File(SHADOWLIST_FOLDER).mkdirs();
        if (!success) {
            System.out.println("Error creating directory " + SHADOWLIST_FOLDER + ".");
        }

        // download list
        final URL url = new URL("https://www.opencaching.de/api/gc2oc.php");
        ReadableByteChannel readableByteChannel = Channels.newChannel(url.openStream());
        FileOutputStream fileOutputStream = new FileOutputStream(SHADOWLIST_PATH);
        fileOutputStream.getChannel().transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
        fileOutputStream.close();*/
    }

    public static ShadowList loadShadowList() throws Throwable {
        final HashMap<String, String> shadowList = new HashMap<>();

        System.out.println(
                "The shadow list retrieval has been disabled temporarily as the API endpoint seems to be broken for now.");
        // TODO: Enable after the GZip archive is valid again.
        /*FileHelper.processFiles(SHADOWLIST_PATH, new FileHelper.InputAction() {
            @Override
            public void process(InputStream inputStream) throws Throwable
            {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    final String[] token = line.split(",");
                    // Column 2 == "1" means verified by a human
                    if (token[2].equals("1")) {
                        // <GC, OC>
                        shadowList.put(token[0], token[1]);
                    }
                }
            }
        });*/

        return new ShadowList(shadowList);
    }

    private final Map<String, String> shadowList;

    private ShadowList(Map<String, String> shadowList) {
        this.shadowList = shadowList;
    }

    public String getMatchingOcCode(String gcCode) {
        return shadowList.get(gcCode);
    }

    public boolean contains(String gcCode) {
        return shadowList.get(gcCode) != null;
    }

    public void postToShadowList(Geocache gc, Geocache oc) throws Exception {
        // TODO: Enable once the API is working again.
        // Do not repost items which are already upstream.
        /*if (contains(gc.getCode())) {
            return;
        }

        // do not repost local findings.
        final File file = new File(SHADOWLIST_POSTED_FOLDER + "/" + gc.getCode());
        if (file.exists()) {
            return;
        }

        final String url =
                "https://www.opencaching.de/api/gc2oc.php"
                        + "?report=1"
                        + "&ocwp="
                        + oc.getCode()
                        + "&gcwp="
                        + gc.getCode()
                        + "&source="
                        + Constants.APP_NAME
                        + "+"
                        + Version.VERSION;

        // Post.
        HTTP.get(url);

        // Remember our post.
        new File(SHADOWLIST_POSTED_FOLDER).mkdirs();
        file.createNewFile();*/
    }
}
