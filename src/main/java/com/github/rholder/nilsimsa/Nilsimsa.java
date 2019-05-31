/*
 * Copyright 2015 Ray Holder
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/*
 * Copyright (c) 2012-2014 Diffeo, Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.github.rholder.nilsimsa;

import static java.util.Arrays.fill;

/**
 * This class provides an implementation of the Nilsimsa locality-sensitive hashing algorithm.
 */
public class Nilsimsa {

    /**
     * This is a precomputed constant for the standard Nilsimsa "53"-based transition table.
     */
    public static final int[] TRAN53 = new int[]{
            0x02, 0xD6, 0x9E, 0x6F, 0xF9, 0x1D, 0x04, 0xAB, 0xD0, 0x22, 0x16, 0x1F, 0xD8, 0x73, 0xA1, 0xAC,
            0x3B, 0x70, 0x62, 0x96, 0x1E, 0x6E, 0x8F, 0x39, 0x9D, 0x05, 0x14, 0x4A, 0xA6, 0xBE, 0xAE, 0x0E,
            0xCF, 0xB9, 0x9C, 0x9A, 0xC7, 0x68, 0x13, 0xE1, 0x2D, 0xA4, 0xEB, 0x51, 0x8D, 0x64, 0x6B, 0x50,
            0x23, 0x80, 0x03, 0x41, 0xEC, 0xBB, 0x71, 0xCC, 0x7A, 0x86, 0x7F, 0x98, 0xF2, 0x36, 0x5E, 0xEE,
            0x8E, 0xCE, 0x4F, 0xB8, 0x32, 0xB6, 0x5F, 0x59, 0xDC, 0x1B, 0x31, 0x4C, 0x7B, 0xF0, 0x63, 0x01,
            0x6C, 0xBA, 0x07, 0xE8, 0x12, 0x77, 0x49, 0x3C, 0xDA, 0x46, 0xFE, 0x2F, 0x79, 0x1C, 0x9B, 0x30,
            0xE3, 0x00, 0x06, 0x7E, 0x2E, 0x0F, 0x38, 0x33, 0x21, 0xAD, 0xA5, 0x54, 0xCA, 0xA7, 0x29, 0xFC,
            0x5A, 0x47, 0x69, 0x7D, 0xC5, 0x95, 0xB5, 0xF4, 0x0B, 0x90, 0xA3, 0x81, 0x6D, 0x25, 0x55, 0x35,
            0xF5, 0x75, 0x74, 0x0A, 0x26, 0xBF, 0x19, 0x5C, 0x1A, 0xC6, 0xFF, 0x99, 0x5D, 0x84, 0xAA, 0x66,
            0x3E, 0xAF, 0x78, 0xB3, 0x20, 0x43, 0xC1, 0xED, 0x24, 0xEA, 0xE6, 0x3F, 0x18, 0xF3, 0xA0, 0x42,
            0x57, 0x08, 0x53, 0x60, 0xC3, 0xC0, 0x83, 0x40, 0x82, 0xD7, 0x09, 0xBD, 0x44, 0x2A, 0x67, 0xA8,
            0x93, 0xE0, 0xC2, 0x56, 0x9F, 0xD9, 0xDD, 0x85, 0x15, 0xB4, 0x8A, 0x27, 0x28, 0x92, 0x76, 0xDE,
            0xEF, 0xF8, 0xB2, 0xB7, 0xC9, 0x3D, 0x45, 0x94, 0x4B, 0x11, 0x0D, 0x65, 0xD5, 0x34, 0x8B, 0x91,
            0x0C, 0xFA, 0x87, 0xE9, 0x7C, 0x5B, 0xB1, 0x4D, 0xE5, 0xD4, 0xCB, 0x10, 0xA2, 0x17, 0x89, 0xBC,
            0xDB, 0xB0, 0xE2, 0x97, 0x88, 0x52, 0xF7, 0x48, 0xD3, 0x61, 0x2C, 0x3A, 0x2B, 0xD1, 0x8C, 0xFB,
            0xF1, 0xCD, 0xE4, 0x6A, 0xE7, 0xA9, 0xFD, 0xC4, 0x37, 0xC8, 0xD2, 0xF6, 0xDF, 0x58, 0x72, 0x4E
    };

    /**
     * This is an optimization table for doing bitwise vector comparisons. The
     * population count of x, POPC[x], is the number of 1's in the binary
     * representation of x. The bitwise XOR(a, b) applied within this table,
     * POPC[a ^ b], is the Hamming distance between a and b. For more
     * information, see http://en.wikipedia.org/wiki/Hamming_weight.
     */
    public static final int[] POPC = new int[]{
            0x00, 0x01, 0x01, 0x02, 0x01, 0x02, 0x02, 0x03, 0x01, 0x02, 0x02, 0x03, 0x02, 0x03, 0x03, 0x04,
            0x01, 0x02, 0x02, 0x03, 0x02, 0x03, 0x03, 0x04, 0x02, 0x03, 0x03, 0x04, 0x03, 0x04, 0x04, 0x05,
            0x01, 0x02, 0x02, 0x03, 0x02, 0x03, 0x03, 0x04, 0x02, 0x03, 0x03, 0x04, 0x03, 0x04, 0x04, 0x05,
            0x02, 0x03, 0x03, 0x04, 0x03, 0x04, 0x04, 0x05, 0x03, 0x04, 0x04, 0x05, 0x04, 0x05, 0x05, 0x06,
            0x01, 0x02, 0x02, 0x03, 0x02, 0x03, 0x03, 0x04, 0x02, 0x03, 0x03, 0x04, 0x03, 0x04, 0x04, 0x05,
            0x02, 0x03, 0x03, 0x04, 0x03, 0x04, 0x04, 0x05, 0x03, 0x04, 0x04, 0x05, 0x04, 0x05, 0x05, 0x06,
            0x02, 0x03, 0x03, 0x04, 0x03, 0x04, 0x04, 0x05, 0x03, 0x04, 0x04, 0x05, 0x04, 0x05, 0x05, 0x06,
            0x03, 0x04, 0x04, 0x05, 0x04, 0x05, 0x05, 0x06, 0x04, 0x05, 0x05, 0x06, 0x05, 0x06, 0x06, 0x07,
            0x01, 0x02, 0x02, 0x03, 0x02, 0x03, 0x03, 0x04, 0x02, 0x03, 0x03, 0x04, 0x03, 0x04, 0x04, 0x05,
            0x02, 0x03, 0x03, 0x04, 0x03, 0x04, 0x04, 0x05, 0x03, 0x04, 0x04, 0x05, 0x04, 0x05, 0x05, 0x06,
            0x02, 0x03, 0x03, 0x04, 0x03, 0x04, 0x04, 0x05, 0x03, 0x04, 0x04, 0x05, 0x04, 0x05, 0x05, 0x06,
            0x03, 0x04, 0x04, 0x05, 0x04, 0x05, 0x05, 0x06, 0x04, 0x05, 0x05, 0x06, 0x05, 0x06, 0x06, 0x07,
            0x02, 0x03, 0x03, 0x04, 0x03, 0x04, 0x04, 0x05, 0x03, 0x04, 0x04, 0x05, 0x04, 0x05, 0x05, 0x06,
            0x03, 0x04, 0x04, 0x05, 0x04, 0x05, 0x05, 0x06, 0x04, 0x05, 0x05, 0x06, 0x05, 0x06, 0x06, 0x07,
            0x03, 0x04, 0x04, 0x05, 0x04, 0x05, 0x05, 0x06, 0x04, 0x05, 0x05, 0x06, 0x05, 0x06, 0x06, 0x07,
            0x04, 0x05, 0x05, 0x06, 0x05, 0x06, 0x06, 0x07, 0x05, 0x06, 0x06, 0x07, 0x06, 0x07, 0x07, 0x08
    };

    private int[] transitions;
    private long count;
    private int[] acc;
    private int[] window;

    public Nilsimsa(int[] transitions) {

        this.transitions = transitions;

        // num characters seen
        count = 0;

        // accumulators for computing digest
        acc = new int[256];

        // window for the last four characters seen (-1 until set)
        window = new int[4];
        fill(window, -1);
    }

    /**
     * Create an instance of the default Nilsimsa "53"-based transition table.
     */
    public Nilsimsa() {
        this(TRAN53);
    }

    /**
     * Update accumulators for 0-8 trigrams based on the next character in the
     * array and up to 4 of the previously seen characters.
     *
     * @param data input for the hash
     * @return this instance for chaining
     */
    public Nilsimsa update(byte[] data) {
        return update(data, 0, data.length);
    }
    
    /**
     * Update accumulators for 0-8 trigrams based on the next character in the
     * array and up to 4 of the previously seen characters.
     *
     * @param buffer input data buffer for the hash
     * @param offset the offset of the data in the buffer
     * @param len the length of data in the buffer
     * @return this instance for chaining
     */
    public Nilsimsa update(byte[] buffer, int offset, int len) {

        // increment accumulators for trigrams
        for (int i = 0; i < len; ++i) {

            // remove signage, just in case
            int ch = buffer[offset++] & 0xFF;
            count++;

            if (window[1] > -1) {
                // seen at least 3 characters
                acc[tran3(ch, window[0], window[1], 0)] += 1;
            }

            if (window[2] > -1) {
                // seen at least 4 characters
                acc[tran3(ch, window[0], window[2], 1)] += 1;
                acc[tran3(ch, window[1], window[2], 2)] += 1;
            }

            if (window[3] > -1) {
                // we have a full window of characters
                acc[tran3(ch, window[0], window[3], 3)] += 1;
                acc[tran3(ch, window[1], window[3], 4)] += 1;
                acc[tran3(ch, window[2], window[3], 5)] += 1;

                // duplicate hashes, used to maintain 8 trigrams per character
                acc[tran3(window[3], window[0], ch, 6)] += 1;
                acc[tran3(window[3], window[2], ch, 7)] += 1;
            }

            // adjust last seen chars by sliding the window over
            window[3] = window[2];
            window[2] = window[1];
            window[1] = window[0];
            window[0] = ch;
        }
        return this;
    }

    /**
     * Get digest of data seen thus far as an array of ints.
     */
    public int[] digest() {
        // number of trigrams seen
        long total = 0;

        if (count == 3) {
            // 3 chars -> 1 trigram
            total = 1;
        } else if (count == 4) {
            // 4 chars -> 4 trigrams
            total = 4;
        } else if (count > 4) {
            // over 4 trigrams and we do this
            // 0, 0, 0, 1, 4, 12, 20, 28, 36, 44...

            // > 4 chars -> 8 for each char, minus 28 (count up
            // otherwise 8 triplets/char less, 28 'missed' during 'ramp-up'
            total = 8 * count - 28;
        }

        // threshold for accumulator (mean of the accumulator), used to compute the hash
        long threshold = total / 256;

        // start with all zero bits
        int[] code = new int[32];
        for (int i = 0; i < 256; i++) {
            if (acc[i] > threshold) {
                // if over threshold
                code[i >> 3] += 1 << (i & 7);
            }
        }

        unsafeReverse(code);
        return code;
    }

    /**
     * Return the 256-bit digest as 64 hex characters.
     */
    public String toHexDigest() {
        StringBuilder b = new StringBuilder();
        for (int d : digest()) {
            if (d < 16) {
                b.append("0");
            }
            b.append(Integer.toHexString(d));
        }
        return b.toString();
    }

    /**
     * Return the accumulator index for the given chars a, b, and c when
     * computing for a specific trigram index. This is the primary hashing
     * function used to direct updates to the accumulator array.
     *
     * @param a first char
     * @param b second char
     * @param c third char
     * @param n trigram index, between 0 - 7
     */
    private int tran3(int a, int b, int c, int n) {
        return (((transitions[(a + n) & 255] ^ transitions[b] * (n + n + 1)) + transitions[c ^ transitions[n]]) & 255);
    }

    /**
     * In place reverse the elements of the given int array. No bounds checking
     * is done so don't abuse this.
     *
     * @param array the array to reverse
     */
    public static void unsafeReverse(int[] array) {
        int i = 0;
        int j = array.length - 1;
        int tmp;
        while (j > i) {
            tmp = array[j];
            array[j] = array[i];
            array[i] = tmp;
            j--;
            i++;
        }
    }

    /**
     * Compute the difference in bits between digest1 and digest2. Returns -127
     * to 128; 128 is the same, -127 is different.
     *
     * @param digest1 first digest to compare
     * @param digest2 second digest to compare
     * @return a value between -127 and 128, from least similar to most
     */
    public static int compare(String digest1, String digest2) {
        return compare(unsafeToHex(digest1), unsafeToHex(digest2));
    }

    /**
     * Given a String of hex characters, return them as an array of ints. No
     * bounds checking or other assumptions are handled here so if you give it a
     * String made up of non-hex characters your safety is not guaranteed.
     *
     * @param s the target string
     */
    public static int[] unsafeToHex(String s) {
        int len = s.length();
        int[] data = new int[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }

    /**
     * Compute the difference in bits between digest1 and digest2. Returns -127
     * to 128; 128 is the same, -127 is different.
     *
     * @param digest1 first digest to compare
     * @param digest2 second digest to compare
     * @return a value between -127 and 128, from least similar to most
     */
    public static int compare(int[] digest1, int[] digest2) {
        int bits = 0;
        for (int i = 0; i < 32; i++) {
            // computes the bit diff between the i'th position of the digests
            bits += POPC[255 & (digest1[i] ^ digest2[i])];
        }
        return 128 - bits;
    }

    /**
     * Generate a Nilsimsa transition table such that when the entire table is
     * XOR'd together, the end result will be 0. The default is generated from a
     * target of 53, but any value between 0 - 255 can be used to generate a
     * different variation of the transition table that adheres to this
     * property.
     *
     * @param target variation used for generating a table
     * @return an array of 256 ints
     */
    public static int[] generateTransitions(int target) {
        int[] tran = new int[256];
        for (int i = 0, j = 0; i < 256; i++) {
            // increment j by multiple of target + 1, keeping range of 0 to 255
            j = (j * target + 1) & 255;

            // increment j by j
            j += j;

            // try to keep j under 256
            if (j > 255) {
                j -= 255;
            }

            // for each value in transition table up to i
            for (int k = 0; k < i; k++) {
                // when j is the same as an existing value in transition table
                if (j == tran[k]) {
                    // increment j, reset k for another iteration
                    j = (j + 1) & 255;
                    k = 0;
                }
            }
            tran[i] = j;
        }

        return tran;
    }
}
