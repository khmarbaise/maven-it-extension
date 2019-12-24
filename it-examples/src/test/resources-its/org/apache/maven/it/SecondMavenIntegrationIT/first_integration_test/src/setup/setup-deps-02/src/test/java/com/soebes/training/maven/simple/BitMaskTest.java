package com.soebes.training.maven.simple;

import static junit.framework.Assert.assertEquals;

import org.junit.Test;

public class BitMaskTest {

    @Test
    public void checkFirstBitTest() {
        BitMask bm = new BitMask(0x8000000000000000L);
        assertEquals(true, bm.isBitSet(63));
    }

    @Test
    public void checkNumberBitTest() {
        for (int bitNumber = 0; bitNumber < 64; bitNumber++) {
            long bitMask = Long.rotateLeft(1, bitNumber);
            BitMask bm = new BitMask(bitMask);
            assertEquals(true, bm.isBitSet(bitNumber));
        }
    }

    @Test
    public void setBitNumberTest() {
        BitMask bm = new BitMask();
        for (int bitNumber = 0; bitNumber < 64; bitNumber++) {
            bm.setBit(bitNumber);
            assertEquals(true, bm.isBitSet(bitNumber));
        }
    }

    @Test
    public void unsetBitNumberTest() {
        BitMask bm = new BitMask();
        for (int bitNumber = 0; bitNumber < 64; bitNumber++) {
            bm.setBit(bitNumber);
        }
        for (int bitNumber = 0; bitNumber < 64; bitNumber++) {
            bm.unsetBit(bitNumber);
            assertEquals(false, bm.isBitSet(bitNumber));
        }
    }

    @Test
    public void adhocBitTest() {
        BitMask bm = new BitMask(0xffffffffffffffffL);
        bm.unsetBit(10);
        bm.unsetBit(20);
        bm.unsetBit(30);
        bm.unsetBit(40);
        bm.unsetBit(50);
        bm.unsetBit(60);

        assertEquals(false, bm.isBitSet(10));
        assertEquals(false, bm.isBitSet(20));
        assertEquals(false, bm.isBitSet(30));
        assertEquals(false, bm.isBitSet(40));
        assertEquals(false, bm.isBitSet(50));
        assertEquals(false, bm.isBitSet(60));
    }
}
