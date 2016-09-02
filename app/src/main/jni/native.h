//
// Created by jiaxing on 9/1/16.
//
#include <jni.h>
#include <android/bitmap.h>
#include <android/log.h>
#include <memory.h>

#ifndef ANDROIDLEARNING_NATIVE_H
#define ANDROIDLEARNING_NATIVE_H

#endif //ANDROIDLEARNING_NATIVE_H


void processBitmap(JNIEnv *env, jclass instance, jobject jBitmap,
                   void (*filter)(void **pixels, int height, int width));

void filterGrayscale(void **pixels, int height, int width);

void filterBorW(void **pixels, int height, int width);

void filterNagetive(void **pixels, int height, int width);

void filterAnaglyph(void **pixels, int height, int width);