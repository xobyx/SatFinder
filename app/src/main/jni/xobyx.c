#include <string.h>
#include <stdint.h>
#include <jni.h>
#include <android/log.h>

#define LOG_TAG "xobyx"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)

#define LOBYTE(x) ((jbyte)((x) & 0xFF))
#define HIBYTE(x) ((jbyte)(((x) >> 8) & 0xFF))

static const int freq[30] = {
    9750, 10600, 9750, 10700, 9750, 10750, 5150, 5750,
    5750, 5150,  5150, 5150,  5750, 5750,  5950, 5950,
    9750, 9750,  10000, 10000, 10600, 10600, 10700, 10700,
    10750, 10750, 11250, 11250, 11300, 11300
};

static jbyte mEntries[1024];
static int mEntriesOffset = 0;

jbyte checkSum(const jbyte* parray, int size) {
    jbyte c = 0;
    for (int i = 0; i < size; i++) {
        c ^= parray[i];
    }
    return c;
}

JNIEXPORT jstring JNICALL
Java_com_xobyx_satfinder_HelloJni_stringFromJNI(JNIEnv* env, jobject thiz) {
    return (*env)->NewStringUTF(env, "Hello from JNI !");
}

JNIEXPORT jboolean JNICALL
Java_com_xobyx_satfinder_MainActivity_write(JNIEnv* env, jobject thiz, jbyteArray m_array) {
    jclass activityClass = (*env)->GetObjectClass(env, thiz);
    jfieldID handlerField = (*env)->GetFieldID(env, activityClass, "mHandler", "Landroid/os/Handler;");
    if (!handlerField) return JNI_FALSE;

    jobject handler = (*env)->GetObjectField(env, thiz, handlerField);
    if (!handler) return JNI_FALSE;

    jclass handlerClass = (*env)->GetObjectClass(env, handler);

    // Clone the byte array for the Handler message
    jsize len = (*env)->GetArrayLength(env, m_array);
    jbyteArray copyArray = (*env)->NewByteArray(env, len);
    jbyte* elems = (*env)->GetByteArrayElements(env, m_array, NULL);
    (*env)->SetByteArrayRegion(env, copyArray, 0, len, elems);
    (*env)->ReleaseByteArrayElements(env, m_array, elems, JNI_ABORT);

    jmethodID obtainMsg = (*env)->GetMethodID(env, handlerClass, "obtainMessage", "(ILjava/lang/Object;)Landroid/os/Message;");
    jobject msg = (*env)->CallObjectMethod(env, handler, obtainMsg, 15, copyArray);

    jmethodID sendMsg = (*env)->GetMethodID(env, handlerClass, "sendMessage", "(Landroid/os/Message;)Z");
    jboolean result = (*env)->CallBooleanMethod(env, handler, sendMsg, msg);

    (*env)->DeleteLocalRef(env, msg);
    (*env)->DeleteLocalRef(env, copyArray);
    (*env)->DeleteLocalRef(env, handlerClass);
    (*env)->DeleteLocalRef(env, handler);
    (*env)->DeleteLocalRef(env, activityClass);

    return result;
}

// Helper to fetch the selected Transponder object from MainActivity
static jobject getSelectedTransponder(JNIEnv* env, jobject thiz, jclass mainClass, jint* outSatPos) {
    jfieldID satPosField = (*env)->GetFieldID(env, mainClass, "mSatPos", "I");
    jfieldID tpPosField  = (*env)->GetFieldID(env, mainClass, "mTpPos", "I");
    jint mSatPos = (*env)->GetIntField(env, thiz, satPosField);
    jint mTpPos  = (*env)->GetIntField(env, thiz, tpPosField);
    if (outSatPos) *outSatPos = mSatPos;

    jfieldID satListField = (*env)->GetFieldID(env, mainClass, "mSatellites", "Ljava/util/List;");
    jobject satList = (*env)->GetObjectField(env, thiz, satListField);
    jclass listClass = (*env)->GetObjectClass(env, satList);
    jmethodID listGet = (*env)->GetMethodID(env, listClass, "get", "(I)Ljava/lang/Object;");

    jobject sat = (*env)->CallObjectMethod(env, satList, listGet, mSatPos);
    jclass satClass = (*env)->GetObjectClass(env, sat);
    jfieldID tpListField = (*env)->GetFieldID(env, satClass, "mTransponders", "Ljava/util/List;");
    jobject tpList = (*env)->GetObjectField(env, sat, tpListField);

    jobject tp = (*env)->CallObjectMethod(env, tpList, listGet, mTpPos);

    (*env)->DeleteLocalRef(env, tpList);
    (*env)->DeleteLocalRef(env, satClass);
    (*env)->DeleteLocalRef(env, sat);
    (*env)->DeleteLocalRef(env, listClass);
    (*env)->DeleteLocalRef(env, satList);

    return tp;
}

JNIEXPORT void JNICALL
Java_com_xobyx_satfinder_MainActivity_SendLNBConfig(JNIEnv* env, jobject thiz) {
    jclass mainClass = (*env)->GetObjectClass(env, thiz);
    jint mLNB_Freq = (*env)->GetIntField(env, thiz, (*env)->GetFieldID(env, mainClass, "mLNB_Freq", "I"));
    jint mLNB_22k  = (*env)->GetIntField(env, thiz, (*env)->GetFieldID(env, mainClass, "mLNB_22k", "I"));
    jint mLNB_Disq = (*env)->GetIntField(env, thiz, (*env)->GetFieldID(env, mainClass, "mLNB_Disq", "I"));

    jbyte buf[9];
    buf[0] = (jbyte)0xE4; // 484 LE = 0xE4, 0x01
    buf[1] = 0x01;

    short freq1 = (short)freq[2 * mLNB_Freq];
    short freq2 = (short)freq[2 * mLNB_Freq + 1];
    buf[2] = HIBYTE(freq1);
    buf[3] = LOBYTE(freq1);
    buf[4] = HIBYTE(freq2);
    buf[5] = LOBYTE(freq2);
    buf[6] = LOBYTE(mLNB_22k);
    buf[7] = LOBYTE(mLNB_Disq);
    buf[8] = checkSum(buf, 8);

    jbyteArray narray = (*env)->NewByteArray(env, 9);
    (*env)->SetByteArrayRegion(env, narray, 0, 9, buf);
    Java_com_xobyx_satfinder_MainActivity_write(env, thiz, narray);

    (*env)->DeleteLocalRef(env, narray);
    (*env)->DeleteLocalRef(env, mainClass);
}

JNIEXPORT void JNICALL
Java_com_xobyx_satfinder_MainActivity_SendSelectedSatTp(JNIEnv* env, jobject thiz) {
    jclass mainClass = (*env)->GetObjectClass(env, thiz);
    jobject tp = getSelectedTransponder(env, thiz, mainClass, NULL);
    jclass tpClass = (*env)->GetObjectClass(env, tp);

    jint mFrequency  = (*env)->GetIntField(env, tp, (*env)->GetFieldID(env, tpClass, "mFrequency", "I"));
    jint mSymbolRate = (*env)->GetIntField(env, tp, (*env)->GetFieldID(env, tpClass, "mSymbolRate", "I"));
    jint mPolization = (*env)->GetIntField(env, tp, (*env)->GetFieldID(env, tpClass, "mPolization", "I"));

    jbyte buf[8];
    buf[0] = (jbyte)0xE4; // 740 LE = 0xE4, 0x02
    buf[1] = 0x02;

    short tp_freq = (short)(mFrequency / 1000);
    short sr      = (short)(mSymbolRate / 1000);
    buf[2] = HIBYTE(tp_freq);
    buf[3] = LOBYTE(tp_freq);
    buf[4] = HIBYTE(sr);
    buf[5] = LOBYTE(sr);
    buf[6] = LOBYTE(mPolization);
    buf[7] = checkSum(buf, 7);

    jbyteArray array = (*env)->NewByteArray(env, 8);
    (*env)->SetByteArrayRegion(env, array, 0, 8, buf);
    Java_com_xobyx_satfinder_MainActivity_write(env, thiz, array);

    (*env)->DeleteLocalRef(env, array);
    (*env)->DeleteLocalRef(env, tpClass);
    (*env)->DeleteLocalRef(env, tp);
    (*env)->DeleteLocalRef(env, mainClass);
}

JNIEXPORT void JNICALL
Java_com_xobyx_satfinder_MainActivity_SendPluse(JNIEnv* env, jobject thiz) {
    jbyte buf[4];
    buf[0] = (jbyte)0xE4; // 1252 LE = 0xE4, 0x04
    buf[1] = 0x04;
    buf[2] = 0x00;
    buf[3] = checkSum(buf, 3);

    jbyteArray array = (*env)->NewByteArray(env, 4);
    (*env)->SetByteArrayRegion(env, array, 0, 4, buf);
    Java_com_xobyx_satfinder_MainActivity_write(env, thiz, array);
    (*env)->DeleteLocalRef(env, array);
}

JNIEXPORT void JNICALL
Java_com_xobyx_satfinder_MainActivity_SendSelectedSatTpLNBConfig(JNIEnv* env, jobject thiz) {
    jclass mainClass = (*env)->GetObjectClass(env, thiz);
    jint mSatPos = 0;
    jobject tp = getSelectedTransponder(env, thiz, mainClass, &mSatPos);
    jclass tpClass = (*env)->GetObjectClass(env, tp);

    jint mFrequency  = (*env)->GetIntField(env, tp, (*env)->GetFieldID(env, tpClass, "mFrequency", "I"));
    jint mSymbolRate = (*env)->GetIntField(env, tp, (*env)->GetFieldID(env, tpClass, "mSymbolRate", "I"));
    jint mPolization = (*env)->GetIntField(env, tp, (*env)->GetFieldID(env, tpClass, "mPolization", "I"));

    jint mLNB_Freq = (*env)->GetIntField(env, thiz, (*env)->GetFieldID(env, mainClass, "mLNB_Freq", "I"));
    jint mLNB_22k  = (*env)->GetIntField(env, thiz, (*env)->GetFieldID(env, mainClass, "mLNB_22k", "I"));
    jint mLNB_Disq = (*env)->GetIntField(env, thiz, (*env)->GetFieldID(env, mainClass, "mLNB_Disq", "I"));

    jbyte buf[14];
    buf[0] = (jbyte)0xE4; // 1764 LE = 0xE4, 0x06
    buf[1] = 0x06;

    short freq1   = (short)freq[2 * mLNB_Freq];
    short freq2   = (short)freq[2 * mLNB_Freq + 1];
    short tp_freq = (short)(mFrequency / 1000);
    short sr      = (short)(mSymbolRate / 1000);

    buf[2]  = HIBYTE(freq1);
    buf[3]  = LOBYTE(freq1);
    buf[4]  = HIBYTE(freq2);
    buf[5]  = LOBYTE(freq2);
    buf[6]  = LOBYTE(mLNB_22k);
    buf[7]  = LOBYTE(mLNB_Disq);
    buf[8]  = HIBYTE(tp_freq);
    buf[9]  = LOBYTE(tp_freq);
    buf[10] = HIBYTE(sr);
    buf[11] = LOBYTE(sr);
    // Note: Use LOBYTE(mPolization) if byte 12 is polarization, or LOBYTE(mSatPos) if matching original decompilation
    buf[12] = LOBYTE(mPolization);
    buf[13] = checkSum(buf, 13);

    jbyteArray arraym = (*env)->NewByteArray(env, 14);
    (*env)->SetByteArrayRegion(env, arraym, 0, 14, buf);
    Java_com_xobyx_satfinder_MainActivity_write(env, thiz, arraym);

    (*env)->DeleteLocalRef(env, arraym);
    (*env)->DeleteLocalRef(env, tpClass);
    (*env)->DeleteLocalRef(env, tp);
    (*env)->DeleteLocalRef(env, mainClass);
}

JNIEXPORT void JNICALL
Java_com_xobyx_satfinder_FieldStrengthFragment_SendCurrentTp(JNIEnv* env, jobject thiz, jint index) {
    if (index < 0 || index > 3) return;

    jclass fragClass = (*env)->GetObjectClass(env, thiz);
    const char* posNames[4] = {"mPos1", "mPos2", "mPos3", "mPos4"};
    jfieldID posField = (*env)->GetFieldID(env, fragClass, posNames[index], "I");
    jint tpIndex = (*env)->GetIntField(env, thiz, posField);

    jfieldID tpsField = (*env)->GetFieldID(env, fragClass, "mTps", "Ljava/util/List;");
    jobject tpsList = (*env)->GetObjectField(env, thiz, tpsField);
    jclass listClass = (*env)->GetObjectClass(env, tpsList);
    jmethodID getMethod = (*env)->GetMethodID(env, listClass, "get", "(I)Ljava/lang/Object;");

    jobject selectedTp = (*env)->CallObjectMethod(env, tpsList, getMethod, tpIndex);
    jclass tpClass = (*env)->GetObjectClass(env, selectedTp);

    jint mFrequency  = (*env)->GetIntField(env, selectedTp, (*env)->GetFieldID(env, tpClass, "mFrequency", "I"));
    jint mSymbolRate = (*env)->GetIntField(env, selectedTp, (*env)->GetFieldID(env, tpClass, "mSymbolRate", "I"));
    jint mPolization = (*env)->GetIntField(env, selectedTp, (*env)->GetFieldID(env, tpClass, "mPolization", "I"));

    jbyte buf[9];
    buf[0] = (jbyte)0xE4; // 1508 LE = 0xE4, 0x05
    buf[1] = 0x05;
    buf[2] = LOBYTE(index);

    short freq_tp = (short)(mFrequency / 1000);
    short sr      = (short)(mSymbolRate / 1000);
    buf[3] = HIBYTE(freq_tp);
    buf[4] = LOBYTE(freq_tp);
    buf[5] = HIBYTE(sr);
    buf[6] = LOBYTE(sr);
    buf[7] = LOBYTE(mPolization);
    buf[8] = checkSum(buf, 8);

    jbyteArray array_m = (*env)->NewByteArray(env, 9);
    (*env)->SetByteArrayRegion(env, array_m, 0, 9, buf);

    // Obtain parent Activity if FieldStrengthFragment does not have mHandler directly
    jmethodID getActivity = (*env)->GetMethodID(env, fragClass, "getActivity", "()Landroid/app/Activity;");
    jobject activity = getActivity ? (*env)->CallObjectMethod(env, thiz, getActivity) : thiz;
    Java_com_xobyx_satfinder_MainActivity_write(env, activity, array_m);

    (*env)->DeleteLocalRef(env, array_m);
    (*env)->DeleteLocalRef(env, tpClass);
    (*env)->DeleteLocalRef(env, selectedTp);
    (*env)->DeleteLocalRef(env, listClass);
    (*env)->DeleteLocalRef(env, tpsList);
    (*env)->DeleteLocalRef(env, fragClass);
}

JNIEXPORT void JNICALL
Java_com_xobyx_satfinder_MainActivity_ProgressRecivedMessage(JNIEnv* env, jobject thiz, jbyteArray array) {
    jbyte* parray = (*env)->GetByteArrayElements(env, array, NULL);
    jsize array_len = (*env)->GetArrayLength(env, array);
    if (array_len < 1) {
        (*env)->ReleaseByteArrayElements(env, array, parray, JNI_ABORT);
        return;
    }

    jclass mainClass = (*env)->GetObjectClass(env, thiz);
    jfieldID handlerField = (*env)->GetFieldID(env, mainClass, "mHandler", "Landroid/os/Handler;");
    jobject mHandler = (*env)->GetObjectField(env, thiz, handlerField);
    jclass handlerClass = (*env)->GetObjectClass(env, mHandler);

    jmethodID obtainEmpty = (*env)->GetMethodID(env, handlerClass, "obtainMessage", "(I)Landroid/os/Message;");
    jmethodID obtainObj   = (*env)->GetMethodID(env, handlerClass, "obtainMessage", "(ILjava/lang/Object;)Landroid/os/Message;");
    jmethodID obtainArgs  = (*env)->GetMethodID(env, handlerClass, "obtainMessage", "(III)Landroid/os/Message;");
    jmethodID sendMsg     = (*env)->GetMethodID(env, handlerClass, "sendMessage", "(Landroid/os/Message;)Z");

    jobject mmsg = NULL;

    switch (parray[0] + 29) {
        case 0:
            if (array_len >= 3 && parray[1] == 1 && parray[2] == 1) {
                mmsg = (*env)->CallObjectMethod(env, mHandler, obtainEmpty, 7);
            }
            break;

        case 2:
            if (array_len >= 4) {
                jfieldID sigStrField  = (*env)->GetFieldID(env, mainClass, "mSigStrength", "I");
                jfieldID sigQualField = (*env)->GetFieldID(env, mainClass, "mSigQuality", "I");
                (*env)->SetIntField(env, thiz, sigStrField, parray[1]);
                (*env)->SetIntField(env, thiz, sigQualField, parray[2]);
                mmsg = (*env)->CallObjectMethod(env, mHandler, obtainEmpty, 3);
            }
            break;

        case 3:
            if (array_len >= 3) {
                jsize chunkLen = array_len - 3;
                if (mEntriesOffset + chunkLen < (jsize)sizeof(mEntries)) {
                    memcpy(&mEntries[mEntriesOffset], parray + 2, chunkLen);
                    mEntriesOffset += chunkLen;
                }
                if ((parray[1] & 0x0F) == 15) {
                    mEntries[mEntriesOffset] = parray[1];
                    jbyteArray pa = (*env)->NewByteArray(env, mEntriesOffset + 1);
                    (*env)->SetByteArrayRegion(env, pa, 0, mEntriesOffset + 1, mEntries);
                    mEntriesOffset = 0;
                    mmsg = (*env)->CallObjectMethod(env, mHandler, obtainObj, 4, pa);
                    (*env)->DeleteLocalRef(env, pa);
                }
            }
            break;

        case 4:
        case 20:
            if (array_len >= 4) {
                // Fixed: pass parray[1] and parray[3] as arg1, arg2 to obtainMessage
                mmsg = (*env)->CallObjectMethod(env, mHandler, obtainArgs, 6, (jint)parray[1], (jint)parray[3]);
            }
            break;

        case 5:
            if (array_len >= 2) {
                // Fixed: pass 0 as arg2 to match (III) signature
                mmsg = (*env)->CallObjectMethod(env, mHandler, obtainArgs, 5, (jint)parray[1], 0);
            }
            break;

        case 6:
            if (array_len >= 3 && (unsigned char)parray[2] + 3 <= array_len) {
                jsize payloadLen = (unsigned char)parray[2];
                jbyteArray pa = (*env)->NewByteArray(env, payloadLen);
                (*env)->SetByteArrayRegion(env, pa, 0, payloadLen, parray + 3);
                int msg_id = 13;
                if (parray[1] == 2) msg_id = 9;
                else if (parray[1] == 4) msg_id = 8;
                mmsg = (*env)->CallObjectMethod(env, mHandler, obtainObj, msg_id, pa);
                (*env)->DeleteLocalRef(env, pa);
            }
            break;

        case 8:
            if (array_len >= 2) {
                mmsg = (*env)->CallObjectMethod(env, mHandler, obtainArgs, 17, (jint)parray[1], 0);
            }
            break;

        case 18:
            if (array_len >= 20) {
                jbyteArray pa = (*env)->NewByteArray(env, 18);
                (*env)->SetByteArrayRegion(env, pa, 0, 18, parray + 1);
                mmsg = (*env)->CallObjectMethod(env, mHandler, obtainObj, 24, pa);
                (*env)->DeleteLocalRef(env, pa);
            }
            break;

        default:
            break;
    }

    if (mmsg != NULL) {
        (*env)->CallBooleanMethod(env, mHandler, sendMsg, mmsg);
        (*env)->DeleteLocalRef(env, mmsg);
    }

    (*env)->DeleteLocalRef(env, handlerClass);
    (*env)->DeleteLocalRef(env, mHandler);
    (*env)->DeleteLocalRef(env, mainClass);
    (*env)->ReleaseByteArrayElements(env, array, parray, JNI_ABORT);
}
