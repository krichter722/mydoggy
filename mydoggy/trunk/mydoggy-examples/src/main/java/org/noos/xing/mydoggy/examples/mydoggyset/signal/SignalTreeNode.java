package org.noos.xing.mydoggy.examples.mydoggyset.signal;

import javax.swing.event.EventListenerList;
import java.util.*;

class SignalTreeNode {
    private String signalName = null;

    private SignalTreeNode parent;
    private Hashtable<String, SignalTreeNode> nodes;
    private EventListenerList listeners;

    public SignalTreeNode(String signalName) {
        if (signalName == null)
            throw new IllegalArgumentException("Node name cannot be null.");

        nodes = new Hashtable<String, SignalTreeNode>();
        listeners = new EventListenerList();
        this.signalName = signalName;
    }

    // Add, Remore Node
    public synchronized boolean addNode(SignalTreeNode signalTreeNode) {
        if (signalTreeNode == null)
            throw new IllegalArgumentException("Cannot add a null node.");

        if (nodes.get(signalTreeNode.getSignalName()) != null) {
            // Il nodo esiste già
            return false;
        } else {
            // Viene inserito il nodo
            nodes.put(signalTreeNode.getSignalName(), signalTreeNode);
            signalTreeNode.setParent(this);
            return true;
        }
    }

    public synchronized void removeNode(String s) {
        nodes.remove(s);
    }

    public void addListener(SignalListener signalListener) {
        listeners.add(SignalListener.class, signalListener);
    }

    public void removeListener(SignalListener signalListener) {
        listeners.remove(SignalListener.class, signalListener);
    }

    public int numNodes() {
        return nodes.size();
    }

    public int numListeners() {
        return listeners.getListenerCount(SignalListener.class);
    }

    public SignalTreeNode getParent() {
        return parent;
    }

    public void setParent(SignalTreeNode parent) {
        this.parent = parent;
    }

    public SignalTreeNode getSubNode(String signal) {
        return getSubNode(this, SignalUtil.parseSignals(signal));
    }

    public SignalTreeNode getSubNode(List signalTokensList) {
        return getSubNode(this, signalTokensList);
    }

    private synchronized SignalTreeNode getSubNode(SignalTreeNode current, List signalTokensList) {
        SignalTreeNode signalTreeNode;

        while (true) {
            String signal = (String) signalTokensList.get(1);
            signalTreeNode = current.getNode(signal);
            if (signalTreeNode == null) {
                signalTreeNode = new SignalTreeNode(signal);
                current.addNode(signalTreeNode);
            }
            signalTokensList.remove(1);
            if (signalTokensList.size() > 1) {
                current = signalTreeNode;
            } else
                break;
        }
        return signalTreeNode;
    }

    public String getSignalName() {
        return signalName;
    }

    public Enumeration<SignalTreeNode> getNodes() {
        return nodes.elements();
    }

    public SignalTreeNode getNode(String signal) {
        if (signal == null)
            throw new IllegalArgumentException("Signal cannot be null.");
        return nodes.get(signal);
    }

    public SignalListener[] getSignalListeners() {
        return listeners.getListeners(SignalListener.class);
    }

    // Event
    public void deliverEvent(String signal, SignalEvent event) {
        if (signal.equals("*"))
            deliverEvent(this, new ArrayList(0), signal, event);
        else
            deliverEvent(this, SignalUtil.parseSignals(signal), signal, event);
    }

    protected void deliverEvent(SignalTreeNode current, List signalTokensList, String signal, SignalEvent event) {
        while (true) {
            for (SignalListener signalListener : current.getSignalListeners()) {
                signalListener.handleSignalEvent(signal, event);
                if (event.isConsumed())
                    break;
            }

            if (event.isConsumed())
                break;

            if (signalTokensList.size() > 1) {
                String intermediateSignal = (String) signalTokensList.get(1);
                signalTokensList.remove(0);
                SignalTreeNode signalTreeNode = current.getNode(intermediateSignal);
                if (signalTreeNode != null) {
                    current = signalTreeNode;
                } else
                    break;
            } else
                break;
        }
    }

    public String toString() {
        return "SignalTreeNode [name=" + getSignalName() + "]";
    }

}