/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
#include <string.h>
#include <jni.h>
#include <android/log.h>
 /* This is a trivial JNI example where we use a native method
  * to return a new VM String. See the corresponding Java source
  * file located at:
  *
  *   apps/samples/hello-jni/project/src/com/example/hellojni/HelloJni.java
  */

#define _BYTE jbyte
#define BYTEn(x,n) (*((_BYTE*)&(x)+n))
#define BYTE1(x) BYTEn(x,1)
#define LOBYTE(x) (*((_BYTE*)&(x)))
#define HIBYTE(x) (*((_BYTE*)&(x)+1))

jbyte checkSum(jbyte* parray, int size)
{
	jbyte c = 0;
	int v3 = 0;



	while (v3 < size)
	{
		jbyte v5 = *(parray + v3++);
		c ^= v5;
	}
	return c;
}
jstring
Java_com_xobyx_satfinder_HelloJni_stringFromJNI(JNIEnv* env,
	jobject thiz)
{

	return (*env)->NewStringUTF(env, "Hello from JNI !");
}

jboolean Java_com_xobyx_satfinder_MainActivity_write(JNIEnv* env,
	jobject thiz,
	jarray m_array)
{

	jclass main_activity_class = (*env)->GetObjectClass(env, thiz);
	jfieldID ma_handler_f_id = (*env)->GetFieldID(env, main_activity_class, "mHandler", "Landroid/os/Handler;");
	__android_log_write(ANDROID_LOG_INFO,"xobyx","MainActivity_write1");
	jobject ma_handler_F = (*env)->GetObjectField(env, thiz, ma_handler_f_id);
	jclass ma_handler_class = (*env)->GetObjectClass(env, ma_handler_F);
	__android_log_write(ANDROID_LOG_INFO,"xobyx","MainActivity_write2");
	jsize array_len = (*env)->GetArrayLength(env, m_array);
	jbyteArray arraym = (*env)->NewByteArray(env, array_len);
	__android_log_write(ANDROID_LOG_INFO,"xobyx","MainActivity_write3");
	jbyte *parray = (*env)->GetByteArrayElements(env, m_array, 0);
	(*env)->SetByteArrayRegion(env, arraym, 0, array_len, parray);
	jmethodID obtain_m_id = (*env)->GetMethodID(
		env,
		ma_handler_class,
		"obtainMessage",
		"(ILjava/lang/Object;)Landroid/os/Message;");
		__android_log_write(ANDROID_LOG_INFO,"xobyx","MainActivity_write4");
		jint msg_id = 15;
	jobject msg = (*env)->CallObjectMethod(env, ma_handler_F, obtain_m_id, msg_id,arraym);
		__android_log_write(ANDROID_LOG_INFO,"xobyx","MainActivity_write5");
	jmethodID send_m_id = (*env)->GetMethodID(env, ma_handler_class, "sendMessage", "(Landroid/os/Message;)Z");
		__android_log_write(ANDROID_LOG_INFO,"xobyx","MainActivity_write6");
	return (*env)->CallBooleanMethod(env, ma_handler_F, send_m_id, msg);

}
int freq[30] = { 9750, 10600, 9750, 10700, 9750, 10750, 5150,
5750,5750, 5150, 5150, 5150, 5750, 5750, 5950, 5950, 9750,
9750, 10000, 10000, 10600, 10600, 10700, 10700, 10750,
10750, 11250, 11250, 11300, 11300
};
void
Java_com_xobyx_satfinder_MainActivity_c(JNIEnv* env,
	jobject thiz)
{
	jclass mainc = (*env)->GetObjectClass(env, thiz);
	jfieldID mLNB_Freq_id = (*env)->GetFieldID(env, mainc, "mLNB_Freq", "I");
	jfieldID mLNB_22k_id = (*env)->GetFieldID(env, mainc, "mLNB_22k", "I");
	jfieldID mLNB_Disq_id = (*env)->GetFieldID(env, mainc, "mLNB_Disq", "I");
	jint mLNB_22k = (*env)->GetIntField(env, thiz, mLNB_22k_id);
	jint mLNB_Freq = (*env)->GetIntField(env, thiz, mLNB_Freq_id);
	jint mLNB_Disq = (*env)->GetIntField(env, thiz, mLNB_Disq_id);
	jbyteArray narray = (*env)->NewByteArray(env, 9);
	jboolean copy = 1;
	jbyte* array = (*env)->GetByteArrayElements(env, array, &copy);

	*((short*)array) = 484;

	short freq1 = freq[2 * mLNB_Freq];
	short freq2 = freq[2 * mLNB_Freq + 1];
	array[2] = BYTE1(freq1);
	array[3] = freq1;

	array[4] = BYTE1(freq2);
	array[5] = freq2;
	array[6] = LOBYTE(mLNB_22k);
	array[7] = mLNB_Disq;
	array[8] = checkSum(array, 8);
	(*env)->SetByteArrayRegion(env, narray, 0, 9, array);
	Java_com_xobyx_satfinder_MainActivity_write(env, thiz, narray);
	(*env)->ReleaseByteArrayElements(env, narray, array, 0);




}

void
Java_com_xobyx_satfinder_MainActivity_b(JNIEnv* env, jobject thiz)
{
	jclass MainActivityClass = (*env)->GetObjectClass(env, thiz);

	jfieldID mSatPos_id = (*env)->GetFieldID(env, MainActivityClass, "mSatPos", "I");
	jfieldID mTpPos_id = (*env)->GetFieldID(env, MainActivityClass, "mTpPos", "I");
	jint mSatPos = (*env)->GetIntField(env, thiz, mSatPos_id);
	jint mTpPos = (*env)->GetIntField(env, thiz, mTpPos_id);

	jfieldID mSatellites_list_id = (*env)->GetFieldID(env, MainActivityClass, "mSatellites", "Ljava/util/List;");
	jobject mSatellites_list = (*env)->GetObjectField(env, thiz, mSatellites_list_id);

	jclass mSatellites_list_class = (*env)->GetObjectClass(env, mSatellites_list);
	jmethodID get_method = (*env)->GetMethodID(env, mSatellites_list_class, "get", "(I)Ljava/lang/Object;");
	jobject sat = (*env)->CallObjectMethod(env, mSatellites_list, get_method, mSatPos);

	jclass sat_class = (*env)->GetObjectClass(env, sat);
	jfieldID mTransponders_list_id = (*env)->GetFieldID(env, sat_class, "mTransponders", "Ljava/util/List;");
	jobject mTransponders_list = (*env)->GetObjectField(env, sat, mTransponders_list_id);

	jclass mTransponders_list_class = (*env)->GetObjectClass(env, mTransponders_list);
	jmethodID get_method2 = (*env)->GetMethodID(env, mTransponders_list_class, "get", "(I)Ljava/lang/Object;");
	jobject tranz = (*env)->CallObjectMethod(env, mTransponders_list, get_method2, mTpPos);
	jclass tranz_class = (*env)->GetObjectClass(env, tranz);
	jfieldID mFrequency_id = (*env)->GetFieldID(env, tranz_class, "mFrequency", "I");
	jfieldID mSymbolRate_id = (*env)->GetFieldID(env, tranz_class, "mSymbolRate", "I");
	jfieldID mPolization_id = (*env)->GetFieldID(env, tranz_class, "mPolization", "I");
	jint mFrequency = (*env)->GetIntField(env, tranz, mFrequency_id);
	jint mSymbolRate = (*env)->GetIntField(env, tranz, mSymbolRate_id);
	jint mPolization = (*env)->GetIntField(env, tranz, mPolization_id);
	jboolean bo = 1;
	jbyteArray array = (*env)->NewByteArray(env, 8);

	jbyte* parray = (*env)->GetByteArrayElements(env, array, &bo);

	*((short*)parray) = 740;
	short tp_freq = (mFrequency / 1000);
	parray[2] = BYTE1(tp_freq);
	parray[3] = tp_freq;
	short sr = mSymbolRate / 1000;
	parray[4] = BYTE1(sr);
	parray[5] = sr;
	parray[6] = LOBYTE(mPolization);
	parray[7] = checkSum(parray, 7);
	(*env)->SetByteArrayRegion(env, array, 0, 8, parray);
	Java_com_xobyx_satfinder_MainActivity_write(env, thiz, array);
	(*env)->ReleaseByteArrayElements(env, array, parray, 0);
	(*env)->DeleteLocalRef(env, array);


}
jbyte mEntries[1024];

char _F[7] = { '\0' };
void Java_com_xobyx_satfinder_MainActivity_f(JNIEnv* env, jobject thiz, jbyteArray array)
{





	jbyte * parray = (*env)->GetByteArrayElements(env, array, 0);

	jsize array_len = (*env)->GetArrayLength(env, array);
	jclass MainActivityClass = (*env)->GetObjectClass(env, thiz);


	jfieldID handler_id = (*env)->GetFieldID(env, MainActivityClass, "mHandler", "Landroid/os/Handler;");
	jobject mHandler = (*env)->GetObjectField(env, thiz, handler_id);
	jclass HandlerClass = (*env)->GetObjectClass(env, mHandler);
	jfieldID mChnDialog_id1 = (*env)->GetFieldID(env, MainActivityClass, "mChnDialog", "Landroid/app/AlertDialog;");
	jobject Dialog = (*env)->GetObjectField(env, thiz, mChnDialog_id1);

	switch (*parray + 29)
	{
	case 0:



			if (parray[1]==1 && parray[2] == 1)
			{

				jmethodID obtainMessage_m_id = (*env)->GetMethodID(env, HandlerClass, "obtainMessage", "(I)Landroid/os/Message;");

				jint msg_id = 7;

				jobject mmsg = (*env)->CallObjectMethod(env, mHandler, obtainMessage_m_id, msg_id);
				jmethodID sendMessage_m_id = (*env)->GetMethodID(env, HandlerClass, "sendMessage", "(Landroid/os/Message;)Z");

				(*env)->CallBooleanMethod(env, mHandler, sendMessage_m_id, mmsg);

				return (*env)->ReleaseByteArrayElements(env, array, parray, 0);
			}
		return (*env)->ReleaseByteArrayElements(env, array, parray, 0);
	case 1:
	case 7:
	case 9:
	case 10:
	case 11:
	case 12:
	case 13:
	case 14:
	case 15:
	case 16:
	case 17:
	case 19:
		return (*env)->ReleaseByteArrayElements(env, array, parray, 0);
	case 2:
		if (array_len >= 4)
		{
			jfieldID mSigStrength_id = (*env)->GetFieldID(env, MainActivityClass, "mSigStrength", "I");
			jfieldID mSigQuality_id = (*env)->GetFieldID(env, MainActivityClass, "mSigQuality", "I");
			(*env)->SetIntField(env, thiz, mSigStrength_id, parray[1]);
			(*env)->SetIntField(env, thiz, mSigQuality_id, parray[2]);

			jmethodID obtainMessage_m_id = (*env)->GetMethodID(env, HandlerClass, "obtainMessage", "(I)Landroid/os/Message;");

			jint msg_id = 3;

			jobject mmsg = (*env)->CallObjectMethod(env, mHandler, obtainMessage_m_id, msg_id);
			jmethodID sendMessage_m_id = (*env)->GetMethodID(env, HandlerClass, "sendMessage", "(Landroid/os/Message;)Z");
			(*env)->CallBooleanMethod(env, mHandler, sendMessage_m_id, mmsg);

			return (*env)->ReleaseByteArrayElements(env, array, parray, 0);
		}
		return (*env)->ReleaseByteArrayElements(env, array, parray, 0);
	case 3:
		if (array_len >= 3)
		{
			jbyte* p2 = parray + 2;
			jsize len_m_3 = array_len - 3;
			int f3 = *(int*)&_F[3];
			jbyte* entries_ptr = &mEntries[*(int*)&_F[3]];
			while (len_m_3)
			{
				jbyte v24 = *p2++;
				--len_m_3;
				*entries_ptr++ = v24;
			}
			jsize array_len_p_f3 = array_len + f3;
			*(int*)&_F[3] = array_len_p_f3 - 3;
			if ((parray[1] & 0xF) == 15)
			{
				jbyteArray pa = (*env)->NewByteArray(env, array_len_p_f3 - 2);
				int f3 = *(int*)&_F[3];
				mEntries[*(int*)&_F[3]] = parray[1];
				(*env)->SetByteArrayRegion(env, pa, 0, f3 + 1, mEntries);
				*(int*)&_F[3] = 0;


				jmethodID obtain_m_id = (*env)->GetMethodID(env, HandlerClass, "obtainMessage", "(ILjava/lang/Object;)Landroid/os/Message;");

				int msg_id = 4;
				jobject mmsg = (*env)->CallObjectMethod(env, mHandler, obtain_m_id, msg_id,pa);
				jmethodID sendMessage_m_id = (*env)->GetMethodID(env, HandlerClass, "sendMessage", "(Landroid/os/Message;)Z");

				(*env)->CallBooleanMethod(env, mHandler, sendMessage_m_id, mmsg);

				return (*env)->ReleaseByteArrayElements(env, array, parray, 0);
			}
		}
		return (*env)->ReleaseByteArrayElements(env, array, parray, 0);
	case 4:
	case 20:
		if (array_len >= 4)
		{

			jbyte p1 = parray[1];
			jbyte p3 = parray[3];
			jmethodID obtain_m_id = (*env)->GetMethodID(env, HandlerClass, "obtainMessage", "(III)Landroid/os/Message;");


			int msg_id = 6;
			jobject mmsg = (*env)->CallObjectMethod(env, mHandler, obtain_m_id, msg_id);
			jmethodID sendMessage_m_id = (*env)->GetMethodID(env, HandlerClass, "sendMessage", "(Landroid/os/Message;)Z");

			(*env)->CallBooleanMethod(env, mHandler, sendMessage_m_id, mmsg,p1,p3);

			return (*env)->ReleaseByteArrayElements(env, array, parray, 0);
		}
		return (*env)->ReleaseByteArrayElements(env, array, parray, 0);
	case 5:
		if (array_len >= 2)
		{

			jbyte p1 = parray[1];
			jmethodID obtain_m_id = (*env)->GetMethodID(env, HandlerClass, "obtainMessage", "(III)Landroid/os/Message;");

			int msg_id = 5;
			jobject mmsg = (*env)->CallObjectMethod(env, mHandler, obtain_m_id, msg_id,p1);
			jmethodID sendMessage_m_id = (*env)->GetMethodID(env, HandlerClass, "sendMessage", "(Landroid/os/Message;)Z");

			(*env)->CallBooleanMethod(env, mHandler, sendMessage_m_id, mmsg);

			return (*env)->ReleaseByteArrayElements(env, array, parray, 0);
		}
		return (*env)->ReleaseByteArrayElements(env, array, parray, 0);
	case 6:
	{

		jbyteArray pa = (*env)->NewByteArray(env, parray[2]);
		(*env)->SetByteArrayRegion(env, pa, 0, parray[2], parray + 3);

		jmethodID obtain_m_id = (*env)->GetMethodID(env, HandlerClass, "obtainMessage", "(ILjava/lang/Object;)Landroid/os/Message;");
		int msg_id = 13;

		if (parray[1] == 2)
			msg_id = 9;
		if (parray[1] == 4)
			msg_id = 8;



		jobject mmsg = (*env)->CallObjectMethod(env, mHandler, obtain_m_id, msg_id,pa);
		jmethodID sendMessage_m_id = (*env)->GetMethodID(env, HandlerClass, "sendMessage", "(Landroid/os/Message;)Z");

		(*env)->CallBooleanMethod(env, mHandler, sendMessage_m_id, mmsg);

		return (*env)->ReleaseByteArrayElements(env, array, parray, 0);
	}
	case 8:
		if (array_len < 2)
			return (*env)->ReleaseByteArrayElements(env, array, parray, 0);


		jmethodID obtain_m_id = (*env)->GetMethodID(env, HandlerClass, "obtainMessage", "(III)Landroid/os/Message;");

		int msg_id = 17;
		jobject mmsg = (*env)->CallObjectMethod(env, mHandler, obtain_m_id, msg_id);
		jmethodID sendMessage_m_id = (*env)->GetMethodID(env, HandlerClass, "sendMessage", "(Landroid/os/Message;)Z");

		(*env)->CallBooleanMethod(env, mHandler, sendMessage_m_id, mmsg);

		return (*env)->ReleaseByteArrayElements(env, array, parray, 0);
	case 18:
		if (array_len >= 20)
		{
			jbyteArray pa = (*env)->NewByteArray(env, 18);
			(*env)->SetByteArrayRegion(env, pa, 0, 18, parray + 1);


			jmethodID obtain_m_id = (*env)->GetMethodID(env, HandlerClass, "obtainMessage", "(ILjava/lang/Object;)Landroid/os/Message;");

			int msg_id = 24;

			jmethodID mmsg = (*env)->CallObjectMethod(env, mHandler, obtain_m_id, msg_id,pa);
			jmethodID sendMessage_m_id = (*env)->GetMethodID(env, HandlerClass, "sendMessage", "(Landroid/os/Message;)Z");

			(*env)->CallBooleanMethod(env, mHandler, sendMessage_m_id, mmsg);
		}
		return (*env)->ReleaseByteArrayElements(env, array, parray, 0);
	}
}

void Java_com_xobyx_satfinder_MainActivity_d(JNIEnv *env, jobject thiz)//d
{

	jboolean v9 = 1;
	jbyteArray array = (*env)->NewByteArray(env, 4);

	jbyte* parray = (*env)->GetByteArrayElements(env, array, &v9);

	*((short*)parray) = 1252;
	parray[2] = 0;
	parray[3] = checkSum(parray, 3);
	(*env)->SetByteArrayRegion(env, array, 0, 4, parray);
	Java_com_xobyx_satfinder_MainActivity_write(env, thiz, array);
	(*env)->ReleaseByteArrayElements(env, array, parray, 0);
	(*env)->DeleteLocalRef(env, array);

}

void  Java_com_xobyx_satfinder_MainActivity_e(JNIEnv *env, jobject thiz)
{

	jclass MainActivityClass = (*env)->GetObjectClass(env, thiz);

	jfieldID mSatPos_id = (*env)->GetFieldID(env, MainActivityClass, "mSatPos", "I");

	jfieldID mTpPos_id = (*env)->GetFieldID(env, MainActivityClass, "mTpPos", "I");
	jint mSatPos = (*env)->GetIntField(env, thiz, mSatPos_id);
	jint mTpPos = (*env)->GetIntField(env, thiz, mTpPos_id);
	jfieldID mSatellites_list_id = (*env)->GetFieldID(env, MainActivityClass, "mSatellites", "Ljava/util/List;");
	jobject mSatellites_list = (*env)->GetObjectField(env, thiz, mSatellites_list_id);
__android_log_write(ANDROID_LOG_INFO,"xobyx","xxxxxxxxxxxxxxxxxxxxxx");
	jclass List_class = (*env)->GetObjectClass(env, mSatellites_list);
	jmethodID list_get_method_id = (*env)->GetMethodID(env, List_class, "get", "(I)Ljava/lang/Object;");
	jobject Sat = (*env)->CallObjectMethod(env, mSatellites_list, list_get_method_id, mSatPos);

	jclass Satellite_Class = (*env)->GetObjectClass(env, Sat);
	jfieldID mTransponders_id = (*env)->GetFieldID(env, Satellite_Class, "mTransponders", "Ljava/util/List;");
	jobject mTransponders_list = (*env)->GetObjectField(env, Sat, mTransponders_id);

	jclass list_class2 = (*env)->GetObjectClass(env, mTransponders_list);
	jmethodID list_get_method_id2 = (*env)->GetMethodID(env, list_class2, "get", "(I)Ljava/lang/Object;");
__android_log_write(ANDROID_LOG_INFO,"xobyx","xxxxxxxxxxxxxxxxxxxxxx2");
	jobject selected_tp = (*env)->CallObjectMethod(env, mTransponders_list, list_get_method_id2, mTpPos);
	jclass Transponder_class = (*env)->GetObjectClass(env, selected_tp);
	jfieldID mFrequency_id = (*env)->GetFieldID(env, Transponder_class, "mFrequency", "I");
	jfieldID mSymbolRate_id = (*env)->GetFieldID(env, Transponder_class, "mSymbolRate", "I");
	jfieldID mPolization_id = (*env)->GetFieldID(env, Transponder_class, "mPolization", "I");
	jint mFrequency = (*env)->GetIntField(env, selected_tp, mFrequency_id);
	jint mSymbolRate = (*env)->GetIntField(env, selected_tp, mSymbolRate_id);
	jint mPolization = (*env)->GetIntField(env, selected_tp, mPolization_id);
	__android_log_write(ANDROID_LOG_INFO,"xobyx","xxxxxxxxxxxxxxxxxxxxxx3");
	jfieldID mLNB_Freq_id = (*env)->GetFieldID(env, MainActivityClass, "mLNB_Freq", "I");
	jfieldID mLNB_22k_id = (*env)->GetFieldID(env, MainActivityClass, "mLNB_22k", "I");
	jfieldID mLNB_Disq_id = (*env)->GetFieldID(env, MainActivityClass, "mLNB_Disq", "I");
	jint mLNB_Freq = (*env)->GetIntField(env, thiz, mLNB_Freq_id);
	jint mLNB_22k = (*env)->GetIntField(env, thiz, mLNB_22k_id);
	jint mLNB_Disq = (*env)->GetIntField(env, thiz, mLNB_Disq_id);
	jbyteArray arraym = (*env)->NewByteArray(env, 14);

	jbyte* parray = (*env)->GetByteArrayElements(env, arraym, 0);
__android_log_write(ANDROID_LOG_INFO,"xobyx","xxxxxxxxxxxxxxxxxxxxxx4");
	*((short*)parray) = 1764;
	short freq1 = freq[2 * mLNB_Freq];
	short freq2 = freq[2 * mLNB_Freq + 1];
	parray[2] = BYTE1(freq1);
	parray[3] = freq1;
	parray[4] = BYTE1(freq2);
	parray[5] = freq2;
	parray[6] = mLNB_22k;
	parray[7] = mLNB_Disq;
	short tp_freq = mFrequency / 1000;
	parray[8] = BYTE1(tp_freq);
	parray[9] = tp_freq;
	short sr = mSymbolRate / 1000;
	parray[10] = BYTE1(sr);
	parray[11] = sr;
	parray[12] = mSatPos;
	parray[13] = checkSum(parray, 13);
	(*env)->SetByteArrayRegion(env, arraym, 0, 14, parray);
	Java_com_xobyx_satfinder_MainActivity_write(env, thiz, arraym);
	__android_log_write(ANDROID_LOG_INFO,"xobyx","xxxxxxxxxxxxxxxxxxxxxx5");
	(*env)->ReleaseByteArrayElements(env, arraym, parray, 0);
	return;// (*env)->DeleteLocalRef(env, arraym);
}
void  Java_com_xobyx_satfinder_FieldStrengthFragment_d(JNIEnv *env, jobject thiz, jint index)
{

	jclass FieldFragmentClass = (*env)->GetObjectClass(env, thiz);
	jfieldID mPos1_id = (*env)->GetFieldID(env, FieldFragmentClass, "mPos1", "I");
	jfieldID mPos2_id = (*env)->GetFieldID(env, FieldFragmentClass, "mPos2", "I");
	jfieldID mPos3_id = (*env)->GetFieldID(env, FieldFragmentClass, "mPos3", "I");
	jfieldID mPos4_id = (*env)->GetFieldID(env, FieldFragmentClass, "mPos4", "I");

	jint mPos[] = { (*env)->GetIntField(env, thiz, mPos1_id),(*env)->GetIntField(env, thiz, mPos2_id),(*env)->GetIntField(env, thiz, mPos3_id),(*env)->GetIntField(env, thiz, mPos4_id) };


	jfieldID mTps_list_id = (*env)->GetFieldID(env, FieldFragmentClass, "mTps", "Ljava/util/List;");
	jobject mTps_list = (*env)->GetObjectField(env, thiz, mTps_list_id);

	jclass mTps_list_class = (*env)->GetObjectClass(env, mTps_list);
	jmethodID List_get_method_id = (*env)->GetMethodID(env, mTps_list_class, "get", "(I)Ljava/lang/Object;");
	jobject selected_tp = (*env)->CallObjectMethod(env, mTps_list, List_get_method_id, mPos[index]);
	jclass Transponder_class = (*env)->GetObjectClass(env, selected_tp);
	jfieldID mFrequency_id = (*env)->GetFieldID(env, Transponder_class, "mFrequency", "I");
	jfieldID mSymbolRate_id = (*env)->GetFieldID(env, Transponder_class, "mSymbolRate", "I");
	jfieldID mPolization_id = (*env)->GetFieldID(env, Transponder_class, "mPolization", "I");
	jint mFrequency = (*env)->GetIntField(env, selected_tp, mFrequency_id);
	jint mSymbolRate = (*env)->GetIntField(env, selected_tp, mSymbolRate_id);
	jint mPolization = (*env)->GetIntField(env, selected_tp, mPolization_id);
	jbyteArray array_m = (*env)->NewByteArray(env, 9);

	jbyte* parray = (*env)->GetByteArrayElements(env, array_m, 0);

	*((short*)parray) = 1508;
	parray[2] = index;
	short freq_tp = mFrequency / 1000;
	parray[3] = BYTE1(freq_tp);
	parray[4] = mFrequency / 1000;
	short sr = mSymbolRate / 1000;
	parray[5] = BYTE1(sr);
	parray[6] = mSymbolRate / 1000;
	parray[7] = LOBYTE(mPolization);
	parray[8] = checkSum(parray, 8);
	(*env)->SetByteArrayRegion(env, array_m, 0, 9, parray);
	Java_com_xobyx_satfinder_MainActivity_write(env, thiz, array_m);
	return (*env)->ReleaseByteArrayElements(env, array_m, parray, 0);
}