package org.noos.xing.mydoggy;

/**
 * Every tool window has a type that specifies the visual behaviours of the tool.
 *
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 * @since 1.0.0
 */
public enum ToolWindowType {

    /**
     * When DOCKED type is used, the tool window shares frame space and fixes to a docking
     * area along one of the tool window bars (the one containing the corresponding anchor button).
     *
     * @see org.noos.xing.mydoggy.DockedTypeDescriptor
     * @since 1.0.0
     */
    DOCKED,

    /**
     * When SLIDING type is used, the tool window overlaps the main frame and/or other tool windows.
     * When it loses focus, the tool window hides itself.
     *
     * @since 1.0.0
     */
    SLIDING,

    /**
     * This type enables a tool window to be detached from the main window frame.
     * When Floating type is used, the tool window detaches to the position where it was last floated
     * (or screen center or location setted for toolwindow's FloatingTypeDescriptor, if never before floated)
     *
     * @see org.noos.xing.mydoggy.FloatingTypeDescriptor
     * @since 1.0.0
     */
    FLOATING,

    /**
     * This type differs from FLOATING type for the absence of the representative button
     * on the tool window bar.
     *
     * @see org.noos.xing.mydoggy.ToolWindowAnchor
     * @since 1.0.0
     */
    FLOATING_FREE,

    /**
     * This type enables a tool to be floating in the frame rather than having
     * floating windows "out of the frame".
     *
     * @see FloatingLiveTypeDescriptor
     * @since 1.3.2
     */
    FLOATING_LIVE,

    /**
     * When a toolwindow is added to another tool as a tab, that tool acquire this type.
     * The user cannot set directly this type.
     *
     * @see ToolWindow#addToolWindowTab(Dockable)
     * @since 1.3.2
     */
    EXTERN,

}
