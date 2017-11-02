//package com.clean.spaceplus.cleansdk.util;
//
//import java.io.ByteArrayOutputStream;
//import java.io.EOFException;
//import java.io.IOException;
//import java.io.InputStream;
//import java.util.concurrent.atomic.AtomicReference;
//
///**
// * @author zengtao.kuang
// * @Description: 流工具
// * @date 2016/5/12 14:12
// * @copyright TCL-MIG
// */
//public class Streams {
//
//    private static AtomicReference<byte[]> skipBuffer = new AtomicReference<byte[]>();
//
//    private Streams() {}
//
//    /**
//     * Implements InputStream.read(int) in terms of InputStream.read(byte[], int, int).
//     * InputStream assumes that you implement InputStream.read(int) and provides default
//     * implementations of the others, but often the opposite is more efficient.
//     */
//    public static int readSingleByte(InputStream in) throws IOException {
//        byte[] buffer = new byte[1];
//        int result = in.read(buffer, 0, 1);
//        return (result != -1) ? buffer[0] & 0xff : -1;
//    }
//
//    /**
//     * Implements OutputStream.write(int) in terms of OutputStream.write(byte[], int, int).
//     * OutputStream assumes that you implement OutputStream.write(int) and provides default
//     * implementations of the others, but often the opposite is more efficient.
//     */
/////<DEAD CODE>///    public static void writeSingleByte(OutputStream out, int b) throws IOException {
////       byte[] buffer = new byte[1];
////       buffer[0] = (byte) (b & 0xff);
////       out.write(buffer);
////   }
//
//    /**
//     * Fills 'dst' with bytes from 'in', throwing EOFException if insufficient bytes are available.
//     */
/////<DEAD CODE>///    public static void readFully(InputStream in, byte[] dst) throws IOException {
////       readFully(in, dst, 0, dst.length);
////   }
//
//    /**
//     * Reads exactly 'byteCount' bytes from 'in' (into 'dst' at offset 'offset'), and throws
//     * EOFException if insufficient bytes are available.
//     *
//     * Used to implement {@link java.io.DataInputStream#readFully(byte[], int, int)}.
//     */
//    public static void readFully(InputStream in, byte[] dst, int offset, int byteCount) throws IOException {
//        if (byteCount == 0) {
//            return;
//        }
//        while (byteCount > 0) {
//            int bytesRead = in.read(dst, offset, byteCount);
//            if (bytesRead < 0) {
//                throw new EOFException();
//            }
//            offset += bytesRead;
//            byteCount -= bytesRead;
//        }
//    }
//
//    /**
//     * Returns a byte[] containing the remainder of 'in', closing it when done.
//     */
/////<DEAD CODE>///    public static byte[] readFully(InputStream in) throws IOException {
////       try {
////           return readFullyNoClose(in);
////       } finally {
////           in.close();
////       }
////   }
//
//    /**
//     * Returns a byte[] containing the remainder of 'in'.
//     */
//    public static byte[] readFullyNoClose(InputStream in) throws IOException {
//        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
//        byte[] buffer = new byte[1024];
//        int count;
//        while ((count = in.read(buffer)) != -1) {
//            bytes.write(buffer, 0, count);
//        }
//        return bytes.toByteArray();
//    }
//
//    /**
//     * Returns the remainder of 'reader' as a string, closing it when done.
//     */
/////<DEAD CODE>///     public static String readFully(Reader reader) throws IOException {
////        try {
////            StringWriter writer = new StringWriter();
////            char[] buffer = new char[1024];
////            int count;
////            while ((count = reader.read(buffer)) != -1) {
////                writer.write(buffer, 0, count);
////            }
////            return writer.toString();
////        } finally {
////            reader.close();
////        }
////    }
//
/////<DEAD CODE>///     public static void skipAll(InputStream in) throws IOException {
////        do {
////            in.skip(Long.MAX_VALUE);
////        } while (in.read() != -1);
////    }
//
//    /**
//     * Call {@code in.read()} repeatedly until either the stream is exhausted or
//     * {@code byteCount} bytes have been read.
//     *
//     * <p>This method reuses the skip buffer but is careful to never use it at
//     * the same time that another stream is using it. Otherwise streams that use
//     * the caller's buffer for consistency checks like CRC could be clobbered by
//     * other threads. A thread-local buffer is also insufficient because some
//     * streams may call other streams in their skip() method, also clobbering the
//     * buffer.
//     */
/////<DEAD CODE>///     public static long skipByReading(InputStream in, long byteCount) throws IOException {
////        // acquire the shared skip buffer.
////        byte[] buffer = skipBuffer.getAndSet(null);
////        if (buffer == null) {
////            buffer = new byte[4096];
////        }
////
////        long skipped = 0;
////        while (skipped < byteCount) {
////            int toRead = (int) Math.min(byteCount - skipped, buffer.length);
////            int read = in.read(buffer, 0, toRead);
////            if (read == -1) {
////                break;
////            }
////            skipped += read;
////            if (read < toRead) {
////                break;
////            }
////        }
////
////        // release the shared skip buffer.
////        skipBuffer.set(buffer);
////
////        return skipped;
////    }
//
//    /**
//     * Copies all of the bytes from {@code in} to {@code out}. Neither stream is closed.
//     * Returns the total number of bytes transferred.
//     */
/////<DEAD CODE>///     public static int copy(InputStream in, OutputStream out) throws IOException {
////        int total = 0;
////        byte[] buffer = new byte[8192];
////        int c;
////        while ((c = in.read(buffer)) != -1) {
////            total += c;
////            out.write(buffer, 0, c);
////        }
////        return total;
////    }
//
//    /**
//     * Returns the ASCII characters up to but not including the next "\r\n", or
//     * "\n".
//     *
//     * @throws EOFException if the stream is exhausted before the next newline
//     *     character.
//     */
/////<DEAD CODE>///     public static String readAsciiLine(InputStream in) throws IOException {
////        // TODO: support UTF-8 here instead
////
////        StringBuilder result = new StringBuilder(80);
////        while (true) {
////            int c = in.read();
////            if (c == -1) {
////                throw new EOFException();
////            } else if (c == '\n') {
////                break;
////            }
////
////            result.append((char) c);
////        }
////        int length = result.length();
////        if (length > 0 && result.charAt(length - 1) == '\r') {
////            result.setLength(length - 1);
////        }
////        return result.toString();
////    }
//}
