package org.potato.core.util.packet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.potato.core.util.packet.serde.PacketSerde;

public class Packet {
    /** The data stored in this packet (byte[]) */
    byte[] data;

    int read, write;

    public Packet(int size) {
        data = new byte[size];
    }

    public Packet(byte[] data) {
        this.data = data;
    }

    public int getLength() {
        return data.length;
    }

    /**
     * Clears all data in the packet
     */
    public void clear() {
        data = new byte[data.length];
    }

    /**
     * Sets the packet's data
     * 
     * @param data The new data you want to set (byte[])
     */
    public void setData(byte[] data) {
        this.data = data;
    }

    public byte[] getWrittenDataCopy() {
        return Arrays.copyOfRange(data, 0, write);
    }

    private static int newLength(int old, int min, int pref) {
        int prefLen = old + Math.max(min, pref);
        if(0 < prefLen && prefLen <= Integer.MAX_VALUE - 8) {
            return prefLen;
        } else {
            return hugeLength(old, min);
        }
    }

    private static int hugeLength(int old, int min) {
        int minLen = old + min;
        if(minLen < 0) {
            throw new OutOfMemoryError("Required array length " + old + " + " + min + " is too large");
        } else if(minLen < Integer.MAX_VALUE - 8) {
            return Integer.MAX_VALUE - 8;
        } else {
            return minLen;
        }
    }

    private void ensureCapacity(int bytes) {
        int minCap = write + bytes;
        int oldCap = data.length;
        if(minCap < oldCap) {
            return;
        }
        if(oldCap > 0) {
            int newCap = Packet.newLength(oldCap, minCap - oldCap, oldCap >> 1);
            data = Arrays.copyOf(data, newCap);
        } else {
            data = new byte[Math.max(256, minCap)];
        }
    }





    //
    // ENCODING
    //





    /**
     * Encodes something into the packet
     * 
     * @param in The byte to encode
     * 
     * @return Returns false if the packet is too small for the data
     */
    public boolean encode(byte in) {
        ensureCapacity(1);
        if(data.length < write + 1) {
            return false;
        }
        data[write++] = in;
        return true;
    }

    /**
     * Encodes something into the packet
     * 
     * @param in The boolean to encode
     * 
     * @return Returns false if the packet is too small for the data
     */
    public boolean encode(boolean in) {
        ensureCapacity(1);
        if(data.length < write + 1) {
            return false;
        }
        data[write++] = in ? (byte) 1 : (byte) 0;
        return true;
    }

    /**
     * Encodes something into the packet
     * 
     * @param in The int to encode
     * 
     * @return Returns false if the packet is too small for the data
     */
    public boolean encode(int in) {
        ensureCapacity(4);
        if(data.length < write + 4) {
            return false;
        }
        // Ints have 32 bits, and thus need 4 bytes.
        data[write++] = (byte) in;
        data[write++] = (byte) (in >>> 8);
        data[write++] = (byte) (in >>> 16);
        data[write++] = (byte) (in >>> 24);
        return true;
    }

    /**
     * Encodes something into the packet
     * 
     * @param in The float to encode
     * 
     * @return Returns false if the packet is too small for the data
     */
    public boolean encode(float in) {
        ensureCapacity(4);
        if(data.length < write + 4) {
            return false;
        }
        // Convert the float to int bits so its easier
        int bits = Float.floatToIntBits(in);
        data[write++] = (byte) (bits & 0xff);
        data[write++] = (byte) ((bits >> 8) & 0xff);
        data[write++] = (byte) ((bits >> 16) & 0xff);
        data[write++] = (byte) ((bits >> 24) & 0xff);
        return true;
    }

    /**
     * Encodes something into the packet
     * 
     * @param in The double to encode
     * 
     * @return Returns false if the packet is too small for the data
     */
    public boolean encode(double in) {
        ensureCapacity(8);
        if(data.length < write + 8) {
            return false;
        }
        // Doubles have 64 bits
        long bits = Double.doubleToRawLongBits(in); // Use raw so NaN values don't collapse
        data[write++] = (byte) (bits & 0xff);
        data[write++] = (byte) ((bits >> 8) & 0xff);
        data[write++] = (byte) ((bits >> 16) & 0xff);
        data[write++] = (byte) ((bits >> 24) & 0xff);
        data[write++] = (byte) ((bits >> 32) & 0xff);
        data[write++] = (byte) ((bits >> 40) & 0xff);
        data[write++] = (byte) ((bits >> 48) & 0xff);
        data[write++] = (byte) ((bits >> 56) & 0xff);
        return true;
    }

    /**
     * Encodes something into the packet
     * 
     * @param in The long to encode
     * 
     * @return Returns false if the packet is too small for the data
     */
    public boolean encode(long in) {
        ensureCapacity(8);
        if(data.length < write + 8) {
            return false;
        }
        // Longs have 64 bits
        data[write++] = (byte) (in & 0xff);
        data[write++] = (byte) ((in >> 8) & 0xff);
        data[write++] = (byte) ((in >> 16) & 0xff);
        data[write++] = (byte) ((in >> 24) & 0xff);
        data[write++] = (byte) ((in >> 32) & 0xff);
        data[write++] = (byte) ((in >> 40) & 0xff);
        data[write++] = (byte) ((in >> 48) & 0xff);
        data[write++] = (byte) ((in >> 56) & 0xff);
        return true;
    }

    /**
     * Encodes something into the packet
     * 
     * @param in The double array to encode
     * 
     * @return Returns false if the packet is too small for the data
     */
    public boolean encode(double[] in) {
        for(double d : in) {
            encode(d);
        }
        return true;
    }

    /**
     * Encodes something into the packet
     * 
     * @param in The data to encode
     * 
     * @return Returns false if the packet is too small for the data
     */
    public <T extends PacketSerializable<T>> boolean encode(T in) {
        in.getSerde().pack(this, in);
        return true;
    }

    /**
     * Encodes something into the packet
     * 
     * @param in The data list to encode
     * 
     * @return Returns false if the packet is too small for the data
     */
    public <T extends PacketSerializable<T>> boolean encode(List<T> in) {
        byte size = (byte) in.size();
        encode(size);
        for(T d : in) {
            d.getSerde().pack(this, d);
        }
        return true;
    }

    /**
     * Encodes something into the packet
     * 
     * @param in The optional to encode
     * 
     * @return Returns false if the packet is too small for the data
     */
    public <T extends PacketSerializable<T>> boolean encode(Optional<T> in) {
        encode(in.isPresent());
        if(in.isPresent()) {
            in.get().getSerde().pack(this, in.get());
        }
        return true;
    }





    //
    // DECODING
    //





    /**
     * Decodes something from the packet
     * 
     * @return The decoded byte
     */
    public byte decodeByte() {
        if(data.length < read) {
            return '\0';
        }
        return data[read];
    }

    /**
     * Decodes something from the packet
     * 
     * @return The decoded int
     */
    public int decodeInt() {
        if(data.length < read + 3) {
            return 0;
        }
        return (0xff & data[read++]) | (0xff & data[read++]) << 8 | (0xff & data[read++]) << 16 | (0xff & data[read++]) << 24;
    }

    /**
     * Decodes something from the packet
     * 
     * @return The decoded float
     */
    public float decodeFloat() {
        if(data.length < read + 3) {
            return 0;
        }
        return Float.intBitsToFloat((0xff & data[read++]) | (0xff & data[read++]) << 8 | (0xff & data[read++]) << 16 | (0xff & data[read++]) << 24);
    }

    /**
     * Decodes something from the packet
     * 
     * @return The decoded double
     */
    public double decodeDouble() {
        if(data.length < read + 7) {
            return 0;
        }
        long out = (long) (
            (long) (0xff & data[read++]) |
            (long) (0xff & data[read++]) << 8 |
            (long) (0xff & data[read++]) << 16 |
            (long) (0xff & data[read++]) << 24 |
            (long) (0xff & data[read++]) << 32 |
            (long) (0xff & data[read++]) << 40 |
            (long) (0xff & data[read++]) << 48 |
            (long) (0xff & data[read++]) << 56
        );
        return Double.longBitsToDouble(out);
    }

    /**
     * Decodes something from the packet
     * 
     * @return The decoded long
     */
    public long decodeLong() {
        if(data.length < read + 7) {
            return 0;
        }
        long out = (long) (
            (long) (0xff & data[read++]) |
            (long) (0xff & data[read++]) << 8 |
            (long) (0xff & data[read++]) << 16 |
            (long) (0xff & data[read++]) << 24 |
            (long) (0xff & data[read++]) << 32 |
            (long) (0xff & data[read++]) << 40 |
            (long) (0xff & data[read++]) << 48 |
            (long) (0xff & data[read++]) << 56
        );
        return out;
    }

    /**
     * Decodes something from the packet
     * 
     * @return The decoded boolean
     */
    public boolean decodeBoolean() {
        if (data.length < read) {
            return false;
        }
        return data[read++] == 1;
    }

    /**
     * Decodes something from the packet
     * 
     * @return The decoded double array
     */
    public double[] decodeDoubleArray(int length) {
        double[] out = new double[length];
        for(int i = 0; i < length; i++) {
            out[i] = decodeDouble();
        }
        return out;
    }

    /**
     * Decodes something from the packet
     * 
     * @return The decoded list
     */
    public <T extends PacketSerializable<T>> List<T> decodeList(PacketSerde<T> serde) {
        byte length = decodeByte();

        ArrayList<T> out = new ArrayList<T>();
        out.ensureCapacity(length);

        for(int i = 0; i < length; i++) {
            out.add(serde.unpack(this));
        }

        return out;
    }

    /**
     * Decodes something from the packet
     * 
     * @return The decoded optional
     */
    public <T extends PacketSerializable<T>> Optional<T> decodeOptional(PacketSerde<T> serde) {
        if(decodeBoolean()) {
            return Optional.of(serde.unpack(this));
        }
        return Optional.empty();
    }

    /**
     * Decodes something from the packet
     * 
     * @return The decoded struct
     */
    public <T extends PacketSerializable<T>> T decodeStruct(PacketSerializable<T> type) {
        return type.getSerde().unpack(this);
    }
}
