package org.noos.xing.mydoggy.plaf.persistence.xml;

import org.noos.common.context.Context;
import org.noos.common.context.MutableContext;
import org.noos.common.element.ElementParser;
import org.noos.common.element.ElementWriter;
import org.noos.xing.mydoggy.*;
import org.noos.xing.mydoggy.plaf.MyDoggyToolWindowBar;
import org.noos.xing.mydoggy.plaf.MyDoggyToolWindowManager;
import org.noos.xing.mydoggy.plaf.common.context.DefaultMutableContext;
import org.noos.xing.mydoggy.plaf.persistence.InternalPersistenceDelegateFilter;
import org.noos.xing.mydoggy.plaf.persistence.InternalPersistenceDelegateFilterAdapter;
import org.noos.xing.mydoggy.plaf.persistence.InternalPersistenceDelegateFilterWrapper;
import org.noos.xing.mydoggy.plaf.persistence.PersistenceDelegateCallbackAdapter;
import org.noos.xing.mydoggy.plaf.persistence.xml.merge.MergePolicyApplier;
import org.noos.xing.mydoggy.plaf.persistence.xml.merge.ResetMergePolicy;
import org.noos.xing.mydoggy.plaf.persistence.xml.merge.UnionMergePolicy;
import org.noos.xing.mydoggy.plaf.ui.MyDoggyKeySpace;
import org.noos.xing.mydoggy.plaf.ui.cmp.DockableDropPanel;
import org.noos.xing.mydoggy.plaf.ui.cmp.MultiSplitDockableContainer;
import org.noos.xing.mydoggy.plaf.ui.cmp.MultiSplitLayout;
import org.noos.xing.mydoggy.plaf.ui.cmp.MultiSplitWindow;
import org.noos.xing.mydoggy.plaf.ui.content.ContentDescriptor;
import org.noos.xing.mydoggy.plaf.ui.content.MyDoggyMultiSplitContentManagerUI;
import org.noos.xing.mydoggy.plaf.ui.util.SwingUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import javax.swing.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.awt.*;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.*;
import java.util.*;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class XMLPersistenceDelegate implements PersistenceDelegate {
    protected enum ContextKey {
        ActiveTool,
        MultiSplitContentManagerUILayout
    }


    protected MyDoggyToolWindowManager toolWindowManager;

    protected ElementParser<Element> masterElementParser;
    protected ElementWriter<XMLWriter> masterElementWriter;

    protected Map<MergePolicy, MergePolicyApplier> mergePolicyApplierMap;

    protected PersistenceDelegateCallback dummyCallback;
    protected PersistenceDelegateFilter dummyFilter;


    public XMLPersistenceDelegate(MyDoggyToolWindowManager toolWindowManager) {
        this.toolWindowManager = toolWindowManager;
        this.masterElementParser = new MasterElementParser();
        this.masterElementWriter = new ToolWindowManagerElementWriter(toolWindowManager);
        this.dummyCallback = new PersistenceDelegateCallbackAdapter();
        this.dummyFilter = new InternalPersistenceDelegateFilterAdapter();

        initMaps();
    }


    public void save(OutputStream outputStream) {
        saveInternal(outputStream, dummyFilter, true);
    }

    public void save(OutputStream outputStream, PersistenceDelegateFilter filter) {
        saveInternal(outputStream, filter, true);
    }

    public void apply(InputStream inputStream) {
        applyInternal(inputStream, MergePolicy.RESET, null);
    }

    public void merge(InputStream inputStream, MergePolicy mergePolicy) {
        applyInternal(inputStream, mergePolicy, null);
    }

    public void merge(InputStream inputStream, MergePolicy mergePolicy, PersistenceDelegateCallback callback) {
        applyInternal(inputStream, mergePolicy, callback);
    }


    public void save(OutputStream outputStream, PersistenceDelegateFilter filter, boolean standalone) {
        saveInternal(outputStream, filter, standalone);
    }

    public void apply(Element element, MergePolicy mergePolicy, PersistenceDelegateCallback callback) {
        if (callback == null) {
            // Setup a dummy callback
            callback = dummyCallback;
        }

        try {
            // Setup the context
            DefaultMutableContext context = new DefaultMutableContext();
            context.put(ToolWindowManager.class, toolWindowManager);
            context.put(MyDoggyToolWindowManager.class, toolWindowManager);
            context.put(MergePolicyApplier.class, mergePolicyApplierMap.get(mergePolicy));
            context.put(PersistenceDelegateCallback.class, callback);

            // start parsing..
            masterElementParser.parse(element, context);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    protected void initMaps() {
        mergePolicyApplierMap = new Hashtable<MergePolicy, MergePolicyApplier>();
        mergePolicyApplierMap.put(PersistenceDelegate.MergePolicy.RESET, new ResetMergePolicy());
        mergePolicyApplierMap.put(PersistenceDelegate.MergePolicy.UNION, new UnionMergePolicy());
    }

    protected void saveInternal(OutputStream outputStream, PersistenceDelegateFilter filter, boolean standalone) {
        try {
            XMLWriter writer = new XMLWriter(new OutputStreamWriter(outputStream));

            MutableContext context = new DefaultMutableContext();
            context.put(MyDoggyToolWindowManager.class, toolWindowManager);
            context.put(PersistenceDelegateFilter.class, (filter != null) ? filter : dummyFilter);
            context.put(InternalPersistenceDelegateFilter.class,
                        (filter != null)
                        ? (filter instanceof InternalPersistenceDelegateFilter ? filter : new InternalPersistenceDelegateFilterWrapper(filter))
                        : dummyFilter);
            context.put("standalone", standalone);

            masterElementWriter.write(writer, context);
            writer.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected void applyInternal(InputStream inputStream, MergePolicy mergePolicy, PersistenceDelegateCallback callback) {
        if (callback == null) {
            // Setup a dummy callback
            callback = dummyCallback;
        }

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(inputStream);

            DefaultMutableContext context = new DefaultMutableContext();
            context.put(ToolWindowManager.class, toolWindowManager);
            context.put(MyDoggyToolWindowManager.class, toolWindowManager);
            context.put(MergePolicyApplier.class, mergePolicyApplierMap.get(mergePolicy));
            context.put(PersistenceDelegateCallback.class, callback);

            masterElementParser.parse(document.getDocumentElement(), context);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void storeMultiSplitWindow(XMLWriter writer, MultiSplitWindow<? extends Dockable> sharedWindow) {
        if (sharedWindow.getDockableCount() > 1) {
            try {
                writer.startElement("sharedWindow");
                for (Dockable dockable : sharedWindow.getDockables()) {
                    AttributesImpl attributes = new AttributesImpl();
                    attributes.addAttribute(null, "id", null, null, dockable.getId());
                    writer.dataElement("dockable", attributes);
                }

                // store layout of the window ...

                writer.startElement("layout");
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                XMLEncoder encoder = new XMLEncoder(os);
                encoder.writeObject(sharedWindow.getMultiSplitLayout());
                encoder.flush();
                encoder.close();
                String model = os.toString();
                writer.cdata(model.substring(model.indexOf('\n')));
                writer.endElement("layout");

                writer.endElement("sharedWindow");
            } catch (SAXException e) {
                throw new RuntimeException(e);
            }
        }
    }


    // Writing

    public class ToolWindowManagerElementWriter implements ElementWriter<XMLWriter> {
        protected ToolWindowManager manager;
        protected Map<Class, ElementWriter<XMLWriter>> elementWriterMap;

        public ToolWindowManagerElementWriter(ToolWindowManager manager) {
            this.manager = manager;
            this.elementWriterMap = new HashMap<Class, ElementWriter<XMLWriter>>();

            populateWriterMap();
        }


        public void write(XMLWriter writer, Context context) {
            try {
                // Init context
                MutableContext mutableContext = (MutableContext) context;
                mutableContext.put(ToolWindowManager.class, manager);
                boolean standalone = (Boolean) context.get("standalone");

                // Start document
                if (standalone)
                    writer.startDocument();

                // Write header
                AttributesImpl mydoggyAttributes = new AttributesImpl();
                mydoggyAttributes.addAttribute(null, "version", null, null, "1.5.0");
                mydoggyAttributes.addAttribute(null, "contentManagerEnabled", null, null,
                                               String.valueOf(manager.getContentManager().isEnabled()));
                writer.startElement("mydoggy", mydoggyAttributes);

                // Write ToolWindows
                writer.startElement("toolWindows");

                // Write SharedWindows
                writer.startElement("sharedWindows");

                for (MultiSplitWindow<? extends Dockable> multiSplitWindow : toolWindowManager.getFloatingWindows()) {
                    storeMultiSplitWindow(writer, multiSplitWindow);
                }

                for (MultiSplitWindow<? extends Dockable> multiSplitWindow : toolWindowManager.getFloatingLiveWindows()) {
                    storeMultiSplitWindow(writer, multiSplitWindow);
                }

                writer.endElement("sharedWindows");

                // Write tools

                for (ToolWindow toolWindow : manager.getToolWindows()) {
                    if (!context.get(PersistenceDelegateFilter.class).storeToolWindow(toolWindow))
                        continue;

                    mutableContext.put(ToolWindow.class, toolWindow);
                    getElementWriter(ToolWindow.class).write(writer, context);
                }

                writer.endElement("toolWindows");

                // Write ToolWindowManagerDescriptor
                if (context.get(PersistenceDelegateFilter.class).storeToolWindowManagerDescriptor()) {
                    mutableContext.put(ToolWindowManagerDescriptor.class, manager.getToolWindowManagerDescriptor());
                    getElementWriter(ToolWindowManagerDescriptor.class).write(writer, context);
                }

                if (context.get(PersistenceDelegateFilter.class).storeContentManager()) {
                    // Write ContentManagerUI
                    mutableContext.put(ContentManager.class, manager.getContentManager());
                    mutableContext.put(ContentManagerUI.class, manager.getContentManager().getContentManagerUI());

                    ContentManager contentManager = manager.getContentManager();
                    writer.startElement("contentManagerUI");
                    getElementWriter(contentManager.getContentManagerUI().getClass()).write(writer, context);
                    writer.endElement("contentManagerUI");

                    // Write ContentManager
                    getElementWriter(ContentManager.class).write(writer, context);
                }

                // Write ToolWindowAnchor
                getElementWriter(ToolWindowAnchor.class).write(writer, context);

                // End document
                writer.endElement("mydoggy");

                if (standalone)
                    writer.endDocument();

                writer.flush();
            } catch (SAXException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }


        protected ElementWriter<XMLWriter> getElementWriter(Class clazz) {
            ElementWriter<XMLWriter> elementWriter = elementWriterMap.get(clazz);
            if (elementWriter == null) {

                while (clazz != null) {

                    // Check super class
                    elementWriter = elementWriterMap.get(clazz.getSuperclass());
                    if (elementWriter != null)
                        return elementWriter;

                    // Check interfaces
                    for (Class interfaceClazz : clazz.getInterfaces()) {
                        elementWriter = elementWriterMap.get(interfaceClazz);
                        if (elementWriter != null)
                            return elementWriter;
                    }

                    clazz = clazz.getSuperclass();
                }
            }

            return elementWriter;
        }

        protected void populateWriterMap() {
            elementWriterMap.put(ToolWindow.class, new ToolWindowEntityWriter());
            elementWriterMap.put(ToolWindowManagerDescriptor.class, new ToolWindowManagerDescriptorEntityWriter());
            elementWriterMap.put(ContentManager.class, new ContentManagerEntityWriter());

            elementWriterMap.put(TabbedContentManagerUI.class, new TabbedContentManagerUIEntityWriter());
            elementWriterMap.put(MultiSplitContentManagerUI.class, new MultiSplitContentManagerUIEntityWriter());
            elementWriterMap.put(DesktopContentManagerUI.class, new DesktopContentManagerUIEntityWriter());
            elementWriterMap.put(ToolWindowAnchor.class, new ToolWindowAnchorEntityWriter());
        }

    }

    public class ToolWindowEntityWriter implements ElementWriter<XMLWriter> {

        public void write(XMLWriter writer, Context context) {
            try {
                ToolWindow toolWindow = context.get(ToolWindow.class);

                AttributesImpl toolAttributes = new AttributesImpl();
                toolAttributes.addAttribute(null, "id", null, null, String.valueOf(toolWindow.getId()));
                toolAttributes.addAttribute(null, "available", null, null, String.valueOf(toolWindow.isAvailable()));
                toolAttributes.addAttribute(null, "visible", null, null, String.valueOf(toolWindow.isVisible()));
                toolAttributes.addAttribute(null, "active", null, null, String.valueOf(toolWindow.isActive()));
                toolAttributes.addAttribute(null, "autoHide", null, null, String.valueOf(toolWindow.isAutoHide()));
                toolAttributes.addAttribute(null, "anchor", null, null, String.valueOf(toolWindow.getAnchor()));
                toolAttributes.addAttribute(null, "anchorIndex", null, null, String.valueOf(toolWindow.getAnchorIndex()));
                toolAttributes.addAttribute(null, "type", null, null, String.valueOf(toolWindow.getType()));
                toolAttributes.addAttribute(null, "aggregateMode", null, null, String.valueOf(toolWindow.isAggregateMode()));
                toolAttributes.addAttribute(null, "maximized", null, null, String.valueOf(toolWindow.isMaximized()));
                toolAttributes.addAttribute(null, "index", null, null, String.valueOf(toolWindow.getIndex()));
                toolAttributes.addAttribute(null, "flashing", null, null, String.valueOf(toolWindow.isFlashing()));

                // Properties from 1.5.0
                toolAttributes.addAttribute(null, "lockedOnAnchor", null, null, String.valueOf(toolWindow.isLockedOnAnchor()));
                toolAttributes.addAttribute(null, "hideOnZeroTabs", null, null, String.valueOf(toolWindow.isHideOnZeroTabs()));
                writer.startElement("toolWindow", toolAttributes);

                writer.startElement("descriptors");

                // DockedTypeDescriptor
                DockedTypeDescriptor dockedTypeDescriptor = (DockedTypeDescriptor) toolWindow.getTypeDescriptor(ToolWindowType.DOCKED);

                AttributesImpl dockedDescriptorAttributes = new AttributesImpl();
                addAttributes(dockedDescriptorAttributes, dockedTypeDescriptor);
                dockedDescriptorAttributes.addAttribute(null, "dockLength", null, null, String.valueOf(dockedTypeDescriptor.getDockLength()));
                dockedDescriptorAttributes.addAttribute(null, "minimumDockLength", null, null, String.valueOf(dockedTypeDescriptor.getMinimumDockLength()));
                dockedDescriptorAttributes.addAttribute(null, "popupMenuEnabled", null, null, String.valueOf(dockedTypeDescriptor.isPopupMenuEnabled()));
                writer.dataElement("docked", dockedDescriptorAttributes);

                // SlidingTypeDescriptor
                SlidingTypeDescriptor slidingTypeDescriptor = (SlidingTypeDescriptor) toolWindow.getTypeDescriptor(ToolWindowType.SLIDING);

                AttributesImpl slidingDescriptorAttributes = new AttributesImpl();
                addAttributes(slidingDescriptorAttributes, slidingTypeDescriptor);
                slidingDescriptorAttributes.addAttribute(null, "transparentMode", null, null, String.valueOf(slidingTypeDescriptor.isTransparentMode()));
                slidingDescriptorAttributes.addAttribute(null, "transparentDelay", null, null, String.valueOf(slidingTypeDescriptor.getTransparentDelay()));
                slidingDescriptorAttributes.addAttribute(null, "transparentRatio", null, null, String.valueOf(slidingTypeDescriptor.getTransparentRatio()));
                writer.dataElement("sliding", slidingDescriptorAttributes);

                // FloatingTypeDescriptor
                FloatingTypeDescriptor floatingTypeDescriptor = (FloatingTypeDescriptor) toolWindow.getTypeDescriptor(ToolWindowType.FLOATING);

                AttributesImpl floatingDescriptorAttributes = new AttributesImpl();
                addAttributes(floatingDescriptorAttributes, floatingTypeDescriptor);
                floatingDescriptorAttributes.addAttribute(null, "modal", null, null, String.valueOf(floatingTypeDescriptor.isModal()));
                floatingDescriptorAttributes.addAttribute(null, "transparentMode", null, null, String.valueOf(floatingTypeDescriptor.isTransparentMode()));
                floatingDescriptorAttributes.addAttribute(null, "transparentDelay", null, null, String.valueOf(floatingTypeDescriptor.getTransparentDelay()));
                floatingDescriptorAttributes.addAttribute(null, "transparentRatio", null, null, String.valueOf(floatingTypeDescriptor.getTransparentRatio()));
                floatingDescriptorAttributes.addAttribute(null, "addToTaskBar", null, null, String.valueOf(floatingTypeDescriptor.isAddToTaskBar()));
                floatingDescriptorAttributes.addAttribute(null, "alwaysOnTop", null, null, String.valueOf(floatingTypeDescriptor.isAlwaysOnTop()));
                floatingDescriptorAttributes.addAttribute(null, "osDecorated", null, null, String.valueOf(floatingTypeDescriptor.isOsDecorated()));
                floatingDescriptorAttributes.addAttribute(null, "resizable", null, null, String.valueOf(floatingTypeDescriptor.isResizable()));

                // Store location
                Point point = floatingTypeDescriptor.getLocation();
                Dimension dimension = floatingTypeDescriptor.getSize();
                if (point != null || dimension != null) {
                    writer.startElement("floating", floatingDescriptorAttributes);

                    if (point != null) {
                        AttributesImpl attributes = new AttributesImpl();
                        attributes.addAttribute(null, "x", null, null, String.valueOf(point.x));
                        attributes.addAttribute(null, "y", null, null, String.valueOf(point.y));
                        writer.dataElement("location", attributes);
                    }

                    if (dimension != null) {
                        AttributesImpl attributes = new AttributesImpl();
                        attributes.addAttribute(null, "width", null, null, String.valueOf(dimension.width));
                        attributes.addAttribute(null, "height", null, null, String.valueOf(dimension.height));
                        writer.dataElement("size", attributes);
                    }

                    writer.endElement("floating");
                } else
                    writer.dataElement("floating", floatingDescriptorAttributes);

                // Store ScreenWindow bounds.. TODO: this is never loaded....why??? when a tool is maximized and stored also the previzios bounds should be stored and then restored...
                if (toolWindow.isMaximized() && (toolWindow.getType() == ToolWindowType.FLOATING || toolWindow.getType() == ToolWindowType.FLOATING_FREE)) {
                    Rectangle screenWindowBounds = context.get(MyDoggyToolWindowManager.class).getDescriptor(toolWindow).getScreenWindowBounds();

                    AttributesImpl attributes = new AttributesImpl();
                    attributes.addAttribute(null, "x", null, null, String.valueOf(screenWindowBounds.x));
                    attributes.addAttribute(null, "y", null, null, String.valueOf(screenWindowBounds.y));
                    attributes.addAttribute(null, "width", null, null, String.valueOf(screenWindowBounds.width));
                    attributes.addAttribute(null, "height", null, null, String.valueOf(screenWindowBounds.height));
                    writer.dataElement("screenWindowBounds", attributes);
                }

                // FloatingLiveTypeDescriptor
                FloatingLiveTypeDescriptor floatingLiveTypeDescriptor = (FloatingLiveTypeDescriptor) toolWindow.getTypeDescriptor(ToolWindowType.FLOATING_LIVE);

                AttributesImpl floatingLiveDescriptorAttributes = new AttributesImpl();
                addAttributes(floatingLiveDescriptorAttributes, floatingLiveTypeDescriptor);
                floatingLiveDescriptorAttributes.addAttribute(null, "transparentMode", null, null, String.valueOf(floatingLiveTypeDescriptor.isTransparentMode()));
                floatingLiveDescriptorAttributes.addAttribute(null, "transparentDelay", null, null, String.valueOf(floatingLiveTypeDescriptor.getTransparentDelay()));
                floatingLiveDescriptorAttributes.addAttribute(null, "transparentRatio", null, null, String.valueOf(floatingLiveTypeDescriptor.getTransparentRatio()));
                floatingLiveDescriptorAttributes.addAttribute(null, "resizable", null, null, String.valueOf(floatingLiveTypeDescriptor.isResizable()));

                // Store location
                point = floatingLiveTypeDescriptor.getLocation();
                dimension = floatingLiveTypeDescriptor.getSize();
                if (point != null || dimension != null) {
                    writer.startElement("floatingLive", floatingDescriptorAttributes);

                    if (point != null) {
                        AttributesImpl attributes = new AttributesImpl();
                        attributes.addAttribute(null, "x", null, null, String.valueOf(point.x));
                        attributes.addAttribute(null, "y", null, null, String.valueOf(point.y));
                        writer.dataElement("location", attributes);
                    }

                    if (dimension != null) {
                        AttributesImpl attributes = new AttributesImpl();
                        attributes.addAttribute(null, "width", null, null, String.valueOf(dimension.width));
                        attributes.addAttribute(null, "height", null, null, String.valueOf(dimension.height));
                        writer.dataElement("size", attributes);
                    }

                    writer.endElement("floatingLive");
                } else
                    writer.dataElement("floatingLive", floatingLiveDescriptorAttributes);

                // RepresentativeAnchor Descriptor
                RepresentativeAnchorDescriptor anchorDescriptor = toolWindow.getRepresentativeAnchorDescriptor();

                AttributesImpl anchorDescriptorAttributes = new AttributesImpl();
                anchorDescriptorAttributes.addAttribute(null, "visible", null, null, String.valueOf(toolWindow.getRepresentativeAnchorDescriptor().isVisible()));
                anchorDescriptorAttributes.addAttribute(null, "title", null, null, toolWindow.getRepresentativeAnchorDescriptor().getTitle());
                anchorDescriptorAttributes.addAttribute(null, "previewEnabled", null, null, String.valueOf(anchorDescriptor.isPreviewEnabled()));
                anchorDescriptorAttributes.addAttribute(null, "previewDelay", null, null, String.valueOf(anchorDescriptor.getPreviewDelay()));
                anchorDescriptorAttributes.addAttribute(null, "previewTransparentRatio", null, null, String.valueOf(anchorDescriptor.getPreviewTransparentRatio()));
                writer.startElement("anchor", anchorDescriptorAttributes);

                writer.startElement("lockingAnchors");
                for (ToolWindowAnchor anchor : anchorDescriptor.getLockingAnchors()) {
                    AttributesImpl attributes = new AttributesImpl();
                    attributes.addAttribute(null, "anchor", null, null, anchor.toString());

                    writer.dataElement("lockingAnchor", attributes);
                }
                writer.endElement("lockingAnchors");

                writer.endElement("anchor");

                // End descriptors
                writer.endElement("descriptors");

                // Tabs
                boolean addTabsTag = false;
                for (ToolWindowTab tab : toolWindow.getToolWindowTabs()) {
                    if (tab.getDockableDelegator() != null) {
                        Dockable dockable = tab.getDockableDelegator();

                        if (!addTabsTag) {
                            addTabsTag = true;
                            writer.startElement("tabs");
                        }

                        AttributesImpl attributes = new AttributesImpl();
                        attributes.addAttribute(null, "dockableId", null, null, dockable.getId());
                        attributes.addAttribute(null, "selected", null, null, String.valueOf(tab.isSelected()));
                        attributes.addAttribute(null, "maximized", null, null, String.valueOf(tab.isMaximized()));
                        attributes.addAttribute(null, "minimized", null, null, String.valueOf(tab.isMinimized()));
                        attributes.addAttribute(null, "closeable", null, null, String.valueOf(tab.isCloseable()));
                        attributes.addAttribute(null, "minimizable", null, null, String.valueOf(tab.isMinimizable()));
                        attributes.addAttribute(null, "detached", null, null, String.valueOf(tab.isDetached()));
                        attributes.addAttribute(null, "flashing", null, null, String.valueOf(tab.isFlashing()));

                        writer.dataElement("tab", attributes);
                    }
                }

                if (addTabsTag)
                    writer.endElement("tabs");

                writer.endElement("toolWindow");
            } catch (SAXException e) {
                throw new RuntimeException(e);
            }
        }


        protected void addAttributes(AttributesImpl attributes, ToolWindowTypeDescriptor descriptor) {
            attributes.addAttribute(null, "animating", null, null, String.valueOf(descriptor.isAnimating()));
            attributes.addAttribute(null, "autoHide", null, null, String.valueOf(descriptor.isAutoHide()));
            attributes.addAttribute(null, "enabled", null, null, String.valueOf(descriptor.isEnabled()));
            attributes.addAttribute(null, "hideRepresentativeButtonOnVisible", null, null, String.valueOf(descriptor.isHideRepresentativeButtonOnVisible()));
            attributes.addAttribute(null, "idVisibleOnTitleBar", null, null, String.valueOf(descriptor.isIdVisibleOnTitleBar()));
            attributes.addAttribute(null, "titleBarButtonsVisible", null, null, String.valueOf(descriptor.isTitleBarButtonsVisible()));
            attributes.addAttribute(null, "titleBarVisible", null, null, String.valueOf(descriptor.isTitleBarVisible()));
        }
    }

    public class ToolWindowManagerDescriptorEntityWriter implements ElementWriter<XMLWriter> {

        public void write(XMLWriter writer, Context context) {
            try {
                ToolWindowManagerDescriptor descriptor = context.get(ToolWindowManagerDescriptor.class);

                // Start toolWindowDescriptorManager
                AttributesImpl attributes = new AttributesImpl();
                attributes.addAttribute(null, "numberingEnabled", null, null, String.valueOf(descriptor.isNumberingEnabled()));
                attributes.addAttribute(null, "previewEnabled", null, null, String.valueOf(descriptor.isPreviewEnabled()));
                attributes.addAttribute(null, "showUnavailableTools", null, null, String.valueOf(descriptor.isShowUnavailableTools()));
                writer.startElement("toolWindowManagerDescriptor", attributes);

                // Start pushAway
                attributes = new AttributesImpl();
                attributes.addAttribute(null, "pushAwayMode", null, null,
                                        descriptor.getPushAwayMode().toString());
                writer.startElement("pushAway", attributes);

                // start MOST_RECENT policy
                attributes = new AttributesImpl();
                attributes.addAttribute(null, "type", null, null, String.valueOf(PushAwayMode.MOST_RECENT));
                writer.startElement("mode", attributes);

                MostRecentDescriptor mostRecentDescriptor = (MostRecentDescriptor) descriptor.getPushAwayModeDescriptor(PushAwayMode.MOST_RECENT);

                for (ToolWindowAnchor toolWindowAnchor : mostRecentDescriptor.getMostRecentAnchors()) {
                    AttributesImpl anchorAttributes = new AttributesImpl();
                    anchorAttributes.addAttribute(null, "type", null, null, String.valueOf(toolWindowAnchor));
                    writer.dataElement("anchor", anchorAttributes);
                }

                // end MOST_RECENT policy
                writer.endElement("mode");

                // End pushAway
                writer.endElement("pushAway");

                // End toolWindowDescriptorManager
                writer.endElement("toolWindowManagerDescriptor");
            } catch (SAXException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public class ContentManagerEntityWriter implements ElementWriter<XMLWriter> {

        public void write(XMLWriter writer, Context context) {
            try {
                // Start contentManager
                MyDoggyToolWindowManager toolWindowManager = (MyDoggyToolWindowManager) context.get(ToolWindowManager.class);
                ContentManager contentManager = context.get(ContentManager.class);

                writer.startElement("contentManager");

                writer.startElement("sharedWindows");
                for (MultiSplitWindow<? extends Dockable> multiSplitWindow : SwingUtil.getContentWindows()) {
                    storeMultiSplitWindow(writer, multiSplitWindow);
                }
                writer.endElement("sharedWindows");

                // write contents
                writer.startElement("contents");
                for (Content content : contentManager.getContents()) {
                    ContentUI contentUI = content.getContentUI();

                    // Prepare "content" element
                    AttributesImpl contentAttributes = new AttributesImpl();
                    contentAttributes.addAttribute(null, "id", null, null, content.getId());
                    contentAttributes.addAttribute(null, "detached", null, null, String.valueOf(content.isDetached()));
                    contentAttributes.addAttribute(null, "enabled", null, null, String.valueOf(content.isEnabled()));
                    contentAttributes.addAttribute(null, "selected", null, null, context.get(InternalPersistenceDelegateFilter.class).saveSelectedContent() ? String.valueOf(content.isSelected()) : "false");
                    contentAttributes.addAttribute(null, "maximized", null, null, String.valueOf(content.isMaximized()));
                    contentAttributes.addAttribute(null, "minimized", null, null, String.valueOf(content.isMinimized()));
                    contentAttributes.addAttribute(null, "flashing", null, null, String.valueOf(content.isFlashing()));

                    contentAttributes.addAttribute(null, "closeable", null, null, String.valueOf(contentUI.isCloseable()));
                    contentAttributes.addAttribute(null, "detachable", null, null, String.valueOf(contentUI.isDetachable()));
                    contentAttributes.addAttribute(null, "minimizable", null, null, String.valueOf(contentUI.isMinimizable()));
                    contentAttributes.addAttribute(null, "maximizable", null, null, String.valueOf(contentUI.isMaximizable()));
                    contentAttributes.addAttribute(null, "transparentMode", null, null, String.valueOf(contentUI.isTransparentMode()));
                    contentAttributes.addAttribute(null, "transparentDelay", null, null, String.valueOf(contentUI.getTransparentDelay()));
                    contentAttributes.addAttribute(null, "transparentRatio", null, null, String.valueOf(contentUI.getTransparentRatio()));
                    contentAttributes.addAttribute(null, "addToTaskBarWhenDetached", null, null, String.valueOf(contentUI.isAddToTaskBarWhenDetached()));
                    contentAttributes.addAttribute(null, "alwaysOnTop", null, null, String.valueOf(contentUI.isAlwaysOnTop()));

                    writer.startElement("content", contentAttributes);

                    // Prepare "detachedBounds" sub element if necessary
                    Rectangle detachedBounds = contentUI.getDetachedBounds();
                    if (detachedBounds != null) {
                        AttributesImpl attributes = new AttributesImpl();
                        attributes.addAttribute(null, "x", null, null, String.valueOf(detachedBounds.x));
                        attributes.addAttribute(null, "y", null, null, String.valueOf(detachedBounds.y));
                        attributes.addAttribute(null, "width", null, null, String.valueOf(detachedBounds.width));
                        attributes.addAttribute(null, "height", null, null, String.valueOf(detachedBounds.height));
                        writer.dataElement("detachedBounds", attributes);
                    }

                    // Prepare "minimizedState" sub element if necessary
                    if (content.isMinimized()) {
                        ContentDescriptor contentDescriptor = (ContentDescriptor) toolWindowManager.getDockableDescriptor(content.getId());

                        AttributesImpl attributes = new AttributesImpl();
                        attributes.addAttribute(null, "anchor", null, null, contentDescriptor.getAnchor().toString());
                        attributes.addAttribute(null, "anchorIndex", null, null, String.valueOf(contentDescriptor.getAnchorIndex()));

                        writer.dataElement("minimizedState", attributes);
                    }

                    writer.endElement("content");
                }
                writer.endElement("contents");

                if (!contentManager.isEnabled()) {
                    MultiSplitDockableContainer dockableContainer = (MultiSplitDockableContainer) ((DockableDropPanel) toolWindowManager.getMainContent()).getComponent();
                    writer.startElement("layout");

                    ByteArrayOutputStream os = new ByteArrayOutputStream();
                    XMLEncoder encoder = new XMLEncoder(os);
                    encoder.writeObject(dockableContainer.getMultiSplitLayout());
                    encoder.flush();
                    encoder.close();

                    String model = os.toString();
                    writer.cdata(model.substring(model.indexOf('\n')));

                    writer.endElement("layout");
                }

                // End contentManager
                writer.endElement("contentManager");
            } catch (SAXException e) {
                throw new RuntimeException(e);
            }
        }

    }

    public abstract class ContentManagerUIEntityWriter implements ElementWriter<XMLWriter> {

        protected void addContentManagerUIAttributes(AttributesImpl attributes, ContentManagerUI contentManagerUI) {
            attributes.addAttribute(null, "closeable", null, null, String.valueOf(contentManagerUI.isCloseable()));
            attributes.addAttribute(null, "detachable", null, null, String.valueOf(contentManagerUI.isDetachable()));
            attributes.addAttribute(null, "minimizable", null, null, String.valueOf(contentManagerUI.isMinimizable()));
            attributes.addAttribute(null, "maximizable", null, null, String.valueOf(contentManagerUI.isMaximizable()));
            attributes.addAttribute(null, "popupMenuEnabled", null, null, String.valueOf(contentManagerUI.isPopupMenuEnabled()));
        }

    }

    public class TabbedContentManagerUIEntityWriter extends ContentManagerUIEntityWriter {

        public void write(XMLWriter writer, Context context) {
            try {
                TabbedContentManagerUI tabbedContentManagerUI = (TabbedContentManagerUI) context.get(ContentManagerUI.class);

                AttributesImpl attributes = new AttributesImpl();
                addContentManagerUIAttributes(attributes, tabbedContentManagerUI);
                attributes.addAttribute(null, "showAlwaysTab", null, null, String.valueOf(tabbedContentManagerUI.isShowAlwaysTab()));
                attributes.addAttribute(null, "tabLayout", null, null, tabbedContentManagerUI.getTabLayout().toString());
                attributes.addAttribute(null, "tabPlacement", null, null, tabbedContentManagerUI.getTabPlacement().toString());

                writer.dataElement("TabbedContentManagerUI", attributes);
            } catch (SAXException e) {
                throw new RuntimeException(e);
            }
        }

    }

    public class MultiSplitContentManagerUIEntityWriter extends ContentManagerUIEntityWriter {

        public void write(XMLWriter writer, Context context) {
            try {
                MultiSplitContentManagerUI multiSplitContentManagerUI = (MultiSplitContentManagerUI) context.get(ContentManagerUI.class);

                AttributesImpl attributes = new AttributesImpl();
                addContentManagerUIAttributes(attributes, multiSplitContentManagerUI);

                attributes.addAttribute(null, "showAlwaysTab", null, null, String.valueOf(multiSplitContentManagerUI.isShowAlwaysTab()));
                attributes.addAttribute(null, "tabLayout", null, null, multiSplitContentManagerUI.getTabLayout().toString());
                attributes.addAttribute(null, "tabPlacement", null, null, multiSplitContentManagerUI.getTabPlacement().toString());

                writer.startElement("MultiSplitContentManagerUI", attributes);

                writer.startElement("contents");
                for (Content content : context.get(ContentManager.class).getContents()) {
                    MultiSplitContentUI contentUI = (MultiSplitContentUI) content.getContentUI();

                    AttributesImpl contentUIAttributes = new AttributesImpl();
                    contentUIAttributes.addAttribute(null, "id", null, null, content.getId());
                    contentUIAttributes.addAttribute(null, "showAlwaysTab", null, null, String.valueOf(contentUI.isShowAlwaysTab()));

                    writer.dataElement("content", contentUIAttributes);
                }
                writer.endElement("contents");

                writer.startElement("layout");
                MyDoggyMultiSplitContentManagerUI splitContentManagerUI = (MyDoggyMultiSplitContentManagerUI) multiSplitContentManagerUI;

                ByteArrayOutputStream os = new ByteArrayOutputStream();
                XMLEncoder encoder = new XMLEncoder(os);
                encoder.writeObject(splitContentManagerUI.getLayout());
                encoder.flush();
                encoder.close();

                String model = os.toString();
                writer.cdata(model.substring(model.indexOf('\n')));
                writer.endElement("layout");

                writer.endElement("MultiSplitContentManagerUI");
            } catch (SAXException e) {
                throw new RuntimeException(e);
            }

        }

    }

    public class DesktopContentManagerUIEntityWriter extends ContentManagerUIEntityWriter {

        public void write(XMLWriter writer, Context context) {
            try {
                DesktopContentManagerUI desktopContentManagerUI = (DesktopContentManagerUI) context.get(ContentManagerUI.class);

                AttributesImpl attributes = new AttributesImpl();
                addContentManagerUIAttributes(attributes, desktopContentManagerUI);

                writer.startElement("DesktopContentManagerUI", attributes);

                writer.startElement("contents");
                for (Content content : context.get(ContentManager.class).getContents()) {
                    DesktopContentUI contentUI = (DesktopContentUI) content.getContentUI();

                    AttributesImpl contentUIAttributes = new AttributesImpl();
                    contentUIAttributes.addAttribute(null, "id", null, null, content.getId());
                    contentUIAttributes.addAttribute(null, "x", null, null, String.valueOf(contentUI.getLocation().x));
                    contentUIAttributes.addAttribute(null, "y", null, null, String.valueOf(contentUI.getLocation().y));
                    contentUIAttributes.addAttribute(null, "width", null, null, String.valueOf(contentUI.getSize().width));
                    contentUIAttributes.addAttribute(null, "height", null, null, String.valueOf(contentUI.getSize().height));
                    contentUIAttributes.addAttribute(null, "iconified", null, null, String.valueOf(contentUI.isIconified()));

                    writer.dataElement("content", contentUIAttributes);
                }
                writer.endElement("contents");

                writer.endElement("DesktopContentManagerUI");
            } catch (SAXException e) {
                throw new RuntimeException(e);
            }
        }

    }

    public class ToolWindowAnchorEntityWriter implements ElementWriter<XMLWriter> {

        public void write(XMLWriter writer, Context context) {
            try {
                ToolWindowManager toolWindowManager = context.get(ToolWindowManager.class);

                // Save bars
                writer.startElement("toolWindowBars");

                // Save single bar
                saveBar(writer, (MyDoggyToolWindowBar) toolWindowManager.getToolWindowBar(ToolWindowAnchor.LEFT), context);
                saveBar(writer, (MyDoggyToolWindowBar) toolWindowManager.getToolWindowBar(ToolWindowAnchor.BOTTOM), context);
                saveBar(writer, (MyDoggyToolWindowBar) toolWindowManager.getToolWindowBar(ToolWindowAnchor.RIGHT), context);
                saveBar(writer, (MyDoggyToolWindowBar) toolWindowManager.getToolWindowBar(ToolWindowAnchor.TOP), context);

                writer.endElement("toolWindowBars");
            } catch (SAXException e) {
                throw new RuntimeException(e);
            }
        }

        protected void saveBar(XMLWriter writer, MyDoggyToolWindowBar toolWindowBar, Context context) throws SAXException {
            if (context.get(PersistenceDelegateFilter.class).storeToolWindowBar(toolWindowBar)) {
                AttributesImpl attributes = new AttributesImpl();
                attributes.addAttribute(null, "anchor", null, null, toolWindowBar.getAnchor().toString());
                attributes.addAttribute(null, "dividerSize", null, null, String.valueOf(toolWindowBar.getDividerSize()));
                attributes.addAttribute(null, "aggregateMode", null, null, String.valueOf(toolWindowBar.isAggregateMode()));
                writer.startElement("toolWindowBar", attributes);

                // Check for model
                if (toolWindowBar.getToolWindows().length > 0) {
                    writer.startElement("layout");

                    ByteArrayOutputStream os = new ByteArrayOutputStream();
                    XMLEncoder encoder = new XMLEncoder(os);
                    encoder.writeObject(toolWindowBar.getLayout());
                    encoder.flush();
                    encoder.close();

                    String model = os.toString();
                    writer.cdata(model.substring(model.indexOf('\n')));

                    writer.endElement("layout");
                }

                if (toolWindowBar.getVisibleWorkspace() != null) {
                    writer.startElement("workspace");
                    writer.cdata(Base64.encodeBytes(toolWindowBar.getVisibleWorkspace().toByteArray()));
                    writer.endElement("workspace");
                }

                writer.endElement("toolWindowBar");
            }
        }

    }


    // Reading

    public class MasterElementParser implements ElementParser<Element> {
        protected Map<String, ElementParser<Element>> elementParserMap;

        public MasterElementParser() {
            elementParserMap = new Hashtable<String, ElementParser<Element>>();
            populateParserMap();
        }

        public boolean parse(Element element, Context context) {
            try {
                context.get(MyDoggyToolWindowManager.class).putClientProperty(MyDoggyKeySpace.PERSISTENCE_DELEGATE_PARSING, this);

                // Parse tree
                boolean result = parseTree(element, context);

                // Finalize
                final ToolWindow activeTool = (ToolWindow) context.get(ContextKey.ActiveTool);
                if (activeTool != null) {
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            activeTool.setActive(true);
                        }
                    });
                }
                return result;
            } finally {
                context.get(MyDoggyToolWindowManager.class).putClientProperty(MyDoggyKeySpace.PERSISTENCE_DELEGATE_PARSING, null);
            }
        }

        public boolean parseTree(Element element, Context context) {
            ElementParser<Element> elementParser = elementParserMap.get(element.getNodeName());

            if (elementParser == null || elementParser.parse(element, context)) {
                NodeList children = element.getChildNodes();

                for (int i = 0, size = children.getLength(); i < size; i++) {
                    Node node = children.item(i);
                    if (node.getNodeType() == Node.ELEMENT_NODE)
                        parse((Element) node, context);
                }
            }
            return false;
        }


        protected void populateParserMap() {
            elementParserMap.put("mydoggy", new MyDoggyElementParser());
            elementParserMap.put("toolWindowManagerDescriptor", new ToolWindowManagerDescriptorElementParser());
            elementParserMap.put("pushAway", new PushAwayModeElementParser());
            elementParserMap.put("toolWindows", new ToolWindowsElementParser());
            elementParserMap.put("contentManager", new ContentManagerElementParser());
            elementParserMap.put("toolWindowBar", new ToolWindowBarElementParser());
            elementParserMap.put("MultiSplitContentManagerUI", new MultiSplitContentManagerUIElementParser());
            elementParserMap.put("TabbedContentManagerUI", new TabbedContentManagerUIElementParser());
            elementParserMap.put("DesktopContentManagerUI", new DekstopManagerUIElementParser());
        }

    }


    public static abstract class ElementParserAdapter implements ElementParser<Element> {
        protected static XMLPersistenceNode node = new XMLPersistenceNode();


        protected ElementParserAdapter() {
        }


        public Element getElement(Element root, String name) {
            NodeList list = root.getElementsByTagName(name);
            if (list.getLength() == 0)
                return null;
            return (Element) list.item(0);
        }

        public boolean isAttributePresent(Element element, String name) {
            String attr = element.getAttribute(name);
            return attr != null && !"".equals(attr.trim());
        }

        public String getAttributeValue(Context context, Element element, String name, String defaultValue) {
            try {
                String attr = context.get(PersistenceDelegateCallback.class).validate(node.setElement(element), name, element.getAttribute(name), defaultValue);

                if (attr != null && !"".equals(attr.trim()))
                    return attr;
                else
                    return defaultValue;
            } catch (Exception e) {
                return defaultValue;
            }
        }

        public boolean getBoolean(Context context, Element element, String name, boolean defaultValue) {
            try {
                String attr = context.get(PersistenceDelegateCallback.class).validate(node.setElement(element), name, element.getAttribute(name), defaultValue);

                if (attr != null && !"".equals(attr.trim()))
                    return Boolean.parseBoolean(attr);
                else
                    return defaultValue;
            } catch (Exception e) {
                return defaultValue;
            }
        }

        public int getInteger(Context context, Element element, String name, int defaultValue) {
            try {
                String attr = context.get(PersistenceDelegateCallback.class).validate(node.setElement(element), name, element.getAttribute(name), defaultValue);

                if (attr != null && !"".equals(attr.trim()))
                    return Integer.parseInt(attr);
                else
                    return defaultValue;
            } catch (Exception e) {
                return defaultValue;
            }
        }

        public float getFloat(Context context, Element element, String name, float defaultValue) {
            try {
                String attr = context.get(PersistenceDelegateCallback.class).validate(node.setElement(element), name, element.getAttribute(name), defaultValue);

                if (attr != null && !"".equals(attr.trim()))
                    return Float.parseFloat(attr);
                else
                    return defaultValue;
            } catch (Exception e) {
                return defaultValue;
            }
        }

        public ToolWindowAnchor getToolWindowAnchor(Context context, Element element, String name, ToolWindowAnchor defaultValue) {
            try {
                String attr = context.get(PersistenceDelegateCallback.class).validate(node.setElement(element), name, element.getAttribute(name), defaultValue);

                if (attr != null && !"".equals(attr.trim()))
                    return ToolWindowAnchor.valueOf(attr);
                else
                    return defaultValue;
            } catch (Exception e) {
                return defaultValue;
            }
        }

    }

    public class MyDoggyElementParser extends ElementParserAdapter {

        public boolean parse(Element element, Context context) {
            // Validate version
            if (!"1.5.0".equals(element.getAttribute("version")))
                throw new IllegalArgumentException("Invalid workspace version. Expected 1.5.0");

            // Sets content manager enable property...
            ContentManager contentManager = context.get(ToolWindowManager.class).getContentManager();
            contentManager.setEnabled(getBoolean(context, element, "contentManagerEnabled", true));

            return true;
        }
    }

    public class ToolWindowManagerDescriptorElementParser extends ElementParserAdapter {

        public boolean parse(Element element, Context context) {
            ToolWindowManagerDescriptor descriptor = context.get(ToolWindowManager.class).getToolWindowManagerDescriptor();

            descriptor.setNumberingEnabled(getBoolean(context, element, "numberingEnabled", true));
            descriptor.setPreviewEnabled(getBoolean(context, element, "previewEnabled", true));
            descriptor.setShowUnavailableTools(getBoolean(context, element, "showUnavailableTools", false));

            return true;
        }

    }

    public class PushAwayModeElementParser extends ElementParserAdapter {

        public boolean parse(Element element, Context context) {
            ToolWindowManagerDescriptor descriptor = context.get(ToolWindowManager.class).getToolWindowManagerDescriptor();

            // Load mode settings
            NodeList modes = element.getElementsByTagName("mode");
            for (int i = 0, size = modes.getLength(); i < size; i++) {
                Element mode = (Element) modes.item(i);
                if ("MOST_RECENT".equals(getAttributeValue(context, mode, "type", null))) {
                    MostRecentDescriptor mostRecentDescriptor = (MostRecentDescriptor) descriptor.getPushAwayModeDescriptor(PushAwayMode.MOST_RECENT);

                    NodeList anchors = element.getElementsByTagName("anchor");
                    for (int j = 0, sizej = anchors.getLength(); j < sizej; j++) {
                        Element anchor = (Element) anchors.item(j);
                        mostRecentDescriptor.append(ToolWindowAnchor.valueOf(anchor.getAttribute("type")));
                    }
                }
            }

            // Setup pushAwayMode
            if (isAttributePresent(element, "pushAwayMode"))
                descriptor.setPushAwayMode(PushAwayMode.valueOf(element.getAttribute("pushAwayMode")));

            return false;
        }

    }

    public class ToolWindowsElementParser extends ElementParserAdapter {
        protected MergePolicyApplier mergePolicyApplier;

        public boolean parse(Element element, Context context) {
            mergePolicyApplier = context.get(MergePolicyApplier.class);

            Element sharedWindowsElem = getElement(element, "sharedWindows");
            SharedWindows sharedWindows = new SharedWindows(context.get(ToolWindowManager.class),
                                                            context.get(PersistenceDelegateCallback.class));

            if (sharedWindowsElem != null) {
                NodeList sharedWindowsList = sharedWindowsElem.getElementsByTagName("sharedWindow");
                for (int i = 0, size = sharedWindowsList.getLength(); i < size; i++) {
                    Element sharedwindowElem = (Element) sharedWindowsList.item(i);

                    // Load dockable ids
                    NodeList dockabledIdList = sharedwindowElem.getElementsByTagName("dockable");
                    if (dockabledIdList.getLength() <= 0)
                        continue;

                    String[] ids = new String[dockabledIdList.getLength()];
                    for (int j = 0, sizej = dockabledIdList.getLength(); j < sizej; j++) {
                        ids[j] = ((Element) dockabledIdList.item(j)).getAttribute("id");
                    }

                    Element modelElement = getElement(element, "layout");
                    if (modelElement == null)
                        continue;

                    String text = modelElement.getTextContent();
                    XMLDecoder decoder = new XMLDecoder(new ByteArrayInputStream(text.getBytes()));
                    final MultiSplitLayout.Node model = (MultiSplitLayout.Node) decoder.readObject();

                    sharedWindows.addSharedWindow(model, ids);
                }
            }


            // Load toolwindows
            NodeList tools = element.getElementsByTagName("toolWindow");

            for (int i = 0, size = tools.getLength(); i < size; i++) {
                Element tool = (Element) tools.item(i);
                String toolId = tool.getAttribute("id");

                // load descriptors
                ToolWindow toolWindow = context.get(ToolWindowManager.class).getToolWindow(toolId);
                if (toolWindow == null)
                    toolWindow = context.get(PersistenceDelegateCallback.class).toolwindowNotFound(
                            context.get(ToolWindowManager.class),
                            toolId,
                            node.setElement(tool)
                    );

                if (toolWindow == null)
                    continue;

                Element typeElement = getElement(tool, "docked");
                if (typeElement != null) {
                    DockedTypeDescriptor descriptor = (DockedTypeDescriptor) toolWindow.getTypeDescriptor(ToolWindowType.DOCKED);

                    applyAttributes(context, typeElement, descriptor);
                    descriptor.setDockLength(getInteger(context, typeElement, "dockLength", 200));
                    descriptor.setMinimumDockLength(getInteger(context, typeElement, "minimumDockLength", 100));
                    descriptor.setPopupMenuEnabled(getBoolean(context, typeElement, "popupMenuEnabled", true));
                }

                typeElement = getElement(tool, "sliding");
                if (typeElement != null) {
                    SlidingTypeDescriptor descriptor = (SlidingTypeDescriptor) toolWindow.getTypeDescriptor(ToolWindowType.SLIDING);

                    applyAttributes(context, typeElement, descriptor);
                    descriptor.setTransparentDelay(getInteger(context, typeElement, "transparentDelay", 0));
                    descriptor.setTransparentMode(getBoolean(context, typeElement, "transparentMode", true));
                    descriptor.setTransparentRatio(getFloat(context, typeElement, "transparentRatio", 0.7f));
                }

                typeElement = getElement(tool, "floating");
                if (typeElement != null) {
                    FloatingTypeDescriptor descriptor = (FloatingTypeDescriptor) toolWindow.getTypeDescriptor(ToolWindowType.FLOATING);

                    applyAttributes(context, typeElement, descriptor);
                    descriptor.setTransparentDelay(getInteger(context, typeElement, "transparentDelay", 0));
                    descriptor.setTransparentMode(getBoolean(context, typeElement, "transparentMode", true));
                    descriptor.setTransparentRatio(getFloat(context, typeElement, "transparentRatio", 0.7f));
                    descriptor.setModal(getBoolean(context, typeElement, "modal", false));
                    descriptor.setAddToTaskBar(getBoolean(context, typeElement, "addToTaskBar", false));
                    descriptor.setAlwaysOnTop(getBoolean(context, typeElement, "alwaysOnTop", true));
                    descriptor.setOsDecorated(getBoolean(context, typeElement, "osDecorated", false));
                    descriptor.setResizable(getBoolean(context, typeElement, "resizable", true));

                    Element location = getElement(typeElement, "location");
                    if (location != null)
                        descriptor.setLocation(
                                getInteger(context, location, "x", 0),
                                getInteger(context, location, "y", 0)
                        );
                    Element dimension = getElement(typeElement, "size");
                    if (dimension != null)
                        descriptor.setSize(
                                getInteger(context, dimension, "width", 100),
                                getInteger(context, dimension, "height", 100)
                        );
                }

                typeElement = getElement(tool, "floatingLive");
                if (typeElement != null) {
                    FloatingLiveTypeDescriptor descriptor = (FloatingLiveTypeDescriptor) toolWindow.getTypeDescriptor(ToolWindowType.FLOATING_LIVE);

                    applyAttributes(context, typeElement, descriptor);
                    descriptor.setTransparentDelay(getInteger(context, typeElement, "transparentDelay", 0));
                    descriptor.setTransparentMode(getBoolean(context, typeElement, "transparentMode", true));
                    descriptor.setTransparentRatio(getFloat(context, typeElement, "transparentRatio", 0.7f));
                    descriptor.setResizable(getBoolean(context, typeElement, "resizable", true));

                    Element location = getElement(typeElement, "location");
                    if (location != null)
                        descriptor.setLocation(
                                getInteger(context, location, "x", 0),
                                getInteger(context, location, "y", 0)
                        );
                    Element dimension = getElement(typeElement, "size");
                    if (dimension != null)
                        descriptor.setSize(
                                getInteger(context, dimension, "width", 100),
                                getInteger(context, dimension, "height", 100)
                        );
                }

                typeElement = getElement(tool, "anchor");
                if (typeElement != null) {
                    RepresentativeAnchorDescriptor descriptor = toolWindow.getRepresentativeAnchorDescriptor();
                    descriptor.setPreviewEnabled(getBoolean(context, typeElement, "previewEnabled", true));
                    descriptor.setPreviewDelay(getInteger(context, typeElement, "previewDelay", 0));
                    descriptor.setPreviewTransparentRatio(getFloat(context, typeElement, "previewTransparentRatio", 0.7f));
                    descriptor.setTitle(typeElement.getAttribute("title"));
                    if (toolWindow.getType() != ToolWindowType.FLOATING_FREE)
                        descriptor.setVisible(getBoolean(context, typeElement, "visible", true));

                    descriptor.removeAllLockingAnchor();
                    Element lockingAnchorsElement = getElement(typeElement, "lockingAnchors");
                    if (lockingAnchorsElement != null) {
                        NodeList lockingAnchorList = lockingAnchorsElement.getElementsByTagName("lockingAnchor");
                        for (int j = 0, sizej = lockingAnchorList.getLength(); j < sizej; j++) {
                            Element lockingAnchorElement = (Element) lockingAnchorList.item(j);

                            descriptor.addLockingAnchor(ToolWindowAnchor.valueOf(lockingAnchorElement.getAttribute("anchor")));
                        }
                    }
                }


                // Set toolwindow properties

                ToolWindowType type = ToolWindowType.valueOf(tool.getAttribute("type"));
                if (type != ToolWindowType.EXTERN)
                    toolWindow.setType(type);

                toolWindow.setAutoHide(getBoolean(context, tool, "autoHide", false));
                toolWindow.setAvailable(getBoolean(context, tool, "available", false));
                int index = getInteger(context, tool, "index", -1);
                if (index != -1)
                    toolWindow.setIndex(index);
                toolWindow.setAggregateMode(getBoolean(context, tool, "aggregateMode", false));
                toolWindow.setFlashing(getBoolean(context, tool, "flashing", false));
                toolWindow.setLockedOnAnchor(getBoolean(context, tool, "lockedOnAnchor", false));
                toolWindow.setHideOnZeroTabs(getBoolean(context, tool, "hideOnZeroTabs", false));

                // Load tabs
                Element tabs = getElement(tool, "tabs");
                if (tabs != null) {
                    NodeList tabList = tabs.getElementsByTagName("tab");

                    // Compare tabLists
                    for (ToolWindowTab tab : toolWindow.getToolWindowTabs()) {
                        if (tab.getDockableDelegator() != null) {
                            String dockableId = tab.getDockableDelegator().getId();

                            for (int j = 0, sizej = tabList.getLength(); j < sizej; j++) {
                                Element tabElement = (Element) tabList.item(j);

                                if (dockableId.equals(tabElement.getAttribute("dockableId"))) {
                                    toolWindow.removeToolWindowTab(tab);
                                    break;
                                }
                            }
                        }
                    }

                    ToolWindowTab selectedTab = null;
                    ToolWindowTab maximizedTab = null;
                    for (int j = 0, sizej = tabList.getLength(); j < sizej; j++) {
                        Element tabElement = (Element) tabList.item(j);

                        String dockableId = tabElement.getAttribute("dockableId");
                        boolean selected = getBoolean(context, tabElement, "selected", false);
                        boolean maximized = getBoolean(context, tabElement, "maximized", false);

                        Dockable dockable = context.get(ToolWindowManager.class).getDockableById(dockableId);
                        if (dockable != null) {
                            ToolWindowTab tab = toolWindow.addToolWindowTab(dockable);

                            if (selected)
                                selectedTab = tab;

                            if (maximized)
                                maximizedTab = tab;

                            tab.setSelected(false);
                            tab.setMaximized(false);
                            tab.setCloseable(getBoolean(context, tabElement, "closeable", true));
                            tab.setMinimizable(getBoolean(context, tabElement, "minimizable", true));
                            tab.setFlashing(getBoolean(context, tabElement, "flashing", false));
                            tab.setMinimized(getBoolean(context, tabElement, "minimized", false));
                        }
                    }

                    if (maximizedTab != null)
                        maximizedTab.setMaximized(true);

                    if (selectedTab != null)
                        selectedTab.setSelected(true);
                } else {
                    for (ToolWindowTab tab : toolWindow.getToolWindowTabs()) {
                        if (tab.getDockableDelegator() != null)
                            toolWindow.removeToolWindowTab(tab);
                    }
                }
            }

            apply(context, tools, ToolWindowAnchor.LEFT, sharedWindows);
            apply(context, tools, ToolWindowAnchor.BOTTOM, sharedWindows);
            apply(context, tools, ToolWindowAnchor.RIGHT, sharedWindows);
            apply(context, tools, ToolWindowAnchor.TOP, sharedWindows);

            // Apply shared windows layout...
            sharedWindows.applyLayouts();

            return false;
        }

        protected void apply(final Context context, NodeList tools, ToolWindowAnchor anchor, SharedWindows sharedWindows) {
            // Filter tools by anchor
            java.util.List<Element> toolsByAnchor = new ArrayList<Element>();
            for (int i = 0, size = tools.getLength(); i < size; i++) {
                Element tool = (Element) tools.item(i);
                if (ToolWindowAnchor.valueOf(tool.getAttribute("anchor")) == anchor)
                    toolsByAnchor.add(tool);
            }

            Collections.sort(toolsByAnchor, new Comparator<Element>() {
                public int compare(Element o1, Element o2) {
                    int anchorIndex1 = getInteger(context, o1, "anchorIndex", 0);
                    int anchorIndex2 = getInteger(context, o2, "anchorIndex", 0);

                    ToolWindowType type1 = ToolWindowType.valueOf(o1.getAttribute("type"));
                    ToolWindowType type2 = ToolWindowType.valueOf(o2.getAttribute("type"));

                    if (type1 == ToolWindowType.SLIDING)
                        return 1;
                    if (type2 == ToolWindowType.SLIDING)
                        return -1;

                    if (anchorIndex1 < anchorIndex2)
                        return -1;
                    else if (anchorIndex1 == anchorIndex2)
                        return 0;
                    return 1;
                }
            });

            ToolWindow activeTool = null;
            ToolWindow maximizedTool = null;
            for (Element tool : toolsByAnchor) {
                String toolId = tool.getAttribute("id");

                ToolWindow toolWindow = context.get(ToolWindowManager.class).getToolWindow(toolId);
                if (toolWindow == null)
                    toolWindow = context.get(PersistenceDelegateCallback.class).toolwindowNotFound(
                            context.get(ToolWindowManager.class),
                            toolId,
                            node.setElement(tool)
                    );

                if (toolWindow == null)
                    continue;

                int anchorIndex = getInteger(context, tool, "anchorIndex", Integer.MIN_VALUE);
                ToolWindowAnchor toolWindowAnchor = ToolWindowAnchor.LEFT;
                if (isAttributePresent(tool, "anchor"))
                    toolWindowAnchor = ToolWindowAnchor.valueOf(tool.getAttribute("anchor"));

                if (anchorIndex == Integer.MIN_VALUE)
                    toolWindow.setAnchor(toolWindowAnchor);
                else
                    toolWindow.setAnchor(toolWindowAnchor,
                                         anchorIndex);

                mergePolicyApplier.applyToolWindow(toolWindow, node.setElement(tool), sharedWindows);

                if (getBoolean(context, tool, "active", false))
                    activeTool = toolWindow;

                if (getBoolean(context, tool, "maximized", false))
                    maximizedTool = toolWindow;
            }

            if (activeTool != null)
                ((MutableContext) context).put(ContextKey.ActiveTool, activeTool);

            if (maximizedTool != null)
                maximizedTool.setMaximized(true);
        }

        protected void applyAttributes(Context context, Element element, ToolWindowTypeDescriptor descriptor) {
            descriptor.setAnimating(getBoolean(context, element, "animating", true));
            descriptor.setAutoHide(getBoolean(context, element, "autoHide", false));
            descriptor.setEnabled(getBoolean(context, element, "enabled", true));
            descriptor.setHideRepresentativeButtonOnVisible(getBoolean(context, element, "hideRepresentativeButtonOnVisible", false));
            descriptor.setIdVisibleOnTitleBar(getBoolean(context, element, "idVisibleOnTitleBar", true));
            descriptor.setTitleBarButtonsVisible(getBoolean(context, element, "titleBarButtonsVisible", true));
            descriptor.setTitleBarVisible(getBoolean(context, element, "titleBarVisible", true));
        }
    }

    public class ContentManagerElementParser extends ElementParserAdapter {

        public boolean parse(Element element, final Context context) {
            // Load shared windows...
            Element sharedWindowsElem = getElement(element, "sharedWindows");
            SharedWindows sharedWindows = new SharedWindows(context.get(ToolWindowManager.class).getContentManager(),
                                                            context.get(PersistenceDelegateCallback.class));

            if (sharedWindowsElem != null) {
                NodeList sharedWindowsList = sharedWindowsElem.getElementsByTagName("sharedWindow");
                for (int i = 0, size = sharedWindowsList.getLength(); i < size; i++) {
                    Element sharedwindowElem = (Element) sharedWindowsList.item(i);

                    // Load dockable ids
                    NodeList dockabledIdList = sharedwindowElem.getElementsByTagName("dockable");
                    if (dockabledIdList.getLength() <= 0)
                        continue;

                    String[] ids = new String[dockabledIdList.getLength()];
                    for (int j = 0, sizej = dockabledIdList.getLength(); j < sizej; j++) {
                        ids[j] = ((Element) dockabledIdList.item(j)).getAttribute("id");
                    }

                    Element modelElement = getElement(element, "layout");
                    if (modelElement == null)
                        continue;

                    String text = modelElement.getTextContent();
                    XMLDecoder decoder = new XMLDecoder(new ByteArrayInputStream(text.getBytes()));
                    final MultiSplitLayout.Node model = (MultiSplitLayout.Node) decoder.readObject();

                    sharedWindows.addSharedWindow(model, ids);
                }
            }


            // Load contents..
            NodeList contents = element.getElementsByTagName("content");

            Content selectedContent = null;
            Content maximizedContent = null;

            // load contents properties
            for (int i = 0, size = contents.getLength(); i < size; i++) {
                Element contentElement = (Element) contents.item(i);

                String contentId = contentElement.getAttribute("id");

                Content content = context.get(ToolWindowManager.class).getContentManager().getContent(contentId);
                if (content == null)
                    content = context.get(PersistenceDelegateCallback.class).contentNotFound(context.get(ToolWindowManager.class),
                                                                                             contentId,
                                                                                             node.setElement(contentElement));

                if (content != null) {
                    if (getBoolean(context, contentElement, "selected", false))
                        selectedContent = content;
                    if (getBoolean(context, contentElement, "maximized", false))
                        maximizedContent = content;

                    content.setEnabled(getBoolean(context, contentElement, "enabled", true));
                    boolean detached = getBoolean(context, contentElement, "detached", false);
                    content.setMaximized(false);
                    content.setMinimized(getBoolean(context, contentElement, "minimized", false));
                    content.setFlashing(getBoolean(context, contentElement, "flashing", false));

                    // Load "minimizedState" sub element if any
                    Element minimizedStateElem = getElement(contentElement, "minimizedState");
                    if (minimizedStateElem != null && content.isMinimized()) {
                        ContentDescriptor contentDescriptor = (ContentDescriptor) toolWindowManager.getDockableDescriptor(content.getId());
                        contentDescriptor.setAnchor(getToolWindowAnchor(context, minimizedStateElem, "anchor", ToolWindowAnchor.LEFT),
                                                    getInteger(context, minimizedStateElem, "anchorIndex", -1)
                        );
                    }


                    // Load contentUI
                    ContentUI contentUI = content.getContentUI();
                    contentUI.setCloseable(getBoolean(context, contentElement, "closeable", true));
                    contentUI.setDetachable(getBoolean(context, contentElement, "detachable", true));
                    contentUI.setMinimizable(getBoolean(context, contentElement, "minimizable", true));
                    contentUI.setMaximizable(getBoolean(context, contentElement, "maximizable", true));
                    contentUI.setTransparentMode(getBoolean(context, contentElement, "transparentMode", true));
                    contentUI.setTransparentDelay(getInteger(context, contentElement, "transparentDelay", 0));
                    contentUI.setTransparentRatio(getFloat(context, contentElement, "transparentRatio", 0.7f));
                    contentUI.setAddToTaskBarWhenDetached(getBoolean(context, contentElement, "addToTaskBarWhenDetached", false));
                    contentUI.setAlwaysOnTop(getBoolean(context, contentElement, "alwaysOnTop", false));

                    // Load detachedBounds if any
                    NodeList list = contentElement.getElementsByTagName("detachedBounds");
                    if (list.getLength() > 0) {
                        Element detachedBoundsElm = (Element) list.item(0);
                        contentUI.setDetachedBounds(new Rectangle(getInteger(context, detachedBoundsElm, "x", 100),
                                                                  getInteger(context, detachedBoundsElm, "y", 100),
                                                                  getInteger(context, detachedBoundsElm, "width", 320),
                                                                  getInteger(context, detachedBoundsElm, "height", 200)));
                    }

                    if (detached) {
                        if (sharedWindows.isInSharedWindow(content)) {
                            Dockable refDockable = sharedWindows.getRefDockable(content);

                            if (refDockable != null)
                                content.detachByReference((Content) refDockable, AggregationPosition.DEFAULT);
                            else
                                content.setDetached(true);
                        } else
                            content.setDetached(true);
                    } else
                        content.setDetached(false);
                }
            }

            // Load layout
            Element modelElement = getElement(element, "layout");
            if (modelElement != null && !toolWindowManager.getContentManager().isEnabled()) {
                String text = modelElement.getTextContent();
                XMLDecoder decoder = new XMLDecoder(new ByteArrayInputStream(text.getBytes()));
                final MultiSplitLayout.Node model = (MultiSplitLayout.Node) decoder.readObject();

                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        MultiSplitDockableContainer dockableContainer = (MultiSplitDockableContainer) ((DockableDropPanel) context.get(MyDoggyToolWindowManager.class).getMainContent()).getComponent();
                        dockableContainer.setMultiSplitLayout(model);
                    }
                });
            } else if (context.get(ContextKey.MultiSplitContentManagerUILayout) != null && toolWindowManager.getContentManager().isEnabled()) {
                // Load MultiSplitContentManagerUILayout

                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        MyDoggyMultiSplitContentManagerUI myDoggyMultiSplitContentManagerUI = (MyDoggyMultiSplitContentManagerUI) toolWindowManager.getContentManager().getContentManagerUI();
                        myDoggyMultiSplitContentManagerUI.setLayout(context.get(ContextKey.MultiSplitContentManagerUILayout));
                    }
                });
            }

            final Content selectedContent1 = selectedContent;
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    if (selectedContent1 != null)
                        selectedContent1.setSelected(true);
                }
            });

            if (maximizedContent != null)
                maximizedContent.setMaximized(true);

            return false;
        }
    }

    public class ToolWindowBarElementParser extends ElementParserAdapter {

        public boolean parse(Element element, final Context context) {
            final ToolWindowAnchor anchor = ToolWindowAnchor.valueOf(element.getAttribute("anchor"));

            // load toolWindowBar properties
            ToolWindowBar toolWindowBar = context.get(ToolWindowManager.class).getToolWindowBar(anchor);
            toolWindowBar.setDividerSize(getInteger(context, element, "left", 3));
            toolWindowBar.setAggregateMode(getBoolean(context, element, "aggregateMode", false));

            Element layoutElement = getElement(element, "layout");
            if (layoutElement != null) {
                String layoutAsText = layoutElement.getTextContent();
                XMLDecoder decoder = new XMLDecoder(new ByteArrayInputStream(layoutAsText.getBytes()));

                final MultiSplitLayout.Node layout = (MultiSplitLayout.Node) decoder.readObject();

                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        ((MyDoggyToolWindowBar) context.get(ToolWindowManager.class).getToolWindowBar(anchor)).setLayout(layout);
                    }
                });
            }

            return false;
        }

    }

    public class MultiSplitContentManagerUIElementParser extends ElementParserAdapter {

        public boolean parse(Element element, Context context) {
            if (context.get(ToolWindowManager.class).getContentManager().getContentManagerUI() instanceof MultiSplitContentManagerUI) {
                MultiSplitContentManagerUI managerUI = (MultiSplitContentManagerUI) context.get(ToolWindowManager.class).getContentManager().getContentManagerUI();

                managerUI.setCloseable(getBoolean(context, element, "closeable", true));
                managerUI.setDetachable(getBoolean(context, element, "detachable", false));
                managerUI.setMinimizable(getBoolean(context, element, "minimizable", false));
                managerUI.setMaximizable(getBoolean(context, element, "maximizable", false));
                managerUI.setPopupMenuEnabled(getBoolean(context, element, "popupMenuEnabled", true));
                managerUI.setShowAlwaysTab(getBoolean(context, element, "showAlwaysTab", false));
                managerUI.setTabLayout(TabbedContentManagerUI.TabLayout.valueOf(element.getAttribute("tabLayout")));
                managerUI.setTabPlacement(TabbedContentManagerUI.TabPlacement.valueOf(element.getAttribute("tabPlacement")));

                ContentManager contentManager = context.get(ToolWindowManager.class).getContentManager();

                // Load contents
                Element contents = getElement(element, "contents");
                if (contents != null) {
                    NodeList contentUIElms = contents.getElementsByTagName("content");
                    for (int i = 0, size = contentUIElms.getLength(); i < size; i++) {
                        Element contentUIElm = (Element) contentUIElms.item(i);

                        String contentId = contentUIElm.getAttribute("id");
                        Content content = contentManager.getContent(contentId);
                        if (content == null)
                            content = context.get(PersistenceDelegateCallback.class).contentNotFound(
                                    context.get(ToolWindowManager.class),
                                    contentId,
                                    node.setElement(contentUIElm)
                            );

                        if (content != null) {
                            MultiSplitContentUI multiSplitContentUI = (MultiSplitContentUI) content.getContentUI();
                            multiSplitContentUI.setShowAlwaysTab(getBoolean(context, contentUIElm, "showAlwaysTab", true));
                        }
                    }
                }

                // Load layout
                Element layout = getElement(element, "layout");
                if (layout != null) {
                    String text = layout.getTextContent();
                    XMLDecoder decoder = new XMLDecoder(new ByteArrayInputStream(text.getBytes()));
                    ((MutableContext) context).put(ContextKey.MultiSplitContentManagerUILayout, decoder.readObject());
                } else
                    ((MutableContext) context).put(ContextKey.MultiSplitContentManagerUILayout, null);
            }

            return false;
        }

    }

    public class TabbedContentManagerUIElementParser extends ElementParserAdapter {

        public boolean parse(Element element, Context context) {
            if (context.get(ToolWindowManager.class).getContentManager().getContentManagerUI() instanceof TabbedContentManagerUI) {
                TabbedContentManagerUI managerUI = (TabbedContentManagerUI) context.get(ToolWindowManager.class).getContentManager().getContentManagerUI();

                managerUI.setCloseable(getBoolean(context, element, "closeable", true));
                managerUI.setDetachable(getBoolean(context, element, "detachable", false));
                managerUI.setMinimizable(getBoolean(context, element, "minimizable", false));
                managerUI.setMaximizable(getBoolean(context, element, "maximizable", false));
                managerUI.setPopupMenuEnabled(getBoolean(context, element, "popupMenuEnabled", true));
                managerUI.setShowAlwaysTab(getBoolean(context, element, "showAlwaysTab", false));
                managerUI.setTabLayout(TabbedContentManagerUI.TabLayout.valueOf(element.getAttribute("tabLayout")));
                managerUI.setTabPlacement(TabbedContentManagerUI.TabPlacement.valueOf(element.getAttribute("tabPlacement")));
            }

            return false;
        }
    }

    public class DekstopManagerUIElementParser extends ElementParserAdapter {

        public boolean parse(Element element, Context context) {
            if (context.get(ToolWindowManager.class).getContentManager().getContentManagerUI() instanceof DesktopContentManagerUI) {
                DesktopContentManagerUI managerUI = (DesktopContentManagerUI) context.get(ToolWindowManager.class).getContentManager().getContentManagerUI();

                managerUI.setCloseable(getBoolean(context, element, "closeable", true));
                managerUI.setDetachable(getBoolean(context, element, "detachable", false));
                managerUI.setMinimizable(getBoolean(context, element, "minimizable", false));
                managerUI.setMaximizable(getBoolean(context, element, "maximizable", false));
                managerUI.setPopupMenuEnabled(getBoolean(context, element, "popupMenuEnabled", true));

                ContentManager contentManager = context.get(ToolWindowManager.class).getContentManager();

                NodeList contentUIElms = element.getElementsByTagName("content");
                for (int i = 0, size = contentUIElms.getLength(); i < size; i++) {
                    Element contentUIElm = (Element) contentUIElms.item(i);

                    String contentId = contentUIElm.getAttribute("id");
                    Content content = contentManager.getContent(contentId);
                    if (content == null)
                        content = context.get(PersistenceDelegateCallback.class).contentNotFound(context.get(ToolWindowManager.class),
                                                                                                 contentId,
                                                                                                 node.setElement(contentUIElm));

                    if (content != null) {
                        DesktopContentUI desktopContentUI = (DesktopContentUI) content.getContentUI();
                        desktopContentUI.setIconified(getBoolean(context, contentUIElm, "iconified", false));
                        desktopContentUI.setLocation(
                                getInteger(context, contentUIElm, "x", 0),
                                getInteger(context, contentUIElm, "y", 0)
                        );
                        desktopContentUI.setSize(
                                getInteger(context, contentUIElm, "width", 100),
                                getInteger(context, contentUIElm, "height", 1000)
                        );
                    }
                }
            }

            return false;
        }

    }

}
