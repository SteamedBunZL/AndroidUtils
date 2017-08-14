package com.tcl.security.cloudengine;

import android.util.Log;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.NoSuchElementException;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;
import java.util.zip.ZipEntry;

import static com.tcl.security.cloudengine.ZipConstants.SHORT;
import static com.tcl.security.cloudengine.ZipConstants.WORD;
import static com.tcl.security.cloudengine.ZipConstants.ZIP64_MAGIC_SHORT;


public class CustomUnzip {
    private static final String TAG = ProjectEnv.bDebug ?  "CustomUnzip" : CustomUnzip.class.getSimpleName();

    public class ZipEnumeration implements Enumeration<UnzipEntry> {
        private boolean hasNext = true;
        private long lastPos = -1;

        @Override
        public boolean hasMoreElements() {
            return hasNext;
        }

        @Override
        public UnzipEntry nextElement() {
            if (hasNext) {
                try {
                    if (lastPos > 0)
                        archive.seek(lastPos);
                    UnzipEntry entry = readCentralDirectoryEntry();
                    archive.readFully(WORD_BUF);
                    hasNext = ZipLong.getValue(WORD_BUF) == CFH_SIG;
                    lastPos = archive.getFilePointer();
                    return entry;
                } catch (Exception e) {
                    if (ProjectEnv.bDebug) {
                        Log.e(TAG, "next error:\n");
                        e.printStackTrace();
                    }
                }
            }

            throw new NoSuchElementException();
        }

        /**
         * Reads an individual entry of the central directory.
         */
        private UnzipEntry readCentralDirectoryEntry() throws IOException {

            UnzipEntry unzipEntry = new UnzipEntry();

            archive.readFully(CFH_BUF);
            int off = 0;

            // Platform
            off += SHORT;

            // Version Needed
            off += SHORT;

            // GPF
            unzipEntry.generalPurposeFlag = ZipShort.getValue(CFH_BUF, off);
            off += SHORT;

            // Compression Method
            unzipEntry.compressionMethod = ZipShort.getValue(CFH_BUF, off);
            off += SHORT;

            // Time
            off += WORD;

            // CRC32
            //
            unzipEntry.crc32 = ZipLong.getValue(CFH_BUF, off);
            off += WORD;

            // Compressed Size
            unzipEntry.compressedSize = ZipLong.getValue(CFH_BUF, off);
            off += WORD;

            // Size
            unzipEntry.uncompressedSize = ZipLong.getValue(CFH_BUF, off);
            off += WORD;

            int fileNameLen = ZipShort.getValue(CFH_BUF, off);
            off += SHORT;

            // Extra Size
            int extraLen = ZipShort.getValue(CFH_BUF, off);
            off += SHORT;

            // Comment Size
            int commentLen = ZipShort.getValue(CFH_BUF, off);
            off += SHORT;

            // Skip disk number start, always 0
            off += SHORT;

            // Skip internal file attributes, always 0
            off += SHORT;

            // Skip external file attributes, always 0
            off += WORD;

            byte[] buf = new byte[fileNameLen];
            archive.readFully(buf);
            unzipEntry.fileNameBytes = buf;
            unzipEntry.fileName = newString(buf);

            // LFH offset
            unzipEntry.lfhOffset = ZipLong.getValue(CFH_BUF, off);

            skipBytes(extraLen);
            skipBytes(commentLen);

            lastPos = archive.getFilePointer();

            unzipEntry.resolveLocalFileHeaderData();

            archive.seek(lastPos);

            return unzipEntry;
        }
    }

    private String newString(byte [] buf) {
        char [] ch = new char[buf.length];
        for (int i = 0; i < buf.length; i++) {
            ch[i] = (char) buf[i];
        }
        return new String(ch);
    }

    public CustomUnzip(String filename) throws IOException {
        archive = new RandomAccessFile(filename, "r");

        positionAtCentralDirectory();

        archive.readFully(WORD_BUF);
        long sig = ZipLong.getValue(WORD_BUF);

        if (sig != CFH_SIG && startsWithLocalFileHeader()) {
            throw new IOException(filename + ": central directory is empty.");
        }
    }

    /**
     * Returns an enumeration of the entries. The entries are listed in the
     * order in which they appear in the ZIP archive.
     *
     * @return the enumeration of the entries.
     */
    public Enumeration<UnzipEntry> entries() {
        return enumeration;
    }

    /**
     * Closes this ZIP file.
     *
     * @throws IOException
     *             if an IOException occurs.
     */
    public void close() throws IOException {
        archive.close();
    }

    /**
     *
     * Searches for either the "End of central dir record", positions the stream at the first central directory record.
     *
     * @throws IOException
     */
    private void positionAtCentralDirectory() throws IOException {
        positionAtEndOfCentralDirectoryRecord();
        positionAtCentralDirectory32();
    }

    /**
     * Length of the "End of central directory record" - which is
     * supposed to be the last structure of the archive - without file
     * comment.
     */
    private static final int MIN_EOCD_SIZE =
    /* end of central dir signature    */WORD
    /* number of this disk             */+ SHORT
    /* number of the disk with the     */
    /* start of the central directory  */+ SHORT
    /* total number of entries in      */
    /* the central dir on this disk    */+ SHORT
    /* total number of entries in      */
    /* the central dir                 */+ SHORT
    /* size of the central directory   */+ WORD
    /* offset of start of central      */
    /* directory with respect to       */
    /* the starting disk number        */+ WORD
    /* zipfile comment length          */+ SHORT;

    /**
     * Maximum length of the "End of central directory record" with a
     * file comment.
     */
    private static final int MAX_EOCD_SIZE = MIN_EOCD_SIZE
    /* maximum length of zipfile comment */+ ZIP64_MAGIC_SHORT;

    /**
     * end of central dir signature
     */
    private static final byte[] EOCD_SIG = ZipLong.getBytes(0X06054B50L);

    /**
     * Searches for the and positions the stream at the start of the
     * &quot;End of central dir record&quot;.
     */
    private void positionAtEndOfCentralDirectoryRecord() throws IOException {
        boolean found = tryToLocateSignature(MIN_EOCD_SIZE, MAX_EOCD_SIZE, EOCD_SIG);
        if (!found) {
            throw new IOException("archive is not a ZIP archive");
        }
    }

    /**
     * Offset of the field that holds the location of the first
     * central directory entry inside the "End of central directory
     * record" relative to the start of the "End of central directory
     * record".
     */
    private static final int CFD_LOCATOR_OFFSET =
    /* end of central dir signature    */WORD
    /* number of this disk             */+ SHORT
    /* number of the disk with the     */
    /* start of the central directory  */+ SHORT
    /* total number of entries in      */
    /* the central dir on this disk    */+ SHORT
    /* total number of entries in      */
    /* the central dir                 */+ SHORT
    /* size of the central directory   */+ WORD;

    /**
     * Parses the &quot;End of central dir record&quot; and positions
     * the stream at the first central directory record.
     *
     * Expects stream to be positioned at the beginning of the
     * &quot;End of central dir record&quot;.
     */
    private void positionAtCentralDirectory32() throws IOException {
        skipBytes(CFD_LOCATOR_OFFSET);
        archive.readFully(WORD_BUF);
        archive.readFully(CMT_BUF);
        int len = ZipShort.getValue(CMT_BUF);
        if (len > 0) {
            commentBytes = new byte[len];
            archive.readFully(commentBytes);
            comment = newString(commentBytes);
        }
        archive.seek(ZipLong.getValue(WORD_BUF));
    }

    /**
     * Skips the given number of bytes or throws an EOFException if
     * skipping failed.
     */
    private void skipBytes(final int count) throws IOException {
        int totalSkipped = 0;
        while (totalSkipped < count) {
            int skippedNow = archive.skipBytes(count - totalSkipped);
            if (skippedNow <= 0) {
                throw new EOFException();
            }
            totalSkipped += skippedNow;
        }
    }

    private static final int POS_0 = 0;
    private static final int POS_1 = 1;
    private static final int POS_2 = 2;
    private static final int POS_3 = 3;

    /**
     * Searches the archive backwards from minDistance to maxDistance
     * for the given signature, positions the RandomaccessFile right
     * at the signature if it has been found.
     */
    private boolean tryToLocateSignature(long minDistanceFromEnd, long maxDistanceFromEnd, byte[] sig)
            throws IOException {
        boolean found = false;
        fileSize = archive.length();
        long off = fileSize - minDistanceFromEnd;
        final long stopSearching = Math.max(0L, archive.length() - maxDistanceFromEnd);
        if (off >= 0) {
            for (; off >= stopSearching; off--) {
                archive.seek(off);
                int curr = archive.read();
                if (curr == -1) {
                    break;
                }
                if (curr == sig[POS_0]) {
                    curr = archive.read();
                    if (curr == sig[POS_1]) {
                        curr = archive.read();
                        if (curr == sig[POS_2]) {
                            curr = archive.read();
                            if (curr == sig[POS_3]) {
                                found = true;
                                break;
                            }
                        }
                    }
                }
            }
        }
        if (found) {
            archive.seek(off);
        }
        return found;
    }

    /**
     * Checks whether the archive starts with a LFH. If it doesn't,
     * it may be an empty archive.
     */
    private boolean startsWithLocalFileHeader() throws IOException {
        archive.seek(0);
        archive.readFully(WORD_BUF);
        return Arrays.equals(WORD_BUF, ZipLong.LFH_SIG.getBytes());
    }

    static final int NIBLET_MASK = 0x0f;
    static final int BYTE_SHIFT = 8;

    /**
     * Length of a "central directory" entry structure without file
     * name, extra fields or comment.
     */
    private static final int CFH_LEN =
    /* version made by                 */SHORT
    /* version needed to extract       */+ SHORT
    /* general purpose bit flag        */+ SHORT
    /* compression method              */+ SHORT
    /* last mod file time              */+ SHORT
    /* last mod file date              */+ SHORT
    /* crc-32                          */+ WORD
    /* compressed size                 */+ WORD
    /* uncompressed size               */+ WORD
    /* filename length                 */+ SHORT
    /* extra field length              */+ SHORT
    /* file comment length             */+ SHORT
    /* disk number start               */+ SHORT
    /* internal file attributes        */+ SHORT
    /* external file attributes        */+ WORD
    /* relative offset of local header */+ WORD;

    /**
     * central file header signature
     */
    private static final long CFH_SIG = ZipLong.getValue(ZipLong.CFH_SIG.getBytes());

    // cached buffers - must only be used locally in the class (COMPRESS-172 - reduce garbage collection)
    // private final byte[] DWORD_BUF = new byte[DWORD];
    private final byte[] WORD_BUF = new byte[WORD];
    private final byte[] CFH_BUF = new byte[CFH_LEN];
    private final byte[] SHORT_BUF = new byte[SHORT];
    private final byte[] CMT_BUF = new byte[SHORT];

    final RandomAccessFile archive;
    byte[] commentBytes = null;
    String comment;
    long fileSize;

    final ZipEnumeration enumeration = new ZipEnumeration();

    public class UnzipEntry {

        String fileName;

        int generalPurposeFlag;

        int compressionMethod;

        long compressedSize;

        /** LFH Offset */
        long lfhOffset;

        /** Data Offset */
        long dataOffset;

        long crc32;
        long uncompressedSize;
        byte[] fileNameBytes;

        /**
         * Determine whether or not this {@code UnzipEntry} is a directory.
         *
         * @return {@code true} when this {@code UnzipEntry} is a directory, {@code
         *         false} otherwise.
         */
        public boolean isDirectory() {
            return fileName.charAt(fileName.length() - 1) == '/';
        }

        /**
         * Gets the file name of this {@code UnzipEntry} file.
         *
         * @return the file name of this {@code UnzipEntry} file.
         */
        public String getName() {
            return fileName;
        }

        /**
         * Returns an InputStream for reading the contents of the entry.
         *
         * @return a stream to read the entry from.
         * @throws IOException
         *             if unable to create an input stream from the zipentry
         */
        public InputStream getInputStream() throws IOException {
            long start = dataOffset;
            BoundedInputStream bis = new BoundedInputStream(start, compressedSize);
            switch (compressionMethod) {
                case ZipEntry.STORED:
                    return bis;
                case ZipEntry.DEFLATED:
                    bis.addDummy();
                    final Inflater inflater = new Inflater(true);
                    return new InflaterInputStream(bis, inflater) {
                        @Override
                        public void close() throws IOException {
                            super.close();
                            inflater.end();
                        }
                    };
                default:
                    throw new IOException("Unsupported compression method " + compressionMethod);
            }
        }

        /**
         * Number of bytes in local file header up to the &quot;length of
         * filename&quot; entry.
         */
        private static final int LFH_LEN =
        /* local file header signature     */WORD
        /* version needed to extract       */+ SHORT
        /* general purpose bit flag        */+ SHORT
        /* compression method              */+ SHORT
        /* last mod file time              */+ SHORT
        /* last mod file date              */+ SHORT
        /* crc-32                          */+ WORD
        /* compressed size                 */+ WORD
        /* uncompressed size               */+ WORD;

        /**
         * Number of bytes in local file header up to the &quot;length of
         * filename&quot; entry.
         */
        private static final long LFH_OFFSET_FOR_FILENAME_LENGTH =
        /* local file header signature     */WORD
        /* version needed to extract       */+ SHORT
        /* general purpose bit flag        */+ SHORT
        /* compression method              */+ SHORT
        /* last mod file time              */+ SHORT
        /* last mod file date              */+ SHORT
        /* crc-32                          */+ WORD
        /* compressed size                 */+ WORD
        /* uncompressed size               */+ WORD;

        /**
         * Adds the data available from the local file header.
         *
         * <p>
         * Also records the offsets for the data to read from the entries.
         * </p>
         */
        void resolveLocalFileHeaderData() throws IOException {

            archive.seek(lfhOffset);

            // local file header signature
            archive.readFully(WORD_BUF);

            if (Arrays.equals(WORD_BUF, ZipLong.LFH_SIG.getBytes())) {
                archive.seek(lfhOffset + LFH_LEN);

                archive.readFully(SHORT_BUF);
                int fileNameLen = ZipShort.getValue(SHORT_BUF);
                archive.readFully(SHORT_BUF);
                int extraFieldLen = ZipShort.getValue(SHORT_BUF);

                dataOffset = lfhOffset + LFH_OFFSET_FOR_FILENAME_LENGTH + SHORT + SHORT + fileNameLen + extraFieldLen;

            } else {
                throw new IOException("Invalid entry LFH offset: " + lfhOffset);
            }
        }

        /**
         * InputStream that delegates requests to the underlying
         * RandomAccessFile, making sure that only bytes from a certain
         * range can be read.
         */
        private class BoundedInputStream extends InputStream {
            private long remaining;
            private long loc;
            private boolean addDummyByte = false;

            BoundedInputStream(long start, long remaining) {
                this.remaining = remaining;
                loc = start;
            }

            @Override
            public int read() throws IOException {
                if (remaining-- <= 0) {
                    if (addDummyByte) {
                        addDummyByte = false;
                        return 0;
                    }
                    return -1;
                }
                synchronized (archive) {
                    archive.seek(loc++);
                    return archive.read();
                }
            }

            @Override
            public int read(byte[] b, int off, int len) throws IOException {
                if (remaining <= 0) {
                    if (addDummyByte) {
                        addDummyByte = false;
                        b[off] = 0;
                        return 1;
                    }
                    return -1;
                }

                if (len <= 0) {
                    return 0;
                }

                if (len > remaining) {
                    len = (int) remaining;
                }
                int ret = -1;
                synchronized (archive) {
                    archive.seek(loc);
                    ret = archive.read(b, off, len);
                }
                if (ret > 0) {
                    loc += ret;
                    remaining -= ret;
                }
                return ret;
            }

            /**
             * Inflater needs an extra dummy byte for nowrap - see
             * Inflater's javadocs.
             */
            void addDummy() {
                addDummyByte = true;
            }
        }
    }
}
