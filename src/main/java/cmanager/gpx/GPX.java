package cmanager.gpx;

import cmanager.FileHelper;
import cmanager.geo.Coordinate;
import cmanager.geo.Geocache;
import cmanager.geo.GeocacheAttribute;
import cmanager.geo.GeocacheLog;
import cmanager.geo.Waypoint;
import cmanager.global.Constants;
import cmanager.global.Version;
import cmanager.gui.ExceptionPanel;
import cmanager.xml.Element;
import cmanager.xml.Element.XmlAttribute;
import cmanager.xml.Parser;
import cmanager.xml.Parser.XMLParserCallbackI;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

public class GPX {

    public static void loadFromStream(
            InputStream inputStream, final List<Geocache> geocaches, final List<Waypoint> waypoints)
            throws Throwable {
        final ExecutorService service =
                Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 2);

        Parser.parse(
                inputStream,
                new XMLParserCallbackI() {
                    public boolean elementFinished(final Element element) {
                        if (!element.is("wpt")) {
                            return false;
                        }

                        service.submit(
                                new Runnable() {
                                    public void run() {
                                        Geocache geocache = null;
                                        Waypoint waypoint = null;

                                        try {
                                            waypoint = toWaypoint(element);
                                            geocache = toCache(element);
                                        } catch (NullPointerException exception) {
                                            ExceptionPanel.display(exception);
                                        }

                                        if (geocache != null) {
                                            synchronized (geocaches) {
                                                geocaches.add(geocache);
                                            }
                                        } else if (waypoint != null) {
                                            synchronized (waypoints) {
                                                waypoints.add(waypoint);
                                            }
                                        }
                                    }
                                });
                        return true;
                    }

                    public boolean elementLocatedCorrectly(Element element, Element parent) {
                        if (element.is("gpx")) {
                            return parent.getName() == null;
                        }
                        if (element.is("wpt")) {
                            return parent.is("gpx");
                        }

                        return true;
                    }
                });

        service.shutdown();
        // Incredible high delay but still ugly.
        service.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
    }

    private static Waypoint toWaypoint(Element waypointElement) {
        Coordinate coordinate;
        String code = null;
        String description = null;
        String symbol = null;
        String type = null;
        String parent = null;
        String date = null;

        double latitude = 0.0;
        double longitude = 0.0;
        for (final XmlAttribute attribute : waypointElement.getAttributes()) {
            if (attribute.is("lat")) {
                latitude = attribute.getValueDouble();
            } else if (attribute.is("lon")) {
                longitude = attribute.getValueDouble();
            }
        }

        coordinate = new Coordinate(latitude, longitude);

        for (final Element element : waypointElement.getChildren()) {
            if (element.is("name")) {
                code = element.getUnescapedBody();
            } else if (element.is("desc")) {
                description = element.getUnescapedBody();
            } else if (element.is("sym")) {
                symbol = element.getUnescapedBody();
            } else if (element.is("type")) {
                type = element.getUnescapedBody();
            } else if (element.is("time")) {
                date = element.getUnescapedBody();
            } else if (element.is("gsak:wptExtension")) {
                for (final Element extensionElement : element.getChildren()) {
                    if (extensionElement.is("gsak:Parent")) {
                        parent = extensionElement.getUnescapedBody();
                    }
                }
            }
        }

        final Waypoint waypoint = new Waypoint(coordinate, code, description, symbol, type, parent);
        waypoint.setDate(date);
        return waypoint;
    }

    private static Geocache toCache(Element waypointElement) {
        String code = null;
        String urlName = null;
        String cacheName = null;
        Coordinate coordinate;
        Double difficulty = null;
        Double terrain = null;
        String type = null;
        String owner = null;
        String container = null;
        String listing = null;
        String listingShort = null;
        String hint = null;
        Integer id = null;
        Boolean archived = null;
        Boolean available = null;
        Boolean gcPremium = null;
        Integer favoritePoints = null;

        final List<GeocacheAttribute> attributes = new ArrayList<>();
        final List<GeocacheLog> logs = new ArrayList<>();

        double latitude = 0.0;
        double longitude = 0.0;
        for (final XmlAttribute attribute : waypointElement.getAttributes()) {
            if (attribute.is("lat")) {
                latitude = attribute.getValueDouble();
            } else if (attribute.is("lon")) {
                longitude = attribute.getValueDouble();
            }
        }
        coordinate = new Coordinate(latitude, longitude);

        boolean groundspeak_cache = false;
        for (final Element element : waypointElement.getChildren()) {
            if (element.is("name")) {
                code = element.getUnescapedBody();
            } else if (element.is("urlname")) {
                urlName = element.getUnescapedBody();
            } else if (element.is("groundspeak:cache")) {
                groundspeak_cache = true;

                for (final XmlAttribute attribute : element.getAttributes()) {
                    if (attribute.is("id")) {
                        try {
                            id = Integer.valueOf(attribute.getValue());
                        } catch (Exception ignored) {
                        }
                    }
                    if (attribute.is("archived")) {
                        archived = Boolean.valueOf(attribute.getValue());
                    } else if (attribute.is("available")) {
                        available = Boolean.valueOf(attribute.getValue());
                    }
                }

                for (final Element groundspeakElement : element.getChildren()) {
                    if (groundspeakElement.is("groundspeak:name")) {
                        cacheName = groundspeakElement.getUnescapedBody();
                    } else if (groundspeakElement.is("groundspeak:difficulty")) {
                        difficulty = groundspeakElement.getBodyDouble();
                    } else if (groundspeakElement.is("groundspeak:terrain")) {
                        terrain = groundspeakElement.getBodyDouble();
                    } else if (groundspeakElement.is("groundspeak:type")) {
                        type = groundspeakElement.getUnescapedBody();
                    } else if (groundspeakElement.is("groundspeak:owner")) {
                        owner = groundspeakElement.getUnescapedBody();
                    } else if (groundspeakElement.is("groundspeak:container")) {
                        container = groundspeakElement.getUnescapedBody();
                    } else if (groundspeakElement.is("groundspeak:long_description")) {
                        listing = groundspeakElement.getUnescapedBody();
                    } else if (groundspeakElement.is("groundspeak:short_description")) {
                        listingShort = groundspeakElement.getUnescapedBody();
                    } else if (groundspeakElement.is("groundspeak:encoded_hints")) {
                        hint = groundspeakElement.getUnescapedBody();
                    } else if (groundspeakElement.is("groundspeak:logs")) {
                        for (final Element logElement : groundspeakElement.getChildren())
                            if (logElement.is("groundspeak:log")) {
                                // Skip geotoad info log.
                                if (logElement.attrIs("id", "-2")) {
                                    continue;
                                }

                                String logType = null;
                                String author = null;
                                String text = null;
                                String date = null;

                                for (final Element logChildren : logElement.getChildren()) {
                                    if (logChildren.is("groundspeak:date")) {
                                        date = logChildren.getUnescapedBody();
                                    } else if (logChildren.is("groundspeak:type")) {
                                        logType = logChildren.getUnescapedBody();
                                    } else if (logChildren.is("groundspeak:finder")) {
                                        author = logChildren.getUnescapedBody();
                                    } else if (logChildren.is("groundspeak:text")) {
                                        text = logChildren.getUnescapedBody();
                                    }
                                }

                                if (logType != null && logType.equals("Other")) {
                                    continue;
                                }

                                try {
                                    final GeocacheLog log =
                                            new GeocacheLog(logType, author, text, date);
                                    logs.add(log);
                                } catch (NullPointerException | IllegalArgumentException ex) {
                                    ExceptionPanel.display(ex);
                                }
                            }
                    } else if (groundspeakElement.is("groundspeak:attributes")) {
                        for (final Element attributeElement : groundspeakElement.getChildren()) {
                            if (attributeElement.is("groundspeak:attribute")) {
                                Integer attributeId = null;
                                Integer attributeInc = null;
                                String description = null;

                                for (final XmlAttribute attributeAttribute :
                                        attributeElement.getAttributes()) {
                                    if (attributeAttribute.is("id")) {
                                        attributeId = attributeAttribute.getValueInteger();
                                    } else if (attributeAttribute.is("inc")) {
                                        attributeInc = attributeAttribute.getValueInteger();
                                    }
                                }
                                description = attributeElement.getUnescapedBody();

                                try {
                                    final GeocacheAttribute attribute =
                                            new GeocacheAttribute(
                                                    attributeId, attributeInc, description);
                                    attributes.add(attribute);
                                } catch (NullPointerException | IllegalArgumentException ex) {
                                    ExceptionPanel.display(ex);
                                }
                            }
                        }
                    }
                }
            } else if (element.is("gsak:wptExtension")) {
                for (final Element extensionElement : element.getChildren()) {
                    if (extensionElement.is("gsak:IsPremium")) {
                        gcPremium = extensionElement.getBodyBoolean();
                    } else if (extensionElement.is("gsak:FavPoints")) {
                        favoritePoints = extensionElement.getBodyInteger();
                    }
                }
            }
        }

        if (!groundspeak_cache) {
            return null;
        }

        if (container != null && container.equals("unknown")) {
            container = null;
        }

        final Geocache geocache =
                new Geocache(
                        code,
                        cacheName != null ? cacheName : urlName,
                        coordinate,
                        difficulty,
                        terrain,
                        type);
        geocache.setOwner(owner);
        geocache.setContainer(container);
        geocache.setListing(listing);
        geocache.setListingShort(listingShort);
        geocache.setHint(hint);
        geocache.setId(id);
        geocache.setArchived(archived);
        geocache.setAvailable(available);
        geocache.setGcPremium(gcPremium);
        geocache.setFavoritePoints(favoritePoints);
        geocache.addAttributes(attributes);
        geocache.addLogs(logs);

        return geocache;
    }

    public static void saveToFile(List<Geocache> list, String listName, String pathToGpx)
            throws Throwable {
        OutputStream outputStream = FileHelper.openFileWrite(pathToGpx);
        ZipOutputStream zipOutputStream = new ZipOutputStream(outputStream);
        zipOutputStream.setLevel(7);

        if (FileHelper.getFileExtension(listName).equals("zip")) {
            listName = listName.substring(0, listName.length() - 4);
        }

        int subListNumber = 0;
        int baseIndex = 0;
        final int CACHES_PER_GPX = 1000;
        final boolean useSingleFile = list.size() <= CACHES_PER_GPX;
        do {
            final List<Geocache> subList = new ArrayList<>(CACHES_PER_GPX);

            for (int index = 0;
                    index < CACHES_PER_GPX && index + baseIndex < list.size();
                    index++) {
                subList.add(list.get(index + baseIndex));
            }
            baseIndex += CACHES_PER_GPX;
            subListNumber += 1;

            final String subListFileName =
                    useSingleFile ? listName : listName + "-" + subListNumber + ".gpx";
            zipOutputStream.putNextEntry(new ZipEntry(subListFileName));

            final Element root = cacheListToXml(subList, listName);
            Parser.xmlToBuffer(root, zipOutputStream);

            zipOutputStream.closeEntry();
        } while (baseIndex < list.size());

        zipOutputStream.close();
        outputStream.close();
    }

    private static Element cacheListToXml(final List<Geocache> list, String name) {
        final Element root = new Element();

        final Element gpx = new Element("gpx");
        gpx.add(new XmlAttribute("version", "1.0"));
        gpx.add(new XmlAttribute("creator", Constants.APP_NAME));
        gpx.add(
                new XmlAttribute(
                        "xsi:schemaLocation",
                        "http://www.topografix.com/GPX/1/0 http://www.topografix.com/GPX/1/0/gpx.xsd http://www.groundspeak.com/cache/1/0/1 http://www.groundspeak.com/cache/1/0/1/cache.xsd http://www.gsak.net/xmlv1/6 http://www.gsak.net/xmlv1/6/gsak.xsd"));
        gpx.add(new XmlAttribute("xmlns", "http://www.topografix.com/GPX/1/0"));
        gpx.add(new XmlAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance"));
        gpx.add(new XmlAttribute("xmlns:groundspeak", "http://www.groundspeak.com/cache/1/0/1"));
        gpx.add(new XmlAttribute("xmlns:gsak", "http://www.gsak.net/xmlv1/6"));
        gpx.add(new XmlAttribute("xmlns:cgeo", "http://www.cgeo.org/wptext/1/0"));
        root.add(gpx);

        gpx.add(new Element("name", name));
        gpx.add(
                new Element(
                        "desc",
                        "Geocache file generated by "
                                + Constants.APP_NAME
                                + " "
                                + Version.VERSION));
        gpx.add(new Element("author", Constants.APP_NAME));

        final DateTime ddateTime = new DateTime();
        final DateTimeFormatter formatter = ISODateTimeFormat.dateTime();
        final String dateString = formatter.print(ddateTime);
        gpx.add(new Element("time", dateString));

        for (final Geocache geocache : list) {
            gpx.add(cacheToXml(geocache));
            for (Waypoint waypoint : geocache.getWaypoints()) {
                gpx.add(waypointToXml(waypoint));
            }
        }

        return root;
    }

    private static Element waypointToXml(Waypoint waypoint) {
        final Element waypointElement = new Element("wpt");
        waypointElement.add(new XmlAttribute("lat", waypoint.getCoordinate().getLatitude()));
        waypointElement.add(new XmlAttribute("lon", waypoint.getCoordinate().getLongitude()));

        waypointElement.add(new Element("time", waypoint.getDateStrIso8601()));
        waypointElement.add(new Element("name", waypoint.getCode()));
        waypointElement.add(new Element("desc", waypoint.getDescription()));
        waypointElement.add(new Element("sym", waypoint.getSymbol()));
        waypointElement.add(new Element("type", waypoint.getType()));

        final Element gsakExtension = new Element("gsak:wptExtension");
        waypointElement.add(gsakExtension);
        gsakExtension.add(new Element("gsak:Parent", waypoint.getParent()));

        return waypointElement;
    }

    private static Element cacheToXml(Geocache geocache) {
        final Element waypoint = new Element("wpt");
        waypoint.add(new XmlAttribute("lat", geocache.getCoordinate().getLatitude()));
        waypoint.add(new XmlAttribute("lon", geocache.getCoordinate().getLongitude()));

        waypoint.add(new Element("name", geocache.getCode()));
        waypoint.add(new Element("urlname", geocache.getName()));

        final Element groundspeakCache = new Element("groundspeak:cache");
        groundspeakCache.add(new XmlAttribute("id", geocache.getId()));
        groundspeakCache.add(new XmlAttribute("available", geocache.isAvailable()));
        groundspeakCache.add(new XmlAttribute("archived", geocache.isArchived()));
        waypoint.add(groundspeakCache);

        final Element groundspeakAttributes = new Element("groundspeak:attributes");
        groundspeakCache.add(groundspeakAttributes);
        for (GeocacheAttribute attribute : geocache.getAttributes()) {
            final Element groundspeakAttribute = new Element("groundspeak:attribute");
            groundspeakAttribute.add(new XmlAttribute("id", attribute.getId()));
            groundspeakAttribute.add(new XmlAttribute("inc", attribute.getInc()));
            groundspeakAttribute.setBody(attribute.getDescription());
            groundspeakAttributes.add(groundspeakAttribute);
        }

        groundspeakCache.add(new Element("groundspeak:name", geocache.getName()));
        groundspeakCache.add(new Element("groundspeak:difficulty", geocache.getDifficulty()));
        groundspeakCache.add(new Element("groundspeak:terrain", geocache.getTerrain()));
        groundspeakCache.add(new Element("groundspeak:type", geocache.getType().asGcType()));
        groundspeakCache.add(new Element("groundspeak:owner", geocache.getOwner()));
        groundspeakCache.add(new Element("groundspeak:container", geocache.getContainer().asGc()));
        groundspeakCache.add(new Element("groundspeak:long_description", geocache.getListing()));
        groundspeakCache.add(
                new Element("groundspeak:short_description", geocache.getListingShort()));
        groundspeakCache.add(new Element("groundspeak:encoded_hints", geocache.getHint()));

        if (geocache.getLogs().size() > 0) {
            final Element groundspeakLogs = new Element("groundspeak:logs");
            groundspeakCache.add(groundspeakLogs);

            for (final GeocacheLog log : geocache.getLogs()) {
                final Element groundspeakLog = new Element("groundspeak:log");
                groundspeakLogs.add(groundspeakLog);

                groundspeakLog.add(new Element("groundspeak:date", log.getDateStrIso8601()));
                groundspeakLog.add(new Element("groundspeak:type", log.getTypeStr()));
                groundspeakLog.add(new Element("groundspeak:finder", log.getAuthor()));
                groundspeakLog.add(new Element("groundspeak:text", log.getText()));
            }
        }

        final Element gsakExtension = new Element("gsak:wptExtension");
        gsakExtension.add(new Element("gsak:IsPremium", geocache.isGcPremium()));
        gsakExtension.add(new Element("gsak:FavPoints", geocache.getFavoritePoints()));
        waypoint.add(gsakExtension);

        return waypoint;
    }
}
