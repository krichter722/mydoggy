package org.noos.xing.mydoggy.plaf.persistence.xml;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.*;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class XMLCharacterEncoder extends Writer {
    private Charset charset;
    private CharsetEncoder encoder;
    private Writer writer = null;
    private OutputStream os = null;

    private boolean doEscape = false;
    private boolean hasFinished = false;


    public XMLCharacterEncoder(OutputStream os, String encoding) throws UnsupportedEncodingException {
        try {
            charset = Charset.forName(encoding);
            encoder = charset.newEncoder();
            encoder.onMalformedInput(CodingErrorAction.REPORT);
            encoder.onUnmappableCharacter(CodingErrorAction.REPORT);
            encoder.reset();
        }
        catch (IllegalCharsetNameException e) {
            UnsupportedEncodingException ee =
                    new UnsupportedEncodingException("encoding name is illegal: " + encoding);
            ee.initCause(e);
            throw ee;
        }
        catch (UnsupportedCharsetException e) {
            UnsupportedEncodingException ee =
                    new UnsupportedEncodingException("encoding is not available: " + encoding);
            ee.initCause(e);
            throw ee;
        }
        catch (UnsupportedOperationException e) {
            UnsupportedEncodingException ee =
                    new UnsupportedEncodingException("encoding is not available: " + encoding);
            ee.initCause(e);
            throw ee;
        }

        this.os = os;
    }


    /**
     * Constructs an XMLCharacterEncoder which writes to the given
     * {@link java.io.Writer}. Does not encode or escape.
     *
     * @param writer the {@link java.io.Writer} to write to.
     */
    public XMLCharacterEncoder(Writer writer) {
        charset = null;
        encoder = null;

        this.writer = writer;
    }


    /**
     * Enable escaping with XML character entites. In effect until
     * {@link #disableEscaping()} is invoked.
     * <p/>
     * <em>Note:</em> Escaping is disabled at start.
     */
    public void enableEscaping() {
        doEscape = true;
    }


    /**
     * Disable escaping with XML character entites. In effect until
     * {@link #enableEscaping()} is invoked.
     * <p/>
     * <em>Note:</em> Escaping is disabled at start.
     */
    public void disableEscaping() {
        doEscape = false;
    }

    // java.io.Writer implementation


    public void write(int c)
            throws IOException {
        if (writer != null) {
            writer.write(c);
        } else {
            CharBuffer in = CharBuffer.allocate(1);
            in.put((char) c);
            in.rewind();
            encodeWrite(in);
        }
    }


    public void write(char cbuf[])
            throws IOException {
        if (writer != null) {
            writer.write(cbuf);
        } else {
            encodeWrite(CharBuffer.wrap(cbuf));
        }
    }


    public void write(char cbuf[], int off, int len)
            throws IOException {
        if (writer != null) {
            writer.write(cbuf, off, len);
        } else {
            encodeWrite(CharBuffer.wrap(cbuf, off, len));
        }
    }


    public void write(String str)
            throws IOException {
        if (writer != null) {
            writer.write(str);
        } else {
            encodeWrite(CharBuffer.wrap(str));
        }
    }


    public void write(String str, int off, int len)
            throws IOException {
        if (writer != null) {
            writer.write(str, off, len);
        } else {
            encodeWrite(CharBuffer.wrap(str, off, off + len));
        }
    }


    public void flush()
            throws IOException {
        if (writer != null) {
            writer.flush();
        } else {
            os.flush();
        }
    }


    /**
     * Finish encoding and flush output, without closing underlaying stream.
     */
    public void finish()
            throws IOException {
        if (hasFinished) return;

        _finish();
        flush();
    }


    private void _finish()
            throws IOException {
        if (hasFinished) return;

        if (writer != null) {
            // nothing to do
        } else {
            byte[] buf = new byte[16];
            ByteBuffer out = ByteBuffer.wrap(buf);

            while (true) {
                CoderResult cr = encoder.flush(out);
                if (cr.isUnderflow()) {
                    break;
                } else if (cr.isOverflow()) {
                    os.write(buf, 0, out.position());
                    out.clear();
                }
            }
            if (out.position() > 0) os.write(buf, 0, out.position());
        }

        hasFinished = true;
    }


    public void close()
            throws IOException {
        _finish();

        if (writer != null) {
            writer.close();
        } else {
            os.close();
        }
    }


    private void encodeWrite(CharBuffer in)
            throws IOException {
        int size = doEscape
                   ? (int) (in.remaining() * encoder.averageBytesPerChar() * 1.1)
                   : (int) (in.remaining() * encoder.averageBytesPerChar());

        if (size % 2 > 0) size++; // make size even
        if (size < 256) size = 256;

        byte[] buf = new byte[size];
        ByteBuffer out = ByteBuffer.wrap(buf);

        CoderResult cr;
        while (true) {
            cr = encoder.encode(in, out, true);
            if (cr.isUnderflow()) {
                if (in.hasRemaining())
                    throw new CharConversionException(
                            "Malformed Unicode character: remaining input at underflow");
                else
                    break;
            } else if (cr.isOverflow()) {
                os.write(buf, 0, out.position());
                out.clear();
            } else if (cr.isUnmappable()) {
                if (doEscape) {
                    os.write(buf, 0, out.position());
                    out.clear();
                    for (int i = 0; i < cr.length(); i++) {
                        String entity = "&#x" + Integer.toHexString((int) in.get()) + ";";
                        disableEscaping();
                        encodeWrite(CharBuffer.wrap(entity));
                        enableEscaping();
                    }
                } else {
                    throw new CharConversionException(
                            "Unmappable Unicode character \\u"
                            + Integer.toHexString((int) in.get())
                            + " in context where escaping is not possible");
                }
            } else // if (cr.isMalformed())
            {
                throw new CharConversionException(
                        "Malformed Unicode character: \\u" + Integer.toHexString((int) in.get()));
            }
        }
        os.write(buf, 0, out.position());
    }

    /**
     * For testing only.
     */
/*    public static void main(String[] args)
    throws Exception
{
    String encoding = args[0];

    XMLCharacterEncoder xce = new XMLCharacterEncoder(System.out, encoding);
    xce.enableEscaping();
    xce.write(args[1]);
    xce.disableEscaping();
    xce.close();
} */
}
