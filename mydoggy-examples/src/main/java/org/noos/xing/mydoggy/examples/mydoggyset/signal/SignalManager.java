package org.noos.xing.mydoggy.examples.mydoggyset.signal;

public class SignalManager {

    private static final SignalManager INSTANCE = new SignalManager();

    public static SignalManager getInstance() {
        return INSTANCE;
    }

    private SignalTreeNode root;            // La root dell'albero dei segnali

    protected SignalManager() {
        root = new SignalTreeNode("root");
    }

    public void sendEvent(Object signal, Object message) {
        String strSignal = signal.toString();
        root.deliverEvent(strSignal, new SignalEvent(strSignal, message));
    }

    public void addSignalListener(Object signal, SignalListener eventListener) {
        String strSignal = signal.toString();
        if (signal.equals("*"))
            root.addListener(eventListener);
        else
            root.getSubNode(strSignal).addListener(eventListener);
    }

/*
    public void removeSignalListener(String signal, SignalListener eventListener) {
        if (signal.equals("*")) {
            root.removeListener(eventListener);
        } else {
            SignalTreeNode signalTreeNode = root.getSubNode(signal);
            signalTreeNode.removeListener(eventListener);

            pruneNode(signalTreeNode);
        }
    }

    private void pruneNode(SignalTreeNode signalTreeNode) {
        while (signalTreeNode != null && signalTreeNode != root && signalTreeNode.numNodes() == 0 && signalTreeNode.numListeners() == 0) {
            signalTreeNode.getParent().removeNode(signalTreeNode.getSignalName());
            signalTreeNode = signalTreeNode.getParent();
        }
    }
*/

}