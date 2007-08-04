package org.noos.xing.mydoggy.examples.mydoggyset.signal;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.StringTokenizer;

public class SignalUtil {
    private static Hashtable signalsMap = new Hashtable();

    private SignalUtil() {
    }

    public static List parseSignals(String signal) {
        List signalTokensList = (List) signalsMap.get(signal);
        if (signalTokensList == null) {
            StringTokenizer st = new StringTokenizer(signal, ".");
            signalTokensList = new ArrayList(st.countTokens());
            for (; st.hasMoreTokens(); signalTokensList.add(st.nextToken())) ;
            signalsMap.put(signal, signalTokensList);
        }
        return (List) ((ArrayList) signalTokensList).clone();
    }

    public static String assembleSignals(List signalTokensList) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < signalTokensList.size(); i++) {
            sb.append((String) signalTokensList.get(i));
            if (i + 1 < signalTokensList.size())
                sb.append(".");
        }
        return sb.toString();
    }

}
