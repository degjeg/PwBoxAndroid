package com.pw.box.net;

/**
 * 循环缓冲区
 * Created by danger on 2016/12/2.
 */

public class MyByteBuffer {

    private byte[] data;
    private int head = 0;
    private int headMark = 0;
    private int end = 0;

    public MyByteBuffer(int size) {
        data = new byte[size];
    }

    public static void main(String[] ar) {
        MyByteBuffer b = new MyByteBuffer(10);
        b.writeBytes("11".getBytes());
        b.writeBytes("222".getBytes());
        b.writeBytes("3333".getBytes());
        b.get(1);
        b.get(2);
        b.get(3);

        b.writeBytes("11".getBytes());
        b.writeBytes("222".getBytes());
        b.writeBytes("3333".getBytes());

        b.get(2);
        b.get(3);
        b.get(3);
        b.get(4);

        b.writeBytes("11".getBytes());
        b.writeBytes("222".getBytes());
        b.writeBytes("3333".getBytes());
        b.get(1);
        b.get(2);
        b.get(3);

        b.writeBytes("11".getBytes());
        b.writeBytes("222".getBytes());
        b.writeBytes("3333".getBytes());

        b.get(2);
        b.get(3);
        b.get(3);
        b.get(4);

        b.writeBytes("11".getBytes());
        b.writeBytes("222".getBytes());
        b.writeBytes("3333".getBytes());
        b.get(1);
        b.get(2);
        b.get(3);

        b.writeBytes("11".getBytes());
        b.writeBytes("222".getBytes());
        b.writeBytes("3333".getBytes());

        b.get(2);
        b.get(3);
        b.get(3);
        b.get(4);
        b.writeBytes("11".getBytes());
        b.writeBytes("222".getBytes());
        b.writeBytes("3333".getBytes());
        b.get(1);
        b.get(2);
        b.get(3);

        b.writeBytes("11".getBytes());
        b.writeBytes("222".getBytes());
        b.writeBytes("3333".getBytes());

        b.get(2);
        b.get(3);
        b.get(3);
        b.get(4);
        b.writeBytes("11".getBytes());
        b.writeBytes("222".getBytes());
        b.writeBytes("3333".getBytes());
        b.get(1);
        b.get(2);
        b.get(3);

        b.writeBytes("11".getBytes());
        b.writeBytes("222".getBytes());
        b.writeBytes("3333".getBytes());

        b.get(2);
        b.get(3);
        b.get(3);
        b.get(4);
        b.writeBytes("11".getBytes());
        b.writeBytes("222".getBytes());
        b.writeBytes("3333".getBytes());
        b.get(1);
        b.get(2);
        b.get(3);

        b.writeBytes("11".getBytes());
        b.writeBytes("222".getBytes());
        b.writeBytes("3333".getBytes());

        b.get(2);
        b.get(3);
        b.get(3);
        b.get(4);
        b.writeBytes("11".getBytes());
        b.writeBytes("222".getBytes());
        b.writeBytes("3333".getBytes());
        b.get(1);
        b.get(2);
        b.get(3);

        b.writeBytes("11".getBytes());
        b.writeBytes("222".getBytes());
        b.writeBytes("3333".getBytes());

        b.get(2);
        b.get(3);
        b.get(3);
        b.get(4);
        b.writeBytes("11".getBytes());
        b.writeBytes("222".getBytes());
        b.writeBytes("3333".getBytes());
        b.get(1);
        b.get(2);
        b.get(3);

        b.writeBytes("11".getBytes());
        b.writeBytes("222".getBytes());
        b.writeBytes("3333".getBytes());

        b.get(2);
        b.get(3);
        b.get(3);
        b.get(4);
        b.writeBytes("11".getBytes());
        b.writeBytes("222".getBytes());
        b.writeBytes("3333".getBytes());
        b.get(1);
        b.get(2);
        b.get(3);

        b.writeBytes("11".getBytes());
        b.writeBytes("222".getBytes());
        b.writeBytes("3333".getBytes());

        b.get(2);
        b.get(3);
        b.get(3);
        b.get(4);
    }

    public int getCapacity() {
        return data.length;
    }

    /**
     * 返回缓冲区的剩余空间字节数据
     *
     * @return
     */
    public int getRemaingSpace() {
        return data.length - readableBytes() - 1;
    }

    public byte[] get() {
        return data;
    }

    public byte[] get(int bytesCount) {
        if (bytesCount > readableBytes()) {
            return null;
        }

        byte[] d = new byte[bytesCount];
        if (end > head) {
            System.arraycopy(data, head, d, 0, bytesCount);
            head += bytesCount;
        } else if (data.length - head >= bytesCount) {
            System.arraycopy(data, head, d, 0, bytesCount);
            head = (head + bytesCount) % data.length;
        } else {
            int size1 = data.length - head;
            int size2 = bytesCount - size1;

            System.arraycopy(data, head, d, 0, size1);
            System.arraycopy(data, 0, d, size1, size2);
            head = size2;
        }

        System.out.println("get " + bytesCount + ":" + head + " " + end);
        return d;
    }

    public void clear() {
        head = end = headMark = 0;
    }

    public int writeBytes(byte[] d) {
        return writeBytes(d, 0, d.length);
    }

    public int writeBytes(byte[] d, int pos, int len) {
        if (d == null) {
            return -1;
        }
        if (len > getRemaingSpace()) { // 空间不足
            return -2;
        }

        if (data.length - end >= len) { // 可以在尾部
            System.arraycopy(d, pos, data, end, len);
            end = (end + len) % data.length;
        } else {
            int size1 = data.length - end;
            int size2 = len - size1;

            System.arraycopy(d, pos, data, end, size1);
            System.arraycopy(d, pos + size1, data, 0, size2);
            end = size2;
        }

        System.out.println("write " + len + ":" + head + " " + end);
        return 0;
    }

    public void writeShort(int i) {
        byte[] d = new byte[2];
        d[0] = (byte) (i >> 8 & 0xff);
        d[1] = (byte) (i & 0xff);
        writeBytes(d);
    }

    public void writeInt(int i) {
        byte[] d = new byte[4];
        d[0] = (byte) (i >> 24 & 0xff);
        d[1] = (byte) ((i >> 16) & 0xff);
        d[2] = (byte) ((i >> 8) & 0xff);
        d[3] = (byte) (i & 0xff);
        writeBytes(d);
    }

    public short readShort() {
        byte data[] = get(2);
        if (data == null) {
            return 0;
        }
        int head = 0;
        short i = (short) ((data[head] << 8 & 0xff00) |
                (data[head + 1] & 0xff));

        return i;
    }

    public short readInt() {
        byte data[] = get(4);
        if (data == null) {
            return 0;
        }
        int head = 0;
        short i = (short) (
                (data[head] << 24 & 0xff000000) |
                        ((data[head + 1] << 16) & 0xff0000) |
                        ((data[head + 2] << 8) & 0xff00) |
                        ((data[head + 3]) & 0xff)
        );

        return i;
    }

    public int readableBytes() {
        if (end > head) {
            return end - head;
        } else if (end == head) {
            return 0;
        } else {
            return data.length - head + end;
        }
    }

    public void markReaderIndex() {
        headMark = head;
    }

    public void resetReaderIndex() {
        head = headMark;
    }
}
