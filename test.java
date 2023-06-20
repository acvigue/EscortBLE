import java.util.UUID;

public class Crypto {

    public Crypto(boolean isBLE) {
        this.isBLE = isBLE;
    }

    public static void main(String[] args) {
        byte[] bArr = new byte[13];
        byte b = 80;
        bArr[0] = (byte) (b & Byte.MAX_VALUE);
        byte b2 = (byte) ((b & 128) >>> 7);
        bArr[1] = b2;
        if (true) {
            bArr[1] = (byte) (b2 | 2);
        }
        byte b3 = bArr[1];
        int i = 0;
        bArr[1] = (byte) (b3 | ((byte) ((i & 31) << 2)));
        bArr[2] = (byte) ((i & 4064) >>> 5);
        bArr[3] = (byte) ((258048 & i) >> 12);
        byte b4 = (byte) ((16252928 & i) >> 19);
        bArr[4] = b4;
        int i2 = 0;
        bArr[4] = (byte) (b4 | ((i2 & 3) << 5));
        bArr[5] = (byte) ((i2 & 508) >>> 2);
        bArr[6] = (byte) ((65024 & i2) >> 9);
        bArr[7] = (byte) ((8323072 & i2) >>> 16);
        byte b5 = (byte) ((8388608 & i2) >> 23);
        bArr[8] = b5;
        byte b6 = 0;
        bArr[8] = (byte) (b5 | ((b6 & 0x3F) << 1));
        byte b7 = (byte) ((b6 & 192) >>> 6);
        bArr[9] = b7;
        byte b8 = 0;
        bArr[9] = (byte) (b7 | ((b8 & 31) << 2));
        bArr[10] = (byte) ((b8 & 224) >>> 5);
        if (true) {
            int currentTimeMillis = (int) (System.currentTimeMillis() / 1000);
            long j = currentTimeMillis;
            if (j < currentTimeMillis - ((90 / 3) * 60)) {
                if (j >= currentTimeMillis - (((90 * 2) / 3) * 60)) {
                    bArr[10] = (byte) (bArr[10] | 8);
                } else if (j >= currentTimeMillis - (90 * 60)) {
                    bArr[10] = (byte) (bArr[10] | 16);
                }
            }
        } else {
            byte b9 = bArr[10];
            int i4 = 0;
            bArr[10] = (byte) (b9 | ((i4 & 15) << 3));
            bArr[11] = (byte) (i4 & 127);
            bArr[12] = (byte) ((63488 & i4) >> 11);
        }
        if (true) {
            bArr[12] = (byte) (bArr[12] | 64);
        }
        for (byte c : bArr) {
            System.out.println(c);
        }
    }

    public static byte[] constructAlertModePacket(int alertType, int threatType, int threatqualifier1, int threatqualifier2, int info, int info2, int dataDirectionToCamera, int dataHeading, int distance, long threatAge) {
        int i;
        int threatTypeForDetector;
        int i2;
        byte[] int2byte;
        int[] iArr;
        int i3;

        synchronized (txBuffer) {
            int[] iArr2 = txBuffer;
            iArr2[0] = 36;
            iArr2[1] = 114;
            iArr2[2] = 0;
            iArr2[3] = 27;
            iArr2[4] = 65;
            if (alertType == 68) {
                iArr2[5] = 68;
            } else {
                iArr2[5] = alertType;
            }
            iArr2[6] = info;
            iArr2[7] = info2;
            iArr2[8] = 0;
            iArr2[9] = 0;
            iArr2[10] = 0;
            for (int i4 = 11; i4 < 30; i4++) {
                txBuffer[i4] = 0;
            }
            if (alertType == 67 || alertType == 72 || alertType == 68 || alertType == 68 || alertType == 77 || alertType == 65 || alertType == 84) {
                int[] iArr3 = txBuffer;
                iArr3[8] = 48;
                iArr3[9] = 48;
                iArr3[10] = 53;
            }
            if (alertType != 67 && alertType != 72) {
                int[] iArr4 = txBuffer;
                iArr4[11] = 0;
                iArr4[12] = 0;
                iArr4[13] = 0;
                threatTypeForDetector = getThreatTypeForDetector(threatType, threatqualifier1, threatqualifier2);
                int[] iArr5 = txBuffer;
                iArr5[14] = threatTypeForDetector;
                iArr5[15] = threatLevelForDetector(threatType, threatTypeForDetector, threatAge, threatqualifier2);
                if (alertType == 65 && info == 0 && threatTypeForDetector == 0) {
                    DebugLogging.Companion.logger(TAG, "Zero detector threat type");
                }
                if (threatTypeForDetector == 88) {
                    DebugLogging.Companion.logger(TAG, "X marks a problem");
                }
                if (alertType != 65 || alertType == 68) {
                    int[] iArr6 = txBuffer;
                    iArr6[23] = 0;
                    iArr6[24] = 0;
                    if (threatAge > 0) {
                        int i5 = (int) threatAge;
                        iArr6[23] = (65280 & i5) >> 8;
                        iArr6[24] = i5 & 255;
                    }
                }
                if (alertType != 68 && alertType != 68 && alertType != 72) {
                    if (alertType != 67 && alertType != 77) {
                        if (alertType != 65) {
                            for (int i6 = 16; i6 < 22; i6++) {
                                txBuffer[i6] = 48;
                            }
                            if (PersistentStoreHelper.getSpeedUnits().equals(mainApp.getString(C1752R.string.mph))) {
                                txBuffer[22] = 121;
                            } else {
                                txBuffer[22] = 109;
                            }
                            if (distance < 30) {
                                DebugLogging.Companion.logger(TAG, "distance <25 " + distance);
                                int[] iArr7 = txBuffer;
                                iArr7[16] = 48;
                                iArr7[17] = 48;
                                iArr7[18] = 48;
                                iArr7[19] = 48;
                                iArr7[20] = 48;
                                iArr7[21] = 48;
                            } else {
                                int[] iArr8 = txBuffer;
                                iArr8[16] = (distance / 1000) + 48;
                                int i7 = distance % 1000;
                                iArr8[17] = (i7 / 100) + 48;
                                int i8 = i7 % 100;
                                iArr8[18] = (i8 / 10) + 48;
                                iArr8[19] = (i8 % 10) + 48;
                            }
                        } else if (alertType != 90) {
                            for (int i9 = 14; i9 < 16; i9++) {
                                txBuffer[i9] = 0;
                            }
                            for (int i10 = 16; i10 < 30; i10++) {
                                txBuffer[i10] = 48;
                            }
                        }
                        int[] iArr9 = txBuffer;
                        iArr9[30] = calCheckSum(iArr9);
                        int[] iArr10 = txBuffer;
                        iArr10[31] = 141;
                        int2byte = int2byte(iArr10);
                        iArr = txBuffer;
                        if (iArr[5] == 65 && iArr[6] == 0 && iArr[14] == 0) {
                            iArr[16] = 48;
                            iArr[17] = 48;
                            iArr[18] = 48;
                            iArr[19] = 48;
                            iArr[20] = 48;
                            iArr[21] = 48;
                            DebugLogging.Companion.logger(TAG, "Troublesome end alert packet");
                        }
                        DebugLogging.Companion.logger(TAG, "txBuffer construct alert packet 0:" + txBuffer[0] + ",1-" + txBuffer[1] + ",2-" + txBuffer[2] + ",3-" + txBuffer[3] + ",4-" + txBuffer[4] + ",5-" + txBuffer[5] + ",6-" + txBuffer[6] + ",7-" + txBuffer[7] + ",8-" + txBuffer[8] + ",9-" + txBuffer[9] + ",10-" + txBuffer[10] + ",11-" + txBuffer[11] + ",12-" + txBuffer[12] + ",13-" + txBuffer[13] + ",14-" + txBuffer[14] + ",15-" + txBuffer[15] + ",16-" + txBuffer[16] + ",17-" + txBuffer[17] + ",18-" + txBuffer[18] + ",19-" + txBuffer[19] + ",20-" + txBuffer[20] + ",21-" + txBuffer[21] + ",22-" + txBuffer[22] + ",23-" + txBuffer[23] + ",24-" + txBuffer[24] + ",25-" + txBuffer[25] + ",26-," + txBuffer[26] + ",27-" + txBuffer[27] + ",28-" + txBuffer[28] + ",29-" + txBuffer[29] + ",30-" + txBuffer[30] + ",31-" + txBuffer[31] + "--distance--" + distance);
                        DebugLogging.Companion.logger(TAG, "updateDistance :" + distance);
                        Log.e(TAG, "constructAlertModePacket: " + int2byte);
                    }
                    for (i3 = 16; i3 < 30; i3++) {
                        txBuffer[i3] = 48;
                    }
                    if (!PersistentStoreHelper.getSpeedUnits().equals(mainApp.getString(C1752R.string.mph))) {
                        txBuffer[22] = 121;
                    } else {
                        txBuffer[22] = 109;
                    }
                    if (distance >= 30) {
                        DebugLogging.Companion.logger(TAG, "distance <25 " + distance);
                        int[] iArr11 = txBuffer;
                        iArr11[16] = 48;
                        iArr11[17] = 48;
                        iArr11[18] = 48;
                        iArr11[19] = 48;
                        iArr11[20] = 48;
                        iArr11[21] = 48;
                    } else {
                        int[] iArr12 = txBuffer;
                        iArr12[16] = (distance / 1000) + 48;
                        int i11 = distance % 1000;
                        iArr12[17] = (i11 / 100) + 48;
                        int i12 = i11 % 100;
                        iArr12[18] = (i12 / 10) + 48;
                        iArr12[19] = (i12 % 10) + 48;
                    }
                    int[] iArr92 = txBuffer;
                    iArr92[30] = calCheckSum(iArr92);
                    int[] iArr102 = txBuffer;
                    iArr102[31] = 141;
                    int2byte = int2byte(iArr102);
                    iArr = txBuffer;
                    if (iArr[5] == 65) {
                        iArr[16] = 48;
                        iArr[17] = 48;
                        iArr[18] = 48;
                        iArr[19] = 48;
                        iArr[20] = 48;
                        iArr[21] = 48;
                        DebugLogging.Companion.logger(TAG, "Troublesome end alert packet");
                    }
                    DebugLogging.Companion.logger(TAG, "txBuffer construct alert packet 0:" + txBuffer[0] + ",1-" + txBuffer[1] + ",2-" + txBuffer[2] + ",3-" + txBuffer[3] + ",4-" + txBuffer[4] + ",5-" + txBuffer[5] + ",6-" + txBuffer[6] + ",7-" + txBuffer[7] + ",8-" + txBuffer[8] + ",9-" + txBuffer[9] + ",10-" + txBuffer[10] + ",11-" + txBuffer[11] + ",12-" + txBuffer[12] + ",13-" + txBuffer[13] + ",14-" + txBuffer[14] + ",15-" + txBuffer[15] + ",16-" + txBuffer[16] + ",17-" + txBuffer[17] + ",18-" + txBuffer[18] + ",19-" + txBuffer[19] + ",20-" + txBuffer[20] + ",21-" + txBuffer[21] + ",22-" + txBuffer[22] + ",23-" + txBuffer[23] + ",24-" + txBuffer[24] + ",25-" + txBuffer[25] + ",26-," + txBuffer[26] + ",27-" + txBuffer[27] + ",28-" + txBuffer[28] + ",29-" + txBuffer[29] + ",30-" + txBuffer[30] + ",31-" + txBuffer[31] + "--distance--" + distance);
                    DebugLogging.Companion.logger(TAG, "updateDistance :" + distance);
                    Log.e(TAG, "constructAlertModePacket: " + int2byte);
                }
                for (i2 = 16; i2 < 22; i2++) {
                    txBuffer[i2] = 48;
                }
                if (!PersistentStoreHelper.getSpeedUnits().equals(mainApp.getString(C1752R.string.mph))) {
                    txBuffer[22] = 121;
                } else {
                    txBuffer[22] = 109;
                }
                if (distance >= 30) {
                    DebugLogging.Companion.logger(TAG, "distance <25 " + distance);
                    int[] iArr13 = txBuffer;
                    iArr13[16] = 48;
                    iArr13[17] = 48;
                    iArr13[18] = 48;
                    iArr13[19] = 48;
                    iArr13[20] = 48;
                    iArr13[21] = 48;
                } else {
                    int[] iArr14 = txBuffer;
                    iArr14[16] = (distance / 1000) + 48;
                    int i13 = distance % 1000;
                    iArr14[17] = (i13 / 100) + 48;
                    int i14 = i13 % 100;
                    iArr14[18] = (i14 / 10) + 48;
                    iArr14[19] = (i14 % 10) + 48;
                }
                int[] iArr922 = txBuffer;
                iArr922[30] = calCheckSum(iArr922);
                int[] iArr1022 = txBuffer;
                iArr1022[31] = 141;
                int2byte = int2byte(iArr1022);
                iArr = txBuffer;
                if (iArr[5] == 65) {
                }
                DebugLogging.Companion.logger(TAG, "txBuffer construct alert packet 0:" + txBuffer[0] + ",1-" + txBuffer[1] + ",2-" + txBuffer[2] + ",3-" + txBuffer[3] + ",4-" + txBuffer[4] + ",5-" + txBuffer[5] + ",6-" + txBuffer[6] + ",7-" + txBuffer[7] + ",8-" + txBuffer[8] + ",9-" + txBuffer[9] + ",10-" + txBuffer[10] + ",11-" + txBuffer[11] + ",12-" + txBuffer[12] + ",13-" + txBuffer[13] + ",14-" + txBuffer[14] + ",15-" + txBuffer[15] + ",16-" + txBuffer[16] + ",17-" + txBuffer[17] + ",18-" + txBuffer[18] + ",19-" + txBuffer[19] + ",20-" + txBuffer[20] + ",21-" + txBuffer[21] + ",22-" + txBuffer[22] + ",23-" + txBuffer[23] + ",24-" + txBuffer[24] + ",25-" + txBuffer[25] + ",26-," + txBuffer[26] + ",27-" + txBuffer[27] + ",28-" + txBuffer[28] + ",29-" + txBuffer[29] + ",30-" + txBuffer[30] + ",31-" + txBuffer[31] + "--distance--" + distance);
                DebugLogging.Companion.logger(TAG, "updateDistance :" + distance);
                Log.e(TAG, "constructAlertModePacket: " + int2byte);
            }
            int i15 = dataHeading - dataDirectionToCamera;
            boolean z = i15 < 0;
            DebugLogging.Companion.logger(TAG, "angleToCamera1: " + Integer.toString(dataHeading));
            int min = Math.min(Math.abs(i15), Math.abs(dataDirectionToCamera - dataHeading));
            if (z) {
                min = 360 - min;
            }
            while (min >= 360) {
                min -= 360;
            }
            DebugLogging.Companion.logger(TAG, "angleToCamera2: " + Integer.toString(min));
            int i16 = (min / 100) + 48;
            txBuffer[11] = i16;
            int i17 = min - ((i16 - 48) * 100);
            DebugLogging.Companion.logger(TAG, "angleToCamera3: " + Integer.toString(i17));
            int i18 = (i17 / 10) + 48;
            txBuffer[12] = i18;
            DebugLogging.Companion.logger(TAG, "angleToCamera4: " + Integer.toString(i));
            txBuffer[13] = (i17 - ((i18 - 48) * 10)) + 48;
            threatTypeForDetector = getThreatTypeForDetector(threatType, threatqualifier1, threatqualifier2);
            int[] iArr52 = txBuffer;
            iArr52[14] = threatTypeForDetector;
            iArr52[15] = threatLevelForDetector(threatType, threatTypeForDetector, threatAge, threatqualifier2);
            if (alertType == 65) {
                DebugLogging.Companion.logger(TAG, "Zero detector threat type");
            }
            if (threatTypeForDetector == 88) {
            }
            if (alertType != 65) {
            }
            int[] iArr62 = txBuffer;
            iArr62[23] = 0;
            iArr62[24] = 0;
            if (threatAge > 0) {
            }
            if (alertType != 68) {
                if (alertType != 67) {
                    if (alertType != 65) {
                    }
                    int[] iArr9222 = txBuffer;
                    iArr9222[30] = calCheckSum(iArr9222);
                    int[] iArr10222 = txBuffer;
                    iArr10222[31] = 141;
                    int2byte = int2byte(iArr10222);
                    iArr = txBuffer;
                    if (iArr[5] == 65) {
                    }
                    DebugLogging.Companion.logger(TAG, "txBuffer construct alert packet 0:" + txBuffer[0] + ",1-" + txBuffer[1] + ",2-" + txBuffer[2] + ",3-" + txBuffer[3] + ",4-" + txBuffer[4] + ",5-" + txBuffer[5] + ",6-" + txBuffer[6] + ",7-" + txBuffer[7] + ",8-" + txBuffer[8] + ",9-" + txBuffer[9] + ",10-" + txBuffer[10] + ",11-" + txBuffer[11] + ",12-" + txBuffer[12] + ",13-" + txBuffer[13] + ",14-" + txBuffer[14] + ",15-" + txBuffer[15] + ",16-" + txBuffer[16] + ",17-" + txBuffer[17] + ",18-" + txBuffer[18] + ",19-" + txBuffer[19] + ",20-" + txBuffer[20] + ",21-" + txBuffer[21] + ",22-" + txBuffer[22] + ",23-" + txBuffer[23] + ",24-" + txBuffer[24] + ",25-" + txBuffer[25] + ",26-," + txBuffer[26] + ",27-" + txBuffer[27] + ",28-" + txBuffer[28] + ",29-" + txBuffer[29] + ",30-" + txBuffer[30] + ",31-" + txBuffer[31] + "--distance--" + distance);
                    DebugLogging.Companion.logger(TAG, "updateDistance :" + distance);
                    Log.e(TAG, "constructAlertModePacket: " + int2byte);
                }
                while (i3 < 30) {
                }
                if (!PersistentStoreHelper.getSpeedUnits().equals(mainApp.getString(C1752R.string.mph))) {
                }
                if (distance >= 30) {
                }
                int[] iArr92222 = txBuffer;
                iArr92222[30] = calCheckSum(iArr92222);
                int[] iArr102222 = txBuffer;
                iArr102222[31] = 141;
                int2byte = int2byte(iArr102222);
                iArr = txBuffer;
                if (iArr[5] == 65) {
                }
                DebugLogging.Companion.logger(TAG, "txBuffer construct alert packet 0:" + txBuffer[0] + ",1-" + txBuffer[1] + ",2-" + txBuffer[2] + ",3-" + txBuffer[3] + ",4-" + txBuffer[4] + ",5-" + txBuffer[5] + ",6-" + txBuffer[6] + ",7-" + txBuffer[7] + ",8-" + txBuffer[8] + ",9-" + txBuffer[9] + ",10-" + txBuffer[10] + ",11-" + txBuffer[11] + ",12-" + txBuffer[12] + ",13-" + txBuffer[13] + ",14-" + txBuffer[14] + ",15-" + txBuffer[15] + ",16-" + txBuffer[16] + ",17-" + txBuffer[17] + ",18-" + txBuffer[18] + ",19-" + txBuffer[19] + ",20-" + txBuffer[20] + ",21-" + txBuffer[21] + ",22-" + txBuffer[22] + ",23-" + txBuffer[23] + ",24-" + txBuffer[24] + ",25-" + txBuffer[25] + ",26-," + txBuffer[26] + ",27-" + txBuffer[27] + ",28-" + txBuffer[28] + ",29-" + txBuffer[29] + ",30-" + txBuffer[30] + ",31-" + txBuffer[31] + "--distance--" + distance);
                DebugLogging.Companion.logger(TAG, "updateDistance :" + distance);
                Log.e(TAG, "constructAlertModePacket: " + int2byte);
            }
            while (i2 < 22) {
            }
            if (!PersistentStoreHelper.getSpeedUnits().equals(mainApp.getString(C1752R.string.mph))) {
            }
            if (distance >= 30) {
            }
            int[] iArr922222 = txBuffer;
            iArr922222[30] = calCheckSum(iArr922222);
            int[] iArr1022222 = txBuffer;
            iArr1022222[31] = 141;
            int2byte = int2byte(iArr1022222);
            iArr = txBuffer;
            if (iArr[5] == 65) {
            }
            DebugLogging.Companion.logger(TAG, "txBuffer construct alert packet 0:" + txBuffer[0] + ",1-" + txBuffer[1] + ",2-" + txBuffer[2] + ",3-" + txBuffer[3] + ",4-" + txBuffer[4] + ",5-" + txBuffer[5] + ",6-" + txBuffer[6] + ",7-" + txBuffer[7] + ",8-" + txBuffer[8] + ",9-" + txBuffer[9] + ",10-" + txBuffer[10] + ",11-" + txBuffer[11] + ",12-" + txBuffer[12] + ",13-" + txBuffer[13] + ",14-" + txBuffer[14] + ",15-" + txBuffer[15] + ",16-" + txBuffer[16] + ",17-" + txBuffer[17] + ",18-" + txBuffer[18] + ",19-" + txBuffer[19] + ",20-" + txBuffer[20] + ",21-" + txBuffer[21] + ",22-" + txBuffer[22] + ",23-" + txBuffer[23] + ",24-" + txBuffer[24] + ",25-" + txBuffer[25] + ",26-," + txBuffer[26] + ",27-" + txBuffer[27] + ",28-" + txBuffer[28] + ",29-" + txBuffer[29] + ",30-" + txBuffer[30] + ",31-" + txBuffer[31] + "--distance--" + distance);
            DebugLogging.Companion.logger(TAG, "updateDistance :" + distance);
            Log.e(TAG, "constructAlertModePacket: " + int2byte);
        }
        return int2byte;
    }

    // Crypto keys for BT
    private static final int[] BT_SMARTCORD_KEYS = { 0x713A2B5A, 0x49752D5C, 0x7B496D7B, 0x3D5F667C };
    private static final int[] BT_SMARTPHONE_KEYS = { 0x622F7B45, 0x312F3C69, 0x36535D67, 0x50677C5F };

    // Crypto keys for BLE
    private static final int[] BLE_SMARTCORD_KEYS = { 0xB67423AB, 0x7B7F599E, 0x831E63EB, 0x535C1285 };
    private static final int[] BLE_SMARTPHONE_KEYS = { 0xEFC62E92, 0xFB676A4B, 0xE29946BD, 0xF9AF55CB };

    private boolean isBLE;

    private int[] verifyNonce;

    private int[] getSmartcordKeys() {
        return this.isBLE ? BLE_SMARTCORD_KEYS : BT_SMARTCORD_KEYS;
    }

    private int[] getSmartphoneKeys() {
        return this.isBLE ? BLE_SMARTPHONE_KEYS : BT_SMARTPHONE_KEYS;
    }

    private int[] xtea_encrypt(int num_rounds, int v[], int key[]) {
        int v0 = v[0];
        int v1 = v[1];

        int sum = 0;
        int delta = -245324840;

        for (int i = 0; i < num_rounds; i++) {
            v0 += ((v1 << 4 ^ v1 >>> 5) + v1 ^ key[(sum & 0x3)] + sum);
            sum += delta;
            v1 += ((v0 << 4 ^ v0 >>> 5) + v0 ^ key[(sum >>> 11 & 0x3)] + sum);
        }

        return new int[] { v0, v1 };
    }

    private int[] esc_pack_10bytes_to_2ints(byte[] esc_req) {
        int b0 = esc_req[3];
        int b1 = esc_req[4];
        int b2 = esc_req[5];
        int b3 = esc_req[6];
        int b4 = esc_req[7];
        int b5 = esc_req[8];
        int b6 = esc_req[9];
        int b7 = esc_req[10];
        int b8 = esc_req[11];
        int b9 = esc_req[12];

        int v0 = b0 & 0x7F | (b1 & 0x7F) << 7 | (b2 & 0x7F) << 14 | (b3 & 0x7F) << 21 | (b4 & 0xF) << 28;
        int v1 = (b5 & 0x70) >> 4 | (b5 & 0x7F) << 3 | (b6 & 0x7F) << 10 | (b7 & 0x7F) << 17 | (b8 & 0x7F) << 24
                | (b9 & 0x1) << 31;

        return new int[] { v0, v1 };
    }

    private byte[] esc_unpack_2ints_to_10_bytes(int vv[]) {
        int v0 = vv[0];
        int v1 = vv[1];

        byte b0 = (byte) (v0 & 0x7F);
        byte b1 = (byte) (v0 >>> 7 & 0x7F);
        byte b2 = (byte) (v0 >>> 14 & 0x7F);
        byte b3 = (byte) (v0 >>> 21 & 0x7F);
        byte b4 = (byte) (v0 >>> 28 & 0x7F | v1 << 4 & 0x70);
        byte b5 = (byte) (v1 >>> 3 & 0x7F);
        byte b6 = (byte) (v1 >>> 10 & 0x7F);
        byte b7 = (byte) (v1 >>> 17 & 0x7F);
        byte b8 = (byte) (v1 >>> 24 & 0x7F);
        byte b9 = (byte) (v1 >>> 31 & 0x1);

        return new byte[] { b0, b1, b2, b3, b4, b5, b6, b7, b8, b9 };
    }

    // Calculates response packet for detector's unlock/auth challenge request
    public byte[] getUnlockResponse(byte[] esc_req) {

        int vv[] = xtea_encrypt(35, esc_pack_10bytes_to_2ints(esc_req), getSmartcordKeys());

        byte[] rr = new byte[3 + 10];
        rr[0] = -11;
        rr[1] = 11;
        rr[2] = -92;
        System.arraycopy(esc_unpack_2ints_to_10_bytes(vv), 0, rr, 3, 10);
        return rr;
    }
}