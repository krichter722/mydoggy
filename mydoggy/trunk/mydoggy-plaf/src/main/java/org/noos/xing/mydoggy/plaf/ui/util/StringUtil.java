package org.noos.xing.mydoggy.plaf.ui.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.NumberFormat;
import java.util.StringTokenizer;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class StringUtil {
    public static final String EMPTY_STRING = "";

    private StringUtil() {
    }

    public static String format(double value) {
        return format(value, true);
    }

    public static String format(double value, boolean groupingUsed) {
        NumberFormat numberFormat = NumberFormat.getNumberInstance();
        numberFormat.setGroupingUsed(groupingUsed);
        return numberFormat.format(value);
    }

    public static void pad(StringBuffer sb, String s, int i) {
        int j = s.length();
        if (j == i) {
            sb.append(s);
            return;
        }
        if (j > i) {
            sb.append(s.substring(0, i));
            return;
        }
        sb.append(s.substring(0, j));
        while (j++ < i)
            sb.append(' ');
    }

    public static String pad(String s, int i) {
        int j = s.length();
        if (j == i)
            return s;
        if (j > i)
            return s.substring(0, i);
        char[] ac = new char[i];
        s.getChars(0, j, ac, 0);
        while (j < i)
            ac[j++] = ' ';
        return new String(ac);
    }

    public static String truncate(String s, int i) {
        if (s.length() <= i)
            return s;
        else
            return s.substring(0, i);
    }

    public static String abbreviate(String s, int max) {
        if (s.length() < max) return s;
        return s.substring(0, max - 3) + "...";
    }

    public static String abbreviateNicely(String s, int i) {
        if (i <= 3) {
            StringBuffer stringbuffer = new StringBuffer(i);
            for (int k = 0; k < i; k++)
                stringbuffer.append(".");
            return stringbuffer.toString();
        }
        int j = i - 3;
        StringBuffer sb = new StringBuffer();
        StringTokenizer tokenizer = new StringTokenizer(s);
        while (tokenizer.hasMoreTokens()) {
            String s1 = tokenizer.nextToken();
            int l = sb.length();
            if (s1.length() + l + 1 <= j) {
                if (l == 0) {
                    sb.append(s1);
                } else {
                    sb.append(" ");
                    sb.append(s1);
                }
                continue;
            }
            if (sb.length() == 0)
                sb.append(s.substring(0, j));
            sb.append("...");
            break;
        }
        return sb.toString();
    }

    public static String zerofill(int x, int d) {
        String s = "";
        switch (d) {
            case 7:
                if (x < 1000000) s += "0";
            case 6:
                if (x < 100000) s += "0";
            case 5:
                if (x < 10000) s += "0";
            case 4:
                if (x < 1000) s += "0";
            case 3:
                if (x < 100) s += "0";
            case 2:
                if (x < 10) s += "0";
        }
        return s + x;
    }

    public static String fill(String source, int length, char car) {
        if (source.length() == length)
            return source;
        else {
            StringBuffer sb = new StringBuffer(source);
            for (int i = 0; i < length - source.length(); i++) sb.append(car);
            return sb.toString();
        }
    }

    public static void printIndent(PrintWriter out, int indent) {
        out.print(indent(indent));
    }

    public static void printIndent(int indent) {
        System.out.print(indent(indent));
    }

    public static String indent(int indent) {
        switch (indent) {
            case 8:
                return "                               ";
            case 7:
                return "                           ";
            case 6:
                return "                       ";
            case 5:
                return "                   ";
            case 4:
                return "               ";
            case 3:
                return "           ";
            case 2:
                return "       ";
            case 1:
                return "   ";
            default:
                StringBuffer buf = new StringBuffer();
                for (int i = 0; i < indent; ++i) {
                    buf.append("    ");
                }
                return buf.toString();
        }
    }

    public static String strdiff(String s1, String s2) {
        int i;
        for (i = 0; i < s1.length() && i < s2.length(); ++i) {
            if (s1.charAt(i) != s2.charAt(i)) {
                break;
            }
        }
        if (i < s2.length())
            return s2.substring(i);
        return "";
    }

    public static int count(String s, char ch) {
        int c = 0;
        for (int i = 0; i < s.length(); ++i) {
            if (s.charAt(i) == ch) c++;
        }
        return c;
    }

    public static String englishPluralize(String s) {
        if (s.endsWith("y"))
            return s.substring(0, s.length() - 1) + "ies";
        else if (s.endsWith("s"))
            return s + "es";
        else
            return s + 's';
    }

    public static String getStackTrace(Throwable t) {
        StringWriter s = new StringWriter();
        PrintWriter p = new PrintWriter(s);
        t.printStackTrace(p);
        p.close();
        return s.toString();
    }

    public static String quotedString(String from, String start, String end) {
        int startPos = from.indexOf(start);
        int endPos = from.indexOf(end);

        if (startPos > endPos)
            return null;

        if (startPos == -1) //  start not found
            return null;
        else if (endPos == -1)
            return null;
        else
            return from.substring(startPos, endPos + 1);
    }

    public static String replace(String str, String from, String to) {
        int startPos = str.indexOf(from);
        if (startPos == -1)
            return str;
        StringBuffer sb = new StringBuffer(str);
        sb.replace(startPos, startPos + from.length(), to);
        return sb.toString();
    }

    public static final String afterLast(String from, char ch) {
        int lastPos = from.lastIndexOf(ch);
        if (lastPos == -1)
            return from;
        else
            return from.substring(lastPos + 1, from.length());
    }

    public static final String beforeLast(String from, char ch) {
        int lastPos = from.lastIndexOf(ch);
        if (lastPos == -1)
            return "";
        else
            return from.substring(0, lastPos);
    }

    public static String capitalize(String s) {
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }

    public static String beanalize(String s) {
        if (s.length() == 1)
            return s.toUpperCase();
        else if (Character.isUpperCase(s.charAt(1)))
            return s;
        else
            return s.substring(0, 1).toUpperCase() + s.substring(1);
    }

    public static String uncapitalize(String s) {
        return s.substring(0, 1).toLowerCase() + s.substring(1);
    }

    public static String removeExtraSpaces(String s) {
        while (true) {
            int idx = s.indexOf("  ");
            if (idx > -1) {
                s = s.substring(0, idx + 1) + s.substring(idx + 2);
            } else {
                break;
            }
        }
        if (s.startsWith(" ")) s = s.substring(1);
        return s;
    }

    public static String removeExtraCharacter(String str, char aChar) {
        while (true) {
            int idx = str.indexOf(aChar);
            if (idx > -1) {
                str = str.substring(0, idx) + str.substring(idx + 1);
            } else {
                break;
            }
        }
        return str;
    }

    public static boolean isBlank(String buf) {
        if (buf == null)
            return false;
        int len = buf.length();
        for (int i = 0; i < len; i++) {
            if (!Character.isWhitespace(buf.charAt(i)))
                return false;
        }
        return true;
    }

    public static boolean isUpperCase(String name) {
        for (int i = 0; i < name.length(); i++) {
            if (Character.isLowerCase(name.charAt(i))) return false;
        }
        return true;
    }

    public static final String safeToString(Object o) {
        return o != null ? o.toString() : null;
    }

    public static String bytes2MBytes(long nBytes) {
        long MBytes = nBytes >> 10 >> 10;
        return MBytes > 0 ? Long.toString(MBytes) : "0";
    }
}